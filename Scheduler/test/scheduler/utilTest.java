package scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.IntSupplier;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import scheduler.util.ValueBindings;
import testHelpers.FakeApp;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class utilTest {
    
    public utilTest() {
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
    
    class TestClassA implements IntSupplier {
        private int value = 0;
        private final Object monitor = new Object();
        synchronized int testA(IntSupplier supplier) {
            return supplier.getAsInt();
        }
        synchronized int testB(int value) {
            return testA(this) + value;
        }

        @Override
        public int getAsInt() {
            synchronized (monitor) {
                return ++value;
            }
        }
    }
    
    /**
     * Test assumed monitor behavior.
     */
    @Test
    public void testMonitorAssertions() {
        int value = 5;
        TestClassA target = new TestClassA();
        int expected = 6;
        int actual = target.testB(value);
        assertEquals(actual, expected);
    }
    
    /**
     * Test of asTrimmedAndNotNull method, of class Bindings.
     */
    @Test
    public void testAsTrimmedAndNotNull() {
        TextField targetControl = new TextField();
        StringBinding targetBinding = ValueBindings.asTrimmedAndNotNull(targetControl.textProperty());

        String inputString = " test\r\n";
        String expResult = "test";
        targetControl.setText(inputString);
        String result = targetBinding.get();
        assertNotNull(result);
        assertEquals(expResult, result);
        
        inputString = "";
        expResult = "";
        targetControl.setText(inputString);
        result = targetBinding.get();
        assertNotNull(result);
        assertEquals(expResult, result);
        
        inputString = null;
        expResult = "";
        targetControl.setText(inputString);
        result = targetBinding.get();
        assertNotNull(result);
        assertEquals(expResult, result);
    }

    /**
     * Test of notNullOrWhiteSpace method, of class Bindings.
     */
    @Test
    public void testNotNullOrWhiteSpace() {
        TextField targetControl = new TextField();
        BooleanBinding targetBinding = ValueBindings.notNullOrWhiteSpace(targetControl.textProperty());
        
        String inputString = "!";
        boolean expResult = true;
        targetControl.setText(inputString);
        boolean result = targetBinding.get();
        assertEquals(expResult, result);
        
        inputString = "\r\n";
        expResult = false;
        targetControl.setText(inputString);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        inputString = "\r\n";
        expResult = false;
        targetControl.setText(inputString);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        inputString = "\r\n";
        expResult = false;
        targetControl.setText(inputString);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        inputString = " test\r\n";
        expResult = true;
        targetControl.setText(inputString);
        result = targetBinding.get();
        assertEquals(expResult, result);
    }

    /**
     * Test of asLocalDateTime method, of class Bindings.
     */
    @Test
    public void testAsLocalDateTime() {
        DatePicker targetDateControl = new DatePicker();
        ComboBox<Integer> targetHourControl = new ComboBox();
        ObservableList<Integer> hourOptions = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);
        targetHourControl.setItems(hourOptions);
        ComboBox<Integer> targetMinuteControl = new ComboBox();
        ObservableList<Integer> minuteOptions = FXCollections.observableArrayList(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55);
        targetMinuteControl.setItems(minuteOptions);
        ObjectBinding<LocalDateTime> targetBinding = ValueBindings.asLocalDateTime(targetDateControl.valueProperty(), targetHourControl.valueProperty(), targetMinuteControl.valueProperty());
        
        LocalDateTime result = targetBinding.get();
        assertNull(result);
        
        int selectedHourIndex = 12;
        int selectedMinuteIndex = 6;
        targetHourControl.getSelectionModel().select(selectedHourIndex);
        targetMinuteControl.getSelectionModel().select(selectedMinuteIndex);
        result = targetBinding.get();
        assertNull(result);
        
        LocalDate selectedDate = LocalDate.now();
        targetDateControl.setValue(selectedDate);
        targetHourControl.getSelectionModel().clearSelection();
        targetMinuteControl.getSelectionModel().clearSelection();
        result = targetBinding.get();
        assertNull(result);
        
        targetHourControl.getSelectionModel().select(selectedHourIndex);
        targetMinuteControl.getSelectionModel().select(selectedMinuteIndex);
        LocalDateTime expResult = LocalDateTime.of(selectedDate, LocalTime.of(hourOptions.get(selectedHourIndex), minuteOptions.get(selectedMinuteIndex), 0, 0));
        result = targetBinding.get();
        assertNotNull(result);
        assertEquals(expResult, result);
        
        selectedHourIndex = 0;
        selectedMinuteIndex = 0;
        targetHourControl.getSelectionModel().select(selectedHourIndex);
        targetMinuteControl.getSelectionModel().select(selectedMinuteIndex);
        expResult = LocalDateTime.of(selectedDate, LocalTime.of(hourOptions.get(selectedHourIndex), minuteOptions.get(selectedMinuteIndex), 0, 0));
        result = targetBinding.get();
        assertNotNull(result);
        assertEquals(expResult, result);
    }

    /**
     * Test of isRangeUndefinedOrValid method, of class Bindings.
     */
    @Test
    public void testIsRangeUndefinedOrValid() {
        ObservableList<Integer> hourOptions = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);
        ObservableList<Integer> minuteOptions = FXCollections.observableArrayList(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55);
        DatePicker targetStartDateControl = new DatePicker();
        ComboBox<Integer> targetStartHourControl = new ComboBox();
        targetStartHourControl.setItems(hourOptions);
        ComboBox<Integer> targetStartMinuteControl = new ComboBox();
        targetStartMinuteControl.setItems(minuteOptions);
        ObjectExpression<LocalDateTime> targetStartExpression = ValueBindings.asLocalDateTime(targetStartDateControl.valueProperty(), targetStartHourControl.valueProperty(), targetStartMinuteControl.valueProperty());
        
        DatePicker targetEndDateControl = new DatePicker();
        ComboBox<Integer> targetEndHourControl = new ComboBox();
        targetEndHourControl.setItems(hourOptions);
        ComboBox<Integer> targetEndMinuteControl = new ComboBox();
        targetEndMinuteControl.setItems(minuteOptions);
        ObjectExpression<LocalDateTime> targetEndExpression = ValueBindings.asLocalDateTime(targetEndDateControl.valueProperty(), targetEndHourControl.valueProperty(), targetEndMinuteControl.valueProperty());
        
        BooleanBinding targetBinding = ValueBindings.isRangeUndefinedOrValid(targetStartExpression, targetEndExpression);
        
        boolean expResult = true;
        boolean result = targetBinding.get();
        assertEquals(expResult, result);
        
        LocalDate selectedStartDate = LocalDate.now();
        int selectedStartHourIndex = 12;
        int selectedStartMinuteIndex = 6;
        expResult = true;
        targetStartDateControl.setValue(selectedStartDate);
        targetStartHourControl.getSelectionModel().select(selectedStartHourIndex);
        targetStartMinuteControl.getSelectionModel().select(selectedStartMinuteIndex);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        LocalDate selectedEndDate = selectedStartDate;
        int selectedEndHourIndex = selectedStartHourIndex;
        int selectedEndMinuteIndex = selectedStartMinuteIndex;
        expResult = true;
        targetEndDateControl.setValue(selectedEndDate);
        targetEndHourControl.getSelectionModel().select(selectedEndHourIndex);
        targetEndMinuteControl.getSelectionModel().select(selectedEndMinuteIndex);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        selectedEndHourIndex = selectedStartHourIndex + 1;
        expResult = true;
        targetEndHourControl.getSelectionModel().select(selectedEndHourIndex);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        selectedEndHourIndex = selectedStartHourIndex - 1;
        expResult = false;
        targetEndHourControl.getSelectionModel().select(selectedEndHourIndex);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        expResult = true;
        targetStartDateControl.setValue(selectedStartDate);
        targetEndHourControl.getSelectionModel().clearSelection();
        result = targetBinding.get();
        assertEquals(expResult, result);
    }

}
