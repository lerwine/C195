/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.dml;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.SchemaHelper;

/**
 *
 * @author lerwi
 */
public class DmlTableTest {

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

    public DmlTable.BuilderJoin assertJoin(DmlTable.Builder leftBuilder, DbColumn leftColumn, DbColumn rightColumn,
            Map<String, DmlTable.Builder> allTables, Map<String, DmlTable.BuilderColumn> allColumns) {
        int index = leftBuilder.getJoinedTables().size();
        int count = leftBuilder.getColumns().size();
        DmlTable.Builder rightBuilder = leftBuilder.leftJoin(leftColumn, rightColumn, (DbColumn t) -> SchemaHelper.isEntityDataOrRef(t));
        assertEquals(allTables, leftBuilder.getAllTables());
        assertEquals(allColumns, leftBuilder.getAllColumns());
        assertEquals(count, leftBuilder.getColumns().size());
        assertNotNull(rightBuilder);
        assertEquals(allTables, rightBuilder.getAllTables());
        assertEquals(allColumns, rightBuilder.getAllColumns());
        assertEquals(rightColumn.getTable(), rightBuilder.getTable());
        assertEquals(rightColumn.getTable().toString(), rightBuilder.getAlias());
        assertNotNull(rightBuilder.getJoinedTables());
        assertTrue(rightBuilder.getJoinedTables().isEmpty());
        assertEquals(index + 1, leftBuilder.getJoinedTables().size());
        DmlTable.BuilderJoin join = leftBuilder.getJoinedTables().get(index);
        assertNotNull(join);
        assertEquals(leftBuilder, join.getParentTable());
        assertEquals(leftColumn, join.getParentColumn());
        assertEquals(TableJoinType.LEFT, join.getType());
        assertEquals(rightBuilder, join.getChildTable());
        assertEquals(rightColumn, join.getChildColumn());
        assertEquals(leftBuilder, rightBuilder.getParent());
        assertTrue(allTables.containsKey(leftBuilder.getAlias()));
        assertEquals(leftBuilder, allTables.get(leftBuilder.getAlias()));
        assertTrue(allTables.containsKey(rightBuilder.getAlias()));
        assertEquals(rightBuilder, allTables.get(rightBuilder.getAlias()));
        Optional<DmlTable.BuilderColumn> col = leftBuilder.getColumns().stream().filter((t) -> t.getColumn() == leftColumn).findFirst();
        Optional<DmlTable.BuilderJoin> oj;
        if (col.isPresent()) {
            oj = col.get().getChildJoins().stream().filter((t) -> t.getChildTable() == rightBuilder && t.getChildColumn() == rightColumn).findFirst();
            assertTrue(oj.isPresent());
            assertEquals(oj.get(), join);
        }
        col = rightBuilder.getColumns().stream().filter((t) -> t.getColumn() == rightColumn).findFirst();
        if (col.isPresent()) {
            assertEquals(col.get().getParentJoin(), join);
        }
        return join;
    }
    
    public void assertBuilderColumns(Map<String, DmlTable.BuilderColumn> allColumns, DbColumn ...expectedColumns) {
        for (DbColumn column : expectedColumns) {
            assertTrue(allColumns.containsKey(column.toString()));
            DmlTable.BuilderColumn bc = allColumns.get(column.toString());
            assertNotNull(bc);
            assertEquals(column, bc.getColumn());
            assertEquals(column.toString(), bc.getAlias());
        }
    }
    
    public void assertBuilderColumns(DmlTable.Builder builder, Map<String, DmlTable.BuilderColumn> allColumns, DbColumn ...expectedColumns) {
        List<DmlTable.BuilderColumn> coll = builder.getColumns();
        for (DbColumn column : expectedColumns) {
            Optional<DmlTable.BuilderColumn> opt = coll.stream().filter((t) -> t.getColumn() == column).findFirst();
            assertTrue(opt.isPresent());
            DmlTable.BuilderColumn bc = opt.get();
            assertNotNull(bc);
            assertEquals(column, bc.getColumn());
            assertEquals(column.toString(), bc.getAlias());
            assertEquals(builder, bc.getOwner());
            assertTrue(allColumns.containsKey(column.toString()));
            assertEquals(allColumns.get(column.toString()), bc);
        }
    }
    
