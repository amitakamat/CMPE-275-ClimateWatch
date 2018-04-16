import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.text.SimpleDateFormat;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.lambdaworks.redis.*;
import redis.clients.jedis.Jedis;

public class Parser {
	static String[] headers = {"STN", "WeatherDate", "MNET", "SLAT", "SLON", "SELV", "TMPF", "SKNT", "DRCT", "GUST", "PMSL", "ALTI", "DWPF", "RELH", "WTHR", "P24I"};
	protected static MongoClient mongoClient;
	protected static DBCollection dbCollection; 
	
	public void parseMesowest() {
		try {
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
			File file = new File("//mnt//e//mesowest10.out");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			int count = 0;
			while ((line = bufferedReader.readLine()) != null) {
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
					System.out.println(line);
					stringBuffer.append("\n\n\n");
				}
				count++;
			}
			fileReader.close();
			//System.out.println("Contents of file:");
			System.out.println(stringBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Jedis jedis = new Jedis("redis-11146.c11.us-east-1-2.ec2.cloud.redislabs.com", 11146);
	    jedis.auth("CMPE295");
	    UUID uuid = UUID.randomUUID();
	    jedis.hset("IP-Map", String.valueOf(uuid), "192.168.25.300");
	    //jedis.append(String.valueOf(uuid), "192.168.25.32");
	    Map<String, String> records = jedis.hgetAll("IP-Map");
	    for(Map.Entry<String,String> entry : records.entrySet()){
	    	System.out.println(String.format("%s : %s", entry.getKey(), entry.getValue()));
	    }
	    
	    /** To delete the entire hashmap 
	         jedis.del("IP-Map");
	         
	      ** To delete a key-value pair
     	    jedis.hdel("IP-Map", String.valueOf(uuid));   
	     */


	    System.out.println("Connected to Redis");
	}
}
