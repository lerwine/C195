package scheduler.dao.dml;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import scheduler.dao.schema.DbColumn;
import scheduler.dao.schema.DbTable;
import testHelpers.ReflectionHelper;

/**
 *
 * @author lerwi
 */
@RunWith(Parameterized.class)
public class DmlTable_DbTable_AliasTest {
    private final String title;
    private final DbTable table;
    private final Optional<String> alias;
    private final String expAlias;
    private final DbColumn[] expAllTableColumns;
    private final DbColumn[] expFilteredTableColumns;
    
    public DmlTable_DbTable_AliasTest(String title, DbTable table, Optional<String> alias, DbColumn[] expAllTableColumns, DbColumn[] expFilteredTableColumns) {
        this.title = title;
        this.table = table;
        this.alias = alias;
        this.expAllTableColumns = expAllTableColumns;
        this.expFilteredTableColumns = expFilteredTableColumns;
        if (alias.isPresent()) {
            String s = alias.get();
            expAlias = (null == s) ? table.toString() : ((s.trim().isEmpty()) ? table.getDbName().toString() : s);
        } else {
            expAlias = table.toString();
        }
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
        ArrayList<Object[]> result = new ArrayList<>();
        Object[][] items =  new Object[][] {
                new Object[] {
                    DbTable.ADDRESS.name(), DbTable.ADDRESS, Optional.empty(),
                    new DbColumn[] {
                        DbColumn.ADDRESS1, DbColumn.ADDRESS2, DbColumn.ADDRESS_CITY, DbColumn.POSTAL_CODE, DbColumn.PHONE, DbColumn.ADDRESS_ID,
                        DbColumn.ADDRESS_CREATE_DATE, DbColumn.ADDRESS_CREATED_BY, DbColumn.ADDRESS_LAST_UPDATE, DbColumn.ADDRESS_LAST_UPDATE_BY
                    },
                    new DbColumn[] {
                        DbColumn.ADDRESS1, DbColumn.ADDRESS2, DbColumn.POSTAL_CODE, DbColumn.PHONE
                    }
                }, new Object[] {
                    DbTable.APPOINTMENT.name(), DbTable.APPOINTMENT, Optional.empty(),
                    new DbColumn[] {
                        DbColumn.APPOINTMENT_CUSTOMER, DbColumn.APPOINTMENT_USER, DbColumn.TITLE, DbColumn.DESCRIPTION, DbColumn.LOCATION,
                        DbColumn.CONTACT, DbColumn.TYPE, DbColumn.URL, DbColumn.START, DbColumn.END, DbColumn.APPOINTMENT_ID,
                        DbColumn.APPOINTMENT_CREATE_DATE, DbColumn.APPOINTMENT_CREATED_BY, DbColumn.APPOINTMENT_LAST_UPDATE,
                        DbColumn.APPOINTMENT_LAST_UPDATE_BY
                    },
                    new DbColumn[] {
                        DbColumn.TITLE, DbColumn.DESCRIPTION, DbColumn.LOCATION, DbColumn.CONTACT, DbColumn.TYPE, DbColumn.URL, DbColumn.START,
                        DbColumn.END
                    }
                }, new Object[] {
                    DbTable.CITY.name(), DbTable.CITY, Optional.empty(),
                    new DbColumn[] {
                        DbColumn.CITY_NAME, DbColumn.CITY_COUNTRY, DbColumn.CITY_ID, DbColumn.CITY_CREATE_DATE, DbColumn.CITY_CREATED_BY,
                        DbColumn.CITY_LAST_UPDATE, DbColumn.CITY_LAST_UPDATE_BY
                    },
                    new DbColumn[] {
                        DbColumn.CITY_NAME
                    }
                }, new Object[] {
                    DbTable.COUNTRY.name(), DbTable.COUNTRY, Optional.empty(),
                    new DbColumn[] {
                        DbColumn.COUNTRY_NAME, DbColumn.COUNTRY_ID, DbColumn.COUNTRY_CREATE_DATE, DbColumn.COUNTRY_CREATED_BY,
                        DbColumn.COUNTRY_LAST_UPDATE, DbColumn.COUNTRY_LAST_UPDATE_BY
                    },
                    new DbColumn[] {
                        DbColumn.COUNTRY_NAME
                    }
                }, new Object[] {
                    DbTable.CUSTOMER.name(), DbTable.CUSTOMER, Optional.empty(),
                    new DbColumn[] {
                        DbColumn.CUSTOMER_NAME, DbColumn.CUSTOMER_ADDRESS, DbColumn.ACTIVE, DbColumn.CUSTOMER_ID, DbColumn.CUSTOMER_CREATE_DATE,
                        DbColumn.CUSTOMER_CREATED_BY, DbColumn.CUSTOMER_LAST_UPDATE, DbColumn.CUSTOMER_LAST_UPDATE_BY
                    },
                    new DbColumn[] {
                        DbColumn.CUSTOMER_NAME, DbColumn.ACTIVE
                    }
                }, new Object[] {
                    DbTable.USER.name(), DbTable.USER, Optional.empty(),
                    new DbColumn[] {
                        DbColumn.USER_NAME, DbColumn.PASSWORD, DbColumn.STATUS, DbColumn.USER_ID, DbColumn.USER_CREATE_DATE,
                        DbColumn.USER_CREATED_BY, DbColumn.USER_LAST_UPDATE, DbColumn.USER_LAST_UPDATE_BY
                    },
                    new DbColumn[] {
                        DbColumn.USER_NAME, DbColumn.PASSWORD, DbColumn.STATUS
                    }
                }
        };
        for (Object[] a : items) {
            result.add(a);
            result.add(new Object[] { String.format("%s, alias=null", a), a[1], Optional.ofNullable(null), a[3], a[4] });
            result.add(new Object[] { String.format("%s, alias=\"\"", a), a[1], Optional.of(""), a[3], a[4] });
            result.add(new Object[] { String.format("%s, alias=\"test\"", a), a[1], Optional.of("test"), a[3], a[4] });
        }
        return result;
    }
    
