package com.github.nailcui.sim.example.redis;

import com.github.nailcui.sim.ChannelContext;
import com.github.nailcui.sim.codec.LineCodec;
import com.github.nailcui.sim.codec.resp2.Resp2Codec;
import com.github.nailcui.sim.codec.resp2.command.Command;
import com.github.nailcui.sim.handler.AbstractHandler;
import com.github.nailcui.sim.handler.Codec;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dingyu
 * @date 2021-12-19 00:40
 */
@Slf4j
public class RedisClientHandler extends AbstractHandler {

  private final Codec codec = new Resp2Codec();

  @Override
  public void onConnect(ChannelContext context) {
    try {
      log.info("connected, send hello");
      context.write("*2\r\n$3\r\nget\r\n$1\r\na\r\n");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void onMessage(ChannelContext context, Object msg) {
    log.info("received message: {}", msg);
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
    return codec.encode(msg);
  }

  @Override
  public void decode(ByteBuffer readBuffer, Queue<Object> readQueue) {
//    LineCodec.INSTANCE.decode(readBuffer, readQueue);
    codec.decode(readBuffer, readQueue);

  }
}
