package com.example.app.helpers;

import junit.framework.TestCase;

import java.util.HashMap;

public class IndexActorHelperTest extends TestCase {

    public void setUp() throws Exception {
        super.setUp();

    }

    public void tearDown() throws Exception {

    }

    public void testCalculateIndex() throws Exception {
        HashMap<String, Double > test = new HashMap<>();
        test.put("v1",5.0);
        test.put("v2",7.0);
        IndexActorHelper tester = new IndexActorHelper();
        Double ret = tester.calculateIndex(test);
        assertEquals(5.91607978,ret,0.001);


    }
}