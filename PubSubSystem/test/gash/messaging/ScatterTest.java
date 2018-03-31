package gash.messaging;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import com.mongodb.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBCollection;

import gash.messaging.transports.Scatter;

public class ScatterTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testFloodLoad() throws Exception {
		/*Mongo mongoClient = new Mongo("localhost", 27017);
		DB database = mongoClient.getDB("mean");
		DBCollection collection = database.getCollection("sampleGeoData");
		System.out.println(collection);
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("primary id", "KAKR");
		DBCursor cursor = collection.find(searchQuery);
		String test;
		 
		while (cursor.hasNext()) {
		    System.out.println(cursor.next());
		    //test=cursor.next().toString();
		}*/
		
		Scatter myKafka = new Scatter(2);
		for (int n = 0; n < 10; n++) {
			Message m = new Message(n);
			m.setOriginator(-999);
			m.setMessage("This is a message - " + n);

			myKafka.sendMessage(m);
		}
		//mongoClient.close();

		Thread.sleep(15000);

		myKafka.printFinalStats();
	}
}
