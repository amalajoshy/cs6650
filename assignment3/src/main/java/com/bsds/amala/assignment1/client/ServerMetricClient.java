package com.bsds.amala.assignment1.client;

import com.bsds.amala.assignment1.server.dao.MetricsDataDAO;
import com.bsds.amala.assignment1.util.MathUtil;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;

import java.io.IOException;
import java.util.List;

public class ServerMetricClient {

    @Option(name = "-s", aliases = {"--hostname"}, usage = "server hostname", required = false)
    private String hostname;

    @Option(name = "-l", aliases = {"--latencyMetric"}, usage = "latency metric type", required = false)
    private boolean latencyMetric;

    @Option(name = "-e", aliases = {"--errorMetric"}, usage = "error metric type", required = false)
    private boolean errorMetric;

    @Option(name = "-d", aliases = {"--dbMetric"}, usage = "db latency metric", required = false)
    private boolean dbMetric;

    @Option(name = "-a", aliases = {"--allHosts"}, usage = "All host metric", required = false)
    private boolean allHostMetric;


    @Option(name = "-h", aliases = {"--help"}, usage = "print usage", help = true)
    private boolean printUsage = false;

    public static void main(String[] args) throws IOException, CmdLineException {
        new ServerMetricClient().doMain(args);
    }

    public void doMain(String[] args) throws IOException, CmdLineException {

        MetricsDataDAO metricsDataDAO = new MetricsDataDAO();
        System.out.println("List of servers, choose any of these hostnames for the cmd line argument to get the metrics :" );
        List<String> hostnames =  metricsDataDAO.getHostnames();
        for (String hostname : hostnames) {
            System.out.println(hostname);
        }

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

        String metricType = "LATENCY";

        if (errorMetric) {
            System.out.println("Count of error in requests : " + metricsDataDAO.getErrorCount());
        } else {
                if (dbMetric) {
                    metricType = "DB";
                }

                if (allHostMetric) {
                    List<Long> metricValues = metricsDataDAO.getLatencyMetricListForAllHosts(metricType);
                    System.out.println("\nFinding median latency for server all servers : \nMetric type : " + metricType);
                    printMetricDetails(metricValues);

                } else {
                    List<Long> metricValues = metricsDataDAO.getLatencyMetricList(hostname, metricType);
                    System.out.println("\nFinding median latency for server : " + hostname + "\nMetric type : " + metricType);
                    printMetricDetails(metricValues);
                }

            }
    }

    public void printMetricDetails(List<Long> metricValues) {
        Long median = MathUtil.findMedian(metricValues);
        Long mean = MathUtil.findMean(metricValues);
        Long p99 = MathUtil.findPercentile(metricValues, 0.99);
        Long p95 = MathUtil.findPercentile(metricValues, 0.95);
        System.out.println("Median latency : " + median + "ms\nMean latency :" + mean + "ms\n99th percentile :" + p99 +"ms\n95th percentile :" +p95 +"ms");

    }

}
