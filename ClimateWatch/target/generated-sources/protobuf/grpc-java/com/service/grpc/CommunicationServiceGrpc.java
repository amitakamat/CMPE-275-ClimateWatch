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
 * <pre>
 *=====================================================
 *GRPC services
 *=====================================================
 * Defining a Service, a Service can have multiple RPC operations
 * </pre>
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.7.0)",
    comments = "Source: CommunicationService.proto")
public final class CommunicationServiceGrpc {

  private CommunicationServiceGrpc() {}

  public static final String SERVICE_NAME = "com.service.grpc.CommunicationService";

  // Static method descriptors that strictly reflect the proto.
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.service.grpc.CommunicationServiceOuterClass.TransferDataRequest,
      com.service.grpc.CommunicationServiceOuterClass.TransferDataResponse> METHOD_COMMUNICATION =
      io.grpc.MethodDescriptor.<com.service.grpc.CommunicationServiceOuterClass.TransferDataRequest, com.service.grpc.CommunicationServiceOuterClass.TransferDataResponse>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "com.service.grpc.CommunicationService", "communication"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.service.grpc.CommunicationServiceOuterClass.TransferDataRequest.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.service.grpc.CommunicationServiceOuterClass.TransferDataResponse.getDefaultInstance()))
          .setSchemaDescriptor(new CommunicationServiceMethodDescriptorSupplier("communication"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.service.grpc.CommunicationServiceOuterClass.Ping,
      com.service.grpc.CommunicationServiceOuterClass.Ping> METHOD_PING_HANDLER =
      io.grpc.MethodDescriptor.<com.service.grpc.CommunicationServiceOuterClass.Ping, com.service.grpc.CommunicationServiceOuterClass.Ping>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "com.service.grpc.CommunicationService", "pingHandler"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.service.grpc.CommunicationServiceOuterClass.Ping.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.service.grpc.CommunicationServiceOuterClass.Ping.getDefaultInstance()))
          .setSchemaDescriptor(new CommunicationServiceMethodDescriptorSupplier("pingHandler"))
          .build();
  @io.grpc.ExperimentalApi("https://github.com/grpc/grpc-java/issues/1901")
  public static final io.grpc.MethodDescriptor<com.service.grpc.CommunicationServiceOuterClass.Header,
      com.service.grpc.CommunicationServiceOuterClass.Header> METHOD_MESSAGE_HANDLER =
      io.grpc.MethodDescriptor.<com.service.grpc.CommunicationServiceOuterClass.Header, com.service.grpc.CommunicationServiceOuterClass.Header>newBuilder()
          .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
          .setFullMethodName(generateFullMethodName(
              "com.service.grpc.CommunicationService", "messageHandler"))
          .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.service.grpc.CommunicationServiceOuterClass.Header.getDefaultInstance()))
          .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
              com.service.grpc.CommunicationServiceOuterClass.Header.getDefaultInstance()))
          .setSchemaDescriptor(new CommunicationServiceMethodDescriptorSupplier("messageHandler"))
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
   * <pre>
   *=====================================================
   *GRPC services
   *=====================================================
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
   */
  public static abstract class CommunicationServiceImplBase implements io.grpc.BindableService {

    /**
     * <pre>
     * Define a RPC operation
     * </pre>
     */
    public void communication(com.service.grpc.CommunicationServiceOuterClass.TransferDataRequest request,
        io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.TransferDataResponse> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_COMMUNICATION, responseObserver);
    }

    /**
     */
    public void pingHandler(com.service.grpc.CommunicationServiceOuterClass.Ping request,
        io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Ping> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_PING_HANDLER, responseObserver);
    }

    /**
     */
    public void messageHandler(com.service.grpc.CommunicationServiceOuterClass.Header request,
        io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Header> responseObserver) {
      asyncUnimplementedUnaryCall(METHOD_MESSAGE_HANDLER, responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            METHOD_COMMUNICATION,
            asyncUnaryCall(
              new MethodHandlers<
                com.service.grpc.CommunicationServiceOuterClass.TransferDataRequest,
                com.service.grpc.CommunicationServiceOuterClass.TransferDataResponse>(
                  this, METHODID_COMMUNICATION)))
          .addMethod(
            METHOD_PING_HANDLER,
            asyncUnaryCall(
              new MethodHandlers<
                com.service.grpc.CommunicationServiceOuterClass.Ping,
                com.service.grpc.CommunicationServiceOuterClass.Ping>(
                  this, METHODID_PING_HANDLER)))
          .addMethod(
            METHOD_MESSAGE_HANDLER,
            asyncUnaryCall(
              new MethodHandlers<
                com.service.grpc.CommunicationServiceOuterClass.Header,
                com.service.grpc.CommunicationServiceOuterClass.Header>(
                  this, METHODID_MESSAGE_HANDLER)))
          .build();
    }
  }

  /**
   * <pre>
   *=====================================================
   *GRPC services
   *=====================================================
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
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
     * <pre>
     * Define a RPC operation
     * </pre>
     */
    public void communication(com.service.grpc.CommunicationServiceOuterClass.TransferDataRequest request,
        io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.TransferDataResponse> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_COMMUNICATION, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void pingHandler(com.service.grpc.CommunicationServiceOuterClass.Ping request,
        io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Ping> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_PING_HANDLER, getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void messageHandler(com.service.grpc.CommunicationServiceOuterClass.Header request,
        io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Header> responseObserver) {
      asyncUnaryCall(
          getChannel().newCall(METHOD_MESSAGE_HANDLER, getCallOptions()), request, responseObserver);
    }
  }

  /**
   * <pre>
   *=====================================================
   *GRPC services
   *=====================================================
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
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
     * <pre>
     * Define a RPC operation
     * </pre>
     */
    public com.service.grpc.CommunicationServiceOuterClass.TransferDataResponse communication(com.service.grpc.CommunicationServiceOuterClass.TransferDataRequest request) {
      return blockingUnaryCall(
          getChannel(), METHOD_COMMUNICATION, getCallOptions(), request);
    }

    /**
     */
    public com.service.grpc.CommunicationServiceOuterClass.Ping pingHandler(com.service.grpc.CommunicationServiceOuterClass.Ping request) {
      return blockingUnaryCall(
          getChannel(), METHOD_PING_HANDLER, getCallOptions(), request);
    }

    /**
     */
    public com.service.grpc.CommunicationServiceOuterClass.Header messageHandler(com.service.grpc.CommunicationServiceOuterClass.Header request) {
      return blockingUnaryCall(
          getChannel(), METHOD_MESSAGE_HANDLER, getCallOptions(), request);
    }
  }

  /**
   * <pre>
   *=====================================================
   *GRPC services
   *=====================================================
   * Defining a Service, a Service can have multiple RPC operations
   * </pre>
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
     * <pre>
     * Define a RPC operation
     * </pre>
     */
    public com.google.common.util.concurrent.ListenableFuture<com.service.grpc.CommunicationServiceOuterClass.TransferDataResponse> communication(
        com.service.grpc.CommunicationServiceOuterClass.TransferDataRequest request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_COMMUNICATION, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.service.grpc.CommunicationServiceOuterClass.Ping> pingHandler(
        com.service.grpc.CommunicationServiceOuterClass.Ping request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_PING_HANDLER, getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<com.service.grpc.CommunicationServiceOuterClass.Header> messageHandler(
        com.service.grpc.CommunicationServiceOuterClass.Header request) {
      return futureUnaryCall(
          getChannel().newCall(METHOD_MESSAGE_HANDLER, getCallOptions()), request);
    }
  }

  private static final int METHODID_COMMUNICATION = 0;
  private static final int METHODID_PING_HANDLER = 1;
  private static final int METHODID_MESSAGE_HANDLER = 2;

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
        case METHODID_COMMUNICATION:
          serviceImpl.communication((com.service.grpc.CommunicationServiceOuterClass.TransferDataRequest) request,
              (io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.TransferDataResponse>) responseObserver);
          break;
        case METHODID_PING_HANDLER:
          serviceImpl.pingHandler((com.service.grpc.CommunicationServiceOuterClass.Ping) request,
              (io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Ping>) responseObserver);
          break;
        case METHODID_MESSAGE_HANDLER:
          serviceImpl.messageHandler((com.service.grpc.CommunicationServiceOuterClass.Header) request,
              (io.grpc.stub.StreamObserver<com.service.grpc.CommunicationServiceOuterClass.Header>) responseObserver);
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
              .addMethod(METHOD_COMMUNICATION)
              .addMethod(METHOD_PING_HANDLER)
              .addMethod(METHOD_MESSAGE_HANDLER)
              .build();
        }
      }
    }
    return result;
  }
}
