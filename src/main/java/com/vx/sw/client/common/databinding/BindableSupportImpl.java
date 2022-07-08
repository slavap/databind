package com.vx.sw.client.common.databinding;

import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.errai9.databinding.client.BindableProxyAgent;
import org.jboss.errai9.databinding.client.BindableSupport;
import org.jboss.errai9.databinding.client.PropertyType;

import com.google.gwt.core.client.JavaScriptObject;
import com.vx.sw.client.common.js.JSON;

import gwt.interop.utils.shared.collections.Array;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class BindableSupportImpl implements BindableSupport {

    private static final Map<Object, MyAgent<?>> agentsObserv = new IdentityHashMap<>();
    private static final Map<Object, MyAgent<?>> agentsTarget = new IdentityHashMap<>();

    private static Object dontFireUpdateEventProxy;
    private static String dontFireUpdateEventProperty;

    private class MyAgent<T> extends BindableProxyAgent<T> {

        protected MyAgent(T proxy) {
            super(proxy);
        }

        @Override
        protected T getProxy() {
            return super.getProxy();
        }

        @Override
        protected void setProxy(T proxy) {
            super.setProxy(proxy);
        }

        void setPropertyTypes(Map<String, PropertyType> value) {
            super.propertyTypes.clear();
            if (value != null) super.propertyTypes.putAll(value);
        }

        @Override
        protected void copyValues() {
            super.copyValues();
        }

        @Override
        protected void updateWidgetsAndFireEvents() {
            super.updateWidgetsAndFireEvents();
        }

        @Override
        protected <P> void updateWidgetsAndFireEvent(boolean sync, String property, P oldValue, P newValue) {
            super.updateWidgetsAndFireEvent(sync, property, oldValue, newValue);
        }

        /**
         * @param target - original "raw/pure" object.
         * @param property - this is path in thisAgent.proxy, not in proxy/target parameters!
         *        <br> E.g. thisAgent(model.guest.client), and we call: model.guest.client = newClient;
         *        <br> proxy/target is model.guest, but property is "guest.client", so property makes sense for thisAgent only.
         */
        public void onPropertyChanged(Object proxy, Object target, String property, Object oldValue, Object newValue) {
            if (oldValue != null && newValue != null && oldValue != newValue) {
                if ("object".equals(Js.typeof(oldValue))) {
                    T myProxy = this.getProxy();
                    if (myProxy != null) {
                        Object newObservable = JsDataBinder.getFieldChainValue(myProxy, property);
                        if (newObservable != null && JsDataBinder.isProxy(newObservable)) {
                            @SuppressWarnings("unchecked")
                            MyAgent<Object> agent = (MyAgent<Object>) findAgentDeep(oldValue);
                            if (agent != null) {
                                Object oldObservable = agent.getProxy();
                                if (oldObservable != newObservable) {
                                    MyAgent<?> a = findAgent(newObservable);
                                    if (a != null) { // it's too late for reconfiguration of agent
                                        agent.updateBindersProxy(newObservable);
                                        removeAgentDeep(oldValue);
                                    } else { // let's reconfigure
                                        JsDataBinder.observe(newObservable, agent::onPropertyChanged);
                                        agent.setProxy(newObservable);
                                        Map<String, PropertyType> props = getBeanProperties(newObservable);
                                        agent.setPropertyTypes(props);
                                        agent.copyValues();
                                        agent.updateBindersProxy(newObservable);
                                        removeAgentDeep(oldValue);
                                        addAgent(newObservable, agent);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (proxy == dontFireUpdateEventProxy && property.equals(dontFireUpdateEventProperty)) {
                // in that case update event from here is not needed
            } else {
                fireUpdateEvent(property, oldValue, newValue);
            }
        }

        private void fireUpdateEvent(String property, Object oldValue, Object newValue) {
            boolean isList = JsDataBinder.isArray(oldValue) || JsDataBinder.isArray(newValue);
            Object oldV = JsDataBinder.isProxy(oldValue) ? JsDataBinder.getTarget(oldValue) : oldValue;
            Object newV = JsDataBinder.isProxy(newValue) ? JsDataBinder.getTarget(newValue) : newValue;
            updateWidgetsAndFireEvent(isList/*sync*/, property, oldV, newV);
        }
    }

    private static <T> void addAgent(T observable, MyAgent<T> agent) {
        Map<Object, MyAgent<?>> aObserv = agentsObserv; // just for better in browser debug
        aObserv.put(observable, agent);
        Object target = JsDataBinder.getTarget(observable);
        Map<Object, MyAgent<?>> aTarget = agentsTarget; // just for better in browser debug
        aTarget.put(target, agent);
    }

    private static <T> MyAgent<?> findAgent(T model) {
        Map<Object, MyAgent<?>> aObserv = agentsObserv; // just for better in browser debug
        MyAgent<?> a = aObserv.get(model);
        return a;
    }

    private static <T> MyAgent<?> findAgentDeep(T model) {
        MyAgent<?> a = findAgent(model);
        if (a != null) return a;
        Map<Object, MyAgent<?>> aTarget = agentsTarget; // just for better in browser debug
        a = aTarget.get(model);
        return a;

//        for (Entry<Object, MyAgent<?>> i : agents.entrySet()) {
//            Object m = i.getKey();
//            if (m == model) return i.getValue();
//            if (JsDataBinder.getTarget(m) == model) return i.getValue();
//        }
//        return null;
    }

    private static <T> void removeAgentDeep(T model) {
        for (Entry<Object, MyAgent<?>> i : agentsObserv.entrySet()) {
            Object m = i.getKey();
            if (m == model || JsDataBinder.getTarget(m) == model) {
                agentsObserv.remove(m);
                break;
            }
        }
        Object modelTarget = JsDataBinder.getTarget(model);
        agentsTarget.remove(modelTarget);
    }

    private <T> T createAgent(T model) {
        // one agent per observable (they are singletons by nature)
        MyAgent<T> agent = new MyAgent<T>(null);
        T observable = JsDataBinder.observe(model, agent::onPropertyChanged);
        agent.setProxy(observable);
        Map<String, PropertyType> props = getBeanProperties(observable);
        agent.setPropertyTypes(props);
        agent.copyValues();
        addAgent(observable, agent);
        return observable;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T observe(T model) {
        if (JsDataBinder.isProxy(model)) {
            MyAgent<?> a = findAgent(model);
            if (a != null) return model;
        } else {
            MyAgent<?> a = findAgentDeep(model);
            if (a != null) return (T) a.getProxy();
        }
        return createAgent(model);
    }

    @Override
    public boolean isProxy(Object obj) {
        return JsDataBinder.isProxy(obj);
    }

    @Override
    public <T> T unwrap(T proxy) {
        return JsDataBinder.getTarget(proxy);
    }

    @Override
    public Object get(Object proxy, String propertyName) {
        if (!isProxy(proxy)) return null;
        JsPropertyMap<Object> m = Js.cast(proxy);
        return m.get(propertyName);
    }

    @Override
    public void set(Object proxy, String propertyName, Object value) {
        set(proxy, propertyName, value, true/*fireUpdateEvent*/);
    }

    @Override
    public void set(Object proxy, String propertyName, Object value, boolean fireUpdateEvent) {
        if (!isProxy(proxy)) return;
        JsPropertyMap<Object> m = Js.cast(proxy);
        if (fireUpdateEvent) m.set(propertyName, value);
        else {
            dontFireUpdateEventProxy = proxy;
            dontFireUpdateEventProperty = propertyName;
            try {
                m.set(propertyName, value);
            } finally {
                dontFireUpdateEventProxy = null;
                dontFireUpdateEventProperty = null;
            }
        }
    }

    private static PropertyType getBeanProperty(JsPropertyMap<Object> m, String property) {
        Object val = m.get(property);
        if (val == null) {
            return new PropertyType(Object.class, true/*bindable*/, false/*list*/);
        } else {
            if (JsDataBinder.isArray(val)) {
                return new PropertyType(Array.class, true/*bindable*/, true/*list*/);
            } else {
                String t = Js.typeof(val);
                switch (t) {
                case "string":
                    return new PropertyType(String.class, true/*bindable*/, false/*list*/);
                case "number":
                    return new PropertyType(Double.class, true/*bindable*/, false/*list*/);
                case "boolean":
                    return new PropertyType(Boolean.class, true/*bindable*/, false/*list*/);
                case "object":
                    if (!(val instanceof JavaScriptObject)) {
                        // native js type may have non-js(i.e. java) field, like Integer, Date, ...
                        return new PropertyType(val.getClass(), true/*bindable*/, false/*list*/);
                    } else {
                        return new PropertyType(Object.class, true/*bindable*/, false/*list*/);
                    }
                case "function":
                    return null; // no binding is supposed to be used for functions
                default:
                    return new PropertyType(Object.class, true/*bindable*/, false/*list*/);
                }
            }
        }
    }

    @Override
    public PropertyType getBeanProperty(Object proxy, String property) {
        if (!isProxy(proxy)) return null;
        JsPropertyMap<Object> m = Js.cast(unwrap(proxy));
        return getBeanProperty(m, property);
    }

    /**
     * There is a huge difference between Errai generated bindings and native js bindings.
     * <br> There is no info about all possible fields of js object (because there is no reflection/runtime metainfo).
     * <br> So it means that ANY field is literally possible, that's why it would be wise
     * to "predefine" bindable fields before model usage (by populating with empty values).
     */
    @Override
    public Map<String, PropertyType> getBeanProperties(Object proxy) {
        Map<String, PropertyType> rslt = new LinkedHashMap<>();
        if (isProxy(proxy)) {
            JsPropertyMap<Object> m = Js.cast(unwrap(proxy));
            m.forEach(key -> {
                PropertyType pt = getBeanProperty(m, key);
                if (pt != null) rslt.put(key, pt);
            });
        }
        return rslt;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> BindableProxyAgent<T> getBindableProxyAgent(Object proxy) {
        if (proxy == null) return null;
        MyAgent<?> a = findAgent(proxy);
        if (a != null) return (BindableProxyAgent<T>) a;
        else { // could be non-proxied OR proxied, though not by observe(), but as nested in js code.
            proxy = createAgent(proxy);
            a = findAgent(proxy);
            return (BindableProxyAgent<T>) a;
        }
    }

    @Override
    public void updateWidgets(Object proxy) {
        if (proxy == null) return;
        final MyAgent<?> agent;
        if (isProxy(proxy)) {
            agent = (MyAgent<Object>) getBindableProxyAgent(proxy);
        } else {
            agent = findAgentDeep(proxy);
        }
        if (agent != null) agent.updateWidgetsAndFireEvents();
    }

    @Override
    public <T> T deepUnwrap(T proxy) {
        String s = JSON.stringify(unwrap(proxy));
        return JSON.parse(s);
    }

    @Override
    public <T> void agentUnbound(BindableProxyAgent<T> agent) {
        // currently we cannot do anything meaningful, but removing change handler from native
        // databinding could be implemented, then agent could be unregistered and removed from agents.
    }

}
