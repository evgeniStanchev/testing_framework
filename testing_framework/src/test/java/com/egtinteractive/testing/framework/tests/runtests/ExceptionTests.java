package com.egtinteractive.testing.framework.tests.runtests;

import static com.egtinteractive.testing.framework.Result.FAILED;
import static com.egtinteractive.testing.framework.Result.PASSED;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.egtinteractive.testing.framework.MyTestingFramework;
import com.egtinteractive.testing.framework.TestMetadata;
import com.egtinteractive.testing.framework.tests.samples.CorrectValuesTests;
import com.egtinteractive.testing.framework.tests.samples.DataProviderIllegalArgumentExceptionTests;
import com.egtinteractive.testing.framework.tests.samples.ExpectedExceptionsTest;
import com.egtinteractive.testing.framework.tests.samples.MethodMatcherExceptionTest;

public class ExceptionTests {
    private final List<Class<?>> clsList = new ArrayList<>();
    private MyTestingFramework testingFramework;
    private int size = 0;
    private Map<Method, List<Class<?>>> exceptionExpected = new HashMap<>();
    private final Map<Class<?>, List<Method>> testMethodsMap = new HashMap<>();
    private List<String> failedTests = new ArrayList<>();

    @BeforeTest
    public void beforeTests() {
	addTest(CorrectValuesTests.class);
	addTest(ExpectedExceptionsTest.class);
	addTest(DataProviderIllegalArgumentExceptionTests.class);
	addTest(MethodMatcherExceptionTest.class);
	this.testingFramework = new MyTestingFramework(this.clsList);
	this.testingFramework.run();
	getExceptions();
	fillMethodsMap();
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

    @Test
    public void wrongTypeArgumentsTest() throws Exception {
	final TestMetadata tm = new TestMetadata(this.clsList.get(this.clsList.size() - 1));
	this.failedTests = tm.getResults().get(FAILED);
	for (Class<?> cls : this.clsList) {
	    for (Method currentMethod : this.testMethodsMap.get(cls)) {
		final Test test = currentMethod.getAnnotation(Test.class);
		final Method dataProvider = tm.getDataProviders().get(test.dataProvider());
		if (dataProvider != null && !test.dataProvider().isEmpty()
			&& dataProvider.getName().equals(test.dataProvider())) {
		    final Object obj = cls.newInstance();
		    try {
			final Object[][] dataProviderInstance = (Object[][]) dataProvider.invoke(obj, (Object[]) null);
			for (int i = 0; i < dataProviderInstance.length; i++) {
			    for (int j = 0; j < dataProviderInstance[i].length; j++) {
				try {
				    currentMethod.invoke(obj, dataProviderInstance[i][j]);
				} catch (final Exception e) {
				    assertTrue(this.failedTests.contains(currentMethod.getName()));
				}
			    }
			}
		    } catch (Exception e) {
			assertEquals(e.getClass(), IllegalArgumentException.class);
		    }
		}
	    }
	}
    }

    @Test
    public void testExpectedExceptions() throws InstantiationException, IllegalAccessException {
	final TestMetadata tm = new TestMetadata(this.clsList.get(0));
	for (Class<?> cls : this.clsList) {
	    for (Method currentMethod : cls.getDeclaredMethods()) {
		if (currentMethod.isAnnotationPresent(Test.class)) {
		    final Object obj = cls.newInstance();
		    try {
			currentMethod.invoke(obj, (Object[]) null);
			assertEquals(this.exceptionExpected.get(currentMethod).size(), 0);
		    } catch (Exception e) {
			if (e.getCause() != null) {
			    e = (Exception) e.getCause();
			}
			if (!this.exceptionExpected.get(currentMethod).contains(e.getClass())) {
			    assertFalse(tm.getResults().get(PASSED).contains(currentMethod.getName()));
			} else {
			    assertTrue(tm.getResults().get(PASSED).contains(currentMethod.getName()));
			}
		    }
		}
	    }
	}
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

    private void getExceptions() {
	for (Class<?> cls : this.clsList) {
	    for (Method method : cls.getDeclaredMethods()) {
		method.setAccessible(true);
		if (method.isAnnotationPresent(Test.class)) {
		    final Test test = method.getAnnotation(Test.class);
		    this.exceptionExpected.put(method, Arrays.asList(test.expectedExceptions()));
		}
	    }
	}
    }

    private void fillMethodsMap() {
	for (Class<?> cls : this.clsList) {
	    List<Method> methodList = new ArrayList<>();
	    for (Method method : cls.getDeclaredMethods()) {
		if (method.isAnnotationPresent(Test.class)) {
		    methodList.add(method);
		}
	    }
	    this.testMethodsMap.put(cls, methodList);
	}
    }

}
