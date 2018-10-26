package com.egtinteractive.testing.framework.tests.dataprovider;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class CorrectValuesTests {
    @DataProvider
    public Object[][] intProvider() {
	return new Object[][] { { ThreadLocalRandom.current().nextInt() }, { ThreadLocalRandom.current().nextInt() } };
    }

    @DataProvider
    public Object[][] multipleValues() {
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

    @Test(dataProvider = "multipleValues")
    public void testMultipleValues(final int num1, final int num2) {

    }

}
