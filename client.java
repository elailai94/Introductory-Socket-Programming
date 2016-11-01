//==============================================================================
// Introductory Socket Programming
//
// @description: An implementation of a client program in Java
// @author: Elisha Lai
// @version: 1.0 11/10/2016
//==============================================================================

import java.io.DataOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.net.Socket;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class client {
   public static void main(String[] args) throws Exception {
      // Checks the number and formats of the command line arguments passed
      checkCommandLineArguments(args);

      String serverAddress = args[0];
      int negotiationPort = Integer.parseInt(args[1]);
      int requestCode = Integer.parseInt(args[2]);
      String message = args[3];

      // Performs the negotiation stage
      int randomPort =
         performNegotiationStage(serverAddress, negotiationPort, requestCode);

      // Performs the transaction stage
      performTransactionStage(message, serverAddress, randomPort);
   } // main

   // Checks the number and formats of the command line arguments passed
   private static void checkCommandLineArguments(String[] args) throws Exception {
      if (args.length != 4) {
         System.out.println("ERROR: Expecting 4 command line arguments," +
            " but got " + args.length + " arguments");
         System.exit(-1);
      } // if

      if ((!isValidIPAddress(args[0])) && (!isValidHostName(args[0]))) {
         System.out.println("ERROR: Expecting a server address which is" +
            " a valid IP address or host name, but got " + args[0]);
         System.exit(-1);
      } // if

      try {
         int negotiationPort = Integer.parseInt(args[1]);
      } catch (NumberFormatException e) {
         System.out.println("ERROR: Expecting a negotiation port which is" +
            " an integer, but got " + args[1]);
         System.exit(-1);
      } // try

      try {
         int requestCode = Integer.parseInt(args[2]);
      } catch (NumberFormatException e) {
         System.out.println("ERROR: Expecting a request code which is an" +
            " integer, but got " + args[2]);
         System.exit(-1);
      } // try

      if (!isValidMessage(args[3])) {
         System.out.println("ERROR: Expecting a message which has a size" +
            " of at most 1024 bytes, but got " + args[3].getBytes().length +
            " bytes");
         System.exit(-1);
      } // if
   } // checkCommandLineArguments

   // Checks if a string is a valid IP address, which ranges from 0.0.0.0 to
   // 255.255.255.255
   private static boolean isValidIPAddress(String string) throws Exception {
      String regex = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                     "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                     "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                     "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";
      boolean isRegexMatch = string.matches(regex);
      return isRegexMatch;
   } // isIPAddress

   // Checks if a string is a valid host name, which complies with RFC 1912
   private static boolean isValidHostName(String string) throws Exception {
      String regex = "^([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])" +
                     "(\\.([a-zA-Z0-9]|[a-zA-Z0-9][a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9]))*$";
      boolean isRegexMatch = string.matches(regex);
      
      boolean isCorrectLength = (string.length() <= 255);
      
      String[] labels = string.split("\\.");
      boolean isLabelsNotAllNumeric = true;
      for (String label : labels) {
         if (label.matches("^[0-9]+$")) {
            isLabelsNotAllNumeric = false;
            break;
         } // if
      } // for

      if (isRegexMatch && isCorrectLength && isLabelsNotAllNumeric) {
         return true;
      } else {
         return false;
      } // if
   } // isHostName

   // Checks if a string is a valid message, which has a size of at most
   // 1024 bytes
   private static boolean isValidMessage(String string) throws Exception {
      boolean isCorrectSize = (string.getBytes().length <= 1024);
      return isCorrectSize;
   } // isValidMessage

   // Performs the negotiation stage
   private static int performNegotiationStage(String serverAddress,
      int negotiationPort, int requestCode) throws Exception {
      // Creates the client TCP socket and initiates the TCP connection
      // between the client and the server
      Socket clientTCPSocket = new Socket(serverAddress, negotiationPort);

      // Create input and output streams, which are attached to the client
      // TCP socket
      DataOutputStream outputToServer =
         new DataOutputStream(clientTCPSocket.getOutputStream());
      BufferedReader inputFromServer =
         new BufferedReader(new InputStreamReader(clientTCPSocket.getInputStream()));

      // Writes the request code out to the client TCP socket, which is
      // sent to the server
      outputToServer.writeBytes(Integer.toString(requestCode) + "\n");

      // Attempts to read in the random port number from the client TCP
      // socket, which may or may not be sent from the server
      String randomPortStr = inputFromServer.readLine();
      if (randomPortStr == null) { 
         System.out.println("ERROR: Expecting a request code which" +
            " matches the request code at the server, but got " +
            requestCode);
         System.exit(-1);
      } // if
      int randomPortNum = Integer.parseInt(randomPortStr);

      // Closes the TCP connection between the client and the server
      clientTCPSocket.close();

      return randomPortNum;
   } // performNegotiationStage

   // Performs the transaction stage
   private static void performTransactionStage(String message, String serverAddress,
      int randomPort) throws Exception {
      // Creates the client UDP socket
      DatagramSocket clientUDPSocket = new DatagramSocket();
      
      // Creates a packet to send data to the server and writes it out to
      // the client UDP socket
      byte[] dataToServer = new byte[1024];
      dataToServer = message.getBytes();
      InetAddress serverIPAddress = InetAddress.getByName(serverAddress);
      sendPacket(dataToServer, serverIPAddress, randomPort, clientUDPSocket);

      // Creates a packet to receive data from the server and reads into
      // it from the client UDP socket
      byte[] dataFromServer = new byte[1024];
      DatagramPacket packetFromServer =
         receivePacket(dataFromServer, clientUDPSocket);

      // Reads in the reversed message 
      String reversedMesssage = new String(packetFromServer.getData());
      System.out.println(reversedMesssage);

      // Closes the client UDP socket
      clientUDPSocket.close();
   } // performTransactionStage

   // Creates a packet to receive data from the server and reads into it 
   // from the data transfer socket
   private static DatagramPacket receivePacket(byte[] dataFromServer,
      DatagramSocket dataTransferSocket) throws Exception {
      DatagramPacket packetFromServer =
         new DatagramPacket(dataFromServer, dataFromServer.length);
      dataTransferSocket.receive(packetFromServer);
      return packetFromServer;
   } // receivePacket

   // Creates a packet to send data to the server and writes it out to the
   // data transfer socket
   private static void sendPacket(byte[] dataToServer, InetAddress serverIPAddress,
      int serverPort, DatagramSocket dataTransferSocket) throws Exception {
      DatagramPacket packetToServer =
         new DatagramPacket(dataToServer, dataToServer.length, serverIPAddress, serverPort);
      dataTransferSocket.send(packetToServer);
   } // sendPacket
}
