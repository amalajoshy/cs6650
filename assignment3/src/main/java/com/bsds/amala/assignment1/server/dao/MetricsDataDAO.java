package com.bsds.amala.assignment1.server.dao;

import com.bsds.amala.assignment1.server.db.DatabaseConnectionPool;
import com.bsds.amala.assignment1.server.metrics.MetricData;
import com.bsds.amala.assignment1.util.DBUtil;
import com.bsds.amala.assignment1.util.SystemUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MetricsDataDAO {
    private static final String METRIC_DATA_TABLE_NAME = "MetricDataTable";
    private Statement stmt = null;
    private Connection conn = DBUtil.getConnection(DatabaseConnectionPool.JDBC_URL);

    public void insertMetricData(MetricData metricData) {
        try {
            String insertData = "insert into " + METRIC_DATA_TABLE_NAME + " (timestamp, hostname, metric, metricValue)" +
                    " values (?, ?, ?, ?)";
            PreparedStatement insertDataStatement = conn.prepareStatement(insertData);
            insertDataStatement.setLong(1, metricData.getTimestamp());
            insertDataStatement.setString(2, SystemUtil.HOSTNAME);
            insertDataStatement.setString(3, metricData.getMetric().name());
            insertDataStatement.setLong(4, metricData.getValue());
            insertDataStatement.execute();
            insertDataStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Long> getLatencyMetricList(String hostname, String metric) {
        try {
            String getLatencyQuery = "SELECT metricValue from MetricDataTable WHERE hostname = ? AND metric LIKE ? ORDER BY metricValue";
            PreparedStatement medianValueStatement = conn.prepareStatement(getLatencyQuery);
            medianValueStatement.setString(1, hostname);
            medianValueStatement.setString(2, "%"+metric+"%");
            ResultSet rs = medianValueStatement.executeQuery();
            List<Long> metricValue = new ArrayList<>();
            while(rs.next()) {
                metricValue.add(rs.getLong(1));
            }
            return metricValue;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    public List<Long> getLatencyMetricListForAllHosts(String metric) {
        try {
            String getLatencyQuery = "SELECT metricValue from MetricDataTable WHERE metric LIKE ? ORDER BY metricValue";
            PreparedStatement medianValueStatement = conn.prepareStatement(getLatencyQuery);
            medianValueStatement.setString(1, "%"+metric+"%");
            ResultSet rs = medianValueStatement.executeQuery();
            List<Long> metricValue = new ArrayList<>();
            while(rs.next()) {
                metricValue.add(rs.getLong(1));
            }
            return metricValue;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return Collections.emptyList();
    }

    public int getErrorCount() {
        try {
            int errorCount = 0;
            String getLatencyQuery = "SELECT count(metricValue) from MetricDataTable WHERE metric LIKE '%ERRORS%' AND metricValue > 0";
            PreparedStatement medianValueStatement = conn.prepareStatement(getLatencyQuery);
//          medianValueStatement.setString(1, hostname);
            ResultSet rs = medianValueStatement.executeQuery();
            while (rs.next()) {
                errorCount = rs.getInt(1);
            }
            return errorCount;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null)
                    conn.close();
            } catch (SQLException se) {
            }
            try {
                if (conn != null)
                    conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        return 0;

    }

    public List<String> getHostnames() {
        try {
            String getHostnameQuery = "SELECT DISTINCT(hostname) FROM MetricDataTable";
            PreparedStatement getHostnameStmt = conn.prepareStatement(getHostnameQuery);
            ResultSet rs = getHostnameStmt.executeQuery();
            List<String> hostnames = new ArrayList<>();
            while (rs.next()) {
                hostnames.add(rs.getString(1));
            }
            return hostnames;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
}