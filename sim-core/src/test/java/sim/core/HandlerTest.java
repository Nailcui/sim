package sim.core;

import com.github.nailcui.sim.ChannelContext;
import com.github.nailcui.sim.handler.AbstractHandler;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Queue;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dingyu
 * @date 2021-12-19 00:40
 */
@Slf4j
public class HandlerTest extends AbstractHandler {

  @Override
  public void onConnect(ChannelContext context) {
    log.info("on connect");
  }

  @Override
  public void onMessage(ChannelContext context, Object msg) {
    log.info("on message: {}", msg);
    context.write(msg);
  }

  @Override
  public void onInvalid(ChannelContext context) {
    log.info("on valid");
  }

  @Override
  public void onException(ChannelContext context, Exception e) {
    log.info("on exception");
  }

  @Override
  public ByteBuffer encode(Object msg) {
    return ByteBuffer.wrap(((String)msg).getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public void decode(ByteBuffer readBuffer, Queue<Object> readQueue) {
    String msg = new String(readBuffer.array(), readBuffer.position(), readBuffer.limit(),
        StandardCharsets.UTF_8);
    log.info("decode: {}", msg);
    readQueue.offer(msg);
    readBuffer.position(readBuffer.limit());
  }
}
