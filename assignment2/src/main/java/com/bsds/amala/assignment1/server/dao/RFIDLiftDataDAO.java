package com.bsds.amala.assignment1.server.dao;

import com.bsds.amala.assignment1.model.RFIDLiftData;
import com.bsds.amala.assignment1.model.SkiStats;
import com.bsds.amala.assignment1.server.db.DatabaseConnectionPool;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
public class RFIDLiftDataDAO {

    private static final String RFID_LIFT_DATA_TABLE_NAME = "RFIDLiftData";
    private final DatabaseConnectionPool databaseConnectionPool;

    public void insertRFIDLiftData(RFIDLiftData rfidLiftData) {
        Connection conn = null;
        try {
            conn = databaseConnectionPool.getConnection();
            String insertData = "insert into " + RFID_LIFT_DATA_TABLE_NAME + " values (?, ?, ?, ?, ?)";
            PreparedStatement insertDataStatement = conn.prepareStatement(insertData);
            insertDataStatement.setInt(1, rfidLiftData.getSkierID());
            insertDataStatement.setInt(2, rfidLiftData.getDayNum());
            insertDataStatement.setInt(3, rfidLiftData.getLiftId());
            insertDataStatement.setInt(4, rfidLiftData.getResortId());
            insertDataStatement.setInt(5, rfidLiftData.getTime());
            insertDataStatement.execute();
            insertDataStatement.close();
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ignore) {
                }
            }
        }
    }

    public SkiStats getSkierStats(int skierId, int dayNum) {
        Connection conn = null;
        try {
            conn = databaseConnectionPool.getConnection();
            String queryString = "select liftId from " + RFID_LIFT_DATA_TABLE_NAME + " where skierId = ? and dayNum = ?";
            PreparedStatement queryStatement = conn.prepareStatement(queryString);
            queryStatement.setInt(1, skierId);
            queryStatement.setInt(2, dayNum);
            ResultSet resultSet = queryStatement.executeQuery();
            int liftRides = 0;
            int verticalMeters = 0;
            while (resultSet.next()) {
                ++liftRides;
                verticalMeters += getVerticalMetersForLift(resultSet.getInt("liftId"));

            }
            queryStatement.close();
            return new SkiStats(skierId, dayNum, verticalMeters, liftRides);
        } catch (SQLException e) {
            e.printStackTrace(System.err);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception ignore) {
                }
            }
        }
        return null;
    }

    private int getVerticalMetersForLift(int liftId) {
        if (liftId < 1 || liftId > 40) {
            return 0;
        }

        if (liftId > 30) {
            return 500;
        }
        if (liftId > 20) {
            return 400;
        }
        if (liftId > 10) {
            return 300;
        }
        return 200;
    }
}
