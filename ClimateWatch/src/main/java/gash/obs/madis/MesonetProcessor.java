package gash.obs.madis;

import com.cmpe275.grpcComm.*;
import com.google.protobuf.ByteString;
import io.grpc.ManagedChannel;
import io.grpc.stub.StreamObserver;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.awt.*;
import java.io.File;
import java.io.FileFilter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MesonetProcessor
{

    /**
     * file name prefix for general data
     */
    public static final String sGeneralTag = "madis-mesonet";

    int maxChunkSize = 10;
    int chunkSize = 0;
    StringBuilder requestPayload = new StringBuilder();
    StreamObserver<Request> requestObs;

    ManagedChannel channel;
    CommunicationServiceGrpc.CommunicationServiceStub asyncStub;

    Request request;

    public MesonetProcessor(File catalogF, File outputDir)
    {
        // TODO read catalog
        // TODO read data

        // TODO if needed, save catalog w/ new stations
    }

    public MesonetProcessor(ManagedChannel managedChannel, CommunicationServiceGrpc.CommunicationServiceStub asyncStub)
    {
        this.channel = managedChannel;
        this.asyncStub = asyncStub;
    }

    /**
     * assumption: data is received once every 15 minutes or so.
     * <p>
     * This will perform batch processing.
     *
     * @param args
     */
    public void main(String[] args)
    {
        if (args.length != 3) {
            System.out.println("Usage: dataSource catalogFile outputDir");
            System.exit(2);
        }

        File dataSource = new File(args[0]);
        File catF = new File(args[1]);
        File outdir = new File(args[2]);

        String name = dataSource.getName().substring(0, dataSource.getName().lastIndexOf("."));
        ;
        DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyyMMdd_HHmm");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd/HHmm");
        DateTime dt = formatter.parseDateTime(name);

        // filters
        Date startDate = null;
        Date endDate = null;
        Rectangle region = null;
        Set<String> stationIDs = null;

        System.out.println("\n\nSource: " + dataSource + "\nCatalog: " + catF.getAbsolutePath() + "\nOutput: "
                + outdir.getAbsolutePath());

        long startTime = System.currentTimeMillis();
        try {
            Catalog cat = new Catalog();
            if (!cat.load(catF)) {
                System.out.println("-- new catalog file created");
            }

            MesonetReader rawReader = new MesonetReader();

            /**
             * note use readFile() to perform the same steps
             */

            if (!dataSource.exists())
                return;
            if (dataSource.isFile()) {
                List<Station> stations = rawReader.extractCatalog(dataSource);
                if (stations != null) {
                    for (Station s : stations)
                        cat.addStation(s);
                }


                List<MesonetData> data = rawReader.extract(dataSource, startDate, endDate, region, stationIDs);
                System.out.println("processed " + data.size() + " entries");

                // now do something with the data
                // 1. send to the cluster or cloud

                StreamObserver<Response> responseObserver = new StreamObserver<Response>()
                {
                    @Override
                    public void onNext(Response resp)
                    {
                        System.out.println("Sent data push request. Checking for disk Space....");
                        System.out.println(resp.getMsg());
                    }

                    @Override
                    public void onError(Throwable t)
                    {
                        System.out.println("Pushing data request failed. Please try again..!");
                        System.out.println(t.getMessage());
                    }

                    @Override
                    public void onCompleted()
                    {
                        System.out.println("Finished Pushing data....");
                        channel.shutdownNow();
                    }
                };
                requestObs = asyncStub.putHandler(responseObserver);

                for (MesonetData d : data) {
                    String line = d.getStationID() + "," + simpleDateFormat.format(d.getTimeObsAsDate()) + "," + d.getDataProvider() + "," + d.getLatitude() + "," + d.getLongitude() + "," + d.getElevation() + "," + d.getTemperature() + "," + d.getWindSpeed() + "," + d.getWindDir() + "," + d.getWindGust() + "," + d.getSeaLevelPress() + "," + d.getAltimeter() + "," + d.getDewpoint() + "," + d.getRelHumidity() + ",-9999.00,-9999.00\n";

//                    System.out.print(line);

                    chunkSize++;
                    requestPayload.append(line);

                    if (chunkSize == maxChunkSize) {
                        chunkSize = 0;

                        //send data
                        MetaData pmetadata = MetaData.newBuilder()
                                .setUuid(UUID.randomUUID().toString())
                                .setNumOfFragment(1)
                                .setMediaType(1)
                                .build();

                        DatFragment dataFragment = DatFragment.newBuilder()
                                .setData(ByteString.copyFromUtf8(requestPayload.toString()))
                                .build();

                        PutRequest putRequest = PutRequest.newBuilder()
                                .setDatFragment(dataFragment)
                                .setMetaData(pmetadata)
                                .build();

                        request = Request.newBuilder()
                                .setFromSender("from sender")
                                .setToReceiver("to Receiver")
                                .setPutRequest(putRequest)
                                .build();

                        requestObs.onNext(request);
                        Thread.sleep(1000);

                        requestPayload = new StringBuilder();
                    }
                }
            }
            else {
                FileFilter filter = new FileFilter()
                {
                    public boolean accept(File pathname)
                    {
                        return (pathname.isFile() && !pathname.getName().startsWith(".")
                                && !pathname.getName().endsWith(".gz"));
                    }
                };

                // TODO walk through accepted files and process
                System.out.println("TODO: process files");

            }

            if (chunkSize != 0) {
                chunkSize = 0;

                //send data
                MetaData pmetadata = MetaData.newBuilder()
                        .setUuid(UUID.randomUUID().toString())
                        .setNumOfFragment(1)
                        .setMediaType(1)
                        .build();

                DatFragment dataFragment = DatFragment.newBuilder()
                        .setData(ByteString.copyFromUtf8(requestPayload.toString()))
                        .build();

                PutRequest putRequest = PutRequest.newBuilder()
                        .setDatFragment(dataFragment)
                        .setMetaData(pmetadata)
                        .build();

                request = Request.newBuilder()
                        .setFromSender("from sender")
                        .setToReceiver("to Receiver")
                        .setPutRequest(putRequest)
                        .build();

                requestObs.onNext(request);
                Thread.sleep(1000);

                requestPayload = new StringBuilder();
            }

            Thread.sleep(1000);
            requestObs.onCompleted();
            System.out.println("Complete called");
            // save catalog
            cat.save(catF);

            long stopTime = System.currentTimeMillis();
            System.out.println(
                    "MADIS Mesonet - total processing time is " + ((stopTime - startTime) / 1000.0) + " seconds");
        } catch (Throwable t) {
            System.out.println(
                    "Unable to process mesowest data in " + dataSource.getAbsolutePath() + ": " + t.getMessage());
        }
    }
}
