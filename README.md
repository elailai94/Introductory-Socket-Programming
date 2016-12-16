# Introductory Socket Programming
### About
This repository contains client and server programs to communicate between themselves. They are written entirely in Java. The client sends requests to the server to reverse strings over the network using sockets. The client and the server uses a two stage communication process. In the negotiation stage, the client and the server negotiate on a random port(\<r_port\>) for later use through a fixed negotiation port(\<n_port\>) of the server. Later in the transaction stage, the client connects to the server through the selected random port for actual data transfer.


### How Does It Work?
#### Stage 1: Negotiation Using TCP Sockets
In this stage, the client creates a TCP connection with the server using \<server_address\> as the server address and \<n_port\> as the negotiation port on the server (where the server is listening). The client sends a request to get the random port number from the server where it will send the actual request (i.e., the string to be reversed). To initiate this negotiation, the client sends a request code (\<req_code\>), an integer (e.g., 13), after creating the TCP connection. If the client fails to send the intended \<req_code\>, the server closes the TCP connection. Once the server verifies the \<req_code\>, it replies back with a random port number \<r_port\> where it will be listening for the actual request. After receiving this \<r_port\>, the client closes the TCP connection with the server.
#### Stage 2: Transaction Using UDP Sockets
In this stage, the client creates a UDP socket to the server in \<r_port\> and sends the \<msg\> containing a string. On the other side, the server receives the string and sends the reversed string back to the client. Once received, the client prints out the reversed string and exits. The server continues to listen on its \<n_port\> for subsequent client requests.


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
./client.sh <server_address> <n_port> <req_code> '<msg>'
```

### License
This repository is licensed under the [MIT license](https://github.com/elailai94/Introductory-Socket-Programming/blob/master/LICENSE.md).
