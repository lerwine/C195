package scheduler;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import testHelpers.FakeApp;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334)
 */
public class AppConfigTest {
    
    public AppConfigTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        FakeApp.setUp();
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
     * Test of getDbServerName method, of class AppConfig.
     */
    @Test
    public void testGetDbServerName() {
        System.out.println("getDbServerName");
        String expResult = "3.227.166.251";
        String result = AppResources.getDbServerName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDatabaseName method, of class AppConfig.
     */
    @Test
    public void testGetDatabaseName() {
        System.out.println("getDatabaseName");
        String expResult = "U03vHM";
        String result = AppResources.getDatabaseName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getConnectionUrl method, of class AppConfig.
     */
    @Test
    public void testGetConnectionUrl() {
        System.out.println("getConnectionUrl");
        String expResult = "jdbc:mysql://3.227.166.251/U03vHM";
        String result = AppResources.getConnectionUrl();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDbLoginName method, of class AppConfig.
     */
    @Test
    public void testGetDbLoginName() {
        System.out.println("getDbLoginName");
        String expResult = "U03vHM";
        String result = AppResources.getDbLoginName();
        assertEquals(expResult, result);
    }

    /**
     * Test of getDbLoginPassword method, of class AppConfig.
     */
    @Test
    public void testGetDbLoginPassword() {
        System.out.println("getDbLoginPassword");
        String expResult = "53688096290";
        String result = AppResources.getDbLoginPassword();
        assertEquals(expResult, result);
    }

}
