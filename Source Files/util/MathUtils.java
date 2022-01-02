package util;

import java.util.Arrays;
import java.util.Comparator;

public class MathUtils {

    public static <T extends Comparable<T>> T max(T... array) {
        return Arrays.stream(array).max(Comparator.naturalOrder()).get();
    }

    public static <T extends Comparable<T>> T max(T[][] array) {
        return Arrays.stream(array).flatMap(Arrays::stream).max(Comparator.naturalOrder()).get();
    }

    public static <T extends Comparable<T>> T min(T... array) {
        return Arrays.stream(array).min(Comparator.naturalOrder()).get();
    }

    public static <T extends Comparable<T>> T min(T[][] array) {
        return Arrays.stream(array).flatMap(Arrays::stream).min(Comparator.naturalOrder()).get();
    }

    public static Double avg(Double[] array) {
        return Arrays.stream(array).mapToDouble(Double::doubleValue).average().getAsDouble();
    }

    public static Double avg(Double[][] array) {
        return Arrays.stream(array).flatMap(Arrays::stream).mapToDouble(Double::doubleValue).average().getAsDouble();
    }
}
