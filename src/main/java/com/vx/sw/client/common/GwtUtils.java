package com.vx.sw.client.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.jsonp.client.JsonpRequestBuilder;
import com.google.gwt.logging.client.LogConfiguration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

public class GwtUtils {

    private GwtUtils() {} // static class

    /**
     * Superdev mode detector, see <a href="https://code.google.com/p/google-web-toolkit/issues/detail?id=7634#c15">superdev mode discussion</a>
     **/
    protected static class SuperdevModeHelper {

        public boolean isSuperdevMode() {
            return false;
        }
    }

    // TODO: it's enough to just check: System.getProperty("superdevmode").equals("on")
    protected static class SuperdevModeOn extends SuperdevModeHelper {

        @Override
        public boolean isSuperdevMode() {
            return true;
        }
    }

    private static final SuperdevModeHelper superDevHelper = GWT.create(SuperdevModeHelper.class);

    /**
     * See <a href="http://www.gwtproject.org/doc/latest/DevGuideLogging.html">GWT Logging</a>
     */
    private static final Logger logger = Logger.getLogger("GwtUtils");

    public static boolean isLogEnabled(Level level) {
        return LogConfiguration.loggingIsEnabled(level);
    }

    public static void log(Level level, String msg, Throwable thrown) {
        if (!LogConfiguration.loggingIsEnabled(level)) return;
        logger.log(level, msg, thrown);
    }

    public static void logError(String msg, Throwable... thrown) {
        log(Level.SEVERE, msg, thrown.length > 0 ? thrown[0] : null);
    }

    public static void logErrorCodeDownload(String msg, Throwable... thrown) {
        logError(msg + " code download failed", thrown);
    }

    public static boolean isLogErrorEnabled() {
        return isLogEnabled(Level.SEVERE);
    }

    public static void logWarn(String msg, Throwable... thrown) {
        log(Level.WARNING, msg, thrown.length > 0 ? thrown[0] : null);
    }

    public static boolean isLogWarnEnabled() {
        return isLogEnabled(Level.WARNING);
    }

    public static void logInfo(String msg, Throwable... thrown) {
        log(Level.INFO, msg, thrown.length > 0 ? thrown[0] : null);
    }

    public static boolean isLogInfoEnabled() {
        return isLogEnabled(Level.INFO);
    }

    public static void logDebug(String msg, Throwable... thrown) {
        // There is no Level.DEBUG, that's why Level.CONFIG is used, must be lower than Level.INFO
        log(Level.CONFIG, msg, thrown.length > 0 ? thrown[0] : null);
    }

    public static boolean isLogDebugEnabled() {
        return isLogEnabled(Level.CONFIG);
    }

    /**
     * @return - true in case code is running in devmode or superdevmode.
     *
     * <br><br> See <a href="http://stackoverflow.com/a/3552772">If GWT code is running in development mode?</a>
     **/
    public static boolean isDevelopmentMode() {
        return !GWT.isProdMode() && GWT.isClient() || superDevHelper.isSuperdevMode();
    }

    /**
     * Return current DOM focused element.
     */
    public static native Element getFocused() /*-{
        return $doc.activeElement;
    }-*/;

    /**
     * Return DOM body.
     */
    public static native Element getDocBody() /*-{
        return $doc.body;
    }-*/;

    public static SafeHtml imageToHtml(ImageResource img) {
        return SafeHtmlUtils.fromSafeConstant("<img src='" + img.getSafeUri().asString() + "'>");
    }

    public static boolean isWidgetAndParentsVisible(Widget w) {
        if (w == null || !w.isVisible()) return false;
        Widget p = w.getParent();
        while (p != null) {
            if (!p.isVisible()) return false;
            p = p.getParent();
        }
        return true;
    }

