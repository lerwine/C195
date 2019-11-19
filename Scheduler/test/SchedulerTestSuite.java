/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 *
 * @author Leonard T. Erwine
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({})
public class SchedulerTestSuite {

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void UserConstructorTest() {
        
    }
    
    @Test
    public void GetFieldMappingsTest() {
      Assert.assertNotNull(this);
    }
    
    @After
    public void tearDown() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
}
