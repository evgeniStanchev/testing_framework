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
import org.testng.internal.reflect.MethodMatcherException;

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
    private static final Map<Method, Class<? extends Throwable>> ACTUAL_EXCEPTION_VIEW = Collections
	    .unmodifiableMap(ACTUAL_EXCEPTION);

    @SuppressWarnings("unchecked")
    public TestMetadata(final Class<?> cls) {
	this.dataProviderMap = Collections.unmodifiableMap(getMapWithAnnotatedMethods(cls, DataProvider.class));
	this.beforeTestList = Collections
		.unmodifiableList(new ArrayList<>(getListWithAnnotatedMethods(cls, BeforeTest.class)));
	this.afterTestList = Collections
		.unmodifiableList(new ArrayList<>(getListWithAnnotatedMethods(cls, AfterTest.class)));
	this.testList = Collections.unmodifiableList(new ArrayList<>(getListWithAnnotatedMethods(cls, Test.class)));
	for (Method method : this.testList) {
	    this.expectedException.put(method, Arrays.asList(method.getAnnotation(Test.class).expectedExceptions()));
	}
	this.expectedExceptionView = Collections.unmodifiableMap(this.expectedException);
    }

    public List<Method> getBeforeTests() {
	return this.beforeTestList;
    }

    public List<Method> getTests() {
	return this.testList;
    }

    public List<Method> getAfterTests() {
	return this.afterTestList;
    }

    public Map<Method, List<Class<? extends Throwable>>> getExpectedExceptions() {
	return this.expectedExceptionView;
    }

    public Map<Method, Class<? extends Throwable>> getActualException() {
	return ACTUAL_EXCEPTION_VIEW;
    }

    public Map<Result, List<String>> getResults() {
	return RESULT_VIEW;
    }

    public Map<String, Method> getDataProviders() {
	return this.dataProviderMap;
    }

    public static void addException(final Method method, final Class<? extends Throwable> exception) {
	ACTUAL_EXCEPTION.put(method, exception);
    }

    public void addResult(final Result result, final List<String> methods) {
	if (RESULT.get(result) == null) {
	    RESULT.put(result, new ArrayList<>(methods));
	} else {
	    final List<String> methodNames = RESULT.get(result);
	    methodNames.addAll(methods);
	    RESULT.put(result, methodNames);
	}
    }

    public static boolean isExceptionExpected(final Method method, final TestMetadata testMetadata) {
	final List<Class<? extends Throwable>> expectedExceptions = testMetadata.getExpectedExceptions().get(method);
	final Class<? extends Throwable> actualException = testMetadata.getActualException().get(method);
	return expectedExceptions.contains(actualException) && dataProviderMatches(actualException);
    }

    private static boolean dataProviderMatches(final Class<? extends Throwable> actualException) {
	return MethodMatcherException.class != actualException;
    }

}