package com.egtinteractive.testing.framework.tests.runtests;

import static com.egtinteractive.testing.framework.Result.FAILED;
import static com.egtinteractive.testing.framework.Result.PASSED;
import static com.egtinteractive.testing.framework.Result.SKIPPED;
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

public class TestResults {
    private final List<Class<?>> clsList = new ArrayList<>();
    private MyTestingFramework testingFramework;

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
    public void TestTests() {
	try {
	    for (Class<?> cls : this.clsList) {

		final Object instance = cls.newInstance();

		final int actualPassedMethodsSize = testingFramework.getClassResults().getSize(cls, PASSED);
		final int actualFailedMethodsSize = testingFramework.getClassResults().getSize(cls, FAILED);
		final int actualSkippedMethodsSize = testingFramework.getClassResults().getSize(cls, SKIPPED);

		final Method getPassed = cls.getMethod("getPassedTests", (Class<?>[]) null);
		final Method getFailed = cls.getMethod("getFailedTests", (Class<?>[]) null);
		final Method getSkipped = cls.getMethod("getSkippedTests", (Class<?>[]) null);

		final int expectedPassedMethodsSize = (int) getPassed.invoke(instance, (Object[]) null);
		final int expectedFailedMethodsSize = (int) getFailed.invoke(instance, (Object[]) null);
		final int expectedSkippedMethodsSize = (int) getSkipped.invoke(instance, (Object[]) null);

		assertEquals(actualPassedMethodsSize, expectedPassedMethodsSize);
		assertEquals(actualFailedMethodsSize, expectedFailedMethodsSize);
		assertEquals(actualSkippedMethodsSize, expectedSkippedMethodsSize);
	    }

	} catch (final Exception e) {
	    throw new RuntimeException(e);
	}
    }

    private void addTest(final Class<?> cls) {
	this.clsList.add(cls);
	for (Method method : cls.getDeclaredMethods()) {

	    if (method.isAnnotationPresent(Test.class)) {
		method.setAccessible(true);
	    }
	}
    }
}
