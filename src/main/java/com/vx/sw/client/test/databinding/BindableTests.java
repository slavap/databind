package com.vx.sw.client.test.databinding;

import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jboss.errai9.databinding.client.Bindable;
import org.jboss.errai9.databinding.client.api.Convert;
import org.jboss.errai9.databinding.client.api.Converter;
import org.jboss.errai9.databinding.client.api.DataBinder;
import org.jboss.errai9.databinding.client.api.handler.property.PropertyChangeHandler;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.ui.TextBox;
import com.vx.sw.client.common.GwtUtils;
import com.vx.sw.client.common.databinding.JsDataBinder;
import com.vx.sw.client.common.js.JSON;
import com.vx.sw.client.common.js.UniKey;
import com.vx.sw.client.test.domain.DtoTestEnum;
import com.vx.sw.client.test.nativ.NativeChild11;
import com.vx.sw.client.test.nativ.NativeDto1;
import com.vx.sw.client.test.nativ.NativeDtoFactory;

import gwt.interop.utils.client.collections.JsArray;
import jsinterop.base.Any;
import jsinterop.base.Js;
import jsinterop.base.JsPropertyMap;

public class BindableTests {

    private BindableTests() {} // static class

    public static void testNativeDataBinding() {
        NativeDto1 dto = NativeDtoFactory.createNativeDto1();
        dto = JsDataBinder.observe(dto, (proxy, target, property, oldVal, newVal) -> {
            GwtUtils.logInfo(property + " | Old: " + JSON.stringify(oldVal)
                    + " | New: " + JSON.stringify(newVal));
        });
        dto.intVal = 11;
        dto.doubleVal = 444d;
        dto.setDateFrom(new Date());
        dto.setEnum1(DtoTestEnum.Z);
        dto.setEnumSet(EnumSet.allOf(DtoTestEnum.class));
    }

    private static final PropertyChangeHandler<Object> dfltChangeHandler = event -> {
        GwtUtils.logInfo(event.getPropertyName()
                + " | Old: " + (event.getOldValue() instanceof JavaScriptObject ? JSON.stringify(event.getOldValue()) : event.getOldValue())
                + " | New: " + (event.getNewValue() instanceof JavaScriptObject ? JSON.stringify(event.getNewValue()) : event.getNewValue()));
    };

