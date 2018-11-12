package com.egtinteractive.testing.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClassResults {

    private final Map<Class<?>, Map<Result, List<String>>> results = new HashMap<>();

    public void addResults(final Class<?> cls, final Result result, final List<String> methodNames) {
	if (!results.containsKey(cls)) {
	    final Map<Result, List<String>> classMethods = new HashMap<>();
	    classMethods.put(result, methodNames);
	    results.put(cls, classMethods);
	} else {
	    if (!results.get(cls).containsKey(result)) {
		results.get(cls).put(result, methodNames);
	    } else {
		final List<String> oldMethods = results.get(cls).get(result);
		oldMethods.addAll(methodNames);
		results.get(cls).put(result, oldMethods);
	    }
	}
    }

    public int getSize(final Class<?> cls, final Result result) {
	return results.get(cls).get(result).size();
    }
}