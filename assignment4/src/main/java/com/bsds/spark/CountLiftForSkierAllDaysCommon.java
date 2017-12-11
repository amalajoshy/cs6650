package com.bsds.spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CountLiftForSkierAllDaysCommon {

    public static JavaPairRDD<String, Map<String, Integer>> runMapReduceLogic(JavaRDD<String> dataSet) {
        // mapToPair will return a list of (key, value) pairs with (dayNum, liftIds)
        JavaPairRDD<String, Map<String, Integer>> result = dataSet
                .mapToPair((PairFunction<String, String, String>) s -> {
                    String[] values = s.split(",");
                    String skierId = values[3];
                    String liftId = values[4];
                    return new Tuple2<>(skierId, liftId);
                })
                .aggregateByKey(new HashMap<>(), new Function2<Map<String, Integer>, String, Map<String, Integer>>() {
                    @Override
                    public Map<String, Integer> call(Map<String, Integer> acc, String skierId) {
                        int newCount = acc.getOrDefault(skierId, 0) + 1;
                        acc.put(skierId, newCount);

                        return acc;
                    }
                }, new Function2<Map<String, Integer>, Map<String, Integer>, Map<String, Integer>>() {
                    @Override
                    // In-case there is skierId in another partition, do a sum of for the skierID count
                    public Map<String, Integer> call(Map<String, Integer> m1, Map<String, Integer> m2) {
                        return Stream.of(m1, m2)
                                .map(Map::entrySet)
                                .flatMap(Collection::stream)
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, Integer::sum));
                    }
                });


        return result;
    }
}
