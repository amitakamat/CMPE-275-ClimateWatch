package gash.leaderelection.raft;

import gash.messaging.Message;
import gash.messaging.Message.Delivery;
import gash.messaging.Node;
import gash.messaging.transports.Bus;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The Raft consensus algorithm is similar to PAXOS and Flood Max though it
 * claims to be easier to understand. The paper "In Search of an Understandable
 * Consensus Algorithm" explains the concept. See
 * https://ramcloud.stanford.edu/raft.pdf
 * <p>
 * Note the Raft algo is both a leader election and consensus algo. It ties the
 * election process to the state of its distributed log (state) as the state is
 * part of the decision process of which node should be the leader.
 *
 * @author gash
 */
public class Raft {
    static AtomicInteger msgID = new AtomicInteger(0);

    private Bus<? extends RaftMessage> transport;

    public Raft()
    {
        transport = new Bus<RaftMessage>(0);
    }

    public void addNode(RaftNode node)
    {
        if (node == null)
            return;

        node.setTransport(transport);

        @SuppressWarnings({"rawtypes", "unchecked"})
        Node<Message> n = (Node) (node);
        transport.addNode(n);

        if (!node.isAlive())
            node.start();
    }

    /**
     * processes heartbeats
     */
    public interface HeartMonitorListener {
        public void doMonitor();
    }

    public static abstract class LogEntryBase {
        private int term;
    }

    private static class LogEntry extends LogEntryBase {

    }

    /**
     * triggers monitoring of the heartbeat
     */
    public static class RaftMonitor extends TimerTask {
        private RaftNode<RaftMessage> node;

        public RaftMonitor(RaftNode<RaftMessage> node)
        {
            if (node == null)
                throw new RuntimeException("Missing node");

            this.node = node;
        }

        @Override
        public void run()
        {
            node.checkBeats();
        }
    }

    /**
     * triggers monitoring of the heartbeat
     */
    public static class ElectionMonitor extends TimerTask {
        private RaftNode<RaftMessage> node;

        public ElectionMonitor(RaftNode<RaftMessage> node)
        {
            if (node == null)
                throw new RuntimeException("Missing node");

            this.node = node;
        }

        @Override
        public void run()
        {
            node.startElection();
        }
    }

    /**
     * our network node
     */
    public static class RaftNode<M extends RaftMessage> extends Node<M> {
        static Boolean DEBUG = true;

        int counter = 0;
        private int currentTerm;
        private int lastVoteTerm;
        private Integer votedFor;
        private int voteCount;
        private int numOfServers;
        private RState state;
        private int leaderId;
        private int electionTimeout;
        private int heartBeatTimeout;
        private Timer heartBeatTimer;
        private Timer electionTimer;
        private RaftMonitor raftMonitor;
        private ElectionMonitor electionMonitor;

        private Bus<? extends RaftMessage> transport;

        public RaftNode(int id)
        {
            super(id);
            state = RState.Follower;
            startElectionTimer();
            System.out.println(getNodeId() + " Election timeout period : " + electionTimeout);
            //TODO Make Dynamic
            numOfServers = 9;
        }

        protected void checkBeats()
        {
            System.out.println("--> node " + getNodeId() + " heartbeat");

            if (state == RState.Leader) {
                if (DEBUG) {
                    if (counter == 4) {
                        System.out.println("Stop sending heartbeat");
                        stopHeartBeatTimer();
                    }
                    else {
                        sendAppendNotice();
                        counter++;
                    }
                }
                else {
                    sendAppendNotice();
                }
            }
            else if (state == RState.Candidate) {
                sendEmptyAppendNotice();
            }
        }

        private void sendLeaderNotice()
        {
            RaftMessage msg = new RaftMessage(Raft.msgID.incrementAndGet());
            msg.setOriginator(getNodeId());
            msg.setDeliverAs(Delivery.Broadcast);
            msg.setDestination(-1);
            msg.setAction(RaftMessage.Action.Leader);
            msg.setTerm(currentTerm);
            send(msg);
        }

        private void sendAppendNotice()
        {
            RaftMessage msg = new RaftMessage(Raft.msgID.incrementAndGet());
            msg.setOriginator(getNodeId());
            msg.setDeliverAs(Delivery.Broadcast);
            msg.setDestination(-1);
            msg.setAction(RaftMessage.Action.Append);
            msg.setTerm(currentTerm);
            send(msg);
        }

        private void sendEmptyAppendNotice()
        {
            RaftMessage msg = new RaftMessage(Raft.msgID.incrementAndGet());
            msg.setOriginator(getNodeId());
            msg.setDeliverAs(Delivery.Broadcast);
            msg.setDestination(-1);
            msg.setAction(RaftMessage.Action.EmptyAppend);
            msg.setTerm(currentTerm);
            send(msg);
        }

