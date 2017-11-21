//package com.bsds.amala.assignment1.client;
//
//import com.bsds.amala.assignment1.util.MathUtil;
//import org.kohsuke.args4j.CmdLineException;
//import org.kohsuke.args4j.CmdLineParser;
//import org.kohsuke.args4j.Option;
//
//import javax.ws.rs.client.Client;
//import javax.ws.rs.client.ClientBuilder;
//import javax.ws.rs.client.WebTarget;
//import javax.ws.rs.core.Response;
//import java.io.IOException;
//import java.time.Instant;
//import java.util.Collection;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//import java.util.concurrent.TimeUnit;
//
//public class Main {
//
//    @Option(name = "-t", usage = "no of threads")
//    private int numberOfThreads = 10;
//
//    @Option(name = "-n", usage = "no of iterations")
//    private int numberOfIterations = 100;
//
//    @Option(name = "-i", aliases = {"--ip"}, usage = "ip address", required = true)
//    private String ipAddress;
//
//    @Option(name = "-p", aliases = {"--port"}, usage = "port")
//    private int port = 8080;
//
//    @Option(name = "-h", aliases = {"--help"}, usage = "print usage", help = true)
//    private boolean printUsage = false;
//
//    Map<String, Long> executionTimeMap = new HashMap<>();
//
//    private int failedCalls = 0;
//
//    public static void main(String[] args) throws IOException, CmdLineException {
//        new Main().doMain(args);
//    }
//
//    public void doMain(String[] args) throws IOException, CmdLineException {
//        CmdLineParser cmdLineParser = new CmdLineParser(this);
//
//        try {
//            cmdLineParser.parseArgument(args);
//        } catch (CmdLineException e) {
//            cmdLineParser.printUsage(System.err);
//            System.exit(1);
//        }
//
//        if (printUsage) {
//            cmdLineParser.printUsage(System.out);
//            System.exit(0);
//        }
//
//        Client client = ClientBuilder.newClient();
//        WebTarget localhostWebTarget = client.target("http://" + ipAddress + ":" + port + "/tomcat-war-deployment/myresource");
//
//        WebClient webClient = new WebClient(localhostWebTarget);
//        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
//
//        Instant startTime = Instant.now();
//        System.out.println("Client starting :" + startTime);
//
//        for (int i = 0; i < numberOfThreads; i++) {
//            final int threadNum = i;
//            executor.submit(() -> {
//                for (int j = 0; j < numberOfIterations; j++) {
//                    long runtimeGetMethodLatency = invokeAndTimeClientMethod("GET", webClient);
//                    String threadInfoKeyGet = "Thread Number : " + threadNum + " Thread Iteration :" + j + " HTTP GET";
//                    executionTimeMap.put(threadInfoKeyGet, runtimeGetMethodLatency);
//
//                    String threadInfoKeyPost = "Thread Number : " + threadNum + " Thread Iteration :" + j + " HTTP POST";
//                    long runtimePostMethodLatency = invokeAndTimeClientMethod("POST", webClient);
//                    executionTimeMap.put(threadInfoKeyPost, runtimePostMethodLatency);
//                }
//            });
//        }
//        System.out.println("All threads running ....");
//        executor.shutdown();
//        try {
//            executor.awaitTermination(60, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
//
//        Instant endTime = Instant.now();
//        System.out.println("All threads complete :" + endTime);
//        long totalRuntime = endTime.toEpochMilli() - startTime.toEpochMilli();
//
//        reportStats(totalRuntime);
//        System.exit(0);
//    }
//
//    private void reportStats(long totalRuntimeMs) {
//        int numberOfRequest = numberOfThreads * numberOfIterations * 2;
//
//
//        System.out.println("Total number of request sent :" +  numberOfRequest);
//        System.out.println("Total number of Successful calls: " + (numberOfRequest - failedCalls));
//        System.out.println("Total run time for all threads to complete :" + totalRuntimeMs + " ms");
//
//        Collection<Long> latencyValues = executionTimeMap.values();
//        System.out.println("Median latency: " + MathUtil.findMedian(latencyValues) + " ms");
//        System.out.println("Mean latency: " + MathUtil.findMean(latencyValues) + " ms");
//        System.out.println("95th percentile :" + MathUtil.findPercentile(latencyValues, 0.95) + " ms");
//        System.out.println("99th percentile :" + MathUtil.findPercentile(latencyValues, 0.99) + " ms");
//    }
//
//    private long invokeAndTimeClientMethod(String methodName, WebClient webClient) {
//        Response response;
//
//        Instant startTime = Instant.now();
//        if (methodName.equals("GET")) {
//            response = webClient.get();
//        } else {
//            response = webClient.post("hello world");
//        }
//        Instant endTime = Instant.now();
//
//        if (response.getStatus() != 200) {
//            synchronized (this) {
//                failedCalls++;
//            }
//        }
//
//        return endTime.toEpochMilli() - startTime.toEpochMilli();
//    }
//}
