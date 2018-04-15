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
import com.service.grpc.CommunicationServiceOuterClass.UploadStatusCode;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;

import org.bson.Document;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CommunicationServiceImpl extends CommunicationServiceGrpc.CommunicationServiceImplBase {   
	protected static MongoClient mongoClient;
	protected static DBCollection dbCollection; 
	
	public void ping(CommunicationServiceOuterClass.Request request,
          StreamObserver<CommunicationServiceOuterClass.Response> responseObserver) {
		System.out.println("Received a ping request");
		String successMsg = "Ping Successfull";
		
		
		CommunicationServiceOuterClass.Response response = CommunicationServiceOuterClass.Response.newBuilder()
  	          .setCode(UploadStatusCode.Ok)
  	          .setMsg(successMsg)
  	          .build();
		
	     responseObserver.onNext(response);
	     responseObserver.onCompleted();
	}
	
    public void putHandler(StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Request> request,
           StreamObserver<CommunicationServiceOuterClass.Response> responseObserver) {
     // CommunicationRequest has toString auto-generated.

		   System.out.println("Received a PUT request");
    	   String successMsg = "Put call Successfull";
    	   //System.out.println(request.getPutRequest().toString().length());
       /*else {
    	   successMsg = "Get call Successfull";
    	   CommunicationServiceOuterClass.QueryParams params = request.getGetRequest().getQueryParams();
    	   if (queryDB(params.getFromUtc(), params.getToUtc()))
    		   successMsg = "Data present";
    	   else
    		   successMsg = "Data not present";
       }*/	   
       
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

       CommunicationServiceOuterClass.Response response = CommunicationServiceOuterClass.Response.newBuilder()
    	          .setMetaData(metadata)
    	          .setDatFragment(dataFragment)
    	          .build();

       responseObserver.onNext(response);
       responseObserver.onCompleted();
     }
	
    public void getHandler(CommunicationServiceOuterClass.Request request,
          StreamObserver<CommunicationServiceOuterClass.Response> responseObserver) {

		   System.out.println("Get call Successfull");
		   String responseMsg ;
   	   CommunicationServiceOuterClass.QueryParams params = request.getGetRequest().getQueryParams();
   	 try {  
	   	Date fromTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(params.getFromUtc());
		Date toTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(params.getToUtc());
		List<DBObject> responseData = new MongoHandler().queryDB(fromTime, toTime, "", "");
   	   
   	   if (!responseData.isEmpty()) {
   		responseMsg = "Data present";
   		   int fragment = 1;
   		   
   		   for(DBObject record: responseData) {
	   			CommunicationServiceOuterClass.MetaData metadata =
	   	   		      CommunicationServiceOuterClass.MetaData.newBuilder()
	   	   		          .setUuid(String.valueOf(record.get("_id")))
	   	   		          .setNumOfFragment(fragment)
	   	   		          .setMediaType(3)
	   	   		          .build();
   			    record.removeField("_id");
	   			fragment++;
	   			
	   			CommunicationServiceOuterClass.DatFragment dataFragment =
	   			      CommunicationServiceOuterClass.DatFragment.newBuilder()
	   			      		  .setData(ByteString.copyFromUtf8(String.valueOf(record)))
	   			      		  .build();
	   			
	   			CommunicationServiceOuterClass.Response response = CommunicationServiceOuterClass.Response.newBuilder()
	   	   	          .setMsg(responseMsg)
	   	   	          .setMetaData(metadata)
	   	   	          .setDatFragment(dataFragment)
	   	   	          .build();
	
	   	      responseObserver.onNext(response);
   		   }
	   	   responseObserver.onCompleted();
   	   }
   	   else {
   		   responseMsg = "Data not present";	   
      
	      CommunicationServiceOuterClass.MetaData metadata =
	   		      CommunicationServiceOuterClass.MetaData.newBuilder()
	   		          .setUuid("")
	   		          .setNumOfFragment(1)
	   		          .setMediaType(3)
	   		          .build();
	      
	      CommunicationServiceOuterClass.DatFragment dataFragment =
			      CommunicationServiceOuterClass.DatFragment.newBuilder()
			      		  .setData(ByteString.copyFromUtf8(""))
			      		  .build();
	
	      CommunicationServiceOuterClass.Response response = CommunicationServiceOuterClass.Response.newBuilder()
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
}
