package gash.messaging.transports;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import gash.messaging.Message;
import gash.messaging.Node;

import gash.router.client.MessageClient;

/**
 * simulate a kafka network where we have a public set of frontend servers
 * (leaders) and a private network of receivers.
 * 
 * enhancements:
 * <ol>
 * <li>The receivers could be on another transport model (e.g. bus) rather than
 * a list of nodes
 * <li>More than one leader node can setup a active-passive role like raft for
 * fault tolerance.
 * <li>Enable load balancing across receivers.
 * <li>Failure and recovery of messages in a receiver. How to re-route enqueued
 * messages without a centralized design?
 * </ol>
 * 
 * @author gash
 *
 */
public class Scatter implements MessageTransport<Message> {
	private LeaderNode leader;
	private SortedMap<Integer, ReceiverNode> nodes;

	public Scatter(int numNodes) {
		nodes = new TreeMap<Integer, ReceiverNode>();

		if (numNodes <= 0)
			return;

		// leader(s)
		System.out.println("initializing leaders");
		LeaderNode aLeader = new LeaderNode(numNodes * 10 + 1, this);
		aLeader.start();
		setLeader(aLeader);

		// receivers
		System.out.println("initialing receivers (" + numNodes + ")");
		/*
		 * for (int n = 0; n < numNodes; n++) { ReceiverNode node = new
		 * ReceiverNode(n, this); nodes.put(node.getNodeId(), node);
		 * node.start(); }
		 */

		ReceiverNode node2 = new ReceiverNode(2, "127.0.0.1", 4568, this);
		nodes.put(node2.getNodeId(), node2);
		node2.start();

		/*ReceiverNode node1 = new ReceiverNode(1, "172.20.10.4", 4567, this);
		nodes.put(node1.getNodeId(), node1);
		node1.start();*/
	}

	public void setLeader(LeaderNode leader) {
		this.leader = leader;
	}

	public LeaderNode getLeader() {
		return leader;
	}

	public void printFinalStats() {
		getLeader().printStats(false);
	}

	/**
	 * messages received from a leader and routed to a receiver
	 * 
	 * @param msg
	 */
	protected void forwardMessage(Message msg) {
		ReceiverNode n = nodes.get(msg.getDestination());
		//System.out.println("forwarded to " + n.getNodeId()+" on port "+n.mc.commConnection.port);
		n.mc.postMessage("Hey from "+n.mc.commConnection.channel.channel().localAddress());
		n.message(msg);
	}

	@Override
	public Node<Message>[] getNodes() {
		Node<Message>[] r = new ReceiverNode[nodes.size()];
		return nodes.values().toArray(r);
	}

	@Override
	public void addNode(Node<Message> node) {
		if (node != null && !nodes.keySet().contains(node.getNodeId()))
			nodes.put(node.getNodeId(), (ReceiverNode) node);
	}

	@Override
	public void sendMessage(Message msg) {
		if (msg == null)
			return;

		getLeader().message(msg);
	}

	@Override
	public void sendMessage(int fromNodeId, int toNodeId, String text) {
		Message msg = new Message(0);
		msg.setOriginator(fromNodeId);
		msg.setMessage(text);
		getLeader().message(msg);
	}

	@Override
	public void broadcastMessage(int fromNodeId, String text) {
		Message msg = new Message(0);
		msg.setOriginator(fromNodeId);
		msg.setMessage(text);
		getLeader().message(msg);
	}

	/**
	 * represents a node in the scatter network. A scatter node is a receiver
	 * (broker in the kafka model) and does not act as a leader.
	 * 
	 * @author gash1
	 * 
	 */
	public class ReceiverNode extends Node<Message> {
		private int processed = 0;
		private boolean debug = false;
		private Scatter network;
		public MessageClient mc;
		public int port;

		/**
		 * construct a new receiver node - it must be aware of the network for
		 * the simulation
		 * 
		 * @param id
		 *            The unique ID for the receiver
		 * @param network
		 *            The network it belongs to
		 */
		public ReceiverNode(int id, String ip, int port, Scatter network) {
			super(id);
			this.network = network;
			this.port=port;
			this.mc = new MessageClient(ip, port);

			// delay factor to simulate work latency variability
			// setDelayFactor(4);

			// simulation - because the heartbeat is only sent when processing a
			// message, we have to notify the leader that the receiver exists
			// and is ready to receive messages.
			sendHeartBeat();
		}

		public void setDebug(boolean on) {
			debug = on;
		}

		private Node<Message> getLeader() {
			// TODO what happens if there is no leader?
			return network.getLeader();
		}

		private void sendHeartBeat() {
			// simulate sending a HB message
			LeaderNode ln = (LeaderNode) getLeader();
			ln.heartbeat(this.nodeId, this.getInbox().size(), processed);
		}

		@Override
		public void process(Message msg) {
			if ((msg.getDeliverAs() == Message.Delivery.Direct && msg.getDestination() == getNodeId())
					|| (msg.getDeliverAs() == Message.Delivery.Broadcast)) {
				processed++;
				sendHeartBeat();
				try {
					sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (debug)
					System.out.println("Receiver Node " + getNodeId() + " (from = " + msg.getOriginator() + ") "
							+ msg.getMessage());
			} else {
				if (debug)
					System.out.println("Receiver Node " + getNodeId() + " ignoring msg from " + msg.getOriginator());
			}
		}
	}

