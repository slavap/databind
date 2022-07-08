package com.vx.sw.client.test.databinding;

import java.util.Objects;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class NativeEquals {
    public double id;
    public String name;

    @Override
    @JsOverlay
    public final int hashCode() {
        return Objects.hashCode(name);
    }

    @Override
    @JsOverlay
    public final boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        NativeEquals that = (NativeEquals) object;
        return Objects.equals(this.name, that.name);
    }
}
