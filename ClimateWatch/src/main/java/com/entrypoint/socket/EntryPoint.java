package com.entrypoint.socket;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.service.heartbeat.HeartBeatApp;

import heartbeat.HeartBeat.Request;
import heartbeat.HeartBeat.Response;
import heartbeat.HeartBeatServiceGrpc;
import heartbeat.HeartBeatServiceGrpc.HeartBeatServiceBlockingStub;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class EntryPoint {

	public static void main(String[] args) throws Exception {
		// PC pc = PC.getInstance();

		// trying to keep main thread alive.
		initializeHeartBeatService();

		Thread.sleep(3000);
		while (true) {
			sendHeartBeat();
			Thread.sleep(10000);
		}
	}

	public static void initializeHeartBeatService() throws IOException, InterruptedException {
		// Start Server on self
		Thread runServer = new Thread() {
			@Override
			public void run() {
				try {
					HeartBeatApp.startServer(9090);
				} catch (Exception e) {

				}
			}
		};

		runServer.start();

	}

	public static void sendHeartBeat() {

		// for (String ip : PC.getInstance().otherNodes) {

		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090).usePlaintext(true).build();
		HeartBeatServiceBlockingStub hsb = HeartBeatServiceGrpc.newBlockingStub(channel);

		Response hBResp = hsb.heartBeat(Request.newBuilder().setBeat(1).build());

		System.out.println(hBResp);

		// }

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

}
