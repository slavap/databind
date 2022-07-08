package com.vx.sw.client.common.databinding;

import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "document.swdatabinder")
public class JsDataBinder {
    /** Never creates observable over observable, "raw/pure" model object will be observed only once,
     *  and then this observable will be always returned without creating new one.
     *
     *  @param callback - must be non-null
     */
    @JsMethod public static native <T> T observe(T obj, JsPropertyChanged callback);

    /** @return - true in case of this model object is already proxied/observable. */
    @JsMethod public static native boolean isProxy(Object obj);

    /** @return - unwrapped/non-proxied model object. */
    @JsMethod public static native <T> T getTarget(T obj);

    /** @return - true in case of object is array. */
    @JsMethod public static native boolean isArray(Object obj);

    /**
     * @param chain - like a.b.c or a.b[3].c, also a.b.* and a.b.** are supported.
     * @param value - optional, value to set in case field does not exist (null will be used by default).
     * @param forceSet - if true value will be always set (even if field is already existed).
     * @return - current value of field chain.
     */
    @JsMethod public static native <T> T defineFieldChain(Object obj, String chain, T value, boolean forceSet);
    @JsMethod public static native <T> T defineFieldChain(Object obj, String chain, T value);
    @JsMethod public static native <T> T defineFieldChain(Object obj, String chain);

    /**
     * @param chain - like a.b.c or a.b[3].c, also a.b.* and a.b.** are supported.
     * @return - current value of field chain (no any intermediate fields are created in opposite to defineFieldChain() method).
     */
    @JsMethod public static native <T> T getFieldChainValue(Object obj, String chain);

    @JsMethod public static native void setGwtEnumClass(Class<?> value);
}
