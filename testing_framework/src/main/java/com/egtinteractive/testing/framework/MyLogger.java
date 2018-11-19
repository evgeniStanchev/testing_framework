package com.egtinteractive.testing.framework;

import static com.egtinteractive.testing.framework.Result.FAILED;
import static com.egtinteractive.testing.framework.Result.SKIPPED;
import static com.egtinteractive.testing.framework.TestingFrameworkUtils.getMethodsCount;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import org.testng.internal.reflect.MethodMatcherException;

public final class MyLogger implements Logger {

    @Override
    public void printException(final Throwable e) {
	if (e.getCause() != null) {
	    e.getCause().printStackTrace();
	}
    }

    @Override
    public void doesntMeetExpectationsException(final List<Class<? extends Throwable>> expected,
	    final Class<? extends Throwable> actualException, final Method method) {
	final StringBuilder sb = new StringBuilder();
	sb.append("Failed method: ").append(method).append(System.lineSeparator());
	sb.append("Exception/s expected: ");
	for (Class<? extends Throwable> expectedException : expected) {
	    sb.append(expectedException.getName()).append(System.lineSeparator());
	}
	sb.append("Exception got: ");
	sb.append(actualException.getName()).append(System.lineSeparator());
	System.out.println(sb.toString());
    }

    @Override
    public void printResult(final Map<Result, List<String>> results) {
	final StringBuilder sb = new StringBuilder();
	for (Result result : results.keySet()) {
	    for (String method : results.get(result)) {
		sb.append(result).append(":").append(method).append(System.lineSeparator());
	    }
	}
	sb.append("===============================================").append(System.lineSeparator())
		.append(" Default test: ").append(System.lineSeparator()).append(" Tests run: ")
		.append(getMethodsCount(results)).append(", Failures:");
	if (results.get(FAILED) != null) {
	    sb.append(results.get(FAILED).size());
	} else {
	    sb.append(0);
	}
	sb.append(", Skips:");
	if (results.get(SKIPPED) != null) {
	    sb.append(results.get(SKIPPED).size());
	} else {
	    sb.append(0);
	}
	sb.append(System.lineSeparator());
	sb.append("===============================================").append(System.lineSeparator());
	System.out.println(sb);
    }

    @Override
    public void throwDataProviderException(final Method method) {
	throw new MethodMatcherException(
		"Data provider mismatch" + System.lineSeparator() + "method:" + method.getName());
    }

    @Override
    public void throwDataProviderException(final Method method, final String message) {
	throw new MethodMatcherException("method: " + method.getName() + message);
    }
}