package scheduler.view.appointment.edit;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import scheduler.fx.CssClassName;
import scheduler.observables.BinaryOptionalBinding;
import scheduler.observables.BindingHelper;
import scheduler.util.BinaryOptional;
import static scheduler.util.NodeUtil.addCssClass;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.removeCssClass;
import static scheduler.util.NodeUtil.restoreLabeled;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;

/**
 * FXML Controller class for editing the date range for an {@link scheduler.model.ui.AppointmentModel}. This is loaded by the {@link EditAppointment} controller.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
@FXMLResource("/scheduler/view/appointment/edit/DateRange.fxml")
public final class DateRangeControl extends GridPane {

    private static final Pattern INT_PATTERN = Pattern.compile("^\\s*\\d{1,9}\\s*");
    private static final String INVALID_NUMBER = "Invalid number";

    private static final Logger LOG = Logger.getLogger(DateRangeControl.class.getName());

    private static int tryParseInteger(String s, int max) {
        NumberFormat fmt = NumberFormat.getIntegerInstance();
        String v;
        if (null == s || (v = s.trim()).isEmpty()) {
            return -1;
        }

        try {
            Matcher m = INT_PATTERN.matcher(v);
            if (!m.find()) {
                throw new ParseException(INVALID_NUMBER, s.length() - v.length());
            } else if (m.end() < v.length()) {
                throw new ParseException(INVALID_NUMBER, m.end() + (s.length() - v.length()));
            }
            int i = fmt.parse(v).intValue();
            return (i < 0 || (max > 0 && i > max)) ? -2 : i;
        } catch (ParseException ex) {
            return -2;
        }
    }

    private final ObservableList<TimeZone> timeZones;
    private final SimpleStringProperty conflictMessage;
    private final SimpleObjectProperty<ConflictCheckStatus> conflictCheckStatus;
    private final ReadOnlyObjectWrapper<ZonedAppointmentTimeSpan> timeSpan;
    private BinaryOptionalBinding<ZonedDateAndTimeSelection, String> parseStartBinding;
    private BinaryOptionalBinding<AppointmentDuration, String> parseDurationBinding;
    private StringBinding startParseMessage;
    private StringBinding durationParseMessage;
    private StringBinding startMessage;

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

    @SuppressWarnings("LeakingThisInConstructor")
    public DateRangeControl() {
        conflictMessage = new SimpleStringProperty("");
        conflictCheckStatus = new SimpleObjectProperty<>(ConflictCheckStatus.NOT_CHECKED);
        timeSpan = new ReadOnlyObjectWrapper<>();
        timeZones = FXCollections.observableArrayList();
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error loading view", ex);
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        LOG.fine(() -> "Initializing");
        assert checkConflictsButton != null : "fx:id=\"checkConflictsButton\" was not injected: check your FXML file 'DateRange.fxml'.";
        assert showConflictsButton != null : "fx:id=\"showConflictsButton\" was not injected: check your FXML file 'DateRange.fxml'.";
        assert startDatePicker != null : "fx:id=\"startDatePicker\" was not injected: check your FXML file 'DateRange.fxml'.";
        assert startValidationLabel != null : "fx:id=\"startValidationLabel\" was not injected: check your FXML file 'DateRange.fxml'.";
        assert startHourTextField != null : "fx:id=\"startHourTextField\" was not injected: check your FXML file 'DateRange.fxml'.";
        assert startMinuteTextField != null : "fx:id=\"startMinuteTextField\" was not injected: check your FXML file 'DateRange.fxml'.";
        assert amPmComboBox != null : "fx:id=\"amPmComboBox\" was not injected: check your FXML file 'DateRange.fxml'.";
        assert durationHourTextField != null : "fx:id=\"durationHourTextField\" was not injected: check your FXML file 'DateRange.fxml'.";
        assert durationMinuteTextField != null : "fx:id=\"durationMinuteTextField\" was not injected: check your FXML file 'DateRange.fxml'.";
        assert timeZoneComboBox != null : "fx:id=\"timeZoneComboBox\" was not injected: check your FXML file 'DateRange.fxml'.";
        assert durationValidationLabel != null : "fx:id=\"durationValidationLabel\" was not injected: check your FXML file 'DateRange.fxml'.";
        assert localTimeLabel != null : "fx:id=\"localTimeLabel\" was not injected: check your FXML file 'DateRange.fxml'.";
        assert localTimeValue != null : "fx:id=\"localTimeValue\" was not injected: check your FXML file 'DateRange.fxml'.";

        Arrays.stream(TimeZone.getAvailableIDs()).map((t) -> TimeZone.getTimeZone(t)).sorted((o1, o2) -> {
            return o1.getRawOffset() - o2.getRawOffset();
        }).forEachOrdered((t) -> timeZones.add(t));

        amPmComboBox.setItems(FXCollections.observableArrayList(false, true));

        timeZoneComboBox.setItems(timeZones);

        startDatePicker.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            LOG.fine(() -> "startDatePicker#editor#text changed");
            onStartControlChanged();
        });
        startDatePicker.valueProperty().addListener((observable, oldValue, newValue) -> {
            LOG.fine(() -> "startDatePicker#value changed");
            onStartControlChanged();
        });
        startHourTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            LOG.fine(() -> "startHourTextField#text changed");
            onStartControlChanged();
        });
        startMinuteTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            LOG.fine(() -> "startMinuteTextField#text changed");
            onStartControlChanged();
        });
        amPmComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            LOG.fine(() -> "amPmComboBox#value changed");
            onStartControlChanged();
        });

        durationHourTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            LOG.fine(() -> "durationHourTextField#text changed");
            onDurationControlChanged();
        });
        durationMinuteTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            LOG.fine(() -> "durationMinuteTextField#text changed");
            onDurationControlChanged();
        });
        timeZoneComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            LOG.fine(() -> "timeZoneComboBox#value changed");
            onDurationControlChanged();
        });
        parseStartBinding = BindingHelper.createBinaryOptionalBinding(() -> {
            LOG.fine(() -> "parseStartBinding invalidated");
            return parseDateAndTime(startDatePicker.getEditor().getText(), startDatePicker.getValue(), startHourTextField.getText(),
                    startMinuteTextField.getText(), amPmComboBox.getValue(), timeZoneComboBox.getValue());
        },
                startDatePicker.getEditor().textProperty(), startDatePicker.valueProperty(), startHourTextField.textProperty(),
                startMinuteTextField.textProperty(), amPmComboBox.valueProperty(), timeZoneComboBox.valueProperty());
        startParseMessage = parseStartBinding.mapToString((t) -> "", (s) -> s, resources.getString(RESOURCEKEY_REQUIRED));
        startMessage = Bindings.createStringBinding(() -> {
            LOG.fine(() -> "startMessage invalidated");
            String c = conflictMessage.get();
            String s = startParseMessage.get();
            return (s.isEmpty()) ? c : s;
        }, conflictMessage, startParseMessage);
        BooleanBinding timeZoneMissing = timeZoneComboBox.getSelectionModel().selectedItemProperty().isNull();
        parseDurationBinding = BindingHelper.createBinaryOptionalBinding(() -> parseDuration(durationHourTextField.getText(),
                durationMinuteTextField.getText(), timeZoneMissing.get()), durationHourTextField.textProperty(),
                durationMinuteTextField.textProperty(), timeZoneMissing);
        durationParseMessage = parseDurationBinding.mapToString((t) -> "", (s) -> s, resources.getString(RESOURCEKEY_REQUIRED));
        timeSpan.bind(Bindings.createObjectBinding(() -> {
            BinaryOptional<ZonedDateAndTimeSelection, String> start = parseStartBinding.get();
            BinaryOptional<AppointmentDuration, String> duration = parseDurationBinding.get();
            if (start.isPrimary() && duration.isPrimary()) {
                return ZonedAppointmentTimeSpan.of(start.getPrimary(), duration.getPrimary());
            }
            return null;
        }, parseStartBinding, parseDurationBinding));
        conflictCheckStatus.addListener((observable, oldValue, newValue) -> {
            switch (newValue) {
                case NO_CONFLICT:
                    collapseNode(checkConflictsButton);
                    break;
                case HAS_CONFLICT:
                    collapseNode(checkConflictsButton);
                    restoreNode(showConflictsButton);
                    return;
                default:
                    restoreNode(checkConflictsButton);
                    break;
            }
            collapseNode(showConflictsButton);
        });
        onStartControlChanged();
        onDurationControlChanged();
    }

    public ConflictCheckStatus getConflictCheckStatus() {
        return conflictCheckStatus.get();
    }

    public void setConflictCheckStatus(ConflictCheckStatus value) {
        conflictCheckStatus.set(value);
    }

    public ObjectProperty<ConflictCheckStatus> conflictCheckStatusProperty() {
        return conflictCheckStatus;
    }

    public ZonedAppointmentTimeSpan getTimeSpan() {
        return timeSpan.get();
    }

    public ReadOnlyObjectProperty<ZonedAppointmentTimeSpan> timeSpanProperty() {
        return timeSpan;
    }

    public String getConflictMessage() {
        return conflictMessage.get();
    }

    public void setConflictMessage(String message) {
        conflictMessage.set(message);
    }

    public StringProperty conflictMessageProperty() {
        return conflictMessage;
    }

    public final EventHandler<ActionEvent> getOnCheckConflictsButtonAction() {
        return checkConflictsButton.getOnAction();
    }

    public final void setOnCheckConflictsButtonAction(EventHandler<ActionEvent> value) {
        checkConflictsButton.setOnAction(value);
    }

    public final EventHandler<ActionEvent> getOnShowConflictsButtonAction() {
        return showConflictsButton.getOnAction();
    }

    public final void setOnShowConflictsButtonAction(EventHandler<ActionEvent> value) {
        showConflictsButton.setOnAction(value);
    }

    public void setDateRange(LocalDateTime start, Duration duration, TimeZone timeZone) {
        if (null == start) {
            startDatePicker.getEditor().setText("");
            startHourTextField.setText("");
            startMinuteTextField.setText("");
        } else {
            startDatePicker.setValue(start.toLocalDate());
            int h = start.getHour();
            if (h < 12) {
                startHourTextField.setText(String.format("%d", (h == 0) ? 12 : h));
                amPmComboBox.getSelectionModel().select(false);
            } else {
                startHourTextField.setText(String.format("%d", (h > 12) ? h - 12 : 12));
                amPmComboBox.getSelectionModel().select(true);
            }
            startMinuteTextField.setText(String.format("%02d", start.getMinute()));
        }
        if (null == duration) {
            durationHourTextField.setText("");
            durationMinuteTextField.setText("");
        } else {
            long minutes = duration.getSeconds() / 60;
            long hours = minutes / 60;
            minutes -= (hours * 60);
            durationHourTextField.setText(String.format("%d", hours));
            durationMinuteTextField.setText(String.format("%02d", minutes));
        }
        if (null == timeZone) {
            timeZoneComboBox.getSelectionModel().clearSelection();
        } else {
            timeZoneComboBox.getSelectionModel().select(timeZone);
        }

    }

    private void onTimeSpanComponentChanged() {
        ZonedAppointmentTimeSpan ts = timeSpan.get();
        if (null != ts) {
            DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL);
            ZonedDateTime s = ts.toZonedStartDateTime().withZoneSameInstant(ZoneId.systemDefault());
            ZonedDateTime e = ts.toZonedEndDateTime().withZoneSameInstant(ZoneId.systemDefault());
            restoreNode(localTimeLabel);
            restoreLabeled(localTimeValue, String.format(resources.getString(RESOURCEKEY_TIMERANGE),
                    formatter.format(s), formatter.format(e)));
        } else {
            collapseNode(localTimeLabel);
            collapseNode(localTimeValue);
        }
    }

    private void onStartControlChanged() {
        onTimeSpanComponentChanged();
        String s = startMessage.get();
        if (s.isEmpty()) {
            startValidationLabel.setVisible(false);
        } else {
            if (startParseMessage.get().isEmpty()) {
                removeCssClass(startValidationLabel, CssClassName.ERROR);
                addCssClass(startValidationLabel, CssClassName.WARNING);
            } else {
                removeCssClass(startValidationLabel, CssClassName.WARNING);
                addCssClass(startValidationLabel, CssClassName.ERROR);
            }
            startValidationLabel.setVisible(true);
            startValidationLabel.setText(s);
        }
    }

    private void onDurationControlChanged() {
        String s = durationParseMessage.get();
        durationValidationLabel.setVisible(!s.isEmpty());
        durationValidationLabel.setText(s);
        onTimeSpanComponentChanged();
    }

    private BinaryOptional<ZonedDateAndTimeSelection, String> parseDateAndTime(String startDateText, LocalDate startDateValue,
            String startHourText, String startMinuteText, Boolean pm, TimeZone timeZone) {
        int h, m;
        if (startDateText.trim().isEmpty()) {
            h = tryParseInteger(startHourText, 12);
            if (h == -2 || h == 0) {
                return BinaryOptional.ofSecondary(resources.getString(RESOURCEKEY_INVALIDSTARTHOUR));
            }
            m = tryParseInteger(startMinuteText, 59);
            if (m == -2) {
                return BinaryOptional.ofSecondary(resources.getString(RESOURCEKEY_INVALIDSTARTMINUTE));
            }
            return BinaryOptional.ofSecondary(resources.getString(RESOURCEKEY_REQUIRED));
        }
        LocalDate startDate;
        try {
            startDate = startDatePicker.getConverter().fromString(startDateText);
        } catch (DateTimeException ex) {
            return BinaryOptional.ofSecondary(resources.getString(RESOURCEKEY_INVALIDSTARTDATE));
        }
        h = tryParseInteger(startHourText, 12);
        if (h == -2 || h == 0) {
            return BinaryOptional.ofSecondary(resources.getString(RESOURCEKEY_INVALIDSTARTHOUR));
        }
        m = tryParseInteger(startMinuteText, 59);
        if (m == -2) {
            return BinaryOptional.ofSecondary(resources.getString(RESOURCEKEY_INVALIDSTARTMINUTE));
        }
        if (null == pm || h < 0 || m < 0) {
            return BinaryOptional.ofSecondary(resources.getString(RESOURCEKEY_REQUIRED));
        }
        if (null == timeZone) {
            return BinaryOptional.ofSecondary("");
        }
        return BinaryOptional.ofPrimary(
                ZonedDateAndTimeSelection.of(
                        ZonedDateTime.of(
                                LocalDateTime.of(
                                        startDate,
                                        LocalTime.of((pm) ? ((h == 12) ? 12 : h + 12) : ((h == 12) ? 0 : h), m)
                                ),
                                timeZone.toZoneId()
                        )
                )
        );
    }

    private BinaryOptional<AppointmentDuration, String> parseDuration(String durationHourText, String durationMinuteText, boolean timeZoneMissing) {
        int h = tryParseInteger(durationHourText, -1);
        if (h == -2) {
            return BinaryOptional.ofSecondary(resources.getString(RESOURCEKEY_INVALIDDURATIONHOUR));
        }
        int m = tryParseInteger(durationMinuteText, 59);
        if (m == -2) {
            return BinaryOptional.ofSecondary(resources.getString(RESOURCEKEY_INVALIDDURATIONMINUTE));
        }
        if (h < 0 || m < 0) {
            return BinaryOptional.empty();
        }
        if (timeZoneMissing) {
            return BinaryOptional.ofSecondary(resources.getString(RESOURCEKEY_TIMEZONENOTSPECIFIED));
        }
        return BinaryOptional.ofPrimary(AppointmentDuration.of(Duration.ofSeconds(((long) h * 60L + (long) m) * 60L)));
    }

}
