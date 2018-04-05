package gash.leaderelection;

import gash.leaderelection.raft.Raft;
import gash.leaderelection.raft.Raft.RaftNode;
import gash.leaderelection.raft.RaftMessage;
import org.junit.Test;

public class RaftTest
{
    @Test
    public void testStartup() throws Exception
    {
        int numOfServers = 9;
        Raft raft = new Raft();
        for (int i = 0; i < numOfServers; i++) {
            RaftNode<RaftMessage> raftNode = new RaftNode<>(i);
            raft.addNode(raftNode);
        }
        Thread.sleep(30000);
    }
}
