package com.service.grpc;

import java.util.Iterator;

import com.google.protobuf.ByteString;

import io.grpc.*;
import io.grpc.stub.StreamObserver;

public class Client
{
    public static void main( String[] args ) throws Exception
    {
      final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:9000")
        .usePlaintext(true)
        .build();
      
      
      CommunicationServiceOuterClass.PingRequest pingRequest =
      CommunicationServiceOuterClass.PingRequest.newBuilder()
          .setMsg("Sample Ping Request")
          .build();

      CommunicationServiceGrpc.CommunicationServiceBlockingStub stub = CommunicationServiceGrpc.newBlockingStub(channel);
      CommunicationServiceOuterClass.Request request =
      CommunicationServiceOuterClass.Request.newBuilder()
          .setFromSender("from sender")
          .setToReceiver("to Receiver")
          .setPing(pingRequest)
          .build();

      CommunicationServiceOuterClass.Response response = stub.ping(request);
      System.out.println(response);
     
      CommunicationServiceOuterClass.MetaData metadata =
		      CommunicationServiceOuterClass.MetaData.newBuilder()
		          .setUuid("12345")
		          .setNumOfFragment(1)
		          .setMediaType(3)
		          .build();
   
   CommunicationServiceOuterClass.DatFragment dataFragment =
		      CommunicationServiceOuterClass.DatFragment.newBuilder()
		      		  .setData(ByteString.copyFromUtf8("sample raw bytes"))
		      		  .build();
   
   /*CommunicationServiceOuterClass.PutRequest putRequest =
		      CommunicationServiceOuterClass.PutRequest.newBuilder()
		      		  .setDatFragment(dataFragment)
		      		  .setMetaData(metadata)
		      		  .build();
   
   request = CommunicationServiceOuterClass.Request.newBuilder()
		          .setFromSender("from sender")
		          .setToReceiver("to Receiver")
		          .setPutRequest(putRequest)
		          .build();
   	  response = stub.putHandler(request);

      System.out.println(response);*/
      
      CommunicationServiceOuterClass.QueryParams queryParams =
		      CommunicationServiceOuterClass.QueryParams.newBuilder()
		      		  .setFromUtc("2018/03/21 01:00:00")
		      		  .setToUtc("2018/03/21 22:20:00")
		      		//.setFromUtc("2017/01/01 00:00:00") *** Test for data not present
		      		// .setToUtc("2018/01/01 00:00:00")
		      		  .build();
      
      
      CommunicationServiceOuterClass.GetRequest getRequest =
		      CommunicationServiceOuterClass.GetRequest.newBuilder()
		      		  .setMetaData(metadata)
		      		  .setQueryParams(queryParams)
		      		  .build();
      request = CommunicationServiceOuterClass.Request.newBuilder()
	          .setFromSender("from sender")
	          .setToReceiver("to Receiver")
	          .setGetRequest(getRequest)
	          .build();
      
      Iterator<CommunicationServiceOuterClass.Response> getResponse = stub.getHandler(request);
      while(getResponse.hasNext()) {
    	  System.out.println(getResponse.next());
      }


      channel.shutdownNow();
    }
}