    public void assertDmlColumns(Map<String, DmlColumn> allColumns, DbColumn ...expectedColumns) {
        for (DbColumn column : expectedColumns) {
            assertTrue(allColumns.containsKey(column.toString()));
            DmlColumn dc = allColumns.get(column.toString());
            assertNotNull(dc);
            assertEquals(column, dc.getDbColumn());
            assertEquals(column.toString(), dc.getName());
        }
    }
    
    public void assertDmlColumns(DmlTable table, Map<String, DmlColumn> allColumns, DbColumn ...expectedColumns) {
        assertEquals(allColumns, table.getAllColumns());
        List<DmlColumn> coll = table.getColumns();
        for (DbColumn column : expectedColumns) {
            Optional<DmlColumn> opt = coll.stream().filter((t) -> t.getDbColumn() == column).findFirst();
            assertTrue(opt.isPresent());
            DmlColumn dc = opt.get();
            assertNotNull(dc);
            assertEquals(column, dc.getDbColumn());
            assertEquals(column.toString(), dc.getName());
            assertEquals(table, dc.getOwner());
            assertTrue(allColumns.containsKey(column.toString()));
            assertEquals(allColumns.get(column.toString()), dc);
        }
    }
    
    public void assertBuilderTables(Map<String, DmlTable.Builder> allTables, DmlTable.Builder ...expectedTables) {
        for (DmlTable.Builder builder : expectedTables) {
            assertTrue(allTables.containsKey(builder.getAlias()));
            assertEquals(builder, allTables.get(builder.getAlias()));
        }
    }
    
