package com.vx.sw.client.test.databinding;

import jsinterop.annotations.JsProperty;

public class Model00 {

    public double id;
    public String name;

    @JsProperty
    public String getInfo() {
        return id + " - " + name;
    }

    @JsProperty
    public void setInfo(String value) {
        id = 0;
        name = value;
    }

//  On release version @JsProperty methods generated as:
//
//    defineProperties(_, {info:{'get':function getInfo_0(){
//        return this.id_0 + ' - ' + this.name_0;
//      }
//      }});
//    defineProperties(_, {info:{'set':function setInfo(value_0){
//        this.id_0 = 0;
//        this.name_0 = value_0;
//      }
//      }});
//
//  But compiler may optimize method calls and ignore @JsProperty methods definition like (Example BindableTests.test07()):
//
//    m_2 = new Model00;
//    dataBinder_1 = new DataBinder_0(m_2);
//    m_2 = ($wnd.document.swdatabinder.isProxy(dataBinder_1.proxy) || (dataBinder_1.proxy = $observe(notNull(dataBinder_1.proxy))) , dataBinder_1.proxy);
//    $addPropertyChangeHandler_4(dataBinder_1, dfltChangeHandler);
//    m_2.id_0 = 0;
//    m_2.name_0 = 'zyx';
//
//  So if it used on xml bindings or in bindings like:
//
//    dataBinder2.addPropertyChangeHandler("info", event -> {
//        GwtUtils.logInfo("m2.info: " + event.getNewValue());
//    });
//
//  Methods getInfo() or setInfo(String value) not invokes bindings by property name "info".
//  It could be resolved by calling from code like:
//  Js.asPropertyMap(m2).set("info", "zyx");    --> generated as m2['info'] = 'zyx';
//  which use @JsProperty methods and invoke bindings by correct property name.
}
