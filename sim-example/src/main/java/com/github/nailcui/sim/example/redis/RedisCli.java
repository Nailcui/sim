package com.github.nailcui.sim.example.redis;

import com.github.nailcui.sim.Client;
import com.github.nailcui.sim.codec.resp2.command.Command;
import com.github.nailcui.sim.example.echo.ClientHandler;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

/**
 * @author dingyu
 * @date 2022-01-29 23:33
 */
public class RedisCli {

  public static void main(String[] args) throws Exception {
    Client client = new Client(new RedisClientHandler());
    client.tail("0.0.0.0", 6379);
    client.async();

    Scanner input = new Scanner(System.in);

    while (true) {
      String command = input.nextLine();
      if (command == null || command.trim().length() == 0) {
        continue;
      }
      client.send(Command.fromString(command));
    }
  }

}
