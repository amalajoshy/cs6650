package com.bsds.spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MostUsedLiftForSkierCommon {

    static ArrayList<String> arr = new ArrayList<>();

    public static JavaPairRDD<String, String> runMapReduceLogic(JavaRDD<String> dataSet) {
        // mapToPair will return a list of (key, value) pairs with (dayNum, liftIds)
        JavaPairRDD<String, String> result = dataSet
                .mapToPair((PairFunction<String, String, String>) s -> {
                    String[] values = s.split(",");
                    String liftId = values[4];
                    String skierId = values[3];
                    arr.add(skierId);

//                    try (Writer writer = new BufferedWriter(new OutputStreamWriter(
//                            new FileOutputStream("SkierIdFile.txt"), "utf-8"))) {
//                        writer.write(liftId+" ");
//                    }

                    //System.out.println(liftId);
                    return new Tuple2<>(skierId, liftId);
                })
                // group the result set by dayNum, increment the count if the same lifId appears again for the day
                /*
                   AggregateByKey:
                  1. An intitial ‘zero’ value that will not effect the total values to be collected. For example if
                    we were adding numbers the initial value would be 0. Or in the case of collecting unique elements per
                    key, the initial value would be an empty set.
                   2. A combining function accepting two parameters. The second parameter is merged into the first
                    parameter. This function combines/merges values within a partition.
                   3. A merging function function accepting two parameters. In this case the parameters are merged into
                    one. This step merges values across partitions. */
                .aggregateByKey(new HashMap<>(), new Function2<Map<String, Integer>, String, Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> call(Map<String, Integer> acc, String skierId) {
                        int newCount = acc.getOrDefault(skierId, 0) + 1;
                        acc.put(skierId, newCount);
                        //System.out.println(acc);
                        return acc;
                    }
                }, new Function2<Map<String, Integer>, Map<String, Integer>, Map<String, Integer>>() {
                    @Override
                    // In-case there is liftId in another partition, do a sum of for the liftId count
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

        return result;
    }
}
