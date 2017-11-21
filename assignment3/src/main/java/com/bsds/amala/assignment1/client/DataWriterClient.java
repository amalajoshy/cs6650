package com.bsds.amala.assignment1.client;


import com.bsds.amala.assignment1.model.RFIDLiftData;
import com.bsds.amala.assignment1.util.MathUtil;
import com.bsds.amala.assignment1.util.PlotLatency;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DataWriterClient {

    @Option(name = "-t", usage = "no of threads")
    private int numberOfThreads = 150;

    @Option(name = "-i", aliases = {"--ip"}, usage = "ip address", required = false)
    private String ipAddress;

    @Option(name = "-p", aliases = {"--port"}, usage = "port")
    private int port = 8080;

    @Option(name = "-h", aliases = {"--help"}, usage = "print usage", help = true)
    private boolean printUsage = false;

    Map<String, Long> executionTimeMap = new HashMap<>();

    private int failedCalls = 0;

    private Function<String, RFIDLiftData> mapToItem = (line) -> {
        String[] entries = line.split(",");
        RFIDLiftData item = new RFIDLiftData(Integer.parseInt(entries[0]),
                Integer.parseInt(entries[1]),
                Integer.parseInt(entries[2]),
                Integer.parseInt(entries[3]),
                Integer.parseInt(entries[4]));
        return item;
    };

    private List<RFIDLiftData> processInputFile(InputStream inputStream) {
        // read data from csv file
        List<RFIDLiftData> rfidLiftDatas;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            // skip the header of the csv
            rfidLiftDatas = br.lines().skip(1).map(mapToItem).collect(Collectors.toList());
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
        return rfidLiftDatas;
    }

    public static void main(String[] args) {
        new DataWriterClient().doMain(args);
    }

    public void doMain(String[] args) {

        CmdLineParser cmdLineParser = new CmdLineParser(this);

        try {
            cmdLineParser.parseArgument(args);
        } catch (CmdLineException e) {
            cmdLineParser.printUsage(System.err);
            System.exit(1);
        }

        if (printUsage) {
            cmdLineParser.printUsage(System.out);
            System.exit(0);
        }

        Client client = ClientBuilder.newClient();
//       WebTarget writeDataWebTarget = client.target("http://localhost:8080/assignment1/lift/load");
        WebTarget writeDataWebTarget = client.target("http://bsds-elb-550199777.us-west-2.elb.amazonaws.com/tomcat-war-deployment/lift/load");
//       WebTarget writeDataWebTarget = client.target("http://localhost:8080/tomcat-war-deployment/lift/load");

        ExecutorService writeExecutor = Executors.newFixedThreadPool(numberOfThreads);
        ExecutorService statsCollectionExecutor = Executors.newSingleThreadExecutor();
        final Map<Long, Long> writeLatencyMap = new ConcurrentHashMap<>(1 << 18);
        WebClient writeDataClient = new WebClient(writeDataWebTarget, statsCollectionExecutor, writeLatencyMap);


        List<RFIDLiftData> rfidLiftDataList = processInputFile(getClass().getResourceAsStream("../../../../../BSDSAssignment2Day4.csv"));
        System.out.println("Read csv: " + rfidLiftDataList.size() + " entries");

        System.out.println("====== Writing Skier data =====");
        final long startTime = System.currentTimeMillis();
        System.out.println("Write Start time: " + new Date(startTime));
        for (RFIDLiftData liftData : rfidLiftDataList) {
            writeExecutor.submit(() -> writeDataClient.post(liftData.toJson()));
        }
        writeExecutor.shutdown();
        try {
            writeExecutor.awaitTermination(60, TimeUnit.MINUTES);
            long endTime = System.currentTimeMillis();
            long totalRuntime = endTime - startTime;
            System.out.println("Write End time: " + new Date(endTime));
            System.out.println("All write threads complete :" + totalRuntime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        statsCollectionExecutor.shutdown();
        try {
            statsCollectionExecutor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Collection<Long> latencyValues = writeLatencyMap.values();
        System.out.println("Client Median latency: " + MathUtil.findMedian(latencyValues) + " ms");
        System.out.println("Client Mean latency: " + MathUtil.findMean(latencyValues) + " ms");
        System.out.println("Client 95th percentile :" + MathUtil.findPercentile(latencyValues, 0.95) + " ms");
        System.out.println("Clinet 99th percentile :" + MathUtil.findPercentile(latencyValues, 0.99) + " ms");

        int dbPoolSize = 32;
        int serverThreads = 50;
        PlotLatency.getStepChart("Write Latency graph", writeLatencyMap, numberOfThreads, serverThreads, dbPoolSize, startTime, "write");
    }
}