    public void assertDmlTables(Map<String, DmlTable> allTables, DbTable ...expectedTables) {
        for (DbTable table : expectedTables) {
            assertTrue(allTables.containsKey(table.toString()));
            DmlTable dml = allTables.get(table.toString());
            assertNotNull(dml);
            assertEquals(table, dml.getTable());
            assertEquals(allTables, dml.getAllTables());
            assertEquals(table.toString(), dml.getName());
        }
    }
    
//    /**
//     * Test of builder method, of class DmlTable with argument DbTable.APPOINTMENT.
//     */
//    @Test
//    public void buildAppointmentTableTest() {
//        final DmlTable.Builder appointmentBuilder = DmlTable.builder(DbTable.APPOINTMENT);
//        assertNotNull(appointmentBuilder);
//        Map<String, DmlTable.Builder> allTables = appointmentBuilder.getAllTables();
//        assertNotNull(allTables);
//        Map<String, DmlTable.BuilderColumn> allColumns = appointmentBuilder.getAllColumns();
//        assertNotNull(allColumns);
//        DmlTable.BuilderJoin appointmentCustomer = assertJoin(appointmentBuilder, DbColumn.APPOINTMENT_CUSTOMER, DbColumn.CUSTOMER_ID, allTables, allColumns);
//        DmlTable.BuilderJoin customerAddress = assertJoin(appointmentCustomer.getChildTable(), DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS_ID, allTables, allColumns);
//        DmlTable.BuilderJoin addressCity = assertJoin(customerAddress.getChildTable(), DbColumn.ADDRESS_CITY, DbColumn.CITY_ID, allTables, allColumns);
//        DmlTable.BuilderJoin cityCountry = assertJoin(addressCity.getChildTable(), DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID, allTables, allColumns);
//        DmlTable.BuilderJoin appointmentUser = assertJoin(appointmentBuilder, DbColumn.APPOINTMENT_USER, DbColumn.USER_ID, allTables, allColumns);
//        assertEquals(6, allTables.size());
//        assertEquals(28, allColumns.size());
//        assertEquals(DbTable.APPOINTMENT, appointmentBuilder.getTable());
//        assertEquals(DbTable.APPOINTMENT.toString(), appointmentBuilder.getAlias());
//        assertNull(appointmentBuilder.getParent());
//        assertBuilderTables(allTables, appointmentBuilder, appointmentCustomer.getChildTable(), customerAddress.getChildTable(),
//                addressCity.getChildTable(), cityCountry.getChildTable(), appointmentUser.getChildTable());
//        assertBuilderColumns(allColumns, DbColumn.APPOINTMENT_CUSTOMER, DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS1,
//                DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY, DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_NAME, DbColumn.POSTAL_CODE,
//                DbColumn.PHONE, DbColumn.ACTIVE, DbColumn.APPOINTMENT_USER, DbColumn.USER_NAME, DbColumn.STATUS, DbColumn.TITLE, DbColumn.DESCRIPTION,
//                DbColumn.LOCATION, DbColumn.CONTACT, DbColumn.TYPE, DbColumn.URL, DbColumn.START, DbColumn.END, DbColumn.APPOINTMENT_ID,
//                DbColumn.APPOINTMENT_CREATE_DATE, DbColumn.APPOINTMENT_CREATED_BY, DbColumn.APPOINTMENT_LAST_UPDATE,
//                DbColumn.APPOINTMENT_LAST_UPDATE_BY);
//        assertBuilderColumns(appointmentBuilder, allColumns, DbColumn.APPOINTMENT_CUSTOMER, DbColumn.APPOINTMENT_USER, DbColumn.TITLE,
//                DbColumn.DESCRIPTION, DbColumn.LOCATION, DbColumn.CONTACT, DbColumn.TYPE, DbColumn.URL, DbColumn.START, DbColumn.END,
//                DbColumn.APPOINTMENT_ID, DbColumn.APPOINTMENT_CREATE_DATE, DbColumn.APPOINTMENT_CREATED_BY, DbColumn.APPOINTMENT_LAST_UPDATE,
//                DbColumn.APPOINTMENT_LAST_UPDATE_BY);
//        assertJoinedCustomerBuilder(appointmentCustomer, customerAddress, addressCity, cityCountry, allColumns);
//        assertJoinedUserBuilder(appointmentCustomer, allColumns);
//        DmlTable appointmentTable = appointmentBuilder.build();
//        assertDmlColumns(appointmentTable.getAllColumns(), DbColumn.APPOINTMENT_CUSTOMER, DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS,
//                DbColumn.ADDRESS1, DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY, DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_NAME,
//                DbColumn.POSTAL_CODE, DbColumn.PHONE, DbColumn.ACTIVE, DbColumn.APPOINTMENT_USER, DbColumn.USER_NAME, DbColumn.STATUS, DbColumn.TITLE,
//                DbColumn.DESCRIPTION, DbColumn.LOCATION, DbColumn.CONTACT, DbColumn.TYPE, DbColumn.URL, DbColumn.START, DbColumn.END,
//                DbColumn.APPOINTMENT_ID, DbColumn.APPOINTMENT_CREATE_DATE, DbColumn.APPOINTMENT_CREATED_BY, DbColumn.APPOINTMENT_LAST_UPDATE,
//                DbColumn.APPOINTMENT_LAST_UPDATE_BY);
//        assertDmlColumns(appointmentTable, appointmentTable.getAllColumns(), DbColumn.APPOINTMENT_CUSTOMER, DbColumn.APPOINTMENT_USER, DbColumn.TITLE,
//                DbColumn.DESCRIPTION, DbColumn.LOCATION, DbColumn.CONTACT, DbColumn.TYPE, DbColumn.URL, DbColumn.START, DbColumn.END,
//                DbColumn.APPOINTMENT_ID, DbColumn.APPOINTMENT_CREATE_DATE, DbColumn.APPOINTMENT_CREATED_BY, DbColumn.APPOINTMENT_LAST_UPDATE,
//                DbColumn.APPOINTMENT_LAST_UPDATE_BY);
//        List<DmlTable.Join> dmlJoinList = appointmentTable.getJoins();
//        assertNotNull(dmlJoinList);
//        assertEquals(2, dmlJoinList);
//        DmlTable.Join dmlJoin = dmlJoinList.get(0);
//        assertNotNull(dmlJoin);
//        DmlTable childTable = assertDmlJoin(dmlJoin, appointmentTable, DbColumn.APPOINTMENT_CUSTOMER, DbColumn.CUSTOMER_ID);
//        assertJoinedCustomerDml(childTable, appointmentTable.getAllTables(), appointmentTable.getAllColumns());
//        dmlJoin = dmlJoinList.get(1);
//        assertNotNull(dmlJoin);
//        childTable = assertDmlJoin(dmlJoin, appointmentTable, DbColumn.APPOINTMENT_USER, DbColumn.USER_ID);
//        assertJoinedUserDml(childTable, appointmentTable.getAllTables(), appointmentTable.getAllColumns());
//    }

