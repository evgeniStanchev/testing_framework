package com.egtinteractive.testing.framework.tests.regular;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class BeforeTestTesting {

    @BeforeTest
    public void test1() {
	throw new RuntimeException();
    }

    @Test
    public void test2() {

    }

    @Test
    public void test3() {

    }

    @Test
    public void test4() {

    }

}
