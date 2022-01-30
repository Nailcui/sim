package com.github.nailcui.sim.codec.resp2.command;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author dingyu
 * @date 2022-01-29 23:10
 */
public class SimpleStrings implements Command {

  private final String string;

  public SimpleStrings(String string) {
    this.string = string;
  }

  @Override
  public ByteBuffer encode() {
    byte[] bytes = ("+" + string + SEPARATOR_CR + SEPARATOR_LF)
        .getBytes(StandardCharsets.UTF_8);
    ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
    writeBuffer.put(bytes);
    return writeBuffer;
  }

  @Override
  public String toString() {
    return "SimpleStrings[" + string + "]";
  }
}
