package com.entrypoint.socket;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.service.grpc.App;
import gash.messaging.Message;
import gash.messaging.Node;
import gash.router.client.CommListener;
import gash.router.client.MessageClient;
import gash.router.server.MessageServer;
import redis.clients.jedis.Jedis;
import routing.Pipe.Route;

public class PC extends Node implements CommListener {

	String LeaderNodeIP = null;
	boolean isLeader = false;
	public String ip = null;
	public List<String> otherNodes = new ArrayList<String>();

	public MessageClient mc;
	public MessageServer ms;

	public App appServer;

	public List<String> qList;
	public List<String> qList_space;


	public static PC instance = null;

	public enum RState {
		Follower, Candidate, Leader
	}

	RState state;
	private int voteCount;
	private int currentTerm;
	private int max;
	private Jedis jedis;
	private String maxIP;
	private Timer timer;

	public PC(int id, String ip) throws Exception {
		super(id);
		// parseMesowest();

		// update my ip
		this.ip = ip;

		// Connect to local redis
		initDB();


		//Start local server
	    File cf=new File("resources/routing.conf");
		this.ms=new MessageServer(cf,this);

		this.qList=new ArrayList<String>();
		this.qList_space=new ArrayList<String>();

		Runnable startServerThread = new StartServerThread(this.ms);
		new Thread(startServerThread).start();

		// set state
		state = RState.Follower;

		// Timer timer = new Timer();
		// //Scheduling elections in 30 sec
		// timer.schedule(new ElectionMonitor(this), 20*1000);
		//
		// wait 20 sec

		Thread.sleep(20 * 1000);

		electionFunctions();

	}

	public static PC getInstance() throws Exception
	{
		if(instance==null)
		{
			instance = new PC(1,EntryPoint.getIP());
			//instance = new PC(1,"169.254.198.56");
		}
		return instance;

	}

	@Override
	public String getListenerID() {
		return "demo";
	}

	@Override
	       public void onMessage(Route msg) {
	               //System.out.println("Final PC recieved ---> " + msg.getPayload());
	               if(msg.getPayload().contains("SERVERRESPONSE"))
	            	   qList.add(msg.getPayload());
	               if(msg.getPayload().contains("SPACECHECK"))
	            	   qList_space.add(msg.getPayload());

	       }
	public void initDB() {
		jedis = new Jedis("redis-11146.c11.us-east-1-2.ec2.cloud.redislabs.com", 11146);
		jedis.auth("CMPE295");
		putIdIP();

	}

	public void putIdIP() {
		// Update redis
		Random rand = new Random();
		int n = rand.nextInt(1000) + 1;
		jedis.hset("IP-Map", String.valueOf(n), this.ip);
	}

	public void updateLeader() throws Exception {
		// delete from
		Jedis jedis = new Jedis("redis-11146.c11.us-east-1-2.ec2.cloud.redislabs.com", 11146);
		jedis.auth("CMPE295");
		jedis.hdel("IP-Map", String.valueOf(max));

		// delete from bluemix
		deleteLeaderAmongClusters(maxIP);

		// delete other nodes
		otherNodes.clear();

		this.electionFunctions();
	}

	public void electionFunctions() throws Exception {

		// pull from redis
		Jedis jedis = new Jedis("redis-11146.c11.us-east-1-2.ec2.cloud.redislabs.com", 11146);
		jedis.auth("CMPE295");
		Map<String, String> records = jedis.hgetAll("IP-Map");
		int max = 0;

		maxIP = this.ip;
		for (Map.Entry<String, String> entry : records.entrySet()) {
			System.out.println(String.format("%s : %s", entry.getKey(), entry.getValue()));
			otherNodes.add(entry.getValue());
			if (Integer.parseInt(entry.getKey()) > max) {
				max = Integer.parseInt(entry.getKey());
				maxIP = entry.getValue();
			}

		}
		System.out.println("Leader is" + maxIP);

		setLeaderAmongClusters(maxIP);

		if (this.ip.equals(maxIP)) {
			this.state = RState.Leader;

			if (timer != null) {
				timer.cancel();
			}

			timer = new Timer();
			timer.schedule(new HeartBeatTask(this), 1, 40 * 1000);

			// pc.disperseData();

			if (appServer != null) {
				appServer.stop();
			}
			appServer = new App();
			appServer.start(this);

		}

	}

