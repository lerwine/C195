/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import scheduler.dao.schema.DbTable;
import scheduler.dao.schema.DbColumn;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author lerwi
 */
public class DmlTableSetTest {

    public DmlTableSetTest() {
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
     * Test of getTableName method, of class DmlTableSet.
     */
    @Test
    public void testGetTableName() {
        System.out.println("getTableName");
        DmlTableSet instance = new DmlTableSet(DbTable.COUNTRY);
        DbTable expResult = DbTable.COUNTRY;
        DbTable result = instance.getTableName();
        assertEquals(expResult, result);
        instance = new DmlTableSet(DbTable.ADDRESS, DbTable.COUNTRY.name());
        expResult = DbTable.ADDRESS;
        result = instance.getTableName();
        assertEquals(expResult, result);
        instance.leftJoin(DbColumn.ADDRESS_CITY, DbColumn.CITY_ID);
        result = instance.getTableName();
        assertEquals(expResult, result);
        assertTrue(instance.getTableName() == expResult);
    }

    /**
     * Test of getTableJoins method, of class DmlTableSet.
     */
    @Test
    public void testGetTableJoins() {
        System.out.println("getTableJoins");
        DmlTableSet instance = new DmlTableSet(DbTable.ADDRESS);
        DmlTableSet.JoinedTable expResult = instance.leftJoin(DbColumn.ADDRESS_CITY, DbColumn.CITY_ID);
        List<DmlTableSet.JoinedTable> result = instance.getTableJoins();
        assertEquals(1, result.size());
        assertEquals(expResult, result.get(0));
        expResult.leftJoin(DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID);
    }

    /**
     * Test of getDmlColumns method, of class DmlTableSet.
     */
    @Test
    public void testGetDmlColumns() {
        System.out.println("getDmlColumns");
        DmlTableSet instance = new DmlTableSet(DbTable.ADDRESS);
        DbColumn[] expResult = new DbColumn[]{
            DbColumn.ADDRESS1,
            DbColumn.ADDRESS2,
            DbColumn.ADDRESS_CITY,
            DbColumn.POSTAL_CODE,
            DbColumn.PHONE,
            DbColumn.ADDRESS_ID,
            DbColumn.ADDRESS_CREATE_DATE,
            DbColumn.ADDRESS_CREATED_BY,
            DbColumn.ADDRESS_LAST_UPDATE,
            DbColumn.ADDRESS_LAST_UPDATE_BY
        };
        List<DmlTableSet.DmlColumn> result = instance.getDmlColumns();
        assertEquals(expResult.length, result.size());
        for (int i = 0; i < expResult.length; i++) {
            assertEquals(expResult[i], result.get(i).getColumn());
        }
        DmlTableSet.JoinedTable joinedTable = instance.leftJoin(DbColumn.ADDRESS_CITY, DbColumn.CITY_ID);
        List<DmlTableSet.DmlColumn> joinedResult = joinedTable.getDmlColumns();
        assertEquals(result, instance.getDmlColumns());
        assertEquals(expResult.length, result.size());
        for (int i = 0; i < expResult.length; i++) {
            assertEquals(expResult[i], result.get(i).getColumn());
        }

        DbColumn[] expJoinResult = new DbColumn[]{
            DbColumn.CITY_NAME,
            DbColumn.CITY_COUNTRY
        };
        assertEquals(expJoinResult.length, joinedResult.size());
        for (int i = 0; i < expJoinResult.length; i++) {
            assertEquals(expJoinResult[i], joinedResult.get(i).getColumn());
        }

        List<DmlTableSet.DmlColumn> finalResult = joinedTable.leftJoin(DbColumn.CITY_COUNTRY, DbColumn.COUNTRY_ID).getDmlColumns();
        assertEquals(result, instance.getDmlColumns());
        assertEquals(expResult.length, result.size());
        for (int i = 0; i < expResult.length; i++) {
            assertEquals(expResult[i], result.get(i).getColumn());
        }

        assertEquals(joinedResult, joinedTable.getDmlColumns());
        assertEquals(expJoinResult.length, joinedResult.size());
        for (int i = 0; i < expJoinResult.length; i++) {
            assertEquals(expJoinResult[i], joinedResult.get(i).getColumn());
        }

        assertEquals(1, finalResult.size());
        assertEquals(DbColumn.COUNTRY_NAME, finalResult.get(0).getColumn());
    }

    /**
     * Test of getTableAlias method, of class DmlTableSet.
     */
    @Test
    public void testGetTableAlias() {
        System.out.println("getTableAlias");
        DmlTableSet instance = new DmlTableSet(DbTable.COUNTRY);
        String expResult = DbTable.COUNTRY.getAlias();
        String result = instance.getTableAlias();
        assertEquals(expResult, result);
        instance = new DmlTableSet(DbTable.COUNTRY, (String) null);
        result = instance.getTableAlias();
        assertEquals(expResult, result);
        instance = new DmlTableSet(DbTable.COUNTRY, DbTable.COUNTRY.getAlias());
        result = instance.getTableAlias();
        assertEquals(expResult, result);
        expResult = DbTable.COUNTRY.getDbName();
        instance = new DmlTableSet(DbTable.COUNTRY, "");
        result = instance.getTableAlias();
        assertEquals(expResult, result);
        instance = new DmlTableSet(DbTable.COUNTRY, DbTable.COUNTRY.getDbName());
        result = instance.getTableAlias();
        assertEquals(expResult, result);
        expResult = "xyz";
        instance = new DmlTableSet(DbTable.COUNTRY, "xyz");
        result = instance.getTableAlias();
        assertEquals(expResult, result);
    }

    /**
     * Test of getSelectQuery method, of class DmlTableSet.
     */
    @Test
    public void testGetSelectQuery() {
        System.out.println("getSelectQuery");
        DmlTableSet instance = new DmlTableSet(DbTable.APPOINTMENT);
        String expResult = "SELECT `customerId`, `userId`, `title`, `description`, `location`, `contact`, `type`, `url`, `start`, `end`,"
                + " `appointmentId`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy` FROM `appointment`";
        StringBuilder result = instance.getSelectQuery();
        assertEquals(expResult, result.toString());
        instance = new DmlTableSet(DbTable.CUSTOMER);
        expResult = "SELECT `customerName`, `addressId`, `active`, `customerId`, `createDate`, `createdBy`, `lastUpdate`, `lastUpdateBy`"
                + " FROM `customer`";
        result = instance.getSelectQuery();
        assertEquals(expResult, result.toString());
    }

    /**
     * Test of innerJoin method, of class DmlTableSet.
     */
    @Test
    public void testInnerJoin() {
        System.out.println("innerJoin");
        DmlTableSet instance = new DmlTableSet(DbTable.ADDRESS);
        DmlTableSet.JoinedTable joinedTable = instance.innerJoin(DbColumn.ADDRESS_CITY, DbColumn.CITY_ID);
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
     * Test of leftJoin method, of class DmlTableSet.
     */
    @Test
    public void testLeftJoin() {
        System.out.println("leftJoin");
        DmlTableSet instance = new DmlTableSet(DbTable.CUSTOMER);
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
     * Test of rightJoin method, of class DmlTableSet.
     */
    @Test
    public void testRightJoin() {
        System.out.println("rightJoin");
        DmlTableSet instance = new DmlTableSet(DbTable.APPOINTMENT);
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
     * Test of fullJoin method, of class DmlTableSet.
     */
    @Test
    public void testFullJoin() {
        System.out.println("fullJoin");
        DbColumn leftColumn = DbColumn.APPOINTMENT_LAST_UPDATE_BY;
        DbColumn rightColumn = DbColumn.USER_NAME;
        Predicate<DbColumn> columnSelector = (t) -> t == DbColumn.STATUS || t == DbColumn.USER_NAME|| t == DbColumn.USER_ID;
        Function<DbColumn, String> aliasMapper = (t) -> (t == DbColumn.USER_ID) ? "id" : null;
        DmlTableSet instance = new DmlTableSet(DbTable.APPOINTMENT);
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
