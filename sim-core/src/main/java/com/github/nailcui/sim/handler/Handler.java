package com.github.nailcui.sim.handler;

import com.github.nailcui.sim.ChannelContext;
import java.nio.ByteBuffer;

/**
 * @author dingyu
 * @date 2021-12-19 01:07
 */
public interface Handler {

  void onConnect(ChannelContext context);
  void onValid(ChannelContext context);
  void onException(ChannelContext context, Exception e);

  void decode(ChannelContext context, ByteBuffer readBuffer);
}
