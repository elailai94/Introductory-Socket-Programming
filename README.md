# Introductory Socket Programming
### About
The client and server programs are written entirely in Java. The client sends requests to the server to reverse strings over the network using sockets. The client and the server uses a two stage communication process. In the negotiation stage, the client and the server negotiate on a random port(\<r_port\>) for later use through a fixed negotiation port(\<n_port\>) of the server. Later in the transaction stage, the client connects to the server through the selected random port for actual data transfer.

### Compilation
```Bash
make
```

### Clean Build
```Bash
make clean
```

### Execution
```Bash
./server.sh <req_code>
./client.sh
```

### License
Introductory Socket Programming is licensed under the [MIT license](https://github.com/elailai94/Introductory-Socket-Programming/blob/master/LICENSE.md).
