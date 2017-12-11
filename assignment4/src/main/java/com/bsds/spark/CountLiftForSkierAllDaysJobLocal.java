package com.bsds.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;
import java.util.List;

import java.util.Map;

public class CountLiftForSkierAllDaysJobLocal {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf()
                .setAppName(CountLiftForSkierAllDaysJobLocal.class.getSimpleName());
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        // Run it locally :
        // cmd:
        // spark-submit --master local --class com.bsds.spark.CountLiftForSkierAllDaysJobLocal <path_to_jar_file>

        String localFilePath = "/Users/vishwalfc/Desktop/BSDS/assignment4/src/main/resources/BSDSAssignment2Day1.csv";
        //String localFilePath = "/Users/vishwalfc/Desktop/BSDS/assignment4/src/main/resources/sample.csv";
        JavaRDD<String> dataSet = sc.textFile(localFilePath, 4);

        // mapToPair will return a list of (key, value) pairs with (skierId, liftIds)
        JavaPairRDD<String, Map<String, Integer>> result = CountLiftForSkierAllDaysCommon.runMapReduceLogic(dataSet);


        List<Tuple2<String, Map<String, Integer>>> output = result.collect();
        for (Tuple2<?,?> tuple : output) {
            System.out.println(tuple._1() + ": " + tuple._2());
        }
        sc.stop();

    }
}
