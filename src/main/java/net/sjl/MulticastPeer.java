package net.sjl;

import java.net.*;
import java.io.*;
import static java.util.Arrays.*;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

public class MulticastPeer {

  public static void main(String[] args) {
    OptionParser parser = new OptionParser() {
      {
        acceptsAll(asList("g", "group")).withRequiredArg().describedAs("the multicast address").ofType(String.class);
        acceptsAll(asList("p", "port")).withRequiredArg().describedAs("the multicast port").ofType(Integer.class).defaultsTo(6789);
        accepts("m").withRequiredArg().describedAs("the message to send").ofType(String.class);
      }
    };

    MulticastSocket mPeer = null;
    try {
      parser.printHelpOn(System.out);
      OptionSet options = parser.parse(args);
      String group = options.has("g") ? (String)options.valueOf("g") : (String)options.valueOf("group");
      InetAddress mcast = InetAddress.getByName(group);
      int mcastPort = options.has("p") ? (Integer)options.valueOf("p") : (Integer)options.valueOf("port");
      mPeer = new MulticastSocket(mcastPort);
      mPeer.joinGroup(mcast);
      String msg  = options.has("m") ? (String)options.valueOf("m") : "";
      byte[] m = new StringBuilder(msg).append(" from ").append(InetAddress.getLocalHost().getHostName()).toString().getBytes();
      DatagramPacket msgOut = new DatagramPacket(m, m.length, mcast, mcastPort);
      mPeer.send(msgOut);
      byte[] buffer = new byte[1024];
      for(int i = 0; i < 3; i++) {
        DatagramPacket msgIn = new DatagramPacket(buffer, buffer.length);
        mPeer.receive(msgIn);
        System.out.println("Received: " + new String(msgIn.getData()) + ", from: " + msgIn.getAddress());
      }
      mPeer.leaveGroup(mcast);
    } catch(SocketException e) {
      System.out.println("Socket: " + e.getMessage());
    } catch(IOException e) {
      System.out.println("IO: " + e.getMessage());
    } catch(Exception e) {
      System.out.println("General: " + e.getMessage());
    } finally {
      if(mPeer != null) 
        mPeer.close();
    }
  }
}
