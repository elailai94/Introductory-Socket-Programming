//==============================================================================
// Introductory Socket Programming
//
// @description: An implementation of a server program in Java
// @author: Elisha Lai
// @version: 1.0 10/11/2016
//==============================================================================

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.DataOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

public class server {
   public static void main(String[] args) throws Exception {
      // Checks the number and formats of the command line arguments passed
      checkCommandLineArguments(args);

      int serverRequestCode = Integer.parseInt(args[0]);

      // Creates the welcoming socket with an automatically allocated
      // negotiation port number
      ServerSocket welcomeSocket = new ServerSocket(0);

      // Retrieves the automatically allocated negotiation port
      // number and prints it on the server's standard output
      int negotiationPort = welcomeSocket.getLocalPort();
      System.out.println("SERVER_PORT=" + negotiationPort);

      while (true) {
         // Creates the connection socket with the automatically allocated
         // negotiation port number when a connection is made to the
         // welcoming socket
         Socket connectionSocket = welcomeSocket.accept();

         // Create input and output streams, which are attached to the
         // connection socket
         BufferedReader inputFromClient =
            new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
         DataOutputStream outputToClient =
            new DataOutputStream(connectionSocket.getOutputStream());

         // Performs the negotiation stage
         performNegotiationStage(serverRequestCode, inputFromClient,
            outputToClient, connectionSocket);
      } // while
   } // main

   // Performs the negotiation stage
   private static void performNegotiationStage(int serverRequestCode,
      BufferedReader inputFromClient, DataOutputStream outputToClient,
      Socket connectionSocket) throws Exception {
      // Reads in the request code from the connection socket, which is
      // sent from the client
      int clientRequestCode = Integer.parseInt(inputFromClient.readLine());

      // Closes the TCP connection between the client and the server if
      // the client fails to send the intended request code. Otherwise,
      // sends a random port number to the client and performs the
      // transaction stage
      if (serverRequestCode != clientRequestCode) {
         // Closes the TCP connection between the client and the server
         connectionSocket.close();
      } else {
         // Creates the data transfer socket with an automatically
         // allocated random port number
         DatagramSocket dataTransferSocket = new DatagramSocket();

         // Retrieves the automatically allocated random port number
         // and writes it out to the connection socket, which is sent
         // to the client
         int randomPort = dataTransferSocket.getLocalPort();
         outputToClient.writeBytes(Integer.toString(randomPort) + "\n");

         // Performs the transaction stage
         performTransactionStage(dataTransferSocket);
      } // if
   } // performNegotiationStage

   // Checks the number and formats of the command line arguments passed
   private static void checkCommandLineArguments(String[] args) throws Exception {
      if (args.length != 1) {
         System.out.println("ERROR: Expecting 1 command line argument," +
            " but got " + args.length + " arguments");
         System.exit(-1);
      } // if

      try {
         int requestCode = Integer.parseInt(args[0]);
      } catch (NumberFormatException e) {
         System.out.println("ERROR: Expecting a request code which is" +
            " an integer, but got " + args[0]);
         System.exit(-1);
      } // try
   } // checkCommandLineArguments

   // Performs the transaction stage
   private static void performTransactionStage(DatagramSocket dataTransferSocket) 
      throws Exception {
      // Creates a packet to receive data from the client and reads
      // into it from the data transfer socket
      byte[] dataFromClient = new byte[1024];
      DatagramPacket packetFromClient =
         receivePacket(dataFromClient, dataTransferSocket);
            
      // Creates a packet to send data to the client and writes it out
      // to the data transfer socket
      byte[] dataToClient = new byte[1024];
      String clientMessage = new String(packetFromClient.getData());
      String reversedMessage = reverseString(clientMessage);
      dataToClient = reversedMessage.getBytes();
      InetAddress clientIPAddress = packetFromClient.getAddress();
      int clientPort = packetFromClient.getPort();
      sendPacket(dataToClient, clientIPAddress, clientPort, dataTransferSocket);

      // Closes the data transfer socket
      dataTransferSocket.close(); 
   } // performTransactionStage

   // Creates a packet to receive data from the client and reads into it 
   // from the data transfer socket
   private static DatagramPacket receivePacket(byte[] dataFromClient,
      DatagramSocket dataTransferSocket) throws Exception {
      DatagramPacket packetFromClient =
         new DatagramPacket(dataFromClient, dataFromClient.length);
      dataTransferSocket.receive(packetFromClient);
      return packetFromClient;
   } // receivePacket

   // Creates a packet to send data to the client and writes it out to the
   // data transfer socket
   private static void sendPacket(byte[] dataToClient, InetAddress clientIPAddress,
      int clientPort, DatagramSocket dataTransferSocket) throws Exception {
      DatagramPacket packetToClient =
         new DatagramPacket(dataToClient, dataToClient.length, clientIPAddress, clientPort);
      dataTransferSocket.send(packetToClient);
   } // sendPacket

   // Returns a new string with the characters from originalString in
   // reverse order
   private static String reverseString(String originalString) {
      String reversedString =
         new StringBuffer(originalString).reverse().toString();
      return reversedString;
   } // reverseString
}
