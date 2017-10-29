package net.sjl;

import java.net.*;
import java.io.*;
import static java.util.Arrays.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class UDPServer {

  public static void main(String[] args) {
    OptionParser parser = new OptionParser() {
      {
        acceptsAll(asList("p", "port")).withRequiredArg().describedAs("the target UDP server port to send").ofType(Integer.class).defaultsTo(6789);
      }
    };

    DatagramSocket aSocket = null;
    try {
      parser.printHelpOn(System.out);
      OptionSet options = parser.parse(args);
      int serverPort = options.has("p") ? (Integer)options.valueOf("p") : (Integer)options.valueOf("port");
      aSocket = new DatagramSocket(serverPort);
      byte[] buffer = new byte[1024];
      while(true) {
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        aSocket.receive(request);
        System.out.println("Received: " + new String(request.getData()));
        DatagramPacket reply = new DatagramPacket(request.getData(), request.getLength(), request.getAddress(), request.getPort());
        aSocket.send(reply);
      }
    } catch(SocketException e) {
      System.out.println("Socket: " + e.getMessage());
    } catch(IOException e) {
      System.out.println("IO: " + e.getMessage());
    } catch(Exception e) {
      System.out.println("General: " + e.getMessage());
    } finally {
      if(aSocket != null) 
        aSocket.close();
    }
  }
}
