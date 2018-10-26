package com.egtinteractive.testing.framework;

import static com.egtinteractive.testing.framework.TestingFrameworkUtils.LOG;
import static com.egtinteractive.testing.framework.TestingFrameworkUtils.getMapWithAnnotatedMethods;
import static com.egtinteractive.testing.framework.TestingFrameworkUtils.getMethodsCount;
import static com.egtinteractive.testing.framework.TestingFrameworkUtils.getSpecificMethods;
import static com.egtinteractive.testing.framework.TestingFrameworkUtils.invokeWithDataProvider;
import static com.egtinteractive.testing.framework.TestingFrameworkUtils.theExceptionIsExpected;

import java.lang.annotation.Annotation;
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

public final class MyTestingFramework implements TestingFramework {

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
    private final List<Class<?>> classList;

    public static final Class<Test> TESTS = Test.class;
    public static final Class<BeforeTest> BEFORE_TESTS = BeforeTest.class;
    public static final Class<AfterTest> AFTER_TESTS = AfterTest.class;
    public static final Class<DataProvider> DATA_PROVIDERS = DataProvider.class;

    public MyTestingFramework(final Builder builder) {
	this.methods = builder.methods;
	this.results = builder.results;
	this.classList = builder.classList;
    }

    public static class Builder {
	private final List<Class<?>> classList;
	private final Map<String, List<Method>> results = new HashMap<>();
	private final Map<String, Map<String, List<Method>>> methods = new HashMap<>();

	public Builder(final List<Class<?>> classList) {
	    this.classList = classList;
	    methods.put(TESTS.getName(), getMapWithAnnotatedMethods(classList, TESTS));
	    methods.put(BEFORE_TESTS.getName(), getMapWithAnnotatedMethods(classList, BEFORE_TESTS));
	    methods.put(AFTER_TESTS.getName(), getMapWithAnnotatedMethods(classList, BEFORE_TESTS));
	    methods.put(DATA_PROVIDERS.getName(), getMapWithAnnotatedMethods(classList, DATA_PROVIDERS));
	}

	public Builder addAnnotation(final Class<? extends Annotation> cls) {
	    methods.put(cls.getName(), getMapWithAnnotatedMethods(this.classList, cls));
	    return this;
	}
    }

    @Override
    public void run() {
	if (getMethodsCount(methods.get(TESTS.getName())) == 0) {
	    LOG.printResult(results);
	    return;
	}
	for (Class<?> cls : classList) {
	    if (getMethodsCount(methods.get(BEFORE_TESTS.getName())) != 0) {
		try {
		    runBeforeMethods(cls, getSpecificMethods(methods, BEFORE_TESTS.getName(), cls));
		} catch (final Exception e) {
		    LOG.printException(e);
		    increaseResults("SKIPPED", cls);
		    continue;
		}
	    }
	    runTestMethods(cls, getSpecificMethods(methods, TESTS.getName(), cls));
	    runAfterMethods(cls, getSpecificMethods(methods, AFTER_TESTS.getName(), cls));
	}
	LOG.printResult(results);
    }

    private void increaseResults(final String key, final Class<?> cls) {
	this.results.put(key, getSpecificMethods(methods, TESTS.getName(), cls));
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
		    LOG.printException(e);
		    failedTests.add(currentMethod);
		}
	    }
	}
	increaseResults("PASSED", successfulTests);
	increaseResults("FAILED", failedTests);
    }

    private void runAfterMethods(final Class<?> cls, final List<Method> afterTestMethods) {
	if (Objects.nonNull(afterTestMethods))
	    for (Method currentMethod : afterTestMethods) {
		try {
		    //TODO WARNING
		    final Object obj = cls.newInstance();
		    currentMethod.setAccessible(true);
		    currentMethod.invoke(obj, (Object[]) null);
		} catch (final Exception e) {
		    LOG.printException(e);
		}
	    }
    }
}