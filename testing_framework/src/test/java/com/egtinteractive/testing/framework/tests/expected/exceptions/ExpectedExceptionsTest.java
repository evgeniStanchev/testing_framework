package com.egtinteractive.testing.framework.tests.expected.exceptions;

import org.testng.annotations.Test;

public class ExpectedExceptionsTest {

    @Test(expectedExceptions = RuntimeException.class)
    public void incorrectException1() throws NullPointerException {
	try {
	    throw new NullPointerException();
	} catch (NullPointerException e) {

	}
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void incorrectException2() throws NullPointerException {
	try {
	    throw new IndexOutOfBoundsException();
	} catch (IndexOutOfBoundsException e) {

	}
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void correctException() throws NullPointerException {
	try {
	    throw new NullPointerException();
	} catch (NullPointerException e) {
	}
    }
}