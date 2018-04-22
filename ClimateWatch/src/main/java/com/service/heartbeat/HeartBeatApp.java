package com.service.heartbeat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

import io.grpc.*;

public class HeartBeatApp 
{
	private static final Logger logger = Logger.getLogger(HeartBeatApp.class.getName());

	private Server server;
	public static int grpcport = 9090;
	
	private void start() throws IOException {
	    
	    
	    server = ServerBuilder.forPort(grpcport)
	        	.addService(new HeartBeatServiceImpl())
	        	.build();
	    server.start();
	    
	    logger.info("GRPC Server started, listening on " + grpcport);
	    
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	      @Override
	      public void run() {
	        System.err.println("*** shutting down gRPC server since JVM is shutting down");
	        HeartBeatApp.this.stop();
	        System.err.println("*** server shut down");
	      }
	    });
	  }

	  private void stop() {
	    if (server != null) {
	      server.shutdown();
	    }
	  }

	  /**
	   * Await termination on the main thread since the grpc library uses daemon threads.
	   */
	  private void blockUntilShutdown() throws InterruptedException {
	    if (server != null) {
	      server.awaitTermination();
	    }
	  }
	
    public static void startServer(int port) throws IOException, InterruptedException{
    	grpcport = port;
    	final HeartBeatApp appServer = new HeartBeatApp();
    	appServer.start();
      	// Server threads are running in the background.
      	System.out.println("HeartBeat Server started...");
      	// Don't exit the main thread. Wait until server is terminated.
      	appServer.blockUntilShutdown();
    }
}
