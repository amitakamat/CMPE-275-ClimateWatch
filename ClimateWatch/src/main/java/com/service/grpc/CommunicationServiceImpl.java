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
import com.mongodb.BasicDBObject;
import org.bson.Document;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;

public class CommunicationServiceImpl extends CommunicationServiceGrpc.CommunicationServiceImplBase {   
     
	@Override
     public void messageHandler(CommunicationServiceOuterClass.Request request,
           StreamObserver<CommunicationServiceOuterClass.Response> responseObserver) {
     // CommunicationRequest has toString auto-generated.

       System.out.println("Received a header request");
       String successMsg = "";
       if(request.getPing().toString().length() != 0) {
    	   successMsg = "Ping Successfull";
    	  // System.out.println(request.getPing().toString().length());
       }
       else if(request.getPutRequest().toString().length() != 0) {
    	   successMsg = "Put call Successfull";
    	   //System.out.println(request.getPutRequest().toString().length());
       }
       else
    	   successMsg = "Get call Successfull";
       
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
    	          .setIsSuccess(true)
    	          .setMsg(successMsg)
    	          .setMetaData(metadata)
    	          .setDatFragment(dataFragment)
    	          .build();

       responseObserver.onNext(response);
       responseObserver.onCompleted();
     }
}
