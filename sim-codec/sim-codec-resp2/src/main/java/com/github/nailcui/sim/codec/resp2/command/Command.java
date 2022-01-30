package com.github.nailcui.sim.codec.resp2.command;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

/**
 * @author dingyu
 * @date 2022-01-29 23:10
 */
public interface Command {

  String PREFIX_SIMPLE_STRINGS = "+";
  String PREFIX_ERRORS = "-";
  String PREFIX_INTEGERS = ":";
  String PREFIX_BULK_STRINGS = "$";
  String PREFIX_ARRAYS = "*";
  String SEPARATOR_CR = "\r";
  String SEPARATOR_LF = "\n";
  byte[] SEPARATOR = "\r\n".getBytes(StandardCharsets.UTF_8);

  /**
   * en
   * @param s s
   * @return Command
   */
  static Command fromString(String s) {
    s = s.trim();
    String[] strings = s.split(" ");
    if (strings.length > 0) {
      BulkStrings[] bulkStrings = new BulkStrings[strings.length];
      for (int i = 0; i < strings.length; i++) {
        bulkStrings[i] = new BulkStrings(strings[i]);
      }
      return new Arrays(bulkStrings);
    }
    return null;
  }

  /**
   * encode
   */
  ByteBuffer encode();

}
