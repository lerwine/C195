package devhelper;

import java.net.URL;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.TextStyle;
import java.util.Arrays;
import java.util.Locale;
import java.util.Locale.Category;
import java.util.ResourceBundle;
import java.util.TimeZone;
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
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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

    public static final String ALL_ITEM = " (all) ";
    public static final String EMPTY_ITEM = " (empty) ";

    public static <T> Predicate<T> createFilter(Predicate<T> newFilter, Predicate<T> currentFilter) {
        if (newFilter == null) {
            return currentFilter;
        }
        if (currentFilter == null) {
            return newFilter;
        }
        return (T t) -> newFilter.test(t) && currentFilter.test(t);
    }

    private static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="resourceBundlesTab"
    private Tab resourceBundlesTab; // Value injected by FXMLLoader

    @FXML // fx:id="dateFormattingTab"
    private Tab dateFormattingTab; // Value injected by FXMLLoader

    @FXML // fx:id="languageTagTab"
    private Tab languageTagTab; // Value injected by FXMLLoader

    @FXML // fx:id="inputTextBox"
    private TextField inputTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="hashTextBox"
    private TextField hashTextBox; // Value injected by FXMLLoader

    @FXML // fx:id="localesTab"
    private Tab localesTab; // Value injected by FXMLLoader

    @FXML // fx:id="twoLetterLanguageCodesComboBox"
    private ComboBox<?> twoLetterLanguageCodesComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="threeLetterLanguageCodesComboBox"
    private ComboBox<?> threeLetterLanguageCodesComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="languageTagsComboBox"
    private ComboBox<?> languageTagsComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="timeZonesTableView"
    private TableView<TimeZoneInfo> timeZonesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="timeZoneIDsComboBox"
    private ComboBox<String> timeZoneIDsComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="zoneIDsComboBox"
    private ComboBox<String> zoneIDsComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="zoneOffsetIDsComboBox"
    private ComboBox<String> zoneOffsetIDsComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="zoneIdsTableView"
    private TableView<ZoneIdInfo> zoneIdsTableView; // Value injected by FXMLLoader

    private ObservableList<TimeZoneInfo> availableTimeZones;
    private ObservableList<TimeZoneInfo> filteredTimeZones;
    private ObservableList<ZoneIdInfo> availableZoneIds;
    private ObservableList<ZoneIdInfo> filteredZoneIds;
    private ObservableList<String> timeZoneIDs;
    private ObservableList<String> zoneIDs;
    private ObservableList<String> zoneOffsetIDs;

    @FXML
    void getHashButtonClick(ActionEvent event) {
        hashTextBox.setText((new PwHash(inputTextBox.getText(), true)).getEncodedHash());
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

    @FXML
    void initialize() {
        assert resourceBundlesTab != null : "fx:id=\"resourceBundlesTab\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert dateFormattingTab != null : "fx:id=\"dateFormattingTab\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert languageTagTab != null : "fx:id=\"languageTagTab\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert inputTextBox != null : "fx:id=\"inputTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert hashTextBox != null : "fx:id=\"hashTextBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert localesTab != null : "fx:id=\"localesTab\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert twoLetterLanguageCodesComboBox != null : "fx:id=\"twoLetterLanguageCodesComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert threeLetterLanguageCodesComboBox != null : "fx:id=\"threeLetterLanguageCodesComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert languageTagsComboBox != null : "fx:id=\"languageTagsComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert timeZonesTableView != null : "fx:id=\"timeZonesTableView\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert timeZoneIDsComboBox != null : "fx:id=\"timeZoneIDsComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert zoneIDsComboBox != null : "fx:id=\"zoneIDsComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert zoneOffsetIDsComboBox != null : "fx:id=\"zoneOffsetIDsComboBox\" was not injected: check your FXML file 'FXMLDocument.fxml'.";
        assert zoneIdsTableView != null : "fx:id=\"zoneIdsTableView\" was not injected: check your FXML file 'FXMLDocument.fxml'.";

        dateFormattingTab.setContent(new DateFormattingControl());
        languageTagTab.setContent(new LanguageTagParseControl());
        localesTab.setContent(new LocaleListingControl());

        availableTimeZones = FXCollections.observableArrayList();
        filteredTimeZones = FXCollections.observableArrayList();
        availableZoneIds = FXCollections.observableArrayList();
        filteredZoneIds = FXCollections.observableArrayList();
        timeZoneIDs = FXCollections.observableArrayList();
        zoneIDs = FXCollections.observableArrayList();
        zoneOffsetIDs = FXCollections.observableArrayList();
        timeZoneIDs.add(ALL_ITEM);
        zoneIDs.add(ALL_ITEM);
        zoneOffsetIDs.add(ALL_ITEM);
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
        timeZonesTableView.setItems(filteredTimeZones);
        timeZonesTableView.setOnKeyPressed(new TableCopyEventHandler());
        timeZoneIDsComboBox.setItems(timeZoneIDs);
        zoneOffsetIDsComboBox.setItems(zoneOffsetIDs);
        zoneIDsComboBox.setItems(zoneIDs);
        zoneIdsTableView.setItems(filteredZoneIds);
        zoneIdsTableView.setOnKeyPressed(new TableCopyEventHandler());
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
                    ZoneOffset zo = (ZoneOffset) z;
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

    public static class TableCopyEventHandler implements EventHandler<KeyEvent> {

        KeyCodeCombination copyKeyCodeCompination = new KeyCodeCombination(KeyCode.C, KeyCombination.CONTROL_ANY);

        private String asString(Object obj) {
            if (null == obj) {
                return "";
            }
            String s;
            if (obj instanceof String) {
                s = (String) obj;
            } else {
                s = obj.toString();
            }
            return s.replace("\t", "\\t").replace("\r", "\\r").replace("\n", "\\n");
        }

        @Override
        public void handle(final KeyEvent event) {
            if (copyKeyCodeCompination.match(event)) {
                final TableView<?> tableView = (TableView<?>) event.getSource();
                final ObservableList<?> items = tableView.getItems();
                final TableView.TableViewSelectionModel<?> selectionModel = tableView.getSelectionModel();
                final StringBuilder content = new StringBuilder();
                final TableColumn<?, ?>[] columns = tableView.getColumns().stream().filter(new Predicate<TableColumn<?, ?>>() {
                    private int colIndex = -1;

                    @Override
                    public boolean test(TableColumn<?, ?> t) {
                        if (t.isVisible()) {
                            if (++colIndex > 0) {
                                content.append("\t");
                            }
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
}
