/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.dml;

import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import scheduler.dao.schema.ColumnUsage;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;

/**
 *
 * @author erwinel
 */
public class DmlTableTest {
    
    public DmlTableTest() {
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
     * Test of builder method, of class DmlTable.
     */
    @Test
    public void testBuilder_4args() {
        System.out.println("builder");
        Predicate<DbColumn> columnSelector = (t) -> t.getUsage() == ColumnUsage.DATA;
        Function<DbColumn, String> aliasMapper = (t) -> "test_" + t.getDefaultAlias();
        DmlTable.Builder result = DmlTable.builder(DbTable.CUSTOMER, "cc", columnSelector, aliasMapper);
        
        assertEquals("cc", result.getAlias());
        assertEquals(DbTable.CUSTOMER, result.getTable());
        assertEquals(1, result.getAllColumns().size());
        assertTrue(result.getAllColumns().containsKey("test_active"));
        DmlTable.BuilderColumn bc = result.getAllColumns().get("test_active");
        assertEquals("test_active", bc.getAlias());
        assertEquals(DbColumn.ACTIVE, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        assertEquals(1, result.getAllTables().size());
        assertTrue(result.getAllTables().containsKey("cc"));
        assertEquals(result, result.getAllTables().get("cc"));
        assertEquals(1, result.getColumns().size());
        assertEquals(bc, result.getColumns().get(0));
        assertTrue(result.getJoinedTables().isEmpty());
        
    }

    /**
     * Test of builder method, of class DmlTable.
     */
    @Test
    public void testBuilder_3args_1() {
        System.out.println("builder");
        DbTable table = DbTable.ADDRESS;
        String alias = "";
        Predicate<DbColumn> columnSelector = (t) -> t.getUsage() == ColumnUsage.DATA;
        DmlTable.Builder result = DmlTable.builder(table, alias, columnSelector);
        
        assertEquals(table.getDbName().toString(), result.getAlias());
        assertEquals(DbTable.ADDRESS, result.getTable());
        assertEquals(4, result.getAllColumns().size());
        assertTrue(result.getAllColumns().containsKey(DbColumn.ADDRESS1.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.ADDRESS2.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.POSTAL_CODE.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.PHONE.getDefaultAlias()));
        
        DmlTable.BuilderColumn bc = result.getAllColumns().get(DbColumn.ADDRESS1.getDefaultAlias());
        assertEquals(DbColumn.ADDRESS1.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.ADDRESS1, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.ADDRESS2.getDefaultAlias());
        assertEquals(DbColumn.ADDRESS2.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.ADDRESS2, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.POSTAL_CODE.getDefaultAlias());
        assertEquals(DbColumn.POSTAL_CODE.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.POSTAL_CODE, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.PHONE.getDefaultAlias());
        assertEquals(DbColumn.PHONE.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.PHONE, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        assertEquals(1, result.getAllTables().size());
        assertTrue(result.getAllTables().containsKey(table.getDbName().toString()));
        assertEquals(result, result.getAllTables().get(table.getDbName().toString()));
        assertEquals(4, result.getColumns().size());
        
        bc = result.getAllColumns().get(DbColumn.ADDRESS1.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(0));
        
        bc = result.getAllColumns().get(DbColumn.ADDRESS2.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(1));
        
        bc = result.getAllColumns().get(DbColumn.POSTAL_CODE.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(2));
        
        bc = result.getAllColumns().get(DbColumn.PHONE.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(3));
        
        assertTrue(result.getJoinedTables().isEmpty());
    }

    /**
     * Test of builder method, of class DmlTable.
     */
    @Test
    public void testBuilder_3args_2() {
        System.out.println("builder");
        DbTable table = DbTable.USER;
        String alias = null;
        Function<DbColumn, String> aliasMapper = (t) -> (t.getUsage() == ColumnUsage.DATA) ? "" : null;
        DmlTable.Builder result = DmlTable.builder(table, alias, aliasMapper);
        
        
        assertEquals(table.getDbName().toString(), result.getAlias());
        assertEquals(DbTable.USER, result.getTable());
        assertEquals(8, result.getAllColumns().size());
        assertTrue(result.getAllColumns().containsKey(DbColumn.USER_NAME.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.PASSWORD.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.STATUS.getDbName().toString()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.USER_ID.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.USER_CREATE_DATE.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.USER_CREATED_BY.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.USER_LAST_UPDATE.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.USER_LAST_UPDATE_BY.getDefaultAlias()));
        
        DmlTable.BuilderColumn bc = result.getAllColumns().get(DbColumn.USER_NAME.getDefaultAlias());
        assertEquals(DbColumn.USER_NAME.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.USER_NAME, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.PASSWORD.getDefaultAlias());
        assertEquals(DbColumn.PASSWORD.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.PASSWORD, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.STATUS.getDbName().toString());
        assertEquals(DbColumn.STATUS.getDbName().toString(), bc.getAlias());
        assertEquals(DbColumn.STATUS, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.USER_ID.getDefaultAlias());
        assertEquals(DbColumn.USER_ID.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.USER_ID, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.USER_CREATE_DATE.getDefaultAlias());
        assertEquals(DbColumn.USER_CREATE_DATE.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.USER_CREATE_DATE, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.USER_CREATED_BY.getDefaultAlias());
        assertEquals(DbColumn.USER_CREATED_BY.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.USER_CREATED_BY, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.USER_LAST_UPDATE.getDefaultAlias());
        assertEquals(DbColumn.USER_LAST_UPDATE.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.USER_LAST_UPDATE, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.USER_LAST_UPDATE_BY.getDefaultAlias());
        assertEquals(DbColumn.USER_LAST_UPDATE_BY.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.USER_LAST_UPDATE_BY, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        assertEquals(1, result.getAllTables().size());
        assertTrue(result.getAllTables().containsKey(table.getDbName().toString()));
        assertEquals(result, result.getAllTables().get(table.getDbName().toString()));
        assertEquals(4, result.getColumns().size());
        
        bc = result.getAllColumns().get(DbColumn.USER_NAME.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(0));
        
        bc = result.getAllColumns().get(DbColumn.PASSWORD.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(1));
        
        bc = result.getAllColumns().get(DbColumn.STATUS.getDbName().toString());
        assertEquals(bc, result.getColumns().get(2));
        
        bc = result.getAllColumns().get(DbColumn.USER_ID.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(3));
        
        bc = result.getAllColumns().get(DbColumn.USER_CREATE_DATE.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(4));
        
        bc = result.getAllColumns().get(DbColumn.USER_CREATED_BY.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(5));
        
        bc = result.getAllColumns().get(DbColumn.USER_LAST_UPDATE.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(6));
        
        bc = result.getAllColumns().get(DbColumn.USER_LAST_UPDATE_BY.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(7));
        
        assertTrue(result.getJoinedTables().isEmpty());
    }

    /**
     * Test of builder method, of class DmlTable.
     */
    @Test
    public void testBuilder_3args_3() {
        System.out.println("builder");
        DbTable table = DbTable.COUNTRY;
        String alias = "asdf";
        DmlTable.Builder result = DmlTable.builder(table, alias, true);
        
        assertEquals(alias, result.getAlias());
        assertEquals(DbTable.COUNTRY, result.getTable());
        assertEquals(0, result.getAllColumns().size());
        assertEquals(1, result.getAllTables().size());
        assertTrue(result.getAllTables().containsKey(alias));
        assertEquals(result, result.getAllTables().get(alias));
        assertEquals(0, result.getColumns().size());
        assertTrue(result.getJoinedTables().isEmpty());
        
        result = DmlTable.builder(table, alias, false);
        
        assertEquals(table.getDbName().toString(), result.getAlias());
        assertEquals(DbTable.COUNTRY, result.getTable());
        assertEquals(6, result.getAllColumns().size());
        assertTrue(result.getAllColumns().containsKey(DbColumn.COUNTRY_NAME.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.COUNTRY_ID.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.COUNTRY_CREATE_DATE.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.COUNTRY_CREATED_BY.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.COUNTRY_LAST_UPDATE.getDefaultAlias()));
        assertTrue(result.getAllColumns().containsKey(DbColumn.COUNTRY_LAST_UPDATE_BY.getDefaultAlias()));
        
        DmlTable.BuilderColumn bc = result.getAllColumns().get(DbColumn.COUNTRY_NAME.getDefaultAlias());
        assertEquals(DbColumn.COUNTRY_NAME.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.COUNTRY_NAME, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.COUNTRY_ID.getDefaultAlias());
        assertEquals(DbColumn.COUNTRY_ID.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.COUNTRY_ID, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.COUNTRY_CREATE_DATE.getDefaultAlias());
        assertEquals(DbColumn.COUNTRY_CREATE_DATE.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.COUNTRY_CREATE_DATE, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.COUNTRY_CREATED_BY.getDefaultAlias());
        assertEquals(DbColumn.COUNTRY_CREATED_BY.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.COUNTRY_CREATED_BY, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.COUNTRY_LAST_UPDATE.getDefaultAlias());
        assertEquals(DbColumn.COUNTRY_LAST_UPDATE.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.COUNTRY_LAST_UPDATE, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        bc = result.getAllColumns().get(DbColumn.COUNTRY_LAST_UPDATE_BY.getDefaultAlias());
        assertEquals(DbColumn.COUNTRY_LAST_UPDATE_BY.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.COUNTRY_LAST_UPDATE_BY, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        
        assertEquals(1, result.getAllTables().size());
        assertTrue(result.getAllTables().containsKey(table.getDbName().toString()));
        assertEquals(result, result.getAllTables().get(table.getDbName().toString()));
        assertEquals(4, result.getColumns().size());
        
        bc = result.getAllColumns().get(DbColumn.COUNTRY_NAME.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(0));
        
        bc = result.getAllColumns().get(DbColumn.COUNTRY_ID.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(1));
        
        bc = result.getAllColumns().get(DbColumn.COUNTRY_CREATE_DATE.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(2));
        
        bc = result.getAllColumns().get(DbColumn.COUNTRY_CREATED_BY.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(3));
        
        bc = result.getAllColumns().get(DbColumn.COUNTRY_LAST_UPDATE.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(4));
        
        bc = result.getAllColumns().get(DbColumn.COUNTRY_LAST_UPDATE_BY.getDefaultAlias());
        assertEquals(bc, result.getColumns().get(5));
        
        assertTrue(result.getJoinedTables().isEmpty());
    }

    /**
     * Test of builder method, of class DmlTable.
     */
    @Test
    public void testBuilder_3args_4() {
        System.out.println("builder");
        Predicate<DbColumn> columnSelector = (t) -> t.getUsage() == ColumnUsage.DATA;
        Function<DbColumn, String> aliasMapper = (t) -> null;
        DmlTable.Builder result = DmlTable.builder(DbTable.USER, columnSelector, aliasMapper);
        
        assertEquals(DbTable.USER.getAlias(), result.getAlias());
        assertEquals(DbTable.USER, result.getTable());
        assertEquals(1, result.getAllColumns().size());
        assertTrue(result.getAllColumns().containsKey(DbColumn.STATUS.getDefaultAlias()));
        DmlTable.BuilderColumn bc = result.getAllColumns().get(DbColumn.STATUS.getDefaultAlias());
        assertEquals(DbColumn.STATUS.getDefaultAlias(), bc.getAlias());
        assertEquals(DbColumn.STATUS, bc.getColumn());
        assertEquals(result, bc.getOwner());
        assertEquals(0, bc.getChildJoins().size());
        assertNull(bc.getParentJoin());
        assertEquals(1, result.getAllTables().size());
        assertTrue(result.getAllTables().containsKey(DbTable.USER.getAlias()));
        assertEquals(result, result.getAllTables().get(DbTable.USER.getAlias()));
        assertEquals(1, result.getColumns().size());
        assertEquals(bc, result.getColumns().get(0));
        assertTrue(result.getJoinedTables().isEmpty());
        
    }

//    /**
//     * Test of builder method, of class DmlTable.
//     */
//    @Test
//    public void testBuilder_DbTable_Predicate() {
//        System.out.println("builder");
//        DbTable table = null;
//        Predicate<DbColumn> columnSelector = null;
//        DmlTable.Builder expResult = null;
//        DmlTable.Builder result = DmlTable.builder(table, columnSelector);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of builder method, of class DmlTable.
//     */
//    @Test
//    public void testBuilder_DbTable_Function() {
//        System.out.println("builder");
//        DbTable table = null;
//        Function<DbColumn, String> aliasMapper = null;
//        DmlTable.Builder expResult = null;
//        DmlTable.Builder result = DmlTable.builder(table, aliasMapper);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of builder method, of class DmlTable.
//     */
//    @Test
//    public void testBuilder_DbTable_boolean() {
//        System.out.println("builder");
//        DbTable table = null;
//        boolean noColumns = false;
//        DmlTable.Builder expResult = null;
//        DmlTable.Builder result = DmlTable.builder(table, noColumns);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of builder method, of class DmlTable.
//     */
//    @Test
//    public void testBuilder_DbTable() {
//        System.out.println("builder");
//        DbTable table = null;
//        DmlTable.Builder expResult = null;
//        DmlTable.Builder result = DmlTable.builder(table);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

}
