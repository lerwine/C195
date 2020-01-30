/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.filter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import scheduler.dao.AddressFactory;
import scheduler.view.ItemModel;
import scheduler.view.address.AddressModel;

/**
 *
 * @author erwinel
 */
public class FilterOrTest {
    
    public FilterOrTest() {
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
     * Test of combine method, of class FilterOr.
     */
    @Test
    public void testCombine() {
        System.out.println("combine");
        ModelFilter<AddressModel> x = ModelFilter.columnIsEqualTo(AddressFactory.VALUE_ACCESSOR_ADDRESS1, ModelFilter.COMPARATOR_STRING, "");
        ModelFilter<AddressModel> y = ModelFilter.columnIsEqualTo(AddressFactory.VALUE_ACCESSOR_POSTALCODE, ModelFilter.COMPARATOR_STRING, "");
        String expResult = String.format("`%s`=? OR `%s`=?", AddressFactory.COLNAME_ADDRESS, AddressFactory.COLNAME_POSTALCODE);
        ModelFilter<AddressModel> filter = FilterOr.combine(x, y);
        String result = filter.get();
        assertEquals(expResult, result);
        y = ModelFilter.columnIsEqualTo(AddressFactory.VALUE_ACCESSOR_CITY_NAME, ModelFilter.COMPARATOR_STRING, "");
        filter = FilterAnd.combine(filter, y);
        expResult = String.format("(%s) AND `%s`=?", expResult, AddressFactory.VALUE_ACCESSOR_CITY_NAME);
        result = filter.get();
        assertEquals(expResult, result);
    }

    /**
     * Test of join method, of class FilterOr.
     */
    @Test
    public void testJoin() {
        System.out.println("join");
        ModelFilter[] items = null;
        ModelFilter expResult = null;
        ModelFilter result = FilterOr.join(items);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of add method, of class FilterOr.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        FilterOr instance = null;
        boolean expResult = false;
        boolean result = instance.add(null);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of or method, of class FilterOr.
     */
    @Test
    public void testOr() {
        System.out.println("or");
        FilterOr instance = null;
        ModelFilter expResult = null;
        ModelFilter result = instance.or(null);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getOperator method, of class FilterOr.
     */
    @Test
    public void testGetOperator() {
        System.out.println("getOperator");
        FilterOr instance = null;
        String expResult = "";
        String result = instance.getOperator();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of setParameterValues method, of class FilterOr.
     */
    @Test
    public void testSetParameterValues() throws Exception {
        System.out.println("setParameterValues");
        ParameterConsumer consumer = null;
        FilterOr instance = null;
        instance.setParameterValues(consumer);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isCompound method, of class FilterOr.
     */
    @Test
    public void testIsCompound() {
        System.out.println("isCompound");
        FilterOr instance = null;
        boolean expResult = false;
        boolean result = instance.isCompound();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of makeClone method, of class FilterOr.
     */
    @Test
    public void testMakeClone() {
        System.out.println("makeClone");
        FilterOr instance = null;
        ModelFilter expResult = null;
        ModelFilter result = instance.makeClone();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toConditional method, of class FilterOr.
     */
    @Test
    public void testToConditional() {
        System.out.println("toConditional");
        FilterOr instance = null;
        SqlConditional expResult = null;
        SqlConditional result = instance.toConditional();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of test method, of class FilterOr.
     */
    @Test
    public void testTest() {
        System.out.println("test");
        ItemModel t = null;
        FilterOr instance = null;
        boolean expResult = false;
        boolean result = instance.test(t);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of get method, of class FilterOr.
     */
    @Test
    public void testGet() {
        System.out.println("get");
        FilterOr instance = null;
        String expResult = "";
        String result = instance.get();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getColName method, of class FilterOr.
     */
    @Test
    public void testGetColName() {
        System.out.println("getColName");
        FilterOr instance = null;
        String expResult = "";
        String result = instance.getColName();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
