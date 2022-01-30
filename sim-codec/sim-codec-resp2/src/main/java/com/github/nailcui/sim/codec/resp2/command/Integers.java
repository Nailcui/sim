package com.github.nailcui.sim.codec.resp2.command;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author dingyu
 * @date 2022-01-29 23:10
 */
public class Integers implements Command {

  private final Integer integer;

  public Integers(Integer integer) {
    this.integer = integer;
  }

  @Override
  public ByteBuffer encode() {
    byte[] bytes = ("+" + integer + SEPARATOR_CR + SEPARATOR_LF)
        .getBytes(StandardCharsets.UTF_8);
    ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
    writeBuffer.put(bytes);
    return writeBuffer;
  }

  @Override
  public String toString() {
    return "Integers[" + integer + "]";
  }
}
