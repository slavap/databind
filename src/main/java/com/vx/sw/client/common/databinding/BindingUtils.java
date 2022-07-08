package com.vx.sw.client.common.databinding;

import org.jboss.errai9.databinding.client.Bindable;
import org.jboss.errai9.databinding.client.BindableProxyAgent;
import org.jboss.errai9.databinding.client.PropertyChangeUnsubscribeHandle;
import org.jboss.errai9.databinding.client.api.DataBinder;
import org.jboss.errai9.databinding.client.api.handler.property.PropertyChangeEvent;
import org.jboss.errai9.databinding.client.api.handler.property.PropertyChangeHandler;

import com.vx.sw.client.common.js.JSON;

import jsinterop.base.Any;
import jsinterop.base.JsPropertyMap;

public class BindingUtils {

    private BindingUtils() {} // static class

    public static void copy(Object srcModel, Object destModel) {
        String json = JSON.stringify(Bindable.support.unwrap(srcModel));
        DataBinder<?> dataBinder = DataBinder.forModel(destModel);
        Object mm = dataBinder.getModel();
        JsPropertyMap<Any> srcMap = JSON.parse(json);
        srcMap.forEach(key -> {
            if (!"$H".equals(key)) {
                Any v = srcMap.get(key);
                JsDataBinder.defineFieldChain(mm, key, v, true/*forceSet*/);
            }
        });
    }

    public static boolean targetEquals(Object o1, Object o2) {
        if (o1 == o2) return true;
        if (o1 == null || o2 == null) return false;
        Object t1 = JsDataBinder.getTarget(o1);
        Object t2 = JsDataBinder.getTarget(o2);
        return t1 == t2;
    }

    /**
     * Be <b>absolutely CAREFUL</b> with calling notifyPropsChanged() from inside the handler!
     * <br> It will lead to infinite notification loop. The only possible pattern is:
     * <br> {@code if (notifyPropsChangedCount == 0) notifyPropsChanged(); }
     */
    public static class PropChangeHelper<M, P> {

        private final String property;
        private final Runnable onPropChange;
        private PropertyChangeHandler<P> propChangeHandler;

        private M model;
        private PropertyChangeHandler<P> internPropChangeHandler;
        private PropertyChangeUnsubscribeHandle internPropChangeUnsubscribe;

        public PropChangeHelper(String property, Runnable onPropChange) {
            this.property = property;
            this.onPropChange = onPropChange;
        }

        public PropChangeHelper(String property, PropertyChangeHandler<P> propChangeHandler) {
            this(property, (Runnable) null);
            this.propChangeHandler = propChangeHandler;
        }

        public PropChangeHelper(String property) {
            this(property, (Runnable) null);
        }

        /**
         * Checks that specified newModel is set as base for change handler,
         * i.e. automatically switches from "old" model to "new" one.
         */
        public void checkPropChangeHandler(M newModel) {
            if (model == newModel) return;
            if (newModel != null && !(JsDataBinder.isProxy(newModel))) {
                newModel = Bindable.support.observe(newModel);
            }
            if (model != null && internPropChangeUnsubscribe != null) {
                internPropChangeUnsubscribe.unsubscribe();
                internPropChangeUnsubscribe = null;
                internPropChangeHandler = null;
            }
            model = newModel;
            if (model != null) {
                BindableProxyAgent<M> agent = Bindable.support.getBindableProxyAgent(model);
                if (agent != null) {
                    internPropChangeHandler = new PropertyChangeHandler<P>() {
                        @Override
                        public void onPropertyChange(PropertyChangeEvent<P> event) {
                            PropChangeHelper.this.onPropertyChange(event);
                            if (onPropChange != null) onPropChange.run();
                            if (propChangeHandler != null) {
                                propChangeHandler.onPropertyChange(event);
                            }
                        }
                    };
                    internPropChangeUnsubscribe = agent.addPropertyChangeHandler(property, internPropChangeHandler);
                }
            }

        }

        /** @param event */
        protected void onPropertyChange(PropertyChangeEvent<P> event) {
            // nothing, can be overridden by descendants
        }

        public PropertyChangeHandler<P> getPropChangeHandler() {
            return propChangeHandler;
        }

        public void setPropChangeHandler(PropertyChangeHandler<P> propChangeHandler) {
            this.propChangeHandler = propChangeHandler;
        }

        public M getModel() {
            return model;
        }
    }

    public interface ConverterParams {
        //@formatter:off

        String getStr1();
        void setStr1(String value);

        String getStr2();
        void setStr2(String value);

        String getStr3();
        void setStr3(String value);

        String getStr4();
        void setStr4(String value);

        String getStr5();
        void setStr5(String value);

        String getStr6();
        void setStr6(String value);

        //@formatter:on
    }

    public static class ConverterParamsImpl implements ConverterParams {

        protected String str1;
        protected String str2;
        protected String str3;
        protected String str4;
        protected String str5;
        protected String str6;

        public ConverterParamsImpl() {
        }

        @Override
        public String getStr1() {
            return str1;
        }

        @Override
        public void setStr1(String str1) {
            this.str1 = str1;
        }

        @Override
        public String getStr2() {
            return str2;
        }

        @Override
        public void setStr2(String str2) {
            this.str2 = str2;
        }

        @Override
        public String getStr3() {
            return str3;
        }

        @Override
        public void setStr3(String str3) {
            this.str3 = str3;
        }

        @Override
        public String getStr4() {
            return str4;
        }

        @Override
        public void setStr4(String str4) {
            this.str4 = str4;
        }

        @Override
        public String getStr5() {
            return str5;
        }

        @Override
        public void setStr5(String str5) {
            this.str5 = str5;
        }

        @Override
        public String getStr6() {
            return str6;
        }

        @Override
        public void setStr6(String str6) {
            this.str6 = str6;
        }
    }

}
