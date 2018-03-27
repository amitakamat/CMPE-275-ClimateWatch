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
  public void communication(CommunicationServiceOuterClass.TransferDataRequest request,
        StreamObserver<CommunicationServiceOuterClass.TransferDataResponse> responseObserver) {

    System.out.println("Data request from " + request.getFromtimestamp() + " to " + request.getTotimestamp());

    CommunicationServiceOuterClass.TransferDataResponse response = CommunicationServiceOuterClass.TransferDataResponse.newBuilder()
      .setCommunication("Communication setup successfully, " + request.getFromtimestamp() + " " + request.getTotimestamp())
      .setData("Yes")
      .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

    @Override
    public void pingHandler(CommunicationServiceOuterClass.Ping request,
          StreamObserver<CommunicationServiceOuterClass.Ping> responseObserver) {

            System.out.println("Received a ping request");

            CommunicationServiceOuterClass.Ping response =
            CommunicationServiceOuterClass.Ping.newBuilder()
                .setRespond(true)
                .build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();
    }
   
     @Override
     public void messageHandler(CommunicationServiceOuterClass.Header request,
           StreamObserver<CommunicationServiceOuterClass.Header> responseObserver) {
     // CommunicationRequest has toString auto-generated.

       System.out.println("Received a header request");
       CommunicationServiceOuterClass.Ping pingResponse =
    		      CommunicationServiceOuterClass.Ping.newBuilder()
    		          .setRequest(true)
    		          .build();

       CommunicationServiceOuterClass.Header response = CommunicationServiceOuterClass.Header.newBuilder()
    	          .setFromIp("Sender IP")
    	          .setToIp("Receiver IP")
    	          .setOriginalIp("Original IP")
    	          .setMaxHop(5)
    	          .setPing(pingResponse)
    	          .setToken("1234567890")
    	          .build();

       responseObserver.onNext(response);
       responseObserver.onCompleted();
     }
}
