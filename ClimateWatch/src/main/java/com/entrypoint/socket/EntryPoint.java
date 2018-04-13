package com.entrypoint.socket;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Enumeration;

public class EntryPoint {
	
	private static Enumeration<NetworkInterface> networkInterfaces;

	public static void main(String[] args) {
		
	// get ip
//	 PC pc = new PC(1,"test");
	 
		class test
		{
			
		}
	 // convert to a node
		test test2 = new test();
	

	
	}
	
	
	
	
	
	
	byte[] buf = new byte[256];
	
	public void run() {
    	try
    	{
        MulticastSocket socket = new MulticastSocket(4446);
        
        
        InetAddress group = InetAddress.getByName("230.0.0.0");
        socket.joinGroup(group);
        while (true) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            System.out.println("receiving packets");
            String received = new String(
              packet.getData(), 0, packet.getLength());
            System.out.println("received Packet " + received);
            if ("end".equals(received)) {
                break;
            }
            System.out.println("done");
        }
        socket.leaveGroup(group);
        socket.close();
    	}
    	catch(Exception e)
    	{
    		
    	}
    }

}
