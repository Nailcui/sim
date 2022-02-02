package com.github.nailcui.sim.project.redis.server.processor;

import com.github.nailcui.sim.codec.resp2.command.Command;
import com.github.nailcui.sim.codec.resp2.command.Errors;
import com.github.nailcui.sim.codec.resp2.command.SimpleStrings;
import com.github.nailcui.sim.project.redis.server.Database;
import com.github.nailcui.sim.project.redis.server.Processor;

/**
 * @author dingyu
 * @date 2022-01-30 13:44
 */
public class Ping implements Processor {

  public static final String KEY = "PING";
  public static final Command RESULT = new SimpleStrings("PONG");

  @Override
  public String key() {
    return KEY;
  }

  @Override
  public Command process(Database database, Command[] commands) {
    if (commands.length != 1) {
      return new Errors("ERR wrong number of arguments for '" + KEY + "' command");
    }
    return RESULT;
  }
}
