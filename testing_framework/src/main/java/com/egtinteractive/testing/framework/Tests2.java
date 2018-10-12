package com.egtinteractive.testing.framework;

import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class Tests2 {
    @DataProvider
    public Object[][] provider(){
	return new Object[][]{{3}};
    }
    
    @Test(dataProvider = "provider")
    public void ss(int i) throws Exception {
    }

    @Test
    public void ss1() {
    }

    @Test
    public void ss2() {
    }

    @AfterTest
    public void after1() throws Exception {

    }

    @AfterTest
    public void after2() throws Exception {
    }

    @AfterTest
    public void after3() throws Exception {
	
    }

    @BeforeTest
    public void before() throws Exception {
    }
}