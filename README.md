sim
---

sim is a simple network application framework.

### feature
- [x] framework
  - [x] tcp server
  - [x] tcp client
  - [ ] new byte buffer
  - [ ] avoid java nio bug
  - [ ] performance testing
- [x] codec
  - [x] line codec
  - [x] [Redis RESP2 codec](https://github.com/Nailcui/sim/blob/master/sim-codec/sim-codec-resp2)
  - [x] http1.1 codec
- [x] experimental project
  - [x] [redis server](https://github.com/Nailcui/sim/blob/master/sim-project/sim-redis-server)

### example

#### echo server
```
  Server server = Server.tcp(10010);
  server.handler(new ServerHandler());
  server.start();
```

#### echo client
```
  Server server = Server.tcp(10010);
  server.handler(new ServerHandler());
  server.start();
```

#### redis client

```
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

```