    /**
     * Test of builder method, of class DmlTable.
     */
    @Test
    public void testBuilder_Predicate_Function1() {
        Predicate<DbColumn> columnSelector = null;
        Function<DbColumn, String> aliasMapper = null;
        DmlTable.Builder result;
        if (alias.isPresent()) {
            result = DmlTable.builder(table, alias.get(), columnSelector, aliasMapper);
        } else {
            result = DmlTable.builder(table, columnSelector, aliasMapper);
        }
        assertEquals("getTable()", table, result.getTable());
        assertEquals("getAlias()", expAlias, result.getAlias());
        assertEquals("getAllColumns().size()", expAllTableColumns.length, result.getAllColumns().size());
        assertEquals("getColumns().size()", expAllTableColumns.length, result.getColumns().size());
        Iterator<DmlTable.BuilderColumn> resultColumns = result.getColumns().iterator();
        int index = -1;
        for (DbColumn expColumn : expAllTableColumns) {
            DmlTable.BuilderColumn actualColumn = resultColumns.next();
            assertEquals(String.format("get(%s).getColumn()", ++index),
                    expColumn, actualColumn.getColumn());
            String a = expColumn.toString();
            assertEquals(String.format("get(%s).getAlias()", index),
                    a, actualColumn.getAlias());
            assertTrue(String.format("containsKey(%s)", ReflectionHelper.toJavaLiteral(a)),
                    result.getAllColumns().containsKey(a));
            assertEquals(String.format("get(%s)", ReflectionHelper.toJavaLiteral(a)),
                    actualColumn, result.getAllColumns().get(a));
            assertEquals(String.format("get(%s).getOwner()", index),
                    result, actualColumn.getOwner());
            assertNull(String.format("get(%s).getParentJoin()", index),
                    actualColumn.getParentJoin());
            assertTrue(String.format("get(%s).getChildJoins()", index),
                    actualColumn.getChildJoins().isEmpty());
        }
        assertEquals("getAllTables().size()", 1, result.getAllTables().size());
        assertTrue(String.format("getAllTables().containsKey(%s)", ReflectionHelper.toJavaLiteral(expAlias)),
                result.getAllTables().containsKey(expAlias));
        assertEquals(String.format("getAllTables().get(%s)", ReflectionHelper.toJavaLiteral(expAlias)),
                result, result.getAllTables().get(expAlias));
        assertTrue("getJoinedTables().isEmpty()", result.getJoinedTables().isEmpty());
    }

