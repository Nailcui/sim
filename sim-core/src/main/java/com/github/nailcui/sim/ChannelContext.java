package com.github.nailcui.sim;

import com.github.nailcui.sim.handler.Handler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dingyu
 * @date 2021-12-19 00:40
 */
@Slf4j
public class ChannelContext {
  public SocketChannel socketChannel;
  private final EventLoop eventLoop;
  private ByteBuffer readBuffer = ByteBuffer.allocate(32);
  private final Queue<Object> readQueue = new LinkedBlockingQueue<>();
  private final Queue<Object> writeQueue = new LinkedBlockingQueue<>();
  private final Queue<ByteBuffer> writeBufferQueue = new LinkedBlockingQueue<>();
  public Handler handler;

  private void readBufferExtendIfNeeded() {
    if (!readBuffer.hasRemaining()) {
      // 没有剩余容量, 扩容一倍
      log.info("extend readBuffer");
      ByteBuffer old = this.readBuffer;
      readBuffer = ByteBuffer.allocate(readBuffer.capacity() * 2);
      readBuffer.put(old);
    }
  }

  public ChannelContext(SocketChannel socketChannel, Handler handler, EventLoop eventLoop) {
    this.socketChannel = socketChannel;
    this.handler = handler;
    this.eventLoop = eventLoop;
  }

  public static ChannelContext contextOf(SelectionKey key) {
    Object attachment = key.attachment();
    return (ChannelContext) attachment;
  }

  public void read(SelectionKey key) throws IOException {
    int readed = 1;
    while (readed > 0) {
      readBufferExtendIfNeeded();
      readed = this.socketChannel.read(this.readBuffer);
      if (readed == -1) {
        log.info("connection closed by client");
        this.socketChannel.close();
        key.cancel();
        this.handler.onInvalid(this);
        return;
      } else if (readed > 0) {
        this.readBuffer.flip();
        this.handler.decode(this.readBuffer, this.readQueue);
        this.readBuffer.compact();
      }
    }
    Object msg = this.readQueue.poll();
    while (msg != null) {
      this.handler.onMessage(this, msg);
      msg = this.readQueue.poll();
    }
  }

  public void write(Object msg) throws IOException {
    if (this.writeQueue.offer(msg)) {
      this.eventLoop.notifyWrite(this);
    }
  }

  void write(SelectionKey key) throws IOException {
    Object msg = this.writeQueue.poll();
    while (msg != null) {
      ByteBuffer buf = this.handler.encode(msg);
      if (buf != null) {
        // 转为读模式
        buf.flip();
        this.writeBufferQueue.offer(buf);
      }
      msg = this.writeQueue.poll();
    }

    ByteBuffer buffer = this.writeBufferQueue.poll();
    while (buffer != null) {
      int l = buffer.remaining();
      int write = this.socketChannel.write(buffer);
      if (write < l) {
        // todo 写入数据太多这里会有问题
        log.warn("写入数据不够");
      }
      buffer = this.writeBufferQueue.poll();
    }
    key.interestOps(SelectionKey.OP_READ);
  }
}
