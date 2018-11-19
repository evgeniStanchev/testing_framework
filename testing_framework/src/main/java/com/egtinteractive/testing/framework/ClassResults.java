package com.egtinteractive.testing.framework;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class ClassResults {

    private final Map<Class<?>, Map<Result, List<String>>> results = new HashMap<>();

    public void addResults(final Class<?> cls, final Result result, final List<String> methodNames) {
	if (!this.results.containsKey(cls)) {
	    final Map<Result, List<String>> classMethods = new HashMap<>();
	    classMethods.put(result, methodNames);
	    this.results.put(cls, classMethods);
	} else {
	    if (!this.results.get(cls).containsKey(result)) {
		this.results.get(cls).put(result, methodNames);
	    } else {
		final List<String> oldMethods = this.results.get(cls).get(result);
		oldMethods.addAll(methodNames);
		this.results.get(cls).put(result, oldMethods);
	    }
	}
    }
    

    public int getSize(final Class<?> cls, final Result result) {
	return this.results.get(cls).get(result).size();
    }
}