    private DmlTable assertDmlJoin(DmlTable.Join join, DmlTable parentTable, DbColumn parentColumn, DbColumn childColumn) {
        assertNotNull(join);
        assertEquals(parentTable, join.getParentTable());
        assertEquals(parentColumn, join.getParentColumn());
        assertEquals(TableJoinType.LEFT, join.getType());
        assertEquals(childColumn, join.getChildColumn());
        DmlTable childTable = join.getChildTable();
        assertNotNull(childTable);
        assertEquals(childColumn.getTable(), childTable.getTable());
        assertEquals(join, childTable.getParent());
        return childTable;
    }

//    /**
//     * Test of builder method, of class DmlTable with argument DbTable.APPOINTMENT.
//     */
//    @Test
//    public void buildCustomerTableTest() {
//        final DmlTable.Builder customerBuilder = DmlTable.builder(DbTable.CUSTOMER);
//        assertNotNull(customerBuilder);
//        Map<String, DmlTable.Builder> allTables = customerBuilder.getAllTables();
//        assertNotNull(allTables);
//        Map<String, DmlTable.BuilderColumn> allColumns = customerBuilder.getAllColumns();
//        assertNotNull(allColumns);
//        DmlTable.BuilderJoin customerAddress = assertJoin(customerBuilder, DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS_ID, allTables, allColumns);
//        DmlTable.BuilderJoin addressCity = assertJoin(customerAddress.getChildTable(), DbColumn.ADDRESS_CITY, DbColumn.CITY_ID, allTables, allColumns);
//        DmlTable.BuilderJoin cityCountry = assertJoin(addressCity.getChildTable(), DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID, allTables, allColumns);
//        assertEquals(4, allTables.size());
//        assertEquals(28, allColumns.size());
//        assertEquals(DbTable.CUSTOMER, customerBuilder.getTable());
//        assertEquals(DbTable.CUSTOMER.toString(), customerBuilder.getAlias());
//        assertNull(customerBuilder.getParent());
//        assertBuilderTables(allTables, customerBuilder, customerAddress.getChildTable(), addressCity.getChildTable(), cityCountry.getChildTable());
//        assertBuilderColumns(allColumns, DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS1,
//                DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY, DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_NAME, DbColumn.POSTAL_CODE,
//                DbColumn.PHONE, DbColumn.ACTIVE, DbColumn.CUSTOMER_ID, DbColumn.CUSTOMER_CREATE_DATE, DbColumn.CUSTOMER_CREATED_BY,
//                DbColumn.CUSTOMER_LAST_UPDATE, DbColumn.CUSTOMER_LAST_UPDATE_BY);
//        assertBuilderColumns(customerBuilder, allColumns, DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ACTIVE,
//                DbColumn.CUSTOMER_ID, DbColumn.CUSTOMER_CREATE_DATE, DbColumn.CUSTOMER_CREATED_BY, DbColumn.CUSTOMER_LAST_UPDATE,
//                DbColumn.CUSTOMER_LAST_UPDATE_BY);
//        assertJoinedAddressBuilder(customerAddress, addressCity, cityCountry, allColumns);
//        DmlTable customerTable = customerBuilder.build();
//        assertDmlColumns(customerTable.getAllColumns(), DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS1,
//                DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY, DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_NAME, DbColumn.POSTAL_CODE,
//                DbColumn.PHONE, DbColumn.ACTIVE, DbColumn.CUSTOMER_ID, DbColumn.CUSTOMER_CREATE_DATE, DbColumn.CUSTOMER_CREATED_BY,
//                DbColumn.CUSTOMER_LAST_UPDATE, DbColumn.CUSTOMER_LAST_UPDATE_BY);
//        assertDmlColumns(customerTable, customerTable.getAllColumns(), DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ACTIVE,
//                DbColumn.CUSTOMER_ID, DbColumn.CUSTOMER_CREATE_DATE, DbColumn.CUSTOMER_CREATED_BY, DbColumn.CUSTOMER_LAST_UPDATE,
//                DbColumn.CUSTOMER_LAST_UPDATE_BY);
//        List<DmlTable.Join> dmlJoinList = customerTable.getJoins();
//        assertNotNull(dmlJoinList);
//        assertEquals(1, dmlJoinList);
//        DmlTable.Join dmlJoin = dmlJoinList.get(0);
//        assertNotNull(dmlJoin);
//        DmlTable childTable = assertDmlJoin(dmlJoin, customerTable, DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS_ID);
//        assertJoinedAddressDml(childTable, customerTable.getAllTables(), customerTable.getAllColumns());
//    }

