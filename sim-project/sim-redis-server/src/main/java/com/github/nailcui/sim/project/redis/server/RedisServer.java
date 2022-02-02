package com.github.nailcui.sim.project.redis.server;

import com.github.nailcui.sim.Server;
import java.io.IOException;

/**
 * @author dingyu
 * @date 2022-01-30 13:25
 */
public class RedisServer {

  public static void main(String[] args) {
    start(6380);
  }

  public static void start(int port) {
    try {
      // 单线程处理
      Server server = Server.tcp(port, 1);
      server.handler(new RedisServerHandler());
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
