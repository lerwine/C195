/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.schema;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
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
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
@RunWith(Parameterized.class)
public class SchemaHelper_DbTable_DbNameTest {
    private final DbTable table;
    private final DbName name;
    
    public SchemaHelper_DbTable_DbNameTest(DbTable table, DbName name) {
        this.table = table;
        this.name = name;
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

    @Parameterized.Parameters(name = "table: {0}, name: {1}")
    public static Collection getTestParameterSets() {
        ArrayList<Object[]> result = new ArrayList<>();
        for (DbTable t : DbTable.values()) {
            for (DbName n: DbName.values()) {
                result.add(new Object[] { t, n });
            }
        }
        return result;
    }
    
    /**
     * Test of getDbColumn method, of class SchemaHelper.
     */
    @Test
    public void testGetDbColumn() {
        Optional<DbColumn> expResult = Arrays.stream(DbColumn.values())
                .filter((t) -> t.getTable() == table && t.getDbName() == name).findFirst();
        String message = String.format("table: %s, name: %s", ReflectionHelper.toJavaLiteral(table),
                ReflectionHelper.toJavaLiteral(name));
        if (expResult.isPresent()) {
            DbColumn result = SchemaHelper.getDbColumn(table, name);
            assertEquals(message, expResult.get(), result);
        } else {
            NoSuchElementException thrown;
            try {
                SchemaHelper.getDbColumn(table, name);
                thrown = null;
            } catch (NoSuchElementException ex) {
                thrown = ex;
            }
            assertNotNull(message, thrown);
        }
    }

}
