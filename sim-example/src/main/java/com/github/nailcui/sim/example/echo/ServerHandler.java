package com.github.nailcui.sim.example.echo;

import com.github.nailcui.sim.ChannelContext;
import com.github.nailcui.sim.codec.LineCodec;
import com.github.nailcui.sim.handler.AbstractHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Queue;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dingyu
 * @date 2021-12-19 00:40
 */
@Slf4j
public class ServerHandler extends AbstractHandler {

  @Override
  public void onConnect(ChannelContext context) {
    try {
      log.info("new connected from: {}", context.socketChannel.getRemoteAddress());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onMessage(ChannelContext context, Object msg) {
    log.info("on message: {}", msg);
    context.write(msg);
  }

  @Override
  public void onInvalid(ChannelContext context) {
    log.info("invalid channel: {}", context.socketChannel);
  }

  @Override
  public void onException(ChannelContext context, Exception e) {
    log.info("on exception");
  }

  @Override
  public ByteBuffer encode(Object msg) {
    return LineCodec.INSTANCE.encode(msg);
  }

  @Override
  public void decode(ByteBuffer readBuffer, Queue<Object> readQueue) {
    LineCodec.INSTANCE.decode(readBuffer, readQueue);
  }
}
