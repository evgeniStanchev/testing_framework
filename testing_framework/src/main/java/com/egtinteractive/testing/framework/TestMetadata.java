package com.egtinteractive.testing.framework;

import static com.egtinteractive.testing.framework.TestingFrameworkUtils.getListWithAnnotatedMethods;
import static com.egtinteractive.testing.framework.TestingFrameworkUtils.getMapWithAnnotatedMethods;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public final class TestMetadata {

    private final List<Method> beforeTestList;
    private final List<Method> testList;
    private final List<Method> afterTestList;

    private final Map<Method, List<Class<? extends Throwable>>> expectedExceptionView;
    private final Map<Method, List<Class<? extends Throwable>>> expectedException = new HashMap<>();

    private final Map<String, Method> dataProviderMap;
    private static final Map<Result, List<String>> RESULT = new HashMap<>();
    public static final Map<Result, List<String>> RESULT_VIEW = Collections.unmodifiableMap(RESULT);

    private static final Map<Method, Class<? extends Throwable>> ACTUAL_EXCEPTION = new HashMap<>();
    public static final Map<Method, Class<? extends Throwable>> ACTUAL_EXCEPTION_VIEW = Collections
	    .unmodifiableMap(ACTUAL_EXCEPTION);

    @SuppressWarnings("unchecked")
    public TestMetadata(final Class<?> cls) {
	dataProviderMap = Collections.unmodifiableMap(getMapWithAnnotatedMethods(cls, DataProvider.class));
	beforeTestList = Collections
		.unmodifiableList(new ArrayList<>(getListWithAnnotatedMethods(cls, BeforeTest.class)));
	afterTestList = Collections
		.unmodifiableList(new ArrayList<>(getListWithAnnotatedMethods(cls, AfterTest.class)));
	testList = Collections.unmodifiableList(new ArrayList<>(getListWithAnnotatedMethods(cls, Test.class)));
	for (Method method : testList) {
	    expectedException.put(method, Arrays.asList(method.getAnnotation(Test.class).expectedExceptions()));
	}
	expectedExceptionView = Collections.unmodifiableMap(expectedException);
    }

    public List<Method> getBeforeTests() {
	return beforeTestList;
    }

    public List<Method> getTests() {
	return testList;
    }

    public List<Method> getAfterTests() {
	return afterTestList;
    }

    public Map<Method, List<Class<? extends Throwable>>> getExpectedExceptions() {
	return expectedExceptionView;
    }

    public Map<Method, Class<? extends Throwable>> getActualException() {
	return ACTUAL_EXCEPTION_VIEW;
    }

    public Map<Result, List<String>> getResults() {
	return RESULT_VIEW;
    }

    public Map<String, Method> getDataProviders() {
	return dataProviderMap;
    }

    public static void addException(final Method method, final Class<? extends Throwable> exception) {
	ACTUAL_EXCEPTION.put(method, exception);
    }

    public void addResult(final Result key, final List<String> methods) {
	if (RESULT.get(key) == null) {
	    final List<String> methodNames = new ArrayList<>();
	    for (String method : methods) {
		methodNames.add(method);
	    }
	    RESULT.put(key, methodNames);
	} else {
	    final List<String> methodNames = RESULT.get(key);
	    for (String method : methods) {
		methodNames.add(method);
	    }
	    RESULT.put(key, methodNames);
	}
    }

}