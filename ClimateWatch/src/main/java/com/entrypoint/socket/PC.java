package com.entrypoint.socket;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import data.ReadData;
import gash.messaging.Message;
import gash.messaging.Node;
import gash.router.client.MessageClient;
import gash.router.server.MessageServer;
import redis.clients.jedis.Jedis;
import routing.Pipe.Route;


public class PC extends Node{

	String LeaderNodeIP = null;
	boolean isLeader = false;
	String ip = null;
	List<String> otherNodes = new ArrayList<String>();
	
	public MessageClient mc ;
	public MessageServer ms ;
	
    public enum RState {
        Follower, Candidate, Leader
    }
    
    RState state;
	private int voteCount;
	private int currentTerm;
	private int max;

	
	
	public PC(int id,String ip) {
		super(id);
		
		//update my ip
		this.ip = ip;
		
		//Connect to redis
		Jedis jedis = new Jedis("redis-11146.c11.us-east-1-2.ec2.cloud.redislabs.com", 11146);
	    jedis.auth("CMPE295");
	    
	    ////DELETING IPMAP ONLY 1st PC!!!!
	    jedis.del("IP-Map");
	    
	    //Update redis
	    Random rand = new Random();
        int  n = rand.nextInt(1000) + 1;
	    jedis.hset("IP-Map", String.valueOf(n), this.ip);
	    
	    
		//Start local server
	    File cf=new File("resources/routing.conf");
		this.ms=new MessageServer(cf,this);
		//this.mc=new MessageClient();
		Runnable r = new MyRunnable(this.ms);
		new Thread(r).start();
		
		
		//set state 
		state = RState.Follower;
		
		
		Timer timer = new Timer();
		//Scheduling elections in 30 sec
		timer.schedule(new ElectionMonitor(this), 30*1000);
		//Decide the leader in 
	    //timer.schedule(new InitialLeader(this), 20*1000);
	    
	}
	
	public void init()
	{
		
		// Timer timer = new Timer();
	    // timer.schedule(new HeartBeatTask(), 10*1000);
	     
	}

	
	public void disperseData(){
		if(state == RState.Leader){
			ReadData readData = new ReadData();
			readData.getFile(mc,otherNodes);
		}
		
	}
	@Override
	public void process(Message msg) {
		/*System.out.println("Message Received at lalala" + msg);
		//if (msg.toString().contains("RequestVote"))*/
		/*if(state == RState.Leader){
			System.out.println("");
			int i=0;
			
			String data="Data";
			while(true){
				this.mc = new MessageClient(otherNodes.get(i),4568);
				i++;
				mc.postMessage(data+String.valueOf(i));
				if(i == otherNodes.size()){
					i=0;
				}
				
			}
			
		}*/
		
		if(msg.toString().contains("RequestVote")){
			String[] x = msg.toString().split(" ");
			if(Integer.parseInt(x[2]) >max){
				max=Integer.parseInt(x[2]);
				LeaderNodeIP=x[0];
			}
		}
			
		
	}
	
	
	// how to constantly check for a leader?
	public void setLeader(String ip)
	{
		this.LeaderNodeIP = ip;
		
	}
	
	public String getLeader()
	{
		return LeaderNodeIP;
		
	}
	 protected void checkBeats()
     {
       
     }

	  class HeartBeatTask extends TimerTask {
		  PC pc ;
		  
		  public HeartBeatTask() {
			// TODO Auto-generated constructor stub
		}
		  
	        public void run() {
	            if(state == RState.Leader)
	            {
//	            	checkHeartBeat();
	            }
	            else
	            {
//	            	sendHeartBeat();
	            }
	        }
	    }
	  
	  class ElectionMonitor extends TimerTask 
	  {
		  PC pc = null;	      

	        public ElectionMonitor(PC pc)
	        {
	            this.pc = pc;
	        }

	        @Override
	        public void run()
	        {
	        	
	        	//pull from redis
	        	Jedis jedis = new Jedis("redis-11146.c11.us-east-1-2.ec2.cloud.redislabs.com", 11146);
	    	    jedis.auth("CMPE295");
	        	Map<String, String> records = jedis.hgetAll("IP-Map");
	        	int max=0;
	        	
	        	String maxIP=pc.ip;
	        	for(Map.Entry<String,String> entry : records.entrySet()){
	    	    	System.out.println(String.format("%s : %s", entry.getKey(), entry.getValue()));
	    	    	otherNodes.add(entry.getValue());
	    	    	if(Integer.parseInt(entry.getKey())>max){
	    	    		max=Integer.parseInt(entry.getKey());
	    	    		maxIP=entry.getValue();
	    	    	}
	    	    	
	    	    	
	    	    }
	        	System.out.println("Leader is"+maxIP);
	        	if(pc.ip.equals(maxIP)){
	        		pc.state=RState.Leader;
	        		pc.disperseData();
	        		
	        	}
	        	
            	
            	
	        }
	    }

	  class InitialLeader extends TimerTask 
	  {
		  PC pc = null;	      

	        public InitialLeader(PC pc)
	        {
	            this.pc = pc;
	        }

	        @Override
	        public void run()
	        {
	        	//how to determineleader iP
	            pc.setLeader(LeaderNodeIP);
	            System.out.println("My leader is "+LeaderNodeIP);
	            if(pc.ip==LeaderNodeIP){
	            	System.out.println("I AM LEADER");
	            	pc.state=RState.Leader;
	            	/*Object msg;
	            	msg=";";
	            	Message m=new Message(10,((Route)msg).getPayload());
	        		
	            	pc.process(m);*/
	            	pc.disperseData();
	            }
	        }
	    }
	  
	  
	public void startElection() 
	{;
		state = RState.Candidate;
        voteCount = 1;
        currentTerm++;
        
        Random rand = new Random();

        int  n = rand.nextInt(1000) + 1;
        max=n;
        LeaderNodeIP=this.ip;
        
        System.out.println("My ID is"+n);

        sendRequestVoteNotice(n);
        
        
        
//        resetElectionTimer();
        System.out.println("Election started by Node " + getNodeId() + " Term " + currentTerm);
		
	}

	private void sendRequestVoteNotice(int id) {
		
		StringBuilder retString = new StringBuilder();
		retString.append(this.ip + " " );
		retString.append("RequestVote"+ " ");
		retString.append(id);
		for (int i = 0; i < otherNodes.size() ; i++) {
			//this.mc = new MessageClient(otherNodes.get(i),4568);
			mc.commConnection.bs.connect(otherNodes.get(i),4568);
			mc.postMessage(retString.toString());
						
		}
	
        
        
        
        
//        msg.setTerm(currentTerm);
//        send(msg);
		
	}
	
	public class MyRunnable implements Runnable {
			
		MessageServer svr;

		   public MyRunnable(MessageServer svr) {
		       // store parameter for later user
			   this.svr=svr;
		   }

		   public void run() {
			   svr.startServer();
		   }
		}

}
