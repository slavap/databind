package com.vx.sw.client.common.databinding;

import org.jboss.errai9.databinding.client.Bindable;

public class BindableSetup {

    private BindableSetup() {} // static class

    public static void init() {
        if (Bindable.support == null) {
            Bindable.support = new BindableSupportImpl();
        }
    }
}