    private void assertJoinedCustomerBuilder(DmlTable.BuilderJoin appointmentCustomer, DmlTable.BuilderJoin customerAddress,
            DmlTable.BuilderJoin addressCity, DmlTable.BuilderJoin cityCountry, Map<String, DmlTable.BuilderColumn> allColumns) {
        assertBuilderColumns(appointmentCustomer.getChildTable(), allColumns, DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ACTIVE);
        assertJoinedAddressBuilder(customerAddress, addressCity, cityCountry, allColumns);
    }

    private void assertJoinedCustomerDml(DmlTable table, Map<String, DmlTable> allTables, Map<String, DmlColumn> allColumns) {
        assertDmlColumns(table, allColumns, DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ACTIVE);
        List<DmlTable.Join> dmlJoinList = table.getJoins();
        assertNotNull(dmlJoinList);
        assertEquals(1, dmlJoinList);
        DmlTable.Join dmlJoin = dmlJoinList.get(0);
        assertNotNull(dmlJoin);
        DmlTable childTable = assertDmlJoin(dmlJoin, table, DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS_ID);
        assertJoinedAddressDml(childTable, allTables, allColumns);
    }

//    /**
//     * Test of builder method, of class DmlTable with argument DbTable.APPOINTMENT.
//     */
//    @Test
//    public void buildAddressTableTest() {
//        final DmlTable.Builder addressBuilder = DmlTable.builder(DbTable.ADDRESS);
//        assertNotNull(addressBuilder);
//        Map<String, DmlTable.Builder> allTables = addressBuilder.getAllTables();
//        assertNotNull(allTables);
//        Map<String, DmlTable.BuilderColumn> allColumns = addressBuilder.getAllColumns();
//        assertNotNull(allColumns);
//        DmlTable.BuilderJoin addressCity = assertJoin(addressBuilder, DbColumn.ADDRESS_CITY, DbColumn.CITY_ID, allTables, allColumns);
//        DmlTable.BuilderJoin cityCountry = assertJoin(addressCity.getChildTable(), DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID, allTables, allColumns);
//        assertEquals(4, allTables.size());
//        assertEquals(28, allColumns.size());
//        assertEquals(DbTable.ADDRESS, addressBuilder.getTable());
//        assertEquals(DbTable.ADDRESS.toString(), addressBuilder.getAlias());
//        assertNull(addressBuilder.getParent());
//        assertBuilderTables(allTables, addressBuilder, addressCity.getChildTable(), cityCountry.getChildTable());
//        assertBuilderColumns(allColumns, DbColumn.ADDRESS1, DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY, DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY,
//                DbColumn.COUNTRY_NAME, DbColumn.POSTAL_CODE, DbColumn.PHONE, DbColumn.ADDRESS_ID, DbColumn.ADDRESS_CREATE_DATE,
//                DbColumn.ADDRESS_CREATED_BY, DbColumn.ADDRESS_LAST_UPDATE, DbColumn.ADDRESS_LAST_UPDATE_BY);
//        assertBuilderColumns(addressBuilder, allColumns, DbColumn.ADDRESS1, DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY, DbColumn.POSTAL_CODE,
//                DbColumn.PHONE, DbColumn.ADDRESS_ID, DbColumn.ADDRESS_CREATE_DATE, DbColumn.ADDRESS_CREATED_BY, DbColumn.ADDRESS_LAST_UPDATE,
//                DbColumn.ADDRESS_LAST_UPDATE_BY);
//        assertJoinedCityBuilder(addressCity, cityCountry, allColumns);
//        DmlTable addressTable = addressBuilder.build();
//        assertDmlColumns(addressTable.getAllColumns(), DbColumn.ADDRESS1, DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY, DbColumn.CITY_NAME,
//                DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_NAME, DbColumn.POSTAL_CODE, DbColumn.PHONE, DbColumn.ADDRESS_ID, DbColumn.ADDRESS_CREATE_DATE,
//                DbColumn.ADDRESS_CREATED_BY, DbColumn.ADDRESS_LAST_UPDATE, DbColumn.ADDRESS_LAST_UPDATE_BY);
//        assertDmlColumns(addressTable, addressTable.getAllColumns(), DbColumn.ADDRESS1, DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY,
//                DbColumn.POSTAL_CODE, DbColumn.PHONE, DbColumn.ADDRESS_ID, DbColumn.ADDRESS_CREATE_DATE, DbColumn.ADDRESS_CREATED_BY,
//                DbColumn.ADDRESS_LAST_UPDATE, DbColumn.ADDRESS_LAST_UPDATE_BY);
//        List<DmlTable.Join> dmlJoinList = addressTable.getJoins();
//        assertNotNull(dmlJoinList);
//        assertEquals(1, dmlJoinList);
//        DmlTable.Join dmlJoin = dmlJoinList.get(0);
//        assertNotNull(dmlJoin);
//        DmlTable childTable = assertDmlJoin(dmlJoin, addressTable, DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS_ID);
//        assertJoinedCityDml(childTable, addressTable.getAllTables(), addressTable.getAllColumns());
//    }

