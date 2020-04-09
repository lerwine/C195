package scheduler.dao.schema;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
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
public class DbColumnTest {
    
    public DbColumnTest() {
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
     * Test of getColumns method, of class DbColumn.
     */
    @Test
    public void testGetColumns_TableName() {
        System.out.println("getColumns");
        DbTable tableName = DbTable.ADDRESS;
        Stream<DbColumn> expResult = Stream.of(DbColumn.ADDRESS1, DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY, DbColumn.POSTAL_CODE, DbColumn.PHONE,
                DbColumn.ADDRESS_ID, DbColumn.ADDRESS_CREATE_DATE, DbColumn.ADDRESS_CREATED_BY, DbColumn.ADDRESS_LAST_UPDATE,
                DbColumn.ADDRESS_LAST_UPDATE_BY);
        List<DbColumn> result = SchemaHelper.getTableColumns(tableName);
        Iterator<DbColumn> x = expResult.iterator();
        Iterator<DbColumn> r = result.iterator();
        while (x.hasNext()) {
            assertTrue(r.hasNext());
            assertEquals(x.next(), r.next());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.APPOINTMENT;
        expResult = Stream.of(DbColumn.APPOINTMENT_CUSTOMER, DbColumn.APPOINTMENT_USER, DbColumn.TITLE, DbColumn.DESCRIPTION, DbColumn.LOCATION,
                DbColumn.CONTACT, DbColumn.TYPE, DbColumn.URL, DbColumn.START, DbColumn.END, DbColumn.APPOINTMENT_ID,
                DbColumn.APPOINTMENT_CREATE_DATE, DbColumn.APPOINTMENT_CREATED_BY, DbColumn.APPOINTMENT_LAST_UPDATE,
                DbColumn.APPOINTMENT_LAST_UPDATE_BY);
        result = SchemaHelper.getTableColumns(tableName);
        x = expResult.iterator();
        r = result.iterator();
        while (x.hasNext()) {
            assertTrue(r.hasNext());
            assertEquals(x.next(), r.next());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.CITY;
        expResult = Stream.of(DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.CITY_ID, DbColumn.CITY_CREATE_DATE, DbColumn.CITY_CREATED_BY,
                DbColumn.CITY_LAST_UPDATE, DbColumn.CITY_LAST_UPDATE_BY);
        result = SchemaHelper.getTableColumns(tableName);
        x = expResult.iterator();
        r = result.iterator();
        while (x.hasNext()) {
            assertTrue(r.hasNext());
            assertEquals(x.next(), r.next());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.COUNTRY;
        expResult = Stream.of(DbColumn.COUNTRY_NAME, DbColumn.COUNTRY_ID, DbColumn.COUNTRY_CREATE_DATE, DbColumn.COUNTRY_CREATED_BY,
                DbColumn.COUNTRY_LAST_UPDATE, DbColumn.COUNTRY_LAST_UPDATE_BY);
        result = SchemaHelper.getTableColumns(tableName);
        x = expResult.iterator();
        r = result.iterator();
        while (x.hasNext()) {
            assertTrue(r.hasNext());
            assertEquals(x.next(), r.next());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.CUSTOMER;
        expResult = Stream.of(DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ACTIVE, DbColumn.CUSTOMER_ID, DbColumn.CUSTOMER_CREATE_DATE,
                DbColumn.CUSTOMER_CREATED_BY, DbColumn.CUSTOMER_LAST_UPDATE, DbColumn.CUSTOMER_LAST_UPDATE_BY);
        result = SchemaHelper.getTableColumns(tableName);
        x = expResult.iterator();
        r = result.iterator();
        while (x.hasNext()) {
            assertTrue(r.hasNext());
            assertEquals(x.next(), r.next());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.USER;
        expResult = Stream.of(DbColumn.USER_NAME, DbColumn.PASSWORD, DbColumn.STATUS, DbColumn.USER_ID, DbColumn.USER_CREATE_DATE,
                DbColumn.USER_CREATED_BY, DbColumn.USER_LAST_UPDATE, DbColumn.USER_LAST_UPDATE_BY);
        result = SchemaHelper.getTableColumns(tableName);
        x = expResult.iterator();
        r = result.iterator();
        while (x.hasNext()) {
            assertTrue(r.hasNext());
            assertEquals(x.next(), r.next());
        }
        assertFalse(r.hasNext());
        
    }

    /**
     * Test of getColumns method, of class DbColumn.
     */
    @Test
    public void testGetColumns_TableName_Predicate() {
        System.out.println("getColumns");
        DbTable tableName = DbTable.ADDRESS;
        Stream<DbColumn> expResult = Stream.of(DbColumn.ADDRESS1, DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY, DbColumn.POSTAL_CODE, DbColumn.PHONE,
                DbColumn.ADDRESS_ID);
        Stream<DbColumn> result = SchemaHelper.getTableColumns(tableName, (t) -> t.getUsageCategory() != ColumnCategory.AUDIT);
        Iterator<DbColumn> x = expResult.iterator();
        Iterator<DbColumn> r = result.iterator();
        while (x.hasNext()) {
            assertTrue(r.hasNext());
            assertEquals(x.next(), r.next());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.APPOINTMENT;
        expResult = Stream.of(DbColumn.APPOINTMENT_CUSTOMER, DbColumn.APPOINTMENT_USER, DbColumn.TITLE, DbColumn.DESCRIPTION, DbColumn.LOCATION,
                DbColumn.CONTACT, DbColumn.TYPE, DbColumn.URL, DbColumn.START, DbColumn.END, DbColumn.APPOINTMENT_ID);
        result = SchemaHelper.getTableColumns(tableName, (t) -> t.getUsageCategory() != ColumnCategory.AUDIT);
        x = expResult.iterator();
        r = result.iterator();
        while (x.hasNext()) {
            assertTrue(r.hasNext());
            assertEquals(x.next(), r.next());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.CITY;
        expResult = Stream.of(DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.CITY_ID);
        result = SchemaHelper.getTableColumns(tableName, (t) -> t.getUsageCategory() != ColumnCategory.AUDIT);
        x = expResult.iterator();
        r = result.iterator();
        while (x.hasNext()) {
            assertTrue(r.hasNext());
            assertEquals(x.next(), r.next());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.COUNTRY;
        expResult = Stream.of(DbColumn.COUNTRY_NAME, DbColumn.COUNTRY_ID);
        result = SchemaHelper.getTableColumns(tableName, (t) -> t.getUsageCategory() != ColumnCategory.AUDIT);
        x = expResult.iterator();
        r = result.iterator();
        while (x.hasNext()) {
            assertTrue(r.hasNext());
            assertEquals(x.next(), r.next());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.CUSTOMER;
        expResult = Stream.of(DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ACTIVE, DbColumn.CUSTOMER_ID);
        result = SchemaHelper.getTableColumns(tableName, (t) -> t.getUsageCategory() != ColumnCategory.AUDIT);
        x = expResult.iterator();
        r = result.iterator();
        while (x.hasNext()) {
            assertTrue(r.hasNext());
            assertEquals(x.next(), r.next());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.USER;
        expResult = Stream.of(DbColumn.USER_NAME, DbColumn.PASSWORD, DbColumn.STATUS, DbColumn.USER_ID);
        result = SchemaHelper.getTableColumns(tableName, (t) -> t.getUsageCategory() != ColumnCategory.AUDIT);
        x = expResult.iterator();
        r = result.iterator();
        while (x.hasNext()) {
            assertTrue(r.hasNext());
            assertEquals(x.next(), r.next());
        }
        assertFalse(r.hasNext());
    }
    /**
     * Test of getDbName method, of class DbColumn.
     */
    @Test
    public void testGetDbName() {
        System.out.println("getDbName");
        DbTable tableName = DbTable.ADDRESS;
       String[] expResult = new String[] { "address", "address2", "cityId", "postalCode", "phone", "addressId", "createDate", "createdBy", "lastUpdate",
            "lastUpdateBy" };
        List<DbColumn> result = SchemaHelper.getTableColumns(tableName);
        Iterator<DbColumn> r = result.iterator();
        for (String n : expResult) {
            assertTrue(r.hasNext());
            assertEquals(n, r.next().getDbName().toString());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.APPOINTMENT;
        expResult = new String[] { "customerId", "userId", "title", "description", "location", "contact", "type", "url", "start", "end", "appointmentId",
            "createDate", "createdBy", "lastUpdate", "lastUpdateBy" };
        result = SchemaHelper.getTableColumns(tableName);
        r = result.iterator();
        for (String n : expResult) {
            assertTrue(r.hasNext());
            assertEquals(n, r.next().getDbName().toString());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.CITY;
        expResult = new String[] { "city", "countryId", "cityId", "createDate", "createdBy", "lastUpdate", "lastUpdateBy" };
        result = SchemaHelper.getTableColumns(tableName);
        r = result.iterator();
        for (String n : expResult) {
            assertTrue(r.hasNext());
            assertEquals(n, r.next().getDbName().toString());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.COUNTRY;
        expResult = new String[] { "country", "countryId", "createDate", "createdBy", "lastUpdate", "lastUpdateBy" };
        result = SchemaHelper.getTableColumns(tableName);
        r = result.iterator();
        for (String n : expResult) {
            assertTrue(r.hasNext());
            assertEquals(n, r.next().getDbName().toString());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.CUSTOMER;
        expResult = new String[] { "customerName", "addressId", "active", "customerId", "createDate", "createdBy", "lastUpdate", "lastUpdateBy" };
        result = SchemaHelper.getTableColumns(tableName);
        r = result.iterator();
        for (String n : expResult) {
            assertTrue(r.hasNext());
            assertEquals(n, r.next().getDbName().toString());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.USER;
        expResult = new String[] { "userName", "password", "active", "userId", "createDate", "createdBy", "lastUpdate", "lastUpdateBy" };
        result = SchemaHelper.getTableColumns(tableName);
        r = result.iterator();
        for (String n : expResult) {
            assertTrue(r.hasNext());
            assertEquals(n, r.next().getDbName().toString());
        }
        assertFalse(r.hasNext());
    }

    /**
     * Test of getAlias method, of class DbColumn.
     */
    @Test
    public void testGetAlias() {
        System.out.println("getAlias");
        DbTable tableName = DbTable.ADDRESS;
       String[] expResult = new String[] { "address", "address2", "cityId", "postalCode", "phone", "addressId", "createDate", "createdBy", "lastUpdate",
            "lastUpdateBy" };
        List<DbColumn> result = SchemaHelper.getTableColumns(tableName);
        Iterator<DbColumn> r = result.iterator();
        for (String n : expResult) {
            assertTrue(r.hasNext());
            assertEquals(n, r.next().toString());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.APPOINTMENT;
        expResult = new String[] { "customerId", "userId", "title", "description", "location", "contact", "type", "url", "start", "end", "appointmentId",
            "createDate", "createdBy", "lastUpdate", "lastUpdateBy" };
        result = SchemaHelper.getTableColumns(tableName);
        r = result.iterator();
        for (String n : expResult) {
            assertTrue(r.hasNext());
            assertEquals(n, r.next().toString());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.CITY;
        expResult = new String[] { "city", "countryId", "cityId", "createDate", "createdBy", "lastUpdate", "lastUpdateBy" };
        result = SchemaHelper.getTableColumns(tableName);
        r = result.iterator();
        for (String n : expResult) {
            assertTrue(r.hasNext());
            assertEquals(n, r.next().toString());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.COUNTRY;
        expResult = new String[] { "country", "countryId", "createDate", "createdBy", "lastUpdate", "lastUpdateBy" };
        result = SchemaHelper.getTableColumns(tableName);
        r = result.iterator();
        for (String n : expResult) {
            assertTrue(r.hasNext());
            assertEquals(n, r.next().toString());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.CUSTOMER;
        expResult = new String[] { "customerName", "addressId", "active", "customerId", "createDate", "createdBy", "lastUpdate", "lastUpdateBy" };
        result = SchemaHelper.getTableColumns(tableName);
        r = result.iterator();
        for (String n : expResult) {
            assertTrue(r.hasNext());
            assertEquals(n, r.next().toString());
        }
        assertFalse(r.hasNext());
        
        tableName = DbTable.USER;
        expResult = new String[] { "userName", "password", "status", "userId", "createDate", "createdBy", "lastUpdate", "lastUpdateBy" };
        result = SchemaHelper.getTableColumns(tableName);
        r = result.iterator();
        for (String n : expResult) {
            assertTrue(r.hasNext());
            assertEquals(n, r.next().toString());
        }
        assertFalse(r.hasNext());
    }

    /**
     * Test of getTable method, of class DbColumn.
     */
    @Test
    public void testGetTable() {
        System.out.println("getTable");
        DbColumn[] source = new DbColumn[] {
            DbColumn.ADDRESS1, DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY, DbColumn.POSTAL_CODE, DbColumn.PHONE,
                DbColumn.ADDRESS_ID, DbColumn.ADDRESS_CREATE_DATE, DbColumn.ADDRESS_CREATED_BY, DbColumn.ADDRESS_LAST_UPDATE,
                DbColumn.ADDRESS_LAST_UPDATE_BY
        };
        DbTable expResult = DbTable.ADDRESS;
        for (DbColumn instance : source) {
            DbTable result = instance.getTable();
            assertEquals(expResult, result);
        }
        
        expResult = DbTable.APPOINTMENT;
        source = new DbColumn[] {
            DbColumn.APPOINTMENT_CUSTOMER, DbColumn.APPOINTMENT_USER, DbColumn.TITLE, DbColumn.DESCRIPTION, DbColumn.LOCATION,
                DbColumn.CONTACT, DbColumn.TYPE, DbColumn.URL, DbColumn.START, DbColumn.END, DbColumn.APPOINTMENT_ID,
                DbColumn.APPOINTMENT_CREATE_DATE, DbColumn.APPOINTMENT_CREATED_BY, DbColumn.APPOINTMENT_LAST_UPDATE,
                DbColumn.APPOINTMENT_LAST_UPDATE_BY
        };
        for (DbColumn instance : source) {
            DbTable result = instance.getTable();
            assertEquals(expResult, result);
        }
        
        expResult = DbTable.CITY;
        source = new DbColumn[] {
            DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.CITY_ID, DbColumn.CITY_CREATE_DATE, DbColumn.CITY_CREATED_BY,
                DbColumn.CITY_LAST_UPDATE, DbColumn.CITY_LAST_UPDATE_BY
        };
        for (DbColumn instance : source) {
            DbTable result = instance.getTable();
            assertEquals(expResult, result);
        }
        
        expResult = DbTable.COUNTRY;
        source = new DbColumn[] {
            DbColumn.COUNTRY_NAME, DbColumn.COUNTRY_ID, DbColumn.COUNTRY_CREATE_DATE, DbColumn.COUNTRY_CREATED_BY,
                DbColumn.COUNTRY_LAST_UPDATE, DbColumn.COUNTRY_LAST_UPDATE_BY
        };
        for (DbColumn instance : source) {
            DbTable result = instance.getTable();
            assertEquals(expResult, result);
        }
        
        expResult = DbTable.CUSTOMER;
        source = new DbColumn[] {
            DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ACTIVE, DbColumn.CUSTOMER_ID, DbColumn.CUSTOMER_CREATE_DATE,
                DbColumn.CUSTOMER_CREATED_BY, DbColumn.CUSTOMER_LAST_UPDATE, DbColumn.CUSTOMER_LAST_UPDATE_BY
        };
        for (DbColumn instance : source) {
            DbTable result = instance.getTable();
            assertEquals(expResult, result);
        }
        
        expResult = DbTable.USER;
        source = new DbColumn[] {
            DbColumn.USER_NAME, DbColumn.PASSWORD, DbColumn.STATUS, DbColumn.USER_ID, DbColumn.USER_CREATE_DATE,
                DbColumn.USER_CREATED_BY, DbColumn.USER_LAST_UPDATE, DbColumn.USER_LAST_UPDATE_BY
        };
        for (DbColumn instance : source) {
            DbTable result = instance.getTable();
            assertEquals(expResult, result);
        }
    }

}
