package com.vx.sw.client.test.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import com.vx.sw.client.common.Empty;

import gwt.interop.utils.shared.collections.Array;
import gwt.interop.utils.shared.collections.ArrayFactory;
import gwt.interop.utils.shared.collections.StringMap;
import gwt.interop.utils.shared.collections.StringMapFactory;

public class DtoUtils {

    private DtoUtils() {} // static class

    public static <K, V, T> Map<K, V> strMapToMap(StringMap<T> map, Function<String, K> keySupplier, Function<T, V> valSupplier) {
        Map<K, V> result = new HashMap<>();
        if (map != null) map.forEach((v, k, map1) -> result.put(keySupplier.apply(k), valSupplier.apply(v)));
        return result;
    }

    public static Map<String, String> strMapToMap(StringMap<String> map) {
        return strMapToMap(map, s -> s, s -> s);
    }

    public static <T, V> StringMap<V> mapToStrMap(Map<?, T> map, Function<T, V> valueSupplier) {
        final StringMap<V> result = StringMapFactory.create();
        if (!Empty.is(map)) map.forEach((key, value) -> result.set(String.valueOf(key), valueSupplier.apply(value)));
        return result;
    }

    public static String enumToStr(Enum<?> value) {
        return value != null ? value.name() : null;
    }

    public static <T extends Enum<T>> T strToEnum(String value, Class<T> enumType) {
        return (value == null || value.isEmpty()) ? null : Enum.valueOf(enumType, value);
    }

    public static <T extends Enum<T>> Array<String> enumColToArr(Collection<T> value) {
        if (value == null) return null;
        Array<String> arr = ArrayFactory.create();
        for (Enum<?> i : value) arr.push(enumToStr(i));
        return arr;
    }

    public static <T extends Enum<T>> Set<T> arrToEnumSet(Array<String> value, Class<T> enumType) {
        if (value == null) return null;
        Set<T> rslt = EnumSet.noneOf(enumType);
        value.forEachElem(i -> rslt.add(strToEnum(i, enumType)));
        return rslt;
    }

    public static <T extends Enum<T>> Set<T> setToEnumSet(Set<String> value, Class<T> enumType) {
        if (value == null) return null;
        Set<T> rslt = EnumSet.noneOf(enumType);
        value.forEach(i -> rslt.add(strToEnum(i, enumType)));
        return rslt;
    }

    public static <T extends Enum<T>> List<T> arrToEnumList(Array<String> value, Class<T> enumType) {
        if (value == null) return null;
        List<T> rslt = new ArrayList<>();
        value.forEachElem(i -> rslt.add(strToEnum(i, enumType)));
        return rslt;
    }

    public static <T> Set<T> arrToSet(Array<T> value) {
        Set<T> rslt = new HashSet<>();
        if (value != null) value.forEachElem(i -> rslt.add(i));
        return rslt;
    }

    public static <T> List<T> arrToList(Array<T> value) {
        List<T> rslt = new ArrayList<>();
        if (value != null) value.forEachElem(i -> rslt.add(i));
        return rslt;
    }

    public static <T> Array<T> colToArr(Collection<T> value) {
        Array<T> arr = ArrayFactory.create();
        if (value != null) {
            for (T i : value) arr.push(i);
        }
        return arr;
    }

    public static Array<Double> numCollectToArr(Collection<? extends Number> value) {
        if (value == null) return null;
        Array<Double> arr = ArrayFactory.create();
        for (Number i : value) arr.push(i.doubleValue());
        return arr;
    }

    public static <T extends Collection<Long>> T arrToLongCollect(Array<Double> value, T result) {
        if (!Empty.is(result)) result.clear();
        if (value == null || result == null) return result;
        value.forEachElem(i -> result.add(doubleToLong(i)));
        return result;
    }

    public static <T extends Collection<Integer>> T arrToIntCollect(Array<Double> value, T result) {
        if (!Empty.is(result)) result.clear();
        if (value == null || result == null) return result;
        value.forEachElem(i -> result.add(doubleToInt(i)));
        return result;
    }

    public static Date numToDate(Double value) {
        if (value == null) return null;
        return new Date(value.longValue());
    }

    public static Double dateToNum(Date value) {
        if (value == null) return null;
        return (double) value.getTime();
    }

    public static Double longToDouble(Long value) {
        if (value == null) return null;
        return Double.valueOf(value.doubleValue());
    }

    public static Long doubleToLong(Double value) {
        if (value == null) return null;
        return Long.valueOf(value.longValue());
    }

    public static Double intToDouble(Integer value) {
        if (value == null) return null;
        return Double.valueOf(value.doubleValue());
    }

    public static Integer doubleToInt(Double value) {
        if (value == null) return null;
        return Integer.valueOf(value.intValue());
    }

    public static Double roundAmount(Double value) {
        if (value == null) return null;
        return BigDecimal.valueOf(value).setScale(2,RoundingMode.HALF_UP).doubleValue();
    }
}
