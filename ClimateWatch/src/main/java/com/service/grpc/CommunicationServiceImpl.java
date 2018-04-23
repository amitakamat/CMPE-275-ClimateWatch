package com.service.grpc;

import io.grpc.stub.StreamObserver;
import routing.Pipe.Route;
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
import com.service.grpc.MongoHandler;

import gash.router.client.MessageClient;

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
import java.util.concurrent.TimeUnit;

import com.entrypoint.socket.PC;

public class CommunicationServiceImpl extends CommunicationServiceGrpc.CommunicationServiceImplBase {   
	protected static MongoClient mongoClient;
	protected static DBCollection dbCollection; 
	static String[] headers = {"STN", "WeatherDate", "MNET", "SLAT", "SLON", "SELV", "TMPF", "SKNT", "DRCT", "GUST", "PMSL", "ALTI", "DWPF", "RELH", "WTHR", "P24I"};
	static int maxChunkSize = 10;
	private List<String> localnodes;
	private PC pc;
	private int nodeNo;
	
	public CommunicationServiceImpl(){
		
	}
	public CommunicationServiceImpl(PC pc){
		this.pc=pc;
		this.localnodes=pc.otherNodes;
		this.nodeNo=0;
		
	}
	
	/*	int rr=0;
	 	onRecievingMesg
	 		mc = new MessageClient(otherNodes.get(i%otherNodes.size()),4568);
	 		mc.postMessage(mesg);
	 		rr++;
	 */
	
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
		   String sender = request.getFromSender();
		   QueryParams params = request.getGetRequest().getQueryParams();
	   	   try {  
			   	Date fromTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(params.getFromUtc());
				Date toTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(params.getToUtc());
				//List<DBObject> responseData = new MongoHandler().queryDB(fromTime, toTime, params.getParamsJson());
				System.out.println(params.getParamsJson());
				for(int i=0;i<localnodes.size();i++){	
	        		pc.mc = new MessageClient(localnodes.get(i%localnodes.size()),4568);
	        		pc.mc.addListener(pc);
	        		pc.mc.postMessage(pc.addMessageTypeGETQUERY(params.getFromUtc()+"and"+params.getToUtc()+"and"+params.getParamsJson()));
	        	}
	        		
				class checker
				{
				
				 Boolean hasData = false;
				 Integer retry=0;
				 Integer hasDataVal=0;
				
				
				}
				
				final checker chks = new checker();
				new Thread() {	
				    public void run() {
				        try {
				        	while(true)
							{          
				        		
				        		TimeUnit.MILLISECONDS.sleep(5);//SECONDS.sleep(0.5);
								if(pc.qList.size()!=0){
									chks.hasData=true;
									chks.hasDataVal=1;
									System.out.println("removing and sending out last mesg");   
									//System.out.println(pc.qList.remove(0));	
									final String responseMsg1 = "Data not present";	   
								      
								       MetaData metadata = MetaData.newBuilder()
								   		          .setUuid("")
								   		          .setNumOfFragment(1)
								   		          .setMediaType(3)
								   		          .build();
								      
								      DatFragment dataFragment = DatFragment.newBuilder()
										      		  .setData(ByteString.copyFromUtf8(pc.qList.remove(0)))
										      		  .build();
								
								      
								      Response response = Response.newBuilder()
								   	          .setMsg(responseMsg1)
								   	          .setMetaData(metadata)
								   	          .setDatFragment(dataFragment)
								   	          .build();
								
								      responseObserver.onNext(response);
								      //responseObserver.onCompleted();
								      if(pc.qList.size()==0){
								    	  System.out.println("Sent all data");
								    	  responseObserver.onCompleted();
								    	  return;
								      }
									
								}
								else{
									chks.retry++;
								}
								
								if(chks.retry>=10000){
									System.out.println("Found no data");
									chks.hasDataVal=2;
									responseObserver.onCompleted();
				        			return;
								}
							}
				        } catch(Exception v) {
				            System.out.println(v);
				        }
				    }  
				}.start();
				
				while(chks.hasDataVal==0){
					//System.out.println("Waiting for our cluster response...!");
				}
				
				if(chks.hasDataVal == 2) {
					boolean receivedData = false;
					String dataResponse = "";
					System.out.println("No data in our cluster");
					//ArrayList<String> clusterLeaders = new ArrayList<String>();
					ArrayList<String> clusterLeaders = new DataHandler().getClusterLeaders();
					//clusterLeaders.add("169.254.204.172");
		        	for(int i=0; i<clusterLeaders.size(); i++) {
		        		if(!clusterLeaders.get(i).equals(pc.ip) && !clusterLeaders.get(i).equals(sender) && !receivedData){
		        			//System.out.println("My IP : " + pc.ip);
		        			ClusterClient c = new ClusterClient(clusterLeaders.get(i));
		        			System.out.println("Sending ping to : " + clusterLeaders.get(i));
		        			Response r = c.ping();
		        			System.out.println(r.getMsg());
		        			if(r.getCode()== StatusCode.Ok) {
		        				System.out.println("Sending get to : " + clusterLeaders.get(i));
			        			dataResponse = c.getRequest(request);
			        			Thread.sleep(500);
			        			if(!dataResponse.equals("")){
			        				receivedData = true;
			        			}
			        			c.channelShutDown();
			        			break;
		        			}	        			
		        		}
		        	}	   	   
		        	if (!dataResponse.isEmpty()) {
		        		responseMsg = "Data present";
			   		   int fragment = 1;
			   		   int chunkSize = 0;
		   			   String responseChunk ="";
		   			   String[] respArray = dataResponse.split("\n");
		   			   for(int j =0; j< respArray.length; j++) {
			   			   System.out.println("Chunk Size: " + String.valueOf(chunkSize));
			   			    
			   			   responseChunk += respArray[j];
			   			
				   			if(chunkSize >= maxChunkSize || j>=respArray.length-1) {
				   				MetaData metadata = MetaData.newBuilder()
					   	   		          .setUuid("12345")
					   	   		          .setNumOfFragment(1)
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
			   		   responseMsg = "Data not present on any cluster";	   
			      
				       MetaData metadata = MetaData.newBuilder()
				   		          .setUuid("")
				   		          .setNumOfFragment(1)
				   		          .setMediaType(3)
				   		          .build();
				      
				      DatFragment dataFragment = DatFragment.newBuilder()
						      		  .setData(ByteString.copyFromUtf8("No data in any cluster"))
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
		   //	System.out.println(new DataHandler().getClusterLeaders());
	    }
	    catch(Exception ex) {
	    	System.out.println(ex.getMessage());
	    }
    }
    
    public StreamObserver<Request> putHandler(final StreamObserver<Response> responseObserver) {
    	System.out.println("Received a PUT request");
	    
	    System.out.println();
	   // File f = new File("//mnt//c//");
	    //System.out.println("Printing the total space");
	    //System.out.println(f.getTotalSpace()/1000000.00 +" Megabytes");
//	    ArrayList<String> clusterLeaders = new ArrayList<String>();
//    	clusterLeaders.add("169.254.204.172");
//    	//System.out.println("My IP : " + pc.ip);
//    	System.out.println(clusterLeaders);
//    	try {
//	    	/*ArrayList<String> clusterLeaders = new DataHandler().getClusterLeaders();*/
//	    	//for(int i=0; i<clusterLeaders.size(); i++) {
//	    		//if(!clusterLeaders.get(i).equals(pc.ip)){
//	    			//System.out.println("My IP : " + pc.ip);
//	    			ClusterClient c = new ClusterClient(clusterLeaders.get(0));
//	    			//c.putRequest(request);
//	    			Response r = c.ping();
//	    			System.out.println(r.getMsg());
//	    			Thread.sleep(500);
//	    			c.channelShutDown();
//	    			//break;
//	    		//}
//	    	//}
//    	} catch(Exception ex) {
//    		System.out.println(ex.getMessage());
//    	}
	    
	    
	    return new StreamObserver<Request>() {
		    int chunksReceived = 0;
		    String sender = "";

	        @Override
	        public void onNext(Request request) {
	        	try {
		        	String receivedMessage = request.getPutRequest().getDatFragment().getData().toStringUtf8();
		        	sender = request.getFromSender();
		        	System.out.println(localnodes.size());
	        		pc.mc = new MessageClient(localnodes.get(nodeNo%localnodes.size()),4568);
	        		pc.mc.postMessage(pc.addMessageTypeGETSPACE());
		        		/*TimeUnit.MILLISECONDS.sleep(5);//SECONDS.sleep(0.5);
						if(pc.qList.size()!=0){
							//TimeUnit.SECONDS.sleep(2);
							System.out.println("get response for space");
							//responseMsg = pc.qList.remove(0);   
							System.out.println(pc.qList.remove(0));	
						}
						*/
	        		// If has space
	        		pc.mc.postMessage(pc.addMessageTypePUTQUERY(receivedMessage));
	        		nodeNo++;
	        		
	        		/*If not
	        		int noSpaceNodes = 1;
		        	for(int i=0; i<localnodes.size()-1; i++){
		        		nodeNo++;
		        		pc.mc.postMessage(pc.addMessageTypeGETSPACE());
		        		
		        	}
		        	// TODO: Test this functionality
		        	/* If space not available on any node in the cluster*/
		        	//ArrayList<String> clusterLeaders = new ArrayList<String>();
//		        	clusterLeaders.add("169.254.204.172");
		        	ArrayList<String> clusterLeaders = new DataHandler().getClusterLeaders();
//		        	for(int i=0; i<clusterLeaders.size(); i++) {
//		        		//if(!clusterLeaders.get(i).equals(pc.ip) && !clusterLeaders.get(i).equals(sender)){
//		        			//System.out.println("My IP : " + pc.ip);
//		        			ClusterClient c = new ClusterClient(clusterLeaders.get(i));
//		        			c.putRequest(request);
//		        			//Response r = c.ping();
//		        			//System.out.println(r.getMsg());
//		        			Thread.sleep(500);
//		        			c.channelShutDown();
//		        			break;
//		        		//}
//		        	}
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
