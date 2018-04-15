package com.entrypoint.socket;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastReceiver extends Thread {
    protected MulticastSocket socket = null;
    protected byte[] buf = new byte[256];
 
    public void run() {
    	try
    	{
        socket = new MulticastSocket(4446);
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
    
    public static void main(String[] args) {
    	MulticastReceiver multicastReceiver = new MulticastReceiver();
    	multicastReceiver.start();
    }
}