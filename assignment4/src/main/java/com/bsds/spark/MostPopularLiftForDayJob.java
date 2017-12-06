package com.bsds.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;

public class MostPopularLiftForDayJob {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf().setAppName("JavaSparkPi");
        JavaSparkContext jsc = new JavaSparkContext(sparkConf);

        JavaRDD<String> dataSet = jsc.textFile("/Users/amala/Downloads/88e6a992-1e13-4620-a70e-74bca9dd9349.csv", 4);
        String result = dataSet.map((Function<String, String>) s -> {
            System.out.println(s);
            return s;
        }).reduce((Function2<String, String, String>) (s, s2) -> s + " " + s2);
        System.out.println(result);
    }
}
