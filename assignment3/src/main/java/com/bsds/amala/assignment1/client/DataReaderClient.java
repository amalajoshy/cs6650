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

public class DataReaderClient {

    @Option(name = "-t", usage = "no of threads")
    private int numberOfThreads = 100;

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


    public static void main(String[] args) {
        new DataReaderClient().doMain(args);
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
        int numReadThreads = 100;
        ExecutorService readExecutor = Executors.newFixedThreadPool(numReadThreads);
        ExecutorService statsCollectionExecutor = Executors.newSingleThreadExecutor();
        final Map<Long, Long> readLatencyMap = new ConcurrentHashMap<>(1 << 18);

        System.out.println("====== Reading Skier Stats =====");
        final long startTime = System.currentTimeMillis();
        System.out.println("Read Start time: " + new Date(startTime));
        for (int i = 0; i < numReadThreads; i++) {
            final int factor = i;
            readExecutor.submit(() -> {
                for (int j = 1; j <= 400; j++) {
                    int skierId = 400 * factor + j;
                    WebTarget readWebTarget = client
//                                .target("http://localhost:8080/assignment1/lift/myvert?dayNum=1&skierId=" + skierId);
                            .target("http://bsds-elb-550199777.us-west-2.elb.amazonaws.com/tomcat-war-deployment/lift/myvert?dayNum=4&skierId=" + skierId);
                    WebClient webClient = new WebClient(readWebTarget, statsCollectionExecutor, readLatencyMap);
                    webClient.get();
//                        System.out.println(webClient.get().readEntity(String.class));
                }
            });
        }
        readExecutor.shutdown();
        try {
            readExecutor.awaitTermination(60, TimeUnit.MINUTES);
            long endTime = System.currentTimeMillis();
            long totalRuntime = endTime - startTime;
            System.out.println("Read End time: " + new Date(endTime));
            System.out.println("All read threads complete :" + totalRuntime);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        statsCollectionExecutor.shutdown();
        try {
            statsCollectionExecutor.awaitTermination(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Collection<Long> latencyValues = readLatencyMap.values();
        System.out.println("Client Median latency: " + MathUtil.findMedian(latencyValues) + " ms");
        System.out.println("Client Mean latency: " + MathUtil.findMean(latencyValues) + " ms");
        System.out.println("Client 95th percentile :" + MathUtil.findPercentile(latencyValues, 0.95) + " ms");
        System.out.println("Client 99th percentile :" + MathUtil.findPercentile(latencyValues, 0.99) + " ms");

        int dbPoolSize = 32;
        int serverThreads = 50;
        PlotLatency.getStepChart("Read Latency graph", readLatencyMap, numReadThreads, serverThreads, dbPoolSize, startTime, "read");
    }
}