    public static void test00() {
        NativeDto1 m = NativeDtoFactory.createNativeDto1();
        DataBinder<NativeDto1> dataBinder = DataBinder.forModel(m);
        m = dataBinder.getModel();
        dataBinder.addPropertyChangeHandler(dfltChangeHandler);
        dataBinder.addPropertyChangeHandler("mapNum.*", dfltChangeHandler);
        dataBinder.addPropertyChangeHandler("embeddedObj1.**", dfltChangeHandler);

        dataBinder.addPropertyChangeHandler("arrNum.*", event -> {
            GwtUtils.logInfo("arrNum.* change notif: " + event.getPropertyName()
                    + " | Old: " + (event.getOldValue() instanceof JavaScriptObject ? JSON.stringify(event.getOldValue()) : event.getOldValue())
                    + " | New: " + (event.getNewValue() instanceof JavaScriptObject ? JSON.stringify(event.getNewValue()) : event.getNewValue()));
        });
        dataBinder.addPropertyChangeHandler("arrNum", event -> {
            String s = "";
            if (Js.undefined() == event.getOldValue()) s += " | Old is undefined;";
            if (Js.undefined() == event.getNewValue()) s += " | New is undefined;";

            GwtUtils.logInfo("arrNum change notif: " + event.getPropertyName()
                    + " | Old: " + (event.getOldValue() instanceof JavaScriptObject ? JSON.stringify(event.getOldValue()) : event.getOldValue())
                    + " | New: " + (event.getNewValue() instanceof JavaScriptObject ? JSON.stringify(event.getNewValue()) : event.getNewValue())
                    + s);
        });
        dataBinder.addPropertyChangeHandler("children11", event -> {
            String s = "";
            if (Js.undefined() == event.getOldValue()) s += " | Old is undefined;";
            if (Js.undefined() == event.getNewValue()) s += " | New is undefined;";

            GwtUtils.logInfo("children11 change notif: " + event.getPropertyName()
                    + " | Old: " + (event.getOldValue() instanceof JavaScriptObject ? JSON.stringify(event.getOldValue()) : event.getOldValue())
                    + " | New: " + (event.getNewValue() instanceof JavaScriptObject ? JSON.stringify(event.getNewValue()) : event.getNewValue())
                    + s);
        });
        dataBinder.addPropertyChangeHandler("children11.*", event -> {
            String s = "";
            if (Js.undefined() == event.getOldValue()) s += " | Old is undefined;";
            if (Js.undefined() == event.getNewValue()) s += " | New is undefined;";

            GwtUtils.logInfo("children11.* change notif: " + event.getPropertyName()
                    + " | Old: " + (event.getOldValue() instanceof JavaScriptObject ? JSON.stringify(event.getOldValue()) : event.getOldValue())
                    + " | New: " + (event.getNewValue() instanceof JavaScriptObject ? JSON.stringify(event.getNewValue()) : event.getNewValue())
                    + s);
        });
        dataBinder.addPropertyChangeHandler("children11.**", event -> {
            String s = "";
            if (Js.undefined() == event.getOldValue()) s += " | Old is undefined;";
            if (Js.undefined() == event.getNewValue()) s += " | New is undefined;";

            GwtUtils.logInfo("children11.** change notif: " + event.getPropertyName()
                    + " | Old: " + (event.getOldValue() instanceof JavaScriptObject ? JSON.stringify(event.getOldValue()) : event.getOldValue())
                    + " | New: " + (event.getNewValue() instanceof JavaScriptObject ? JSON.stringify(event.getNewValue()) : event.getNewValue())
                    + s);
        });

        m.embeddedObj2.name2 = "Garuspic";

        m.arrNum.push(443d);
        m.arrNum.setAt(m.arrNum.getLength() - 1, 443d); // the same value
        m.arrNum.setAt(0, 777d);
        m.arrNum = JsArray.create(111d, 222d, 333d);
        m.arrNum.setLength(2);
        m.arrNum.shift();
        m.arrNum.push(678d);

        m.children11.getAt(0).name1 = "Coco";
        NativeChild11 ch1 = new NativeChild11();
        ch1.id = 2;
        ch1.name1 = "Max";
        m.children11.setAt(0, ch1);

        m.mapNum.set("test00-n0", 1212d);
        m.mapNum.delete("n1");
        m.setDateFrom(new Date());
        m.setEnum1(DtoTestEnum.Z);
        m.setEnumSet(EnumSet.noneOf(DtoTestEnum.class));
        m.embeddedObj1.name1 = "Sara Hancock";
        m.embeddedObj1.sibling = new NativeChild11();
        m.embeddedObj1.sibling.name1 = "John Hancock";

//      ===== RESULTS log: =====
//      embeddedObj2.name2 | Old: Paul | New: Garuspic
//      arrNum.3 | Old: undefined | New: 443
//      arrNum.* change notif: 3 | Old: undefined | New: 443
//      arrNum change notif: arrNum | Old: undefined | New: [2.71,3.14,9.81,443] | Old is undefined;
//      arrNum.0 | Old: 2.71 | New: 777
//      arrNum.* change notif: 0 | Old: 2.71 | New: 777
//      arrNum change notif: arrNum | Old: undefined | New: [777,3.14,9.81,443] | Old is undefined;
//      arrNum change notif: arrNum | Old: [777,3.14,9.81,443] | New: [111,222,333]
//      arrNum | Old: [777,3.14,9.81,443] | New: [111,222,333]
//      arrNum.length | Old: 3 | New: 2
//      arrNum.* change notif: length | Old: 3 | New: 2
//      arrNum change notif: arrNum | Old: undefined | New: [111,222] | Old is undefined;
//      arrNum.0 | Old: 111 | New: 222
//      arrNum.* change notif: 0 | Old: 111 | New: 222
//      arrNum change notif: arrNum | Old: undefined | New: [222,222] | Old is undefined;
//      arrNum.1 | Old: 222 | New: undefined
//      arrNum.* change notif: 1 | Old: 222 | New: undefined
//      arrNum change notif: arrNum | Old: undefined | New: [222,null] | Old is undefined;
//      arrNum.length | Old: 2 | New: 1
//      arrNum.* change notif: length | Old: 2 | New: 1
//      arrNum change notif: arrNum | Old: undefined | New: [222] | Old is undefined;
//      arrNum.1 | Old: undefined | New: 678
//      arrNum.* change notif: 1 | Old: undefined | New: 678
//      arrNum change notif: arrNum | Old: undefined | New: [222,678] | Old is undefined;
//      children11.0.name1 | Old: Goga | New: Coco
//      children11 change notif: children11 | Old: undefined | New: [{"id":1,"name1":"Coco"}] | Old is undefined;
//      children11.* change notif: 0.name1 | Old: Goga | New: Coco
//      children11.** change notif: 0.name1 | Old: Goga | New: Coco
//      children11.0 | Old: {"id":1,"name1":"Coco","$H":335} | New: {"id":2,"name1":"Max"}
//      children11 change notif: children11 | Old: undefined | New: [{"id":2,"name1":"Max"}] | Old is undefined;
//      children11.* change notif: 0 | Old: {"id":1,"name1":"Coco","$H":335} | New: {"id":2,"name1":"Max"}
//      children11.** change notif: 0 | Old: {"id":1,"name1":"Coco","$H":335} | New: {"id":2,"name1":"Max"}
//      mapNum.test00-n0 | Old: undefined | New: 1212
//      test00-n0 | Old: undefined | New: 1212
//      mapNum.n1 | Old: 123.45 | New: undefined
//      n1 | Old: 123.45 | New: undefined
//      dateFrom | Old: Tue Jan 29 23:34:13 GMT-800 2019 | New: Fri Sep 06 21:30:05 GMT-700 2019
//      dateFromVal | Old: {"msec":1548833653000,"utc":"Wed, 30 Jan 2019 07:34:13 GMT","$H":338} | New: {"msec":1567830605280,"utc":"Sat, 07 Sep 2019 04:30:05 GMT"}
//      enum1 | Old: Y | New: Z
//      enum1Val | Old: Y | New: Z
//      enumSet | Old: [X, Z] | New: []
//      enumSetVal | Old: ["X","Z"] | New: []
//      embeddedObj1.name1 | Old: John | New: Sara Hancock
//      name1 | Old: John | New: Sara Hancock
//      embeddedObj1.sibling | Old: undefined | New: {}
//      sibling | Old: undefined | New: {}
//      embeddedObj1.sibling.name1 | Old: undefined | New: John Hancock
//      sibling.name1 | Old: undefined | New: John Hancock
    }