    public static Object getWidgetValue(Widget widget) {
        if (widget == null) return null;
        Object rslt = null;
        if (widget instanceof HasValue) {
            rslt = ((HasValue<?>) widget).getValue();
        } else if (widget instanceof HasText) {
            rslt = ((HasText) widget).getText();
        }
        return rslt;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean setWidgetValue(Widget widget, Object value, boolean fireEvents) {
        if (widget == null) return false;
        if (widget instanceof HasValue) {
            ((HasValue) widget).setValue(value, fireEvents);
            return true;
        } else if (widget instanceof HasText) {
            ((HasText) widget).setText((String) value);
            return true;
        }
        return false;
    }

    /**
     * Scans all children of passed panel and returns true if any of them has entered value.
     * <p> Empty strings are not considered as entered values! </p>
     */
    public static boolean hasValue(ComplexPanel panel) {
        boolean empty = true;
        for (int i = 0; i < panel.getWidgetCount(); i++) {
            Widget w = panel.getWidget(i);
            if (w instanceof HasEnabled && !((HasEnabled) w).isEnabled()) continue;
            if (w instanceof HasValue) {
                Object v = ((HasValue<?>) w).getValue();
                if (v != null) {
                    if (v instanceof String && ((String) v).isEmpty()) continue;
                    empty = false;
                    break;
                }
            }
            if (w instanceof Composite) {
                w = getCompositeWidget((Composite) w);
            }
            if (w instanceof ComplexPanel) {
                boolean v = hasValue((ComplexPanel) w);
                if (v) return true;
            }
        }
        return !empty;
    }

    public static native Widget getCompositeWidget(Composite composite) /*-{
        return composite.@com.google.gwt.user.client.ui.Composite::getWidget()();
    }-*/;

    /**
     * @param styles - space separated style names
     */
    public static void addStyleNames(UIObject o, String styles) {
        if (o == null || styles == null || styles.isEmpty()) return;
        String[] arr = styles.split(" ");
        for (String i : arr) {
            String s = i.trim();
            if (!s.isEmpty()) o.addStyleName(s);
        }
    }

    /**
     * @param styles - space separated style names
     */
    public static void removeStyleNames(UIObject o, String styles) {
        if (o == null || styles == null || styles.isEmpty()) return;
        String[] arr = styles.split(" ");
        for (String i : arr) {
            String s = i.trim();
            if (!s.isEmpty()) o.removeStyleName(s);
        }
    }

    /**
     * GWT wrapper over: o instanceof T
     */
    public static interface Is<T> {
        boolean is(Object o);
    }

    public static class BaseIs {
        public static final Is<String> STRING = new Is<String>() {
            @Override
            public boolean is(Object o) {
                return o instanceof String;
            }};

        public static final Is<Date> DATE = new Is<Date>() {
            @Override
            public boolean is(Object o) {
                return o instanceof Date;
            }};

        public static final Is<Number> NUMBER = new Is<Number>() {
            @Override
            public boolean is(Object o) {
                return o instanceof Number;
            }};
    }

    /**
     * Returns the first object of specified Is&lt;T>
     */
    @SuppressWarnings("unchecked")
    public static <T> T findObjFirst(List<? extends Object> objects, Is<T> is) {
        if (is == null || objects == null || objects.isEmpty()) return null;
        for (Object o : objects) {
            if (is.is(o)) return (T) o;
        }
        return null;
    }

    /**
     * Returns the latest/newest object of specified Is&lt;T>
     *
     * @param stops - immediately stop searching if one of these Is types is encountered.
     */
    @SuppressWarnings("unchecked")
    public static <T> T findObj(List<? extends Object> objects, Is<T> is, Is<?>... stops) {
        if (is == null || objects == null || objects.isEmpty()) return null;
        for (int i = objects.size() - 1; i >= 0; i--) {
            Object o = objects.get(i);
            if (stops != null && stops.length > 0) {
                for (Is<?> stop : stops) {
                    if (stop.is(o)) return null;
                }
            }
            if (is.is(o)) return (T) o;
        }
        return null;
    }

    /**
     * Returns list of specified Is&lt;T> with the latest/newest objects in the head (i.e. the first ones).
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> findObjs(List<? extends Object> objects, Is<T> is, Is<?>... stops) {
        if (is == null || objects == null || objects.isEmpty()) return null;
        List<T> rslt = null;
        for (int i = objects.size() - 1; i >= 0; i--) {
            Object o = objects.get(i);
            if (stops != null && stops.length > 0) {
                for (Is<?> stop : stops) {
                    if (stop.is(o)) return rslt;
                }
            }
            if (is.is(o)) {
                if (rslt == null) rslt = new ArrayList<T>();
                rslt.add((T) o);
            }
        }
        return rslt;
    }

    public static List<String> findStrings(List<? extends Object> objects) {
        if (objects == null || objects.isEmpty()) return null;
        return GwtUtils.findObjs(objects, BaseIs.STRING);
    }

    /**
     * Search for parameter's value, i.e. when aaa passed, look for aaa=bbb and return bbb.
     */
    public static String findParamValue(String paramName, Collection<String> params) {
        if (paramName == null || paramName.isEmpty() || params == null || params.isEmpty()) {
            return null;
        }
        for (String s : params) {
            if (s == null) continue;
            int i = s.indexOf('=');
            if (i > 0) {
                String pn = s.substring(0, i).trim();
                if (paramName.equals(pn)) return s.substring(i + 1).trim();
            }
        }
        return null;
    }

    public static final String NUM_PARAM_DELIM = ">>"; // must not contain = symbol

    public static String numParam(int num, String paramName) {
        return String.valueOf(num) + NUM_PARAM_DELIM + paramName;
    }

    /**
     * Search for tagged parameter's value, i.e. when (2, "aaa") are passed,
     * look for aaa=2>>bbb and return bbb.
     */
    public static String findParamValue(int num, String paramName, Collection<String> params) {
        if (paramName == null || paramName.isEmpty() || params == null || params.isEmpty()) {
            return null;
        }
        for (String s : params) {
            if (s == null) continue;
            int i = s.indexOf('=');
            if (i > 0) {
                String pn = s.substring(0, i).trim();
                if (paramName.equals(pn)) {
                    String str = s.substring(i + 1).trim();
                    int p = str.indexOf(NUM_PARAM_DELIM);
                    if (p == -1) continue;
                    String n = str.substring(0, p);
                    try {
                        if (Integer.parseInt(n) == num) {
                            return str.substring(p + NUM_PARAM_DELIM.length());
                        }
                    } catch (Exception ex) {
                        continue; // it's not a problem if parseInt() failed, we can continue anyway
                    }
                }
            }
        }
        return null;
    }

    /**
     * Removes all occurrences of parameter in specified collection.
     */
    public static void removeParam(String paramName, Collection<? extends Object> params) {
        if (paramName == null || paramName.isEmpty() || params == null || params.isEmpty()) {
            return;
        }
        Iterator<? extends Object> iter = params.iterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (!BaseIs.STRING.is(obj)) continue;
            String s = (String) obj;
            int i = s.indexOf('=');
            if (i > 0) {
                String pn = s.substring(0, i).trim();
                if (paramName.equals(pn)) iter.remove();
            }
        }
    }

