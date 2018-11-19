package com.egtinteractive.testing.framework.tests.samples;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CorrectValuesTests implements TestSamples {

    private int passedTests = 6;
    private int failedTests = 0;
    private int skippedTests = 0;

    @BeforeTest
    public void something() {

    }

    @DataProvider
    public Object[][] intProvider() {
	return new Object[][] { { ThreadLocalRandom.current().nextInt() }, { ThreadLocalRandom.current().nextInt() } };
    }

    @DataProvider
    public Object[][] multipleArguments() {
	return new Object[][] { { ThreadLocalRandom.current().nextInt(), ThreadLocalRandom.current().nextInt() } };
    }

    @DataProvider
    public Object[][] stringProvider() {
	return new Object[][] { { UUID.randomUUID().toString() }, { UUID.randomUUID().toString() } };
    }

    @Test(dataProvider = "stringProvider")
    public void testString(final String str) {
    }

    @Test(dataProvider = "intProvider")
    public void testInteger(final int num) {
    }

    @Test(dataProvider = "multipleArguments")
    public void testMultipleValues(final int num1, final int num2) {
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