    private static class IntegerStringConverter implements Converter<Integer, String> {
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

    private static class DoubleStringConverter implements Converter<Double, String> {
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

    public static void test01() {
        Convert.registerDefaultConverter(Integer.class, String.class, new IntegerStringConverter());
        NativeDto1 m = NativeDtoFactory.createNativeDto1();
        m.i00 = 1;

        DataBinder<NativeDto1> dataBinder = DataBinder.forModel(m);
        m = dataBinder.getModel();
        m.i01 = 2; // will fail onIE11, proxy polyfill does not support "adding on the fly", so all fields must be known at proxy creation time

        TextBox ed = new TextBox();
        dataBinder.bind(ed, "embeddedObj1.id", new DoubleStringConverter());
        ed.setValue("555", true/*fireEvents*/);
        GwtUtils.logInfo("555 into TextBox -> embeddedObj1.id: " + m.embeddedObj1.id);
        dataBinder.getModel().embeddedObj1.id = 888;
        GwtUtils.logInfo("888 into embeddedObj1.id -> TextBox: " + ed.getValue());

        TextBox edI00 = new TextBox();
        dataBinder.bind(edI00, "i00");
        edI00.setValue("7", true/*fireEvents*/);
        GwtUtils.logInfo("7 into TextBox -> i00: " + m.i00);
        dataBinder.getModel().i00 = 8;
        GwtUtils.logInfo("8 into i00 -> TextBox: " + edI00.getValue());
        edI00.setValue("", true/*fireEvents*/);
        GwtUtils.logInfo("empty into TextBox -> i00: " + m.i00);

        TextBox edI01 = new TextBox();
        dataBinder.bind(edI01, "i01");
        edI01.setValue("77", true/*fireEvents*/);
        GwtUtils.logInfo("77 into TextBox -> i01: " + m.i01);
        dataBinder.getModel().i01 = 88;
        GwtUtils.logInfo("88 into i01 -> TextBox: " + edI01.getValue());

        NativeDto1 n1 = new NativeDto1();
        n1 = DataBinder.forModel(n1).getModel();
        n1.i00 = 5;
        n1.setDateFrom(new Date());
        GwtUtils.logInfo(JSON.stringify(Bindable.support.unwrap(n1)));

        GwtUtils.logInfo(JSON.stringify(Bindable.support.unwrap(m)));
    }

