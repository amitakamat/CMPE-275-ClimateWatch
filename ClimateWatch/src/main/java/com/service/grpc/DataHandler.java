package com.service.grpc;

import io.grpc.stub.StreamObserver;
import io.grpc.*;
import com.google.protobuf.ByteString;
import java.net.HttpURLConnection;
import java.net.URL;

// TODO: should create new class to handle db and remove all these import

import com.mongodb.DBObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.bson.Document;
import java.util.Date;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DataHandler implements HttpHandler {
	
    @Override
    public void handle(HttpExchange t) throws IOException {
    	String requestURI = String.valueOf(t.getRequestURI().getQuery());
    	/*System.out.println(requestURI);
    	String[] requestSplit = requestURI.split("?");
    	System.out.println(requestSplit[1]);*/
    	try {
	    	System.out.println(requestURI);
	    	Date fromTime = new Date();
	    	Date toTime = new Date();
	    	String station = "";
	    	String temp = "";
	    	String[] queryParameters = requestURI.split("&");
	    	List<DBObject> responseData = new ArrayList<>();
	    	System.out.println(String.format("Params : %d", queryParameters.length));
	
	    		for(int  i=0; i<queryParameters.length; i++) {
	    			String[] param = queryParameters[i].split("=");
	    			System.out.println(param[1]);
	    			if(param[0].equals("from")) {
	    				fromTime = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").parse(param[1]);
	    				System.out.println(String.format(" In from : %s", String.valueOf(param[1])));
	    			}
	    			if(param[0].equals("to")) {
	    				toTime = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss").parse(param[1]);
	    				System.out.println(String.format(" In to : %s", String.valueOf(param[1])));
	    			}
	    			if(param[0].equals("station")) {
	    				station = param[1];
	    			}
	    			if(param[0].equals("temp")) {
	    				temp = param[1];
	    			}
	    		}
				System.out.println(fromTime);
				responseData = new MongoHandler().queryDB(fromTime, toTime, station, temp);
    	System.out.println("Rest Request Received");
    	//String responseString = "Rest Request Received";
        OutputStream os = t.getResponseBody();
        String responseString = "{'data':[";
        for(DBObject record:responseData) {
        	//System.out.println(String.valueOf(record));
        	responseString+=String.valueOf(record)+",";
        	//os.write(String.valueOf(record).getBytes());
        }
        responseString = responseString.substring(0, responseString.lastIndexOf(","));
        responseString += "]}";
        System.out.println(responseString);
        t.sendResponseHeaders(200, responseString.length());
        os.write(responseString.getBytes());

        //os.write(String.valueOf(responseData).getBytes());
        os.close();
        
        getClusterLeaders();
    	}
    	catch(Exception ex) {
    		System.out.println("Failed to retrive query parameters");
    		System.out.println(ex.getMessage());
    	}
    }
    
    
    private void getClusterLeaders() throws Exception {
    	String url = "https://cmpe275-spring-18.mybluemix.net/get";
		
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");
		
		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		//print result
		System.out.println(response.toString());

    }
}
