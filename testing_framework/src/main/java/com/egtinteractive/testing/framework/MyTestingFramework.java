package com.egtinteractive.testing.framework;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static com.egtinteractive.testing.framework.TestingFrameworkUtils.*;

public final class MyTestingFramework implements TestingFramework {

    private final List<Class<?>> classList;
    /**
     * <p>
     * The map contains maps with all the methods specified with the given
     * annotations in the list given as an argument in the constructor.<br/>
     * </p>
     * <b>HashMap(String, Map(String, List(Method)))</b>:<br/>
     * <i>key(String)</i> - the name of the annotation's class;<br/>
     * <i>values(Map(String, List(Method)))</i>;<br>
     * <p>
     * <b>Map(String, List(Method))</b>:<br>
     * <i>key(String)</i>- The name of all of the classes in the list specified
     * in the constructor;<br>
     * <i>values(List(Method))</i> - list with all declared methods with the
     * specified annotation;
     * </p>
     */
    private final Map<String, Map<String, List<Method>>> methods;
    private final Map<String, List<Method>> results;

    public MyTestingFramework(final List<Class<?>> classList) {
	this.methods = new HashMap<>();
	this.methods.put(TESTS, getMapWithAnnotatedMethods(classList, Test.class));
	this.methods.put(BEFORE_TESTS, getMapWithAnnotatedMethods(classList, BeforeTest.class));
	this.methods.put(AFTER_TESTS, getMapWithAnnotatedMethods(classList, AfterTest.class));
	this.methods.put(DATA_PROVIDERS, getMapWithAnnotatedMethods(classList, DataProvider.class));
	this.results = new HashMap<>();
	this.classList = classList;
    }

    @Override
    public void run() {
	if (getMethodsCount(methods.get(TESTS)) == 0) {
	    log.printResult(results);
	    return;
	}
	for (Class<?> cls : classList) {
	    if (getMethodsCount(methods.get(BEFORE_TESTS)) != 0) {
		try {
		    runBeforeMethods(cls, getSpecificMethods(methods, BEFORE_TESTS, cls));
		} catch (final Exception e) {
		    log.printException(e);
		    increaseResults("SKIPPED", cls);
		    continue;
		}
		runTestMethods(cls, getSpecificMethods(methods, TESTS, cls));
		runAfterMethods(cls, getSpecificMethods(methods, AFTER_TESTS, cls));
	    }
	}
	log.printResult(results);
    }

    private void increaseResults(final String key, final Class<?> cls) {
	this.results.put(key, getSpecificMethods(methods, TESTS, cls));
    }

    private void increaseResults(final String key, final List<Method> methods) {
	if (methods.size() != 0) {
	    if (Objects.isNull(results.get(key))) {
		this.results.put(key, methods);
	    } else {
		List<Method> list = this.results.get(key);
		for (Method method : methods) {
		    list.add(method);
		}
		this.results.put(key, list);
	    }
	}
    }

    private void runBeforeMethods(final Class<?> cls, final List<Method> beforeTestMethods) throws Exception {
	for (Method currentMethod : beforeTestMethods) {
	    final Object obj = cls.newInstance();
	    currentMethod.setAccessible(true);
	    currentMethod.invoke(obj, (Object[]) null);
	}
    }

    private void runTestMethods(final Class<?> cls, final List<Method> testMethods) {
	final List<Method> failedTests = new ArrayList<>();
	final List<Method> successfulTests = new ArrayList<>();
	for (Method currentMethod : testMethods) {
	    try {
		final Object obj = cls.newInstance();
		currentMethod.setAccessible(true);
		final Method dataProvider = invokeWithDataProvider(currentMethod, cls, this.methods);
		if (Objects.isNull(dataProvider)) {
		    currentMethod.invoke(obj, (Object[]) null);
		}
		successfulTests.add(currentMethod);
	    } catch (final Exception e) {
		if (theExceptionIsExpected(e, currentMethod)) {
		    successfulTests.add(currentMethod);
		} else {
		    log.printException(e);
		    failedTests.add(currentMethod);
		}
	    }
	}
	increaseResults("PASSED", successfulTests);
	increaseResults("FAILED", failedTests);
    }

    private void runAfterMethods(final Class<?> cls, final List<Method> afterTestMethods) {
	for (Method currentMethod : afterTestMethods) {
	    try {
		final Object obj = cls.newInstance();
		currentMethod.setAccessible(true);
		currentMethod.invoke(obj, (Object[]) null);
	    } catch (final Exception e) {
		log.printException(e);
	    }
	}
    }
}