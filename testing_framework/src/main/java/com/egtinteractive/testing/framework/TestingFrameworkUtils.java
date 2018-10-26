package com.egtinteractive.testing.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.reflect.MethodMatcherException;

public final class TestingFrameworkUtils {

    public static final Class<?> DATA_PROVIDERS = DataProvider.class;
    public static final Logger LOG = new Logger();

    public static boolean hasExceptionExpected(final Method method) {
	final Test test = method.getAnnotation(Test.class);
	return (test.expectedExceptions().length == 0) ? false : true;
    }

    public static Throwable getTheRealException(Throwable e) {
	while (Objects.nonNull(e.getCause())) {
	    e = e.getCause();
	}
	return e;
    }

    public static boolean hasDataProvider(final Method method) {
	Test test = method.getAnnotation(Test.class);
	return test.dataProvider().equals("") ? false : true;
    }

    public static int getMethodsCount(final Map<String, List<Method>> map) {
	int size = 0;
	if (Objects.nonNull(map)) {
	    for (List<Method> method : map.values()) {
		size += method.size();
	    }
	}
	return size;
    }

    public static boolean theExceptionIsExpected(final Throwable e, final Method method) {
	for (Class<Throwable> expectedExceptionClass : getExpectedExceptions(method)) {
	    if (getTheRealException(e).getClass().equals(expectedExceptionClass)) {
		return true;
	    }
	}
	return false;
    }

    @SuppressWarnings("unchecked")
    private static List<Class<Throwable>> getExpectedExceptions(final Method method) {
	final List<Class<Throwable>> list = new ArrayList<>();
	if (!hasExceptionExpected(method)) {
	    return list;
	}
	final Test test = method.getAnnotation(Test.class);
	for (Class<Throwable> e : test.expectedExceptions()) {
	    list.add(e);
	}
	return list;
    }

    public static Method invokeWithDataProvider(final Method method, final Class<?> cls,
	    final Map<String, Map<String, List<Method>>> methods) throws Exception {

	Method dataProvider = null;

	if (!hasDataProvider(method)) {
	    if (method.getParameterCount() == 0) {
		return dataProvider;
	    } else {
		LOG.throwDataProviderException(method);
	    }
	}

	final List<Method> dataProviders = getSpecificMethods(methods, DATA_PROVIDERS.getName(), cls);

	final String dataProviderName = method.getAnnotation(Test.class).dataProvider();

	for (Method currentDataProvider : dataProviders) {
	    if (currentDataProvider.getName().equals(dataProviderName)) {
		dataProvider = currentDataProvider;
		break;
	    }
	}
	if (Objects.isNull(dataProvider)) {
	    LOG.throwDataProviderException(method, " has not available data provider");
	}

	final Type[] types = method.getParameterTypes();
	final Object[][] returnValues = (Object[][]) dataProvider.invoke(cls.newInstance(), (Object[]) null);
	for (int i = 0; i < returnValues.length; i++) {
	    if (types.length != returnValues[0].length) {
		LOG.throwDataProviderException(method);
	    }
	    for (int j = 0; j < returnValues[i].length; j++) {
		try {
		    method.invoke(cls.newInstance(), returnValues[i]);
		} catch (final MethodMatcherException e) {
		    LOG.printException(e);
		}
	    }
	}
	return dataProvider;
    }

    public static void validateException(final Method method) {
	if (!hasExceptionExpected(method)) {
	    return;
	}
    }

    public static List<Method> getSpecificMethods(final Map<String, Map<String, List<Method>>> methods,
	    final String annotationName, final Class<?> cls) {
	return methods.get(annotationName).get(cls.getName());
    }

    public static Map<String, List<Method>> getMapWithAnnotatedMethods(final List<Class<?>> clsList,
	    final Class<? extends Annotation> annotation) {
	final Map<String, List<Method>> testMethods = new HashMap<>();
	for (Class<?> cls : clsList) {

	    final Method[] methods = cls.getDeclaredMethods();
	    final List<Method> methodList = new ArrayList<>();
	    for (Method method : methods) {
		if (method.isAnnotationPresent(annotation)) {
		    methodList.add(method);
		}
	    }
	    if (!(methodList.size() == 0)) {
		testMethods.put(cls.getName(), methodList);
	    }
	}
	return testMethods;
    }

}