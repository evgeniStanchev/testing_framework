package com.egtinteractive.testing.framework.tests.runtests;

import java.util.ArrayList;
import java.util.List;

import org.testng.TestException;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.egtinteractive.testing.framework.MyTestingFramework;
import com.egtinteractive.testing.framework.MyTestingFramework.Builder;
import com.egtinteractive.testing.framework.tests.expected.exceptions.ExpectedExceptionsTest;

public class RunExpectedExceptionsTests {

    private final List<Class<?>> list = new ArrayList<>();
    private Builder builder;
    private MyTestingFramework tf;

    @BeforeTest
    public void putTests() {
	list.add(ExpectedExceptionsTest.class);
	this.builder = new Builder(list);
	this.tf = new MyTestingFramework(builder);
    }

    @Test
    public void runExpectedExceptionsTests() {
	try {
	    tf.run();
	} catch (TestException e) {
	}
    }
}
