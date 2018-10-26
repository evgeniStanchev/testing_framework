package com.egtinteractive.testing.framework.tests.dataprovider;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class IllegalArgumentExceptionTests {

    @DataProvider
    public Object[][] intProvider() {
	return new Object[][] { { ThreadLocalRandom.current().nextInt() }, { ThreadLocalRandom.current().nextInt() } };
    }

    @DataProvider
    public Object[][] stringProvider() {
	return new Object[][] { { UUID.randomUUID().toString() }, { UUID.randomUUID().toString() } };
    }



    @Test(dataProvider = "stringProvider", expectedExceptions = IllegalArgumentException.class)
    public void wrongTypeArguments1(final int num) throws IllegalArgumentException {
    }

    @Test(dataProvider = "intProvider", expectedExceptions = IllegalArgumentException.class)
    public void wrongTypeArguments2(final String str) throws IllegalArgumentException {
    }

    

}
