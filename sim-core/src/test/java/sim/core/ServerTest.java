package sim.core;

import com.github.nailcui.sim.Server;
import java.io.IOException;
import org.junit.Test;

/**
 * @author dingyu
 * @date 2021-12-19 00:40
 */
public class ServerTest {

  @Test
  public void testServer() throws IOException {
    try {
      Server server = Server.tcp(10010);
      server.handler(new HandlerTest());
      server.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
