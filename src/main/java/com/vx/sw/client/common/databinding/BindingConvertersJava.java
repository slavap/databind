package com.vx.sw.client.common.databinding;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.jboss.errai9.databinding.client.api.Converter;
import org.jboss.errai9.databinding.client.api.DefaultConverter;

import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.i18n.shared.DateTimeFormat.PredefinedFormat;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Widget;
import com.vx.sw.client.common.Empty;

public class BindingConvertersJava {

    private BindingConvertersJava() {} // static class

    /** Converter with knowledge about its widget, can be useful in asynchronous cases. */
    public static interface WidgetConverter<M, W> extends Converter<M, W> {
        Widget getWidget();
        void setWidget(Widget w);
    }

    /**
     * Allows binding to Set&lt;String&gt;
     */
    public static class SetToStringConverter implements Converter<Set<String>, String> {

        private String separator = ",";

        @Override
        public Set<String> toModelValue(String widgetValue) {
            if (widgetValue == null || widgetValue.isEmpty() || widgetValue.trim().isEmpty()) {
                return null;
            }
            String[] arr = widgetValue.split(separator);
            Set<String> rslt = new HashSet<>(arr.length);
            for (String s : arr) {
                s = s.trim();
                if (s.isEmpty()) continue;
                rslt.add(s);
            }
            return rslt;
        }

