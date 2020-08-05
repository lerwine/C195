/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.function.Supplier;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ValuesTest {
    
    public ValuesTest() {
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
     * Test of asNonNullAndWsNormalizedMultiLine method, of class Values.
     */
    @Test
    public void testAsNonNullAndWsNormalizedMultiLine() {
        System.out.println("asNonNullAndWsNormalizedMultiLine");
        String value = "Put your right hand out, give a firm handshake \n" +
"Talk to me about that     one big break\n" +
"   Spread your ear pollution, both far and wide\n" +
"Keep your contributions by your side";
        String expResult = "Put your right hand out, give a firm handshake\n" +
"Talk to me about that one big break\n" +
"Spread your ear pollution, both far and wide\n" +
"Keep your contributions by your side";
        String result = Values.asNonNullAndWsNormalizedMultiLine(value);
        assertEquals(expResult, result);
    }

}
