package com.vx.sw.client.test.domain;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class NativeChild1 extends NativeParent {
    public String name1;
}