	/**
	 * Holds stats of messages by receiver
	 * 
	 * @author gash
	 *
	 */
	public class RoutingStats {
		public int id;
		public int enqueued, maxEnqueued;
		public int total;

		public void set(int qsize, int total) {
			if (this.maxEnqueued < qsize)
				this.maxEnqueued = qsize;
			this.enqueued = qsize;
			this.total = total;
		}
	}

	/**
	 * for simplification, the leader is our gateway to distribute messages
	 * across the receivers.
	 * 
	 * @author gash
	 *
	 */
	public class LeaderNode extends Node<Message> {
		private Scatter network;
		private HashMap<Integer, RoutingStats> stats;
		private Random algoRand;
		private long received = 0, stride = 10;

		// for algorithms
		private Iterator<RoutingStats> rrIter;

		public LeaderNode(int id, Scatter network) {
			super(id);
			this.stats = new HashMap<Integer, RoutingStats>();
			this.network = network;

			algoRand = new Random(System.currentTimeMillis());
		}

		/**
		 * collect heartbeats from receivers.
		 * 
		 * Note the synchronized is needed for the simulation's threading where
		 * we have many threads (receivers) writing to the heartbeat()
		 * 
		 * @param id
		 * @param qsize
		 * @param processed
		 */
		public synchronized void heartbeat(int id, int qsize, int processed) {
			RoutingStats rs = stats.get(id);
			if (rs == null) {
				rs = new RoutingStats();
				rs.id = id;
				stats.put(rs.id, rs);
			}

			rs.set(qsize, processed);

			// leader prints stats for every 10 messages received or if the
			// receiver's queue drops below 10
			int maxQ = -1;
			for (RoutingStats r : stats.values()) {
				if (r.enqueued > maxQ)
					maxQ = r.enqueued;
			}
			if (received % stride == 0 || maxQ < 10)
				printStats(true);
		}

		public void printStats(boolean oneline) {
			if (oneline) {
				for (Integer k : stats.keySet()) {
					RoutingStats rs = stats.get(k);
					System.out.printf("%5d", rs.enqueued);
				}
			} else {
				System.out.println("\nReceiver Status:");
				for (Integer k : stats.keySet()) {
					RoutingStats rs = stats.get(k);
					System.out.printf("%3d) Q: %4d (max %4d), Total: %4d\n", rs.id, rs.enqueued, rs.maxEnqueued,
							rs.total);
				}
			}
			System.out.printf("\n");
		}

		/**
		 * How the leader routes messages - this is the algorithm used to
		 * distribute messages across the receivers.
		 * 
		 * This must be synchronized as we have writers/readers of stats in
		 * different threads.
		 * 
		 * @param msg
		 * @return
		 */
		private synchronized int algoRandom(Message msg) {
			int n = this.network.getNodes().length;

			// random scattering of messages across receivers
			return (algoRand.nextInt(n));
		}

		/**
		 * How the leader routes messages - this is the algorithm used to
		 * distribute messages across the receivers.
		 * 
		 * This must be synchronized as we have writers/readers of stats in
		 * different threads.
		 * 
		 * @param msg
		 * @return
		 */
		private synchronized int algoRoundRobin(Message msg) {
			int n = this.network.getNodes().length;
			//System.out.println("Length of nodes="+n);
			if (n == 0)
				throw new RuntimeException("no receivers to forward message to");

			if (rrIter == null) {
				rrIter = stats.values().iterator();
			}

			if (rrIter.hasNext())
				return rrIter.next().id;
			else {
				rrIter = stats.values().iterator();
				return rrIter.next().id;
			}
		}

		/**
		 * Assign message to the receiver with the lowest number of enqueued (or
		 * total processed) messages.
		 * 
		 * This must be synchronized as we have writers/readers of stats in
		 * different threads.
		 * 
		 * @param msg
		 * @return
		 */
		private synchronized int algoLowestRank(Message msg) {
			int n = this.network.getNodes().length;

			// choose receiver with fewest tasks
			int lowest = Integer.MAX_VALUE, lowId = 0;
			for (RoutingStats r : stats.values()) {
				if (r.enqueued < lowest) {
					lowest = r.enqueued;
					lowId = r.id;
				}
			}
			if (lowest == Integer.MAX_VALUE) {
				System.out.println("no min found");
				return 0;
			} else
				return lowId;
		}

		@Override
		public void process(Message msg) {

			// this is our dispersion algorithm of messages across receiver
			// nodes. Below are three algorithms to choose from.

			// int to = algoRandom(msg);
			int to = algoRoundRobin(msg);
			// int to = algoLowestRank(msg);

			msg.setDestination(to);
			//System.out.println("Forwarding to ="+to);


			// a specialized message passing as leaders are frontends to the
			// private network of receivers
			network.forwardMessage(msg);
			received++;
		}
	}
}


