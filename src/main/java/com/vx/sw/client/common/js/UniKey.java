package com.vx.sw.client.common.js;

import java.util.function.IntSupplier;
import java.util.function.Predicate;

/**
 * Needed to allow native js classes usage in Java List, Set, and Map.
 */
public class UniKey<T> {
    public final T obj;
    private final Predicate<Object> equalsFn;
    private final IntSupplier hashCodeFn;

    public static <T> UniKey<T> of(T obj, Predicate<Object> equalsFn, IntSupplier hashCodeFn) {
        return new UniKey<T>(obj, equalsFn, hashCodeFn);
    }

    private UniKey(T obj, Predicate<Object> equalsFn, IntSupplier hashCodeFn) {
        this.obj = obj;
        this.equalsFn = equalsFn;
        this.hashCodeFn = hashCodeFn;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof UniKey) return equalsFn.test(((UniKey<?>) other).obj);
        else return equalsFn.test(other);
    }

    @Override
    public int hashCode() {
        return hashCodeFn.getAsInt();
    }
}
