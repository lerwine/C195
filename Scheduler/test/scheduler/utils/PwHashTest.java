package scheduler.utils;

import scheduler.util.PwHash;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
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
