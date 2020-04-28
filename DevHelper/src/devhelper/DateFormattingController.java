package devhelper;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.StageStyle;

public class DateFormattingController {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="dateTimeRadioButton"
    private RadioButton dateTimeRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="dateTimeBuildToggleGroup"
    private ToggleGroup dateTimeBuildToggleGroup; // Value injected by FXMLLoader

    @FXML // fx:id="dateOnlyRadioButton"
    private RadioButton dateOnlyRadioButton; // Value injected by FXMLLoader

    @FXML
    private Spinner<Integer> yearSpinner;

    @FXML
    private Spinner<Integer> monthSpinner;

    @FXML
    private Spinner<Integer> daySpinner;

    @FXML // fx:id="minuteRadioButton"
    private RadioButton minuteRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="timeBuildToggleGroup"
    private ToggleGroup timeBuildToggleGroup; // Value injected by FXMLLoader

    @FXML // fx:id="secondsRadioButton"
    private RadioButton secondsRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="milllisecondsRadioButton"
    private RadioButton milllisecondsRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="timeOnlyRadioButton"
    private RadioButton timeOnlyRadioButton; // Value injected by FXMLLoader

    @FXML
    private Spinner<Integer> hourSpinner;

    @FXML
    private Spinner<Integer> minuteSpinner;

    @FXML
    private Spinner<Integer> secondSpinner;

    @FXML
    private Spinner<Integer> millisecondSpinner;

    @FXML // fx:id="localeCheckBox"
    private CheckBox localeCheckBox; // Value injected by FXMLLoader

    @FXML
    private ComboBox<Locale> localeComboBox;

    @FXML
    private ComboBox<DateTimeFormatterCellFactory.Item> formatterComboBox;

    @FXML // fx:id="temporarStringTextArea"
    private TextArea temporarStringTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="dateFormatValidationMessage"
    private Label dateFormatValidationMessage; // Value injected by FXMLLoader

    @FXML // fx:id="formatStringTextArea"
    private TextArea formatStringTextArea; // Value injected by FXMLLoader

    @FXML // fx:id="formatStringLabel"
    private Label formatStringLabel; // Value injected by FXMLLoader

    @FXML
    void onFormatterComboBoxAction(ActionEvent event) {
        ObservableList<String> styleClass = formatterComboBox.getStyleClass();
        if (formatterComboBox.getValue().isFormatTextAreaDisabled()) {
            if (!styleClass.contains("collapse"))
                styleClass.add("collapse");
            styleClass = formatStringLabel.getStyleClass();
            if (!styleClass.contains("collapse"))
                styleClass.add("collapse");
        } else {
            styleClass.remove("collapse");
            formatStringLabel.getStyleClass().remove("collapse");
        }
    }

    @FXML
    void onLocaleCheckBoxAction(ActionEvent event) {
        localeComboBox.setDisable(!localeCheckBox.isSelected());
    }

