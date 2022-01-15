package com.github.nailcui.sim.handler;

import com.github.nailcui.sim.ChannelContext;
import java.nio.ByteBuffer;
import java.util.Queue;

/**
 * @author dingyu
 * @date 2021-12-19 00:40
 */
public interface Handler {

  void onConnect(ChannelContext context);
  void onValid(ChannelContext context);
  void onException(ChannelContext context, Exception e);

  void decode(ByteBuffer readBuffer, Queue<Object> readQueue);

  void onMessage(ChannelContext context, Object msg);

  void encode(ByteBuffer writeBuffer, Object msg);
}
