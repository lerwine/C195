/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import javafx.collections.FXCollections;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author erwinel
 */
public class OrderByTest {
    
    public OrderByTest() {
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
     * Test of of method, of class OrderBy.
     */
    @Test
    public void testOf_String_boolean() {
        System.out.println("of");
        String colName = "myCol";
        boolean isDescending = false;
        OrderBy result = OrderBy.of(colName, isDescending);
        assertEquals(colName, result.get());
        assertEquals(isDescending, result.isDescending());
        isDescending = true;
        result = OrderBy.of(colName, isDescending);
        assertEquals(colName, result.get());
        assertEquals(isDescending, result.isDescending());
        try {
            result = OrderBy.of(null, true);
        } catch (NullPointerException ex) {
            result = null;
        }
        if (result != null)
            fail("Null value did not throw NullPointerException");
        try {
            result = OrderBy.of("", true);
        } catch (AssertionError ex) {
            result = null;
        }
        if (result != null)
            fail("Empty value did not throw AssertionError");
        try {
            result = OrderBy.of(" ", true);
        } catch (AssertionError ex) {
            result = null;
        }
        if (result != null)
            fail("Whitespace value did not throw AssertionError");
    }

    /**
     * Test of of method, of class OrderBy.
     */
    @Test
    public void testOf_String() {
        System.out.println("of");
        String colName = "myCol";
        boolean isDescending = false;
        OrderBy result = OrderBy.of(colName);
        assertEquals(colName, result.get());
        assertEquals(isDescending, result.isDescending());
        try {
            result = OrderBy.of((String)null);
        } catch (NullPointerException ex) {
            result = null;
        }
        if (result != null)
            fail("Null value did not throw NullPointerException");
        try {
            result = OrderBy.of("");
        } catch (AssertionError ex) {
            result = null;
        }
        if (result != null)
            fail("Empty value did not throw AssertionError");
        try {
            result = OrderBy.of(" ");
        } catch (AssertionError ex) {
            result = null;
        }
        if (result != null)
            fail("Whitespace value did not throw AssertionError");
    }

    /**
     * Test of of method, of class OrderBy.
     */
    @Test
    public void testOf_OrderByArr() {
        System.out.println("of");
        OrderBy[] items = null;
        List<OrderBy> expResult = null;
        List<OrderBy> result = OrderBy.of(items);
        assertTrue(result.isEmpty());
        items = new OrderBy[0];
        result = OrderBy.of(items);
        assertTrue(result.isEmpty());
        result = OrderBy.of(OrderBy.of("test"));
        assertEquals(1, result.size());
        assertEquals("test", result.get(0).get());
        assertFalse(result.get(0).isDescending());
        result = OrderBy.of(OrderBy.of("test1"), OrderBy.of("test2", true));
        assertEquals(2, result.size());
        assertEquals("test1", result.get(0).get());
        assertFalse(result.get(0).isDescending());
        assertEquals("test2", result.get(1).get());
        assertTrue(result.get(1).isDescending());
    }

    /**
     * Test of toSqlClause method, of class OrderBy.
     */
    @Test
    public void testToSqlClause() {
        System.out.println("toSqlClause");
        Iterable<OrderBy> orderBy = null;
        String expResult = "";
        String result = OrderBy.toSqlClause(orderBy);
        assertEquals(expResult, result);
        orderBy = FXCollections.singletonObservableList(OrderBy.of("test"));
        expResult = "ORDER BY `test`";
        result = OrderBy.toSqlClause(orderBy);
        assertEquals(expResult, result);
        orderBy = FXCollections.singletonObservableList(OrderBy.of("test", true));
        expResult = "ORDER BY `test` DESC";
        result = OrderBy.toSqlClause(orderBy);
        assertEquals(expResult, result);
        orderBy = FXCollections.observableArrayList(OrderBy.of("test1"), OrderBy.of("test2", true));
        expResult = "ORDER BY `test1`, `test2` DESC";
        result = OrderBy.toSqlClause(orderBy);
        assertEquals(expResult, result);
    }

    /**
     * Test of getOrderByOrDefault method, of class OrderBy.
     */
    @Test
    public void testGetOrderByOrDefault() {
        System.out.println("getOrderByOrDefault");
        Iterable<OrderBy> orderBy = null;
        Iterable<OrderBy> expResult = FXCollections.observableArrayList(OrderBy.of("test1"), OrderBy.of("test2", true));
        Supplier<Iterable<OrderBy>> ifEmpty = () -> expResult;
        Iterable<OrderBy> result = OrderBy.getOrderByOrDefault(orderBy, ifEmpty);
        assertSame(expResult, result);
        orderBy = new ArrayList<>();
        result = OrderBy.getOrderByOrDefault(orderBy, ifEmpty);
        assertSame(expResult, result);
        orderBy = FXCollections.singletonObservableList(OrderBy.of("test"));
        result = OrderBy.getOrderByOrDefault(orderBy, ifEmpty);
        assertSame(orderBy, result);
    }
    
}
