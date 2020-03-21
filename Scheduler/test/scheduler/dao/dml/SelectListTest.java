/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.dml;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;

/**
 *
 * @author lerwi
 */
public class SelectListTest {
    
    public SelectListTest() {
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
     * Test of getName method, of class SelectList.
     */
    @Test
    public void testGetName() {
        System.out.println("getName");
        SelectList instance = new SelectList(DbTable.COUNTRY);
        String expResult = DbTable.COUNTRY.getAlias();
        String result = instance.getName();
        assertEquals(expResult, result);
        instance = new SelectList(DbTable.COUNTRY, (String) null);
        result = instance.getName();
        assertEquals(expResult, result);
        instance = new SelectList(DbTable.COUNTRY, DbTable.COUNTRY.getAlias());
        result = instance.getName();
        assertEquals(expResult, result);
        expResult = DbTable.COUNTRY.getDbName();
        instance = new SelectList(DbTable.COUNTRY, "");
        result = instance.getName();
        assertEquals(expResult, result);
        instance = new SelectList(DbTable.COUNTRY, DbTable.COUNTRY.getDbName());
        result = instance.getName();
        assertEquals(expResult, result);
        expResult = "xyz";
        instance = new SelectList(DbTable.COUNTRY, "xyz");
        result = instance.getName();
        assertEquals(expResult, result);
    }

//    /**
//     * Test of setName method, of class SelectList.
//     */
//    @Test
//    public void testSetName() {
//        System.out.println("setName");
//        String name = "";
//        SelectList instance = null;
//        instance.setName(name);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of getJoinedTables method, of class SelectList.
     */
    @Test
    public void testGetJoinedTables() {
        System.out.println("getJoinedTables");
        SelectList instance = new SelectList(DbTable.ADDRESS);
        SelectList.Joined expResult = instance.leftJoin(DbColumn.ADDRESS_CITY, DbColumn.CITY_ID);
        List<SelectList.Joined> result = instance.getJoinedTables();
        assertEquals(1, result.size());
        assertEquals(expResult, result.get(0));
        expResult.leftJoin(DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID);
    }

    /**
     * Test of getTableName method, of class SelectList.
     */
    @Test
    public void testGetTableName() {
        System.out.println("getTableName");
        SelectList instance = new SelectList(DbTable.COUNTRY);
        DbTable expResult = DbTable.COUNTRY;
        DbTable result = instance.getTableName();
        assertEquals(expResult, result);
        instance = new SelectList(DbTable.ADDRESS, DbTable.COUNTRY.name());
        expResult = DbTable.ADDRESS;
        result = instance.getTableName();
        assertEquals(expResult, result);
        instance.leftJoin(DbColumn.ADDRESS_CITY, DbColumn.CITY_ID);
        result = instance.getTableName();
        assertEquals(expResult, result);
        assertTrue(instance.getTableName() == expResult);
    }

    /**
     * Test of getSelectQuery method, of class SelectList.
     */
    @Test
    public void testGetSelectQuery() {
        System.out.println("getSelectQuery");
        SelectList instance = new SelectList(DbTable.APPOINTMENT);
        String expResult = "SELECT `customerId`, `userId`, `title`, `description`, `location`, `contact`, `type`, `url`, `start`, `end`,"
                + " `appointmentId`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy` FROM `appointment`";
        StringBuilder result = instance.getSelectQuery();
        assertEquals(expResult, result.toString());
        instance = new SelectList(DbTable.CUSTOMER);
        expResult = "SELECT `customerName`, `addressId`, `active`, `customerId`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`"
                + " FROM `customer`";
        result = instance.getSelectQuery();
        assertEquals(expResult, result.toString());
    }

//    /**
//     * Test of getAllColumns method, of class SelectList.
//     */
//    @Test
//    public void testGetAllColumns() {
//        System.out.println("getAllColumns");
//        SelectList instance = new SelectList(TableName.ADDRESS);
//        DbColumn[] expResult = new DbColumn[]{
//            DbColumn.ADDRESS1,
//            DbColumn.ADDRESS2,
//            DbColumn.ADDRESS_CITY,
//            DbColumn.POSTAL_CODE,
//            DbColumn.PHONE,
//            DbColumn.ADDRESS_ID,
//            DbColumn.ADDRESS_CREATE_DATE,
//            DbColumn.ADDRESS_CREATED_BY,
//            DbColumn.ADDRESS_LAST_UPDATE,
//            DbColumn.ADDRESS_LAST_UPDATE_BY
//        };
//        List<SelectList.SelectColumn> result = instance.getAllColumns(builder);
//        assertEquals(expResult.length, result.size());
//        for (int i = 0; i < expResult.length; i++) {
//            assertEquals(expResult[i], result.get(i).getColumn());
//        }
//        DmlTableSet.JoinedTable joinedTable = instance.leftJoin(DbColumn.ADDRESS_CITY, DbColumn.CITY_ID);
//        List<DmlTableSet.DmlColumn> joinedResult = joinedTable.getDmlColumns();
//        assertEquals(result, instance.getDmlColumns());
//        assertEquals(expResult.length, result.size());
//        for (int i = 0; i < expResult.length; i++) {
//            assertEquals(expResult[i], result.get(i).getColumn());
//        }
//
//        DbColumn[] expJoinResult = new DbColumn[]{
//            DbColumn.CITY_NAME,
//            DbColumn.CITY_COUNTRY
//        };
//        assertEquals(expJoinResult.length, joinedResult.size());
//        for (int i = 0; i < expJoinResult.length; i++) {
//            assertEquals(expJoinResult[i], joinedResult.get(i).getColumn());
//        }
//
//        List<DmlTableSet.DmlColumn> finalResult = joinedTable.leftJoin(DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID).getDmlColumns();
//        assertEquals(result, instance.getDmlColumns());
//        assertEquals(expResult.length, result.size());
//        for (int i = 0; i < expResult.length; i++) {
//            assertEquals(expResult[i], result.get(i).getColumn());
//        }
//
//        assertEquals(joinedResult, joinedTable.getDmlColumns());
//        assertEquals(expJoinResult.length, joinedResult.size());
//        for (int i = 0; i < expJoinResult.length; i++) {
//            assertEquals(expJoinResult[i], joinedResult.get(i).getColumn());
//        }
//
//        assertEquals(1, finalResult.size());
//        assertEquals(DbColumn.COUNTRY_NAME, finalResult.get(0).getColumn());
//    }

//    /**
//     * Test of size method, of class SelectList.
//     */
//    @Test
//    public void testSize() {
//        System.out.println("size");
//        SelectList instance = null;
//        int expResult = 0;
//        int result = instance.size();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of isEmpty method, of class SelectList.
//     */
//    @Test
//    public void testIsEmpty() {
//        System.out.println("isEmpty");
//        SelectList instance = null;
//        boolean expResult = false;
//        boolean result = instance.isEmpty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of contains method, of class SelectList.
//     */
//    @Test
//    public void testContains() {
//        System.out.println("contains");
//        Object o = null;
//        SelectList instance = null;
//        boolean expResult = false;
//        boolean result = instance.contains(o);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of iterator method, of class SelectList.
//     */
//    @Test
//    public void testIterator() {
//        System.out.println("iterator");
//        SelectList instance = null;
//        Iterator<SelectList.SelectColumn> expResult = null;
//        Iterator<SelectList.SelectColumn> result = instance.iterator();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of toArray method, of class SelectList.
//     */
//    @Test
//    public void testToArray() {
//        System.out.println("toArray");
//        SelectList instance = null;
//        Object[] expResult = null;
//        Object[] result = instance.toArray();
//        assertArrayEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of containsAll method, of class SelectList.
//     */
//    @Test
//    public void testContainsAll() {
//        System.out.println("containsAll");
//        Collection c = null;
//        SelectList instance = null;
//        boolean expResult = false;
//        boolean result = instance.containsAll(c);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of get method, of class SelectList.
//     */
//    @Test
//    public void testGet() {
//        System.out.println("get");
//        int index = 0;
//        SelectList instance = null;
//        SelectList.SelectColumn expResult = null;
//        SelectList.SelectColumn result = instance.get(index);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of indexOf method, of class SelectList.
//     */
//    @Test
//    public void testIndexOf() {
//        System.out.println("indexOf");
//        Object o = null;
//        SelectList instance = null;
//        int expResult = 0;
//        int result = instance.indexOf(o);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of lastIndexOf method, of class SelectList.
//     */
//    @Test
//    public void testLastIndexOf() {
//        System.out.println("lastIndexOf");
//        Object o = null;
//        SelectList instance = null;
//        int expResult = 0;
//        int result = instance.lastIndexOf(o);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of listIterator method, of class SelectList.
//     */
//    @Test
//    public void testListIterator_0args() {
//        System.out.println("listIterator");
//        SelectList instance = null;
//        ListIterator<SelectList.SelectColumn> expResult = null;
//        ListIterator<SelectList.SelectColumn> result = instance.listIterator();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of listIterator method, of class SelectList.
//     */
//    @Test
//    public void testListIterator_int() {
//        System.out.println("listIterator");
//        int index = 0;
//        SelectList instance = null;
//        ListIterator<SelectList.SelectColumn> expResult = null;
//        ListIterator<SelectList.SelectColumn> result = instance.listIterator(index);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of subList method, of class SelectList.
//     */
//    @Test
//    public void testSubList() {
//        System.out.println("subList");
//        int fromIndex = 0;
//        int toIndex = 0;
//        SelectList instance = null;
//        List<SelectList.SelectColumn> expResult = null;
//        List<SelectList.SelectColumn> result = instance.subList(fromIndex, toIndex);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    /**
     * Test of innerJoin method, of class SelectList.
     */
    @Test
    public void testInnerJoin() {
        System.out.println("innerJoin");
        SelectList instance = new SelectList(DbTable.ADDRESS);
        SelectList.Joined joinedTable = instance.innerJoin(DbColumn.ADDRESS_CITY, DbColumn.CITY_ID);
        StringBuilder result = instance.getSelectQuery();
        String expResult = "SELECT l.`address` AS address, l.`address2` AS address2, l.`cityId` AS cityId, l.`postalCode` AS postalCode,"
                + " l.`phone` AS phone, l.`addressId` AS addressId, l.`createDate` AS createDate, l.`createdBy` AS createdBy,"
                + " l.`lastUpdate` AS lastUpdate, l.`lastUpdateBy` AS lastUpdateBy,"
                + " c.`city` AS city, c.`countryId` AS countryId"
                + " FROM `address` l JOIN `city` c ON l.`cityId`=c.`cityId`";
        assertEquals(expResult, result.toString());
        joinedTable.innerJoin(DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID);
        result = instance.getSelectQuery();
        expResult = "SELECT l.`address` AS address, l.`address2` AS address2, l.`cityId` AS cityId, l.`postalCode` AS postalCode, l.`phone` AS phone,"
                + " l.`addressId` AS addressId, l.`createDate` AS createDate, l.`createdBy` AS createdBy, l.`lastUpdate` AS lastUpdate,"
                + " l.`lastUpdateBy` AS lastUpdateBy,"
                + " c.`city` AS city, c.`countryId` AS countryId, n.`country` AS country"
                + " FROM `address` l"
                + " JOIN `city` c ON l.`cityId`=c.`cityId`"
                + " JOIN `country` n ON c.`countryId`=n.`countryId`";
        assertEquals(expResult, result.toString());
    }

    /**
     * Test of leftJoin method, of class SelectList.
     */
    @Test
    public void testLeftJoin() {
        System.out.println("leftJoin");
        SelectList instance = new SelectList(DbTable.CUSTOMER);
        instance.leftJoin(DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS_ID)
                .leftJoin(DbColumn.ADDRESS_CITY, DbColumn.CITY_ID)
                .leftJoin(DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID);
        String expResult = "SELECT p.`customerName` AS customerName, p.`addressId` AS addressId, p.`active` AS active, p.`customerId` AS customerId,"
                + " p.`createDate` AS createDate, p.`createdBy` AS createdBy, p.`lastUpdate` AS lastUpdate, p.`lastUpdateBy` AS lastUpdateBy,"
                + " l.`address` AS address, l.`address2` AS address2, l.`cityId` AS cityId, l.`postalCode` AS postalCode, l.`phone` AS phone,"
                + " c.`city` AS city, c.`countryId` AS countryId, n.`country` AS country"
                + " FROM `customer` p"
                + " LEFT JOIN `address` l ON p.`addressId`=l.`addressId`"
                + " LEFT JOIN `city` c ON l.`cityId`=c.`cityId`"
                + " LEFT JOIN `country` n ON c.`countryId`=n.`countryId`";
        StringBuilder result = instance.getSelectQuery();
        assertEquals(expResult, result.toString());
    }

    /**
     * Test of rightJoin method, of class SelectList.
     */
    @Test
    public void testRightJoin() {
        System.out.println("rightJoin");
        SelectList instance = new SelectList(DbTable.APPOINTMENT);
        instance.rightJoin(DbColumn.APPOINTMENT_CUSTOMER, DbColumn.CUSTOMER_ID)
                .leftJoin(DbColumn.CUSTOMER_ADDRESS, DbColumn.ADDRESS_ID)
                .innerJoin(DbColumn.ADDRESS_CITY, DbColumn.CITY_ID)
                .rightJoin(DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID);
        instance.fullJoin(DbColumn.APPOINTMENT_USER, DbColumn.USER_ID);
        String expResult = "SELECT a.`customerId` AS customerId, a.`userId` AS userId, a.`title` AS title, a.`description` AS description,"
                + " a.`location` AS location, a.`contact` AS contact, a.`type` AS type, a.`url` AS url, a.`start` AS start, a.`end` AS end,"
                + " a.`appointmentId` AS appointmentId, a.`createDate` AS createDate, a.`createdBy` AS createdBy, a.`lastUpdate` AS lastUpdate,"
                + " a.`lastUpdateBy` AS lastUpdateBy,"
                + " p.`customerName` AS customerName, p.`addressId` AS addressId, p.`active` AS active,"
                + " l.`address` AS address, l.`address2` AS address2, l.`cityId` AS cityId, l.`postalCode` AS postalCode, l.`phone` AS phone,"
                + " c.`city` AS city, c.`countryId` AS countryId, n.`country` AS country,"
                + " u.`userName` AS userName, u.`password` AS password, u.`active` AS status"
                + " FROM `appointment` a"
                + " RIGHT JOIN `customer` p ON a.`customerId`=p.`customerId`"
                + " LEFT JOIN `address` l ON p.`addressId`=l.`addressId`"
                + " JOIN `city` c ON l.`cityId`=c.`cityId`"
                + " RIGHT JOIN `country` n ON c.`countryId`=n.`countryId`"
                + " FULL JOIN `user` u ON a.`userId`=u.`userId`";
        StringBuilder result = instance.getSelectQuery();
        assertEquals(expResult, result.toString());
    }

    /**
     * Test of fullJoin method, of class SelectList.
     */
    @Test
    public void testFullJoin() {
        System.out.println("fullJoin");
        DbColumn leftColumn = DbColumn.APPOINTMENT_LAST_UPDATE_BY;
        DbColumn rightColumn = DbColumn.USER_NAME;
        Predicate<DbColumn> columnSelector = (t) -> t == DbColumn.STATUS || t == DbColumn.USER_NAME|| t == DbColumn.USER_ID;
        Function<DbColumn, String> aliasMapper = (t) -> (t == DbColumn.USER_ID) ? "id" : null;
        SelectList instance = new SelectList(DbTable.APPOINTMENT);
        instance.fullJoin(leftColumn, rightColumn, columnSelector, aliasMapper);
        String expResult = "SELECT a.`customerId` AS customerId, a.`userId` AS userId, a.`title` AS title,"
                + " a.`description` AS description, a.`location` AS location, a.`contact` AS contact, a.`type` AS type, a.`url` AS url,"
                + " a.`start` AS start, a.`end` AS end, a.`appointmentId` AS appointmentId, a.`createDate` AS createDate, a.`createdBy` AS createdBy,"
                + " a.`lastUpdate` AS lastUpdate, a.`lastUpdateBy` AS lastUpdateBy, u.`userName` AS userName, u.`active` AS status, u.`userId` AS id"
                + " FROM `appointment` a"
                + " FULL JOIN `user` u ON a.`lastUpdateBy`=u.`userName`";
        StringBuilder result = instance.getSelectQuery();
        assertEquals(expResult, result.toString());
    }
    
}
