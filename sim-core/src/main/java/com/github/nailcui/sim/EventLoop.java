package com.github.nailcui.sim;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class EventLoop extends Thread {

  private Selector selector = Selector.open();
  private Queue<SocketChannel> channelQueue = new ConcurrentLinkedQueue<>();

  EventLoop() throws IOException {
  }

  public void register(SocketChannel client) throws IOException {
    client.configureBlocking(false);
    this.channelQueue.offer(client);
    this.selector.wakeup();
  }

  @Override
  public void run() {
    while (true) {
      log.info("event loop select...");
      try {
        sleep(1000);
        int select = this.selector.select();
        log.info("event loop select: {}", select);
        if (select == 0) {
          // 处理新连接
          SocketChannel client = this.channelQueue.poll();
          while (client != null) {
            client.register(this.selector, SelectionKey.OP_READ);
            client = this.channelQueue.poll();
          }
          continue;
        }
      } catch (Exception e) {
        log.info("event loop select exception: ", e);
        continue;
      }
      Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
      while (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        keyIterator.remove();
        if (key.isConnectable()) {
          log.info("key connectable");
        } else if (key.isReadable()) {
          log.info("key readable");
          try {
            handleRead(key);
          } catch (Exception e) {
            log.error("read exception: ", e);
          }
        } else if (key.isWritable()) {
          log.info("key writeable");
          try {
            handleWrite(key);
          } catch (Exception e) {
            log.error("read exception: ", e);
          }
        } else if (key.isValid()) {
          log.info("key valid");
        } else {
          log.info("unKnow key type");
        }
      }
    }
  }

  private void handleRead(SelectionKey key) throws Exception {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    SocketChannel client = (SocketChannel) key.channel();
    int readed = client.read(buffer);
    StringBuilder sb = new StringBuilder();
    if (readed == -1) {
      log.info("connection closed by client");
      client.close();
      key.cancel();
      return;
    } else {
      buffer.flip();
      sb.append(new String(buffer.array(), 0, readed, StandardCharsets.UTF_8));
      buffer.clear();
    }
    readed = client.read(buffer);
    while (readed != -1 && readed != 0) {
      buffer.flip();
      sb.append(new String(buffer.array(), 0, readed, StandardCharsets.UTF_8));
      buffer.clear();
      readed = client.read(buffer);
    }
    log.info("read:  {}", sb.toString());
    key.interestOps(SelectionKey.OP_WRITE);
    key.attach(ByteBuffer.wrap(sb.toString().getBytes(StandardCharsets.UTF_8)));
  }

  private void handleWrite(SelectionKey key) throws Exception {
    SocketChannel channel = (SocketChannel) key.channel();
    ByteBuffer writeBuffer = (ByteBuffer) key.attachment();
    log.info("send: {}", writeBuffer.array());
    channel.write(writeBuffer);
    if (!writeBuffer.hasRemaining()) {
      key.interestOps(SelectionKey.OP_READ);
    }
  }

}
