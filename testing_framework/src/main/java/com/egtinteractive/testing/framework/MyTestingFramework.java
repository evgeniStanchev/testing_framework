package com.egtinteractive.testing.framework;

import static com.egtinteractive.testing.framework.Result.FAILED;
import static com.egtinteractive.testing.framework.Result.PASSED;
import static com.egtinteractive.testing.framework.Result.SKIPPED;
import static com.egtinteractive.testing.framework.TestMetadata.RESULT_VIEW;
import static com.egtinteractive.testing.framework.TestMetadata.addException;
import static com.egtinteractive.testing.framework.TestMetadata.isExceptionExpected;
import static com.egtinteractive.testing.framework.TestingFrameworkUtils.LOG;
import static com.egtinteractive.testing.framework.TestingFrameworkUtils.hasExceptionExpected;
import static com.egtinteractive.testing.framework.TestingFrameworkUtils.invokeWithDataProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MyTestingFramework implements TestingFramework {

    private final List<Class<?>> clsList;
    private final ClassResults classResults;
    private final Logger logger;

    public MyTestingFramework(final List<Class<?>> clsList) {
	this.clsList = clsList;
	this.classResults = new ClassResults();
	this.logger = new MyLogger();
    }

    public MyTestingFramework(final List<Class<?>> clsList, Logger logger) {
	this.clsList = clsList;
	this.classResults = new ClassResults();
	this.logger = logger;
    }

    @Override
    public void run() {

	for (Class<?> cls : this.clsList) {
	    for (Class<?> currentCls = cls; currentCls.getSuperclass() != null; currentCls = currentCls
		    .getSuperclass()) {
		final TestMetadata testMetadata = new TestMetadata(currentCls);
		try {
		    final Object obj = currentCls.newInstance();
		    run(currentCls, testMetadata, obj);
		} catch (final Exception e) {
		    LOG.printException(e);
		    final List<Method> skippedTests = testMetadata.getTests();
		    final List<String> skippedTestsNames = new ArrayList<>();
		    for (Method currentMethod : skippedTests) {
			skippedTestsNames.add(currentMethod.getName());
		    }
		    testMetadata.addResult(SKIPPED, skippedTestsNames);
		}
	    }
	}
	LOG.printResult(RESULT_VIEW);
    }

    private void run(final Class<?> cls, final TestMetadata testMetadata, final Object obj) throws Exception {
	if (testMetadata.getTests().size() == 0) {
	    LOG.printResult(testMetadata.getResults());
	    return;
	}
	if (testMetadata.getBeforeTests().size() != 0) {
	    runBeforeMethods(testMetadata.getBeforeTests(), obj);
	}
	if (testMetadata.getTests().size() != 0) {
	    runTestMethods(cls, testMetadata.getTests(), testMetadata, obj);
	}
	if (testMetadata.getAfterTests().size() != 0) {
	    runAfterMethods(testMetadata.getAfterTests(), obj);
	}
    }

    private void runBeforeMethods(final List<Method> beforeTests, final Object obj) throws Exception {
	for (Method currentMethod : beforeTests) {
	    currentMethod.setAccessible(true);
	    currentMethod.invoke(obj, (Object[]) null);
	}
    }

    private void runTestMethods(final Class<?> cls, final List<Method> tests, final TestMetadata testMetadata,
	    final Object obj) {
	final Map<String, Method> dataProviders = testMetadata.getDataProviders();
	final List<String> failedTests = new ArrayList<>();
	final List<String> successfulTests = new ArrayList<>();
	final List<String> skippedTests = new ArrayList<>();
	for (Method currentMethod : tests) {
	    try {
		currentMethod.setAccessible(true);
		invokeWithDataProvider(currentMethod, cls, dataProviders, successfulTests, obj);
	    } catch (final Exception e) {
		Class<? extends Throwable> exceptionCause = e.getClass();
		if (e.getCause() != null) {
		    exceptionCause = e.getCause().getClass();
		}
		addException(currentMethod, exceptionCause);
		if (hasExceptionExpected(currentMethod)) {
		    if (isExceptionExpected(currentMethod, testMetadata)) {
			testMetadata.getActualException().get(currentMethod);
			successfulTests.add(currentMethod.getName());
		    } else {
			failedTests.add(currentMethod.getName());
			LOG.doesntMeetExpectationsException(testMetadata.getExpectedExceptions().get(currentMethod),
				exceptionCause, currentMethod);
		    }
		} else {
		    LOG.printException(e);
		    failedTests.add(currentMethod.getName());
		}
	    }
	}
	testMetadata.addResult(PASSED, successfulTests);
	testMetadata.addResult(FAILED, failedTests);
	testMetadata.addResult(SKIPPED, skippedTests);
	this.classResults.addResults(cls, PASSED, successfulTests);
	this.classResults.addResults(cls, FAILED, failedTests);
	this.classResults.addResults(cls, SKIPPED, skippedTests);
    }

    private void runAfterMethods(final List<Method> afterMethods, final Object obj) {
	if (afterMethods != null) {
	    for (Method currentMethod : afterMethods) {
		try {
		    currentMethod.setAccessible(true);
		    currentMethod.invoke(obj, (Object[]) null);
		} catch (final Exception e) {
		    LOG.printException(e);
		}
	    }
	}
    }

    public ClassResults getClassResults() {
	return this.classResults;
    }
}