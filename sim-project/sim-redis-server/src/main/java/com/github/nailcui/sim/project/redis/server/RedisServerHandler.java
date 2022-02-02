package com.github.nailcui.sim.project.redis.server;

import com.github.nailcui.sim.ChannelContext;
import com.github.nailcui.sim.codec.resp2.Resp2Codec;
import com.github.nailcui.sim.codec.resp2.command.Arrays;
import com.github.nailcui.sim.codec.resp2.command.BulkStrings;
import com.github.nailcui.sim.codec.resp2.command.Command;
import com.github.nailcui.sim.codec.resp2.command.Errors;
import com.github.nailcui.sim.handler.AbstractHandler;
import com.github.nailcui.sim.handler.Codec;
import com.github.nailcui.sim.project.redis.server.processor.Del;
import com.github.nailcui.sim.project.redis.server.processor.Echo;
import com.github.nailcui.sim.project.redis.server.processor.Exists;
import com.github.nailcui.sim.project.redis.server.processor.Get;
import com.github.nailcui.sim.project.redis.server.processor.Ping;
import com.github.nailcui.sim.project.redis.server.processor.Set;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * @author dingyu
 * @date 2022-01-30 13:27
 */
public class RedisServerHandler extends AbstractHandler {

  private final Codec codec = new Resp2Codec();
  private final Database database = new Database();

  public static Map<String, Processor> processors = new HashMap<>(8);

  static {
    processors.put(Echo.KEY.toUpperCase(), new Echo());
    processors.put(Ping.KEY.toUpperCase(), new Ping());
    processors.put(Get.KEY.toUpperCase(), new Get());
    processors.put(Set.KEY.toUpperCase(), new Set());
    processors.put(Del.KEY.toUpperCase(), new Del());
    processors.put(Exists.KEY.toUpperCase(), new Exists());
  }

  @Override
  public ByteBuffer encode(Object msg) {
    return codec.encode(msg);
  }

  @Override
  public void decode(ByteBuffer readBuffer, Queue<Object> readQueue) {
    codec.decode(readBuffer, readQueue);
  }

  @Override
  public void onConnect(ChannelContext context) {

  }

  @Override
  public void onInvalid(ChannelContext context) {

  }

  @Override
  public void onException(ChannelContext context, Exception e) {

  }

  @Override
  public void onMessage(ChannelContext context, Object msg) {
    if (msg instanceof Command) {
      Command result = process(context, (Command) msg);
      if (result != null) {
        context.write(result);
      }
    }
  }

  public Command process(ChannelContext context, Command command) {
    if (command instanceof Arrays) {
      return processArrays(context, (Arrays) command);
    } else {
      return new Errors("unSupport command: " + command);
    }
  }

  public Command processArrays(ChannelContext context, Arrays command) {
    Command[] commands = command.getCommands();
    if (commands[0] instanceof BulkStrings) {
      return processBulkStrings(context, commands);
    } else {
      return new Errors("unSupport command: " + command);
    }
  }

  public Command processBulkStrings(ChannelContext context, Command[] command) {
    String key = ((BulkStrings)command[0]).getString();
    Processor processor = processors.get(key.toUpperCase());
    if (processor == null) {
      return new Errors("ERR unknown command `" + key + "`, with args beginning with:");
    }
    return processor.process(this.database, command);
  }
}