    public static void test02() {
        NativeDto1 m = NativeDtoFactory.createNativeDto1();
        Double v1 = JsDataBinder.defineFieldChain(m, "arrObj[1].id");
        Double v2 = JsDataBinder.defineFieldChain(m, "arrObj[2].id", 56d);
        DataBinder<NativeDto1> dataBinder = DataBinder.forModel(m);
        m = dataBinder.getModel();
        dataBinder.addPropertyChangeHandler(dfltChangeHandler);
        v1 = JsDataBinder.defineFieldChain(m, "arrObj[1].id", 33d, true/*forceSet*/);
        v2 = JsDataBinder.defineFieldChain(m, "arrObj[2].id", 69d, true/*forceSet*/);
        GwtUtils.logInfo("v1: " + v1 + "; v2: " + v2 + "\n" + JSON.stringify(Bindable.support.unwrap(m)));
    }

    public static void test03() {
        Model00 m = new Model00();
        m.id = 1;
        m.name = "test01";
        DataBinder<Model00> dataBinder = DataBinder.forModel(m);
        m = dataBinder.getModel();
        dataBinder.addPropertyChangeHandler(dfltChangeHandler);
        m.id = 2;
        m.name = "test02";
        TextBox ed = new TextBox();
        dataBinder.bind(ed, "info");
        ed.setValue("xyz", true/*fireEvents*/);
        String json = JSON.stringify(Bindable.support.unwrap(m));
        GwtUtils.logInfo("m.info: " + m.getInfo() + "; m.class: " + m.getClass().getSimpleName()
                + "\n" + json);

        //!!! FAILS: m = JSON.parse(json);
        //GwtUtils.logInfo("m.info: " + m.getInfo() + "; m.name: " + m.name + "; m.class: " + m.getClass().getSimpleName());

        dataBinder = DataBinder.forModel(new Model00());
        Model00 mm = dataBinder.getModel();
        JsPropertyMap<Any> map = JSON.parse(json);
        map.forEach(key -> {
            if (!"$H".equals(key)) {
                Any v = map.get(key);
                JsDataBinder.defineFieldChain(mm, key, v, true/*forceSet*/);
            }
        });
        json = JSON.stringify(Bindable.support.unwrap(mm));
        GwtUtils.logInfo("mm: " + json);
    }

    public static void test04(){
        NativeDto1 m = NativeDtoFactory.createNativeDto1();
        m.intVal = 111;
        m.strVal = "";
        m.doubleObjVal = null;
        m.booleanObjVal = null;
        String s0 = JSON.stringify(m);
        String s1 = JSON.stringify(m, JSON.NO_NULLS);
        String s2 = JSON.stringify(m, JSON.NO_NULLS_NO_EMPTY_STRS);
        GwtUtils.logInfo("JSON.stringify() results: \n\n" + s0 + "\n\nNO_NULLS\n " + s1
                + "\n\nNO_NULLS_NO_EMPTY_STRS\n" + s2);

        NativeDto1 m1 = NativeDtoFactory.createNativeDto1();
        m1.intVal = 111;
        m1.strVal = null;
        m1.doubleObjVal = null;
        m1.booleanObjVal = null;
        GwtUtils.logInfo("JSON.equals() with emptyEqualsNull: "
                + JSON.equals(NativeDto1.class, m, m1, true/*emptyEqualsNull*/));
    }

