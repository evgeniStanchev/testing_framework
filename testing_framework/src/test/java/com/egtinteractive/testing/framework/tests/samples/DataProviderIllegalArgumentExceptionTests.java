package com.egtinteractive.testing.framework.tests.samples;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class DataProviderIllegalArgumentExceptionTests implements TestSamples {

    private int passedTests = 0;
    private int failedTests = 3;
    private int skippedTests = 0;
    
    @DataProvider
    public Object[][] intProvider() {
	return new Object[][] { { ThreadLocalRandom.current().nextInt() }, { UUID.randomUUID().toString() } };
    }

    @DataProvider
    public Object[][] stringProvider() {
	return new Object[][] { { UUID.randomUUID().toString() }, { UUID.randomUUID().toString() } };
    }

    @Test(dataProvider = "stringProvider")
    public void wrongTypeArguments1(final int num) {
    }

    @Test(dataProvider = "intProvider")
    public void wrongTypeArguments2(final String str) {
    }

    @Test
    public void hasArgumentsButWithoutDataProviderTest(int i) {
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