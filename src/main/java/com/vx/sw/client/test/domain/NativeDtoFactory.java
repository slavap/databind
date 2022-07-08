package com.vx.sw.client.test.domain;

import java.util.EnumSet;

import gwt.interop.utils.shared.collections.ArrayFactory;
import gwt.interop.utils.shared.collections.StringMapFactory;

public class NativeDtoFactory {

    private NativeDtoFactory() {} // static class

    public static NativeDto createNativeDto() {
        NativeDto o = new NativeDto();
        o.intVal = 123;
        o.doubleVal = 34.56d;
        o.boolVal = true;
        o.strVal = "hi";
        o.longVal = 123456789012345L;
        o.doubleObjVal = 55.77d;
        o.booleanObjVal = true;

        o.arrStr = ArrayFactory.create();
        o.arrStr.push("aaa");
        o.arrStr.push("bbb");
        o.arrStr.push("ccc");

        o.arrNum = ArrayFactory.create();
        o.arrNum.push(2.71d);
        o.arrNum.push(3.14d);
        o.arrNum.push(9.81d);

        o.mapStr = StringMapFactory.create();
        o.mapStr.set("i1", "one");
        o.mapStr.set("i2", "two");

        o.mapNum = StringMapFactory.create();
        o.mapNum.set("n1", 123.45d);
        o.mapNum.set("n2", 567.89d);

        o.embeddedObj1 = new NativeChild1();
        o.embeddedObj1.id = 1;
        o.embeddedObj1.name1 = "John";

        o.embeddedObj2 = new NativeChild2();
        o.embeddedObj2.id = 2;
        o.embeddedObj2.name2 = "Paul";

        o.map1 = StringMapFactory.create();
        o.map1.set(o.embeddedObj1.name1, o.embeddedObj1);

        o.map2 = StringMapFactory.create();
        o.map2.set(o.embeddedObj2.name2, o.embeddedObj2);

        o.setEnum1(DtoTestEnum.Y);
        o.setEnumSet(EnumSet.of(DtoTestEnum.Z, DtoTestEnum.X));

        //@SuppressWarnings("deprecation")
        //Date d = new Date(119, 0, 29, 23, 34, 13);
        //o.setDateFrom(d);

        return o;
    }

}
