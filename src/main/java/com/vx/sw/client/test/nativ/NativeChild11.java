package com.vx.sw.client.test.nativ;

import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class NativeChild11 extends NativeParent11 {
  public String name1;
  public NativeChild11 sibling;
}