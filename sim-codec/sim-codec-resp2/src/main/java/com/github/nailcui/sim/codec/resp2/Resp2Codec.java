package com.github.nailcui.sim.codec.resp2;

import com.github.nailcui.sim.buffer.ByteBufferUtils;
import com.github.nailcui.sim.codec.resp2.command.Arrays;
import com.github.nailcui.sim.codec.resp2.command.BulkStrings;
import com.github.nailcui.sim.codec.resp2.command.Command;
import com.github.nailcui.sim.codec.resp2.command.Errors;
import com.github.nailcui.sim.codec.resp2.command.Integers;
import com.github.nailcui.sim.codec.resp2.command.SimpleStrings;
import com.github.nailcui.sim.handler.Codec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dingyu
 * @date 2022-01-27 23:28
 */
@Slf4j
public class Resp2Codec implements Codec {

  @Override
  public ByteBuffer encode(Object msg) {
    if (msg instanceof Command) {
      return ((Command) msg).encode();
    } else {
      byte[] bytes = ((String) msg).getBytes(StandardCharsets.UTF_8);
      ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
      writeBuffer.put(bytes);
      return writeBuffer;
    }
  }

  @Override
  public void decode(ByteBuffer readBuffer, Queue<Object> readQueue) {
    while (true) {
      int position = readBuffer.position();
      Command command = readCommand(readBuffer);
      if (command == null) {
        readBuffer.position(position);
        return;
      } else {
        readQueue.add(command);
      }
    }
  }

  private Command readCommand(ByteBuffer readBuffer) {
    Command command = null;
    if (readBuffer.remaining() <= 1) {
      // 可读字节不够
      return null;
    }
    byte label = ByteBufferUtils.getAndSkip(readBuffer);
    if (label == '+') {
      command = readSimpleStrings(readBuffer);
    }
    if (label == '-') {
      command = readErrors(readBuffer);
    }
    if (label == ':') {
      command = readIntegers(readBuffer);
    }
    if (label == '$') {
      command = readBulkStrings(readBuffer);
    }
    if (label == '*') {
      command = readArrays(readBuffer);
    }
    return command;
  }

  private Command readSimpleStrings(ByteBuffer readBuffer) {
    Integer index = ByteBufferUtils.findCRLF(readBuffer);
    if (index == null) {
      return null;
    }
    String string = ByteBufferUtils.readString(readBuffer, index - readBuffer.position());
    readBuffer.position(index + 2);
    return new SimpleStrings(string);
  }

  private Command readErrors(ByteBuffer readBuffer) {
    Integer index = ByteBufferUtils.findCRLF(readBuffer);
    if (index == null) {
      return null;
    }
    String string = ByteBufferUtils.readString(readBuffer, index - readBuffer.position());
    readBuffer.position(index + 2);
    return new Errors(string);
  }

  private Command readIntegers(ByteBuffer readBuffer) {
    Integer integer = readLength(readBuffer);
    if (integer == null) {
      return null;
    }
    return new Integers(integer);
  }

  private Command readBulkStrings(ByteBuffer readBuffer) {
    int position = readBuffer.position();
    Integer len = readLength(readBuffer);
    if (len == null) {
      readBuffer.position(position);
      return null;
    }
    if (len == -1) {
      return new BulkStrings();
    }
    if (readBuffer.remaining() < len+2) {
      readBuffer.position(position);
      return null;
    }
    String string = ByteBufferUtils.readString(readBuffer, len);
    readBuffer.position(readBuffer.position() + len + 2);
    return new BulkStrings(string);
  }

  private Command readArrays(ByteBuffer readBuffer) {
    int position = readBuffer.position();
    Integer len = readLength(readBuffer);
    if (len == null || readBuffer.remaining() < len+2) {
      readBuffer.position(position);
      return null;
    }
    if (readBuffer.remaining() < len * 2) {
      readBuffer.position(position);
      return null;
    }
    if (len == 0) {
      System.out.println("len = 0 !!!!!!!!!!!!!!");
      return null;
    }
    Command[] commands = new Command[len];
    for (int i = 0; i < len; i++) {
      Command command = readCommand(readBuffer);
      if (command == null) {
        readBuffer.position(position);
        return null;
      }
      commands[i] = command;
    }
    return new Arrays(commands);
  }

  private Integer readLength(ByteBuffer readBuffer) {
    Integer index = ByteBufferUtils.findCRLF(readBuffer);
    if (index != null) {
      Integer result = new Integer(new String(
          readBuffer.array(), readBuffer.position(),
          index - readBuffer.position(), StandardCharsets.UTF_8));
      readBuffer.position(index + 2);
      return result;
    }
    return null;
  }

}