    @FXML
    void parseButtonAction(ActionEvent event) {
        DateTimeFormatter formatter = formatterComboBox.getValue().getFormatter(formatStringTextArea.textProperty(),
                localeComboBox.getSelectionModel().selectedItemProperty());
        LocalTime localTime;
        try {
            if (dateOnlyRadioButton.isSelected()) {
                LocalDate localDate = (LocalDate)formatter.parse(temporarStringTextArea.getText());
                yearSpinner.getValueFactory().setValue(localDate.getYear());
                monthSpinner.getValueFactory().setValue(localDate.getMonthValue());
                daySpinner.getValueFactory().setValue(localDate.getDayOfMonth());
                return;
            }
            
            if (timeOnlyRadioButton.isSelected()) {
                localTime = (LocalTime)formatter.parse(temporarStringTextArea.getText());
            } else {
                LocalDateTime localDateTime = (LocalDateTime)formatter.parse(temporarStringTextArea.getText());
                yearSpinner.getValueFactory().setValue(localDateTime.getYear());
                monthSpinner.getValueFactory().setValue(localDateTime.getMonthValue());
                daySpinner.getValueFactory().setValue(localDateTime.getDayOfMonth());
                localTime = localDateTime.toLocalTime();
            }
            hourSpinner.getValueFactory().setValue(localTime.getHour());
            minuteSpinner.getValueFactory().setValue(localTime.getMinute());
            if (milllisecondsRadioButton.isSelected()) {
                millisecondSpinner.getValueFactory().setValue(localTime.getNano());
            } else if (!secondsRadioButton.isSelected()) {
                return;
            }
            secondSpinner.getValueFactory().setValue(localTime.getSecond());
        } catch (DateTimeParseException ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR, String.format(ex.getMessage()), ButtonType.OK);
            alert.setTitle("Parse error");
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.initStyle(StageStyle.UTILITY);
            alert.initOwner(((Button)event.getSource()).getScene().getWindow());
            alert.showAndWait();
        }
    }

    @FXML
    void onConstructButtonAction(ActionEvent event) {
        DateTimeFormatter formatter = formatterComboBox.getValue().getFormatter(formatStringTextArea.textProperty(),
                localeComboBox.getSelectionModel().selectedItemProperty());
        TemporalAccessor temporal;
        if (dateOnlyRadioButton.isSelected()) {
            temporal = LocalDate.of(yearSpinner.getValue(), monthSpinner.getValue(), daySpinner.getValue());
        } else {
            LocalTime localTime;
            if (milllisecondsRadioButton.isSelected())
                localTime = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue(), secondSpinner.getValue(), millisecondSpinner.getValue());
            else if (secondsRadioButton.isSelected())
                localTime = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue(), secondSpinner.getValue());
            else
                localTime = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
            if (timeOnlyRadioButton.isSelected())
                temporal = localTime;
            else
                temporal = LocalDateTime.of(LocalDate.of(yearSpinner.getValue(), monthSpinner.getValue(), daySpinner.getValue()), localTime);
        }
        
        temporarStringTextArea.setText(formatter.format(temporal));
    }

    @FXML
    void initialize() {
        assert dateTimeRadioButton != null : "fx:id=\"dateTimeRadioButton\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert dateTimeBuildToggleGroup != null : "fx:id=\"dateTimeBuildToggleGroup\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert dateOnlyRadioButton != null : "fx:id=\"dateOnlyRadioButton\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert yearSpinner != null : "fx:id=\"yearSpinner\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert monthSpinner != null : "fx:id=\"monthSpinner\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert daySpinner != null : "fx:id=\"daySpinner\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert minuteRadioButton != null : "fx:id=\"minuteRadioButton\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert timeBuildToggleGroup != null : "fx:id=\"timeBuildToggleGroup\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert secondsRadioButton != null : "fx:id=\"secondsRadioButton\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert milllisecondsRadioButton != null : "fx:id=\"milllisecondsRadioButton\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert timeOnlyRadioButton != null : "fx:id=\"timeOnlyRadioButton\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert hourSpinner != null : "fx:id=\"hourSpinner\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert minuteSpinner != null : "fx:id=\"minuteSpinner\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert secondSpinner != null : "fx:id=\"secondSpinner\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert millisecondSpinner != null : "fx:id=\"millisecondSpinner\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert localeCheckBox != null : "fx:id=\"localeCheckBox\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert localeComboBox != null : "fx:id=\"localeComboBox\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert formatterComboBox != null : "fx:id=\"formatterComboBox\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert temporarStringTextArea != null : "fx:id=\"temporarStringTextArea\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert dateFormatValidationMessage != null : "fx:id=\"dateFormatValidationMessage\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert formatStringTextArea != null : "fx:id=\"formatStringTextArea\" was not injected: check your FXML file 'DateFormatting.fxml'.";
        assert formatStringLabel != null : "fx:id=\"formatStringLabel\" was not injected: check your FXML file 'DateFormatting.fxml'.";

        LocaleOptionCellFactory localeOptionCellFactory = new LocaleOptionCellFactory();
        localeComboBox.setCellFactory(localeOptionCellFactory);
        localeComboBox.setItems(localeOptionCellFactory.getItems());
        localeComboBox.setButtonCell(new LocaleOptionCellFactory.Cell());
        localeOptionCellFactory.find(Locale.getDefault().toLanguageTag()).ifPresent((t) -> localeComboBox.getSelectionModel().select(t));
        DateTimeFormatterCellFactory dateTimeFormatterCellFactory = new DateTimeFormatterCellFactory();
        formatterComboBox.setCellFactory(dateTimeFormatterCellFactory);
        formatterComboBox.setItems(dateTimeFormatterCellFactory.getItems());
        formatterComboBox.setButtonCell(new DateTimeFormatterCellFactory.Cell());
        formatterComboBox.getSelectionModel().select(0);
        LocalDateTime now = LocalDateTime.now();
        yearSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(LocalDateTime.MIN.getYear(), LocalDateTime.MAX.getYear(),
                now.getYear()));
        SpinnerValueFactory.IntegerSpinnerValueFactory monthSpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, now.getMonthValue());
        monthSpinner.setValueFactory(monthSpinnerValueFactory);
        SpinnerValueFactory.IntegerSpinnerValueFactory daySpinnerValueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1,
                now.toLocalDate().withDayOfMonth(1).plusMonths(1).minusDays(1).getDayOfMonth(), now.getDayOfMonth());
        daySpinner.setValueFactory(daySpinnerValueFactory);
        monthSpinnerValueFactory.valueProperty().addListener((observable, oldValue, newValue) -> {
            int max = LocalDate.of(yearSpinner.getValue(), monthSpinner.getValue(), 1).plusMonths(1).minusDays(1).getDayOfMonth();
            if (daySpinner.getValue() > max)
                daySpinnerValueFactory.setValue(max);
            daySpinnerValueFactory.setMax(max);
        });
        hourSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, now.getHour()));
        minuteSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, now.getMinute()));
        secondSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, now.getSecond()));
        millisecondSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 999, now.getNano()));
        
        dateTimeBuildToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == dateOnlyRadioButton) {
                hourSpinner.setDisable(true);
                minuteSpinner.setDisable(true);
                secondSpinner.setDisable(true);
                millisecondSpinner.setDisable(true);
            } else {
                hourSpinner.setDisable(false);
                minuteSpinner.setDisable(false);
                if (secondsRadioButton.isSelected()) {
                    secondSpinner.setDisable(false);
                    millisecondSpinner.setDisable(true);
                } else if (milllisecondsRadioButton.isSelected()) {
                    secondSpinner.setDisable(false);
                    millisecondSpinner.setDisable(false);
                } else {
                    secondSpinner.setDisable(true);
                    millisecondSpinner.setDisable(true);
                }
                secondSpinner.setDisable(true);
                millisecondSpinner.setDisable(true);
                if (newValue == timeOnlyRadioButton) {
                    yearSpinner.setDisable(true);
                    monthSpinner.setDisable(true);
                    daySpinner.setDisable(true);
                    return;
                }
            }
            yearSpinner.setDisable(false);
            monthSpinner.setDisable(false);
            daySpinner.setDisable(false);
        });
        
        timeBuildToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (dateOnlyRadioButton.isSelected()) {
                return;
            }
            if (newValue == millisecondSpinner) {
                millisecondSpinner.setDisable(false);
            } else {
                millisecondSpinner.setDisable(true);
                if (newValue != secondSpinner) {
                    secondSpinner.setDisable(true);
                    return;
                }
            }
            secondSpinner.setDisable(false);
        });
    }
    
}
