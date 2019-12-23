/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import scheduler.PwHash;
import scheduler.InvalidArgumentException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Leonard T. Erwine
 */
public class PwHashTest {
    
    public PwHashTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of toString method, of class PwHash.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        PwHash instance = new PwHash("Password123!@#", true);
        int expLength = 50;
        String result = instance.toString();
        assertEquals(expLength, result.length());
    }

    /**
     * Test of test method, of class PwHash.
     */
    @Test
    public void testTest() {
        System.out.println("test");
        String password = "";
        PwHash instance = new PwHash("Password123!@#", true);
        boolean expResult = false;
        boolean result = instance.test(password);
        assertEquals(expResult, result);
        password = "Password123!@";
        result = instance.test(password);
        assertEquals(expResult, result);
        password = "Password123!@#";
        expResult = true;
        result = instance.test(password);
        assertEquals(expResult, result);
    }
}
