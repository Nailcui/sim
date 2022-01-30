package com.github.nailcui.sim.buffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author dingyu
 * @date 2022-01-30 10:13
 */
public class ByteBufferUtils {

  public static byte getAndSkip(ByteBuffer readBuffer) {
    byte b = readBuffer.get(readBuffer.position());
    readBuffer.position(readBuffer.position() + 1);
    return b;
  }

  public static Integer findCRLF(ByteBuffer readBuffer) {
    for (int i = readBuffer.position(); i < readBuffer.limit() - 1; i++) {
      if (readBuffer.get(i) == '\r' && readBuffer.get(i + 1) == '\n') {
        return i;
      }
    }
    return null;
  }

  public static String readString(ByteBuffer readBuffer, int len) {
    return new String(readBuffer.array(), readBuffer.position(), len, StandardCharsets.UTF_8);
  }
}