    private void assertJoinedAddressBuilder(DmlTable.BuilderJoin customerAddress, DmlTable.BuilderJoin addressCity,
            DmlTable.BuilderJoin cityCountry, Map<String, DmlTable.BuilderColumn> allColumns) {
        assertBuilderColumns(customerAddress.getChildTable(), allColumns, DbColumn.ADDRESS1,DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY,
                DbColumn.POSTAL_CODE, DbColumn.PHONE);
        assertJoinedCityBuilder(addressCity, cityCountry, allColumns);
    }

    private void assertJoinedAddressDml(DmlTable table, Map<String, DmlTable> allTables, Map<String, DmlColumn> allColumns) {
        assertDmlColumns(table, allColumns, DbColumn.ADDRESS1, DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY, DbColumn.POSTAL_CODE, DbColumn.PHONE);
        List<DmlTable.Join> dmlJoinList = table.getJoins();
        assertNotNull(dmlJoinList);
        assertEquals(1, dmlJoinList);
        DmlTable.Join dmlJoin = dmlJoinList.get(0);
        assertNotNull(dmlJoin);
        DmlTable childTable = assertDmlJoin(dmlJoin, table, DbColumn.ADDRESS_CITY, DbColumn.CITY_ID);
        assertJoinedCityDml(childTable, allTables, allColumns);
    }

    /**
     * Test of builder method, of class DmlTable with argument DbTable.APPOINTMENT.
     */
    @Test
    public void buildCityTableTest() {
        final DmlTable.Builder cityBuilder = DmlTable.builder(DbTable.CITY);
        assertNotNull(cityBuilder);
        Map<String, DmlTable.Builder> allTables = cityBuilder.getAllTables();
        assertNotNull(allTables);
        Map<String, DmlTable.BuilderColumn> allColumns = cityBuilder.getAllColumns();
        assertNotNull(allColumns);
        DmlTable.BuilderJoin cityCountry = assertJoin(cityBuilder, DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID, allTables, allColumns);
        assertEquals(2, allTables.size());
        assertEquals(8, allColumns.size());
        assertEquals(DbTable.CITY, cityBuilder.getTable());
        assertEquals(DbTable.CITY.toString(), cityBuilder.getAlias());
        assertNull(cityBuilder.getParent());
        assertBuilderTables(allTables, cityBuilder, cityCountry.getChildTable());
        assertBuilderColumns(allColumns, DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_NAME, DbColumn.CITY_ID,
                DbColumn.CITY_CREATE_DATE, DbColumn.CITY_CREATED_BY, DbColumn.CITY_LAST_UPDATE, DbColumn.CITY_LAST_UPDATE_BY);
        assertBuilderColumns(cityBuilder, allColumns, DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.CITY_ID, DbColumn.CITY_CREATE_DATE,
                DbColumn.CITY_CREATED_BY, DbColumn.CITY_LAST_UPDATE, DbColumn.CITY_LAST_UPDATE_BY);
        assertJoinedCountryBuilder(cityCountry, allColumns);
        DmlTable cityTable = cityBuilder.build();
        assertDmlColumns(cityTable.getAllColumns(), DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_NAME, DbColumn.CITY_ID,
                DbColumn.CITY_CREATE_DATE, DbColumn.CITY_CREATED_BY, DbColumn.CITY_LAST_UPDATE, DbColumn.CITY_LAST_UPDATE_BY);
        assertDmlColumns(cityTable, cityTable.getAllColumns(), DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.CITY_ID, DbColumn.CITY_CREATE_DATE,
                DbColumn.CITY_CREATED_BY, DbColumn.CITY_LAST_UPDATE, DbColumn.CITY_LAST_UPDATE_BY);
        List<DmlTable.Join> dmlJoinList = cityTable.getJoins();
        assertNotNull(dmlJoinList);
        assertEquals(1, dmlJoinList);
        DmlTable.Join dmlJoin = dmlJoinList.get(0);
        assertNotNull(dmlJoin);
        DmlTable childTable = assertDmlJoin(dmlJoin, cityTable, DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID);
        assertJoinedCountryDml(childTable, cityTable.getAllTables(), cityTable.getAllColumns());
    }

