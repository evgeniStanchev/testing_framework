package com.egtinteractive.testing.framework;


import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class Tests {

    @Test
    public void test1()  {
	throw new IndexOutOfBoundsException();
    }

    @Test
    public void test2() {
    }

    @Test
    public void test3() {
    }

    @AfterTest
    public void aftertest1() {
    }

    @AfterTest
    public void aftertest2() {
    }

    @AfterTest
    public void aftertest3() throws Exception {

    }

    @BeforeTest
    public void beforetest1() throws Exception {
    }

    @BeforeTest
    public void beforetest2() {
    }
}