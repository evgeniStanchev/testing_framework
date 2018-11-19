package com.egtinteractive.testing.framework.tests.runtests;

import static org.testng.Assert.assertEquals;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.egtinteractive.testing.framework.MyTestingFramework;
import com.egtinteractive.testing.framework.tests.samples.CorrectValuesTests;
import com.egtinteractive.testing.framework.tests.samples.DataProviderIllegalArgumentExceptionTests;
import com.egtinteractive.testing.framework.tests.samples.ExpectedExceptionsTest;
import com.egtinteractive.testing.framework.tests.samples.MethodMatcherExceptionTest;

public class SizeTests {
    private final List<Class<?>> clsList = new ArrayList<>();
    private MyTestingFramework testingFramework;
    private int size = 0;

    @BeforeTest
    public void beforeTests() {
	addTest(CorrectValuesTests.class);
	addTest(DataProviderIllegalArgumentExceptionTests.class);
	addTest(ExpectedExceptionsTest.class);
	addTest(MethodMatcherExceptionTest.class);
	this.testingFramework = new MyTestingFramework(this.clsList);
	this.testingFramework.run();
    }

    @Test
    public void testsSizeTest() {
	int expectedTestsSize = 0;
	for (Class<?> cls : this.clsList) {
	    for (Method currentMethod : cls.getDeclaredMethods()) {
		if (currentMethod.isAnnotationPresent(Test.class)) {
		    expectedTestsSize++;
		}
	    }
	}
	assertEquals(expectedTestsSize, this.size);
    }


    private void addTest(final Class<?> cls) {
	this.clsList.add(cls);
	for (Method method : cls.getDeclaredMethods()) {
	    if (method.isAnnotationPresent(Test.class)) {
		method.setAccessible(true);
		this.size++;
	    }
	}
    }

}
