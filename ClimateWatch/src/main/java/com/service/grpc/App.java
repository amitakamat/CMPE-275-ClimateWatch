package com.service.grpc;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpServer;

import io.grpc.*;

public class App 
{
	private static final Logger logger = Logger.getLogger(App.class.getName());

	private Server server;
	
	private void start() throws IOException {
	    /* The port on which the server should run */
	    int grpcport = 8080;
	   // int httpport = 8000;
	   // server = ServerBuilder.forPort(port).addService((BindableService) new CommunicationServiceImpl()).build();
	    
	    server = ServerBuilder.forPort(grpcport)
	        	.addService(new CommunicationServiceImpl())
	        	.build();
	    server.start();
	     
	    /*HttpServer httpserver = HttpServer.create(new InetSocketAddress(httpport), 0);
	    httpserver.createContext("/v1/getbydate", new DataHandler());
	    httpserver.setExecutor(null); 
	    httpserver.start();*/
	    
	    logger.info("GRPC Server started, listening on " + grpcport);
	   // logger.info("GRPC Server started, listening on " + httpport);
	    
	    Runtime.getRuntime().addShutdownHook(new Thread() {
	      @Override
	      public void run() {
	        // Use stderr here since the logger may have been reset by its JVM shutdown hook.
	        System.err.println("*** shutting down gRPC server since JVM is shutting down");
	        App.this.stop();
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
	
    public static void main( String[] args ) throws IOException, InterruptedException{
    	final App appServer = new App();

      	// Start the server
    	appServer.start();

      	// Server threads are running in the background.
      	System.out.println("Server started...");
      	// Don't exit the main thread. Wait until server is terminated.
      	appServer.blockUntilShutdown();
    }
}
