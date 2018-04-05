# RAFT
Steps to test
1. Run test.gash.leaderelection.RaftTest


### Parameters

`numOfServers` - Changes number of number of server nodes. _(Change in both test and Raft file)_

`DEBUG = true` will cause the Leader to stop sending out heartbeats after sending out `4` heartbeats. Resulting in an election timeout and a subsequent leader election.  
