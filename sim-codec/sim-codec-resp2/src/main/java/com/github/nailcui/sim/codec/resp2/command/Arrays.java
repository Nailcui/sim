package com.github.nailcui.sim.codec.resp2.command;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

/**
 * @author dingyu
 * @date 2022-01-29 23:10
 */
public class Arrays implements Command {

  private Command[] commands;

  public Arrays(Command[] commands) {
    this.commands = commands;
  }

  @Override
  public ByteBuffer encode() {
    byte[] bytes = ("*" + commands.length + "\r\n").getBytes(StandardCharsets.UTF_8);
    int total = bytes.length;
    List<ByteBuffer> bulkStringBufferList = new ArrayList<>(commands.length);

    for (Command command : commands) {
      ByteBuffer bulkStringBuffer = command.encode();
      bulkStringBuffer.flip();
      bulkStringBufferList.add(bulkStringBuffer);
      total += bulkStringBuffer.array().length;
    }

    ByteBuffer writeBuffer = ByteBuffer.allocate(total);
    writeBuffer.put(bytes);
    for (ByteBuffer bulkStringBuffer : bulkStringBufferList) {
      writeBuffer.put(bulkStringBuffer.array());
    }
    return writeBuffer;
  }

  @Override
  public String toString() {
    return "Arrays[" + java.util.Arrays.toString(commands) +"]";
  }
}
