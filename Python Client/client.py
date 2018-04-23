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

# Update this with your IP and server IP before running the client
sender_ip = '127.0.0.1'
receiver_ip = '127.0.0.1'


class client(object):
    def __init__(self, host=receiver_ip, port=8080):
        channel = grpc.insecure_channel('%s:%d' % (host, port))
        self.stub = data_pb2_grpc.CommunicationServiceStub(channel)
        if self.stub is not None:
            print('Connection established with GRPC server.....\n')

    def ping(self, message):
        """
        Pings the server
        """
        print("Logger : Sending Ping request to " + receiver_ip)
        req = Request(
            fromSender=sender_ip,
            toReceiver=receiver_ip,
            ping=PingRequest(msg=message))
        resp = self.stub.ping(req)
        print(resp.msg)

    def get(self, from_time, to_time, params_array):
        """
        Sends a get request to the server
        """

        print("Logger : Sending GET request to " + receiver_ip)
        req = Request(
            fromSender=sender_ip,
            toReceiver=receiver_ip,
            getRequest=GetRequest(
                metaData=MetaData(uuid='12345'),
                queryParams=QueryParams(from_utc=from_time, to_utc=to_time, params_json=params_array)))
        for resp in self.stub.getHandler(req):
            print(resp.datFragment.data.decode('utf-8'))

    def stream_putreq(self, recordlist):
        """
        Streams request to push data to server
        """

        id = str(uuid.uuid1())
        yield Request(
            fromSender=sender_ip,
            toReceiver=receiver_ip,
            putRequest=PutRequest(
                metaData=MetaData(uuid=id, mediaType=1),
                datFragment=DatFragment(data=recordlist.encode())))


def parse_and_push_files(path, clientobj):
    """
    Parses and streams push request
    """

    if len(os.listdir(path)) == 0:
        print("No files in the directory to push....Exiting....\n")
    else:
        maxChunkSize = 10
        chunkSize = 0
        requestPayload = ""
        chunksProcessed = 0
        totalLines = 0

        print("Logger : Streaming Push request to " + receiver_ip)
        for filename in os.listdir(path):
            print(filename)
            with open(path+filename, "r") as f:
                lines = f.readlines()
                totalLines = len(lines)
                for i in range(4, len(lines)):
                    if len(lines[i]) != 0:

                        #Old format with spaces
                        #requestPayload += lines[i]
                        #TODO: Uncomment this when parsing logic is changed.
                        requestPayload += format_data(lines[i]) + '\n'
                        chunkSize += 1
                        if chunkSize == maxChunkSize or i == len(lines)-1:
                            chunkSize = 0
                            iterator = clientobj.stream_putreq(recordlist=requestPayload)
                            resp = clientobj.stub.putHandler(iterator)
                            print(resp.msg)
                            print(requestPayload)
                            requestPayload = ""
                            chunksProcessed += 1
        print("Total lines :" + str(totalLines))
        print("Total chunks :" + str(chunksProcessed))


def format_data(line):
    """
    Formats data input to send in the request
    """

    cols = line.split()
    timestamp = format_timestamp(cols[1])
    return ",".join(cols[:1] + [timestamp] + cols[2:])


def format_timestamp(timestamp):
    """
    Formats timestamp for request
    """

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
            print("Enter the total number of filter parameters :")
            param = input()
            param_json = ""
            if param > 0:
                param_json += "["
                for i in range(0, param):
                    param_json += "{'lhs':'"
                    print("Enter the parameter " + str(i+1) + " name : ")
                    name = str(input())
                    param_json += name + "', 'op':'"
                    print("Enter the parameter " + str(i+1) + " operator : ")
                    op = str(input())
                    param_json += op + "', 'rhs':'"
                    print("Enter the parameter " + str(i+1) + " value : ")
                    value = str(input())
                    if i == param-1:
                        param_json += value + "'}"
                    else:
                        param_json += value + "'},"
                param_json += "]"
                print("Params json = " + param_json)

            clientobj.get(from_time=frm, to_time=to, params_array=param_json)
            #clientobj.get(from_time="2018-03-21 01:00:00", to_time="2018-03-21 01:20:00")

        if sys.argv[1] == "put":
            print("Enter the folder location where you have the files to be pushed."
                  "Please note this python client only accepts mesowest files  :")
            path = str(input())
            print(path)
            parse_and_push_files(path, clientobj)
        if sys.argv[1] not in args:
            print("Wrong argument value.. Please try again with arguments ping/get/put")

if __name__ == '__main__':
  main()


