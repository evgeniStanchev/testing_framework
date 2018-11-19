package com.egtinteractive.testing.framework.tests.runtests;

import static com.egtinteractive.testing.framework.Result.FAILED;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.reflect.MethodMatcherException;

import com.egtinteractive.testing.framework.MyTestingFramework;
import com.egtinteractive.testing.framework.TestMetadata;
import com.egtinteractive.testing.framework.tests.samples.CorrectValuesTests;
import com.egtinteractive.testing.framework.tests.samples.DataProviderIllegalArgumentExceptionTests;
import com.egtinteractive.testing.framework.tests.samples.ExpectedExceptionsTest;
import com.egtinteractive.testing.framework.tests.samples.MethodMatcherExceptionTest;

public class DataProviderTests {
    private final List<Class<?>> clsList = new ArrayList<>();
    private MyTestingFramework testingFramework;
    private final Map<Class<?>, List<Method>> testMethodsMap = new HashMap<>();

    @BeforeTest
    public void beforeTests() {
	addTest(CorrectValuesTests.class);
	addTest(ExpectedExceptionsTest.class);
	addTest(DataProviderIllegalArgumentExceptionTests.class);
	addTest(MethodMatcherExceptionTest.class);
	this.testingFramework = new MyTestingFramework(this.clsList);
	this.testingFramework.run();
	fillMethodsMap();
    }

    @Test
    public void methodWithoutDataProviderButWithArgumentsTest() {
	final Class<?> cls = DataProviderIllegalArgumentExceptionTests.class;
	final TestMetadata tm = new TestMetadata(cls);
	List<String> failedTests = tm.getResults().get(FAILED);
	for (Class<?> currentCls : this.clsList) {
	    for (Method currentMethod : this.testMethodsMap.get(currentCls)) {
		final Test test = currentMethod.getAnnotation(Test.class);
		if (test.dataProvider().isEmpty() && currentMethod.getParameterTypes().length != 0) {
		    assertTrue(failedTests.contains(currentMethod.getName()));
		    assertEquals(tm.getActualException().get(currentMethod), MethodMatcherException.class);
		}
	    }
	}
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

    private void addTest(final Class<?> cls) {
	this.clsList.add(cls);
	for (Method method : cls.getDeclaredMethods()) {

	    if (method.isAnnotationPresent(Test.class)) {
		method.setAccessible(true);
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