        @Override
        public String toWidgetValue(Set<String> modelValue) {
            if (modelValue == null || modelValue.isEmpty()) return null;
            Iterator<String> iter = modelValue.iterator();
            StringBuilder sb = null;
            while (iter.hasNext()) {
                String s = iter.next();
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
        public Class<Set<String>> getModelType() {
            Class<Set<String>> rslt = (Class) Set.class;
            return rslt;
        }

        @Override
        public Class<String> getComponentType() {
            return String.class;
        }

    }

    public static final SetToStringConverter SET_TO_COMMA_STR = new SetToStringConverter();

    /**
     * Can be used for boolean (true means empty), or Number (null or 0 means empty),
     * or String, or Collection properties.
     * <br> Converter parameters:
     * <br> str1 - non-empty text, ignored if showNotEmptyModelValue == true.
     * <br> str2 - empty text.
     *
     */
    public static class EmptyConverter extends BindingUtils.ConverterParamsImpl
            implements Converter<Object, String> {

        private boolean showNotEmptyModelValue;

        private Class<?> modelClazz = Object.class;

        @Override
        public Object toModelValue(String widgetValue) {
            throw new UnsupportedOperationException("EmptyConverter is one-way only");
        }

        protected boolean isEmptyModelValue(Object modelValue) {
            if (modelValue == null) return true;
            if (modelValue instanceof Collection) {
                return ((Collection<?>) modelValue).isEmpty();
            } else if (modelValue instanceof String) {
                return ((String) modelValue).isEmpty();
            } else if (modelValue instanceof Boolean) {
                Boolean v = (Boolean) modelValue;
                return (v != null && v == true);
            } else if (modelValue instanceof Number) {
                Number v = (Number) modelValue;
                return (v == null || v != null && v.equals(0));
            }
            return false;
        }

        @Override
        public String toWidgetValue(Object modelValue) {
            return isEmptyModelValue(modelValue) ? str2
                    : (showNotEmptyModelValue ? modelValue.toString() : str1);
        }

        public boolean isShowNotEmptyModelValue() {
            return showNotEmptyModelValue;
        }

        /**
         * Will ignore str1 value and show model value instead.
         * <p><pre>
         * &lt;ui:with type="com.vx.sw.client.common.ErraiConverters.EmptyConverter"
         *   field="myConv1"&gt;
         *   &lt;ui:attributes showNotEmptyModelValue="true" /&gt;
         * &lt;/ui:with&gt;
         * </pre></p>
         **/
        public void setShowNotEmptyModelValue(boolean showNotEmptyModelValue) {
            this.showNotEmptyModelValue = showNotEmptyModelValue;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Class<Object> getModelType() {
            return (Class<Object>) modelClazz;
        }

        @Override
        public Class<String> getComponentType() {
            return String.class;
        }

        public Class<?> getModelClazz() {
            return modelClazz;
        }

        public void setModelClazz(Class<?> modelClazz) {
            this.modelClazz = modelClazz;
        }

        public void setModelClassName(String value) {
            if (Empty.is(value)) return;
            switch (value) {
            case "int":
                setModelClazz(Integer.class);
                break;
            case "double":
                setModelClazz(Double.class);
                break;
            case "string":
                setModelClazz(String.class);
                break;
            case "date":
                setModelClazz(Date.class);
                break;
            }
        }
    }

    @DefaultConverter
    public static class IntegerStringConverter implements Converter<Integer, String> {
        @Override
        public Integer toModelValue(String widgetValue) {
            if (widgetValue == null || widgetValue.isEmpty()) return null;
            try {
                return Integer.decode(widgetValue);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String toWidgetValue(Integer modelValue) {
            return modelValue == null ? null : modelValue.toString();
        }

        @Override
        public Class<Integer> getModelType() {
            return Integer.class;
        }

        @Override
        public Class<String> getComponentType() {
            return String.class;
        }
    }

    public static final Converter<Integer, String> INTEGER = new IntegerStringConverter();

    @DefaultConverter
    public static class LongStringConverter implements Converter<Long, String> {
        @Override
        public Long toModelValue(String widgetValue) {
            if (widgetValue == null || widgetValue.isEmpty()) return null;
            try {
                return Long.decode(widgetValue);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String toWidgetValue(Long modelValue) {
            return modelValue == null ? null : modelValue.toString();
        }

        @Override
        public Class<Long> getModelType() {
            return Long.class;
        }

        @Override
        public Class<String> getComponentType() {
            return String.class;
        }
    }

    public static final Converter<Long, String> LONG = new LongStringConverter();

    @DefaultConverter
    public static class DoubleStringConverter implements Converter<Double, String> {
        @Override
        public Double toModelValue(String widgetValue) {
            if (widgetValue == null || widgetValue.isEmpty()) return null;
            try {
                return Double.valueOf(widgetValue);
            } catch (Exception e) {
                return null;
            }
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

    public static final String DOUBLE_REGEX = "^[-+]?[0-9]*\\.?[0-9]+$";
    public static final String DOUBLE_REGEX_EXPLAIN = "123.99";

    public static final Converter<Double, String> DOUBLE = new DoubleStringConverter();

    protected static class BaseDateTimeConverter implements Converter<Date, String> {

        protected DateTimeFormat format;

        @UiConstructor
        public BaseDateTimeConverter(DateTimeFormat format) {
            this.format = format;
        }

        @Override
        public Date toModelValue(String widgetValue) {
            if (format == null || widgetValue == null || widgetValue.isEmpty()) return null;
            try {
                return format.parse(widgetValue);
            } catch (Exception e) {
                return null;
            }
        }

        @Override
        public String toWidgetValue(Date modelValue) {
            return format != null && modelValue != null ? format.format(modelValue) : null;
        }

        @Override
        public Class<Date> getModelType() {
            return Date.class;
        }

        @Override
        public Class<String> getComponentType() {
            return String.class;
        }
    }

    public static class DateTimeConverter extends BaseDateTimeConverter {
        @UiConstructor
        public DateTimeConverter(String format) {
            super(DateTimeFormat.getFormat(format));
        }
    }

    public static class DateTimeConverterPredefined extends BaseDateTimeConverter {
        @UiConstructor
        public DateTimeConverterPredefined(PredefinedFormat format) {
            super(DateTimeFormat.getFormat(format));
        }
    }

    public static final Converter<Date, String> DATE_TIME_LONG =
            new DateTimeConverterPredefined(PredefinedFormat.DATE_TIME_LONG);

    public static final Converter<Date, String> DATE_TIME_MEDIUM =
            new DateTimeConverterPredefined(PredefinedFormat.DATE_TIME_MEDIUM);

    public static final Converter<Date, String> DATE_TIME_SHORT =
            new DateTimeConverterPredefined(PredefinedFormat.DATE_TIME_SHORT);

    public static final Converter<Date, String> DATE_LONG =
            new DateTimeConverterPredefined(PredefinedFormat.DATE_LONG);

    public static final Converter<Date, String> DATE_MEDIUM =
            new DateTimeConverterPredefined(PredefinedFormat.DATE_MEDIUM);

    public static final Converter<Date, String> DATE_SHORT =
            new DateTimeConverterPredefined(PredefinedFormat.DATE_SHORT);

    public static final Converter<Date, String> HOUR24_MINUTE_SECOND =
            new DateTimeConverterPredefined(PredefinedFormat.HOUR24_MINUTE_SECOND);
}