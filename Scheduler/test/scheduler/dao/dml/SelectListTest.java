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
 * @author Leonard T. Erwine (Student ID 356334)
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
        expResult = DbTable.COUNTRY.getDbName().getValue();
        instance = new SelectList(DbTable.COUNTRY, "");
        result = instance.getName();
        assertEquals(expResult, result);
        instance = new SelectList(DbTable.COUNTRY, DbTable.COUNTRY.getDbName().getValue());
        result = instance.getName();
        assertEquals(expResult, result);
        expResult = "xyz";
        instance = new SelectList(DbTable.COUNTRY, "xyz");
        result = instance.getName();
        assertEquals(expResult, result);
    }

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
