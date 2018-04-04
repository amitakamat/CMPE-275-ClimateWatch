package com.service.grpc;

import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.7.0)",
    comments = "Source: CommunicationService.proto")
public final class CommunicationServiceGrpc {

  private CommunicationServiceGrpc() {}

  public static final String SERVICE_NAME = "com.service.grpc.CommunicationService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.service.grpc.CommunicationServiceOuterClass.Request,
      com.service.grpc.CommunicationServiceOuterClass.Response> METHOD_PUT_HANDLER =
      io.grpc.MethodDescriptor.<com.service.grpc.CommunicationServiceOuterClass.Request, com.service.grpc.CommunicationServiceOuterClass.Response>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.CLIENT_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "com.service.grpc.CommunicationService", "PutHandler"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.service.grpc.CommunicationServiceOuterClass.Request.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.service.grpc.CommunicationServiceOuterClass.Response.getDefaultInstance()))
          .setSchemaDescriptor(new CommunicationServiceMethodDescriptorSupplier("PutHandler"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.service.grpc.CommunicationServiceOuterClass.Request,
      com.service.grpc.CommunicationServiceOuterClass.Response> METHOD_GET_HANDLER =
      io.grpc.MethodDescriptor.<com.service.grpc.CommunicationServiceOuterClass.Request, com.service.grpc.CommunicationServiceOuterClass.Response>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.SERVER_STREAMING)
          .setFullMethodName(generateFullMethodName(
              "com.service.grpc.CommunicationService", "GetHandler"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.service.grpc.CommunicationServiceOuterClass.Request.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.service.grpc.CommunicationServiceOuterClass.Response.getDefaultInstance()))
          .setSchemaDescriptor(new CommunicationServiceMethodDescriptorSupplier("GetHandler"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.service.grpc.CommunicationServiceOuterClass.Request,
      com.service.grpc.CommunicationServiceOuterClass.Response> METHOD_PING =
      io.grpc.MethodDescriptor.<com.service.grpc.CommunicationServiceOuterClass.Request, com.service.grpc.CommunicationServiceOuterClass.Response>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "com.service.grpc.CommunicationService", "Ping"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.service.grpc.CommunicationServiceOuterClass.Request.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.service.grpc.CommunicationServiceOuterClass.Response.getDefaultInstance()))
          .setSchemaDescriptor(new CommunicationServiceMethodDescriptorSupplier("Ping"))
          .build();

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CommunicationServiceStub newStub(io.grpc.Channel channel) {
    return new CommunicationServiceStub(channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CommunicationServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    return new CommunicationServiceBlockingStub(channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CommunicationServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    return new CommunicationServiceFutureStub(channel);
  }

  /**
   */
  public static abstract class CommunicationServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Request> putHandler(
        io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Response> responseObserver) {
      return asyncUnimplementedStreamingCall(METHOD_PUT_HANDLER, responseObserver);
    }

    /**
     */
    public void getHandler(com.service.grpc.CommunicationServiceOuterClass.Request request,
        io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Response> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_GET_HANDLER, responseObserver);
    }

    /**
     */
    public void ping(com.service.grpc.CommunicationServiceOuterClass.Request request,
        io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Response> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_PING, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_PUT_HANDLER,
            asyncClientStreamingCall(
              new MethodHandlers<
                com.service.grpc.CommunicationServiceOuterClass.Request,
                com.service.grpc.CommunicationServiceOuterClass.Response>(
                  this, METHODID_PUT_HANDLER)))
          .addMethod(
            METHOD_GET_HANDLER,
            asyncServerStreamingCall(
              new MethodHandlers<
                com.service.grpc.CommunicationServiceOuterClass.Request,
                com.service.grpc.CommunicationServiceOuterClass.Response>(
                  this, METHODID_GET_HANDLER)))
          .addMethod(
            METHOD_PING,
            asyncUnaryCall(
              new MethodHandlers<
                com.service.grpc.CommunicationServiceOuterClass.Request,
                com.service.grpc.CommunicationServiceOuterClass.Response>(
                  this, METHODID_PING)))
          .build();
    }
  }

  /**
   */
  public static final class CommunicationServiceStub extends io.grpc.stub.AbstractStub<CommunicationServiceStub> {
    private CommunicationServiceStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CommunicationServiceStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CommunicationServiceStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CommunicationServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Request> putHandler(
        io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Response> responseObserver) {
      return asyncClientStreamingCall(
          getChannel().newCall(METHOD_PUT_HANDLER, getCallOptions()), responseObserver);
    }

    /**
     */
    public void getHandler(com.service.grpc.CommunicationServiceOuterClass.Request request,
        io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Response> responseObserver) {
      asyncServerStreamingCall(
          getChannel().newCall(METHOD_GET_HANDLER, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void ping(com.service.grpc.CommunicationServiceOuterClass.Request request,
        io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Response> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_PING, getCallOptions()), request, responseObserver);
    }
  }

  /**
   */
  public static final class CommunicationServiceBlockingStub extends io.grpc.stub.AbstractStub<CommunicationServiceBlockingStub> {
    private CommunicationServiceBlockingStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CommunicationServiceBlockingStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CommunicationServiceBlockingStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CommunicationServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public java.util.Iterator<com.service.grpc.CommunicationServiceOuterClass.Response> getHandler(
        com.service.grpc.CommunicationServiceOuterClass.Request request) {
      return blockingServerStreamingCall(
          getChannel(), METHOD_GET_HANDLER, getCallOptions(), request);
    }

    /**
     */
    public com.service.grpc.CommunicationServiceOuterClass.Response ping(com.service.grpc.CommunicationServiceOuterClass.Request request) {
      return blockingUnaryCall(
          getChannel(), METHOD_PING, getCallOptions(), request);
    }
  }

  /**
   */
  public static final class CommunicationServiceFutureStub extends io.grpc.stub.AbstractStub<CommunicationServiceFutureStub> {
    private CommunicationServiceFutureStub(io.grpc.Channel channel) {
      super(channel);
    }

    private CommunicationServiceFutureStub(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CommunicationServiceFutureStub build(io.grpc.Channel channel,
        io.grpc.CallOptions callOptions) {
      return new CommunicationServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.service.grpc.CommunicationServiceOuterClass.Response> ping(
        com.service.grpc.CommunicationServiceOuterClass.Request request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_PING, getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_HANDLER = 0;
  private static final int METHODID_PING = 1;
  private static final int METHODID_PUT_HANDLER = 2;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final CommunicationServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(CommunicationServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_HANDLER:
          serviceImpl.getHandler((com.service.grpc.CommunicationServiceOuterClass.Request) request,
              (io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Response>) responseObserver);
          break;
        case METHODID_PING:
          serviceImpl.ping((com.service.grpc.CommunicationServiceOuterClass.Request) request,
              (io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Response>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_PUT_HANDLER:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.putHandler(
              (io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Response>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static abstract class CommunicationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CommunicationServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return com.service.grpc.CommunicationServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CommunicationService");
    }
  }

  private static final class CommunicationServiceFileDescriptorSupplier
      extends CommunicationServiceBaseDescriptorSupplier {
    CommunicationServiceFileDescriptorSupplier() {}
  }

  private static final class CommunicationServiceMethodDescriptorSupplier
      extends CommunicationServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final String methodName;

    CommunicationServiceMethodDescriptorSupplier(String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CommunicationServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CommunicationServiceFileDescriptorSupplier())
              .addMethod(METHOD_PUT_HANDLER)
              .addMethod(METHOD_GET_HANDLER)
              .addMethod(METHOD_PING)
              .build();
        }
      }
    }
    return result;
  }
}
