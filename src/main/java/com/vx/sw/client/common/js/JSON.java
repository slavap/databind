package com.vx.sw.client.common.js;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;
import jsinterop.base.Js;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class JSON {

    @JsFunction
    public interface StringifyReplacerFn {
        Object onInvoke(String key, Object value);
    }

    public native static String stringify(Object obj);
    public native static String stringify(Object obj, StringifyReplacerFn replacer);

    @JsOverlay
    public final static StringifyReplacerFn NO_GWT_HASH = (String key, Object value) -> {
        if ("$H".equals(key)) return Js.undefined(); // see javaemul.internal.ObjectHashing
        else return value;
    };

    @JsOverlay
    public final static StringifyReplacerFn NO_NULLS = (String key, Object value) -> {
        if (value == null) return Js.undefined();
        else return value;
    };

    @JsOverlay
    public final static StringifyReplacerFn NO_NULLS_NO_EMPTY_STRS = (String key, Object value) -> {
        if (value == null) return Js.undefined();
        else if ("string".equals(Js.typeof(value)) && "".equals(value)) return Js.undefined();
        else return value;
    };

    @JsOverlay
    public final static StringifyReplacerFn STR_EMPTY_EQ_NULL = (String key, Object value) -> {
        if ("string".equals(Js.typeof(value)) && "".equals(value)) return null;
        else return value;
    };

    @JsOverlay
    public final static StringifyReplacerFn STR_EMPTY_EQ_NULL_NO_GWT_HASH = (String key, Object value) -> {
        if ("$H".equals(key)) return Js.undefined(); // see javaemul.internal.ObjectHashing
        if ("string".equals(Js.typeof(value)) && "".equals(value)) return null;
        else return value;
    };

    public native static <T> T parse(String obj);

    @JsOverlay
    public final static <T> T clone(T obj) {
        if (obj == null) return null;
        String json = stringify(obj);
        T rslt = parse(json);
        return rslt;
    }

    /**
     * @param clazz - needed to enforce compile time check for compatible classes (A & A | B & B),
     * otherwise it allows mixing of ANY classes, because of mutual Object predecessor, i.e. A & B.
     * <br> Works by comparison of json serialized strings.
     * <br> GWT special/internal fields (like $H) are NOT ignored! So it's pure comparison.
     */
    @JsOverlay
    public final static <T> boolean equalsPure(Class<T> clazz, T obj1, T obj2) {
        if (obj1 == obj2) return true;
        if (obj1 == null || obj2 == null) return false;
        String s1 = stringify(obj1);
        String s2 = stringify(obj2);
        return s1.equals(s2);
    }

    /**
     * @param clazz - needed to enforce compile time check for compatible classes (A & A | B & B),
     * otherwise it allows mixing of ANY classes, because of mutual Object predecessor, i.e. A & B.
     * <br> Works by comparison of json serialized strings.
     * <br> GWT special/internal fields (like $H) are ignored!
     * <br> Needed to eliminate from domain classes the "ugly" equals() by all their fields.
     */
    @JsOverlay
    public final static <T> boolean equals(Class<T> clazz, T obj1, T obj2) {
        if (obj1 == obj2) return true;
        if (obj1 == null || obj2 == null) return false;
        String s1 = stringify(obj1, NO_GWT_HASH);
        String s2 = stringify(obj2, NO_GWT_HASH);
        return s1.equals(s2);
    }

    @JsOverlay
    public final static <T> boolean equals(Class<T> clazz, T obj1, T obj2, boolean emptyEqualsNull) {
        if (!emptyEqualsNull) return equals(clazz, obj1, obj2);
        if (obj1 == obj2) return true;
        if (obj1 == null || obj2 == null) return false;
        String s1 = stringify(obj1, STR_EMPTY_EQ_NULL_NO_GWT_HASH);
        String s2 = stringify(obj2, STR_EMPTY_EQ_NULL_NO_GWT_HASH);
        return s1.equals(s2);
    }
}
