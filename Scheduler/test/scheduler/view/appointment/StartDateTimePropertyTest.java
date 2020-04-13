/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.ObservableList;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import testHelpers.FakeApp;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class StartDateTimePropertyTest {
    
    public StartDateTimePropertyTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        FakeApp.setUp();
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
     * Test of getHour24 method, of class StartDateTimeProperty.
     */
    @Test
    public void testGetHour24() {
        final String hiAM = "पूर्वाह्न";
        final String hiPM = "अपराह्न";
        LocalDate date = LocalDate.of(2020, Month.APRIL, 13);
        LocalTime time = LocalTime.of(13, 57, 23, 683);
        ZoneId zoneId = ZoneId.systemDefault();
        LocalDateTime dateTime = LocalDateTime.of(date, time);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(dateTime, zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("a", Locale.forLanguageTag("es-GT"));
        String result = time.format(formatter);
        String expected = "PM";
        assertEquals(expected, result);
    }
    
}