    /**
     * Test of builder method, of class DmlTable.
     */
    @Test
    public void testBuilder_Predicate1() {
        Predicate<DbColumn> columnSelector = null;
        DmlTable.Builder result;
        if (alias.isPresent()) {
            result = DmlTable.builder(table, alias.get(), columnSelector);
        } else {
            result = DmlTable.builder(table, columnSelector);
        }
        assertEquals("getTable()", table, result.getTable());
        assertEquals("getAlias()", expAlias, result.getAlias());
        assertEquals("getAllColumns().size()", expAllTableColumns.length, result.getAllColumns().size());
        assertEquals("getColumns().size()", expAllTableColumns.length, result.getColumns().size());
        Iterator<DmlTable.BuilderColumn> allColumns = result.getColumns().iterator();
        int index = -1;
        for (DbColumn expColumn : expAllTableColumns) {
            DmlTable.BuilderColumn actualColumn = allColumns.next();
            assertEquals(String.format("get(%s).getColumn()", ++index),
                    expColumn, actualColumn.getColumn());
            String a = expColumn.toString();
            assertEquals(String.format("get(%s).getAlias()", index),
                    a, actualColumn.getAlias());
            assertTrue(String.format("containsKey(%s)", ReflectionHelper.toJavaLiteral(a)),
                    result.getAllColumns().containsKey(a));
            assertEquals(String.format("get(%s)", ReflectionHelper.toJavaLiteral(a)),
                    actualColumn, result.getAllColumns().get(a));
            assertEquals(String.format("get(%s).getOwner()", index),
                    result, actualColumn.getOwner());
            assertNull(String.format("get(%s).getParentJoin()", index),
                    actualColumn.getParentJoin());
            assertTrue(String.format("get(%s).getChildJoins()", index),
                    actualColumn.getChildJoins().isEmpty());
        }
        assertEquals("getAllTables().size()", 1, result.getAllTables().size());
        assertTrue(String.format("getAllTables().containsKey(%s)", ReflectionHelper.toJavaLiteral(expAlias)),
                result.getAllTables().containsKey(expAlias));
        assertEquals(String.format("getAllTables().get(%s)", ReflectionHelper.toJavaLiteral(expAlias)),
                result, result.getAllTables().get(expAlias));
        assertTrue("getJoinedTables().isEmpty()", result.getJoinedTables().isEmpty());
    }

    /**
     * Test of builder method, of class DmlTable.
     */
    @Test
    public void testBuilder_Function1() {
        Function<DbColumn, String> aliasMapper = null;
        DmlTable.Builder result;
        if (alias.isPresent()) {
            result = DmlTable.builder(table, alias.get(), aliasMapper);
        } else {
            result = DmlTable.builder(table, aliasMapper);
        }
        assertEquals("getTable()", table, result.getTable());
        assertEquals("getAlias()", expAlias, result.getAlias());
        assertEquals("getAllColumns().size()", expAllTableColumns.length, result.getAllColumns().size());
        assertEquals("getColumns().size()", expAllTableColumns.length, result.getColumns().size());
        Iterator<DmlTable.BuilderColumn> allColumns = result.getColumns().iterator();
        int index = -1;
        for (DbColumn expColumn : expAllTableColumns) {
            DmlTable.BuilderColumn actualColumn = allColumns.next();
            assertEquals(String.format("get(%s).getColumn()", ++index),
                    expColumn, actualColumn.getColumn());
            String a = expColumn.toString();
            assertEquals(String.format("get(%s).getAlias()", index),
                    a, actualColumn.getAlias());
            assertTrue(String.format("containsKey(%s)", ReflectionHelper.toJavaLiteral(a)),
                    result.getAllColumns().containsKey(a));
            assertEquals(String.format("get(%s)", ReflectionHelper.toJavaLiteral(a)),
                    actualColumn, result.getAllColumns().get(a));
            assertEquals(String.format("get(%s).getOwner()", index),
                    result, actualColumn.getOwner());
            assertNull(String.format("get(%s).getParentJoin()", index),
                    actualColumn.getParentJoin());
            assertTrue(String.format("get(%s).getChildJoins()", index),
                    actualColumn.getChildJoins().isEmpty());
        }
        assertEquals("getAllTables().size()", 1, result.getAllTables().size());
        assertTrue(String.format("getAllTables().containsKey(%s)", ReflectionHelper.toJavaLiteral(expAlias)),
                result.getAllTables().containsKey(expAlias));
        assertEquals(String.format("getAllTables().get(%s)", ReflectionHelper.toJavaLiteral(expAlias)),
                result, result.getAllTables().get(expAlias));
        assertTrue("getJoinedTables().isEmpty()", result.getJoinedTables().isEmpty());
    }

    /**
     * Test of builder method, of class DmlTable.
     */
    @Test
    public void testBuilder_True() {
        DmlTable.Builder result;
        if (alias.isPresent()) {
            result = DmlTable.builder(table, alias.get(), true);
        } else {
            result = DmlTable.builder(table, true);
        }
        assertEquals("getTable()", table, result.getTable());
        assertEquals("getAlias()", expAlias, result.getAlias());
        assertEquals("getAllColumns().size()", 0, result.getAllColumns().size());
        assertEquals("getColumns().size()", 0, result.getColumns().size());
        assertEquals("getAllTables().size()", 1, result.getAllTables().size());
        assertTrue(String.format("getAllTables().containsKey(%s)", ReflectionHelper.toJavaLiteral(expAlias)),
                result.getAllTables().containsKey(expAlias));
        assertEquals(String.format("getAllTables().get(%s)", ReflectionHelper.toJavaLiteral(expAlias)),
                result, result.getAllTables().get(expAlias));
        assertTrue("getJoinedTables().isEmpty()", result.getJoinedTables().isEmpty());
    }

