package com.egtinteractive.testing.framework.tests.dataprovider;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.internal.reflect.MethodMatcherException;

public class MethodMatcherExceptionTest {

    @DataProvider
    public Object[][] intProvider() {
	return new Object[][] { { ThreadLocalRandom.current().nextInt() }, { ThreadLocalRandom.current().nextInt() } };
    }

    @DataProvider
    public Object[][] stringProvider() {
	return new Object[][] { { UUID.randomUUID().toString() }, { UUID.randomUUID().toString() } };
    }

    @Test(dataProvider = "theProvider", expectedExceptions = MethodMatcherException.class)
    public void notExistingDataProvider(final int num) throws MethodMatcherException {
    }

    @Test(dataProvider = "stringProvider", expectedExceptions = MethodMatcherException.class)
    public void wrongNumArguments(final String str1, final String str2) throws MethodMatcherException {
    }

    @Test(dataProvider = "stringProvider")
    public void correctValues(final String str1) {
    }
}
