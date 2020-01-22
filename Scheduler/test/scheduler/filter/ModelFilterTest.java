package scheduler.filter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Comparator;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import scheduler.dao.DataObject;
import scheduler.dao.DataObjectImpl;
import scheduler.dao.PrimaryKeyColumn;
import scheduler.dao.TableName;
import scheduler.dao.DataObjectFactory;
import util.DB;
import view.ChildModel;
import view.ItemModel;

/**
 *
 * @author erwinel
 */
public class ModelFilterTest {
    
    public ModelFilterTest() {
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
     * Test of and method, of class ModelFilter.
     */
    @Test
    public void testAnd() {
        System.out.println("and");
        
        int testInt = 1;
        String testString = "My Parent";
        boolean testBoolean = true;
        TestChildDAO testChild = TestChildDAO.of(12, "My Brother");
        LocalDateTime testDateTime = LocalDateTime.of(2019, Month.MARCH, 12, 5, 45, 00);
        TestParentModelImpl model = new TestParentModelImpl(new TestParentDAOImpl(testInt /*id*/, testString /*title*/, testBoolean /*active*/,
                12 /*rate*/, testChild /*child*/, DB.toUtcTimestamp(testDateTime) /*createDate*/, "he" /*createdBy*/,
                DB.toUtcTimestamp(testDateTime) /*lastModifiedDate*/, "she" /*lastModifiedBy*/, DataObjectFactory.ROWSTATE_UNMODIFIED));
        ModelFilter<TestParentModelImpl> filter = ModelFilter.columnIsEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        filter = filter.and(ModelFilter.columnIsNotEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString));
        String message = String.format("%s: %s ⊥ %s && %s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_ACTIVE,
                (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        String expResult = String.format("`%s`=%% AND `%s`<>%%", TestParentDAO.COLNAME_ACTIVE, TestParentDAO.COLNAME_TITLE);
        String result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testString = "Any Brother";
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        filter = filter.and(ModelFilter.columnIsNotEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString));
        message = String.format("%s: %s ⊥ %s && %s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_ACTIVE,
                (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        testInt = model.getRate();
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        filter = filter.and(ModelFilter.columnIsNotEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString));
        filter = filter.and(ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt));
        expResult = String.format("`%s`=%% AND `%s`<>%% AND `%s`<=%%", TestParentDAO.COLNAME_ACTIVE, TestParentDAO.COLNAME_TITLE,
                TestParentDAO.COLNAME_RATE);
        message = String.format("%s: %s ⊥ %s && %s: \"%s\" ⊥ \"%s\" && %s: %d ⊥ %d", TestParentDAO.COLNAME_ACTIVE,
                (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString,
                TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testInt++;
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        filter = filter.and(ModelFilter.columnIsNotEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString));
        filter = filter.and(ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt));
        message = String.format("%s: %s ⊥ %s && %s: \"%s\" ⊥ \"%s\" && %s: %d ⊥ %d", TestParentDAO.COLNAME_ACTIVE,
                (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString,
                TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
    }

    /**
     * Test of or method, of class ModelFilter.
     */
    @Test
    public void testOr() {
        System.out.println("or");
        
        int testInt = 1;
        String testString = "My Parent";
        boolean testBoolean = true;
        TestChildDAO testChild = TestChildDAO.of(12, "My Brother");
        LocalDateTime testDateTime = LocalDateTime.of(2019, Month.MARCH, 12, 5, 45, 00);
        TestParentModelImpl model = new TestParentModelImpl(new TestParentDAOImpl(testInt /*id*/, testString /*title*/, testBoolean /*active*/,
                12 /*rate*/, testChild /*child*/, DB.toUtcTimestamp(testDateTime) /*createDate*/, "he" /*createdBy*/,
                DB.toUtcTimestamp(testDateTime) /*lastModifiedDate*/, "she" /*lastModifiedBy*/, DataObjectFactory.ROWSTATE_UNMODIFIED));
        ModelFilter<TestParentModelImpl> filter = ModelFilter.columnIsEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        filter = filter.or(ModelFilter.columnIsNotEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString));
        String message = String.format("%s: %s ⊥ %s || %s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_ACTIVE,
                (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        String expResult = String.format("`%s`=%% OR `%s`<>%%", TestParentDAO.COLNAME_ACTIVE, TestParentDAO.COLNAME_TITLE);
        String result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testBoolean = false;
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        filter = filter.or(ModelFilter.columnIsNotEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString));
        message = String.format("%s: %s ⊥ %s || %s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_ACTIVE,
                (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testBoolean = true;
        testString = "Any Brother";
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        filter = filter.or(ModelFilter.columnIsNotEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString));
        message = String.format("%s: %s ⊥ %s || %s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_ACTIVE,
                (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        testInt = model.getRate();
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        filter = filter.or(ModelFilter.columnIsNotEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString));
        filter = filter.or(ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt));
        expResult = String.format("`%s`=%% OR `%s`<>%% OR `%s`<=%%", TestParentDAO.COLNAME_ACTIVE, TestParentDAO.COLNAME_TITLE,
                TestParentDAO.COLNAME_RATE);
        message = String.format("%s: %s ⊥ %s || %s: \"%s\" ⊥ \"%s\" || %s: %d ⊥ %d", TestParentDAO.COLNAME_ACTIVE,
                (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString,
                TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testInt--;
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        filter = filter.or(ModelFilter.columnIsNotEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString));
        filter = filter.or(ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt));
        message = String.format("%s: %s ⊥ %s || %s: \"%s\" ⊥ \"%s\" || %s: %d ⊥ %d", TestParentDAO.COLNAME_ACTIVE,
                (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString,
                TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
    }

//    /**
//     * Test of buildStatement method, of class ModelFilter.
//     */
//    @Test
//    public void testBuildStatement_3args() throws Exception {
//        System.out.println("buildStatement");
//        SqlStatementBuilder builder = null;
//        String baseSQL = "";
//        ModelFilter filter = null;
//        ModelFilter.buildStatement(builder, baseSQL, filter);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of buildStatement method, of class ModelFilter.
//     */
//    @Test
//    public void testBuildStatement_4args_1() throws Exception {
//        System.out.println("buildStatement");
//        SqlStatementBuilder builder = null;
//        String baseSQL = "";
//        ModelFilter filter = null;
//        Iterable<OrderBy> orderBy = null;
//        ModelFilter.buildStatement(builder, baseSQL, filter, orderBy);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of buildStatement method, of class ModelFilter.
//     */
//    @Test
//    public void testBuildStatement_4args_2() throws Exception {
//        System.out.println("buildStatement");
//        SqlStatementBuilder builder = null;
//        String baseSQL = "";
//        ThrowableConsumer<ParameterConsumer, SQLException> setParameters = null;
//        ModelFilter filter = null;
//        ModelFilter.buildStatement(builder, baseSQL, setParameters, filter);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of buildStatement method, of class ModelFilter.
//     */
//    @Test
//    public void testBuildStatement_5args() throws Exception {
//        System.out.println("buildStatement");
//        SqlStatementBuilder builder = null;
//        String baseSQL = "";
//        ThrowableConsumer<ParameterConsumer, SQLException> setParameters = null;
//        ModelFilter filter = null;
//        Iterable<OrderBy> orderBy = null;
//        ModelFilter.buildStatement(builder, baseSQL, setParameters, filter, orderBy);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    static int toComparatorResult(int value) { return (value < 0) ? -1 : ((value > 0) ? 1 : 0); }
    
    /**
     * Test of COMPARATOR_STRING field, of class ModelFilter.
     */
    @Test
    public void testCOMPARATOR_STRING() {
        System.out.println("COMPARATOR_STRING");
        String o1 = "test";
        String o2 = "test";
        int expResult = 0;
        int result = ModelFilter.COMPARATOR_STRING.compare(o1, o2);
        assertEquals("\"test\" ⊥ \"test\"", expResult, toComparatorResult(result));
        
        o1 = "";
        o2 = "";
        result = ModelFilter.COMPARATOR_STRING.compare(o1, o2);
        assertEquals("\"\" ⊥ \"\"", expResult, toComparatorResult(result));
        
        o1 = null;
        o2 = null;
        result = ModelFilter.COMPARATOR_STRING.compare(o1, o2);
        assertEquals("null ⊥ null", expResult, toComparatorResult(result));
        
        o1 = "test1";
        o2 = "test2";
        expResult = -1;
        result = ModelFilter.COMPARATOR_STRING.compare(o1, o2);
        assertEquals("\"\" ⊥ \"\"", expResult, toComparatorResult(result));
        
        o1 = "";
        o2 = "test2";
        result = ModelFilter.COMPARATOR_STRING.compare(o1, o2);
        assertEquals("\"\" ⊥ \"test2\"", expResult, toComparatorResult(result));
        
        o1 = "test1";
        o2 = null;
        result = ModelFilter.COMPARATOR_STRING.compare(o1, o2);
        assertEquals("\"test1\" ⊥ null", expResult, toComparatorResult(result));
        
        o1 = "";
        o2 = null;
        result = ModelFilter.COMPARATOR_STRING.compare(o1, o2);
        assertEquals("\"\" ⊥ null", expResult, toComparatorResult(result));
        
        o1 = "test2";
        o2 = "test1";
        expResult = 1;
        result = ModelFilter.COMPARATOR_STRING.compare(o1, o2);
        assertEquals("\"test2\" ⊥ \"test1\"", expResult, toComparatorResult(result));
        
        o1 = "test2";
        o2 = "";
        result = ModelFilter.COMPARATOR_STRING.compare(o1, o2);
        assertEquals("\"test2\" ⊥ \"\"", expResult, toComparatorResult(result));
        
        o1 = null;
        o2 = "test1";
        result = ModelFilter.COMPARATOR_STRING.compare(o1, o2);
        assertEquals("null ⊥ \"test1\"", expResult, toComparatorResult(result));
        
        o1 = null;
        o2 = "";
        result = ModelFilter.COMPARATOR_STRING.compare(o1, o2);
        assertEquals("null ⊥ \"\"", expResult, toComparatorResult(result));
    }
    
    /**
     * Test of COMPARATOR_BOOLEAN field, of class ModelFilter.
     */
    @Test
    public void testCOMPARATOR_BOOLEAN() {
        System.out.println("COMPARATOR_BOOLEAN");
        boolean o1 = true;
        boolean o2 = true;
        int expResult = 0;
        int result = ModelFilter.COMPARATOR_BOOLEAN.compare(o1, o2);
        assertEquals("true ⊥ true", expResult, toComparatorResult(result));
        
        o1 = false;
        o2 = false;
        result = ModelFilter.COMPARATOR_BOOLEAN.compare(o1, o2);
        assertEquals("false ⊥ false", expResult, toComparatorResult(result));
        
        o2 = true;
        expResult = -1;
        result = ModelFilter.COMPARATOR_BOOLEAN.compare(o1, o2);
        assertEquals("false ⊥ true", expResult, toComparatorResult(result));
        
        o1 = true;
        o2 = false;
        expResult = 1;
        result = ModelFilter.COMPARATOR_BOOLEAN.compare(o1, o2);
        assertEquals("true ⊥ false", expResult, toComparatorResult(result));
    }
    
    /**
     * Test of COMPARATOR_INTEGER field, of class ModelFilter.
     */
    @Test
    public void testCOMPARATOR_INTEGER() {
        System.out.println("COMPARATOR_INTEGER");
        int o1 = 0;
        int o2 = 0;
        int expResult = 0;
        int result = ModelFilter.COMPARATOR_INTEGER.compare(o1, o2);
        assertEquals("0 ⊥ 0", expResult, toComparatorResult(result));
        
        o1 = 1;
        o2 = 1;
        result = ModelFilter.COMPARATOR_INTEGER.compare(o1, o2);
        assertEquals("1 ⊥ 1", expResult, toComparatorResult(result));
        
        o1 = -1;
        o2 = -1;
        result = ModelFilter.COMPARATOR_INTEGER.compare(o1, o2);
        assertEquals("1 ⊥ 1", expResult, toComparatorResult(result));
        
        o1 = -1;
        o2 = 1;
        expResult = -1;
        result = ModelFilter.COMPARATOR_INTEGER.compare(o1, o2);
        assertEquals("-1 ⊥ 1", expResult, toComparatorResult(result));
        
        o1 = -1;
        o2 = 0;
        result = ModelFilter.COMPARATOR_INTEGER.compare(o1, o2);
        assertEquals("-1 ⊥ 1", expResult, toComparatorResult(result));
        
        o1 = 0;
        o2 = 1;
        result = ModelFilter.COMPARATOR_INTEGER.compare(o1, o2);
        assertEquals("-1 ⊥ 1", expResult, toComparatorResult(result));
        
        o1 = 0;
        o2 = -1;
        expResult = 1;
        result = ModelFilter.COMPARATOR_INTEGER.compare(o1, o2);
        assertEquals("1 ⊥ -1", expResult, toComparatorResult(result));
        
        o1 = -1;
        o2 = -2;
        result = ModelFilter.COMPARATOR_INTEGER.compare(o1, o2);
        assertEquals("1 ⊥ -1", expResult, toComparatorResult(result));
        
        o1 = 1;
        o2 = 0;
        result = ModelFilter.COMPARATOR_INTEGER.compare(o1, o2);
        assertEquals("1 ⊥ -1", expResult, toComparatorResult(result));
        
        o1 = 1;
        o2 = -1;
        result = ModelFilter.COMPARATOR_INTEGER.compare(o1, o2);
        assertEquals("1 ⊥ -1", expResult, toComparatorResult(result));
    }
    
    /**
     * Test of COMPARATOR_LOCALDATETIME field, of class ModelFilter.
     */
    @Test
    public void testCOMPARATOR_LOCALDATETIME() {
        System.out.println("COMPARATOR_LOCALDATETIME");
        
        LocalDateTime o1 = LocalDateTime.of(2019, Month.MARCH, 12, 5, 45, 0);
        LocalDateTime o2 = LocalDateTime.of(2019, Month.MARCH, 12, 5, 45, 0);
        int result = ModelFilter.COMPARATOR_LOCALDATETIME.compare(o1, o2);
        int expResult = 0;
        assertEquals("2019-03-12T05:45:00 ⊥ 2019-03-12T05:45:00", expResult, toComparatorResult(result));
        
        o1 = LocalDateTime.of(2019, Month.MARCH, 12, 5, 44, 59);
        o2 = LocalDateTime.of(2019, Month.MARCH, 12, 5, 45, 0);
        result = ModelFilter.COMPARATOR_LOCALDATETIME.compare(o1, o2);
        expResult = -1;
        assertEquals("2019-03-12T05:44:59 ⊥ 2019-03-12T05:45:00", expResult, toComparatorResult(result));
        
        o1 = LocalDateTime.of(2019, Month.MARCH, 12, 5, 45, 1);
        o2 = LocalDateTime.of(2019, Month.MARCH, 12, 5, 45, 0);
        result = ModelFilter.COMPARATOR_LOCALDATETIME.compare(o1, o2);
        expResult = 1;
        assertEquals("2019-03-12T05:45:01 ⊥ 2019-03-12T05:45:00", expResult, toComparatorResult(result));
    }
    
    
    /**
     * Test of columnIsEqualTo method, of class ModelFilter.
     */
    @Test
    public void testColumnIsEqualTo() {
        System.out.println("columnIsEqualTo");
        
        int testInt = 1;
        String testString = "My Parent";
        boolean testBoolean = true;
        TestChildDAO testChild = TestChildDAO.of(12, "My Brother");
        LocalDateTime testDateTime = LocalDateTime.of(2019, Month.MARCH, 12, 5, 45, 00);
        TestParentModelImpl model = new TestParentModelImpl(new TestParentDAOImpl(testInt /*id*/, testString /*title*/, testBoolean /*active*/,
                12 /*rate*/, testChild /*child*/, DB.toUtcTimestamp(testDateTime) /*createDate*/, "he" /*createdBy*/,
                DB.toUtcTimestamp(testDateTime) /*lastModifiedDate*/, "she" /*lastModifiedBy*/, DataObjectFactory.ROWSTATE_UNMODIFIED));
        assertEquals(testBoolean, model.getDataObject().isActive());
        assertEquals(testBoolean, model.isActive());
        assertEquals(testString, model.getTitle());
        assertEquals(12, model.getRate());
        assertEquals(testDateTime, model.getLastModifiedDate());
        ModelFilter<TestParentModelImpl> filter = ModelFilter.columnIsEqualTo(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        String message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        String expResult = String.format("`%s`=%%", TestParentDAO.COLNAME_ID);
        String result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testInt = 12;
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        expResult = String.format("`%s`=%%", TestParentDAO.COLNAME_TITLE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testString = "His Parent";
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        expResult = String.format("`%s`=%%", TestParentDAO.COLNAME_ACTIVE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testBoolean = false;
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        testInt = model.getRate();
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        expResult = String.format("`%s`=%%", TestParentDAO.COLNAME_RATE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testInt--;
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        testChild = TestChildDAO.of(testChild.getPrimaryKey(), testChild.getName());
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID, testChild.getPrimaryKey(),
                testChild.getName(), model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        expResult = String.format("`%s`=%%", TestParentDAO.COLNAME_CHILDID);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testChild = TestChildDAO.of(testChild.getPrimaryKey(), testChild.getName() + " Modified");
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID, testChild.getPrimaryKey(),
                testChild.getName(), model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testChild = TestChildDAO.of(testChild.getPrimaryKey() + 1, model.getChild().getName());
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID, testChild.getPrimaryKey(),
                testChild.getName(), model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        expResult = String.format("`%s`=%%", DataObjectFactory.COLNAME_LASTUPDATE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testDateTime = testDateTime.plusSeconds(1);
        filter = ModelFilter.columnIsEqualTo(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
    }

    /**
     * Test of columnIsNotEqualTo method, of class ModelFilter.
     */
    @Test
    public void testColumnIsNotEqualTo() {
        System.out.println("columnIsNotEqualTo");
        
        int testInt = 1;
        String testString = "My Parent";
        boolean testBoolean = true;
        TestChildDAO testChild = TestChildDAO.of(12, "My Brother");
        LocalDateTime testDateTime = LocalDateTime.of(2019, Month.MARCH, 12, 5, 45, 00);
        TestParentModelImpl model = new TestParentModelImpl(new TestParentDAOImpl(testInt /*id*/, testString /*title*/, testBoolean /*active*/,
                12 /*rate*/, testChild /*child*/, DB.toUtcTimestamp(testDateTime) /*createDate*/, "he" /*createdBy*/,
                DB.toUtcTimestamp(testDateTime) /*lastModifiedDate*/, "she" /*lastModifiedBy*/, DataObjectFactory.ROWSTATE_UNMODIFIED));
        ModelFilter<TestParentModelImpl> filter = ModelFilter.columnIsNotEqualTo(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        String message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        String expResult = String.format("`%s`<>%%", TestParentDAO.COLNAME_ID);
        String result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testInt = 12;
        filter = ModelFilter.columnIsNotEqualTo(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        filter = ModelFilter.columnIsNotEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        expResult = String.format("`%s`<>%%", TestParentDAO.COLNAME_TITLE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testString = "His Parent";
        filter = ModelFilter.columnIsNotEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        filter = ModelFilter.columnIsNotEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        expResult = String.format("`%s`<>%%", TestParentDAO.COLNAME_ACTIVE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testBoolean = false;
        filter = ModelFilter.columnIsNotEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        testInt = model.getRate();
        filter = ModelFilter.columnIsNotEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        expResult = String.format("`%s`<>%%", TestParentDAO.COLNAME_RATE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testInt--;
        filter = ModelFilter.columnIsNotEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        testChild = TestChildDAO.of(testChild.getPrimaryKey(), testChild.getName());
        filter = ModelFilter.columnIsNotEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID, testChild.getPrimaryKey(),
                testChild.getName(), model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        expResult = String.format("`%s`<>%%", TestParentDAO.COLNAME_CHILDID);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testChild = TestChildDAO.of(testChild.getPrimaryKey(), testChild.getName() + " Modified");
        filter = ModelFilter.columnIsNotEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID, testChild.getPrimaryKey(),
                testChild.getName(), model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testChild = TestChildDAO.of(testChild.getPrimaryKey() + 1, model.getChild().getName());
        filter = ModelFilter.columnIsNotEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID, testChild.getPrimaryKey(),
                testChild.getName(), model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        filter = ModelFilter.columnIsNotEqualTo(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        expResult = String.format("`%s`<>%%", DataObjectFactory.COLNAME_LASTUPDATE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testDateTime = testDateTime.plusSeconds(1);
        filter = ModelFilter.columnIsNotEqualTo(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
    }

    /**
     * Test of columnIsGreaterThan method, of class ModelFilter.
     */
    @Test
    public void testColumnIsGreaterThan() {
        System.out.println("columnIsGreaterThan");
        
        int testInt = 1;
        String testString = "My Parent";
        boolean testBoolean = true;
        TestChildDAO testChild = TestChildDAO.of(12, "My Brother");
        LocalDateTime testDateTime = LocalDateTime.of(2019, Month.MARCH, 12, 5, 45, 00);
        TestParentModelImpl model = new TestParentModelImpl(new TestParentDAOImpl(testInt /*id*/, testString /*title*/, testBoolean /*active*/,
                12 /*rate*/, testChild /*child*/, DB.toUtcTimestamp(testDateTime) /*createDate*/, "he" /*createdBy*/,
                DB.toUtcTimestamp(testDateTime) /*lastModifiedDate*/, "she" /*lastModifiedBy*/, DataObjectFactory.ROWSTATE_UNMODIFIED));
        ModelFilter<TestParentModelImpl> filter = ModelFilter.columnIsGreaterThan(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        String message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        String expResult = String.format("`%s`>%%", TestParentDAO.COLNAME_ID);
        String result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testInt++;
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testInt -= 2;
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        expResult = String.format("`%s`>%%", TestParentDAO.COLNAME_TITLE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testString = "His Parent";
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testString = "The Family";
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        expResult = String.format("`%s`>%%", TestParentDAO.COLNAME_ACTIVE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testBoolean = false;
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        model = new TestParentModelImpl(new TestParentDAOImpl(testInt /*id*/, testString /*title*/, testBoolean /*active*/,
                12 /*rate*/, testChild /*child*/, DB.toUtcTimestamp(testDateTime) /*createDate*/, "he" /*createdBy*/,
                DB.toUtcTimestamp(testDateTime) /*lastModifiedDate*/, "she" /*lastModifiedBy*/, DataObjectFactory.ROWSTATE_UNMODIFIED));
        testBoolean = true;
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        testInt = model.getRate();
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        expResult = String.format("`%s`>%%", TestParentDAO.COLNAME_RATE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testInt++;
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testInt -= 2;
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        expResult = String.format("`%s`>%%", TestParentDAO.COLNAME_CHILDID);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName() + " Modified");
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey() + 1, model.getChild().getName());
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey() - 1, model.getChild().getName());
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey() - 1, "His Child");
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        expResult = String.format("`%s`>%%", DataObjectFactory.COLNAME_LASTUPDATE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testDateTime = testDateTime.plusSeconds(1);
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testDateTime = testDateTime.minusSeconds(2);
        filter = ModelFilter.columnIsGreaterThan(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
    }

    /**
     * Test of columnIsGreaterThanOrEqualTo method, of class ModelFilter.
     */
    @Test
    public void testColumnIsGreaterThanOrEqualTo() {
        System.out.println("columnIsGreaterThanOrEqualTo");
        
        int testInt = 1;
        String testString = "My Parent";
        boolean testBoolean = true;
        TestChildDAO testChild = TestChildDAO.of(12, "My Brother");
        LocalDateTime testDateTime = LocalDateTime.of(2019, Month.MARCH, 12, 5, 45, 00);
        TestParentModelImpl model = new TestParentModelImpl(new TestParentDAOImpl(testInt /*id*/, testString /*title*/, testBoolean /*active*/,
                12 /*rate*/, testChild /*child*/, DB.toUtcTimestamp(testDateTime) /*createDate*/, "he" /*createdBy*/,
                DB.toUtcTimestamp(testDateTime) /*lastModifiedDate*/, "she" /*lastModifiedBy*/, DataObjectFactory.ROWSTATE_UNMODIFIED));
        ModelFilter<TestParentModelImpl> filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        String message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        String expResult = String.format("`%s`>=%%", TestParentDAO.COLNAME_ID);
        String result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testInt++;
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testInt -= 2;
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        expResult = String.format("`%s`>=%%", TestParentDAO.COLNAME_TITLE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testString = "His Parent";
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testString = "The Family";
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        expResult = String.format("`%s`>=%%", TestParentDAO.COLNAME_ACTIVE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testBoolean = false;
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        model = new TestParentModelImpl(new TestParentDAOImpl(testInt /*id*/, testString /*title*/, testBoolean /*active*/,
                12 /*rate*/, testChild /*child*/, DB.toUtcTimestamp(testDateTime) /*createDate*/, "he" /*createdBy*/,
                DB.toUtcTimestamp(testDateTime) /*lastModifiedDate*/, "she" /*lastModifiedBy*/, DataObjectFactory.ROWSTATE_UNMODIFIED));
        testBoolean = true;
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        testInt = model.getRate();
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        expResult = String.format("`%s`>=%%", TestParentDAO.COLNAME_RATE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testInt++;
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testInt -= 2;
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        expResult = String.format("`%s`>=%%", TestParentDAO.COLNAME_CHILDID);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName() + " Modified");
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey() + 1, model.getChild().getName());
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey() - 1, model.getChild().getName());
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey() - 1, "His Child");
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        expResult = String.format("`%s`>=%%", DataObjectFactory.COLNAME_LASTUPDATE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testDateTime = testDateTime.plusSeconds(1);
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testDateTime = testDateTime.minusSeconds(2);
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
    }

    /**
     * Test of columnIsLessThan method, of class ModelFilter.
     */
    @Test
    public void testColumnIsLessThan() {
        System.out.println("columnIsLessThan");
        
        int testInt = 1;
        String testString = "My Parent";
        boolean testBoolean = true;
        TestChildDAO testChild = TestChildDAO.of(12, "My Brother");
        LocalDateTime testDateTime = LocalDateTime.of(2019, Month.MARCH, 12, 5, 45, 00);
        TestParentModelImpl model = new TestParentModelImpl(new TestParentDAOImpl(testInt /*id*/, testString /*title*/, testBoolean /*active*/,
                12 /*rate*/, testChild /*child*/, DB.toUtcTimestamp(testDateTime) /*createDate*/, "he" /*createdBy*/,
                DB.toUtcTimestamp(testDateTime) /*lastModifiedDate*/, "she" /*lastModifiedBy*/, DataObjectFactory.ROWSTATE_UNMODIFIED));
        ModelFilter<TestParentModelImpl> filter = ModelFilter.columnIsLessThan(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        String message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        String expResult = String.format("`%s`<%%", TestParentDAO.COLNAME_ID);
        String result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testInt++;
        filter = ModelFilter.columnIsLessThan(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testInt -= 2;
        filter = ModelFilter.columnIsLessThan(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        filter = ModelFilter.columnIsLessThan(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        expResult = String.format("`%s`<%%", TestParentDAO.COLNAME_TITLE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testString = "His Parent";
        filter = ModelFilter.columnIsLessThan(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testString = "The Family";
        filter = ModelFilter.columnIsLessThan(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        filter = ModelFilter.columnIsLessThan(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        expResult = String.format("`%s`<%%", TestParentDAO.COLNAME_ACTIVE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testBoolean = false;
        filter = ModelFilter.columnIsLessThan(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        model = new TestParentModelImpl(new TestParentDAOImpl(testInt /*id*/, testString /*title*/, testBoolean /*active*/,
                12 /*rate*/, testChild /*child*/, DB.toUtcTimestamp(testDateTime) /*createDate*/, "he" /*createdBy*/,
                DB.toUtcTimestamp(testDateTime) /*lastModifiedDate*/, "she" /*lastModifiedBy*/, DataObjectFactory.ROWSTATE_UNMODIFIED));
        testBoolean = true;
        filter = ModelFilter.columnIsLessThan(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        testInt = model.getRate();
        filter = ModelFilter.columnIsLessThan(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        expResult = String.format("`%s`<%%", TestParentDAO.COLNAME_RATE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testInt++;
        filter = ModelFilter.columnIsLessThan(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testInt -= 2;
        filter = ModelFilter.columnIsLessThan(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        testChild = TestChildDAO.of(testChild.getPrimaryKey(), testChild.getName());
        filter = ModelFilter.columnIsLessThan(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID, testChild.getPrimaryKey(),
                testChild.getName(), model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        expResult = String.format("`%s`<%%", TestParentDAO.COLNAME_CHILDID);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testChild = TestChildDAO.of(testChild.getPrimaryKey(), testChild.getName() + " Modified");
        filter = ModelFilter.columnIsLessThan(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID, testChild.getPrimaryKey(),
                testChild.getName(), model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testChild = TestChildDAO.of(testChild.getPrimaryKey() + 1, model.getChild().getName());
        filter = ModelFilter.columnIsLessThan(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID, testChild.getPrimaryKey(),
                testChild.getName(), model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testChild = TestChildDAO.of(testChild.getPrimaryKey() - 1, model.getChild().getName());
        filter = ModelFilter.columnIsLessThan(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID, testChild.getPrimaryKey(),
                testChild.getName(), model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testChild = TestChildDAO.of(testChild.getPrimaryKey() - 1, "His Child");
        filter = ModelFilter.columnIsLessThan(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID, testChild.getPrimaryKey(),
                testChild.getName(), model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        filter = ModelFilter.columnIsLessThan(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        expResult = String.format("`%s`<%%", DataObjectFactory.COLNAME_LASTUPDATE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testDateTime = testDateTime.plusSeconds(1);
        filter = ModelFilter.columnIsLessThan(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testDateTime = testDateTime.minusSeconds(2);
        filter = ModelFilter.columnIsLessThan(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
    }

    /**
     * Test of columnIsLessThanOrEqualTo method, of class ModelFilter.
     */
    @Test
    public void testColumnIsLessThanOrEqualTo() {
        System.out.println("columnIsLessThanOrEqualTo");
        
        int testInt = 1;
        String testString = "My Parent";
        boolean testBoolean = true;
        TestChildDAO testChild = TestChildDAO.of(12, "My Brother");
        LocalDateTime testDateTime = LocalDateTime.of(2019, Month.MARCH, 12, 5, 45, 00);
        TestParentModelImpl model = new TestParentModelImpl(new TestParentDAOImpl(testInt /*id*/, testString /*title*/, testBoolean /*active*/,
                12 /*rate*/, testChild /*child*/, DB.toUtcTimestamp(testDateTime) /*createDate*/, "he" /*createdBy*/,
                DB.toUtcTimestamp(testDateTime) /*lastModifiedDate*/, "she" /*lastModifiedBy*/, DataObjectFactory.ROWSTATE_UNMODIFIED));
        ModelFilter<TestParentModelImpl> filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        String message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        String expResult = String.format("`%s`<=%%", TestParentDAO.COLNAME_ID);
        String result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testInt++;
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testInt -= 2;
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_ID, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_ID, model.getDataObject().getPrimaryKey(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        expResult = String.format("`%s`<=%%", TestParentDAO.COLNAME_TITLE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testString = "His Parent";
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testString = "The Family";
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_TITLE, ModelFilter.COMPARATOR_STRING, testString);
        message = String.format("%s: \"%s\" ⊥ \"%s\"", TestParentDAO.COLNAME_TITLE, model.getTitle(), testString);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        expResult = String.format("`%s`<=%%", TestParentDAO.COLNAME_ACTIVE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testBoolean = false;
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        model = new TestParentModelImpl(new TestParentDAOImpl(testInt /*id*/, testString /*title*/, testBoolean /*active*/,
                12 /*rate*/, testChild /*child*/, DB.toUtcTimestamp(testDateTime) /*createDate*/, "he" /*createdBy*/,
                DB.toUtcTimestamp(testDateTime) /*lastModifiedDate*/, "she" /*lastModifiedBy*/, DataObjectFactory.ROWSTATE_UNMODIFIED));
        testBoolean = true;
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, testBoolean);
        message = String.format("%s: %s ⊥ %s", TestParentDAO.COLNAME_ACTIVE, (model.isActive()) ? "true" : "false", (testBoolean) ? "true" : "false");
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        
        testInt = model.getRate();
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        expResult = String.format("`%s`<=%%", TestParentDAO.COLNAME_RATE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testInt++;
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testInt -= 2;
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_RATE, ModelFilter.COMPARATOR_INTEGER, testInt);
        message = String.format("%s: %d ⊥ %d", TestParentDAO.COLNAME_RATE, model.getRate(), testInt);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName());
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        expResult = String.format("`%s`<=%%", TestParentDAO.COLNAME_CHILDID);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName() + " Modified");
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey() + 1, model.getChild().getName());
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey() - 1, model.getChild().getName());
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        testChild = TestChildDAO.of(model.getChild().getDataObject().getPrimaryKey() - 1, "His Child");
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_CHILD, COMPARATOR_CHILD, TestChildModel.of(testChild));
        message = String.format("%s: { pk: %d, name: \"%s\" } ⊥ { pk: %d, name: \"%s\" }", TestParentDAO.COLNAME_CHILDID,
                model.getChild().getDataObject().getPrimaryKey(), model.getChild().getName(), testChild.getPrimaryKey(), testChild.getName());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
        
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        expResult = String.format("`%s`<=%%", DataObjectFactory.COLNAME_LASTUPDATE);
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testDateTime = testDateTime.plusSeconds(1);
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertTrue(message, filter.test(model));
        testDateTime = testDateTime.minusSeconds(2);
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESSOR_LASTMODIFIEDDATE, ModelFilter.COMPARATOR_LOCALDATETIME, testDateTime);
        message = String.format("%s: %s ⊥ %s", DataObjectFactory.COLNAME_LASTUPDATE, model.getLastModifiedDate().toString(), testDateTime.toString());
        result = filter.get();
        assertEquals(message, expResult, result);
        assertFalse(message, filter.test(model));
    }

//    /**
//     * Test of empty method, of class ModelFilter.
//     */
//    @Test
//    public void testEmpty() {
//        System.out.println("empty");
//        ModelFilter expResult = null;
//        ModelFilter result = ModelFilter.empty();
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

    public static final String TEST_TABLENAME_CHILD = "child";
    public static final String TEST_TABLENAME_PARENT = "parent";
    
    public interface TestChildDAO extends DataObject {
        public static final String COLNAME_PK = "pk";
        public static final String COLNAME_NAME = "name";
        String getName();
        public static TestChildDAO of(int pk, String name) {
            return new TestChildDAO() {
                @Override
                public String getName() { return name; }
                @Override
                public int getPrimaryKey() { return pk; }
                @Override
                public int getRowState() { return DataObjectFactory.ROWSTATE_UNMODIFIED; }
            };
        }
    }
    
    public interface TestParentDAO extends DataObject {
        public static final String COLNAME_ID = "id";
        public static final String COLNAME_TITLE = "title";
        public static final String COLNAME_ACTIVE = "active";
        public static final String COLNAME_RATE = "rate";
        public static final String COLNAME_CHILDID = "childId";
        String getTitle();
        boolean isActive();
        int getRate();
        TestChildDAO getChild();
        public static TestParentDAO of(int id, String title, boolean active, int rate, TestChildDAO child) {
            return new TestParentDAO() {
                @Override
                public String getTitle() { return title; }
                @Override
                public boolean isActive() { return active; }
                @Override
                public int getRate() { return rate; }
                @Override
                public TestChildDAO getChild() { return child; }
                @Override
                public int getPrimaryKey() { return id; }
                @Override
                public int getRowState() { return DataObjectFactory.ROWSTATE_UNMODIFIED; }
            };
        }
    }
    
    @TableName(TEST_TABLENAME_CHILD)
    @PrimaryKeyColumn(TestChildDAO.COLNAME_PK)
    public static class TestChildDAOImpl extends DataObjectImpl implements TestChildDAO {
        public TestChildDAOImpl(int id, String name, Timestamp createDate, String createdBy, Timestamp lastModifiedDate, String lastModifiedBy, int rowState) {
            super(id, createDate, createdBy, lastModifiedDate, lastModifiedBy, rowState);
            this.name = (name == null) ? "" : name;
        }
        private String name;
        @Override
        public String getName() { return name; }
        public void setName(String value) { name = (value == null) ? "" : value; }
    }
    
    @TableName(TEST_TABLENAME_PARENT)
    @PrimaryKeyColumn(TestParentDAO.COLNAME_ID)
    public static class TestParentDAOImpl extends DataObjectImpl implements TestParentDAO {
        public TestParentDAOImpl(int id, String title, boolean active, int rate, TestChildDAO child, Timestamp createDate, String createdBy, Timestamp lastModifiedDate, String lastModifiedBy, int rowState) {
            super(id, createDate, createdBy, lastModifiedDate, lastModifiedBy, rowState);
            this.title = (title == null) ? "" : title;
            this.active = active;
            this.rate = rate;
            this.child = child;
        }
        private String title;
        @Override
        public String getTitle() { return title; }
        public void setTitle(String value) { title = (value == null) ? "" : value; }
        private boolean active;
        @Override
        public boolean isActive() { return active; }
        public void setActive(boolean value) { active = value; }
        private int rate;
        @Override
        public int getRate() { return rate; }
        public void setRate(int value) { rate = value; }
        private TestChildDAO child;
        @Override
        public TestChildDAO getChild() { return child; }
        public void setChild(TestChildDAO value) { child = value; }
    }
    
    public interface TestChildModel<T extends TestChildDAO> extends ChildModel<T> {
        String getName();
        ReadOnlyStringProperty nameProperty();
        public static TestChildModel<?> of(TestChildDAO dao) {
            if (null == dao)
                return null;
            if (dao instanceof TestChildDAOImpl)
                return new TestChildModelImpl((TestChildDAOImpl)dao);
            return new TestChildModel<TestChildDAO>() {
                private final ReadOnlyStringWrapper name = new ReadOnlyStringWrapper(dao.getName());
                @Override
                public String getName() { return name.get(); }
                @Override
                public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }
                @Override
                public TestChildDAO getDataObject() { return dao; }
            };
        }
    }
    
    public interface TestParentModel<T extends TestParentDAO> extends ChildModel<T> {
        String getTitle();
        ReadOnlyStringProperty titleProperty();
        boolean isActive();
        ReadOnlyBooleanProperty activeProperty();
        int getRate();
        ReadOnlyIntegerProperty rateProperty();
        TestChildModel<?> getChild();
        ReadOnlyObjectProperty<TestChildModel<?>> childProperty();
        public static TestParentModel<?> of (TestParentDAO dao) {
            if (null == dao)
                return null;
            if (dao instanceof TestParentDAOImpl)
                return new TestParentModelImpl((TestParentDAOImpl)dao);
            return new TestParentModel<TestParentDAO>() {
                private final ReadOnlyStringWrapper title = new ReadOnlyStringWrapper(dao.getTitle());
                @Override
                public String getTitle() { return title.get(); }
                @Override
                public ReadOnlyStringProperty titleProperty() { return title.getReadOnlyProperty(); }
                @Override
                public TestParentDAO getDataObject() { return dao; }
                private final ReadOnlyBooleanWrapper active = new ReadOnlyBooleanWrapper(dao.isActive());
                @Override
                public boolean isActive() { return active.get(); }
                @Override
                public ReadOnlyBooleanProperty activeProperty() { return active.getReadOnlyProperty(); }
                private final ReadOnlyIntegerWrapper rate = new ReadOnlyIntegerWrapper(dao.getRate());
                @Override
                public int getRate() { return rate.get(); }
                @Override
                public ReadOnlyIntegerProperty rateProperty() { return rate.getReadOnlyProperty(); }
                private final ReadOnlyObjectWrapper<TestChildModel<? extends TestChildDAO>> child = new ReadOnlyObjectWrapper<>();
                @Override
                public TestChildModel<?> getChild() { return child.get(); }
                @Override
                public ReadOnlyObjectProperty<TestChildModel<?>> childProperty() { return child.getReadOnlyProperty(); }
            };
        }
    }
    
    public static class TestChildModelImpl extends ItemModel<TestChildDAOImpl> implements TestChildModel<TestChildDAOImpl> {
        public TestChildModelImpl(TestChildDAOImpl dao) {
            super(dao);
            name = new ReadOnlyStringWrapper(dao.getName());
        }
        @Override
        public boolean delete(Connection connection) { throw new UnsupportedOperationException("Not supported yet."); }
        @Override
        public void saveChanges(Connection connection) { throw new UnsupportedOperationException("Not supported yet."); }
        private final ReadOnlyStringWrapper name;
        @Override
        public String getName() { return name.get(); }
        @Override
        public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }
    }
    
    public static class TestParentModelImpl extends ItemModel<TestParentDAOImpl> implements TestParentModel<TestParentDAOImpl> {
        public TestParentModelImpl(TestParentDAOImpl dao) {
            super(dao);
            title = new ReadOnlyStringWrapper(dao.getTitle());
            active = new ReadOnlyBooleanWrapper(dao.isActive());
            rate = new ReadOnlyIntegerWrapper(dao.getRate());
            child = new ReadOnlyObjectWrapper<>(TestChildModel.of(dao.getChild()));
        }
        @Override
        public boolean delete(Connection connection) { throw new UnsupportedOperationException("Not supported yet."); }
        @Override
        public void saveChanges(Connection connection) { throw new UnsupportedOperationException("Not supported yet."); }
        private final ReadOnlyStringWrapper title;
        @Override
        public String getTitle() { return title.get(); }
        @Override
        public ReadOnlyStringProperty titleProperty() { return title.getReadOnlyProperty(); }
        private final ReadOnlyBooleanWrapper active;
        @Override
        public boolean isActive() { return active.get(); }
        @Override
        public ReadOnlyBooleanProperty activeProperty() { return active.getReadOnlyProperty(); }
        private final ReadOnlyIntegerWrapper rate;
        @Override
        public int getRate() { return rate.get(); }
        @Override
        public ReadOnlyIntegerProperty rateProperty() { return rate.getReadOnlyProperty(); }
        private final ReadOnlyObjectWrapper<TestChildModel<? extends TestChildDAO>> child;
        @Override
        public TestChildModel<?> getChild() { return child.get(); }
        @Override
        public ReadOnlyObjectProperty<TestChildModel<?>> childProperty() { return child.getReadOnlyProperty(); }
    }
    
    public static final ValueAccessor<TestParentModelImpl, Integer> ACCESSOR_ID = new ValueAccessor<TestParentModelImpl, Integer>() {
        @Override
        public Integer apply(TestParentModelImpl t) { return t.getDataObject().getPrimaryKey(); }
        @Override
        public String get() { return TestParentDAO.COLNAME_ID; }
        @Override
        public void accept(Integer t, ParameterConsumer u) throws SQLException { u.setInt(t); }
    };
    public static final ValueAccessor<TestParentModelImpl, String> ACCESSOR_TITLE = new ValueAccessor<TestParentModelImpl, String>() {
        @Override
        public String apply(TestParentModelImpl t) { return t.getTitle(); }
        @Override
        public String get() { return TestParentDAO.COLNAME_TITLE; }
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
    };
    public static final ValueAccessor<TestParentModelImpl, Integer> ACCESSOR_RATE = new ValueAccessor<TestParentModelImpl, Integer>() {
        @Override
        public Integer apply(TestParentModelImpl t) { return t.getRate(); }
        @Override
        public String get() { return TestParentDAO.COLNAME_RATE; }
        @Override
        public void accept(Integer t, ParameterConsumer u) throws SQLException { u.setInt(t); }
    };
    public static final ValueAccessor<TestParentModelImpl, Boolean> ACCESSOR_ACTIVE = new ValueAccessor<TestParentModelImpl, Boolean>() {
        @Override
        public Boolean apply(TestParentModelImpl t) { return t.isActive(); }
        @Override
        public String get() { return TestParentDAO.COLNAME_ACTIVE; }
        @Override
        public void accept(Boolean t, ParameterConsumer u) throws SQLException { u.setBoolean(t); }
    };
    public static final ValueAccessor<TestParentModelImpl, LocalDateTime> ACCESSOR_LASTMODIFIEDDATE = new ValueAccessor<TestParentModelImpl, LocalDateTime>() {
        @Override
        public LocalDateTime apply(TestParentModelImpl t) { return t.getLastModifiedDate(); }
        @Override
        public String get() { return DataObjectFactory.COLNAME_LASTUPDATE; }
        @Override
        public void accept(LocalDateTime t, ParameterConsumer u) throws SQLException { u.setDateTime(t); }
    };
    public static final ValueAccessor<TestParentModelImpl, TestChildModel<?>> ACCESSOR_CHILD = new ValueAccessor<TestParentModelImpl, TestChildModel<?>>() {
        @Override
        public TestChildModel<?> apply(TestParentModelImpl t) { return t.getChild(); }
        @Override
        public String get() { return TestParentDAO.COLNAME_CHILDID; }
        @Override
        public void accept(TestChildModel<?> t, ParameterConsumer u) throws SQLException { u.setInt(t.getDataObject().getPrimaryKey()); }
    };
    public static final ValueAccessor<TestParentModelImpl, String> ACCESSOR_CHILD_NAME = new ValueAccessor<TestParentModelImpl, String>() {
        @Override
        public String apply(TestParentModelImpl t) { return t.getChild().getName(); }
        @Override
        public String get() { return TestChildDAO.COLNAME_NAME; }
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
    };
    public static final Comparator<TestChildModel<?>> COMPARATOR_CHILD = (TestChildModel<?> o1, TestChildModel<?> o2) -> {
        return ModelFilter.compareChildModels(o1, o2, (v1, v2) -> v1.getName().compareTo(v2.getName()));
    };
}
