package com.github.nailcui.sim.project.redis.server;

import com.github.nailcui.sim.codec.resp2.command.Command;

/**
 * @author dingyu
 * @date 2022-01-30 13:43
 */
public interface Processor {

  String key();

  Command process(Database database, Command[] commands);

}
