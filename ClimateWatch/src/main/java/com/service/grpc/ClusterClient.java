package com.service.grpc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;
import java.util.UUID;

import com.google.protobuf.ByteString;
import org.codehaus.jackson.map.ObjectMapper;
import com.cmpe275.grpcComm.*;
import io.grpc.*;
import io.grpc.stub.StreamObserver;


public class ClusterClient {
	static ManagedChannel channel;
	static CommunicationServiceGrpc.CommunicationServiceStub  asyncStub;
	static CommunicationServiceGrpc.CommunicationServiceBlockingStub stub;
	static String ip;
	public ClusterClient(String leaderip) {
		ip = leaderip;
		channel = ManagedChannelBuilder.forTarget(ip + ":8080") //ManagedChannelBuilder.forTarget("169.254.79.93:8080")
		        .usePlaintext(true)
		        .build();
		stub = CommunicationServiceGrpc.newBlockingStub(channel);
		asyncStub = CommunicationServiceGrpc.newStub(channel);
	}
	
	public ClusterClient() {
		channel = ManagedChannelBuilder.forTarget("localhost:8080") //ManagedChannelBuilder.forTarget("169.254.79.93:8080")
		        .usePlaintext(true)
		        .build();
		stub = CommunicationServiceGrpc.newBlockingStub(channel);
		asyncStub = CommunicationServiceGrpc.newStub(channel);
	}
	
	public void channelShutDown() {
		channel.shutdownNow();
	}
	
	public void putRequest(Request request) {
		System.out.println("Sending PUT request to node with IP : " + ip  +"\n\n");
	      
	      StreamObserver<Response> responseObserver = new StreamObserver<Response>() {
	    	  @Override
	          public void onNext(Response resp) {
	            System.out.println("Sent data push request to another cluster....");
	            System.out.println(resp.getMsg());
	          }
	    	  
	    	  @Override
	          public void onError(Throwable t) {
	    		  System.out.println("Pushing data request failed. Please try again..!");
	    		  System.out.println(t.getMessage());
	          }
	    	  
	    	  @Override
	          public void onCompleted() {
	    		  System.out.println("Finished Pushing data....");
	    	      //channel.shutdownNow();
	          }
	      };
	      StreamObserver<Request> requestObs = asyncStub.putHandler(responseObserver);
	      
	      try {	    	  
			      requestObs.onNext(request);
			      Thread.sleep(1000);
		    	  requestObs.onCompleted();
		    	  System.out.println("Complete called");
	      }
	      catch(Exception ex) {
	    	  //requestObs.onError(ex);
	    	  //throw ex;
	    	  System.out.println(ex.getMessage());
	      }
	      //requestObs.onCompleted();
	   	  //response = asyncStub.putHandler(new StreamObserver<Request>() 
	      
		
	}

	
	public Response ping() {
			System.out.println("Sending ping request to node with IP : " + ip  +"\n\n");
			PingRequest pingRequest = PingRequest.newBuilder()
			          .setMsg("Sample Ping Request")
			          .build();

	      Request request = Request.newBuilder()
	          .setFromSender("from sender")
	          .setToReceiver("to Receiver")
	          .setPing(pingRequest)
	          .build();
	
	      System.out.println("Sending Ping request....\n\n");
	      Response response = stub.ping(request);
	      System.out.println(response);
	      return response;
	}

	public String getRequest(Request request) {
		Iterator<Response> getResponse = stub.getHandler(request);
		String responseText = "";
		while(getResponse.hasNext()) {
		  String responseData =  getResponse.next().getDatFragment().getData().toStringUtf8();
		  responseText+=responseData;
		  System.out.println(responseData);
		}
		return responseText;
	}

}