    private static final String HTTP = "http://";
    private static final String HTTPS = "https://";

    /**
     * @return - relative path to application from hosting root like myApp/ ,
     *           i.e. GWT.getHostPageBaseURL() minus address part.
     */
    public static String getApplicationURL() {
       String s = GWT.getHostPageBaseURL();
       String baseUrl = GWT.getModuleBaseURL();
       String baseStatic = GWT.getModuleBaseForStaticFiles();
       String info = "HostPageBase: " + s + " | ModuleBase: " + baseUrl + " | ModuleStaticFiles: " + baseStatic;
       logInfo(info);
       browserConsoleLog(info); // GWT console could be disabled, so we always put starting urls to browser console

       // Superdev mode:
       // HostPageBase: http://127.0.0.1:8888/ | ModuleBase: http://127.0.0.1:8888/swinstares/ | ModuleStaticFiles: http://127.0.0.1:9876/swinstares/
       //
       // Tomcat touchbm:
       // HostPageBase: https://localhost:8443/touchbm/ | ModuleBase: https://localhost:8443/touchbm/swinstares/ | ModuleStaticFiles: https://localhost:8443/touchbm/swinstares/
       //

       if (s == null || s.isEmpty()) return "";
       if (s.startsWith(HTTP)) s = s.substring(HTTP.length());
       else if (s.startsWith(HTTPS)) s = s.substring(HTTPS.length());
       int p = s.indexOf('/');
       if (p == -1) return ""; // something strange
       s = s.substring(p).trim();
       return s;
    }

    public static native void browserConsoleLog(String msg) /*-{
        if ($wnd.console) $wnd.console.log(msg);
    }-*/;

