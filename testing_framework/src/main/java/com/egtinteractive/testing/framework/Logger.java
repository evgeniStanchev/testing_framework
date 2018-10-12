package com.egtinteractive.testing.framework;

import static com.egtinteractive.testing.framework.TestingFrameworkUtils.getMethodsCount;
import static com.egtinteractive.testing.framework.TestingFrameworkUtils.getTheRealException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.testng.internal.reflect.MethodMatcherException;

public final class Logger {

    public void printException(Throwable e) {
	getTheRealException(e).printStackTrace();
    }

    public void printResult(final Map<String, List<Method>> results) {
	final StringBuilder sb = new StringBuilder();
	for (String result : results.keySet()) {
	    for (Method method : results.get(result)) {
		sb.append(result).append(":").append(method.getName()).append(System.lineSeparator());
	    }
	}
	sb.append("===============================================").append(System.lineSeparator())
		.append(" Default test: ").append(System.lineSeparator()).append(" Tests run: ")
		.append(getMethodsCount(results)).append(", Failures:");
	if (Objects.nonNull(results.get("FAILED"))) {
	    sb.append(results.get("FAILED").size());
	} else {
	    sb.append(0);
	}
	sb.append(", Skips:");
	if (Objects.nonNull(results.get("SKIPPED"))) {
	    sb.append(results.get("SKIPPED").size());
	} else {
	    sb.append(0);
	}
	sb.append(System.lineSeparator());
	sb.append("===============================================").append(System.lineSeparator());
	System.out.println(sb);
    }

    public void throwDataProviderException(final Method method) {
	throw new MethodMatcherException(
		"Data provider mismatch" + System.lineSeparator() + "method:" + method.getName());
    }

    public void throwDataProviderException(final Method method, final String message) {
	throw new MethodMatcherException("method:" + method.getName() + message);
    }
}