package com.service.grpc;

import java.io.IOException;
import java.util.logging.Logger;

import io.grpc.*;

public class App 
{
	private static final Logger logger = Logger.getLogger(App.class.getName());

	private Server server;
	
	private void start() throws IOException {
	    /* The port on which the server should run */
	    int port = 8000;
	    //server = ServerBuilder.forPort(port).addService((BindableService) new CommunicationServiceImpl()).build();
	    
	    server = ServerBuilder.forPort(port)
	        	.addService(new CommunicationServiceImpl())
	        	.build().start();
	    
	    logger.info("Server started, listening on " + port);
	    
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
