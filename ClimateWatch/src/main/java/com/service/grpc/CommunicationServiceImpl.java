package com.service.grpc;

import io.grpc.stub.StreamObserver;
import io.grpc.*;
import com.google.protobuf.ByteString;

// TODO: should create new class to handle db and remove all these import
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.client.MongoCursor;
import com.cmpe275.grpcComm.CommunicationServiceGrpc;
import com.cmpe275.grpcComm.*;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;

import org.bson.Document;
import java.util.Date;
import java.util.Iterator;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CommunicationServiceImpl extends CommunicationServiceGrpc.CommunicationServiceImplBase {   
	protected static MongoClient mongoClient;
	protected static DBCollection dbCollection; 
	static String[] headers = {"STN", "WeatherDate", "MNET", "SLAT", "SLON", "SELV", "TMPF", "SKNT", "DRCT", "GUST", "PMSL", "ALTI", "DWPF", "RELH", "WTHR", "P24I"};
	static int maxChunkSize = 10;
	
	public void ping(Request request,
          StreamObserver<Response> responseObserver) {
		System.out.println("Received a ping request");
		String successMsg = "Ping Successfull";
		
		
		Response response = Response.newBuilder()
  	          .setCode(StatusCode.Ok)
  	          .setMsg(successMsg)
  	          .build();
		
	     responseObserver.onNext(response);
	     responseObserver.onCompleted();
	}
	
    public void getHandler(Request request, StreamObserver<Response> responseObserver) {

		   System.out.println("Get call Successfull");
		   String responseMsg ;
		   QueryParams params = request.getGetRequest().getQueryParams();
	   	   try {  
			   	Date fromTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(params.getFromUtc());
				Date toTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(params.getToUtc());
				List<DBObject> responseData = new MongoHandler().queryDB(fromTime, toTime, "", "");
		   	   
		   	   if (!responseData.isEmpty()) {
		   		responseMsg = "Data present";
		   		   int fragment = 1;
		   		   int chunkSize = 0;
	   			   String responseChunk ="";
		   		   for(int j =0; j< responseData.size(); j++) {
		   			   System.out.println("Chunk Size: " + String.valueOf(chunkSize));
		   			   DBObject record = responseData.get(j);
		   			    record.removeField("_id");
			   			
			   			String responseRecord = "";
			   			for(int i=0; i<headers.length; i++) {
			   				if(i == headers.length-1)
			   					responseRecord+=record.get(headers[i]) + "\n";
			   				else
			   					responseRecord+=record.get(headers[i]) + "\t";
			   			}
			   			responseChunk += responseRecord;
			   			
			   			if(chunkSize >= maxChunkSize || j>=responseData.size()-1) {
			   				MetaData metadata = MetaData.newBuilder()
				   	   		          .setUuid(String.valueOf(fragment))
				   	   		          .setNumOfFragment(fragment)
				   	   		          .setMediaType(3)
				   	   		          .build();
				   			DatFragment dataFragment = DatFragment.newBuilder()
				   			      		  .setData(ByteString.copyFromUtf8(responseChunk))
				   			      		  .build();
				   			
				   			Response response =Response.newBuilder()
				   	   	          .setMsg(responseMsg)
				   	   	          .setMetaData(metadata)
				   	   	          .setDatFragment(dataFragment)
				   	   	          .build();
				   		  System.out.println(responseChunk);
				   	      responseObserver.onNext(response);
				   	      responseChunk = "";
				   	      chunkSize = 0;
			   		   }
			   		chunkSize++;
		   		   }
			   			
			   	   responseObserver.onCompleted();
		   	   }
		   	   else {
		   		   responseMsg = "Data not present";	   
		      
			       MetaData metadata = MetaData.newBuilder()
			   		          .setUuid("")
			   		          .setNumOfFragment(1)
			   		          .setMediaType(3)
			   		          .build();
			      
			      DatFragment dataFragment = DatFragment.newBuilder()
					      		  .setData(ByteString.copyFromUtf8(""))
					      		  .build();
			
			      Response response = Response.newBuilder()
			   	          .setMsg(responseMsg)
			   	          .setMetaData(metadata)
			   	          .setDatFragment(dataFragment)
			   	          .build();
			
			      responseObserver.onNext(response);
			      responseObserver.onCompleted();
		   	   } 
	    }
	    catch(Exception ex) {
	    	System.out.println(ex.getMessage());
	    }
    }
    
    public StreamObserver<Request> putHandler(final StreamObserver<Response> responseObserver) {
    	System.out.println("Received a PUT request");
	    
	    System.out.println();
	    
	    File f = new File("//mnt//c//");
	    System.out.println("Printing the total space");
	    System.out.println(f.getTotalSpace()/1000000.00 +" Megabytes");
	    
	    return new StreamObserver<Request>() {
		    int chunksReceived = 0;

	        @Override
	        public void onNext(Request request) {
	        	try {
		        	String receivedMessage = request.getPutRequest().getDatFragment().getData().toStringUtf8();
		    	    System.out.println("Received request : "+ receivedMessage);
		    	    chunksReceived++;
		    	    
	    			Thread.sleep(1000);
	        	} catch(Exception ex) {
	        		System.out.println(ex.getMessage());
	        	}
	        }

	        @Override
	        public void onError(Throwable t) {
	          System.out.println(t.getMessage());
	        }

	        @Override
	        public void onCompleted() {
	        	System.out.println("Complete called");
	        	MetaData metadata = MetaData.newBuilder()
				          .setUuid("12345")
				          .setNumOfFragment(1)
				          .setMediaType(3)
				          .build();
		   
	        	DatFragment dataFragment = DatFragment.newBuilder()
				      		  .setData(ByteString.copyFromUtf8(String.valueOf(chunksReceived) + " Message received"))
				      		  .build();
		
	        	Response response = Response.newBuilder()
	        		  .setMsg(String.valueOf(chunksReceived) + " Message received")
			          .setMetaData(metadata)
			          .setDatFragment(dataFragment)
			          .build();
	        	
  			responseObserver.onNext(response);
	        responseObserver.onCompleted();
	        }
	      };
    }
}
