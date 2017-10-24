package com.bsds.amala.assignment1.model;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.google.gson.Gson;

import java.io.Serializable;

/**
 *
 * Simple class to wrap the data in a RFID lift pass reader record
 */
@DynamoDBTable(tableName="RFIDLiftDataTable")
public class RFIDLiftData implements Serializable, Comparable<RFIDLiftData>  {
    private static final Gson GSON = new Gson();

    private int resortId;
    private int dayNum;
    private int skierId;
    private int liftId;
    private int time;

    public RFIDLiftData() {

    }

    public RFIDLiftData(int resortId, int dayNum, int skierID, int liftId, int time) {
        this.resortId = resortId;
        this.dayNum = dayNum;
        this.skierId = skierID;
        this.liftId = liftId;
        this.time = time;
    }

    @DynamoDBAttribute(attributeName="resortId")
    public int getResortId() {
        return resortId;
    }

    public void setResortId(int resortId) {
        this.resortId = resortId;
    }

    @DynamoDBAttribute(attributeName="dayNum")
    public int getDayNum() {
        return dayNum;
    }

    public void setDayNum(int dayNum) {
        this.dayNum = dayNum;
    }

    @DynamoDBHashKey(attributeName="skierId")
    public int getSkierID() {
        return skierId;
    }

    public void setSkierID(int skierID) {
        this.skierId = skierID;
    }

    @DynamoDBAttribute(attributeName="liftId")
    public int getLiftId() {
        return liftId;
    }

    public void setLiftId(int liftId) {
        this.liftId = liftId;
    }

    @DynamoDBRangeKey(attributeName="time")
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }


    public int compareTo(RFIDLiftData compareData) {
        int compareTime = compareData.getTime();

        //ascending order
        return this.time - compareTime ;
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static RFIDLiftData fromJson(String json) {
        return GSON.fromJson(json, RFIDLiftData.class);
    }
}