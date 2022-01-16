package com.github.nailcui.sim.example.echo;

import com.github.nailcui.sim.Server;
import java.io.IOException;

/**
 * @author dingyu
 * @date 2021-12-19 00:40
 */
public class EchoServer {

  public static void main(String[] args) {
    try {
      Server server = Server.tcp(10010);
      server.handler(new ServerHandler());
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
