package com.bsds.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

import java.util.UUID;

public class MostPopularLiftForDayJob {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf()
                .setAppName(MostPopularLiftForDayJob.class.getSimpleName())
                .set("spark.master", "yarn");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);

        // On EMR get the data from S3 file -> RDD with 8 partitions
        JavaRDD<String> dataSet = sc.textFile("s3://rds-emr-pipeline/rds-data/*", 8);

        JavaPairRDD<String, String> result = MostPopularLiftForDayCommon.runMapReduceLogic(dataSet);

        // Change the path if running locally
        String outputDir = "s3://rds-emr-job/output/" + UUID.randomUUID().toString();
        System.out.println("Writing output to: " + outputDir);
        result.saveAsTextFile(outputDir);
        result.foreach((VoidFunction<Tuple2<String, String>>) dayNumPopularLiftIdTuple ->
                System.out.println(dayNumPopularLiftIdTuple._1() + "\t" + dayNumPopularLiftIdTuple._2()));
    }
}
