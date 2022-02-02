package com.github.nailcui.sim.project.redis.server.processor;

import com.github.nailcui.sim.codec.resp2.command.BulkStrings;
import com.github.nailcui.sim.codec.resp2.command.Command;
import com.github.nailcui.sim.codec.resp2.command.Errors;
import com.github.nailcui.sim.project.redis.server.Database;
import com.github.nailcui.sim.project.redis.server.Processor;
import com.github.nailcui.sim.project.redis.server.RedisObject;

/**
 * @author dingyu
 * @date 2022-01-30 13:44
 */
public class Get implements Processor {

  public static final String KEY = "GET";

  @Override
  public String key() {
    return KEY;
  }

  @Override
  public Command process(Database database, Command[] commands) {
    if (commands.length != 2) {
      return new Errors("ERR wrong number of arguments for '" + KEY + "' command");
    }
    RedisObject result = database.get(((BulkStrings) commands[1]).getString());
    if (result == null) {
      return new BulkStrings();
    }
    return result.toCommand();
  }
}
