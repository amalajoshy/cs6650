package com.bsds.spark;

import com.google.common.io.Resources;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.api.java.function.VoidFunction;
import scala.Tuple2;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MostPopularLiftForDayJob {
    public static void main(String[] args) {
        SparkConf sparkConf = new SparkConf()
                .setAppName(MostPopularLiftForDayJob.class.getSimpleName())
                .set("spark.master", "yarn-client");
        JavaSparkContext sc = new JavaSparkContext(sparkConf);


        // Run it locally :
        // cmd : spark-submit --master local --class com.bsds.spark.MostPopularLiftForDayJob /Users/amala/Desktop/NEU/BSDS/assignment4/target/spark-app.jar

        String localFilePath = Resources.getResource("sample.csv").getPath(); // use this to test locally

        // On EMR get the data from S3 file -> RDD with 8 partitions
        JavaRDD<String> dataSet = sc.textFile("s3://rds-emr-pipeline/rds-data/*", 8);

        // mapToPair will return a list of (key, value) pairs with (dayNum, liftIds)
        JavaPairRDD<String, String> result = dataSet
                .mapToPair((PairFunction<String, String, String>) s -> {
                    String[] values = s.split(",");
                    String dayNum = values[2];
                    String liftId = values[3];
                    return new Tuple2<>(dayNum, liftId);
                })
                // group the result set by dayNum, increment the count if the same lifId appears again for the day
                /*
                   AggregateByKey:
                  1. An intitial ‘zero’ value that will not effect the total values to be collected. For example if
                    we were adding numbers the initial value would be 0. Or in the case of collecting unique elements per
                    key, the initial value would be an empty set.
                   2. A combining function accepting two paremeters. The second paramter is merged into the first
                    parameter. This function combines/merges values within a partition.
                   3. A merging function function accepting two parameters. In this case the paremters are merged into
                    one. This step merges values across partitions. */
                .aggregateByKey(new HashMap<>(), new Function2<Map<String, Integer>, String, Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> call(Map<String, Integer> acc, String liftId) {
                        int newCount = acc.getOrDefault(liftId, 0) + 1;
                        acc.put(liftId, newCount);
                        return acc;
                    }
                }, new Function2<Map<String, Integer>, Map<String, Integer>, Map<String, Integer>>() {
                    @Override
                    // Incase there is liftId in another partition, do a sum of for the liftId count
                    public Map<String, Integer> call(Map<String, Integer> m1, Map<String, Integer> m2) {
                        return Stream.of(m1, m2)
                                .map(Map::entrySet)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
                    }
                })
                // get the max of count
                .mapValues((Function<Map<String, Integer>, String>) liftIdCountMap ->
                        Collections.max(liftIdCountMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue)).getKey());

        // Change the path if running locally
        result.saveAsTextFile("s3://rds-emr-job/output/");
        result.foreach((VoidFunction<Tuple2<String, String>>) dayNumPopularLiftIdTuple ->
                System.out.println(dayNumPopularLiftIdTuple._1() + "\t" + dayNumPopularLiftIdTuple._2()));
    }
}
