package data;

import com.cmpe275.grpcComm.*;
import gash.obs.madis.MesonetProcessor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.io.File;
import java.util.Iterator;
import java.util.Scanner;

public class JavaClient
{
    public static void main(String[] args)
    {

        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter task [get / put / ping]: ");
        String task = scanner.next();


        final ManagedChannel channel = ManagedChannelBuilder.forTarget("127.0.0.1:8080") //ManagedChannelBuilder.forTarget("169.254.79.93:8080")
                .usePlaintext(true)
                .build();
        CommunicationServiceGrpc.CommunicationServiceBlockingStub stub = CommunicationServiceGrpc.newBlockingStub(channel);
        CommunicationServiceGrpc.CommunicationServiceStub asyncStub = CommunicationServiceGrpc.newStub(channel);

        switch (task.toLowerCase()) {
            case "ping":

                PingRequest pingRequest = PingRequest.newBuilder()
                        .setMsg("Sample Ping Request")
                        .build();

                Request request = Request.newBuilder()
                        .setFromSender("from sender")
                        .setToReceiver("to Receiver")
                        .setPing(pingRequest)
                        .build();

                System.out.println("Sending Ping request....\n\n");
                Response response = stub.ping(request);
                System.out.println(response);

                break;
            case "get":
            	String from_time = scanner.nextLine();
                System.out.print("\nEnter from_time 'yyyy-MM-dd HH:mm:ss': ");
                String to_time = scanner.nextLine();
                System.out.print("\nEnter to_time 'yyyy-MM-dd HH:mm:ss': ");
                

                QueryParams queryParams = QueryParams.newBuilder()
                        .setFromUtc(from_time)
                        .setToUtc(to_time)
                        //.setFromUtc("2017/01/01 00:00:00") *** Test for data not present
                        // .setToUtc("2018/01/01 00:00:00")
                        .build();

                MetaData metadata = MetaData.newBuilder()
                        .setUuid("12365")
                        .setNumOfFragment(1)
                        .setMediaType(1)
                        .build();

                GetRequest getRequest = GetRequest.newBuilder()
                        .setMetaData(metadata)
                        .setQueryParams(queryParams)
                        .build();
                request = Request.newBuilder()
                        .setFromSender("from sender")
                        .setToReceiver("to Receiver")
                        .setGetRequest(getRequest)
                        .build();

                Iterator<Response> getResponse = stub.getHandler(request);
                while (getResponse.hasNext()) {
                    String responseData = getResponse.next().getDatFragment().getData().toStringUtf8();
                    System.out.println(responseData);
                }


                break;
            case "put":
                System.out.print("Enter directory path: ");
                String path = scanner.next();

                MesonetProcessor mesonetProcessor = new MesonetProcessor(channel, asyncStub);

                File folder = new File(path);
                File[] listOfFiles = folder.listFiles();

                for (File listOfFile : listOfFiles) {
                    if (listOfFile.getName().lastIndexOf(".") != -1 && !listOfFile.getName().equals(".DS_Store")) {
                        String name = listOfFile.getName().substring(0, listOfFile.getName().lastIndexOf("."));
                        System.out.println(name);
                        mesonetProcessor.main(new String[]{listOfFile.toString(), "./catalog.csv", "./output"});
                    }
                }

                break;
            default:
                System.out.println("Wrong argument value.. Please try again with arguments ping/get/put");
        }
    }
}
