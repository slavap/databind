package com.vx.sw.client.test.domain;

public enum DtoTestEnum {

    X("x"), Y("y"), Z("z");

    public final String val;

    private DtoTestEnum(String val) {
        this.val = val;
    }
}
