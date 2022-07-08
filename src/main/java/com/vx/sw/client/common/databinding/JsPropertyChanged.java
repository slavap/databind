package com.vx.sw.client.common.databinding;

import jsinterop.annotations.JsFunction;

@JsFunction
@FunctionalInterface
public interface JsPropertyChanged {
    void onChanged(Object proxy, Object target, String property, Object oldValue, Object newValue);
}
