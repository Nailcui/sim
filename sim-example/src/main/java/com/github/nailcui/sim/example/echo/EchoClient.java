package com.github.nailcui.sim.example.echo;

import com.github.nailcui.sim.Client;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author dingyu
 * @date 2022-01-16 17:47
 */
public class EchoClient {

  public static void main(String[] args) throws Exception {
    Client client = new Client(new ClientHandler());
    client.tail("127.0.0.1", 10010);
    client.async();
    while (true) {
      TimeUnit.SECONDS.sleep(2);
      client.send("now time is: " + new Date());
    }

  }

}