    /**
     * For CORS, see <a href="http://stackoverflow.com/a/11379802/714136">window.location.host vs window.location.hostname</a>
     **/
    public static native String getAppOrigin() /*-{
        return $wnd.location.protocol + "//" + $wnd.location.hostname;
    }-*/;

    /**
     * @param url - must comply with SOP (see <a href="http://en.wikipedia.org/wiki/Same-origin_policy">Same-Origin Policy</a>)
     * or server must support CORS response.
     */
    public static void httpGet(final String url, int timeoutMillis, final Callback<String, String> done) {
        if (Empty.is(url)) {
            if (done != null) done.onSuccess(null);
            return;
        }
        RequestBuilder builder = new RequestBuilder(RequestBuilder.GET, url);
        try {
            //builder.setHeader("Origin", getAppOrigin()); - it's already populated by GWT so CORS ready
            builder.setTimeoutMillis(timeoutMillis);
            builder.sendRequest(null/*requestData*/, new RequestCallback() {

                @Override
                public void onResponseReceived(Request request, Response response) {
                    int status = response.getStatusCode();
                    if (status == 200) {
                        String s = response.getText();
                        if (done != null) done.onSuccess(s);
                    } else {
                        String s = url + "  >> Response status: " + String.valueOf(status);
                        GwtUtils.logWarn(s);
                        if (done != null) done.onFailure(s);
                    }
                }

                @Override
                public void onError(Request request, Throwable exception) {
                    GwtUtils.logError(url, exception);
                    if (done != null) done.onFailure(url + "  >> " + exception.getMessage());
                }});
        } catch (RequestException e) {
            GwtUtils.logError(url, e);
            if (done != null) done.onFailure(url + "  >> " + e.getMessage());
        }
    }

    public static void httpGetList(final String url, int timeoutMillis,
                                   final Callback<List<String>, String> done) {

        httpGet(url, timeoutMillis, new Callback<String, String>() {

            @Override
            public void onFailure(String reason) {
                if (done != null) done.onFailure(reason);
            }

            @Override
            public void onSuccess(String result) {
                if (done == null) return;
                if (Empty.is(result)) {
                    done.onSuccess(null);
                } else {
                    List<String> lst = new ArrayList<>();
                    String[] arr = result.split("\n");
                    for (int i = 0; i < arr.length; i++) lst.add(arr[i].trim());
                    done.onSuccess(lst);
                }
            }});
    }

    public static void httpGetJsonp(final String url, final Callback<List<String>, String> done) {
        if (Empty.is(url)) {
            if (done != null) done.onSuccess(null);
            return;
        }
        JsonpRequestBuilder builder = new JsonpRequestBuilder();

        builder.requestObject(url, new AsyncCallback<JsArrayString>() {

            @Override
            public void onFailure(Throwable caught) {
                GwtUtils.logError(url, caught);
                if (done != null) done.onFailure(url + "  >> " + caught.getMessage());
            }

            @Override
            public void onSuccess(JsArrayString result) {
                if (done != null) {
                    if (result == null || result.length() == 0) {
                        done.onSuccess(null);
                    } else {
                        List<String> lst = new ArrayList<>(result.length());
                        for (int i = 0; i < result.length(); i++) lst.add(result.get(i).trim());
                        done.onSuccess(lst);
                    }
                }
            }
        });
    }

    private static Map<String, Style.Display> convertDisplay = null;

    public static Style.Display stringToDisplayStyle(String displayStyle) {
        if (Empty.is(displayStyle)) return null;
        if (convertDisplay == null) {
            Display[] vals = Style.Display.values();
            final Map<String, Display> disp = new HashMap<>(vals.length);
            for (Display v : vals) {
                disp.put(v.getCssName(), v);
            }
            convertDisplay = disp;
        }
        return convertDisplay.get(displayStyle);
    }

    /**
     * It looks like throwing an exception (e.g. <code>throw new RuntimeException(...)</code>)
     * doesn't work. It gets replaced internally with {@link java.lang.JsException} before reaching
     * UncaughtExceptionHandler and looses all information.
     * Also, throwing an error when processing a failure - which is often already a result of an exception -
     * is unnecessary.
     */
    public static void defaultExceptionProcessing(Throwable t) {
        GWT.getUncaughtExceptionHandler().onUncaughtException(t);
    }

}
