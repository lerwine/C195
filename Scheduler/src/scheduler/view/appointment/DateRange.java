/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.view.appointment;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.TextField;
import scheduler.observables.BinaryOptionalBinding;
import scheduler.observables.BinarySelectiveBinding;
import scheduler.observables.BindingHelper;
import scheduler.observables.CssClassSwitchBinding;
import scheduler.observables.OptionalBinding;
import scheduler.util.BinaryOptional;
import scheduler.util.BinarySelective;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;

/**
 * FXML Controller class for editing the date range for an {@link AppointmentModel}.
 * <p>
 * This is loaded by the {@link EditAppointment} controller. The associated view is {@code /resources/scheduler/view/appointment/DateRange.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
@FXMLResource("/scheduler/view/appointment/DateRange.fxml")
public final class DateRange {

    private ObservableList<TimeZone> timeZones;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="checkConflictsButton"
    private Button checkConflictsButton; // Value injected by FXMLLoader

    @FXML // fx:id="showConflictsButton"
    private Button showConflictsButton; // Value injected by FXMLLoader

    @FXML // fx:id="startDatePicker"
    private DatePicker startDatePicker; // Value injected by FXMLLoader

    @FXML // fx:id="startValidationLabel"
    private Label startValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="startHourTextField"
    private TextField startHourTextField; // Value injected by FXMLLoader

    @FXML // fx:id="startMinuteTextField"
    private TextField startMinuteTextField; // Value injected by FXMLLoader

    @FXML // fx:id="amPmComboBox"
    private ComboBox<Boolean> amPmComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="durationHourTextField"
    private TextField durationHourTextField; // Value injected by FXMLLoader

    @FXML // fx:id="durationMinuteTextField"
    private TextField durationMinuteTextField; // Value injected by FXMLLoader

    @FXML // fx:id="timeZoneComboBox"
    private ComboBox<TimeZone> timeZoneComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="durationValidationLabel"
    private Label durationValidationLabel; // Value injected by FXMLLoader

    @FXML // fx:id="localTimeLabel"
    private Label localTimeLabel; // Value injected by FXMLLoader

    @FXML // fx:id="localTimeValue"
    private Label localTimeValue; // Value injected by FXMLLoader
    private ObjectBinding<ZonedDateTime> startDateTimeBinding;
    private ObjectBinding<ZonedDateTime> endDateTimeBinding;
    private BooleanBinding valid;
    private ObjectBinding<Duration> durationBinding;

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert checkConflictsButton != null : "fx:id=\"checkConflictsButton\" was not injected: check your FXML file 'temp.fxml'.";
        assert showConflictsButton != null : "fx:id=\"showConflictsButton\" was not injected: check your FXML file 'temp.fxml'.";
        assert startDatePicker != null : "fx:id=\"startDatePicker\" was not injected: check your FXML file 'temp.fxml'.";
        assert startValidationLabel != null : "fx:id=\"startValidationLabel\" was not injected: check your FXML file 'temp.fxml'.";
        assert startHourTextField != null : "fx:id=\"startHourTextField\" was not injected: check your FXML file 'temp.fxml'.";
        assert startMinuteTextField != null : "fx:id=\"startMinuteTextField\" was not injected: check your FXML file 'temp.fxml'.";
        assert amPmComboBox != null : "fx:id=\"amPmComboBox\" was not injected: check your FXML file 'temp.fxml'.";
        assert durationHourTextField != null : "fx:id=\"durationHourTextField\" was not injected: check your FXML file 'temp.fxml'.";
        assert durationMinuteTextField != null : "fx:id=\"durationMinuteTextField\" was not injected: check your FXML file 'temp.fxml'.";
        assert timeZoneComboBox != null : "fx:id=\"timeZoneComboBox\" was not injected: check your FXML file 'temp.fxml'.";
        assert durationValidationLabel != null : "fx:id=\"durationValidationLabel\" was not injected: check your FXML file 'temp.fxml'.";
        assert localTimeLabel != null : "fx:id=\"localTimeLabel\" was not injected: check your FXML file 'temp.fxml'.";
        assert localTimeValue != null : "fx:id=\"localTimeValue\" was not injected: check your FXML file 'temp.fxml'.";

        SingleSelectionModel<TimeZone> timeZoneSelectionModel = timeZoneComboBox.getSelectionModel();
        SingleSelectionModel<Boolean> amPmSelectionModel = amPmComboBox.getSelectionModel();
        timeZones = FXCollections.observableArrayList();
        Arrays.stream(TimeZone.getAvailableIDs()).map((t) -> TimeZone.getTimeZone(t)).sorted((o1, o2) -> {
            return o1.getRawOffset() - o2.getRawOffset();
        }).forEachOrdered((t) -> timeZones.add(t));

        BinaryOptionalBinding<LocalDate, DateTimeException> parseDate = BindingHelper.parseLocalDate(startDatePicker.getEditor().textProperty(),
                startDatePicker.getConverter());
        BinaryOptionalBinding<Integer, ParseException> parseStartHour = BindingHelper.parseInt(startHourTextField.textProperty());
        BinaryOptionalBinding<Integer, ParseException> parseStartMinute = BindingHelper.parseInt(startMinuteTextField.textProperty());
        BinarySelectiveBinding<ZonedDateTime, String> parseStartDateTime = BindingHelper.createBinarySelectiveBinding(() -> {
            BinaryOptional<LocalDate, DateTimeException> p = parseDate.get();
            LocalDate d = startDatePicker.getValue();
            BinaryOptional<Integer, ParseException> hr = parseStartHour.get();
            BinaryOptional<Integer, ParseException> mr = parseStartMinute.get();
            Boolean a = amPmSelectionModel.getSelectedItem();
            TimeZone tz = timeZoneSelectionModel.getSelectedItem();
            if (p.isSecondary()) {
                return BinarySelective.ofSecondary(resources.getString(RESOURCEKEY_INVALIDSTARTDATE));
            }
            if (hr.isSecondary()) {
                return BinarySelective.ofSecondary(resources.getString(RESOURCEKEY_INVALIDHOUR));
            }
            if (mr.isSecondary()) {
                return BinarySelective.ofSecondary(resources.getString(RESOURCEKEY_INVALIDMINUTE));
            }
            if (p.isPresent() && hr.isPresent() && mr.isPresent() && null != d && null != a) {
                int h = hr.getPrimary();
                if (h < 1 || h > 12) {
                    return BinarySelective.ofSecondary(resources.getString(RESOURCEKEY_INVALIDHOUR));
                }
                int m = mr.getPrimary();
                if (m < 0 || m > 59) {
                    return BinarySelective.ofSecondary(resources.getString(RESOURCEKEY_INVALIDMINUTE));
                }
                if (null == tz) {
                    return BinarySelective.ofSecondary(resources.getString(RESOURCEKEY_TIMEZONENOTSPECIFIED));
                }
                return BinarySelective.ofPrimary(ZonedDateTime.of(
                        LocalDateTime.of(
                                d,
                                LocalTime.of((a) ? ((h == 12) ? 12 : h + 12) : ((h == 12) ? 0 : h), m, 0, 0)
                        ),
                        tz.toZoneId()
                ));
            }
            return BinarySelective.ofSecondary(resources.getString(RESOURCEKEY_REQUIRED));
        }, parseDate, startDatePicker.valueProperty(), parseStartHour, parseStartMinute, amPmSelectionModel.selectedItemProperty(),
                timeZoneSelectionModel.selectedItemProperty());

        BinaryOptionalBinding<Integer, ParseException> parseDurationHour = BindingHelper.parseInt(durationHourTextField.textProperty());
        BinaryOptionalBinding<Integer, ParseException> parseDurationMinute = BindingHelper.parseInt(durationMinuteTextField.textProperty());
        BinarySelectiveBinding<Duration, String> parseDuration = BindingHelper.createBinarySelectiveBinding(() -> {
            BinaryOptional<Integer, ParseException> hr = parseDurationHour.get();
            BinaryOptional<Integer, ParseException> mr = parseDurationMinute.get();
            if (hr.isSecondary()) {
                return BinarySelective.ofSecondary(resources.getString(RESOURCEKEY_INVALIDHOUR));
            }
            if (mr.isSecondary()) {
                return BinarySelective.ofSecondary(resources.getString(RESOURCEKEY_INVALIDMINUTE));
            }
            if (hr.isPresent()) {
                int h = hr.getPrimary();
                if (h < 0) {
                    return BinarySelective.ofSecondary(resources.getString(RESOURCEKEY_INVALIDHOUR));
                }
                if (mr.isPresent()) {
                    int m = mr.getPrimary();
                    if (m < 0 || m > 59) {
                        return BinarySelective.ofSecondary(resources.getString(RESOURCEKEY_INVALIDMINUTE));
                    }
                    return BinarySelective.ofPrimary(Duration.ofMinutes((h * 60) + m));
                }
                return BinarySelective.ofPrimary(Duration.ofHours(h));
            }
            if (mr.isPresent()) {
                int m = mr.getPrimary();
                if (m < 0 || m > 59) {
                    return BinarySelective.ofSecondary(resources.getString(RESOURCEKEY_INVALIDMINUTE));
                }
                return BinarySelective.ofPrimary(Duration.ofMinutes(m));
            }
            return BinarySelective.ofSecondary(resources.getString(RESOURCEKEY_REQUIRED));
        }, parseDurationHour, parseDurationMinute);

        StringBinding dateParseErrorMessage = parseStartDateTime.mapToString("", (t) -> t);
        StringBinding durationParseErrorMessage = parseDuration.mapToString("", (t) -> t);
        startDateTimeBinding = parseStartDateTime.map((t) -> t, (ZonedDateTime) null);
        durationBinding = parseDuration.map((t) -> t, (Duration) null);
        endDateTimeBinding = Bindings.createObjectBinding(() -> {
            BinarySelective<ZonedDateTime, String> s = parseStartDateTime.get();
            BinarySelective<Duration, String> d = parseDuration.get();
            if (s.isPrimary() && d.isPrimary()) {
                return s.getPrimary().plus(d.getPrimary());
            }
            return null;
        }, parseStartDateTime, parseDuration);
        startValidationLabel.textProperty().bind(dateParseErrorMessage);
        startValidationLabel.visibleProperty().bind(dateParseErrorMessage.isNotEmpty());
        durationValidationLabel.textProperty().bind(durationParseErrorMessage);
        durationValidationLabel.visibleProperty().bind(durationParseErrorMessage.isNotEmpty());
        valid = endDateTimeBinding.isNotNull();
        CssClassSwitchBinding.collapseIfTrue(localTimeLabel, parseStartDateTime.isPrimary().and(parseDuration.isPrimary()).not());
        CssClassSwitchBinding.collapseIfTrue(localTimeValue, parseStartDateTime.isPrimary().and(parseDuration.isPrimary()).not());

        amPmComboBox.setItems(FXCollections.observableArrayList(false, true));

        timeZoneComboBox.setItems(timeZones);
    }

    boolean isValid() {
        return valid.get();
    }

    BooleanBinding getValid() {
        return valid;
    }

    ZonedDateTime getStartDateTime() {
        return startDateTimeBinding.get();
    }

    ObjectBinding<ZonedDateTime> getStartDateTimeBinding() {
        return startDateTimeBinding;
    }

    Duration getDuration() {
        return durationBinding.get();
    }
    
    ObjectBinding<Duration> getDurationBinding() {
        return durationBinding;
    }

    ZonedDateTime getEndDateTime() {
        return endDateTimeBinding.get();
    }

    ObjectBinding<ZonedDateTime> getEndDateTimeBinding() {
        return endDateTimeBinding;
    }

    void setDateRange(LocalDateTime start, Duration duration, TimeZone timeZone) {
        if (null == start) {
            startDatePicker.getEditor().setText("");
            startHourTextField.setText("");
            startMinuteTextField.setText("");
        } else {
            NumberFormat fmt = NumberFormat.getIntegerInstance();
            startDatePicker.setValue(start.toLocalDate());
            int h = start.getHour();
            if (h < 12) {
                startHourTextField.setText(fmt.format((h == 0) ? 12 : h));
                amPmComboBox.getSelectionModel().select(false);
            } else {
                startHourTextField.setText(fmt.format((h > 12) ? h - 12 : 12));
                amPmComboBox.getSelectionModel().select(true);
            }
            startMinuteTextField.setText(fmt.format(start.getMinute()));
        }
        if (null == duration) {
            durationHourTextField.setText("");
            durationMinuteTextField.setText("");
        } else {
            NumberFormat fmt = NumberFormat.getIntegerInstance();
            long minutes = duration.getSeconds() / 60;
            long hours = minutes / 60;
            minutes -= (hours * 60);
            durationHourTextField.setText(fmt.format(hours));
            startMinuteTextField.setText(fmt.format(minutes));
        }
        if (null == timeZone) {
            timeZoneComboBox.getSelectionModel().clearSelection();
        } else {
            timeZoneComboBox.getSelectionModel().select(timeZone);
        }
        
    }
    
    void setConflictsBinding(OptionalBinding<String> conflictMessageBinding, EventHandler<ActionEvent> checkConflictsListener,
            EventHandler<ActionEvent> showConflictsListener) {
        CssClassSwitchBinding.collapseIfTrue(checkConflictsButton, conflictMessageBinding.isPresent());
        CssClassSwitchBinding.collapseIfTrue(showConflictsButton, conflictMessageBinding.mapToBoolean((s) -> s.isEmpty(), true));
        checkConflictsButton.setOnAction(checkConflictsListener);
        showConflictsButton.setOnAction(showConflictsListener);
    }
    
    void setOnShowConflictsButtonAction(EventHandler<ActionEvent> listener) {
        showConflictsButton.setOnAction(listener);
    }
    
}