        /**
         * TODO args should set voting preference
         */
        private void sendRequestVoteNotice()
        {
            RaftMessage msg = new RaftMessage(Raft.msgID.incrementAndGet());
            msg.setOriginator(getNodeId());
            msg.setDeliverAs(Delivery.Broadcast);
            msg.setDestination(-1);
            msg.setAction(RaftMessage.Action.RequestVote);
            msg.setTerm(currentTerm);
            send(msg);
        }

        private void send(RaftMessage msg)
        {
            // TODO
            // enqueue the message - if we directly call the nodes method, we
            // end up with a deep call stack and not a message-based model.
            transport.sendMessage(msg);
        }

        /**
         * this is called by the Node's run() - reads from its inbox
         */
        @Override
        public void process(RaftMessage msg)
        {
            RaftMessage.Action action = msg.getAction();

            switch (action) {
                case Vote:
                    if (state == RState.Candidate) {
                        receivedVote();
                    }
                    break;
                case Append:
                    if (state == RState.Follower) {
                        leaderId = msg.getOriginator();
                        currentTerm = msg.getTerm();
                        resetElectionTimer();
                    }
                    else if (state == RState.Candidate) {
                        if (currentTerm < msg.getTerm()) {
                            state = RState.Follower;
                            currentTerm = msg.getTerm();
                            stopHeartBeatTimer();
                            startElectionTimer();
                            System.out.println(getNodeId() + " Election timeout period : " + electionTimeout);
                        }
                    }
                    break;
                case Leader:
                    if (currentTerm < msg.getTerm()) {
                        resetElectionTimer();
                        System.out.println(getNodeId() + " Election timeout period : " + electionTimeout);
                        leaderId = msg.getOriginator();
                        currentTerm = msg.getTerm();
                    }
                    break;
                case RequestVote:
                    if (state == RState.Follower) {
                        if (currentTerm < msg.getTerm()) {
                            if (lastVoteTerm < msg.getTerm()) {
                                votedFor = msg.getOriginator();
                                lastVoteTerm = msg.getTerm();
                                voteForCandidate(msg);
                                resetElectionTimer();
                            }
                        }
                    }
                    break;
                case EmptyAppend:
                    if (state == RState.Follower) {
                        resetElectionTimer();
                    }
                    break;
            }
        }

        private void voteForCandidate(RaftMessage voteRequestMsg)
        {
            RaftMessage msg = new RaftMessage(Raft.msgID.incrementAndGet());
            msg.setOriginator(getNodeId());
            msg.setDeliverAs(Delivery.Direct);
            msg.setDestination(voteRequestMsg.getOriginator());
            msg.setTerm(currentTerm);
            msg.setAction(RaftMessage.Action.Vote);
            send(msg);

            System.out.println("Node " + getNodeId() + " Voted for Candidate " + voteRequestMsg.getOriginator());
        }

        private synchronized void receivedVote()
        {
            System.out.println("Node " + getNodeId() + " Received vote");
            voteCount++;
            if (voteCount > ((numOfServers / 2) + 1)) {
                voteCount = 0;
                sendLeaderNotice();
                leaderId = getNodeId();
                state = RState.Leader;
                System.out.println("Leader elected -> " + leaderId);
                startHeartBeatTimer();
                stopElectionTimer();
                counter = 0;
            }
        }

        public void setTransport(Bus<? extends RaftMessage> t)
        {
            this.transport = t;
        }

        private synchronized void startHeartBeatTimer()
        {
            heartBeatTimeout = 1000; // must be less then electionTimeout
            raftMonitor = new RaftMonitor((RaftNode<RaftMessage>) this);
            heartBeatTimer = new Timer();
            heartBeatTimer.scheduleAtFixedRate(raftMonitor, heartBeatTimeout, 1);
        }

        private synchronized void stopHeartBeatTimer()
        {
            if (heartBeatTimer != null) {
                heartBeatTimer.cancel();
                heartBeatTimer.purge();
            }
        }

        private synchronized void startElectionTimer()
        {
            //electionTimeout = new Random().nextInt(7000 + 1 - 5000) + 5000;
            electionTimeout = new Random().nextInt(10000);
            if (electionTimeout < 5000)
                electionTimeout += 3000;
            electionMonitor = new ElectionMonitor((RaftNode<RaftMessage>) this);
            electionTimer = new Timer();
            electionTimer.scheduleAtFixedRate(electionMonitor, electionTimeout, 1);

        }

        private synchronized void resetElectionTimer()
        {
            if (electionTimer != null) {
                electionTimer.cancel();
                startElectionTimer();
            }
        }

        private synchronized void stopElectionTimer()
        {
            if (electionTimer != null) {
                electionTimer.cancel();
                electionTimer.purge();
            }
        }

        private synchronized void startElection()
        {
            state = RState.Candidate;
            voteCount = 1;
            currentTerm++;
            sendRequestVoteNotice();
            resetElectionTimer();
            System.out.println("Election started by Node " + getNodeId() + " Term " + currentTerm);
        }

        public enum RState {
            Follower, Candidate, Leader
        }
    }
}
