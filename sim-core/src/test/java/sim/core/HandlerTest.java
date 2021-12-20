package sim.core;

import com.github.nailcui.sim.ChannelContext;
import com.github.nailcui.sim.handler.AbstractHandler;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import lombok.extern.slf4j.Slf4j;

/**
 * @author dingyu
 * @date 2021-12-19 12:12
 */
@Slf4j
public class HandlerTest extends AbstractHandler {

  @Override
  public void onConnect(ChannelContext context) {
    log.info("on connect");
  }

  @Override
  public void onValid(ChannelContext context) {
    log.info("on valid");
  }

  @Override
  public void onException(ChannelContext context, Exception e) {
    log.info("on exception");
  }

  @Override
  public void decode(ChannelContext context, ByteBuffer readBuffer) {
    readBuffer.flip();
    log.info("read: {}", new String(readBuffer.array(), readBuffer.position(), readBuffer.limit(), StandardCharsets.UTF_8));
    readBuffer.clear();
  }
}
