package com.egtinteractive.testing.framework;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.reflect.MethodMatcherException;

public final class TestingFrameworkUtils {

    public static final Class<?> DATA_PROVIDERS = DataProvider.class;
    public static final Logger LOG = new MyLogger();

    static boolean hasExceptionExpected(final Method method) {
	final Test test = method.getAnnotation(Test.class);
	return (test.expectedExceptions().length == 0) ? false : true;
    }

    static <T, E> int getMethodsCount(final Map<T, List<E>> map) {
	int size = 0;
	if (map != null) {
	    for (List<E> list : map.values()) {
		size += list.size();
	    }
	}
	return size;
    }

    static <E> void invokeWithDataProvider(final Method method, final Class<E> cls,
	    final Map<String, Method> dataProviders, final List<String> successfulTests, final Object obj)
	    throws Exception {
	if (!hasDataProvider(method)) {
	    if (method.getParameterCount() == 0) {
		method.invoke(obj, (Object[]) null);
		successfulTests.add(method.getName());
		return;
	    }
	    LOG.throwDataProviderException(method);
	}
	final String dataProviderName = method.getAnnotation(Test.class).dataProvider();
	final Method dataProvider = dataProviders.get(dataProviderName);

	if (dataProvider == null) {
	    LOG.throwDataProviderException(method, "has not available data provider");
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
		    successfulTests.add(method.getName() + "(" + returnValues[i][j] + ")");
		} catch (final MethodMatcherException e) {
		    LOG.printException(e);
		}
	    }
	}
    }

    static <E> List<Method> getListWithAnnotatedMethods(final Class<E> cls,
	    final Class<? extends Annotation> annotation) {
	final List<Method> methodList = new ArrayList<>();
	final Method[] methods = cls.getDeclaredMethods();
	for (Method method : methods) {
	    if (method.isAnnotationPresent(annotation)) {
		methodList.add(method);
	    }
	}
	return methodList;
    }

    static Map<String, Method> getMapWithAnnotatedMethods(final Class<?> cls,
	    final Class<? extends Annotation> annotation) {
	final Map<String, Method> testMethods = new HashMap<>();
	Class<?> currentClass = cls;
	while (currentClass.getSuperclass() != null) {
	    for (Method method : currentClass.getDeclaredMethods()) {
		if (method.isAnnotationPresent(annotation)) {
		    testMethods.put(method.getName(), method);
		}
	    }
	    currentClass = currentClass.getSuperclass();
	}
	return testMethods;
    }

    private static boolean hasDataProvider(final Method method) {
	final Test test = method.getAnnotation(Test.class);
	return test.dataProvider().equals("") ? false : true; //$NON-NLS-1$
    }
}