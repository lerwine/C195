package devhelper;

import static devhelper.FXMLDocumentController.ALL_ITEM;
import static devhelper.FXMLDocumentController.EMPTY_ITEM;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class LocaleListingControl extends VBox {

    private static int compareLocale(Locale o1, Locale o2) {
        String c1 = o1.getCountry();
        String c2 = o2.getCountry();
        int r;
        if (c1 == null || c1.isEmpty()) {
            if (c2 != null && !c2.isEmpty()) {
                return -1;
            }
        } else {
            if (c2 == null || c2.isEmpty()) {
                return 1;
            }
            if (c1.equals(c2)) {
                return 0;
            }
            r = c1.compareToIgnoreCase(c2);
            if (r != 0) {
                return r;
            }
            r = c1.compareTo(c2);
            if (r != 0) {
                return r;
            }
        }
        try {
            c1 = o1.getISO3Language();
        } catch (Throwable ex) {
            c1 = null;
        }
        if (c1 == null || c1.isEmpty()) {
            c1 = o1.getLanguage();
        }
        try {
            c2 = o2.getISO3Language();
        } catch (Throwable ex) {
            c2 = null;
        }
        if (c2 == null || c2.isEmpty()) {
            c2 = o2.getLanguage();
        }
        if (c1 == null || c1.isEmpty()) {
            if (c2 != null && !c2.isEmpty()) {
                return -1;
            }
        } else {
            if (c2 == null || c2.isEmpty()) {
                return 1;
            }
            r = c1.compareToIgnoreCase(c2);
            if (r != 0) {
                return r;
            }
            r = c1.compareTo(c2);
            if (r != 0) {
                return r;
            }
        }
        c1 = o1.getDisplayName();
        c2 = o2.getDisplayName();
        if (c1 == null || c1.isEmpty()) {
            return (c2 == null || c2.isEmpty()) ? 0 : -1;
        }

        if (c2 == null || c2.isEmpty()) {
            return 1;
        }
        r = c1.compareToIgnoreCase(c2);
        return (r == 0) ? c1.compareTo(c2) : r;
    }

    private final ObservableList<Locale> availableLocales;
    private final ObservableList<Locale> filteredLocales;
    private final ObservableList<String> twoLetterCountryCodes;
    private final ObservableList<String> threeLetterCountryCodes;
    private final ObservableList<String> scriptCodes;
    private final ObservableList<String> variantCodes;
    private final ObservableList<String> twoLetterLanguageCodes;
    private final ObservableList<String> threeLetterLanguageCodes;
    private final ObservableList<String> languageTags;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="twoLetterCountryCodesComboBox"
    private ComboBox<String> twoLetterCountryCodesComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="threeLetterCountryCodesComboBox"
    private ComboBox<String> threeLetterCountryCodesComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="scriptCodesComboBox"
    private ComboBox<String> scriptCodesComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="twoLetterLanguageCodesComboBox"
    private ComboBox<String> twoLetterLanguageCodesComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="threeLetterLanguageCodesComboBox"
    private ComboBox<String> threeLetterLanguageCodesComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="variantsComboBox"
    private ComboBox<String> variantsComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="twoLetterLanguageCodesRadioButton"
    private RadioButton twoLetterLanguageCodesRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="languageCode"
    private ToggleGroup languageCode; // Value injected by FXMLLoader

    @FXML // fx:id="threeLetterLanguageCodesRadioButton"
    private RadioButton threeLetterLanguageCodesRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="twoLetterCountryCodesRadioButton"
    private RadioButton twoLetterCountryCodesRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="countryCode"
    private ToggleGroup countryCode; // Value injected by FXMLLoader

    @FXML // fx:id="threeLetterCountryCodesRadioButton"
    private RadioButton threeLetterCountryCodesRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="languageTagsCheckBox"
    private CheckBox languageTagsCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="languageTagsComboBox"
    private ComboBox<String> languageTagsComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="availableLocalesTableView"
    private TableView<Locale> availableLocalesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="languageTagTableColumn"
    private TableColumn<Locale, String> languageTagTableColumn; // Value injected by FXMLLoader

    @FXML // fx:id="localeToStringTableColumn"
    private TableColumn<Locale, String> localeToStringTableColumn; // Value injected by FXMLLoader

    @SuppressWarnings("LeakingThisInConstructor")
    public LocaleListingControl() {
        availableLocales = FXCollections.observableArrayList();
        filteredLocales = FXCollections.observableArrayList();
        twoLetterCountryCodes = FXCollections.observableArrayList();
        threeLetterCountryCodes = FXCollections.observableArrayList();
        scriptCodes = FXCollections.observableArrayList();
        variantCodes = FXCollections.observableArrayList();
        twoLetterLanguageCodes = FXCollections.observableArrayList();
        threeLetterLanguageCodes = FXCollections.observableArrayList();
        languageTags = FXCollections.observableArrayList();
        twoLetterCountryCodes.add(ALL_ITEM);
        threeLetterCountryCodes.add(ALL_ITEM);
        scriptCodes.add(ALL_ITEM);
        variantCodes.add(ALL_ITEM);
        twoLetterLanguageCodes.add(ALL_ITEM);
        threeLetterLanguageCodes.add(ALL_ITEM);
        languageTags.add(ALL_ITEM);
        availableLocales.addAll(Locale.getAvailableLocales());
        availableLocales.sort(LocaleListingControl::compareLocale);
        filteredLocales.addAll(availableLocales);
        availableLocales.forEach((l) -> {
            String s = l.toLanguageTag();
            if (s == null || s.isEmpty()) {
                s = EMPTY_ITEM;
            }
            if (!languageTags.contains(s)) {
                languageTags.add(s);
            }
            s = l.getLanguage();
            if (!twoLetterLanguageCodes.contains(s)) {
                twoLetterLanguageCodes.add(s);
            }
            s = l.getISO3Language();
            if (!threeLetterLanguageCodes.contains(s)) {
                threeLetterLanguageCodes.add(s);
            }
            s = l.getScript();
            if (!scriptCodes.contains(s)) {
                scriptCodes.add(s);
            }
            s = l.getVariant();
            if (!variantCodes.contains(s)) {
                variantCodes.add(s);
            }
            s = l.getCountry();
            if (!twoLetterCountryCodes.contains(s)) {
                twoLetterCountryCodes.add(s);
            }
            try {
                s = l.getISO3Country();
            } catch (MissingResourceException ex) {
                if (!threeLetterCountryCodes.contains(s)) {
                    threeLetterCountryCodes.add(s);
                }
            }
        });
        FXMLLoader loader = new FXMLLoader(LocaleListingControl.class.getResource("/devhelper/LocaleListing.fxml"), null);
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(DateFormattingControl.class.getName()).log(Level.SEVERE, "Error loading devhelper/LocaleListing.fxml", ex);
        }
    }

    @FXML
    void onLanguageTagsCheckBoxAction(ActionEvent event) {
        if (languageTagsCheckBox.isSelected()) {
            languageTagsComboBox.setDisable(false);
            twoLetterCountryCodesComboBox.setDisable(true);
            threeLetterCountryCodesComboBox.setDisable(true);
            scriptCodesComboBox.setDisable(true);
            twoLetterLanguageCodesComboBox.setDisable(true);
            threeLetterLanguageCodesComboBox.setDisable(true);
            variantsComboBox.setDisable(true);
            twoLetterLanguageCodesRadioButton.setDisable(true);
            threeLetterLanguageCodesRadioButton.setDisable(true);
            twoLetterCountryCodesRadioButton.setDisable(true);
            threeLetterCountryCodesRadioButton.setDisable(true);
            onLanguageTagsComboBoxChanged(languageTagsComboBox.valueProperty(), languageTagsComboBox.getValue(), languageTagsComboBox.getValue());
        } else {
            languageTagsComboBox.setDisable(true);
            twoLetterCountryCodesComboBox.setDisable(false);
            threeLetterCountryCodesComboBox.setDisable(false);
            scriptCodesComboBox.setDisable(false);
            twoLetterLanguageCodesComboBox.setDisable(false);
            threeLetterLanguageCodesComboBox.setDisable(false);
            variantsComboBox.setDisable(false);
            twoLetterLanguageCodesRadioButton.setDisable(false);
            threeLetterLanguageCodesRadioButton.setDisable(false);
            twoLetterCountryCodesRadioButton.setDisable(false);
            threeLetterCountryCodesRadioButton.setDisable(false);
            onLanguageCodeToggleGroupChanged(languageCode.selectedToggleProperty(), languageCode.getSelectedToggle(), languageCode.getSelectedToggle());
            onCountryCodeToggleGroupChanged(countryCode.selectedToggleProperty(), countryCode.getSelectedToggle(), countryCode.getSelectedToggle());
            onScriptCodesComboBoxChanged(scriptCodesComboBox.valueProperty(), scriptCodesComboBox.getValue(), scriptCodesComboBox.getValue());
            onVariantsComboBoxChanged(variantsComboBox.valueProperty(), variantsComboBox.getValue(), variantsComboBox.getValue());
        }
    }

    private void applyFilter() {
        Predicate<Locale> filter;
        if (languageTagsCheckBox.isSelected()) {
            String s = languageTagsComboBox.getValue();
            if (null == s || s.isEmpty() || s.equals(ALL_ITEM)) {
                filter = null;
            } else if (s.equals(EMPTY_ITEM)) {
                filter = (t) -> {
                    String l = t.toLanguageTag();
                    return null == l || l.isEmpty();
                };
            } else {
                filter = (t) -> Objects.equals(t.toLanguageTag(), s);
            }
        } else {
            Predicate<Locale> f1;
            if (threeLetterLanguageCodesRadioButton.isSelected()) {
                String s = threeLetterLanguageCodesComboBox.getValue();
                if (null == s || s.isEmpty() || s.equals(ALL_ITEM)) {
                    f1 = null;
                } else if (s.equals(EMPTY_ITEM)) {
                    f1 = (t) -> {
                        String l = t.getISO3Language();
                        return null == l || l.isEmpty();
                    };
                } else {
                    f1 = (t) -> Objects.equals(t.getISO3Language(), s);
                }
            } else {
                String s = twoLetterLanguageCodesComboBox.getValue();
                if (null == s || s.isEmpty() || s.equals(ALL_ITEM)) {
                    f1 = null;
                } else if (s.equals(EMPTY_ITEM)) {
                    f1 = (t) -> {
                        String l = t.getLanguage();
                        return null == l || l.isEmpty();
                    };
                } else {
                    f1 = (t) -> Objects.equals(t.getLanguage(), s);
                }
            }
            Predicate<Locale> f2;
            if (threeLetterCountryCodesRadioButton.isSelected()) {
                String s = threeLetterCountryCodesComboBox.getValue();
                if (null == s || s.isEmpty() || s.equals(ALL_ITEM)) {
                    f2 = null;
                } else if (s.equals(EMPTY_ITEM)) {
                    f2 = (t) -> {
                        String l = t.getISO3Country();
                        return null == l || l.isEmpty();
                    };
                } else {
                    f2 = (t) -> Objects.equals(t.getISO3Country(), s);
                }
            } else {
                String s = twoLetterCountryCodesComboBox.getValue();
                if (null == s || s.isEmpty() || s.equals(ALL_ITEM)) {
                    f2 = null;
                } else if (s.equals(EMPTY_ITEM)) {
                    f2 = (t) -> {
                        String l = t.getCountry();
                        return null == l || l.isEmpty();
                    };
                } else {
                    f2 = (t) -> Objects.equals(t.getCountry(), s);
                }
            }
            Predicate<Locale> f3;
            String v = scriptCodesComboBox.getValue();
            if (null == v || v.isEmpty() || v.equals(ALL_ITEM)) {
                f3 = null;
            } else if (v.equals(EMPTY_ITEM)) {
                f3 = (t) -> {
                    String l = t.getScript();
                    return null == l || l.isEmpty();
                };
            } else {
                f3 = (t) -> Objects.equals(t.getScript(), v);
            }
            Predicate<Locale> f4;
            String n = variantsComboBox.getValue();
            if (null == n || n.isEmpty() || n.equals(ALL_ITEM)) {
                f4 = null;
            } else if (n.equals(EMPTY_ITEM)) {
                f4 = (t) -> {
                    String l = t.getVariant();
                    return null == l || l.isEmpty();
                };
            } else {
                f4 = (t) -> Objects.equals(t.getVariant(), n);
            }
            if (null != f1) {
                if (null != f2) {
                    if (null != f3) {
                        if (null != f4) {
                            filter = (t) -> f1.test(t) && f2.test(t) && f3.test(t) && f4.test(t);
                        } else {
                            filter = (t) -> f1.test(t) && f2.test(t) && f3.test(t);
                        }
                    } else if (null != f4) {
                        filter = (t) -> f1.test(t) && f2.test(t) && f4.test(t);
                    } else {
                        filter = (t) -> f1.test(t) && f2.test(t);
                    }
                } else if (null != f3) {
                    if (null != f4) {
                        filter = (t) -> f1.test(t) && f3.test(t) && f4.test(t);
                    } else {
                        filter = (t) -> f1.test(t) && f3.test(t);
                    }
                } else if (null != f4) {
                    filter = (t) -> f1.test(t) && f4.test(t);
                } else {
                    filter = f1;
                }
            } else if (null != f2) {
                if (null != f3) {
                    if (null != f4) {
                        filter = (t) -> f2.test(t) && f3.test(t) && f4.test(t);
                    } else {
                        filter = (t) -> f2.test(t) && f3.test(t);
                    }
                } else if (null != f4) {
                    filter = (t) -> f2.test(t) && f4.test(t);
                } else {
                    filter = f2;
                }
            } else if (null != f3) {
                if (null != f4) {
                    filter = (t) -> f3.test(t) && f4.test(t);
                } else {
                    filter = f3;
                }
            } else {
                filter = f4;
            }
        }
        filteredLocales.clear();
        if (null == filter) {
            filteredLocales.addAll(availableLocales);
        } else {
            filteredLocales.addAll(availableLocales.filtered(filter));
        }
    }

    private void onLanguageCodeToggleGroupChanged(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        if (!languageTagsCheckBox.isSelected()) {
            if (threeLetterLanguageCodesRadioButton.isSelected()) {
                twoLetterLanguageCodesComboBox.setDisable(true);
                threeLetterLanguageCodesComboBox.setDisable(false);
            } else {
                twoLetterLanguageCodesComboBox.setDisable(false);
                threeLetterLanguageCodesComboBox.setDisable(true);
            }
            applyFilter();
        }
    }

    private void onCountryCodeToggleGroupChanged(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
        if (!languageTagsCheckBox.isSelected()) {
            if (threeLetterCountryCodesRadioButton.isSelected()) {
                twoLetterCountryCodesComboBox.setDisable(true);
                threeLetterCountryCodesComboBox.setDisable(false);
            } else {
                twoLetterCountryCodesComboBox.setDisable(false);
                threeLetterCountryCodesComboBox.setDisable(true);
            }
            applyFilter();
        }
        if (Objects.equals(newValue, threeLetterLanguageCodesComboBox)) {
            onThreeLetterCountryCodesComboBoxChanged(threeLetterLanguageCodesComboBox.valueProperty(), threeLetterLanguageCodesComboBox.getValue(),
                    threeLetterLanguageCodesComboBox.getValue());
        } else {
            onTwoLetterCountryCodesComboBoxChanged(twoLetterLanguageCodesComboBox.valueProperty(), twoLetterLanguageCodesComboBox.getValue(),
                    twoLetterLanguageCodesComboBox.getValue());
        }
    }

    private void onTwoLetterLanguageCodesComboBoxChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (twoLetterLanguageCodesRadioButton.isSelected() && !languageTagsCheckBox.isSelected()) {
            applyFilter();
        }
    }

    private void onThreeLetterLanguageCodesComboBoxChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (threeLetterLanguageCodesRadioButton.isSelected() && !languageTagsCheckBox.isSelected()) {
            applyFilter();
        }
    }

    private void onScriptCodesComboBoxChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!languageTagsCheckBox.isSelected()) {
            applyFilter();
        }
    }

    private void onVariantsComboBoxChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (!languageTagsCheckBox.isSelected()) {
            applyFilter();
        }
    }

    private void onTwoLetterCountryCodesComboBoxChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (twoLetterCountryCodesRadioButton.isSelected() && !languageTagsCheckBox.isSelected()) {
            applyFilter();
        }
    }

    private void onThreeLetterCountryCodesComboBoxChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (threeLetterCountryCodesRadioButton.isSelected() && !languageTagsCheckBox.isSelected()) {
            applyFilter();
        }
    }

    private void onLanguageTagsComboBoxChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (languageTagsCheckBox.isSelected()) {
            applyFilter();
        }
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert twoLetterCountryCodesComboBox != null : "fx:id=\"twoLetterCountryCodesComboBox\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert threeLetterCountryCodesComboBox != null : "fx:id=\"threeLetterCountryCodesComboBox\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert scriptCodesComboBox != null : "fx:id=\"scriptCodesComboBox\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert twoLetterLanguageCodesComboBox != null : "fx:id=\"twoLetterLanguageCodesComboBox\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert threeLetterLanguageCodesComboBox != null : "fx:id=\"threeLetterLanguageCodesComboBox\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert variantsComboBox != null : "fx:id=\"variantsComboBox\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert twoLetterLanguageCodesRadioButton != null : "fx:id=\"twoLetterLanguageCodesRadioButton\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert languageCode != null : "fx:id=\"languageCode\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert threeLetterLanguageCodesRadioButton != null : "fx:id=\"threeLetterLanguageCodesRadioButton\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert twoLetterCountryCodesRadioButton != null : "fx:id=\"twoLetterCountryCodesRadioButton\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert countryCode != null : "fx:id=\"countryCode\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert threeLetterCountryCodesRadioButton != null : "fx:id=\"threeLetterCountryCodesRadioButton\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert languageTagsCheckBox != null : "fx:id=\"languageTagsCheckBox\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert languageTagsComboBox != null : "fx:id=\"languageTagsComboBox\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert availableLocalesTableView != null : "fx:id=\"availableLocalesTableView\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert languageTagTableColumn != null : "fx:id=\"languageTagTableColumn\" was not injected: check your FXML file 'LocaleListing.fxml'.";
        assert localeToStringTableColumn != null : "fx:id=\"localeToStringTableColumn\" was not injected: check your FXML file 'LocaleListing.fxml'.";

        languageTagTableColumn.setCellValueFactory((TableColumn.CellDataFeatures<Locale, String> param) -> {
            Locale locale = param.getValue();
            return new SimpleObjectProperty<>((locale == null) ? null : locale.toLanguageTag());
        });
        localeToStringTableColumn.setCellValueFactory((TableColumn.CellDataFeatures<Locale, String> param) -> {
            Locale locale = param.getValue();
            return new SimpleObjectProperty<>((locale == null) ? null : locale.toString());
        });

        twoLetterLanguageCodesComboBox.setItems(twoLetterLanguageCodes);
        threeLetterLanguageCodesComboBox.setItems(threeLetterLanguageCodes);
        scriptCodesComboBox.setItems(scriptCodes);
        variantsComboBox.setItems(variantCodes);

        twoLetterCountryCodesComboBox.setItems(twoLetterCountryCodes);
        threeLetterCountryCodesComboBox.setItems(threeLetterCountryCodes);

        languageTagsComboBox.setItems(languageTags);
        availableLocalesTableView.setItems(filteredLocales);

        availableLocalesTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableLocalesTableView.setOnKeyPressed(new FXMLDocumentController.TableCopyEventHandler());
        languageCode.selectedToggleProperty().addListener(this::onLanguageCodeToggleGroupChanged);
        countryCode.selectedToggleProperty().addListener(this::onCountryCodeToggleGroupChanged);
        twoLetterLanguageCodesComboBox.valueProperty().addListener(this::onTwoLetterLanguageCodesComboBoxChanged);
        threeLetterLanguageCodesComboBox.valueProperty().addListener(this::onThreeLetterLanguageCodesComboBoxChanged);
        scriptCodesComboBox.valueProperty().addListener(this::onScriptCodesComboBoxChanged);
        variantsComboBox.valueProperty().addListener(this::onVariantsComboBoxChanged);
        twoLetterCountryCodesComboBox.valueProperty().addListener(this::onTwoLetterCountryCodesComboBoxChanged);
        twoLetterCountryCodesComboBox.valueProperty().addListener(this::onThreeLetterCountryCodesComboBoxChanged);
        languageTagsComboBox.valueProperty().addListener(this::onLanguageTagsComboBoxChanged);
        applyFilter();
    }
}
