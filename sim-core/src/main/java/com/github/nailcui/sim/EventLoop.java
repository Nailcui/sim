package com.github.nailcui.sim;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class EventLoop extends Thread {

  private Selector selector = Selector.open();
  private Queue<ChannelContext> channelQueue = new ConcurrentLinkedQueue<>();

  EventLoop() throws IOException {
  }

  public void register(ChannelContext context) throws IOException {
    context.socketChannel.configureBlocking(false);
    this.channelQueue.offer(context);
    this.selector.wakeup();
  }

  @Override
  public void run() {
    while (true) {
      log.info("event loop select...");
      try {
        // 这个sleep是debug用的
        sleep(1000);
        int select = this.selector.select();
        log.info("event loop select: {}", select);

        // 处理新连接，大部分时候应该都是空的，这一步考虑怎么优化一下
        ChannelContext context = this.channelQueue.poll();
        while (context != null) {
          // 将context绑定上去，使得之后时间过来后可以拿到
          context.socketChannel.register(this.selector, SelectionKey.OP_READ, context);
          context.handler.onConnect(context);
          context = this.channelQueue.poll();
        }
        if (select == 0) {
          continue;
        }
      } catch (Exception e) {
        // todo 异常处理、分类
        log.info("event loop select exception: ", e);
        continue;
      }
      // 处理各种事件
      Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
      while (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        keyIterator.remove();
        log.info("key: {}", key);
        if (key.isReadable()) {
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
          handleValid(key);
        } else {
          log.info("unKnow key type");
        }
      }
    }
  }

  private void handleValid(SelectionKey key) {
    ChannelContext context = ChannelContext.contextOf(key);
    context.handler.onValid(context);
  }

  private void handleRead(SelectionKey key) throws Exception {
    ChannelContext.contextOf(key).read(key);
  }

  private void handleWrite(SelectionKey key) throws Exception {
    ChannelContext.contextOf(key).write(key);
  }

}
