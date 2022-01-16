package com.github.nailcui.sim;

import com.github.nailcui.sim.handler.Handler;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dingyu
 * @date 2021-12-19 00:40
 */
@Slf4j
public class Server {
  private final ServerSocketChannel server = ServerSocketChannel.open();
  private final Selector selector = Selector.open();
  private int port = 10010;
  private Handler handler;
  private List<EventLoop> loops;
  private long loopSize;
  private long conns = 0;

  Server() throws IOException {
  }
  public static Server tcp(int port) throws IOException {
    return tcp(port, 2);
  }

  public static Server tcp(int port, int loopSize) throws IOException {
    if (loopSize <= 0) {
      loopSize = 2;
    }
    Server server = new Server();
    server.port = port;
    server.loopSize = loopSize;
    server.loops = new ArrayList<>();
    for (long i = 0; i < server.loopSize; i++) {
      EventLoop eventLoop = new EventLoop();
      server.loops.add(eventLoop);
      Thread thread = new Thread(eventLoop, "loop-" + i);
      thread.start();
    }
    return server;
  }

  public void handler(Handler handler) {
    this.handler = handler;
  }

  public void start() throws IOException {
    log.debug("start server in " + this.port);
    this.server.configureBlocking(false);
    this.server.socket().bind(new InetSocketAddress(this.port));
    this.server.register(selector, SelectionKey.OP_ACCEPT);
    while (true) {
      log.debug("select...");
      this.selector.select();
      log.debug("selected...");
      Iterator<SelectionKey> keyIterator = selector.selectedKeys().iterator();
      while (keyIterator.hasNext()) {
        SelectionKey key = keyIterator.next();
        keyIterator.remove();
        if (key.isAcceptable()) {
          log.debug("key acceptable");
          this.conns++;
          EventLoop eventLoop = this.loops.get((int) (this.conns % this.loopSize));
          SocketChannel client = server.accept();
          eventLoop.register(new ChannelContext(client, this.handler, eventLoop));
          log.debug("key acceptable register end");
        } else {
          log.debug("unKnow key type: " + key);
        }
      }
    }
  }

}