    private void assertJoinedCityBuilder(DmlTable.BuilderJoin addressCity, DmlTable.BuilderJoin cityCountry,
            Map<String, DmlTable.BuilderColumn> allColumns) {
        assertBuilderColumns(addressCity.getChildTable(), allColumns, DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY);
        assertJoinedCountryBuilder(cityCountry, allColumns);
    }

    private void assertJoinedCityDml(DmlTable table, Map<String, DmlTable> allTables, Map<String, DmlColumn> allColumns) {
        assertDmlColumns(table, allColumns, DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY);
        List<DmlTable.Join> dmlJoinList = table.getJoins();
        assertNotNull(dmlJoinList);
        assertEquals(1, dmlJoinList);
        DmlTable.Join dmlJoin = dmlJoinList.get(0);
        assertNotNull(dmlJoin);
        DmlTable childTable = assertDmlJoin(dmlJoin, table, DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID);
        assertJoinedCountryDml(childTable, allTables, allColumns);
    }

    /**
     * Test of builder method, of class DmlTable with argument DbTable.APPOINTMENT.
     */
    @Test
    public void buildCountryTableTest() {
        final DmlTable.Builder countryBuilder = DmlTable.builder(DbTable.COUNTRY);
        assertNotNull(countryBuilder);
        Map<String, DmlTable.Builder> allTables = countryBuilder.getAllTables();
        assertNotNull(allTables);
        Map<String, DmlTable.BuilderColumn> allColumns = countryBuilder.getAllColumns();
        assertNotNull(allColumns);
        assertEquals(1, allTables.size());
        assertEquals(6, allColumns.size());
        assertEquals(DbTable.COUNTRY, countryBuilder.getTable());
        assertEquals(DbTable.COUNTRY.toString(), countryBuilder.getAlias());
        assertNull(countryBuilder.getParent());
        assertBuilderTables(allTables, countryBuilder);
        assertBuilderColumns(allColumns, DbColumn.COUNTRY_NAME, DbColumn.COUNTRY_ID,
                DbColumn.COUNTRY_CREATE_DATE, DbColumn.COUNTRY_CREATED_BY, DbColumn.COUNTRY_LAST_UPDATE, DbColumn.COUNTRY_LAST_UPDATE_BY);
        assertBuilderColumns(countryBuilder, allColumns, DbColumn.COUNTRY_NAME, DbColumn.COUNTRY_ID,
                DbColumn.COUNTRY_CREATE_DATE, DbColumn.COUNTRY_CREATED_BY, DbColumn.COUNTRY_LAST_UPDATE, DbColumn.COUNTRY_LAST_UPDATE_BY);
        DmlTable countryTable = countryBuilder.build();
        assertDmlColumns(countryTable.getAllColumns(), DbColumn.COUNTRY_NAME, DbColumn.COUNTRY_ID,
                DbColumn.COUNTRY_CREATE_DATE, DbColumn.COUNTRY_CREATED_BY, DbColumn.COUNTRY_LAST_UPDATE, DbColumn.COUNTRY_LAST_UPDATE_BY);
        assertDmlColumns(countryTable, countryTable.getAllColumns(), DbColumn.COUNTRY_NAME, DbColumn.COUNTRY_ID,
                DbColumn.COUNTRY_CREATE_DATE, DbColumn.COUNTRY_CREATED_BY, DbColumn.COUNTRY_LAST_UPDATE, DbColumn.COUNTRY_LAST_UPDATE_BY);
        List<DmlTable.Join> dmlJoinList = countryTable.getJoins();
        assertNotNull(dmlJoinList);
        assertTrue(dmlJoinList.isEmpty());
    }

