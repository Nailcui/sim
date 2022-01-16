package com.github.nailcui.sim.codec;

import com.github.nailcui.sim.handler.Codec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Queue;

/**
 * @author dingyu
 * @date 2022-01-16 11:56
 */
public class LineCodec implements Codec {

  public static final LineCodec INSTANCE = new LineCodec();

  public static final byte[] SEPARATOR = "\n".getBytes(StandardCharsets.UTF_8);

  @Override
  public ByteBuffer encode(Object msg) {
    if (msg instanceof String) {
      byte[] bytes = ((String) msg).getBytes(StandardCharsets.UTF_8);
      ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length + SEPARATOR.length);
      writeBuffer.put(bytes);
      writeBuffer.put(SEPARATOR);
      return writeBuffer;
    }
    return null;
  }

  @Override
  public void decode(ByteBuffer readBuffer, Queue<Object> readQueue) {
    while (true) {
      boolean quit = true;
      for (int i = readBuffer.position(); i < readBuffer.limit(); i++) {
        if (readBuffer.array()[i] == '\n') {
          String s = new String(readBuffer.array(), readBuffer.position(), i, StandardCharsets.UTF_8);
          readQueue.offer(s);
          readBuffer.position(i + 1);
          quit = false;
          break;
        }
      }
      if (quit) {
        return;
      }
    }
  }
}
