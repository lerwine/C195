/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;
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
public class SchemaHelper_PredicateTest {
    private final Predicate<DbColumn> predicate;
    private final String message;
    
    public SchemaHelper_PredicateTest(Predicate<DbColumn> predicate, String message) {
        this.predicate = predicate;
        this.message = message;
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

    @Parameterized.Parameters
    public static Collection getTestParameterSets() {
        ArrayList<Object[]> result = new ArrayList<>();
        result.add(new Object[] { (Predicate<DbColumn>)(u) -> u.getType() == ColumnType.VARCHAR, "getType() == ColumnType.VARCHAR" });
        result.add(new Object[] { (Predicate<DbColumn>)(u) -> u.getDbName() == DbName.CREATED_BY, "getDbName() == DbName.CREATED_BY" });
        return result;
    }
    
    /**
     * Test of getColumns method, of class SchemaHelper.
     */
    @Test
    public void testGetColumns() {
        DbColumn[] expResult;
        if (null == predicate)
            expResult = DbColumn.values();
        else
            expResult = Arrays.stream(DbColumn.values()).filter(predicate).toArray(DbColumn[]::new);
        DbColumn[] result = SchemaHelper.getColumns(predicate).toArray(DbColumn[]::new);
        assertEquals("count()", expResult.length, result.length);
        for (int i = 0; i < expResult.length; i++) {
            assertEquals(String.format("[index: %d]", i), expResult[i], result[i]);
        }
    }

}
