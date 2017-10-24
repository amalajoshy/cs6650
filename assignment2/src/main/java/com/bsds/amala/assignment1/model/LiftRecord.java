package com.bsds.amala.assignment1.model;

import lombok.Data;

@Data
public class LiftRecord {
    private int resortId;
    private int dayNumber;
    private int timestamp;
    private int skierId;
    private int liftId;
}
