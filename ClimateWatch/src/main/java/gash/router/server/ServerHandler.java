/**
 * Copyright 2016 Gash.
 *
 * This file and intellectual content is protected under the Apache License, version 2.0
 * (the "License"); you may not use this file except in compliance with the
 * License.  You may obtain a copy of the License at:
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package gash.router.server;

import java.beans.Beans;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.service.grpc.MongoHandler;

import gash.messaging.Message;
import gash.messaging.Node;
import gash.router.container.RoutingConf;
import gash.router.server.resources.RouteResource;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import routing.Pipe.Route;

/**
 * The message handler processes json messages that are delimited by a 'newline'
 * 
 * TODO replace println with logging!
 * 
 * @author gash
 * 
 */
public class ServerHandler extends /*SimpleChannelInboundHandler<Route>*/ ChannelInboundHandlerAdapter {
	protected static Logger logger = LoggerFactory.getLogger("connect");
	protected static MongoClient mongoClient;
	protected static DBCollection dbCollection;

	private HashMap<String, String> routing;
	
	public Node n;

	public ServerHandler(RoutingConf conf,Node n) {
		this.n=n;
		if (conf != null)
			routing = conf.asHashMap();
		
		if (mongoClient == null) {
			try {
				//mongoClient = new MongoClient(new MongoClientURI("mongodb://localhost:27017"));
				System.out.println("Is this error point");
				mongoClient = new MongoClient("localhost", 27017);
				DB messageDB = mongoClient.getDB("messagesDB");
				dbCollection = messageDB.getCollection("data");
			}
			catch(Exception e) {
				System.out.println("Is this error point");
				System.out.println(e.getMessage());
			}
}
	}



	/**
	 * a message was received from the server. Here we extract the from and to date time,
	 * query the database to fetch records based on the query and display it.
	 * 
	 * @param payload
	 *            The message payload received
	 */
	public void queryDB(String payload) {
		// Assuming we get from and to date in a single message divided by ',' 
		String[] filters = payload.split(",");
		try {
		Date fromTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(filters[0]);
		Date toTime = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(filters[1]);
		
		BasicDBObject query = new BasicDBObject("time", new BasicDBObject("$gte", fromTime).append("$lte", toTime));
		DBCursor cursor = dbCollection.find(query);
		while(cursor.hasNext()) {
	        System.out.println(cursor.next());
	    }
		
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}
	
	public String queryDB1(String payload) {
		String[] filters = payload.split("and");
		MongoHandler h=new MongoHandler();
		String resp="";
		//h.queryDB(fromTime, toTime, station, temp)
		try {
			Date fromTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(filters[0]);
			Date toTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(filters[1]);
			
			BasicDBObject query = new BasicDBObject("time", new BasicDBObject("$gte", fromTime).append("$lte", toTime));
			DBCursor cursor = dbCollection.find(query);
			while(cursor.hasNext()) {
		        //System.out.println(cursor.next());
		        resp += String.valueOf(cursor.next())+" \n";
		    }
			
			}
			catch(Exception ex) {
				System.out.println(ex.getMessage());
			}
		System.out.println(resp);
		return resp;
	}
	public void writeToDB(String split){
    	String[] headers = {"STN", "WeatherDate", "MNET", "SLAT", "SLON", "SELV", "TMPF", "SKNT", "DRCT", "GUST", "PMSL", "ALTI", "DWPF", "RELH", "WTHR", "P24I"};
		 //MongoClient mongoClient = null;
		 //DBCollection dbCollection = null; 
		 
    	System.out.println("Recieved a put query");
    	System.out.println(split);
    	String str = split.substring(0, split.length() - 2);
    	//System.out.println(str);
    	
    	
    	String[] entries=str.split("n");
    	for(int i=0;i<entries.length;i++){
    		entries[i]=entries[i].substring(0, entries[i].length() - 1);
    		System.out.println(entries[i]);
    	}
    	
    	System.out.println("After split");
    	//System.out.println(lines[0]);
    	int lineNo=0;
    	String line;
    	StringBuffer stringBuffer = new StringBuffer();
    	int count=0;
    	
    	while(entries[lineNo]!=null){
    		line=entries[lineNo];
			if(line.length()!=0 && count>3) {
				stringBuffer.append(line);
				System.out.println("\n");
				String[] lineArray = line.split(" ");
				int size = 0;
				int j = 0;
				String uniqueID = UUID.randomUUID().toString();					
				BasicDBObject messageObject = new BasicDBObject("_id", uniqueID);
				for(int i=0; i<lineArray.length; i++) {
					if(lineArray[i].length()!=0) {
						if(j==1) {
							//lineArray[i].replaceAll("/", " ");
							try {
								Date date = new SimpleDateFormat("yyyyMMdd/HHmm").parse(lineArray[i]);
								messageObject.append(headers[j], date);
							}
							catch(Exception ex) {
								System.out.println(ex.getMessage());
							}
						}
						else {
							messageObject.append(headers[j], lineArray[i]);
						}
						j++;
					}
				}
				dbCollection.insert(messageObject);
				//System.out.println(line);
				//stringBuffer.append("\n\n\n");
			}
			count++;
		}
	}

	
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //ByteBuf in = (ByteBuf) msg;
		//Route.Builder rb = Route.newBuilder();
	      // ((Route.Builder)msg).getPayload();
		String recvdMesg=msg.toString();//((Route.Builder)msg).getPayload();
        System.out.println(
            "Server received: " + recvdMesg);
        
        recvdMesg=recvdMesg.split("payload: \"")[1].toString();
        System.out.println("String is below");
        System.out.println(recvdMesg);
        
        String[] splitMesg=recvdMesg.split(" ",2);
        System.out.println(splitMesg[0]+"yay");

        if(splitMesg[0].contains("PUTQUERY")){

        	writeToDB(splitMesg[1]);
        }
        if(splitMesg[0].contains("GETQUERY")){
        	System.out.println(splitMesg[1].substring(0, splitMesg[1].length()-1));

        	queryDB1(splitMesg[1].substring(0, splitMesg[1].length()-1));
        }
        if(splitMesg[0].contains("ping")){

        	//queryToDB(splitMesg[1]);
        }
        
        
    }

    public void channelReadComplete(ChannelHandlerContext ctx) {
    	Route.Builder rb = Route.newBuilder();
		rb.setId(10);
		rb.setPath("/message");
		rb.setPayload("Yo Yo Yo");
        /*ctx.writeAndFlush(rb.build())
            .addListener(ChannelFutureListener.CLOSE);*/
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

}