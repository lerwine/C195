/**
 * Sample Skeleton for 'LanguagePicker.fxml' Controller Class
 */

package devhelper;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Objects;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableView;

public class LanguagePickerController {

    private Locale selectedLocale;

    public Locale getSelectedLocale() {
        return selectedLocale;
    }
    
    private ObservableList<CodeAndName> languages;
    private ObservableList<CodeAndName> countries;
    private ObservableList<LocaleInfo> availableLocales;
    
    @FXML // fx:id="simpleTab"
    private Tab simpleTab; // Value injected by FXMLLoader

    @FXML // fx:id="countryCheckBox"
    private CheckBox countryCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="languageTableView"
    private TableView<CodeAndName> languageTableView; // Value injected by FXMLLoader

    @FXML // fx:id="countryTableView"
    private TableView<CodeAndName> countryTableView; // Value injected by FXMLLoader

    @FXML // fx:id="suffixLabel"
    private Label suffixLabel; // Value injected by FXMLLoader

    @FXML // fx:id="nameLabel"
    private Label nameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="allAvailableTab"
    private Tab allAvailableTab; // Value injected by FXMLLoader

    @FXML // fx:id="availableLanguagesTableView"
    private TableView<LocaleInfo> availableLanguagesTableView; // Value injected by FXMLLoader

    @FXML // fx:id="okButton"
    private Button okButton; // Value injected by FXMLLoader

    @FXML
    void cancelButtonClick(ActionEvent event) {
        selectedLocale = null;
        ((Button)event.getSource()).getScene().getWindow().hide();
    }

    @FXML
    void okButtonClick(ActionEvent event) {
        if (allAvailableTab.isSelected()) {
            LocaleInfo locale = availableLanguagesTableView.getSelectionModel().getSelectedItem();
            selectedLocale = (null == locale) ? null : locale.locale;
        } else {
            CodeAndName lang = languageTableView.getSelectionModel().getSelectedItem();
            if (null == lang)
                selectedLocale = null;
            else if (countryCheckBox.isSelected()) {
                CodeAndName country = countryTableView.getSelectionModel().getSelectedItem();
                selectedLocale = Locale.forLanguageTag((null == country) ? lang.code : String.format("%s-%s", lang.code, country.code));
            } else
                selectedLocale = Locale.forLanguageTag(lang.code);
        }
        ((Button)event.getSource()).getScene().getWindow().hide();
    }

    private PrefixAndNameBinding prefixTextBinding;
    private LocaleSelectedBinding localeSelectedBinding;
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert simpleTab != null : "fx:id=\"simpleTab\" was not injected: check your FXML file 'LanguagePicker.fxml'.";
        assert countryCheckBox != null : "fx:id=\"countryCheckBox\" was not injected: check your FXML file 'LanguagePicker.fxml'.";
        assert languageTableView != null : "fx:id=\"languageTableView\" was not injected: check your FXML file 'LanguagePicker.fxml'.";
        assert countryTableView != null : "fx:id=\"countryTableView\" was not injected: check your FXML file 'LanguagePicker.fxml'.";
        assert suffixLabel != null : "fx:id=\"prefixLabel\" was not injected: check your FXML file 'LanguagePicker.fxml'.";
        assert nameLabel != null : "fx:id=\"nameLabel\" was not injected: check your FXML file 'LanguagePicker.fxml'.";
        assert allAvailableTab != null : "fx:id=\"allAvailableTab\" was not injected: check your FXML file 'LanguagePicker.fxml'.";
        assert availableLanguagesTableView != null : "fx:id=\"availableLanguagesTableView\" was not injected: check your FXML file 'LanguagePicker.fxml'.";
        assert okButton != null : "fx:id=\"okButton\" was not injected: check your FXML file 'LanguagePicker.fxml'.";

        availableLocales = FXCollections.observableArrayList();
        for (Locale locale : Locale.getAvailableLocales()) {
            LocaleInfo item = new LocaleInfo(locale);
            if (!item.languageTag.equals("und"))
                availableLocales.add(item);
        }
        availableLanguagesTableView.setItems(availableLocales);
        
