package com.entrypoint.socket;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gash.messaging.Message;
import gash.messaging.Node;
import gash.router.client.MessageClient;
import gash.router.server.MessageServer;

public class PC extends Node{

	Node LeaderNode = null;
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

	
	
	public PC(int id,String ip) {
		super(id);
		this.ip = ip;
		File cf=new File("resources/routing.conf");
		this.ms=new MessageServer(cf);
		
		state = RState.Follower;
		init();
		//TODO set ip to INETAddress
		
		//TODO use MS
		
		//start timer and timeout
		
		//electionTimer		
		

		
	       
		

	  
		
		//
	}
	
	public void init()
	{
		
		 Timer timer = new Timer();
	     timer.schedule(new HeartBeatTask(), 10*1000);
	}

	
	
	@Override
	public void process(Message msg) {
		System.out.println("Message Received at" + msg);
		
	}
	
	
	// how to constantly check for a leader?
	public void setLeader(Node node)
	{
		this.LeaderNode = node;
		
	}
	
	public Node getLeader()
	{
		return LeaderNode;
		
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

	public void startElection() 
	{
		state = RState.Candidate;
        voteCount = 1;
        currentTerm++;
        sendRequestVoteNotice();
        
        
        
//        resetElectionTimer();
        System.out.println("Election started by Node " + getNodeId() + " Term " + currentTerm);
		
	}

	private void sendRequestVoteNotice() {
		StringBuilder retString = new StringBuilder();
		retString.append(this.ip + " " );
		retString.append("RequestVote"+ " ");
		retString.append(currentTerm);
		for (int i = 0; i < otherNodes.size() ; i++) {
			this.mc = new MessageClient(otherNodes.get(i),4568);
			mc.postMessage(retString.toString());
						
		}
	
        
        
        
        
//        msg.setTerm(currentTerm);
//        send(msg);
		
	}

}
