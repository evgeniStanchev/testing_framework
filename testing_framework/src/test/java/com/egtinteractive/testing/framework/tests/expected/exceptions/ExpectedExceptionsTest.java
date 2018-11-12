package com.egtinteractive.testing.framework.tests.expected.exceptions;

import org.testng.annotations.Test;

public class ExpectedExceptionsTest {

    @Test(expectedExceptions = RuntimeException.class)
    public void incorrectException1() throws NullPointerException {
	throw new NullPointerException();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void incorrectException2() throws NullPointerException {
	throw new IndexOutOfBoundsException();
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void correctException() throws NullPointerException {
	throw new NullPointerException();
    }

    @Test
    public void doesntExpectExceptionAndFailTests() throws NullPointerException {
	throw new NullPointerException();
    }

    @Test(dataProvider = "wrongName")
    public void successTestsWithoutExceptionExpected() {

    }
}