/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.filter.value;

import java.sql.PreparedStatement;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import scheduler.dao.filter.ComparisonOperator;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class BooleanValueFilterTest {
    
    public BooleanValueFilterTest() {
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
     * Test of areEqual method, of class BooleanValueFilter.
     */
    @Test
    public void testAreEqual() {
        BooleanValueFilter a = null;
        BooleanValueFilter b = null;
        boolean expResult = true;
        boolean result = BooleanValueFilter.areEqual(a, b);
        assertEquals(expResult, result);
        expResult = false;
        a = BooleanValueFilter.of(true);
        result = BooleanValueFilter.areEqual(a, b);
        assertEquals(expResult, result);
        b = BooleanValueFilter.of(false);
        result = BooleanValueFilter.areEqual(a, b);
        assertEquals(expResult, result);
        b = BooleanValueFilter.ofNot(true);
        result = BooleanValueFilter.areEqual(a, b);
        assertEquals(expResult, result);
        expResult = true;
        b = BooleanValueFilter.ofTrue();
        result = BooleanValueFilter.areEqual(a, b);
        assertEquals(expResult, result);
        expResult = false;
        a = BooleanValueFilter.ofNot(false);
        b = BooleanValueFilter.of(false);
        result = BooleanValueFilter.areEqual(a, b);
        assertEquals(expResult, result);
        b = BooleanValueFilter.of(false);
        result = BooleanValueFilter.areEqual(a, b);
        assertEquals(expResult, result);
        expResult = true;
        b = BooleanValueFilter.ofNot(false);
        result = BooleanValueFilter.areEqual(a, b);
        assertEquals(expResult, result);
    }

    /**
     * Test of ofTrue method, of class BooleanValueFilter.
     */
    @Test
    public void testOfTrue() {
        System.out.println("ofTrue");
        boolean expResult = true;
        BooleanValueFilter target = BooleanValueFilter.ofTrue();
        boolean result = target.getAsBoolean();
        assertEquals(expResult, result);
        result = target.get();
        assertEquals(expResult, result);
        result = target.test(true);
        assertEquals(expResult, result);
        expResult = false;
        result = target.test(false);
        assertEquals(expResult, result);
        ComparisonOperator expOp = ComparisonOperator.EQUALS;
        ComparisonOperator actualOp = target.getOperator();
        assertEquals(expOp, actualOp);
    }

    /**
     * Test of ofFalse method, of class BooleanValueFilter.
     */
    @Test
    public void testOfFalse() {
        System.out.println("ofFalse");
        BooleanValueFilter target = BooleanValueFilter.ofFalse();
        boolean expResult = false;
        boolean result = target.getAsBoolean();
        assertEquals(expResult, result);
        result = target.get();
        assertEquals(expResult, result);
        result = target.test(true);
        assertEquals(expResult, result);
        expResult = true;
        result = target.test(false);
        assertEquals(expResult, result);
        ComparisonOperator expOp = ComparisonOperator.EQUALS;
        ComparisonOperator actualOp = target.getOperator();
        assertEquals(expOp, actualOp);
    }

    /**
     * Test of of method, of class BooleanValueFilter.
     */
    @Test
    public void testOf() {
        System.out.println("of");
        boolean value = false;
        BooleanValueFilter target = BooleanValueFilter.of(value);
        boolean expResult = false;
        boolean result = target.getAsBoolean();
        assertEquals(expResult, result);
        result = target.get();
        assertEquals(expResult, result);
        result = target.test(true);
        assertEquals(expResult, result);
        expResult = true;
        result = target.test(false);
        assertEquals(expResult, result);
        ComparisonOperator expOp = ComparisonOperator.EQUALS;
        ComparisonOperator actualOp = target.getOperator();
        assertEquals(expOp, actualOp);
        
        value = true;
        target = BooleanValueFilter.of(value);
        result = target.getAsBoolean();
        assertEquals(expResult, result);
        result = target.get();
        assertEquals(expResult, result);
        result = target.test(true);
        assertEquals(expResult, result);
        expResult = false;
        result = target.test(false);
        assertEquals(expResult, result);
        actualOp = target.getOperator();
        assertEquals(expOp, actualOp);
    }

    /**
     * Test of ofNot method, of class BooleanValueFilter.
     */
    @Test
    public void testOfNot() {
        System.out.println("ofNot");
        boolean value = false;
        BooleanValueFilter target = BooleanValueFilter.ofNot(value);
        boolean expResult = false;
        boolean result = target.getAsBoolean();
        assertEquals(expResult, result);
        result = target.get();
        assertEquals(expResult, result);
        result = target.test(false);
        assertEquals(expResult, result);
        expResult = true;
        result = target.test(true);
        assertEquals(expResult, result);
        ComparisonOperator expOp = ComparisonOperator.NOT_EQUALS;
        ComparisonOperator actualOp = target.getOperator();
        assertEquals(expOp, actualOp);
        
        value = true;
        target = BooleanValueFilter.ofNot(value);
        result = target.getAsBoolean();
        assertEquals(expResult, result);
        result = target.get();
        assertEquals(expResult, result);
        result = target.test(false);
        assertEquals(expResult, result);
        expResult = false;
        result = target.test(true);
        assertEquals(expResult, result);
        actualOp = target.getOperator();
        assertEquals(expOp, actualOp);
    }

}
