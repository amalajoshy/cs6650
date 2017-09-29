package com.bsds.amala.assignment1.util;

import java.util.Arrays;
import java.util.Collection;

public class MathUtil {
    public static long findMedian(Collection<Long> values) {
        int length = values.size();
        Long[] array = values.toArray(new Long[length]);
        Arrays.sort(array);
        if (length % 2 == 0) {
            return (array[length / 2] + array[length / 2 - 1]) / 2;
        } else {
            return array[length / 2];
        }
    }

    public static long findMean(Collection<Long> values) {
        int length = values.size();
        Long[] array = values.toArray(new Long[length]);

        double sum = 0;
        for (Long d : array)
            sum += d;
        return Math.round(sum/length);

    }

    public static long findPercentile(Collection<Long> values, double percentile) {
        int length = values.size();
        Long[] array = values.toArray(new Long[length]);
        Arrays.sort(array);

        double n = (length - 1) * percentile + 1;
        if (n == 1d) {
            return array[0];
        } else if (n == length) {
            return array[length - 1];
        } else {
            int k = (int)n;
            double d = n - k;
            double result = array[k - 1] + d * (array[k] - array[k - 1]);
            return Math.round(result);
        }
    }
}
