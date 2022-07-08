package com.vx.sw.client.test.nativ;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.vx.sw.client.common.Empty;

import gwt.interop.utils.client.collections.JsArray;
import gwt.interop.utils.shared.collections.Array;

public class TestDtoUtils {

    private TestDtoUtils() {} // static class

    public interface DtoDateConverter {
        TestDtoDate fromDate(Date value);
        Date toDate(TestDtoDate value);
    }

    /** Different for server and client sides, must be initialized before usage. */
    public static DtoDateConverter dtoDateConverter = new TestJsDtoDateConverter();

    public static String enumToStr(Enum<?> value) {
        return value != null ? value.name() : null;
    }

    public static <T extends Enum<T>> T strToEnum(String value, Class<T> enumType) {
        return (value == null || value.isEmpty()) ? null : Enum.valueOf(enumType, value);
    }

    public static <T extends Enum<T>> Array<String> enumSetToArr(Set<T> value) {
        if (value == null) return null;
        Array<String> arr = JsArray.create();
        for (Enum<?> i : value) arr.push(enumToStr(i));
        return arr;
    }

    public static <T extends Enum<T>> Set<T> arrToEnumSet(Array<String> value, Class<T> enumType) {
        if (value == null) return null;
        Set<T> rslt = EnumSet.noneOf(enumType);
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
        Array<T> arr = JsArray.create();
        if (value != null) {
            for (T i : value) arr.push(i);
        }
        return arr;
    }

    public static Array<Double> numCollectToArr(Collection<? extends Number> value) {
        if (value == null) return null;
        Array<Double> arr = JsArray.create();
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

    public static Date dtoDateToDate(TestDtoDate value) {
        if (value == null) return null;
        return dtoDateConverter.toDate(value);
    }

    public static TestDtoDate dateToDtoDate(Date value) {
        if (value == null) return null;
        return dtoDateConverter.fromDate(value);
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
}
