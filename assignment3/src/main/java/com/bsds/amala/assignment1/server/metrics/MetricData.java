package com.bsds.amala.assignment1.server.metrics;

import lombok.Value;

@Value
public class MetricData {
    private long timestamp;
    private Metric metric;
    private long value;
}
