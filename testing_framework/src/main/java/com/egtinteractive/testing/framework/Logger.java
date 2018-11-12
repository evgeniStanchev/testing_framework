package com.egtinteractive.testing.framework;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public interface Logger {

    void printException(final Throwable e);

    void printResult(final Map<Result, List<String>> results);

    void throwDataProviderException(final Method method);

    void throwDataProviderException(final Method method, final String message);

    void doesntMeetExpectationsException(final List<Class<? extends Throwable>> expected,
	    final Class<? extends Throwable> actual, final Method method);
}