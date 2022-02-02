package com.github.nailcui.sim.project.redis.server;

import com.github.nailcui.sim.project.redis.server.object.RedisString;
import java.util.HashMap;
import java.util.Map;

/**
 * @author dingyu
 * @date 2022-01-30 13:30
 */
public class Database {
  private Map<RedisObject, RedisObject> db = new HashMap<>(32);
  private Map<RedisObject, Long> expires = new HashMap<>(32);

  public RedisObject get(String key) {
    RedisObject redisKey = new RedisString(key);
    if (expireIfNeeded(redisKey) == 1) {
      del(key);
      return null;
    }
    return db.get(redisKey);
  }

  public RedisObject set(String key, String value) {
    RedisString redisKey = new RedisString(key);
    this.expires.remove(redisKey);
    return db.put(redisKey, new RedisString(value));
  }

  public void expireAt(String key, Long expireTime) {
    RedisString redisKey = new RedisString(key);
    expires.put(redisKey, expireTime);
  }

  public int del(String key) {
    RedisString redisKey = new RedisString(key);
    int needed = expireIfNeeded(redisKey);
    if (needed == 0) {
      // 没过期，删除并返回1
      db.remove(redisKey);
      return 1;
    } else if (needed == 1) {
      // 已过期，删除并返回0
      db.remove(redisKey);
      return 0;
    }
    return (db.remove(redisKey) == null) ? 0 : 1;
  }

  public boolean exists(String key) {
    RedisString redisKey = new RedisString(key);
    int needed = expireIfNeeded(redisKey);
    if (needed == 0) {
      // 没过期
      return true;
    } else if (needed == 1) {
      // 已过期，删除并返回
      db.remove(redisKey);
      return false;
    }
    return db.get(redisKey) != null;
  }

  /**
   * key 未过期返回0
   * @param key key
   * @return 0: 未过期 1: 已过期 -1: 不存在
   */
  private int expireIfNeeded(RedisObject key) {
    Long expireTime = this.expires.get(key);
    if (expireTime == null) {
      return -1;
    } else if (expireTime > System.currentTimeMillis()) {
      return 0;
    } else {
      return 1;
    }
  }
}