        languages = FXCollections.observableArrayList();
        for (String code : Locale.getISOLanguages()) {
            availableLocales.stream().filter((t) -> t.language.equals(code)).findFirst().ifPresent((t) -> {
                languages.add(new CodeAndName(code, t.displayLanguage));
            });
        }
        languageTableView.setItems(languages);
        countries = FXCollections.observableArrayList();
        for (String code : Locale.getISOCountries()) {
            availableLocales.stream().filter((t) -> t.country.equals(code) && !t.displayCountry.equals(code)).findFirst().ifPresent((t) ->
                countries.add(new CodeAndName(code, t.displayCountry)));
        }
        countryTableView.setItems(countries);
        
        prefixTextBinding = new PrefixAndNameBinding();
        localeSelectedBinding = new LocaleSelectedBinding(prefixTextBinding);
        
        countryCheckBox.selectedProperty().addListener((observable) -> {
            countryTableView.setDisable(!((BooleanProperty)observable).get());
        });
        
        prefixTextBinding.addListener((observable) -> {
            CodeAndName item = ((PrefixAndNameBinding)observable).get();
            if (null == item) {
                suffixLabel.setText("");
                nameLabel.setText("");
            } else {
                suffixLabel.setText(item.code);
                nameLabel.setText(item.name);
            }
        });
        
        localeSelectedBinding.addListener((observable) -> {
            okButton.setDisable(!((LocaleSelectedBinding)observable).get());
        });
        
