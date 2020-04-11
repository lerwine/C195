/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao.schema;

import java.util.ArrayList;
import java.util.Collection;
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
public class SchemaHelper_DbColumn_DbColumnTest {
    private String title;
    private final DbColumn a;
    private final DbColumn b;
    private final boolean expResult;
    public SchemaHelper_DbColumn_DbColumnTest(String title, DbColumn a, DbColumn b, boolean expResult) {
        this.title = title;
        this.a = a;
        this.b = b;
        this.expResult = expResult;
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
        for (DbColumn a : DbColumn.values()) {
            for (DbColumn b: DbColumn.values()) {
                if (a == b) {
                    result.add(new Object[] { String.format("%s~%s", a.name(), b.name()), a, b, true });
                } else {
                    switch (a) {
                        case CITY_COUNTRY:
                            result.add(new Object[] { String.format("%s~%s", a.name(), b.name()), a, b, b == DbColumn.COUNTRY_ID });
                            break;
                        case COUNTRY_ID:
                            result.add(new Object[] { String.format("%s~%s", a.name(), b.name()), a, b, b == DbColumn.CITY_COUNTRY });
                            break;
                        case ADDRESS_CITY:
                            result.add(new Object[] { String.format("%s~%s", a.name(), b.name()), a, b, b == DbColumn.CITY_ID });
                            break;
                        case CITY_ID:
                            result.add(new Object[] { String.format("%s~%s", a.name(), b.name()), a, b, b == DbColumn.ADDRESS_CITY });
                            break;
                        case CUSTOMER_ADDRESS:
                            result.add(new Object[] { String.format("%s~%s", a.name(), b.name()), a, b, b == DbColumn.ADDRESS_ID });
                            break;
                        case ADDRESS_ID:
                            result.add(new Object[] { String.format("%s~%s", a.name(), b.name()), a, b, b == DbColumn.CUSTOMER_ADDRESS });
                            break;
                        case APPOINTMENT_CUSTOMER:
                            result.add(new Object[] { String.format("%s~%s", a.name(), b.name()), a, b, b == DbColumn.CUSTOMER_ID });
                            break;
                        case CUSTOMER_ID:
                            result.add(new Object[] { String.format("%s~%s", a.name(), b.name()), a, b, b == DbColumn.APPOINTMENT_CUSTOMER });
                            break;
                        case APPOINTMENT_USER:
                            result.add(new Object[] { String.format("%s~%s", a.name(), b.name()), a, b, b == DbColumn.USER_ID });
                            break;
                        case USER_ID:
                            result.add(new Object[] { String.format("%s~%s", a.name(), b.name()), a, b, b == DbColumn.APPOINTMENT_USER });
                            break;
                        default:
                            result.add(new Object[] { String.format("%s~%s", a.name(), b.name()), a, b, false });
                            break;
                    }
                }
            }
        }
        return result;
    }
    
    /**
     * Test of areColumnsRelated method, of class SchemaHelper.
     */
    @Test
    public void testAreColumnsRelated() {
        boolean result = SchemaHelper.areColumnsRelated(a, b);
        assertEquals(expResult, result);
    }
    
}
