package com.egtinteractive.testing.framework.tests.runtests;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.internal.reflect.MethodMatcherException;

import com.egtinteractive.testing.framework.MyTestingFramework;
import com.egtinteractive.testing.framework.MyTestingFramework.Builder;
import com.egtinteractive.testing.framework.tests.dataprovider.MethodMatcherExceptionTest;

public class RunMethodMatcherExceptionTests {

    private final List<Class<?>> list = new ArrayList<>();
    private Builder builder;
    private MyTestingFramework tf;

    @BeforeTest
    public void putTests() {
	list.add(MethodMatcherExceptionTest.class);
	this.builder = new Builder(list);
	this.tf = new MyTestingFramework(builder);
    }

    @Test
    public void runMethodMatcherExceptionTests() {
	try {
	    tf.run();
	} catch (final MethodMatcherException e) {
	}
    }

}