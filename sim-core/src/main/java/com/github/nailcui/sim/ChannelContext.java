package com.github.nailcui.sim;

import com.github.nailcui.sim.handler.Handler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dingyu
 * @date 2021-12-20 16:31
 */
@Slf4j
public class ChannelContext {
  public SocketChannel socketChannel;
  private ByteBuffer readBuffer = ByteBuffer.allocate(32);
  private ByteBuffer writeBuffer = ByteBuffer.allocate(32);
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

  public ChannelContext(SocketChannel socketChannel, Handler handler) {
    this.socketChannel = socketChannel;
    this.handler = handler;
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
        return;
      } else if (readed > 0) {
        this.handler.decode(this, this.readBuffer);
      }
    }
  }

  public void write(SelectionKey key) throws IOException {
    log.info("send: {}", writeBuffer.array());
    this.socketChannel.write(writeBuffer);
    if (!writeBuffer.hasRemaining()) {
      key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
    }

  }
}
