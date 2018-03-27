package com.service.grpc;

import io.grpc.*;

public class Client
{
    public static void main( String[] args ) throws Exception
    {
      final ManagedChannel channel = ManagedChannelBuilder.forTarget("localhost:8000")
        .usePlaintext(true)
        .build();

      CommunicationServiceGrpc.CommunicationServiceBlockingStub stub = CommunicationServiceGrpc.newBlockingStub(channel);
      CommunicationServiceOuterClass.TransferDataRequest request =
      CommunicationServiceOuterClass.TransferDataRequest.newBuilder()
          .setFromtimestamp("2013/01/01")
          .setTotimestamp("2014/01/01")
          .build();

      CommunicationServiceOuterClass.Ping pingRequest =
      CommunicationServiceOuterClass.Ping.newBuilder()
          .setRequest(true)
          .build();

      CommunicationServiceOuterClass.TransferDataResponse response = stub.communication(request);
      CommunicationServiceOuterClass.Ping pingResponse = stub.pingHandler(pingRequest);

      System.out.println(response);
      System.out.println(pingResponse);

      channel.shutdownNow();
    }
}
