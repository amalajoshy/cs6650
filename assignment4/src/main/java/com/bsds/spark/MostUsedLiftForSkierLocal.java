package com.bsds.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

public class MostUsedLiftForSkierLocal {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf()
                .setAppName(MostUsedLiftForSkierLocal.class.getSimpleName());
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        // Run it locally :
        // cmd:
        // spark-submit --master local --class com.bsds.spark.MostUsedLiftForSkierLocal <path_to_jar_file>

        String localFilePath = "/Users/vishwalfc/Desktop/BSDS/assignment4/src/main/resources/BSDSAssignment2Day1.csv";
        JavaRDD<String> dataSet = sc.textFile(localFilePath, 4);

        // mapToPair will return a list of (key, value) pairs with (dayNum, liftIds)
        JavaPairRDD<String, String> result = MostUsedLiftForSkierCommon.runMapReduceLogic(dataSet);

        result.foreach((VoidFunction<Tuple2<String, String>>) dayNumPopularLiftIdTuple ->
                System.out.println(dayNumPopularLiftIdTuple._1() + "\t" + dayNumPopularLiftIdTuple._2()));
    }
}
