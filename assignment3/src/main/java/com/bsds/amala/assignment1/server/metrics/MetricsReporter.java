package com.bsds.amala.assignment1.server.metrics;

import com.bsds.amala.assignment1.server.dao.MetricsDataDAO;
import com.bsds.amala.assignment1.server.db.DatabaseConnectionPool;
import com.bsds.amala.assignment1.util.DBUtil;

import java.sql.Connection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;

public class MetricsReporter {
    private static final long DELAY_SECONDS = 5;

    private final ScheduledExecutorService executor;
    private final ReadWriteLock lock;
    private final MetricsDataDAO metricsDataDAO = new MetricsDataDAO();
    private final Connection conn = DBUtil.getConnection(DatabaseConnectionPool.JDBC_URL);

    private Map<Thread, List<MetricData>> threadLocalMetrics;

    public MetricsReporter() {
        executor = Executors.newScheduledThreadPool(1);
        lock = new ReentrantReadWriteLock();
        threadLocalMetrics = new ConcurrentHashMap<>();

        executor.scheduleWithFixedDelay(() -> {
            lock.writeLock().lock();
            Map<Thread, List<MetricData>> threadLocalMetricsCopy = threadLocalMetrics;
            threadLocalMetrics = new ConcurrentHashMap<>();
            lock.writeLock().unlock();

            threadLocalMetricsCopy.values().forEach(metricDataList ->
                    metricDataList.forEach(metricsDataDAO::insertMetricData));
        }, DELAY_SECONDS, DELAY_SECONDS, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(DELAY_SECONDS, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    DBUtil.closeConnection(conn);
                }
            }
        }));    
    }

    public <T> T reportOperation(Metric errorMetric, Metric latencyMetric, Supplier<T> operation, Supplier<T> onError) {
        long startTime = System.currentTimeMillis();
        int errors = 0;
        try {
            return operation.get();
        } catch (Throwable t) {
            ++errors;
            return onError.get();
        } finally {
            long latency = System.currentTimeMillis() - startTime;
            reportMetric(new MetricData(startTime, latencyMetric, latency));
            reportMetric(new MetricData(startTime, errorMetric, errors));
        }
    }

    private void reportMetric(MetricData metricData) {
        Thread currentThread = Thread.currentThread();
        lock.readLock().lock();
        List<MetricData> metricDataList = threadLocalMetrics.getOrDefault(currentThread, new LinkedList<>());
        metricDataList.add(metricData);
        threadLocalMetrics.put(currentThread, metricDataList);
        lock.readLock().unlock();
    }
}
