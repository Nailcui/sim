package com.github.nailcui.sim.project.redis.server.object;

import com.github.nailcui.sim.codec.resp2.command.BulkStrings;
import com.github.nailcui.sim.codec.resp2.command.Command;
import com.github.nailcui.sim.project.redis.server.RedisObject;

/**
 * @author dingyu
 * @date 2022-01-30 14:22
 */
public class RedisString implements RedisObject {

  private final String string;

  public RedisString(String string) {
    this.string = string;
  }

  @Override
  public Command toCommand() {
    return new BulkStrings(this.string);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof String) {
      return obj.equals(string);
    } else if (obj instanceof RedisString) {
      return string.equals(((RedisString) obj).string);
    }
    return false;
  }

  @Override
  public int hashCode() {
    return string.hashCode();
  }

  @Override
  public String toString() {
    return string;
  }
}
