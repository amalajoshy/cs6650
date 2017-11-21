package com.bsds.amala.assignment1.util;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Map;

public class PlotLatency {

    public static void getStepChart(String title, Map<Long, Long> latencyMap, int clientThreadCount,
                                    int serverThreadCount, int dbPoolSize, long startTime, String operation) {
        String csvFileName = "/tmp/latency-" + operation + "-" + clientThreadCount + "-"
                + serverThreadCount + "-" + dbPoolSize + ".csv";
        PrintWriter writer;
        try {
            writer = new PrintWriter(csvFileName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TimeSeriesCollection timeSeriesCollection = new TimeSeriesCollection();
        TimeSeries ts = new TimeSeries("Time (ms)");
        latencyMap.forEach((time, latency) -> {
            long pointOfTime = time - startTime;
            writer.println(pointOfTime + "," + latency);
            ts.addOrUpdate(new Millisecond(new Date(pointOfTime)), latency);
        });
        writer.close();
        timeSeriesCollection.addSeries(ts);

        String xAxisLabel = "Time";
        String yAxisLabel = String.format("Latency - %d client threads, %d server thread pool, %d DB conn. pool",
                clientThreadCount, serverThreadCount, dbPoolSize);
        JFreeChart chart = ChartFactory.createXYLineChart(title, xAxisLabel, yAxisLabel, timeSeriesCollection,
                PlotOrientation.VERTICAL, true, true, false);
        chart.setBackgroundPaint(Color.WHITE);
        chart.getPlot().setBackgroundPaint(Color.WHITE);
        ((XYPlot) chart.getPlot()).setDomainGridlinePaint(Color.LIGHT_GRAY);
        ((XYPlot) chart.getPlot()).setRangeGridlinePaint(Color.LIGHT_GRAY);

        File imageFile = new File("/tmp/XYLineChart.png");
        int width = 1280;
        int height = 640;

        try {
            ChartUtilities.saveChartAsPNG(imageFile, chart, width, height);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

}
