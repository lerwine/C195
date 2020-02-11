/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.function.IntSupplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
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
 * @author Leonard T. Erwine
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
        System.out.println("asTrimmedAndNotNull: Initialize target controls for property binding");
        TextField targetControl = new TextField();
        StringBinding targetBinding = ValueBindings.asTrimmedAndNotNull(targetControl.textProperty());

        System.out.println("asTrimmedAndNotNull: A string with extraneous whitespace should produce a string with the extraneous space removed.");
        String inputString = " test\r\n";
        String expResult = "test";
        targetControl.setText(inputString);
        String result = targetBinding.get();
        assertNotNull(result);
        assertEquals(expResult, result);
        
        System.out.println("asTrimmedAndNotNull: Setting property to empty string should produce an empty string");
        inputString = "";
        expResult = "";
        targetControl.setText(inputString);
        result = targetBinding.get();
        assertNotNull(result);
        assertEquals(expResult, result);
        
        System.out.println("asTrimmedAndNotNull: Setting property to null should produce an empty string");
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
        System.out.println("notNullOrWhiteSpace: Initialize target controls for property binding");
        TextField targetControl = new TextField();
        BooleanBinding targetBinding = ValueBindings.notNullOrWhiteSpace(targetControl.textProperty());
        
        System.out.println("notNullOrWhiteSpace: A string with a non-whitespace character should return true.");
        String inputString = "!";
        boolean expResult = true;
        targetControl.setText(inputString);
        boolean result = targetBinding.get();
        assertEquals(expResult, result);
        
        System.out.println("notNullOrWhiteSpace: A string with only line separator characters should return false.");
        inputString = "\r\n";
        expResult = false;
        targetControl.setText(inputString);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        System.out.println("notNullOrWhiteSpace: An empty string should return false.");
        inputString = "\r\n";
        expResult = false;
        targetControl.setText(inputString);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        System.out.println("notNullOrWhiteSpace: A null string should return false");
        inputString = "\r\n";
        expResult = false;
        targetControl.setText(inputString);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        System.out.println("notNullOrWhiteSpace: A string that contains extraneous whitespace should return true if it also contains non-whitespace characters.");
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
        System.out.println("asLocalDateTime: Initialize target controls for property binding");
        DatePicker targetDateControl = new DatePicker();
        ComboBox<Integer> targetHourControl = new ComboBox();
        ObservableList<Integer> hourOptions = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);
        targetHourControl.setItems(hourOptions);
        ComboBox<Integer> targetMinuteControl = new ComboBox();
        ObservableList<Integer> minuteOptions = FXCollections.observableArrayList(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55);
        targetMinuteControl.setItems(minuteOptions);
        ObjectBinding<LocalDateTime> targetBinding = ValueBindings.asLocalDateTime(targetDateControl.valueProperty(), targetHourControl.valueProperty(), targetMinuteControl.valueProperty());
        
        System.out.println("asLocalDateTime: When nothing is selected, a null value should be returned");
        LocalDateTime result = targetBinding.get();
        assertNull(result);
        
        System.out.println("asLocalDateTime: When only hour and minute is selected, a null value should be returned");
        int selectedHourIndex = 12;
        int selectedMinuteIndex = 6;
        targetHourControl.getSelectionModel().select(selectedHourIndex);
        targetMinuteControl.getSelectionModel().select(selectedMinuteIndex);
        result = targetBinding.get();
        assertNull(result);
        
        System.out.println("asLocalDateTime: When only date is selected, a null value should be returned");
        LocalDate selectedDate = LocalDate.now();
        targetDateControl.setValue(selectedDate);
        targetHourControl.getSelectionModel().clearSelection();
        targetMinuteControl.getSelectionModel().clearSelection();
        result = targetBinding.get();
        assertNull(result);
        
        System.out.println("asLocalDateTime: When date, hour and minute selected, a LocalDateTime should be returned");
        targetHourControl.getSelectionModel().select(selectedHourIndex);
        targetMinuteControl.getSelectionModel().select(selectedMinuteIndex);
        LocalDateTime expResult = LocalDateTime.of(selectedDate, LocalTime.of(hourOptions.get(selectedHourIndex), minuteOptions.get(selectedMinuteIndex), 0, 0));
        result = targetBinding.get();
        assertNotNull(result);
        assertEquals(expResult, result);
        
        System.out.println("asLocalDateTime: When date with hour and minute selected as zero values, a LocalDateTime should be returned");
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
        System.out.println("isRangeUndefinedOrValid: Initialize range start target controls for property binding");
        ObservableList<Integer> hourOptions = FXCollections.observableArrayList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23);
        ObservableList<Integer> minuteOptions = FXCollections.observableArrayList(0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55);
        DatePicker targetStartDateControl = new DatePicker();
        ComboBox<Integer> targetStartHourControl = new ComboBox();
        targetStartHourControl.setItems(hourOptions);
        ComboBox<Integer> targetStartMinuteControl = new ComboBox();
        targetStartMinuteControl.setItems(minuteOptions);
        ObjectExpression<LocalDateTime> targetStartExpression = ValueBindings.asLocalDateTime(targetStartDateControl.valueProperty(), targetStartHourControl.valueProperty(), targetStartMinuteControl.valueProperty());
        
        System.out.println("isRangeUndefinedOrValid: Initialize range end target controls for property binding");
        DatePicker targetEndDateControl = new DatePicker();
        ComboBox<Integer> targetEndHourControl = new ComboBox();
        targetEndHourControl.setItems(hourOptions);
        ComboBox<Integer> targetEndMinuteControl = new ComboBox();
        targetEndMinuteControl.setItems(minuteOptions);
        ObjectExpression<LocalDateTime> targetEndExpression = ValueBindings.asLocalDateTime(targetEndDateControl.valueProperty(), targetEndHourControl.valueProperty(), targetEndMinuteControl.valueProperty());
        
        System.out.println("isRangeUndefinedOrValid: Initialize target binding");
        BooleanBinding targetBinding = ValueBindings.isRangeUndefinedOrValid(targetStartExpression, targetEndExpression);
        
        System.out.println("isRangeUndefinedOrValid: Should produce a true value if nothing is selected");
        boolean expResult = true;
        boolean result = targetBinding.get();
        assertEquals(expResult, result);
        
        System.out.println("isRangeUndefinedOrValid: Should produce a true value if only start is selected");
        LocalDate selectedStartDate = LocalDate.now();
        int selectedStartHourIndex = 12;
        int selectedStartMinuteIndex = 6;
        expResult = true;
        targetStartDateControl.setValue(selectedStartDate);
        targetStartHourControl.getSelectionModel().select(selectedStartHourIndex);
        targetStartMinuteControl.getSelectionModel().select(selectedStartMinuteIndex);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        System.out.println("isRangeUndefinedOrValid: Should produce a true value if start and is selected and start == end");
        LocalDate selectedEndDate = selectedStartDate;
        int selectedEndHourIndex = selectedStartHourIndex;
        int selectedEndMinuteIndex = selectedStartMinuteIndex;
        expResult = true;
        targetEndDateControl.setValue(selectedEndDate);
        targetEndHourControl.getSelectionModel().select(selectedEndHourIndex);
        targetEndMinuteControl.getSelectionModel().select(selectedEndMinuteIndex);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        System.out.println("isRangeUndefinedOrValid: Should produce a true value if start and is selected and start < end");
        selectedEndHourIndex = selectedStartHourIndex + 1;
        expResult = true;
        targetEndHourControl.getSelectionModel().select(selectedEndHourIndex);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        System.out.println("isRangeUndefinedOrValid: Should produce a false value if start and is selected and start > end");
        selectedEndHourIndex = selectedStartHourIndex - 1;
        expResult = false;
        targetEndHourControl.getSelectionModel().select(selectedEndHourIndex);
        result = targetBinding.get();
        assertEquals(expResult, result);
        
        System.out.println("isRangeUndefinedOrValid: Should produce a true value if only end is selected");
        expResult = true;
        targetStartDateControl.setValue(selectedStartDate);
        targetEndHourControl.getSelectionModel().clearSelection();
        result = targetBinding.get();
        assertEquals(expResult, result);
    }

}
