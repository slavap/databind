package com.vx.sw.client.common.js;

import com.google.gwt.core.client.JavaScriptObject;
import com.vx.sw.client.common.Empty;

import elemental2.core.JsObject;
import jsinterop.base.Js;

/**
 * The main reason for this functionality to have working instanceof() for native classes.
 * <br> In js after JSON.parse() result is always Object and must be manually casted to proper class
 * by calling setPrototypeOf()
 */
public class JsInstanceOfConfig {

    private JsInstanceOfConfig() {}

    public static void prepare(String jsName, Object obj) {
        if (Empty.is(jsName) || !(obj instanceof JavaScriptObject)) return;
        switch (jsName) {
        case "DtoGroup":
            //JsObject.setPrototypeOf(obj, JsObject.getPrototypeOf(new Group()));
            break;
        }
    }

    public static <T> T prepare(Object obj, T proto) {
        if (!(obj instanceof JavaScriptObject) || !(proto instanceof JavaScriptObject)) return null;
        JsObject.setPrototypeOf(obj, JsObject.getPrototypeOf(proto));
        return Js.cast(obj);
    }

}