    private void assertJoinedCountryBuilder(DmlTable.BuilderJoin cityCountry, Map<String, DmlTable.BuilderColumn> allColumns) {
        assertBuilderColumns(cityCountry.getChildTable(), allColumns, DbColumn.COUNTRY_NAME);
    }

    private void assertJoinedCountryDml(DmlTable table, Map<String, DmlTable> allTables, Map<String, DmlColumn> allColumns) {
        assertDmlColumns(table, allColumns, DbColumn.COUNTRY_NAME);
    }

    /**
     * Test of builder method, of class DmlTable with argument DbTable.APPOINTMENT.
     */
    @Test
    public void buildUserTableTest() {
        final DmlTable.Builder userBuilder = DmlTable.builder(DbTable.USER);
        assertNotNull(userBuilder);
        Map<String, DmlTable.Builder> allTables = userBuilder.getAllTables();
        assertNotNull(allTables);
        Map<String, DmlTable.BuilderColumn> allColumns = userBuilder.getAllColumns();
        assertNotNull(allColumns);
        assertEquals(1, allTables.size());
        assertEquals(8, allColumns.size());
        assertEquals(DbTable.USER, userBuilder.getTable());
        assertEquals(DbTable.USER.toString(), userBuilder.getAlias());
        assertNull(userBuilder.getParent());
        assertBuilderTables(allTables, userBuilder);
        assertBuilderColumns(allColumns, DbColumn.USER_NAME, DbColumn.PASSWORD, DbColumn.STATUS, DbColumn.USER_ID,
                DbColumn.USER_CREATE_DATE, DbColumn.USER_CREATED_BY, DbColumn.USER_LAST_UPDATE, DbColumn.USER_LAST_UPDATE_BY);
        assertBuilderColumns(userBuilder, allColumns, DbColumn.USER_NAME, DbColumn.PASSWORD, DbColumn.STATUS, DbColumn.USER_ID,
                DbColumn.USER_CREATE_DATE, DbColumn.USER_CREATED_BY, DbColumn.USER_LAST_UPDATE, DbColumn.USER_LAST_UPDATE_BY);
        DmlTable userTable = userBuilder.build();
        assertDmlColumns(userTable.getAllColumns(), DbColumn.USER_NAME, DbColumn.PASSWORD, DbColumn.STATUS, DbColumn.USER_ID,
                DbColumn.USER_CREATE_DATE, DbColumn.USER_CREATED_BY, DbColumn.USER_LAST_UPDATE, DbColumn.USER_LAST_UPDATE_BY);
        assertDmlColumns(userTable, userTable.getAllColumns(), DbColumn.USER_NAME, DbColumn.PASSWORD, DbColumn.STATUS, DbColumn.USER_ID,
                DbColumn.USER_CREATE_DATE, DbColumn.USER_CREATED_BY, DbColumn.USER_LAST_UPDATE, DbColumn.USER_LAST_UPDATE_BY);
        List<DmlTable.Join> dmlJoinList = userTable.getJoins();
        assertNotNull(dmlJoinList);
        assertTrue(dmlJoinList.isEmpty());
    }

    private void assertJoinedUserBuilder(DmlTable.BuilderJoin appointmentUser, Map<String, DmlTable.BuilderColumn> allColumns) {
        assertBuilderColumns(appointmentUser.getChildTable(), allColumns, DbColumn.USER_NAME, DbColumn.STATUS);
    }

    private void assertJoinedUserDml(DmlTable table, Map<String, DmlTable> allTables, Map<String, DmlColumn> allColumns) {
        assertDmlColumns(table, allColumns, DbColumn.USER_NAME, DbColumn.STATUS);
    }

}
