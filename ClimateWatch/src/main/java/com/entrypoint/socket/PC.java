package com.entrypoint.socket;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import gash.messaging.Message;
import gash.messaging.Node;
import gash.router.client.MessageClient;
import gash.router.server.MessageServer;
import routing.Pipe.Route;

public class PC extends Node{

	String LeaderNodeIP = null;
	boolean isLeader = false;
	String ip = null;
//	List<Node> otherNodes = new ArrayList<Node>();
//	IPAddress
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
		this.ip = ip;
		File cf=new File("resources/routing.conf");
		this.ms=new MessageServer(cf,this);
		max=0;
		//this.ms.startServer();
		
		Runnable r = new MyRunnable(this.ms);
		new Thread(r).start();
		
		
		
		state = RState.Follower;
		init();
		otherNodes.add("169.254.204.172");
		otherNodes.add("169.254.152.143");
		
		
		
		//new ElectionMonitor(this).run();
		
		Timer timer = new Timer();
		timer.schedule(new ElectionMonitor(this), 20*1000);
	    timer.schedule(new InitialLeader(this), 40*1000);
	    
	     
		
		
		System.out.println("Starting server");
		//TODO set ip to INETAddress
		
		//TODO use MS
		
		//start timer and timeout
		
		//electionTimer		
		

		
	       
		

	  
		
		//
	}
	
	public void init()
	{
		
		// Timer timer = new Timer();
	    // timer.schedule(new HeartBeatTask(), 10*1000);
	     
	}

	
	
	@Override
	public void process(Message msg) {
		/*System.out.println("Message Received at lalala" + msg);
		//if (msg.toString().contains("RequestVote"))*/
		if(state == RState.Leader){
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
			
		}
		
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
	            pc.startElection();
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
	            	Object msg;
	            	msg=";";
	            	Message m=new Message(10,((Route)msg).getPayload());
	        		
	            	pc.process(m);
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
			this.mc = new MessageClient(otherNodes.get(i),4568);
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
