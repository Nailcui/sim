sim
---

sim is a simple network application framework.

### feature

- [x] tcp server
- [x] tcp client
- [x] codec
  - [x] line codec
  - [x] Redis RESP2 codec

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
