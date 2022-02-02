package com.github.nailcui.sim.project.redis.server.processor;

import com.github.nailcui.sim.codec.resp2.command.BulkStrings;
import com.github.nailcui.sim.codec.resp2.command.Command;
import com.github.nailcui.sim.codec.resp2.command.Errors;
import com.github.nailcui.sim.codec.resp2.command.SimpleStrings;
import com.github.nailcui.sim.project.redis.server.Database;
import com.github.nailcui.sim.project.redis.server.Processor;

/**
 * set key value [expiration EX seconds|PX milliseconds] [NX|XX]
 * @author dingyu
 * @date 2022-01-30 13:44
 */
public class Set implements Processor {

  public static final String KEY = "SET";
  public static final String NX = "NX";
  public static final String XX = "XX";
  public static final String EX = "EX";
  public static final String PX = "PX";

  @Override
  public String key() {
    return KEY;
  }

  @Override
  public Command process(Database database, Command[] commands) {
    if (commands.length == 3) {
      // set key value
      database.set(((BulkStrings) commands[1]).getString(), ((BulkStrings) commands[2]).getString());
      return SimpleStrings.OK;
    } else if (commands.length == 4) {
      // set key value [NX|XX]
      if (NX.equalsIgnoreCase(((BulkStrings) commands[3]).getString())) {
        // NX: 键不存在，设置成功
        if (database.exists(((BulkStrings) commands[1]).getString())) {
          return new BulkStrings();
        } else {
          database.set(((BulkStrings) commands[1]).getString(), ((BulkStrings) commands[2]).getString());
          return SimpleStrings.OK;
        }
      } else if (XX.equalsIgnoreCase(((BulkStrings) commands[3]).getString())) {
        // XX: 键不存在，设置失败
        if (database.exists(((BulkStrings) commands[1]).getString())) {
          database.set(((BulkStrings) commands[1]).getString(), ((BulkStrings) commands[2]).getString());
          return SimpleStrings.OK;
        } else {
          return new BulkStrings();
        }
      } else {
        return new Errors("ERR syntax error");
      }
    } else if (commands.length == 5) {
      // set key value [expiration EX seconds|PX milliseconds]
      String exPxLabel = ((BulkStrings) commands[3]).getString();
      if (EX.equals(exPxLabel)) {
        long expireTime = System.currentTimeMillis() + Long.parseLong(((BulkStrings) commands[4]).getString()) * 1000;
        database.set(((BulkStrings) commands[1]).getString(), ((BulkStrings) commands[2]).getString());
        database.expireAt(((BulkStrings) commands[1]).getString(), expireTime);
        return SimpleStrings.OK;
      } else if (PX.equals(exPxLabel)) {
        long expireTime = System.currentTimeMillis() + Long.parseLong(((BulkStrings) commands[4]).getString());
        database.set(((BulkStrings) commands[1]).getString(), ((BulkStrings) commands[2]).getString());
        database.expireAt(((BulkStrings) commands[1]).getString(), expireTime);
        return SimpleStrings.OK;
      } else {
        return new Errors("ERR syntax error");
      }
    } else if (commands.length == 6) {
      // set key value [expiration EX seconds|PX milliseconds] [NX|XX]
      // todo
      return new Errors("ERR syntax error");
    } else {
      return new Errors("ERR wrong number of arguments for '" + KEY + "' command");
    }
  }
}
