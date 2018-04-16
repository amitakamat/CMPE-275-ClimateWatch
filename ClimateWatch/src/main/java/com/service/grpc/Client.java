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

public class Client
{
    public static void main( String[] args ) throws Exception
    {
      final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8080") //ManagedChannelBuilder.forTarget("169.254.79.93:8080")
        .usePlaintext(true)
        .build();
      
      System.out.println("Client started....\n\n");
      PingRequest pingRequest =PingRequest.newBuilder()
          .setMsg("Sample Ping Request")
          .build();

      CommunicationServiceGrpc.CommunicationServiceBlockingStub stub = CommunicationServiceGrpc.newBlockingStub(channel);
      CommunicationServiceGrpc.CommunicationServiceStub  asyncStub = CommunicationServiceGrpc.newStub(channel);
      Request request =Request.newBuilder()
          .setFromSender("from sender")
          .setToReceiver("to Receiver")
          .setPing(pingRequest)
          .build();

      System.out.println("Sending Ping request....\n\n");
      Response response = stub.ping(request);
      System.out.println(response);
      
      
      
      
      System.out.println("Sending GET request....\n\n"); 
	QueryParams queryParams = QueryParams.newBuilder()
	      		  .setFromUtc("2018-03-21 01:00:00")
	      		  .setToUtc("2018-03-21 01:20:00")
	      		//.setFromUtc("2017/01/01 00:00:00") *** Test for data not present
	      		// .setToUtc("2018/01/01 00:00:00")
	      		  .build();
	
	MetaData metadata = MetaData.newBuilder()
	      .setUuid("12365")
	      .setNumOfFragment(1)
	      .setMediaType(1)
	      .build();
	
	GetRequest getRequest = GetRequest.newBuilder()
	      		  .setMetaData(metadata)
	      		  .setQueryParams(queryParams)
	      		  .build();
	request = Request.newBuilder()
	      .setFromSender("from sender")
	      .setToReceiver("to Receiver")
	      .setGetRequest(getRequest)
	      .build();
	//ObjectMapper mapperObj = new ObjectMapper();
	
	Iterator<Response> getResponse = stub.getHandler(request);
	while(getResponse.hasNext()) {
	  //System.out.println(String.valueOf(getResponse.next().getDatFragment()).replace("\'", ""))
	 //System.out.println(mapperObj.writeValueAsString(getResponse.next()));
	  String responseData =  getResponse.next().getDatFragment().getData().toStringUtf8();
	  System.out.println(responseData);
	}

      
      
      
	System.out.println("Sending PUT request....\n\n");
      
      StreamObserver<Response> responseObserver = new StreamObserver<Response>() {
    	  @Override
          public void onNext(Response resp) {
            System.out.println("Sent data push request. Checking for disk Space....");
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
    	      channel.shutdownNow();
          }
      };
      StreamObserver<Request> requestObs = asyncStub.putHandler(responseObserver);
      
      try {
    	  String[] req = {"This is message request 1", "This is message request 2", "This is message request 3"};
    	  
    	  for(int i=0; i< req.length; i++) {
    		  MetaData pmetadata = MetaData.newBuilder()
				          .setUuid(UUID.randomUUID().toString())
				          .setNumOfFragment(1)
				          .setMediaType(1)
				          .build();
		   
		      DatFragment dataFragment = DatFragment.newBuilder()
				      		  .setData(ByteString.copyFromUtf8(req[i]))
				      		  .build();
		   
		      PutRequest putRequest = PutRequest.newBuilder()
				      		  .setDatFragment(dataFragment)
				      		  .setMetaData(pmetadata)
				      		  .build();
		   
		      request = Request.newBuilder()
				          .setFromSender("from sender")
				          .setToReceiver("to Receiver")
				          .setPutRequest(putRequest)
				          .build();
    	  
		      requestObs.onNext(request);
		      Thread.sleep(1000);
    	  }
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
}