        countryTableView.setDisable(!countryCheckBox.isSelected());
    }
    
    private class LocaleSelectedBinding extends BooleanBinding {
        private final ReadOnlyBooleanProperty tabSelected = allAvailableTab.selectedProperty();
        private final ReadOnlyObjectProperty<LocaleInfo> selectedLocale = availableLanguagesTableView.getSelectionModel().selectedItemProperty();
        private final PrefixAndNameBinding ptb;
        private final ObservableList<Observable> backingList = FXCollections.observableArrayList();
        private final ObservableList<Observable> dependencies = FXCollections.unmodifiableObservableList(backingList);
        public LocaleSelectedBinding(PrefixAndNameBinding ptb) {
            this.ptb = ptb;
            super.bind(tabSelected, selectedLocale, ptb);
            backingList.add(tabSelected);
            backingList.add(selectedLocale);
            backingList.add(ptb);
        }
        
        @Override
        protected boolean computeValue() {
            CodeAndName p = ptb.get();
            LocaleInfo l = selectedLocale.get();
            return (tabSelected.get()) ? null != l : null != p;
        }

        @Override
        public ObservableList<?> getDependencies() {
            return dependencies;
        }

        @Override
        public void dispose() {
            synchronized (dependencies) {
                if (!dependencies.isEmpty()) {
                    dependencies.clear();
                    super.unbind(tabSelected, selectedLocale, ptb);
                }
            }
            super.dispose(); //To change body of generated methods, choose Tools | Templates.
        }
        
    }
    private class PrefixAndNameBinding extends ObjectBinding<CodeAndName> {
        private final ReadOnlyBooleanProperty tabSelected = simpleTab.selectedProperty();
        private final ReadOnlyObjectProperty<CodeAndName> selectedLanguage = languageTableView.getSelectionModel().selectedItemProperty();
        private final BooleanProperty includeCountry = countryCheckBox.selectedProperty();
        private final ReadOnlyObjectProperty<CodeAndName> selectedCountry = countryTableView.getSelectionModel().selectedItemProperty();
        private final ObservableList<Observable> backingList = FXCollections.observableArrayList();
        private final ObservableList<Observable> dependencies = FXCollections.unmodifiableObservableList(backingList);

        PrefixAndNameBinding() {
            super.bind(tabSelected, selectedLanguage, includeCountry, selectedCountry);
            backingList.add(tabSelected);
            backingList.add(selectedLanguage);
            backingList.add(includeCountry);
            backingList.add(selectedCountry);
        }
        
        @Override
        protected CodeAndName computeValue() {
            CodeAndName lang = selectedLanguage.get();
            CodeAndName ctr = selectedCountry.get();
            boolean i = includeCountry.get();
            if (tabSelected.get() && null != lang) {
                Locale l = Locale.forLanguageTag((i && null != ctr) ? String.format("%s-%s", lang.code, ctr.code) : lang.code);
                return new CodeAndName((i && null != ctr) ? String.format("%s_%s", lang.code, ctr.code) : lang.code, l.getDisplayName());
            }
            return null;
        }
        @Override
        public ObservableList<?> getDependencies() {
            return dependencies;
        }

        @Override
        public void dispose() {
            synchronized (dependencies) {
                if (!dependencies.isEmpty()) {
                    dependencies.clear();
                    super.unbind(tabSelected, selectedLanguage, includeCountry, selectedCountry);
                }
            }
            super.dispose(); //To change body of generated methods, choose Tools | Templates.
        }
    }
    
    public class CodeAndName {
        private final String code;
        private final String name;

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }
        
        @Override
        public int hashCode() {
            return code.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final CodeAndName other = (CodeAndName) obj;
            return Objects.equals(this.code, other.code);
        }

        @Override
        public String toString() {
            return code;
        }
        
        public CodeAndName(String code, String name) {
            this.code = code;
            this.name = name;
        }
    }
    
    public class LocaleInfo {
        private final Locale locale;
        private final String displayName;
        private final String displayLanguage;
        private final String language;
        private final String iSO3Language;
        private final String displayCountry;
        private final String country;
        private final String iSO3Country;
        private final String displayVariant;
        private final String variant;
        private final String string;
        private final String displayScript;
        private final String script;
        private final String languageTag;
        private final boolean currentDisplay;
        private final boolean currentFormat;

        public String getDisplayName() {
            return displayName;
        }

        public String getDisplayLanguage() {
            return displayLanguage;
        }

        public String getLanguage() {
            return language;
        }

        public String getiSO3Language() {
            return iSO3Language;
        }

        public String getDisplayCountry() {
            return displayCountry;
        }

        public String getCountry() {
            return country;
        }

        public String getiSO3Country() {
            return iSO3Country;
        }

        public String getDisplayVariant() {
            return displayVariant;
        }

        public String getVariant() {
            return variant;
        }

        public String getString() {
            return string;
        }

        public String getDisplayScript() {
            return displayScript;
        }

        public String getScript() {
            return script;
        }

        public String getLanguageTag() {
            return languageTag;
        }

        public boolean isCurrentDisplay() {
            return currentDisplay;
        }

        public boolean isCurrentFormat() {
            return currentFormat;
        }

        @Override
        public int hashCode() {
            return languageTag.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final LocaleInfo other = (LocaleInfo) obj;
            return Objects.equals(this.languageTag, other.languageTag);
        }

        @Override
        public String toString() {
            return string;
        }
        
        public LocaleInfo(Locale locale) {
            this.locale = locale;
            displayName = locale.getDisplayName();
            displayLanguage = locale.getDisplayLanguage();
            language = locale.getLanguage();
            String s;
            try { s = locale.getISO3Language(); }
            catch (MissingResourceException ex) {
                s = "";
            }
            iSO3Language = s;
            displayCountry = locale.getDisplayCountry();
            country = locale.getCountry();
            try { s = locale.getISO3Country(); }
            catch (MissingResourceException ex) {
                s = "";
            }
            iSO3Country = s;
            displayVariant = locale.getDisplayVariant();
            variant = locale.getVariant();
            string = locale.toString();
            displayScript = locale.getDisplayScript();
            script = locale.getScript();
            languageTag = locale.toLanguageTag();
            currentDisplay = Locale.getDefault(Locale.Category.DISPLAY).equals(locale);
            currentFormat = Locale.getDefault(Locale.Category.FORMAT).equals(locale);
        }
    }
}
