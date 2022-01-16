package com.github.nailcui.sim.handler;

import com.github.nailcui.sim.ChannelContext;
import java.nio.ByteBuffer;
import java.util.Queue;

/**
 * @author dingyu
 * @date 2021-12-19 00:40
 */
public interface Handler extends Codec {

  void onConnect(ChannelContext context);
  void onInvalid(ChannelContext context);
  void onException(ChannelContext context, Exception e);

  /**
   * 接收并处理消息
   * @param context context
   * @param msg decode 生成的消息
   */
  void onMessage(ChannelContext context, Object msg);
}
