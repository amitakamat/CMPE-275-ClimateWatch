"""
    Client file to interact with the system.
    Provides the feature to ping, get data or push data to our cluster.
    Amita Kamat
"""

import data_pb2_grpc
from data_pb2 import PingRequest, Request, GetRequest, MetaData, QueryParams, PutRequest, DatFragment
import grpc
import sys
import os
import uuid


class client(object):
    def __init__(self, host='169.254.21.84', port=8080):
        channel = grpc.insecure_channel('%s:%d' % (host, port))
        self.stub = data_pb2_grpc.CommunicationServiceStub(channel)
        if self.stub is not None:
            print('Connection established with GRPC server.....\n')

    def ping(self, message):
        req = Request(
            fromSender="sender",
            toReceiver="receiver",
            ping=PingRequest(msg=message))
        resp = self.stub.ping(req)
        print(resp.msg)

    def get(self, from_time, to_time):
        req = Request(
            fromSender="sender",
            toReceiver="receiver",
            getRequest=GetRequest(
                metaData=MetaData(uuid='12345'),
                queryParams=QueryParams(from_utc=from_time, to_utc=to_time)))
        for resp in self.stub.getHandler(req):
            print(resp)

    def stream_putreq(self, recordlist):
        id = str(uuid.uuid1())
        yield Request(
            fromSender="sender",
            toReceiver="receiver",
            putRequest=PutRequest(
                metaData=MetaData(uuid=id, mediaType=1),
                datFragment=DatFragment(data=recordlist.encode())))



def parse_and_push_files(path, clientobj):
    if len(os.listdir(path)) == 0:
        print("No files in the directory to push....Exiting....\n")
    else:
        maxChunkSize = 10
        chunkSize = 0
        requestPayload = ""
        chunksProcessed = 0
        totalLines = 0

        for filename in os.listdir(path):
            print(filename)
            with open(path+filename, "r") as f:
                lines = f.readlines()
                totalLines = len(lines)
                for i in range(4, len(lines)):
                    if len(lines[i]) != 0:
                        requestPayload += format_data(lines[i]) + '\n'
                        chunkSize += 1
                        if chunkSize == maxChunkSize or i == len(lines)-1:
                            chunkSize = 0;
                            #iterator = clientobj.stream_putreq(recordlist=requestPayload)
                            #resp = clientobj.stub.putHandler(iterator)
                            #print(resp.msg)
                            print(requestPayload)
                            requestPayload = ""
                            chunksProcessed += 1
        print("Total lines :" + str(totalLines))
        print("Total chunks :" + str(chunksProcessed))


def format_data(line):
    cols = line.split()
    timestamp_utc = format_timestamp(cols[1])
    return ",".join(cols[:1] + [timestamp_utc] + cols[2:])


def format_timestamp(timestamp):
    tuples = timestamp.split('/')
    year = tuples[0][:4]
    month = tuples[0][4:6]
    day = tuples[0][6:8]
    hour = tuples[1][:2]
    minute = tuples[1][2:4]

    return '%s-%s-%s %s:%s:00' % (year, month, day, hour, minute)



def main():
    print(len(sys.argv))
    if len(sys.argv) == 1:
        print("No arguments found....Please try again with arguments ping/get/put")
    if len(sys.argv) == 2:
        args = ["ping", "get", "put"]
        clientobj = client()
        if sys.argv[1] == "ping":
            clientobj.ping(message="Sample Ping Request")

        if sys.argv[1] == "get":

            # TODO: Validate input time format
            print("Enter from_time ('yyyy-MM-dd HH:mm:ss') :")
            frm = str(input())
            print("Enter to_time ('yyyy-MM-dd HH:mm:ss') :")
            to = str(input())
            clientobj.get(from_time=frm, to_time=to)
            #clientobj.get(from_time="2018-03-21 01:00:00", to_time="2018-03-21 01:20:00")

        if sys.argv[1] == "put":
            print("Enter the folder location where you have the files to be pushed (Please enter the path in quotes) :")
            path = str(input())
            print(path)
            parse_and_push_files(path, clientobj)
        if sys.argv[1] not in args:
            print("Wrong argument value.. Please try again with arguments ping/get/put")

if __name__ == '__main__':
  main()


