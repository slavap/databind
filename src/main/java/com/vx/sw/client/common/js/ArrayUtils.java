package com.vx.sw.client.common.js;

import gwt.interop.utils.shared.collections.Array;
import gwt.interop.utils.shared.collections.ArrayFactory;

public class ArrayUtils {

    private ArrayUtils() {} // static class

    /**
     * @return - shallow clone of the source array.
     */
    public static <T> Array<T> clone(Array<T> src) {
        if (src == null) return null;
        Array<T> rslt = ArrayFactory.create();
        src.forEachElem(i -> rslt.push(i));
        return rslt;
    }
}
