package com.egtinteractive.testing.framework.tests.runtests;

import static com.egtinteractive.testing.framework.Result.FAILED;
import static com.egtinteractive.testing.framework.Result.PASSED;
import static com.egtinteractive.testing.framework.Result.SKIPPED;
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
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.reflect.MethodMatcherException;

import com.egtinteractive.testing.framework.MyTestingFramework;
import com.egtinteractive.testing.framework.TestMetadata;
import com.egtinteractive.testing.framework.tests.dataprovider.CorrectValuesTests;
import com.egtinteractive.testing.framework.tests.dataprovider.DataProviderIllegalArgumentExceptionTests;
import com.egtinteractive.testing.framework.tests.dataprovider.MethodMatcherExceptionTest;
import com.egtinteractive.testing.framework.tests.expected.exceptions.ExpectedExceptionsTest;

public class RunTests {
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
	testingFramework = new MyTestingFramework(clsList);
	testingFramework.run();
	getExceptions();
	fillMethodsMap();
    }

    @Test
    public void testsSizeTest() {
	int expectedTestsSize = 0;
	for (Class<?> cls : clsList) {
	    for (Method currentMethod : cls.getDeclaredMethods()) {
		if (currentMethod.isAnnotationPresent(Test.class)) {
		    expectedTestsSize++;
		}
	    }
	}
	assertEquals(expectedTestsSize, this.size);
    }

    @Test
    public void passedTestsTest() {
	int actualPassedMethodsSize = 0;
	for (Class<?> cls : clsList) {
	    actualPassedMethodsSize += testingFramework.getClassResults().getSize(cls, PASSED);
	}

	TestMetadata tm = new TestMetadata(clsList.get(0));
	assertEquals(actualPassedMethodsSize, tm.getResults().get(PASSED).size());
    }

    @Test
    public void failedTestsTest() {
	int actualFailedMethodsSize = 0;
	for (Class<?> cls : clsList) {
	    actualFailedMethodsSize += testingFramework.getClassResults().getSize(cls, FAILED);
	}

	TestMetadata tm = new TestMetadata(clsList.get(0));
	assertEquals(actualFailedMethodsSize, tm.getResults().get(FAILED).size());
    }

    @Test
    public void skippedTestsTest() {
	int actualSkippedMethodsSize = 0;
	for (Class<?> cls : clsList) {
	    actualSkippedMethodsSize += testingFramework.getClassResults().getSize(cls, SKIPPED);
	}
	TestMetadata tm = new TestMetadata(clsList.get(0));
	assertEquals(actualSkippedMethodsSize, tm.getResults().get(SKIPPED).size());
    }

    @Test
    public void dataProvidersTest() throws Exception {
	final Class<?> cls = DataProviderIllegalArgumentExceptionTests.class;
	final TestMetadata tm = new TestMetadata(cls);
	final Map<String, Method> dataProviders = tm.getDataProviders();
	final Method[] methods = cls.getDeclaredMethods();
	int dataProvidersExpectedSize = 0;
	for (Method method : methods) {
	    method.setAccessible(true);
	    if (method.isAnnotationPresent(DataProvider.class)) {
		dataProvidersExpectedSize++;
	    }
	}
	assertEquals(dataProviders.size(), dataProvidersExpectedSize);
    }

    @Test
    public void wrongTypeArgumentsTest() throws Exception {
	final TestMetadata tm = new TestMetadata(clsList.get(clsList.size() - 1));
	this.failedTests = tm.getResults().get(FAILED);
	for (Class<?> cls : clsList) {
	    for (Method currentMethod : testMethodsMap.get(cls)) {
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
				    assertTrue(failedTests.contains(currentMethod.getName()));
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
    public void methodWithoutDataProviderButWithArgumentsTest() {
	final Class<?> cls = DataProviderIllegalArgumentExceptionTests.class;
	final TestMetadata tm = new TestMetadata(cls);
	List<String> failedTests = tm.getResults().get(FAILED);
	for (Class<?> currentCls : clsList) {
	    for (Method currentMethod : testMethodsMap.get(currentCls)) {
		final Test test = currentMethod.getAnnotation(Test.class);
		if (test.dataProvider().isEmpty() && currentMethod.getParameterTypes().length != 0) {
		    assertTrue(failedTests.contains(currentMethod.getName()));
		    assertEquals(tm.getActualException().get(currentMethod), MethodMatcherException.class);
		}
	    }
	}
    }

    @Test
    public void testExpectedExceptions() throws InstantiationException, IllegalAccessException {
	final TestMetadata tm = new TestMetadata(clsList.get(0));
	for (Class<?> cls : clsList) {
	    for (Method currentMethod : cls.getDeclaredMethods()) {
		if (currentMethod.isAnnotationPresent(Test.class)) {
		    final Object obj = cls.newInstance();
		    try {
			currentMethod.invoke(obj, (Object[]) null);
			assertEquals(exceptionExpected.get(currentMethod).size(), 0);
		    } catch (Exception e) {
			if (e.getCause() != null) {
			    e = (Exception) e.getCause();
			}
			if (!exceptionExpected.get(currentMethod).contains(e.getClass())) {
			    assertFalse(tm.getResults().get(PASSED).contains(currentMethod.getName()));
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
		size++;
	    }
	}
    }

    private void getExceptions() {
	for (Class<?> cls : this.clsList) {
	    for (Method method : cls.getDeclaredMethods()) {
		method.setAccessible(true);
		if (method.isAnnotationPresent(Test.class)) {
		    final Test test = method.getAnnotation(Test.class);
		    exceptionExpected.put(method, Arrays.asList(test.expectedExceptions()));
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
