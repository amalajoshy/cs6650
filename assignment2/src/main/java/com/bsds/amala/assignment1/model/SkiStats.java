package com.bsds.amala.assignment1.model;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SkiStats implements Serializable {

    private static final Gson GSON = new Gson();

    private int skierId;
    private int dayNum;
    private int totalVerticalMetres;
    private int totalLiftRides;

    public String toJson() {
        return GSON.toJson(this);
    }

    public static SkiStats fromJson(String json) {
        return GSON.fromJson(json, SkiStats.class);
    }

}
