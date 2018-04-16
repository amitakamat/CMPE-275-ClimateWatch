package com.service.grpc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.TimeZone;

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
      Request request =Request.newBuilder()
          .setFromSender("from sender")
          .setToReceiver("to Receiver")
          .setPing(pingRequest)
          .build();

      Response response = stub.ping(request);
      System.out.println(response);
     
      MetaData metadata = MetaData.newBuilder()
		          .setUuid("12345")
		          .setNumOfFragment(1)
		          .setMediaType(3)
		          .build();
   
      DatFragment dataFragment = DatFragment.newBuilder()
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
      
      QueryParams queryParams = QueryParams.newBuilder()
		      		  .setFromUtc("2018-03-21 01:00:00")
		      		  .setToUtc("2018-03-21 01:20:00")
		      		//.setFromUtc("2017/01/01 00:00:00") *** Test for data not present
		      		// .setToUtc("2018/01/01 00:00:00")
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


      channel.shutdownNow();
    }
}
