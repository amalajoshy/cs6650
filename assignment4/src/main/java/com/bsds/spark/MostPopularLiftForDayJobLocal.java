package com.bsds.spark;

import com.google.common.io.Resources;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

public class MostPopularLiftForDayJobLocal {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf()
                .setAppName(MostPopularLiftForDayJobLocal.class.getSimpleName());
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        // Run it locally :
        // cmd:
        // spark-submit --master local --class com.bsds.spark.MostPopularLiftForDayJobLocal <path_to_jar_file>

        String localFilePath = "/Users/amala/Desktop/NEU/BSDS/assignment4/src/main/resources/sample.csv";
        JavaRDD<String> dataSet = sc.textFile(localFilePath, 4);

        // mapToPair will return a list of (key, value) pairs with (dayNum, liftIds)
        JavaPairRDD<String, String> result = MostPopularLiftForDayCommon.runMapReduceLogic(dataSet);

        result.foreach((VoidFunction<Tuple2<String, String>>) dayNumPopularLiftIdTuple ->
                System.out.println(dayNumPopularLiftIdTuple._1() + "\t" + dayNumPopularLiftIdTuple._2()));
    }
}
