package com.egtinteractive.testing.framework.tests.regular;

import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

public class AfterTestTesting {

    @AfterTest
    public void expectedZeroFailures() {
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
