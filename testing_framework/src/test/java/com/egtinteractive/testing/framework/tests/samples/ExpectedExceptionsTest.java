package com.egtinteractive.testing.framework.tests.samples;

import org.testng.annotations.Test;

public class ExpectedExceptionsTest implements TestSamples {

    private int passedTests=2;
    private int failedTests=3;
    private int skippedTests=0;

    @Test(expectedExceptions = NullPointerException.class)
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
    
    @Override
    public int getPassedTests() {
	return this.passedTests;
    }

    @Override
    public int getFailedTests() {
	return this.failedTests;
    }

    @Override
    public int getSkippedTests() {
	return this.skippedTests;
    }    
}