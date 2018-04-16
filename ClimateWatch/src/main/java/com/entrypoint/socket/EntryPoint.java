package com.entrypoint.socket;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class EntryPoint {

	public static void main(String[] args) throws Exception 
	{
		PC pc = new PC(1, getIP());
		
		while(true)
		{
			Thread.sleep(1000);
		}
	}

	public static String getIP()
	{
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

}
