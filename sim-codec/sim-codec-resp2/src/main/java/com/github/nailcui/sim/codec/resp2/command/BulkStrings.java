package com.github.nailcui.sim.codec.resp2.command;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author dingyu
 * @date 2022-01-29 23:10
 */
public class BulkStrings implements Command {

  private String string = null;

  public BulkStrings() {
  }

  public BulkStrings(String string) {
    this.string = string;
  }

  @Override
  public ByteBuffer encode() {
    byte[] bytes;
    if (string == null) {
      bytes = ("$-1" + SEPARATOR_CR + SEPARATOR_LF).getBytes(StandardCharsets.UTF_8);
    } else {
      bytes = ("$" + string.length() + SEPARATOR_CR + SEPARATOR_LF + string + SEPARATOR_CR + SEPARATOR_LF)
          .getBytes(StandardCharsets.UTF_8);
    }
    ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
    writeBuffer.put(bytes);
    return writeBuffer;
  }

  @Override
  public String toString() {
    if (string == null) {
      return "BulkStrings[]";
    } else {
      return "BulkStrings[" + string + "]";
    }
  }
}
