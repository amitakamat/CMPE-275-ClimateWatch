package com.entrypoint.socket;

import redis.clients.jedis.Jedis;

public class ClearDb {
	public static void main(String[] args) {
		Jedis jedis = new Jedis("redis-11146.c11.us-east-1-2.ec2.cloud.redislabs.com", 11146);
	    jedis.auth("CMPE295");
	    
	    ////DELETING IPMAP ONLY 1st PC!!!!
	    jedis.del("IP-Map");
	}

}
