package com.vx.sw.client.test.domain;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import gwt.interop.utils.shared.collections.Array;
import gwt.interop.utils.shared.collections.StringMap;
import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class NativeDto {
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

    public NativeChild1 embeddedObj1;
    public NativeChild2 embeddedObj2;
    public StringMap<NativeChild1> map1;
    public StringMap<NativeChild2> map2;

    protected String enum1Val;
    private transient DtoTestEnum enum1;

    protected Array<String> enumSetVal;
    private transient Set<DtoTestEnum> enumSet;

    protected Double idVal;
    private transient Long id;

    protected Double cntVal;
    private transient Integer cnt;

    @JsOverlay public static NativeDto of(int value) {
        NativeDto rslt = new NativeDto();
        rslt.intVal = value;
        return rslt;
    }

    @JsOverlay @JsonIgnore
    public final DtoTestEnum getEnum1() {
        return enum1 != null ? enum1 : (enum1 = DtoUtils.strToEnum(enum1Val, DtoTestEnum.class));
    }

    @JsOverlay @JsonIgnore
    public final void setEnum1(DtoTestEnum value) {
        enum1 = value;
        enum1Val = DtoUtils.enumToStr(value);
    }

    @JsOverlay @JsonIgnore
    public final Set<DtoTestEnum> getEnumSet() {
        return enumSet != null ? enumSet : (enumSet = DtoUtils.arrToEnumSet(enumSetVal, DtoTestEnum.class));
    }

    @JsOverlay @JsonIgnore
    public final void setEnumSet(Set<DtoTestEnum> value) {
        enumSet = value;
        enumSetVal = DtoUtils.enumColToArr(value);
    }

    @JsOverlay @JsonIgnore
    public final Long getId() {
        return id != null ? id : (id = DtoUtils.doubleToLong(idVal));
    }

    @JsOverlay @JsonIgnore
    public final void setId(Long value) {
        id = value;
        idVal = DtoUtils.longToDouble(value);
    }

    @JsOverlay @JsonIgnore
    public final Integer getCnt() {
        return cnt != null ? cnt : (cnt = DtoUtils.doubleToInt(cntVal));
    }

    @JsOverlay @JsonIgnore
    public final void setCnt(Integer value) {
        cnt = value;
        cntVal = DtoUtils.intToDouble(value);
    }
}
