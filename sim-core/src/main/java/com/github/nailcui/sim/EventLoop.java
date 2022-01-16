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

  private final Selector selector = Selector.open();

  /**
   * 用于存储: 建立连接后,还未注册到 this.selector 的连接
   */
  private final Queue<ChannelContext> channelQueue = new ConcurrentLinkedQueue<>();
  private final Queue<ChannelContext> needWriteQueue = new ConcurrentLinkedQueue<>();

  EventLoop() throws IOException {
  }

  public void register(ChannelContext context) throws IOException {
    context.socketChannel.configureBlocking(false);
    this.channelQueue.offer(context);
    this.selector.wakeup();
  }

  public void notifyWrite(ChannelContext context) {
    this.needWriteQueue.offer(context);
    this.selector.wakeup();
  }

  @Override
  public void run() {
    while (true) {
      log.debug("event loop select...");
      try {
        int select = this.selector.select();
        log.debug("event loop selected size: {}", select);

        // 处理新连接
        ChannelContext context = this.channelQueue.poll();
        while (context != null) {
          // 将context绑定上去，使得之后时间过来后可以拿到
          context.socketChannel.register(this.selector, SelectionKey.OP_READ, context);
          context.handler.onConnect(context);
          context = this.channelQueue.poll();
        }
        ChannelContext needWriteContext = this.needWriteQueue.poll();
        while (needWriteContext != null) {
          // 将context绑定上去，使得之后时间过来后可以拿到
          // todo 这里可以直接处理
          needWriteContext.socketChannel.register(this.selector, SelectionKey.OP_WRITE, needWriteContext);
          needWriteContext = this.channelQueue.poll();
        }
        if (select == 0) {
          continue;
        }
      } catch (Exception e) {
        // todo 异常处理、分类
        log.info("event loop select exception: ", e);
        continue;
      }

      // 处理连接事件
      Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
      while (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        keyIterator.remove();
        log.debug("event loop selected key: {}", key);

        if (key.isReadable()) {
          log.debug("event loop selected readable key");
          try {
            handleRead(key);
          } catch (Exception e) {
            log.error("read exception: ", e);
          }

        } else if (key.isWritable()) {
          log.debug("event loop selected writeable key");
          try {
            handleWrite(key);
          } catch (Exception e) {
            log.error("write exception: ", e);
          }

        } else if (key.isValid()) {
          try {
            log.debug("event loop selected invalid key");
            handleInvalid(key);
          } catch (Exception e) {
            log.error("invalid exception: ", e);
          }

        } else {
          log.warn("unKnow key type");
        }
      }
    }
  }

  private void handleInvalid(SelectionKey key) {
    ChannelContext context = ChannelContext.contextOf(key);
    context.handler.onInvalid(context);
  }

  private void handleRead(SelectionKey key) throws Exception {
    ChannelContext.contextOf(key).read(key);
  }

  private void handleWrite(SelectionKey key) throws Exception {
    ChannelContext.contextOf(key).write(key);
  }

}
