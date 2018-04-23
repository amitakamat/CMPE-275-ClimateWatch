package com.service.grpc;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.google.gson.JsonObject;
import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import org.bson.json.*;
import org.json.JSONArray;
import org.json.JSONObject;

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
	public List<DBObject> queryDB(Date fromTime, Date toTime, String parameters) {
		List<DBObject> records = new ArrayList<>();
		try {
		System.out.println(fromTime);
		System.out.println(toTime);
		BasicDBObject query = new BasicDBObject();
		query.put("WeatherDate", BasicDBObjectBuilder.start("$gte", fromTime).add("$lte", toTime).get());
		if(parameters.length() != 0) {
			JSONArray jsonarray = new JSONArray(parameters);
			for (int i = 0; i < jsonarray.length(); i++) {
			    JSONObject jsonobject = jsonarray.getJSONObject(i);
			    String op = "$" + jsonobject.getString("op");
			    query.put(jsonobject.getString("lhs"), new BasicDBObject(op, jsonobject.getString("rhs")));
			}
			
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
	
	public static void main(String[] args) {
		try {
			Date fromTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-03-21 01:00:00");
			Date toTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse("2018-03-21 22:20:00");
			new MongoHandler().queryDB(fromTime, toTime, "[{'lhs': 'station', 'op': 'eq', 'rhs': 'CRN'}, {'lhs': 'temperature', 'op': 'eq', 'rhs': '38.35'}]");
		}
		catch(Exception ex) {
			System.out.println(ex.getMessage());
		}
	}

}
