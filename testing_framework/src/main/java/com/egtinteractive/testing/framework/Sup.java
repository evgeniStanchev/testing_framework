package com.egtinteractive.testing.framework;

import java.util.ArrayList;
import java.util.List;

public class Sup {
    public static void main(String[] args) throws Exception {
	List<Class<?>> list = new ArrayList<>();
	list.add(Tests.class);
	TestingFramework tf = new MyTestingFramework(list);
	tf.run();
    }
}