package com.bsds.amala.assignment1.server;

import com.bsds.amala.assignment1.server.dao.RFIDLiftDataDAO;
import com.bsds.amala.assignment1.server.db.DatabaseConnectionPool;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class LoadConfigurationListener implements ServletContextListener {
    public static final String RFID_LIFT_DATA_DAO = "RFIDLiftDataDao";

    public void contextInitialized(ServletContextEvent sce) {
        DatabaseConnectionPool databaseConnectionPool = new DatabaseConnectionPool();
        ServletContext context = sce.getServletContext();
        context.setAttribute(RFID_LIFT_DATA_DAO, new RFIDLiftDataDAO(databaseConnectionPool));
    }

    public void contextDestroyed(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        context.removeAttribute(RFID_LIFT_DATA_DAO);
    }
}