package com.vx.sw.client;

import com.google.gwt.core.client.EntryPoint;
import com.vx.sw.client.common.databinding.BindableSetup;
import com.vx.sw.client.test.databinding.BindableTests;

public class DatabindEntryPoint implements EntryPoint {

	/** This is the entry point method. */
    @Override
	public void onModuleLoad() {
        BindableSetup.init();
        BindableTests.runAllTests();
	}

}
