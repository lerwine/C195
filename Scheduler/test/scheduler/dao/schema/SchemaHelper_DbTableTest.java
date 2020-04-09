/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.schema;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import testHelpers.ReflectionHelper;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
@RunWith(Parameterized.class)
public class SchemaHelper_DbTableTest {
    private final String title;
    private final DbTable table;
    private final DbColumn expPrimaryKey;
    private final DbColumn[] expTableColumns;
    
    public SchemaHelper_DbTableTest(String title, DbTable table, DbColumn expPrimaryKey, DbColumn[] expTableColumns) {
        this.title = title;
        this.table = table;
        this.expPrimaryKey = expPrimaryKey;
        this.expTableColumns = expTableColumns;
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

    @Parameterized.Parameters(name = "table={0}")
    public static Collection getDbTables() {
        return Arrays.asList(
                new Object[] {
                    DbTable.ADDRESS.name(), DbTable.ADDRESS, DbColumn.ADDRESS_ID,
                    new DbColumn[] {
                        DbColumn.ADDRESS1, DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY, DbColumn.POSTAL_CODE, DbColumn.PHONE, DbColumn.ADDRESS_ID,
                        DbColumn.ADDRESS_CREATE_DATE, DbColumn.ADDRESS_CREATED_BY, DbColumn.ADDRESS_LAST_UPDATE, DbColumn.ADDRESS_LAST_UPDATE_BY
                    }
                }, new Object[] {
                    DbTable.APPOINTMENT.name(), DbTable.APPOINTMENT, DbColumn.APPOINTMENT_ID,
                    new DbColumn[] {
                        DbColumn.APPOINTMENT_CUSTOMER, DbColumn.APPOINTMENT_USER, DbColumn.TITLE, DbColumn.DESCRIPTION, DbColumn.LOCATION,
                        DbColumn.CONTACT, DbColumn.TYPE, DbColumn.URL, DbColumn.START, DbColumn.END, DbColumn.APPOINTMENT_ID,
                        DbColumn.APPOINTMENT_CREATE_DATE, DbColumn.APPOINTMENT_CREATED_BY, DbColumn.APPOINTMENT_LAST_UPDATE,
                        DbColumn.APPOINTMENT_LAST_UPDATE_BY
                    }
                }, new Object[] {
                    DbTable.CITY.name(), DbTable.CITY, DbColumn.CITY_ID,
                    new DbColumn[] {
                        DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.CITY_ID, DbColumn.CITY_CREATE_DATE, DbColumn.CITY_CREATED_BY,
                        DbColumn.CITY_LAST_UPDATE, DbColumn.CITY_LAST_UPDATE_BY
                    }
                }, new Object[] {
                    DbTable.COUNTRY.name(), DbTable.COUNTRY, DbColumn.COUNTRY_ID,
                    new DbColumn[] {
                        DbColumn.COUNTRY_NAME, DbColumn.COUNTRY_ID, DbColumn.COUNTRY_CREATE_DATE, DbColumn.COUNTRY_CREATED_BY,
                        DbColumn.COUNTRY_LAST_UPDATE, DbColumn.COUNTRY_LAST_UPDATE_BY
                    }
                }, new Object[] {
                    DbTable.CUSTOMER.name(), DbTable.CUSTOMER, DbColumn.CUSTOMER_ID,
                    new DbColumn[] {
                        DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ACTIVE, DbColumn.CUSTOMER_ID, DbColumn.CUSTOMER_CREATE_DATE,
                        DbColumn.CUSTOMER_CREATED_BY, DbColumn.CUSTOMER_LAST_UPDATE, DbColumn.CUSTOMER_LAST_UPDATE_BY
                    }
                }, new Object[] {
                    DbTable.USER.name(), DbTable.USER, DbColumn.USER_ID,
                    new DbColumn[] {
                        DbColumn.USER_NAME, DbColumn.PASSWORD, DbColumn.STATUS, DbColumn.USER_ID, DbColumn.USER_CREATE_DATE,
                        DbColumn.USER_CREATED_BY, DbColumn.USER_LAST_UPDATE, DbColumn.USER_LAST_UPDATE_BY
                    }
                }
        );
    }
    
    /**
     * Test of getPrimaryKey method, of class SchemaHelper.
     */
    @Test
    public void testGetPrimaryKey() {
        DbColumn result = SchemaHelper.getPrimaryKey(table);
        assertEquals(expPrimaryKey, result);
    }

    /**
     * Test of getTableColumnMap method, of class SchemaHelper.
     */
    @Test
    public void testGetTableColumnMap() {
        Map<DbName, DbColumn> result = SchemaHelper.getTableColumnMap(table);
        assertEquals("size()", expTableColumns.length, result.size());
        for (DbColumn dbColumn : expTableColumns) {
            assertTrue(String.format("containsKey(%s)", ReflectionHelper.toJavaLiteral(dbColumn.name())),
                    result.containsKey(dbColumn.getDbName()));
            assertEquals(String.format("get(%s)", ReflectionHelper.toJavaLiteral(dbColumn.name())),
                    dbColumn, result.get(dbColumn.getDbName()));
        }
    }

    /**
     * Test of getTableColumns method, of class SchemaHelper.
     */
    @Test
    public void testGetTableColumns() {
        List<DbColumn> result = SchemaHelper.getTableColumns(table);
        assertEquals("size()", expTableColumns.length, result.size());
        for (int i = 0; i < expTableColumns.length; i++) {
            assertEquals(String.format("get(%d)", i), expTableColumns[i], result.get(i));
        }
    }

}
