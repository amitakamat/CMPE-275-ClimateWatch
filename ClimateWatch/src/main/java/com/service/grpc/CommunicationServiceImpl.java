package com.service.grpc;

import io.grpc.stub.StreamObserver;
import io.grpc.*;
import com.google.protobuf.ByteString;

// TODO: should create new class to handle db and remove all these import
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.QueryBuilder;
import com.mongodb.client.MongoCursor;
import com.service.grpc.CommunicationServiceOuterClass.StatusCode;
import com.mongodb.BasicDBObject;
import org.bson.Document;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class CommunicationServiceImpl extends CommunicationServiceGrpc.CommunicationServiceImplBase {   
	protected static MongoClient mongoClient;
	protected static DBCollection dbCollection; 
	
	public CommunicationServiceImpl() {
		if (mongoClient == null) {
			try {
				//mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
				mongoClient = new MongoClient("localhost", 27017);
				DB messageDB = mongoClient.getDB("messagesDB");
				dbCollection = messageDB.getCollection("data");
			
			}
			
			
			catch(Exception e) {
				System.out.println(e.getMessage());
			}
		}
	 }
	
	public void ping(CommunicationServiceOuterClass.Request request,
          StreamObserver<CommunicationServiceOuterClass.Response> responseObserver) {
		System.out.println("Received a ping request");
		String successMsg = "Ping Successfull";
		
		
		CommunicationServiceOuterClass.Response response = CommunicationServiceOuterClass.Response.newBuilder()
  	          .setCode(StatusCode.Ok)
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
    // CommunicationRequest has toString auto-generated.

		   System.out.println("Get call Successfull");
   	   String successMsg = "Get call Successfull";
   	   //System.out.println(request.getPutRequest().toString().length());
   	   CommunicationServiceOuterClass.QueryParams params = request.getGetRequest().getQueryParams();
   	   if (queryDB(params.getFromUtc(), params.getToUtc()))
   		   successMsg = "Data present";
   	   else
   		   successMsg = "Data not present";	   
      
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
   	          .setMsg(successMsg)
   	          .setMetaData(metadata)
   	          .setDatFragment(dataFragment)
   	          .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();
    }
    
	/**
	 * a message was received from the server. Here we extract the from and to date time,
	 * query the database to fetch records based on the query and display it.
	 * 
	 * @param from, to
	 *            The message request parameters received
	 */
	public boolean queryDB(String from, String to) {
		boolean found = false;
		try {
		Date fromTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(from);
		Date toTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(to);
		
		BasicDBObject query = new BasicDBObject("WeatherDate", new BasicDBObject("$gte", fromTime).append("$lte", toTime));
		DBCursor cursor = dbCollection.find(query);
		while(cursor.hasNext()) {
			found = true;
	        System.out.println(cursor.next());
	    }
		return found;
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
			return false;
		}

	}
}
