package com.vx.sw.client.test.nativ;

import java.util.Date;
import java.util.Set;

import com.vx.sw.client.test.domain.DtoTestEnum;

import gwt.interop.utils.shared.collections.Array;
import gwt.interop.utils.shared.collections.StringMap;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

//Native types will have problem with Java's instanceof, because they are just js objects.

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class NativeDto1 {
    public int intVal;
    public double doubleVal;
    public boolean boolVal;
    public String strVal;
    public long longVal;

    //Double is synonymous with a Javascript Number object so can safely be convered to/from JSON
    public Double doubleObjVal;

    //Boolean is synonymous with a Javascript Boolean object so can safely be convered to/from JSON
    public Boolean booleanObjVal;

    public Array<String> arrStr;
    public Array<Double> arrNum;

    public StringMap<String> mapStr;
    public StringMap<Double> mapNum;

    public NativeChild11 embeddedObj1;
    public NativeChild12 embeddedObj2;
    public StringMap<NativeChild11> map1;
    public StringMap<NativeChild12> map2;

    public Array<NativeChild11> children11;

    protected String enum1Val;
    private transient DtoTestEnum enum1;

    protected Array<String> enumSetVal;
    private transient Set<DtoTestEnum> enumSet;

    protected TestDtoDate dateFromVal;
    private transient Date dateFrom;

    protected Double idVal;
    private transient Long id;

    protected Double cntVal;
    private transient Integer cnt;

    public transient Integer i00; // just for client side native databinding test, cannot be passed over the wire nicely, will be like "i00":{"value_1382_g$":5}
    public transient Integer i01;

    public NativeDto1() {
    }

    @JsOverlay public static NativeDto1 of(int value) {
       NativeDto1 rslt = new NativeDto1();
       rslt.intVal = value;
       return rslt;
    }

    @JsOverlay
    public final DtoTestEnum getEnum1() {
       return enum1 != null ? enum1 : (enum1 = TestDtoUtils.strToEnum(enum1Val, DtoTestEnum.class));
    }

    @JsOverlay
    public final void setEnum1(DtoTestEnum value) {
       enum1 = value;
       enum1Val = TestDtoUtils.enumToStr(value);
    }

    @JsOverlay
    public final Set<DtoTestEnum> getEnumSet() {
       return enumSet != null ? enumSet : (enumSet = TestDtoUtils.arrToEnumSet(enumSetVal, DtoTestEnum.class));
    }

    @JsOverlay
    public final void setEnumSet(Set<DtoTestEnum> value) {
       enumSet = value;
       enumSetVal = TestDtoUtils.enumSetToArr(value);
    }

    @JsOverlay
    public final Date getDateFrom() {
       return dateFrom != null ? dateFrom : (dateFrom = TestDtoDate.toDate(dateFromVal));
    }

    @JsOverlay
    public final void setDateFrom(Date value) {
       dateFrom = value;
       dateFromVal = TestDtoDate.of(value);
    }

    @JsOverlay
    public final Long getId() {
       return id != null ? id : (id = TestDtoUtils.doubleToLong(idVal));
    }

    @JsOverlay
    public final void setId(Long value) {
       id = value;
       idVal = TestDtoUtils.longToDouble(value);
    }

    @JsOverlay
    public final Integer getCnt() {
       return cnt != null ? cnt : (cnt = TestDtoUtils.doubleToInt(cntVal));
    }

    @JsOverlay
    public final void setCnt(Integer value) {
       cnt = value;
       cntVal = TestDtoUtils.intToDouble(value);
    }
}
