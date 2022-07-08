package com.vx.sw.client.common.js;

import com.google.gwt.dom.client.Element;

import jsinterop.annotations.JsFunction;
import jsinterop.annotations.JsMethod;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsProperty;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL)
public class JsMisc {

    @JsMethod(namespace = JsPackage.GLOBAL)
    public native static void injectJs(String pathToJsFile);

    /**
     * Example: <pre> JsMisc.injectJsCode("(function() { alert('Hi!'); })();"); </pre>
     */
    @JsMethod(namespace = JsPackage.GLOBAL)
    public native static void injectJsCode(String jsCode);

    @JsMethod(namespace = JsPackage.GLOBAL)
    public native static void injectCss(String pathToCssFile);

    @JsMethod(namespace = JsPackage.GLOBAL)
    public native static void injectCssText(String cssText);

    @JsFunction public static interface NotifyFn {
        void invoke(Object... args);
    }

    @JsProperty(namespace = JsPackage.GLOBAL)
    public static NotifyFn jsOnPayNow;

    @JsProperty(namespace = JsPackage.GLOBAL)
    public static NotifyFn jsSendGTMEvent;

    @JsType(isNative = true)
    public static interface Selection {
        /** Can be used to <a href="https://stackoverflow.com/a/20079910">select all element's text</a>
         *  <br> CSS 'user-select: all' was working in Chrome69 only, but neither FF63 nor IE11.
         *  <br> This method <b>together with</b> user-select works for FF63.
         **/
        void selectAllChildren(Element elt);
    }

    @JsMethod(namespace = JsPackage.GLOBAL)
    public native static Selection getSelection();

}
