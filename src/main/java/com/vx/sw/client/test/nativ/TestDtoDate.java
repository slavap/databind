package com.vx.sw.client.test.nativ;

import java.util.Date;

import jsinterop.annotations.JsOverlay;
import jsinterop.annotations.JsPackage;
import jsinterop.annotations.JsType;

@JsType(isNative = true, namespace = JsPackage.GLOBAL, name = "Object")
public class TestDtoDate {

  public Double msec;
  public String utc;

  @JsOverlay public static TestDtoDate create(Double msec, String utc) {
      TestDtoDate rslt = new TestDtoDate();
      rslt.msec = msec;
      rslt.utc = utc;
      return rslt;
  }

  @JsOverlay public static TestDtoDate of(Date value) {
      return TestDtoUtils.dateToDtoDate(value);
  }

  @JsOverlay public static Date toDate(TestDtoDate value) {
      return TestDtoUtils.dtoDateToDate(value);
  }

  @JsOverlay public final Date toDate() {
      return TestDtoUtils.dtoDateToDate(this);
  }

  @JsOverlay @Override
  public final String toString() {
      return msec + " | " + utc;
  }

}