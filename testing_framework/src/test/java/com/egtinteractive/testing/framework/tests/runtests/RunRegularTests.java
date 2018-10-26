package com.egtinteractive.testing.framework.tests.runtests;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.egtinteractive.testing.framework.MyTestingFramework;
import com.egtinteractive.testing.framework.MyTestingFramework.Builder;
import com.egtinteractive.testing.framework.tests.regular.AfterTestTesting;
import com.egtinteractive.testing.framework.tests.regular.BeforeTestTesting;
import com.egtinteractive.testing.framework.tests.regular.MultipleTestClasses;

public class RunRegularTests {

    private final List<Class<?>> list = new ArrayList<>();
    private Builder builder;
    private MyTestingFramework tf;

    @BeforeTest
    public void putTests() {
	list.add(MultipleTestClasses.class);
	list.add(BeforeTestTesting.class);
	list.add(AfterTestTesting.class);
	this.builder = new Builder(list);
	this.tf = new MyTestingFramework(builder);
    }

    @Test
    public void runRegulaTests() {
	tf.run();
    }
}