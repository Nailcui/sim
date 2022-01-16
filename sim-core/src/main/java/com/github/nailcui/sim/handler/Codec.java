package com.github.nailcui.sim.handler;

import java.nio.ByteBuffer;
import java.util.Queue;

/**
 * @author dingyu
 * @date 2022-01-16 11:55
 */
public interface Codec {

  /**
   * 将 msg 根据自己的编码方式，写入 writeBuffer 中
   * @param msg
   */
  ByteBuffer encode(Object msg);

  /**
   * 从 readBuffer 中解析数据，将一个或多个成品装入 readQueue
   * @param readBuffer readBuffer
   * @param readQueue readQueue
   */
  void decode(ByteBuffer readBuffer, Queue<Object> readQueue);

}