    /*public static void test05(){
        NativeDto1 m = NativeDtoFactory.createNativeDto1();
        DataBinder<NativeDto1> dataBinder = DataBinder.forModel(m);
        m = dataBinder.getModel();
        JQMListBindToArray<Double> l = new JQMListBindToArray<>();
        JQMListBindToArray.Renderer<Double> r = new JQMListBindToArray.Renderer<Double>() {

            @Override
            public List<? extends ComplexPanel> create(JQMListBindToArray<Double> list, Double item) {
                JQMListItem uiItem = new JQMListItem(item.toString());
                List<ComplexPanel> lst = new ArrayList<>();
                lst.add(uiItem);
                return lst;
            }

            @Override
            public boolean reuse(JQMListBindToArray<Double> list, Double newItem,
                    List<? extends ComplexPanel> uiItems) {
                for (ComplexPanel i : uiItems) {
                    ((JQMListItem) i).setText(newItem.toString());
                }
                return true;
            }

            @Override
            public boolean onBeforeRefresh(JQMListBindToArray<Double> list) {
                return false;
            }

            @Override
            public void onAfterRefresh(JQMListBindToArray<Double> list) {
            }
        };
        l.setRenderer(r);

        dataBinder.bind(l, "arrNum");

        m.arrNum.push(443d);
        m.arrNum.setAt(m.arrNum.getLength() - 1, 443d); // the same value
        m.arrNum.setAt(0, 777d);
        m.arrNum = JsArray.create(111d, 222d, 333d);
        m.arrNum.setLength(2);
        m.arrNum.shift();
        m.arrNum.push(678d);

        String s = "";
        List<JQMListItem> items = l.getItems();
        for (JQMListItem i : items) {
            if (i == null) continue;
            s += i.getText() + "; ";
        }
        GwtUtils.logInfo("JQMListBindToArray arrNum: " + s);


        JQMListBindToArray<NativeChild11> lstCh = new JQMListBindToArray<>();
        JQMListBindToArray.Renderer<NativeChild11> reCh = new JQMListBindToArray.Renderer<NativeChild11>() {

            @Override
            public List<? extends ComplexPanel> create(JQMListBindToArray<NativeChild11> list, NativeChild11 item) {
                JQMListItem uiItem = new JQMListItem(item.name1);
                List<ComplexPanel> lst = new ArrayList<>();
                lst.add(uiItem);
                return lst;
            }

            @Override
            public boolean reuse(JQMListBindToArray<NativeChild11> list, NativeChild11 newItem,
                    List<? extends ComplexPanel> uiItems) {
                for (ComplexPanel i : uiItems) {
                    ((JQMListItem) i).setText(newItem.name1);
                }
                return true;
            }

            @Override
            public boolean onBeforeRefresh(JQMListBindToArray<NativeChild11> list) {
                return false;
            }

            @Override
            public void onAfterRefresh(JQMListBindToArray<NativeChild11> list) {
            }
        };
        lstCh.setRenderer(reCh);

        dataBinder.bind(lstCh, "children11");

        NativeChild11 ch1 = new NativeChild11();
        ch1.id = 2;
        ch1.name1 = "Max";
        m.children11.push(ch1);
        m.children11.setAt(m.children11.getLength() - 1, ch1); // the same value
        ch1 = new NativeChild11();
        ch1.id = 3;
        ch1.name1 = "Rose";
        m.children11.setAt(0, ch1);
        m.children11 = JsArray.create();
        ch1 = new NativeChild11();
        ch1.id = 100;
        ch1.name1 = "Cat";
        m.children11.push(ch1);
        ch1 = new NativeChild11();
        ch1.id = 101;
        ch1.name1 = "Dog";
        m.children11.push(ch1);
        m.children11.shift();
        m.children11.setLength(0);
        ch1 = new NativeChild11();
        ch1.id = 102;
        ch1.name1 = "Monkey";
        m.children11.push(ch1);
        ch1 = new NativeChild11();
        ch1.id = 103;
        ch1.name1 = "Bar";
        m.children11.push(ch1);

        s = "";
        items = lstCh.getItems();
        for (JQMListItem i : items) {
            if (i == null) continue;
            s += i.getText() + "; ";
        }
        GwtUtils.logInfo("JQMListBindToArray children11: " + s);
    }*/

