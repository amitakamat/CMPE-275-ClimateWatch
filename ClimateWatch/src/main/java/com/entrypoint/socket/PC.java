package com.entrypoint.socket;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class EntryPoint {

	private static Enumeration<NetworkInterface> networkInterfaces;

	public static void main(String[] args) {

		// get ip
		// TODO Anusha, check if this works on mac. 		
		PC pc = new PC(1, getIP());

		/*
		 * class test {
		 * 
		 * } // convert to a node test test2 = new test();
		 */

	}

	public static String getIP() {
		String retString = null;
		try {
			InetAddress ipAddr = InetAddress.getLocalHost();
			System.out.println(ipAddr.getHostAddress());
			retString = ipAddr.toString();
		} catch (UnknownHostException ex) {
			ex.printStackTrace();
		}
		return retString;

	}

	byte[] buf = new byte[256];

	public void run() {
		try {
			MulticastSocket socket = new MulticastSocket(4446);

			InetAddress group = InetAddress.getByName("230.0.0.0");
			socket.joinGroup(group);
			while (true) {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);
				System.out.println("receiving packets");
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println("received Packet " + received);
				if ("end".equals(received)) {
					break;
				}
				System.out.println("done");
			}
			socket.leaveGroup(group);
			socket.close();
		} catch (Exception e) {

		}
	}

}
