/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.schema;

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
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@RunWith(Parameterized.class)
public class SchemaHelper_DbNameTest {
    private final DbName name;
    
    public SchemaHelper_DbNameTest(DbName name) {
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

    @Parameterized.Parameters(name = "name={0}")
    public static Collection<?> getDbNames() {
        return Arrays.asList(Arrays.stream(DbName.values()).map((t) -> new DbName[] { t }).toArray());
    }
    
    /**
     * Test of toDbName method, of class SchemaHelper.
     */
    @Test
    public void testToDbName() {
        String s = name.toString();
        DbName result = SchemaHelper.toDbName(s);
        assertEquals(name, result);
    }
    
    /**
     * Test of isDbTable method, of class SchemaHelper.
     */
    @Test
    public void testIsDbTable() {
        boolean expResult = Arrays.stream(DbTable.values()).anyMatch((t) -> t.getDbName() == name);
        boolean result = SchemaHelper.isDbTable(name);
        assertEquals(expResult, result);
    }

    /**
     * Test of toDbTable method, of class SchemaHelper.
     */
    @Test
    public void testToDbTable() {
        Optional<DbTable> expResult = Arrays.stream(DbTable.values()).filter((t) -> t.getDbName() == name).findFirst();
        if (expResult.isPresent()) {
            DbTable result = SchemaHelper.toDbTable(name);
            assertEquals(expResult.get(), result);
        } else {
            NoSuchElementException thrown;
            try {
                SchemaHelper.toDbTable(name);
                thrown = null;
            } catch (NoSuchElementException ex) {
                thrown = ex;
            }
            assertNotNull(thrown);
        }
    }

}
