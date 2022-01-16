package com.github.nailcui.sim;

import com.github.nailcui.sim.handler.Handler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

/**
 * @author dingyu
 * @date 2021-12-19 00:40
 */
public class Client {

  private EventLoop eventLoop;
  private SocketChannel channel;
  private Handler handler;
  private ChannelContext context;

  public Client(Handler handler) {
    this.handler = handler;
  }

  public Client tail(String host, int port) throws IOException {
    this.channel = SocketChannel.open(new InetSocketAddress(host, port));
    this.channel.configureBlocking(false);
    this.eventLoop = new EventLoop();
    this.context = new ChannelContext(this.channel, this.handler, this.eventLoop);
    this.eventLoop.register(this.context);
    return this;
  }

  public Client sync() {
    this.eventLoop.run();
    return this;
  }

  public Client async() {
    this.eventLoop.start();
    return this;
  }

  public Client send(Object o) throws IOException {
    this.context.write(o);
    return this;
  }
}
