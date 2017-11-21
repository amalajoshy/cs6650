package com.bsds.amala.assignment1.server;

import com.bsds.amala.assignment1.server.dao.RFIDLiftDataDAO;
import com.bsds.amala.assignment1.server.db.DatabaseConnectionPool;
import com.bsds.amala.assignment1.server.metrics.MetricsReporter;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class LoadConfigurationListener implements ServletContextListener {
    public static final String RFID_LIFT_DATA_DAO = "RFIDLiftDataDao";
    public static final String METRICS_REPORTER = "MetricsReporter";

    public void contextInitialized(ServletContextEvent sce) {
        DatabaseConnectionPool databaseConnectionPool = new DatabaseConnectionPool();
        MetricsReporter metricsReporter = new MetricsReporter();
        ServletContext context = sce.getServletContext();
        context.setAttribute(RFID_LIFT_DATA_DAO, new RFIDLiftDataDAO(databaseConnectionPool, metricsReporter));
        context.setAttribute(METRICS_REPORTER, metricsReporter);
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.removeAttribute(RFID_LIFT_DATA_DAO);
        context.removeAttribute(METRICS_REPORTER);
    }
}