    @SuppressWarnings("unlikely-arg-type")
    public static void test06(){
        NativeEquals o1 = new NativeEquals();
        o1.id = 1;
        o1.name = "aaa";
        NativeEquals o2 = new NativeEquals();
        o2.id = 2;
        o2.name = "aaa";

        Map<NativeEquals, String> map1 = new HashMap<>();
        map1.put(o1, "bla-bla");
        List<NativeEquals> lst1 = new ArrayList<>();
        lst1.add(o1);

        UniKey<NativeEquals> uniKey1 = UniKey.of(o1, o1::equals, o1::hashCode);
        UniKey<NativeEquals> uniKey2 = UniKey.of(o2, o2::equals, o2::hashCode);

        Map<UniKey<NativeEquals>, String> map1u = new HashMap<>();
        //map1u.put(uniKey1, "bla-bla");
        NativeEquals v = map1.keySet().iterator().next();
        map1u.put(UniKey.of(v, v::equals, v::hashCode), "bla-bla");

        GwtUtils.logInfo("o1.equals(o2): " + o1.equals(o2)
                + " | Objects.equals(o1, o2): " + Objects.equals(o1, o2)
                + " | Objects.equals(uniKey1, o2): " + Objects.equals(uniKey1, o2)
                + " | Objects.equals(uniKey1, uniKey2): " + Objects.equals(uniKey1, uniKey2)
                + " | map1.containsKey(o2): " + map1.containsKey(o2)
                + " | map1.containsKey(uniKey2): " + map1.containsKey(uniKey2)
                + " | map1u.containsKey(uniKey2): " + map1u.containsKey(uniKey2)
                + " | lst1.contains(o2): " + lst1.contains(o2)
                + " | lst1.contains(uniKey2): " + lst1.contains(uniKey2)
        );
        // RESULTS:
        // o1.equals(o2): true | Objects.equals(o1, o2): false |
        // Objects.equals(uniKey1, o2): true | Objects.equals(uniKey1, uniKey2): true |
        // map1.containsKey(o2): false | map1.containsKey(uniKey2): false | map1u.containsKey(uniKey2): true |
        // lst1.contains(o2): false | lst1.contains(uniKey2): true
    }

    public static void test07() {
        Model00 m1 = new Model00();
        m1.name = "test01";
        m1.id = 1;
        DataBinder<Model00> dataBinder = DataBinder.forModel(m1);
        m1 = dataBinder.getModel();
        dataBinder.addPropertyChangeHandler("info", event -> {
            GwtUtils.logInfo("m1.info: " + event.getNewValue());
        });
        m1.setInfo("zyx");



        Model00 m2 = new Model00();
        m2.name = "test02";
        m2.id = 2;
        DataBinder<Model00> dataBinder2 = DataBinder.forModel(m2);
        m2 = dataBinder2.getModel();
        dataBinder2.addPropertyChangeHandler("info", event -> {
            GwtUtils.logInfo("m2.info: " + event.getNewValue());
        });
        Js.asPropertyMap(m2).set("info", "zyx");

//      On release version generated as:
//      m_2 = new Model00;
//      m_2.name_0 = 'test01';
//      m_2.id_0 = 1;
//      dataBinder_1 = new DataBinder_0(m_2);
//      m_2 = ($wnd.document.swdatabinder.isProxy(dataBinder_1.proxy) || (dataBinder_1.proxy = $observe(notNull(dataBinder_1.proxy))) , dataBinder_1.proxy);
//      $addPropertyChangeHandler_3(dataBinder_1, 'info', new BindableTests$lambda$8$Type);
//      m_2.id_0 = 0;
//      m_2.name_0 = 'zyx';
//      m2 = new Model00;
//      m2.name_0 = 'test02';
//      m2.id_0 = 2;
//      dataBinder2 = new DataBinder_0(m2);
//      m2 = ($wnd.document.swdatabinder.isProxy(dataBinder2.proxy) || (dataBinder2.proxy = $observe(notNull(dataBinder2.proxy))) , dataBinder2.proxy);
//      $addPropertyChangeHandler_3(dataBinder2, 'info', new BindableTests$lambda$9$Type);
//      m2['info'] = 'zyx';

//      On release version when called setInfo("zyx") it's ignore @JsProperty method definition and
//      not use "info" as property name for proxy binding and code will not called
//
//      dataBinder1.addPropertyChangeHandler("info", event -> {
//          GwtUtils.logInfo("m1.info: " + event.getNewValue());
//      });
//
//      unlike Js.asPropertyMap(m2).set("info", "zyx") which used @JsProperty method definition so for bindings will be used "info" as property name
    }

    public static void runAllTests() {
        testNativeDataBinding();
        test00();
        test01();
        test02();
        test03();
        test04();
        //test05();
        test06();
        test07();
    }
}
