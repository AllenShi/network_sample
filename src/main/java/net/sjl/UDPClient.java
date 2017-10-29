package net.sjl;

import java.net.*;
import java.io.*;
import static java.util.Arrays.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class UDPClient {

  public static void main(String[] args) {
    OptionParser parser = new OptionParser() {
      {
        acceptsAll(asList("s", "server")).withRequiredArg().describedAs("the target UDP server name or ip to send").ofType(String.class);
        acceptsAll(asList("p", "port")).withRequiredArg().describedAs("the target UDP server port to send").ofType(Integer.class).defaultsTo(6789);
        accepts("m").withRequiredArg().describedAs("the message to send").ofType(String.class);
      }
    };

    DatagramSocket aSocket = null;
    try {
      parser.printHelpOn(System.out);
      OptionSet options = parser.parse(args);
      aSocket = new DatagramSocket();
      byte[] m = ((String)options.valueOf("m")).getBytes();
      String host = options.has("s") ? (String)options.valueOf("s") : (String)options.valueOf("server");
      InetAddress server = InetAddress.getByName(host);
      int serverPort = options.has("p") ? (Integer)options.valueOf("p") : (Integer)options.valueOf("port");
      DatagramPacket request = new DatagramPacket(m, m.length, server, serverPort);
      aSocket.send(request);
      byte[] buffer = new byte[1024];
      DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
      aSocket.receive(reply);
      System.out.println("Reply: " + new String(reply.getData()));
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
