package com.service.grpc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class MongoHandler {
	protected static MongoClient mongoClient;
	protected static DBCollection dbCollection; 
	
	public MongoHandler() {
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
	
	/**
	 * a request was received from the client. Here we extract the from and to date time,
	 * query the database to fetch records based on the query and display it.
	 * 
	 * @param from, to
	 *            The message request parameters received
	 */
	public List<DBObject> queryDB(Date fromTime, Date toTime, String station, String temp) {
		List<DBObject> records = new ArrayList<>();
		try {
		System.out.println(fromTime);
		System.out.println(toTime);
		BasicDBObject query = new BasicDBObject();
		query.put("WeatherDate", BasicDBObjectBuilder.start("$gte", fromTime).add("$lte", toTime).get());
		if(station != "") {
			query.put("STN", station);
		}
		if(temp != "") {
			query.put("TMPF", temp);
		}
		//BasicDBObject query = new BasicDBObject("WeatherDate", new BasicDBObject("$gte", fromTime).append("$lte", toTime));
		DBCursor cursor = dbCollection.find(query);
		while(cursor.hasNext()) {
			records.add(cursor.next());
	    }
		System.out.println(String.format("Total records found : %d", records.size()));
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
		return records;
	}

}