	public void disperseData() {
		if (state == RState.Leader) {
			// ReadData readData = new ReadData();
			// readData.getFile(mc,otherNodes);
		}

	}

	@Override
	public void process(Message msg) {

	}

	// how to constantly check for a leader?
	public void setLeader(String ip) {
		this.LeaderNodeIP = ip;

	}

	public String getLeader() {
		return LeaderNodeIP;

	}

	protected void checkBeats() {

	}

	class HeartBeatTask extends TimerTask {
		PC pc;

		public HeartBeatTask(PC pc) {
			// TODO Auto-generated constructor stub
			this.pc = pc;
		}

		public void run() {
			if (state == RState.Leader) {
				System.out.println("sending heartbeat");

				for (int i = 0; i < otherNodes.size(); i++) {
					System.out.println("sending heartbeat to nodes");
					pc.mc = new MessageClient(otherNodes.get(i % otherNodes.size()), 4568);
					pc.mc.postMessage(pc.addMessageTypeHEARTBEAT("HeyImthere"));
				}
			} else {
				// //checkHeartBeat();
			}
		}
	}

	private void setLeaderAmongClusters(String ip) throws Exception {
		String url = "https://cmpe275-spring-18.mybluemix.net/put/";

		url += ip;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());
	}

	private void deleteLeaderAmongClusters(String ip) throws Exception {
		String url = "https://cmpe275-spring-18.mybluemix.net/delete/";

		url += ip;

		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// optional default is GET
		con.setRequestMethod("GET");

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// print result
		System.out.println(response.toString());
	}

	public String addMessageTypeJSON(String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("'Type:JSON',").append("'").append(content).append("'");
		return sb.toString();
	}

	public String addMessageTypePUTQUERY(String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("Type:PUTQUERY ").append(content);
		return sb.toString();
	}

	public String addMessageTypeGETQUERY(String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("Type:GETQUERY ").append(content);
		return sb.toString();
	}

	public String addMessageTypeGETSPACE() {
		StringBuilder sb = new StringBuilder();
		sb.append("Type:GETSPACE");
		return sb.toString();
	}

	public String addMessageTypeHEARTBEAT(String content) {
		StringBuilder sb = new StringBuilder();
		sb.append("Type:HEARTBEAT ");
		return sb.toString();
	}

	public void parseMesowest() {
		String[] headers = { "station", "WeatherDate", "MNET", "latitude", "longitude", "elevation", "temperature",
				"SKNT", "DRCT", "GUST", "PMSL", "altitude", "DWPF", "RELH", "WTHR", "P24I" };
		MongoClient mongoClient = null;
		DBCollection dbCollection = null;
		try {
			if (mongoClient == null) {
				try {
					// mongoClient = new MongoClient(new
					// MongoClientURI("mongodb://localhost:27017"));
					mongoClient = new MongoClient("localhost", 27017);
					DB messageDB = mongoClient.getDB("messagesDB");
					dbCollection = messageDB.getCollection("data");
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
			File file = new File("/Users/mulumoodi/Downloads/11.mesowest.out");
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			int count = 0;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.length() != 0 && count > 3) {
					stringBuffer.append(line);
					System.out.println("\n");
					String[] lineArray = line.split(" ");
					int size = 0;
					int j = 0;
					String uniqueID = UUID.randomUUID().toString();
					BasicDBObject messageObject = new BasicDBObject("_id", uniqueID);
					for (int i = 0; i < lineArray.length; i++) {
						if (lineArray[i].length() != 0) {
							if (j == 1) {
								// lineArray[i].replaceAll("/", " ");
								try {
									Date date = new SimpleDateFormat("yyyyMMdd/HHmm").parse(lineArray[i]);
									messageObject.append(headers[j], date);
								} catch (Exception ex) {
									System.out.println(ex.getMessage());
								}
							} else {
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
			// System.out.println("Contents of file:");
			System.out.println(stringBuffer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public class StartServerThread implements Runnable {

		MessageServer svr;

		public StartServerThread(MessageServer svr) {
			// store parameter for later user
			this.svr = svr;
		}

		public void run() {
			svr.startServer();
		}
	}

}
