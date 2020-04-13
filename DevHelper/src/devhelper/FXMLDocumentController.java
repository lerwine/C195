package devhelper;

import java.text.NumberFormat;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.TimeZone;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyFloatProperty;
import javafx.beans.property.ReadOnlyFloatWrapper;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import scheduler.util.PwHash;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class FXMLDocumentController {

    @FXML
    private RadioButton dateTimeRadioButton;

    @FXML
    private ToggleGroup dateTimeBuildToggleGroup;

    @FXML
    private RadioButton dateOnlyRadioButton;

    @FXML
    private Spinner<?> yearSpinner;

    @FXML
    private Spinner<?> monthSpinner;

    @FXML
    private Spinner<?> daySpinner;

    @FXML
    private RadioButton minuteRadioButton;

    @FXML
    private ToggleGroup timeBuildToggleGroup;

    @FXML
    private RadioButton secondsRadioButton;

    @FXML
    private RadioButton milllisecondsRadioButton;

    @FXML
    private RadioButton timeOnlyRadioButton;

    @FXML
    private Spinner<?> hourSpinner;

    @FXML
    private Spinner<?> minuteSpinner;

    @FXML
    private Spinner<?> secondSpinner;

    @FXML
    private Spinner<?> millisecondSpinner;

    @FXML
    private CheckBox localeCheckBox;

    @FXML
    private ComboBox<?> localeComboBox;

    @FXML
    private ComboBox<?> formatterComboBox;

    @FXML
    private TextArea temporarStringTextArea;

    @FXML
    private Label dateFormatValidationMessage;

    @FXML
    private CheckBox formatStringCheckBox;

    @FXML
    private TextArea formatStringTextArea;

    @FXML // fx:id="languageTagInputTextBox"
    private TextField languageTagInputTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="countryTextBox"
    private TextField countryTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="languageTextBox"
    private TextField languageTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="variantTextBox"
    private TextField variantTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="scriptTextBox"
    private TextField scriptTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="iso3CountryTextBox"
    private TextField iso3CountryTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="iso3LanguageTextBox"
    private TextField iso3LanguageTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="languageTagTextBox"
    private TextField languageTagTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="stringTextBox"
    private TextField stringTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="displayCountryTextBox"
    private TextField displayCountryTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="displayCountryInLocaleTextBox"
    private TextField displayCountryInLocaleTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="displayLanguageTextBox"
    private TextField displayLanguageTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="displayLanguageInLocaleTextBox"
    private TextField displayLanguageInLocaleTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="displayNameTextBox"
    private TextField displayNameTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="displayNameInLocaleTextBox"
    private TextField displayNameInLocaleTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="displayScriptTextBox"
    private TextField displayScriptTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="displayScriptInLocaleTextBox"
    private TextField displayScriptInLocaleTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="displayVariantTextBox"
    private TextField displayVariantTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="displayVariantInLocaleTextBox"
    private TextField displayVariantInLocaleTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="inputTextBox"
    private TextField inputTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="hashTextBox"
    private TextField hashTextBox; // Value injected by FXMLLoader

    @FXML
    private ComboBox<String> twoLetterCountryCodesComboBox;

    @FXML
    private ComboBox<String> threeLetterCountryCodesComboBox;

    @FXML
    private ComboBox<String> iso3CountryCodesComboBox;

    @FXML
    private ComboBox<String> scriptCodesComboBox;

    @FXML
    private ComboBox<String> twoLetterLanguageCodesComboBox;

    @FXML
    private ComboBox<String> threeLetterLanguageCodesComboBox;

    @FXML
    private ComboBox<String> languageTagsComboBox;

    @FXML
    private ComboBox<String> timeZoneIDsComboBox;

    @FXML
    private ComboBox<String> zoneIDsComboBox;

    @FXML
    private ComboBox<String> zoneOffsetIDsComboBox;

    @FXML
    private TableView<Locale> availableLocalesTableView;

    @FXML
    private TableColumn<Locale, String> languageTagTableColumn;

    @FXML
    private TableColumn<Locale, String> localeToStringTableColumn;

    @FXML
    private TableView<TimeZoneInfo> timeZonesTableView;

    @FXML
    private TableView<ZoneIdInfo> zoneIdsTableView;

    private ObservableList<Locale> availableLocales;
    private ObservableList<Locale> filteredLocales;
    private ObservableList<String> twoLetterCountryCodes;
    private ObservableList<String> threeLetterCountryCodes;
    private ObservableList<String> iso3CountryCodes;
    private ObservableList<String> scriptCodes;
    private ObservableList<String> twoLetterLanguageCodes;
    private ObservableList<String> threeLetterLanguageCodes;
    private ObservableList<String> languageTags;
    private ObservableList<TimeZoneInfo> availableTimeZones;
    private ObservableList<TimeZoneInfo> filteredTimeZones;
    private ObservableList<ZoneIdInfo> availableZoneIds;
    private ObservableList<ZoneIdInfo> filteredZoneIds;
    private ObservableList<String> timeZoneIDs;
    private ObservableList<String> zoneIDs;
    private ObservableList<String> zoneOffsetIDs;

    @FXML
    void forLanguageTagButtonClick(ActionEvent event) {
        BiConsumer<TextField, String> setText = (t, u) -> {
            if (null == u) {
                t.setText("(null)");
                t.setStyle("color:red");
            } else {
                t.setText(u);
                t.setStyle("");
            }
        };
        Locale locale = Locale.forLanguageTag(languageTagInputTextBox.getText());
        setText.accept(countryTextBox, locale.getCountry());
        setText.accept(iso3CountryTextBox, locale.getISO3Country());
        setText.accept(languageTextBox, locale.getLanguage());
        setText.accept(iso3LanguageTextBox, locale.getISO3Language());
        setText.accept(variantTextBox, locale.getVariant());
        setText.accept(scriptTextBox, locale.getScript());
        setText.accept(languageTagTextBox, locale.toLanguageTag());
        setText.accept(stringTextBox, locale.toString());
        setText.accept(displayCountryTextBox, locale.getDisplayCountry());
        setText.accept(displayCountryInLocaleTextBox, locale.getDisplayCountry(locale));
        setText.accept(displayLanguageTextBox, locale.getDisplayLanguage());
        setText.accept(displayLanguageInLocaleTextBox, locale.getDisplayLanguage(locale));
        setText.accept(displayNameTextBox, locale.getDisplayName());
        setText.accept(displayNameInLocaleTextBox, locale.getDisplayName(locale));
        setText.accept(displayScriptTextBox, locale.getDisplayScript());
        setText.accept(displayScriptInLocaleTextBox, locale.getDisplayScript(locale));
        setText.accept(displayVariantTextBox, locale.getDisplayVariant());
        setText.accept(displayVariantInLocaleTextBox, locale.getDisplayVariant(locale));
    }

    @FXML
    void getHashButtonClick(ActionEvent event) {
        hashTextBox.setText((new PwHash(inputTextBox.getText(), true)).getEncodedHash());
    }

    public static <T> Predicate<T> createFilter(Predicate<T> newFilter, Predicate<T> currentFilter) {
        if (newFilter == null) {
            return currentFilter;
        }
        if (currentFilter == null) {
            return newFilter;
        }
        return (T t) -> newFilter.test(t) && currentFilter.test(t);
    }

    private final String ALL_ITEM = " (all) ";
    private final String EMPTY_ITEM = " (empty) ";

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    public static class TimeZoneInfo implements Comparable<TimeZoneInfo> {

        private final ReadOnlyStringWrapper availableID;

        public String getAvailableID() {

            return availableID.get();
        }

        public ReadOnlyStringProperty availableIDProperty() {
            return availableID.getReadOnlyProperty();
        }
        
        private final ReadOnlyStringWrapper displayName;

        public String getDisplayName() {
            return displayName.get();
        }

        public ReadOnlyStringProperty displayNameProperty() {
            return displayName.getReadOnlyProperty();
        }

        private final ReadOnlyIntegerWrapper dstSavings;

        public int getDstSavings() {
            return dstSavings.get();
        }

        public ReadOnlyIntegerProperty dstSavingsProperty() {
            return dstSavings.getReadOnlyProperty();
        }
        private final ReadOnlyFloatWrapper rawOffset;

        public float getRawOffset() {
            return rawOffset.get();
        }

        public ReadOnlyFloatProperty rawOffsetProperty() {
            return rawOffset.getReadOnlyProperty();
        }

        private final ReadOnlyStringWrapper id;

        public String getId() {

            return id.get();
        }

        public ReadOnlyStringProperty idProperty() {
            return id.getReadOnlyProperty();
        }
        private final ReadOnlyBooleanWrapper observesDaylightTime;

        public boolean isObservesDaylightTime() {
            return observesDaylightTime.get();
        }

        public ReadOnlyBooleanProperty observesDaylightTimeProperty() {
            return observesDaylightTime.getReadOnlyProperty();
        }
        
        private final ReadOnlyBooleanWrapper useDaylightTime;

        public boolean isUseDaylightTime() {
            return useDaylightTime.get();
        }

        public ReadOnlyBooleanProperty useDaylightTimeProperty() {
            return useDaylightTime.getReadOnlyProperty();
        }
        
        private final ReadOnlyStringWrapper zoneId;

        public String getZoneId() {
            return zoneId.get();
        }

        public ReadOnlyStringProperty zoneIdProperty() {
            return zoneId.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper zoneName;

        public String getZoneName() {
            return zoneName.get();
        }

        public ReadOnlyStringProperty zoneNameProperty() {
            return zoneName.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper normalizedId;

        public String getNormalizedId() {
            return normalizedId.get();
        }

        public ReadOnlyStringProperty normalizedIdProperty() {
            return normalizedId.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper normalizedName;

        public String getNormalizedName() {
            return normalizedName.get();
        }

        public ReadOnlyStringProperty normalizedNameProperty() {
            return normalizedName.getReadOnlyProperty();
        }

        private final ReadOnlyIntegerWrapper totalSeconds;

        public int getTotalSeconds() {
            return totalSeconds.get();
        }

        public ReadOnlyIntegerProperty totalSecondsProperty() {
            return totalSeconds.getReadOnlyProperty();
        }
        private final ReadOnlyIntegerWrapper index;

        public int getIndex() {
            return index.get();
        }

        public ReadOnlyIntegerProperty indexProperty() {
            return index.getReadOnlyProperty();
        }

        public TimeZoneInfo(String id, int index) {
            this.index = new ReadOnlyIntegerWrapper(index);
            TimeZone tz = TimeZone.getTimeZone(id);
            this.availableID = new ReadOnlyStringWrapper(id);
            displayName = new ReadOnlyStringWrapper(tz.getDisplayName());
            dstSavings = new ReadOnlyIntegerWrapper(tz.getDSTSavings());
            rawOffset = new ReadOnlyFloatWrapper((float) tz.getRawOffset() / 1000.0f);
            this.id = new ReadOnlyStringWrapper(tz.getID());
            observesDaylightTime = new ReadOnlyBooleanWrapper(tz.observesDaylightTime());
            useDaylightTime = new ReadOnlyBooleanWrapper(tz.useDaylightTime());
           
            ZoneId z;
            try {
                z = tz.toZoneId();
            } catch (Throwable ex) {
                z = null;
            }
            if (z != null) {
                zoneId = new ReadOnlyStringWrapper(z.getId());
                zoneName = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.FULL, Locale.getDefault(Category.DISPLAY)));
                if (z instanceof ZoneOffset) {
                    ZoneOffset zo = (ZoneOffset)z;
                    normalizedId = new ReadOnlyStringWrapper(zo.getId());
                    normalizedName = new ReadOnlyStringWrapper(zo.getDisplayName(TextStyle.FULL, Locale.getDefault(Category.DISPLAY)));
                    totalSeconds = new ReadOnlyIntegerWrapper(((ZoneOffset) z).getTotalSeconds());
                    return;
                }
                try {
                    z = z.normalized();
                } catch (Throwable ex) {
                    z = null;
                }
                if (z != null) {
                    normalizedId = new ReadOnlyStringWrapper(z.getId());
                    normalizedName = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.FULL, Locale.getDefault(Category.DISPLAY)));
                    if (z instanceof ZoneOffset) {
                        totalSeconds = new ReadOnlyIntegerWrapper(((ZoneOffset) z).getTotalSeconds());
                    } else {
                        totalSeconds = new ReadOnlyIntegerWrapper(-1);
                    }
                    return;
                }
            } else {
                zoneId = new ReadOnlyStringWrapper();
                zoneName = new ReadOnlyStringWrapper();
            }
            totalSeconds = new ReadOnlyIntegerWrapper(-1);
            normalizedId = new ReadOnlyStringWrapper();
            normalizedName = new ReadOnlyStringWrapper();
        }

        @Override
        public int compareTo(TimeZoneInfo o) {
            if (o == null) {
                return 1;
            }
            int r;
            if ((r = (int) (getRawOffset() * 1000) - (int) (o.getRawOffset()) * 1000) != 0 || (r = getTotalSeconds() - o.getTotalSeconds()) != 0) {
                return r;
            }
            return getAvailableID().compareTo(o.getAvailableID());
        }
    }

    public static class ZoneIdInfo implements Comparable<ZoneIdInfo> {

        private final ReadOnlyStringWrapper availableZoneId;

        public String getAvailableZoneId() {
            return availableZoneId.get();
        }

        public ReadOnlyStringProperty availableZoneIdProperty() {
            return availableZoneId.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper id;

        public String getId() {
            return id.get();
        }

        public ReadOnlyStringProperty idProperty() {
            return id.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper fullName;

        public String getFullName() {
            return fullName.get();
        }

        public ReadOnlyStringProperty fullNameProperty() {
            return fullName.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper standaloneFull;

        public String getStandaloneFull() {
            return standaloneFull.get();
        }

        public ReadOnlyStringProperty standaloneFullProperty() {
            return standaloneFull.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper shortName;

        public String getShortName() {
            return shortName.get();
        }

        public ReadOnlyStringProperty shortNameProperty() {
            return shortName.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper standaloneShort;

        public String getStandaloneShort() {
            return standaloneShort.get();
        }

        public ReadOnlyStringProperty standaloneShortProperty() {
            return standaloneShort.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper narrowName;

        public String getNarrowName() {
            return narrowName.get();
        }

        public ReadOnlyStringProperty narrowNameProperty() {
            return narrowName.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper standaloneNarrow;

        public String getStandaloneNarrow() {
            return standaloneNarrow.get();
        }

        public ReadOnlyStringProperty standaloneNarrowProperty() {
            return standaloneNarrow.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper normalizedId;

        public String getNormalizedId() {
            return normalizedId.get();
        }

        public ReadOnlyStringProperty normalizedIdProperty() {
            return normalizedId.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper normalizedNameFull;

        public String getNormalizedNameFull() {
            return normalizedNameFull.get();
        }

        public ReadOnlyStringProperty normalizedNameFullProperty() {
            return normalizedNameFull.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper normalizedNameShort;

        public String getNormalizedNameShort() {
            return normalizedNameShort.get();
        }

        public ReadOnlyStringProperty normalizedNameShortProperty() {
            return normalizedNameShort.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper normalizedNameNarrow;

        public String getNormalizedNameNarrow() {
            return normalizedNameNarrow.get();
        }

        public ReadOnlyStringProperty normalizedNameNarrowProperty() {
            return normalizedNameNarrow.getReadOnlyProperty();
        }
        private final ReadOnlyIntegerWrapper totalSeconds;

        public int getTotalSeconds() {
            return totalSeconds.get();
        }

        public ReadOnlyIntegerProperty totalSecondsProperty() {
            return totalSeconds.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper timeZoneId;

        public String getTimeZoneId() {
            return timeZoneId.get();
        }

        public ReadOnlyStringProperty timeZoneIdProperty() {
            return timeZoneId.getReadOnlyProperty();
        }
        private final ReadOnlyStringWrapper timeZoneName;

        public String getTimeZoneName() {
            return timeZoneName.get();
        }

        public ReadOnlyStringProperty timeZoneNameProperty() {
            return timeZoneName.getReadOnlyProperty();
        }

        private final ReadOnlyFloatWrapper timeZoneOffset;

        public float getTimeZoneOffset() {
            return timeZoneOffset.get();
        }

        public ReadOnlyFloatProperty timeZoneOffsetProperty() {
            return timeZoneOffset.getReadOnlyProperty();
        }
        private final ReadOnlyIntegerWrapper index;

        public int getIndex() {
            return index.get();
        }

        public ReadOnlyIntegerProperty indexProperty() {
            return index.getReadOnlyProperty();
        }

        public ZoneIdInfo(String id, int index) {
            this.index = new ReadOnlyIntegerWrapper(index);
            availableZoneId = new ReadOnlyStringWrapper(id);
            ZoneId z = ZoneId.of(id);
            Locale l = Locale.getDefault(Category.DISPLAY);
            
            this.id = new ReadOnlyStringWrapper(z.getId());
            fullName = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.FULL, l));
            standaloneFull = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.FULL_STANDALONE, l));
            shortName = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
            standaloneShort = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.SHORT_STANDALONE, l));
            narrowName = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.NARROW, l));
            standaloneNarrow = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.NARROW_STANDALONE, l));
            if (z instanceof ZoneOffset) {
                normalizedId = new ReadOnlyStringWrapper(z.getId());
                normalizedNameFull = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.FULL, l));
                normalizedNameShort = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.SHORT, l));
                normalizedNameNarrow = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.NARROW, l));
                totalSeconds = new ReadOnlyIntegerWrapper(((ZoneOffset) z).getTotalSeconds());
            } else {
                try {
                    z = z.normalized();
                } catch (Throwable ex) {
                    z = null;
                }
                if (z != null) {
                    normalizedId = new ReadOnlyStringWrapper(z.getId());
                    normalizedNameFull = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.FULL, l));
                    normalizedNameShort = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.SHORT, l));
                    normalizedNameNarrow = new ReadOnlyStringWrapper(z.getDisplayName(TextStyle.NARROW, l));
                    if (z instanceof ZoneOffset) {
                        totalSeconds = new ReadOnlyIntegerWrapper(((ZoneOffset) z).getTotalSeconds());
                    } else {
                        totalSeconds = new ReadOnlyIntegerWrapper(-1);
                    }
                } else {
                    normalizedId = new ReadOnlyStringWrapper();
                    normalizedNameFull = new ReadOnlyStringWrapper();
                    normalizedNameShort = new ReadOnlyStringWrapper();
                    normalizedNameNarrow = new ReadOnlyStringWrapper();
                    totalSeconds = new ReadOnlyIntegerWrapper(-1);
                }
            }
            TimeZone tz;
            try {
                tz = TimeZone.getTimeZone(z);
            } catch (Throwable ex) {
                tz = null;
            }
            if (tz != null) {
                timeZoneId = new ReadOnlyStringWrapper(tz.getID());
                timeZoneName = new ReadOnlyStringWrapper(tz.getDisplayName());
                timeZoneOffset = new ReadOnlyFloatWrapper((float) tz.getRawOffset() / 1000.0f);
            } else {
                timeZoneId = new ReadOnlyStringWrapper();
                timeZoneName = new ReadOnlyStringWrapper();
                timeZoneOffset = new ReadOnlyFloatWrapper(-1);
            }
        }

        @Override
        public int compareTo(ZoneIdInfo o) {
            if (o == null) {
                return 1;
            }
            int r;
            if ((r = getTotalSeconds() - o.getTotalSeconds()) != 0 || (r = (int) (getTimeZoneOffset() * 1000) - (int) (o.getTimeZoneOffset() * 1000)) != 0) {
                return r;
            }
            return getFullName().compareTo(o.getFullName());
        }
    }

    @FXML
    void initialize() {
        assert languageTagInputTextBox != null : "fx:id=\"languageTagInputTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert countryTextBox != null : "fx:id=\"countryTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert languageTextBox != null : "fx:id=\"languageTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert variantTextBox != null : "fx:id=\"variantTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert scriptTextBox != null : "fx:id=\"scriptTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert iso3CountryTextBox != null : "fx:id=\"iso3CountryTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert iso3LanguageTextBox != null : "fx:id=\"iso3LanguageTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert languageTagTextBox != null : "fx:id=\"languageTagTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert stringTextBox != null : "fx:id=\"stringTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert displayCountryTextBox != null : "fx:id=\"displayCountryTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert displayCountryInLocaleTextBox != null : "fx:id=\"displayCountryInLocaleTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert displayLanguageTextBox != null : "fx:id=\"displayLanguageTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert displayLanguageInLocaleTextBox != null : "fx:id=\"displayLanguageInLocaleTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert displayNameTextBox != null : "fx:id=\"displayNameTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert displayNameInLocaleTextBox != null : "fx:id=\"displayNameInLocaleTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert displayScriptTextBox != null : "fx:id=\"displayScriptTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert displayScriptInLocaleTextBox != null : "fx:id=\"displayScriptInLocaleTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert displayVariantTextBox != null : "fx:id=\"displayVariantTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert displayVariantInLocaleTextBox != null : "fx:id=\"displayVariantInLocaleTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert inputTextBox != null : "fx:id=\"inputTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert hashTextBox != null : "fx:id=\"hashTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert twoLetterCountryCodesComboBox != null : "fx:id=\"twoLetterCountryCodesComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert threeLetterCountryCodesComboBox != null : "fx:id=\"threeLetterCountryCodesComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert iso3CountryCodesComboBox != null : "fx:id=\"iso3CountryCodesComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert scriptCodesComboBox != null : "fx:id=\"scriptCodesComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert twoLetterLanguageCodesComboBox != null : "fx:id=\"twoLetterLanguageCodesComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert threeLetterLanguageCodesComboBox != null : "fx:id=\"threeLetterLanguageCodesComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert languageTagsComboBox != null : "fx:id=\"languageTagsComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert timeZoneIDsComboBox != null : "fx:id=\"timeZoneIDsComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert zoneIDsComboBox != null : "fx:id=\"zoneIDsComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert zoneOffsetIDsComboBox != null : "fx:id=\"zoneOffsetIDsComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert availableLocalesTableView != null : "fx:id=\"availableLocalesTableView\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert languageTagTableColumn != null : "fx:id=\"languageTagTableColumn\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert localeToStringTableColumn != null : "fx:id=\"localeToStringTableColumn\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert timeZonesTableView != null : "fx:id=\"timeZonesTableView\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert zoneIdsTableView != null : "fx:id=\"timeZonesTableView\" was not injected: check your FXML file 'FXMLDocument.fxml'.";

        availableLocales = FXCollections.observableArrayList();
        filteredLocales = FXCollections.observableArrayList();
        twoLetterCountryCodes = FXCollections.observableArrayList();
        threeLetterCountryCodes = FXCollections.observableArrayList();
        iso3CountryCodes = FXCollections.observableArrayList();
        scriptCodes = FXCollections.observableArrayList();
        twoLetterLanguageCodes = FXCollections.observableArrayList();
        threeLetterLanguageCodes = FXCollections.observableArrayList();
        languageTags = FXCollections.observableArrayList();
        availableTimeZones = FXCollections.observableArrayList();
        filteredTimeZones = FXCollections.observableArrayList();
        availableZoneIds = FXCollections.observableArrayList();
        filteredZoneIds = FXCollections.observableArrayList();
        timeZoneIDs = FXCollections.observableArrayList();
        zoneIDs = FXCollections.observableArrayList();
        zoneOffsetIDs = FXCollections.observableArrayList();
        twoLetterCountryCodes.add(ALL_ITEM);
        threeLetterCountryCodes.add(ALL_ITEM);
        iso3CountryCodes.add(ALL_ITEM);
        scriptCodes.add(ALL_ITEM);
        twoLetterLanguageCodes.add(ALL_ITEM);
        threeLetterLanguageCodes.add(ALL_ITEM);
        languageTags.add(ALL_ITEM);
        timeZoneIDs.add(ALL_ITEM);
        zoneIDs.add(ALL_ITEM);
        zoneOffsetIDs.add(ALL_ITEM);
        Arrays.stream(Locale.getAvailableLocales()).sorted(new Comparator<Locale>() {
            @Override
            public int compare(Locale o1, Locale o2) {
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
                    if (c1 == c2) {
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
        }).forEach((Locale l) -> {
            availableLocales.add(l);
            filteredLocales.add(l);
            String s = l.toLanguageTag();
            if (s == null || s.isEmpty()) {
                s = EMPTY_ITEM;
            }
            if (!languageTags.contains(s)) {
                languageTags.add(s);
            }
            s = l.getCountry();
            if (s == null || s.isEmpty()) {
                s = EMPTY_ITEM;
            }
            if (s.length() == 3) {
                if (!threeLetterCountryCodes.contains(s)) {
                    threeLetterCountryCodes.add(s);
                }
            } else if (!twoLetterCountryCodes.contains(s)) {
                twoLetterCountryCodes.add(s);
            }
            try {
                s = l.getISO3Country();
            } catch (Throwable ex) {
                s = null;
            }
            if (s == null || s.isEmpty()) {
                s = EMPTY_ITEM;
            }
            if (!iso3CountryCodes.contains(s)) {
                iso3CountryCodes.add(s);
            }
            s = l.getLanguage();
            if (s == null || s.isEmpty()) {
                s = EMPTY_ITEM;
            }
            if (!twoLetterLanguageCodes.contains(s)) {
                twoLetterLanguageCodes.add(s);
            }
            s = l.getISO3Language();
            if (s == null || s.isEmpty()) {
                s = EMPTY_ITEM;
            }
            if (!threeLetterLanguageCodes.contains(s)) {
                threeLetterLanguageCodes.add(s);
            }
            s = l.getScript();
            if (s == null || s.isEmpty()) {
                s = EMPTY_ITEM;
            }
            if (!scriptCodes.contains(s)) {
                scriptCodes.add(s);
            }
        });
        Arrays.stream(TimeZone.getAvailableIDs()).map(new Function<String, TimeZoneInfo>() {
            private int index = -1;
            @Override
            public TimeZoneInfo apply(String id) {
                return new TimeZoneInfo(id, ++index);
            }
        }).forEach((TimeZoneInfo item) -> {
            availableTimeZones.add(item);
            filteredTimeZones.add(item);
            timeZoneIDs.add(item.getId());
        });
        ZoneId.getAvailableZoneIds().stream().map(new Function<String, ZoneIdInfo>() {
            private int index = -1;
            @Override
            public ZoneIdInfo apply(String id) {
                return new ZoneIdInfo(id, ++index);
            }
            
        }).forEach((ZoneIdInfo item) -> {
            availableZoneIds.add(item);
            filteredZoneIds.add(item);
            zoneIDs.add(item.getAvailableZoneId());
            String s = item.getNormalizedId();
            if (s != null && !s.isEmpty() && !zoneOffsetIDs.contains(s)) {
                zoneOffsetIDs.add(s);
            }
        });
        languageTagTableColumn.setCellValueFactory((TableColumn.CellDataFeatures<Locale, String> param) -> {
            Locale locale = param.getValue();
            return new SimpleObjectProperty<>((locale == null) ? null : locale.toLanguageTag());
        });
        localeToStringTableColumn.setCellValueFactory((TableColumn.CellDataFeatures<Locale, String> param) -> {
            Locale locale = param.getValue();
            return new SimpleObjectProperty<>((locale == null) ? null : locale.toString());
        });
        twoLetterCountryCodesComboBox.setItems(twoLetterCountryCodes);
        threeLetterCountryCodesComboBox.setItems(threeLetterCountryCodes);
        iso3CountryCodesComboBox.setItems(iso3CountryCodes);
        scriptCodesComboBox.setItems(scriptCodes);
        twoLetterLanguageCodesComboBox.setItems(twoLetterLanguageCodes);
        threeLetterLanguageCodesComboBox.setItems(threeLetterLanguageCodes);
        languageTagsComboBox.setItems(languageTags);
        availableLocalesTableView.setItems(filteredLocales);
        availableLocalesTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        availableLocalesTableView.setOnKeyPressed(new TableCopyEventHandler());
        timeZonesTableView.setItems(filteredTimeZones);
        timeZonesTableView.setOnKeyPressed(new TableCopyEventHandler());
        timeZoneIDsComboBox.setItems(timeZoneIDs);
        zoneOffsetIDsComboBox.setItems(zoneOffsetIDs);
        zoneIDsComboBox.setItems(zoneIDs);
        zoneIdsTableView.setItems(filteredZoneIds);
        zoneIdsTableView.setOnKeyPressed(new TableCopyEventHandler());
    }

    public static class TableCopyEventHandler implements EventHandler<KeyEvent> {
        KeyCodeCombination copyKeyCodeCompination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);

        private String asString(Object obj) {
            if (null == obj)
                return "";
            String s;
            if (obj instanceof String)
                s = (String)obj;
            else
                s = obj.toString();
            return s.replace("\t", "\\t").replace("\r", "\\r").replace("\n", "\\n");
        }
        @Override
        public void handle(final KeyEvent event) {
            if (copyKeyCodeCompination.match(event)) {
                final TableView<?> tableView = (TableView<?>)event.getSource();
                final ObservableList<?> items = tableView.getItems();
                final TableView.TableViewSelectionModel<?> selectionModel = tableView.getSelectionModel();
                final StringBuilder content = new StringBuilder();
                final TableColumn<?, ?>[] columns = tableView.getColumns().stream().filter(new Predicate<TableColumn<?, ?>>() {
                    private int colIndex = -1;
                    @Override
                    public boolean test(TableColumn<?, ?> t) {
                        if (t.isVisible()) {
                            if (++colIndex > 0)
                                content.append("\t");
                            content.append(t.getText());
                            return true;
                        }
                        return false;
                    }
                }).toArray(TableColumn<?, ?>[]::new);
                selectionModel.getSelectedIndices().forEach((Integer t) -> {
                    content.append("\n").append(asString(columns[0].getCellData(t)));
                    for (int i = 1; i < columns.length; i++) {
                        content.append("\t").append(asString(columns[i].getCellData(t)));
                    }
                });
                final ClipboardContent clipboardContent = new ClipboardContent();
                clipboardContent.putString(content.toString());
                Clipboard.getSystemClipboard().setContent(clipboardContent);
                event.consume();
            }
        }
        
    }
    
//    public static class TableKeyEventHandler<T> implements EventHandler<KeyEvent> {
//
//        KeyCodeCombination copyKeyCodeCompination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);
//        private final NumberFormat numberFormatter = NumberFormat.getNumberInstance();
//        private final Function<T, CharSequence> toText;
//        TableKeyEventHandler(Function<T, CharSequence> toText) {
//            this.toText = toText;
//        }
//
//        public void handle(final KeyEvent keyEvent) {
//
//            if (copyKeyCodeCompination.match(keyEvent)) {
//
//                if (keyEvent.getSource() instanceof TableView) {
//
//                    // copy to clipboard
//                    copySelectionToClipboard((TableView<T>) keyEvent.getSource());
//
//                    // event is handled, consume it
//                    keyEvent.consume();
//
//                }
//
//            }
//        }
//
//        private void copySelectionToClipboard(TableView<T> tableView) {
//            final StringBuilder clipboardString = new StringBuilder();
//            tableView.getSelectionModel().getSelectedItems().forEach(new Consumer<T>() {
//                private boolean isSubsequent = false;
//                @Override
//                public void accept(T t) {
//                    if (isSubsequent)
//                        clipboardString.append("\n");
//                    clipboardString.append(toText.apply(t));
//                }
//            });
//            // create clipboard content
//            final ClipboardContent clipboardContent = new ClipboardContent();
//            clipboardContent.putString(clipboardString.toString());
//
//            // set clipboard content
//            Clipboard.getSystemClipboard().setContent(clipboardContent);
//
//        }
//
//    }

    @FXML
    void localeComboBoxChanged(ActionEvent event) {
        Predicate<Locale> filter;
        final String code2c = twoLetterCountryCodesComboBox.getValue();
        if (code2c == null || code2c.isEmpty() || code2c.equals(ALL_ITEM)) {
            filter = null;
        } else if (code2c.equals(EMPTY_ITEM)) {
            filter = (Locale l) -> isNullOrEmpty(l.getCountry());
        } else {
            filter = (Locale l) -> code2c.equals(l.getCountry());
        }

        final String code3c = threeLetterCountryCodesComboBox.getValue();
        if (EMPTY_ITEM.equals(code3c)) {
            filter = createFilter((Locale l) -> isNullOrEmpty(l.getCountry()), filter);
        } else if (code3c != null && !code3c.equals(ALL_ITEM)) {
            filter = createFilter((Locale l) -> code3c.equals(l.getCountry()), filter);
        }

        final String iso3c = iso3CountryCodesComboBox.getValue();
        if (EMPTY_ITEM.equals(iso3c)) {
            filter = createFilter((Locale l) -> isNullOrEmpty(l.getISO3Country()), filter);
        } else if (iso3c != null && !iso3c.equals(ALL_ITEM)) {
            filter = createFilter((Locale l) -> iso3c.equals(l.getISO3Country()), filter);
        }

        final String sc = scriptCodesComboBox.getValue();
        if (EMPTY_ITEM.equals(sc)) {
            filter = createFilter((Locale l) -> isNullOrEmpty(l.getScript()), filter);
        } else if (sc != null && !sc.equals(ALL_ITEM)) {
            filter = createFilter((Locale l) -> sc.equals(l.getScript()), filter);
        }

        final String code2l = twoLetterLanguageCodesComboBox.getValue();
        if (EMPTY_ITEM.equals(code2l)) {
            filter = createFilter((Locale l) -> isNullOrEmpty(l.getLanguage()), filter);
        } else if (code2l != null && !code2l.equals(ALL_ITEM)) {
            filter = createFilter((Locale l) -> code2l.equals(l.getLanguage()), filter);
        }

        final String code3l = threeLetterLanguageCodesComboBox.getValue();
        if (EMPTY_ITEM.equals(code3l)) {
            filter = createFilter((Locale l) -> isNullOrEmpty(l.getISO3Language()), filter);
        } else if (code3l != null && !code3l.equals(ALL_ITEM)) {
            filter = createFilter((Locale l) -> code3l.equals(l.getISO3Language()), filter);
        }

        final String lt = languageTagsComboBox.getValue();
        if (EMPTY_ITEM.equals(lt)) {
            filter = createFilter((Locale l) -> isNullOrEmpty(l.toLanguageTag()), filter);
        } else if (lt != null && !lt.equals(ALL_ITEM)) {
            filter = createFilter((Locale l) -> lt.equals(l.toLanguageTag()), filter);
        }

        filteredLocales.clear();
        if (filter == null) {
            for (Locale l : availableLocales) {
                filteredLocales.add(l);
            }
        } else {
            for (Locale l : availableLocales) {
                if (filter.test(l)) {
                    filteredLocales.add(l);
                }
            }
        }
    }

    @FXML
    void timeZoneComboBoxChanged(ActionEvent event) {
        Predicate<TimeZoneInfo> tzFilter;
        Predicate<ZoneIdInfo> zFilter;
        final String tz = timeZoneIDsComboBox.getValue();
        final String id = zoneIDsComboBox.getValue();
        final String zo = zoneOffsetIDsComboBox.getValue();

        if (tz == null || tz.isEmpty() || tz.equals(ALL_ITEM)) {
            tzFilter = null;
            zFilter = null;
        } else if (tz.equals(EMPTY_ITEM)) {
            zFilter = (ZoneIdInfo l) -> isNullOrEmpty(l.getTimeZoneId());
            tzFilter = null;
        } else {
            zFilter = (ZoneIdInfo l) -> tz.equals(l.getTimeZoneId());
            tzFilter = (TimeZoneInfo l) -> tz.equals(l.getId());
        }

        if (EMPTY_ITEM.equals(id)) {
            tzFilter = createFilter((TimeZoneInfo l) -> isNullOrEmpty(l.getZoneId()), tzFilter);
        } else if (id != null && !id.equals(ALL_ITEM)) {
            tzFilter = createFilter((TimeZoneInfo l) -> id.equals(l.getZoneId()), tzFilter);
            zFilter = createFilter((ZoneIdInfo l) -> id.equals(l.getAvailableZoneId()), zFilter);
        }

        if (EMPTY_ITEM.equals(zo)) {
            tzFilter = createFilter((TimeZoneInfo l) -> isNullOrEmpty(l.getNormalizedId()), tzFilter);
        } else if (id != null && !id.equals(ALL_ITEM)) {
            tzFilter = createFilter((TimeZoneInfo l) -> zo.equals(l.getNormalizedId()), tzFilter);
            zFilter = createFilter((ZoneIdInfo l) -> zo.equals(l.getNormalizedId()), zFilter);
        }

        filteredTimeZones.clear();
        if (tzFilter == null) {
            for (TimeZoneInfo l : availableTimeZones) {
                filteredTimeZones.add(l);
            }
        } else {
            for (TimeZoneInfo l : availableTimeZones) {
                if (tzFilter.test(l)) {
                    filteredTimeZones.add(l);
                }
            }
        }
        filteredZoneIds.clear();
        if (zFilter == null) {
            for (ZoneIdInfo l : availableZoneIds) {
                filteredZoneIds.add(l);
            }
        } else {
            for (ZoneIdInfo l : availableZoneIds) {
                if (zFilter.test(l)) {
                    filteredZoneIds.add(l);
                }
            }
        }
    }
}
