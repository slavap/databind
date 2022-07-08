package com.vx.sw.client.common.databinding;

import org.jboss.errai9.databinding.client.api.Converter;

import gwt.interop.utils.shared.collections.Array;
import gwt.interop.utils.shared.collections.ArrayFactory;

public class BindingConverters {

    private BindingConverters() {} // static class

    public static class DoubleIntegerConverter implements Converter<Double, Integer> {
        @Override
        public Double toModelValue(Integer widgetValue) {
            if (widgetValue == null) return null;
            return Double.valueOf(widgetValue);
        }

        @Override
        public Integer toWidgetValue(Double modelValue) {
            return modelValue == null ? null : Integer.valueOf(modelValue.intValue());
        }

        @Override
        public Class<Double> getModelType() {
            return Double.class;
        }

        @Override
        public Class<Integer> getComponentType() {
            return Integer.class;
        }
    }

    public static final Converter<Double, Integer> DOUBLE_INTEGER = new DoubleIntegerConverter();

    /**
     * Allows binding to Set&lt;String&gt;
     */
    public static class ArrayToStringConverter implements Converter<Array<String>, String> {

        private String separator = ",";

        @Override
        public Array<String> toModelValue(String widgetValue) {
            if (widgetValue == null || widgetValue.isEmpty() || widgetValue.trim().isEmpty()) {
                return ArrayFactory.create();
            }
            String[] arr = widgetValue.split(separator);
            Array<String> rslt = ArrayFactory.create();
            for (String s : arr) {
                s = s.trim();
                if (s.isEmpty()) continue;
                rslt.push(s);
            }
            return rslt;
        }

        @Override
        public String toWidgetValue(Array<String> modelValue) {
            if (modelValue == null || modelValue.getLength() == 0) return null;
            Iterable<String> iter = modelValue.asIterable();
            StringBuilder sb = null;
            for (String s : iter) {
                if (sb == null) {
                    sb = new StringBuilder(s);
                } else {
                    sb.append(separator);
                    sb.append(s);
                }
            }
            return sb != null ? sb.toString() : null;
        }

        public String getSeparator() {
            return separator;
        }

        public void setSeparator(String separator) {
            this.separator = separator;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        @Override
        public Class<Array<String>> getModelType() {
            Class<Array<String>> rslt = (Class) Array.class;
            return rslt;
        }

        @Override
        public Class<String> getComponentType() {
            return String.class;
        }

    }

    public static final ArrayToStringConverter ARR_TO_COMMA_STR = new ArrayToStringConverter();


    public static class DoubleStringConverter implements Converter<Double, String> {
        @Override
        public Double toModelValue(String widgetValue) {
            if (widgetValue == null || widgetValue.isEmpty() || widgetValue.trim().isEmpty()) {
                return null;
            }

            return Double.valueOf(widgetValue);
        }

        @Override
        public String toWidgetValue(Double modelValue) {
            return modelValue == null ? null : modelValue.toString();
        }

        @Override
        public Class<Double> getModelType() {
            return Double.class;
        }

        @Override
        public Class<String> getComponentType() {
            return String.class;
        }
    }

    public static final Converter<Double, String> DOUBLE_STRING = new DoubleStringConverter();

}