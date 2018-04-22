package com.service.heartbeat;

import java.util.Timer;
import java.util.TimerTask;

import javax.xml.transform.sax.SAXTransformerFactory;

import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

import heartbeat.HeartBeat.Request;
import heartbeat.HeartBeat.Response;
import heartbeat.HeartBeat.Response.Builder;
import heartbeat.HeartBeat.StatusCode;
import heartbeat.HeartBeatServiceGrpc.HeartBeatServiceImplBase;
import io.grpc.stub.StreamObserver;

public class HeartBeatServiceImpl extends HeartBeatServiceImplBase {

	public CheckIfLeaderPinged leaderCheckTimer;

	public Timer timer = new Timer();

	@Override
	public void heartBeat(Request request, StreamObserver<Response> responseObserver) {

		if (leaderCheckTimer != null) {
			timer.cancel();
			timer.purge();
		}

		leaderCheckTimer = new CheckIfLeaderPinged();
		timer = new Timer();
		timer.schedule(leaderCheckTimer, 40 * 1000);

		Response response;

		Builder responseBuilder = Response.newBuilder();

		responseBuilder.setCode(StatusCode.Ok);
		response = responseBuilder.build();

		responseObserver.onNext(response);
		responseObserver.onCompleted();
	}

	class CheckIfLeaderPinged extends TimerTask {

		@Override
		public void run() {
			{
				System.out.println("Leader LOST , restarting...");

			}

		}

	}

}
