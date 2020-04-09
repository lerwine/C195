/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.schema;

import java.util.ArrayList;
import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

/**
 *SchemaHelperTest_DbColumn_DbColumn
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
@RunWith(Parameterized.class)
public class SchemaHelper_DbColumn_DbNameTest {
    private final DbColumn column;
    private final DbName foreignKeyColName;
    private final Optional<DbTable> referencedTable;
    private final Optional<DbTable> referringTable;
    
    @Rule
    public ExpectedException thrown  = ExpectedException.none();
    
    public SchemaHelper_DbColumn_DbNameTest(DbColumn column, DbName foreignKeyColName, Optional<DbTable> referencedTable, Optional<DbTable> referringTable) {
        this.column = column;
        this.foreignKeyColName = foreignKeyColName;
        this.referencedTable = referencedTable;
        this.referringTable = referringTable;
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

    @Parameterized.Parameters(name = "column: {0}, foreignKeyColName: {1}")
    public static Collection getTestParameterSets() {
        ArrayList<Object[]> result = new ArrayList<>();
        for (DbColumn t : DbColumn.values()) {
            for (DbName n: DbName.values()) {
                switch (t) {
                    case CITY_COUNTRY:
                        if (n == DbName.COUNTRY_ID) {
                            result.add(new Object[] { t, n, Optional.of(DbTable.COUNTRY), Optional.empty() });
                        } else {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.empty() });
                        }
                        break;
                    case COUNTRY_ID:
                        if (n == DbName.COUNTRY_ID) {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.of(DbTable.CITY) });
                        } else {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.empty() });
                        }
                        break;
                    case ADDRESS_CITY:
                        if (n == DbName.CITY_ID) {
                            result.add(new Object[] { t, n, Optional.of(DbTable.CITY),  Optional.empty() });
                        } else {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.empty() });
                        }
                        break;
                    case CITY_ID:
                        if (n == DbName.CITY_ID) {
                            result.add(new Object[] { t, n,  Optional.empty(), Optional.of(DbTable.ADDRESS) });
                        } else {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.empty() });
                        }
                        break;
                    case CUSTOMER_ADDRESS:
                        if (n == DbName.ADDRESS_ID) {
                            result.add(new Object[] { t, n, Optional.of(DbTable.ADDRESS), Optional.empty() });
                        } else {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.empty() });
                        }
                        break;
                    case ADDRESS_ID:
                        if (n == DbName.ADDRESS_ID) {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.of(DbTable.CUSTOMER) });
                        } else {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.empty() });
                        }
                        break;
                    case APPOINTMENT_CUSTOMER:
                        if (n == DbName.CUSTOMER_ID) {
                            result.add(new Object[] { t, n, Optional.of(DbTable.CUSTOMER), Optional.empty() });
                        } else {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.empty() });
                        }
                        break;
                    case CUSTOMER_ID:
                        if (n == DbName.CUSTOMER_ID) {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.of(DbTable.APPOINTMENT) });
                        } else {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.empty() });
                        }
                        break;
                    case APPOINTMENT_USER:
                        if (n == DbName.USER_ID) {
                            result.add(new Object[] { t, n, Optional.of(DbTable.USER), Optional.empty() });
                        } else {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.empty() });
                        }
                        break;
                    case USER_ID:
                        if (n == DbName.USER_ID) {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.of(DbTable.APPOINTMENT) });
                        } else {
                            result.add(new Object[] { t, n, Optional.empty(), Optional.empty() });
                        }
                        break;
                    default:
                        result.add(new Object[] { t, n, Optional.empty(), Optional.empty() });
                        break;
                }
            }
        }
        return result;
    }
    
    /**
     * Test of getReferencedTable method, of class SchemaHelper.
     */
    @Test
    public void testGetReferencedTable() {
        if (referencedTable.isPresent()) {
            DbTable result = SchemaHelper.getReferencedTable(column, foreignKeyColName);
            assertEquals(column.name(), referencedTable.get(), result);
        } else {
            thrown.expect(NoSuchElementException.class);
            SchemaHelper.getReferencedTable(column, foreignKeyColName);
        }
    }

    /**
     * Test of getReferringTable method, of class SchemaHelper.
     */
    @Test
    public void testGetReferringTable() {
        if (referringTable.isPresent()) {
            DbTable result = SchemaHelper.getReferringTable(column, foreignKeyColName);
            assertEquals(column.name(), referringTable.get(), result);
        } else {
            thrown.expect(NoSuchElementException.class);
            SchemaHelper.getReferringTable(column, foreignKeyColName);
        }
    }

}
