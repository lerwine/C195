/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.filter;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Month;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import scheduler.dao.DataObject;
import scheduler.dao.DataObjectImpl;
import util.ThrowableConsumer;
import view.ChildModel;
import view.ItemModel;
import view.appointment.AppointmentModel;

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
    
//    /**
//     * Test of and method, of class ModelFilter.
//     */
//    @Test
//    public void testAnd() {
//        System.out.println("and");
//        ModelFilter instance = new ModelFilterImpl();
//        ModelFilter expResult = null;
//        ModelFilter result = instance.and(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

//    /**
//     * Test of or method, of class ModelFilter.
//     */
//    @Test
//    public void testOr() {
//        System.out.println("or");
//        ModelFilter instance = new ModelFilterImpl();
//        ModelFilter expResult = null;
//        ModelFilter result = instance.or(null);
//        assertEquals(expResult, result);
//        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
//    }

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

    /**
     * Test of COMPARATOR_STRING field, of class ModelFilter.
     */
    @Test
    public void testCOMPARATOR_STRING() {
        System.out.println("COMPARATOR_STRING");
        int result = ModelFilter.COMPARATOR_STRING.compare("test", "test");
        assertTrue(result == 0);
        result = ModelFilter.COMPARATOR_STRING.compare("test1", "test2");
        assertTrue(result < 0);
        result = ModelFilter.COMPARATOR_STRING.compare("test2", "test1");
        assertTrue(result > 0);
    }
    
    /**
     * Test of COMPARATOR_BOOLEAN field, of class ModelFilter.
     */
    @Test
    public void testCOMPARATOR_BOOLEAN() {
        System.out.println("COMPARATOR_BOOLEAN");
        int result = ModelFilter.COMPARATOR_BOOLEAN.compare(true, true);
        assertTrue(result == 0);
        result = ModelFilter.COMPARATOR_BOOLEAN.compare(false, false);
        assertTrue(result == 0);
        result = ModelFilter.COMPARATOR_BOOLEAN.compare(false, true);
        assertTrue(result < 0);
        result = ModelFilter.COMPARATOR_BOOLEAN.compare(true, false);
        assertTrue(result > 0);
    }
    
    /**
     * Test of COMPARATOR_INTEGER field, of class ModelFilter.
     */
    @Test
    public void testCOMPARATOR_INTEGER() {
        System.out.println("COMPARATOR_INTEGER");
        int result = ModelFilter.COMPARATOR_INTEGER.compare(0, 0);
        assertTrue(result == 0);
        result = ModelFilter.COMPARATOR_INTEGER.compare(1, 1);
        assertTrue(result == 0);
        result = ModelFilter.COMPARATOR_INTEGER.compare(-1, 1);
        assertTrue(result < 0);
        result = ModelFilter.COMPARATOR_INTEGER.compare(1, -1);
        assertTrue(result > 0);
    }
    
    /**
     * Test of COMPARATOR_LOCALDATETIME field, of class ModelFilter.
     */
    @Test
    public void testCOMPARATOR_LOCALDATETIME() {
        System.out.println("COMPARATOR_LOCALDATETIME");
        int result = ModelFilter.COMPARATOR_LOCALDATETIME.compare(LocalDateTime.of(2019, Month.MARCH, 12, 5, 45), LocalDateTime.of(2019, Month.MARCH, 12, 5, 45));
        assertTrue(result == 0);
        result = ModelFilter.COMPARATOR_LOCALDATETIME.compare(LocalDateTime.of(2019, Month.MARCH, 12, 5, 44), LocalDateTime.of(2019, Month.MARCH, 12, 5, 45));
        assertTrue(result < 0);
        result = ModelFilter.COMPARATOR_LOCALDATETIME.compare(LocalDateTime.of(2019, Month.MARCH, 12, 5, 46), LocalDateTime.of(2019, Month.MARCH, 12, 5, 45));
        assertTrue(result > 0);
    }
    
    /**
     * Test of columnIsEqualTo method, of class ModelFilter.
     */
    @Test
    public void testColumnIsEqualTo() {
        System.out.println("columnIsEqualTo");
        ModelFilter<TestModel> filter = ModelFilter.columnIsEqualTo(ACCESOR_RATE, ModelFilter.COMPARATOR_INTEGER, 4);
        TestModel model = new TestModel(new TestDataObjectImpl(1, 4, true, new TestReference() {
            private String name = "MyName";
            private final int primaryKey = 1024;
            @Override
            public String getName() { return name; }
            @Override
            public void setName(String value) { name = (value == null) ? "" : value; }
            @Override
            public int getPrimaryKey() { return primaryKey; }
            @Override
            public int getRowState() { return DataObject.ROWSTATE_UNMODIFIED; }
        }, Timestamp.valueOf(LocalDateTime.now().minusDays(1)), "he", Timestamp.valueOf(LocalDateTime.now()), "she", DataObject.ROWSTATE_UNMODIFIED));
        String expResult = "`rate`=%";
        String result = filter.get();
        assertEquals(expResult, result);
        assertTrue(filter.test(model));
        model.getDataObject().setRate(12);
        model = new TestModel(model.getDataObject());
        assertFalse(filter.test(model));
        filter = ModelFilter.columnIsEqualTo(ACCESOR_ACTIVE, ModelFilter.COMPARATOR_BOOLEAN, true);
        assertTrue(filter.test(model));
        model.getDataObject().setActive(false);
        model = new TestModel(model.getDataObject());
        assertFalse(filter.test(model));
    }

    /**
     * Test of columnIsNotEqualTo method, of class ModelFilter.
     */
    @Test
    public void testColumnIsNotEqualTo() {
        System.out.println("columnIsNotEqualTo");
        ModelFilter<TestModel> filter = ModelFilter.columnIsNotEqualTo(ACCESOR_RATE, ModelFilter.COMPARATOR_INTEGER, 4);
        TestModel model = new TestModel(new TestDataObjectImpl(1, 4, true, new TestReference() {
            private String name = "MyName";
            private final int primaryKey = 1024;
            @Override
            public String getName() { return name; }
            @Override
            public void setName(String value) { name = (value == null) ? "" : value; }
            @Override
            public int getPrimaryKey() { return primaryKey; }
            @Override
            public int getRowState() { return DataObject.ROWSTATE_UNMODIFIED; }
        }, Timestamp.valueOf(LocalDateTime.now().minusDays(1)), "he", Timestamp.valueOf(LocalDateTime.now()), "she", DataObject.ROWSTATE_UNMODIFIED));
        String expResult = "`rate`<>%";
        String result = filter.get();
        assertEquals(expResult, result);
        assertFalse(filter.test(model));
        model.getDataObject().setRate(12);
        model = new TestModel(model.getDataObject());
        assertTrue(filter.test(model));
    }

    /**
     * Test of columnIsGreaterThan method, of class ModelFilter.
     */
    @Test
    public void testColumnIsGreaterThan() {
        System.out.println("columnIsGreaterThan");
        ModelFilter<TestModel> filter = ModelFilter.columnIsGreaterThan(ACCESOR_RATE, ModelFilter.COMPARATOR_INTEGER, 4);
        TestModel model = new TestModel(new TestDataObjectImpl(1, 4, true, new TestReference() {
            private String name = "MyName";
            private final int primaryKey = 1024;
            @Override
            public String getName() { return name; }
            @Override
            public void setName(String value) { name = (value == null) ? "" : value; }
            @Override
            public int getPrimaryKey() { return primaryKey; }
            @Override
            public int getRowState() { return DataObject.ROWSTATE_UNMODIFIED; }
        }, Timestamp.valueOf(LocalDateTime.now().minusDays(1)), "he", Timestamp.valueOf(LocalDateTime.now()), "she", DataObject.ROWSTATE_UNMODIFIED));
        String expResult = "`rate`>%";
        String result = filter.get();
        assertEquals(expResult, result);
        assertFalse(filter.test(model));
        filter = ModelFilter.columnIsGreaterThan(ACCESOR_RATE, ModelFilter.COMPARATOR_INTEGER, 3);
        assertTrue(filter.test(model));
    }

    /**
     * Test of columnIsGreaterThanOrEqualTo method, of class ModelFilter.
     */
    @Test
    public void testColumnIsGreaterThanOrEqualTo() {
        System.out.println("columnIsGreaterThanOrEqualTo");
        ModelFilter<TestModel> filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESOR_RATE, ModelFilter.COMPARATOR_INTEGER, 4);
        TestModel model = new TestModel(new TestDataObjectImpl(1, 4, true, new TestReference() {
            private String name = "MyName";
            private final int primaryKey = 1024;
            @Override
            public String getName() { return name; }
            @Override
            public void setName(String value) { name = (value == null) ? "" : value; }
            @Override
            public int getPrimaryKey() { return primaryKey; }
            @Override
            public int getRowState() { return DataObject.ROWSTATE_UNMODIFIED; }
        }, Timestamp.valueOf(LocalDateTime.now().minusDays(1)), "he", Timestamp.valueOf(LocalDateTime.now()), "she", DataObject.ROWSTATE_UNMODIFIED));
        String expResult = "`rate`>=%";
        String result = filter.get();
        assertEquals(expResult, result);
        assertTrue(filter.test(model));
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESOR_RATE, ModelFilter.COMPARATOR_INTEGER, 3);
        assertTrue(filter.test(model));
        filter = ModelFilter.columnIsGreaterThanOrEqualTo(ACCESOR_RATE, ModelFilter.COMPARATOR_INTEGER, 12);
        assertFalse(filter.test(model));
    }

    /**
     * Test of columnIsLessThan method, of class ModelFilter.
     */
    @Test
    public void testColumnIsLessThan() {
        System.out.println("columnIsLessThan");
        ModelFilter<TestModel> filter = ModelFilter.columnIsLessThan(ACCESOR_RATE, ModelFilter.COMPARATOR_INTEGER, 4);
        TestModel model = new TestModel(new TestDataObjectImpl(1, 4, true, new TestReference() {
            private String name = "MyName";
            private final int primaryKey = 1024;
            @Override
            public String getName() { return name; }
            @Override
            public void setName(String value) { name = (value == null) ? "" : value; }
            @Override
            public int getPrimaryKey() { return primaryKey; }
            @Override
            public int getRowState() { return DataObject.ROWSTATE_UNMODIFIED; }
        }, Timestamp.valueOf(LocalDateTime.now().minusDays(1)), "he", Timestamp.valueOf(LocalDateTime.now()), "she", DataObject.ROWSTATE_UNMODIFIED));
        String expResult = "`rate`<%";
        String result = filter.get();
        assertEquals(expResult, result);
        assertFalse(filter.test(model));
        filter = ModelFilter.columnIsLessThan(ACCESOR_RATE, ModelFilter.COMPARATOR_INTEGER, 12);
        assertTrue(filter.test(model));
    }

    /**
     * Test of columnIsLessThanOrEqualTo method, of class ModelFilter.
     */
    @Test
    public void testColumnIsLessThanOrEqualTo() {
        System.out.println("columnIsLessThanOrEqualTo");
        ModelFilter<TestModel> filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESOR_RATE, ModelFilter.COMPARATOR_INTEGER, 4);
        TestModel model = new TestModel(new TestDataObjectImpl(1, 4, true, new TestReference() {
            private String name = "MyName";
            private final int primaryKey = 1024;
            @Override
            public String getName() { return name; }
            @Override
            public void setName(String value) { name = (value == null) ? "" : value; }
            @Override
            public int getPrimaryKey() { return primaryKey; }
            @Override
            public int getRowState() { return DataObject.ROWSTATE_UNMODIFIED; }
        }, Timestamp.valueOf(LocalDateTime.now().minusDays(1)), "he", Timestamp.valueOf(LocalDateTime.now()), "she", DataObject.ROWSTATE_UNMODIFIED));
        String expResult = "`rate`<=%";
        String result = filter.get();
        assertEquals(expResult, result);
        assertTrue(filter.test(model));
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESOR_RATE, ModelFilter.COMPARATOR_INTEGER, 12);
        assertTrue(filter.test(model));
        filter = ModelFilter.columnIsLessThanOrEqualTo(ACCESOR_RATE, ModelFilter.COMPARATOR_INTEGER, 3);
        assertFalse(filter.test(model));
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

    public interface TestReference extends DataObject {
        String getName();
        void setName(String value);
    }
    
    public interface TestDataObject extends DataObject {
        int getRate();
        void setRate(int value);
        boolean isActive();
        void setActive(boolean value);
        TestReference getRefObj();
        void setRefObj(TestReference value);
    }
    
    public interface RefChildObject extends ChildModel<TestReference> {
        String getName();
        ReadOnlyStringProperty nameProperty();
        
        public static RefChildObject of(TestReference obj) {
            if (obj == null)
                return null;
            
            return new RefChildObject() {
                private final ReadOnlyStringWrapper name = new ReadOnlyStringWrapper(obj.getName());
                
                @Override
                public String getName() { return name.get(); }

                @Override
                public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }

                @Override
                public TestReference getDataObject() { return obj; }
                
            };
        }
    }
    
    public class TestDataObjectImpl extends DataObjectImpl implements TestDataObject {

        public TestDataObjectImpl(int primaryKey, int rate, boolean active, TestReference refObj, Timestamp createDate, String createdBy, Timestamp lastModifiedDate, String lastModifiedBy, int rowState) {
            super(primaryKey, createDate, createdBy, lastModifiedDate, lastModifiedBy, rowState);
            this.rate = rate;
            this.active = active;
            this.refObj = refObj;
        }
        
        private int rate;

        @Override
        public int getRate() { return rate; }

        @Override
        public void setRate(int value) { rate = value; }
        
        private boolean active;
        
        @Override
        public boolean isActive() { return active; }

        @Override
        public void setActive(boolean value) { active = value; }

        private TestReference refObj;
        
        @Override
        public TestReference getRefObj() { return refObj; }

        @Override
        public void setRefObj(TestReference value) { refObj = value; }
        
    }
    
    public static final ValueAccessor<TestModel, Integer> ACCESOR_RATE = new ValueAccessor<TestModel, Integer>() {
        @Override
        public Integer apply(TestModel t) { return t.getRate(); }
        @Override
        public String get() { return "rate"; }
        @Override
        public void accept(Integer t, ParameterConsumer u) throws SQLException { u.setInt(t); }
    };
    
    public static final ValueAccessor<TestModel, Boolean> ACCESOR_ACTIVE = new ValueAccessor<TestModel, Boolean>() {
        @Override
        public Boolean apply(TestModel t) { return t.isActive(); }
        @Override
        public String get() { return "rate"; }
        @Override
        public void accept(Boolean t, ParameterConsumer u) throws SQLException { u.setBoolean(t); }
    };
    
    public static final ValueAccessor<TestModel, RefChildObject> ACCESOR_REFOBJ = new ValueAccessor<TestModel, RefChildObject>() {
        @Override
        public RefChildObject apply(TestModel t) { return t.getRefObj(); }
        @Override
        public String get() { return "refId"; }
        @Override
        public void accept(RefChildObject t, ParameterConsumer u) throws SQLException { u.setInt(t.getDataObject().getPrimaryKey()); }
    };
    
    public static final ValueAccessor<TestModel, String> ACCESOR_REFOBJ_NAME = new ValueAccessor<TestModel, String>() {
        @Override
        public String apply(TestModel t) {
            RefChildObject obj = t.getRefObj();
            return (obj == null) ? "" : obj.getName();
        }
        @Override
        public String get() { return "refName"; }
        @Override
        public void accept(String t, ParameterConsumer u) throws SQLException { u.setString(t); }
    };
    
    public class TestModel extends ItemModel<TestDataObjectImpl> {

        private final ReadOnlyIntegerWrapper rate;

        public int getRate() { return rate.get(); }

        public ReadOnlyIntegerProperty rateProperty() { return rate.getReadOnlyProperty(); }

        private final ReadOnlyBooleanWrapper active;

        public boolean isActive() { return active.get(); }

        public ReadOnlyBooleanProperty activeProperty() { return active.getReadOnlyProperty(); }

        private final ReadOnlyObjectWrapper<RefChildObject> refObj;

        public RefChildObject getRefObj() { return refObj.get(); }

        public ReadOnlyObjectProperty<RefChildObject> refObjProperty() {
            return refObj.getReadOnlyProperty();
        }
        public TestModel(TestDataObjectImpl dao) {
            super(dao);
            this.rate = new ReadOnlyIntegerWrapper(dao.getRate());
            this.active = new ReadOnlyBooleanWrapper(dao.isActive());
            this.refObj = new ReadOnlyObjectWrapper<>();
        }

        @Override
        public boolean delete(Connection connection) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void saveChanges(Connection connection) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
}