    /**
     * Test of builder method, of class DmlTable.
     */
    @Test
    public void testBuilder_False() {
        DmlTable.Builder result;
        if (alias.isPresent()) {
            result = DmlTable.builder(table, alias.get(), false);
        } else {
            result = DmlTable.builder(table, false);
        }
        assertEquals("getTable()", table, result.getTable());
        assertEquals("getAlias()", expAlias, result.getAlias());
        assertEquals("getAllColumns().size()", expAllTableColumns.length, result.getAllColumns().size());
        assertEquals("getColumns().size()", expAllTableColumns.length, result.getColumns().size());
        Iterator<DmlTable.BuilderColumn> allColumns = result.getColumns().iterator();
        int index = -1;
        for (DbColumn expColumn : expAllTableColumns) {
            DmlTable.BuilderColumn actualColumn = allColumns.next();
            assertEquals(String.format("get(%s).getColumn()", ++index),
                    expColumn, actualColumn.getColumn());
            String a = expColumn.toString();
            assertEquals(String.format("get(%s).getAlias()", index),
                    a, actualColumn.getAlias());
            assertTrue(String.format("containsKey(%s)", ReflectionHelper.toJavaLiteral(a)),
                    result.getAllColumns().containsKey(a));
            assertEquals(String.format("get(%s)", ReflectionHelper.toJavaLiteral(a)),
                    actualColumn, result.getAllColumns().get(a));
            assertEquals(String.format("get(%s).getOwner()", index),
                    result, actualColumn.getOwner());
            assertNull(String.format("get(%s).getParentJoin()", index),
                    actualColumn.getParentJoin());
            assertTrue(String.format("get(%s).getChildJoins()", index),
                    actualColumn.getChildJoins().isEmpty());
        }
        assertEquals("getAllTables().size()", 1, result.getAllTables().size());
        assertTrue(String.format("getAllTables().containsKey(%s)", ReflectionHelper.toJavaLiteral(expAlias)),
                result.getAllTables().containsKey(expAlias));
        assertEquals(String.format("getAllTables().get(%s)", ReflectionHelper.toJavaLiteral(expAlias)),
                result, result.getAllTables().get(expAlias));
        assertTrue("getJoinedTables().isEmpty()", result.getJoinedTables().isEmpty());
    }

    /**
     * Test of builder method, of class DmlTable.
     */
    @Test
    public void testBuilder_DbTable() {
        DmlTable.Builder result;
        if (alias.isPresent()) {
            result = DmlTable.builder(table, alias.get());
        } else {
            result = DmlTable.builder(table);
        }
        assertEquals("getTable()", table, result.getTable());
        assertEquals("getAlias()", expAlias, result.getAlias());
        assertEquals("getAllColumns().size()", expAllTableColumns.length, result.getAllColumns().size());
        assertEquals("getColumns().size()", expAllTableColumns.length, result.getColumns().size());
        Iterator<DmlTable.BuilderColumn> allColumns = result.getColumns().iterator();
        int index = -1;
        for (DbColumn expColumn : expAllTableColumns) {
            DmlTable.BuilderColumn actualColumn = allColumns.next();
            assertEquals(String.format("get(%s).getColumn()", ++index),
                    expColumn, actualColumn.getColumn());
            String a = expColumn.toString();
            assertEquals(String.format("get(%s).getAlias()", index),
                    a, actualColumn.getAlias());
            assertTrue(String.format("containsKey(%s)", ReflectionHelper.toJavaLiteral(a)),
                    result.getAllColumns().containsKey(a));
            assertEquals(String.format("get(%s)", ReflectionHelper.toJavaLiteral(a)),
                    actualColumn, result.getAllColumns().get(a));
            assertEquals(String.format("get(%s).getOwner()", index),
                    result, actualColumn.getOwner());
            assertNull(String.format("get(%s).getParentJoin()", index),
                    actualColumn.getParentJoin());
            assertTrue(String.format("get(%s).getChildJoins()", index),
                    actualColumn.getChildJoins().isEmpty());
        }
        assertEquals("getAllTables().size()", 1, result.getAllTables().size());
        assertTrue(String.format("getAllTables().containsKey(%s)", ReflectionHelper.toJavaLiteral(expAlias)),
                result.getAllTables().containsKey(expAlias));
        assertEquals(String.format("getAllTables().get(%s)", ReflectionHelper.toJavaLiteral(expAlias)),
                result, result.getAllTables().get(expAlias));
        assertTrue("getJoinedTables().isEmpty()", result.getJoinedTables().isEmpty());
    }
    
}
