/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.schema;

import java.util.Arrays;
import java.util.Collection;
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
 * @author lerwi
 */
@RunWith(Parameterized.class)
public class SchemaHelper_DbColumnTest {
    private final DbColumn column;
    
    public SchemaHelper_DbColumnTest(DbColumn column) {
        this.column = column;
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

    @Parameterized.Parameters(name = "column: {0}")
    public static Collection getTestParameterSets() {
        return Arrays.asList(Arrays.stream(DbColumn.values()).map((t) -> new DbColumn[] { t }).toArray());
    }
    
    /**
     * Test of isEntityData method, of class SchemaHelper.
     */
    @Test
    public void testIsEntityData() {
        boolean expResult;
        switch (column.getUsageCategory()) {
            case AUDIT:
            case FOREIGN_KEY:
            case PRIMARY_KEY:
                expResult = false;
                break;
            default:
                expResult = true;
                break;
        }
        boolean result = SchemaHelper.isEntityData(column);
        assertEquals(expResult, result);
    }

    /**
     * Test of isEntityData method, of class SchemaHelper.
     */
    @Test
    public void testIsEntityDataOrRef() {
        boolean expResult;
        switch (column.getUsageCategory()) {
            case AUDIT:
            case PRIMARY_KEY:
            case CRYPTO_HASH:
                expResult = false;
                break;
            default:
                expResult = true;
                break;
        }
        boolean result = SchemaHelper.isForJoinedData(column);
        assertEquals(expResult, result);
    }

    /**
     * Test of getReferencedColumns method, of class SchemaHelper.
     */
    @Test
    public void testGetReferencedColumns() {
        DbColumn[] expResult = column.getForeignKeys().stream().map((t) -> {
            DbTable table = t.getTable();
            DbName name = t.getColumnName();
            return Arrays.stream(DbColumn.values()).filter((u) -> u.getTable() == table && u.getDbName() == name)
                    .findFirst().get();
        }).toArray(DbColumn[]::new);
        DbColumn[] result = SchemaHelper.getReferencedColumns(column).toArray(new DbColumn[0]);
        assertEquals("size()", expResult.length, result.length);
        for (int i = 0; i < expResult.length; i++) {
            assertEquals(String.format("[index: %d]", i), expResult[i], result[i]);
        }
    }

    /**
     * Test of getReferencingColumns method, of class SchemaHelper.
     */
    @Test
    public void testGetReferencingColumns() {
        DbTable table = column.getTable();
        DbName name = column.getDbName();
        DbColumn[] expResult = Arrays.stream(DbColumn.values())
                .filter((t) -> t.getForeignKeys().stream().anyMatch((u) -> u.getTable() == table && u.getColumnName() == name))
                .toArray(DbColumn[]::new);
        DbColumn[] result = SchemaHelper.getReferencingColumns(column).toArray(new DbColumn[0]);
        assertEquals(String.format("(column: %s).size()", ReflectionHelper.toJavaLiteral(column)), expResult.length,
                result.length);
        for (int i = 0; i < expResult.length; i++) {
            assertEquals(String.format("(column: %s)[index: %d]", ReflectionHelper.toJavaLiteral(column), i), expResult[i],
                    result[i]);
        }
    }

}
