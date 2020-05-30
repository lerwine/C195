package devhelper;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class LanguageTagParseControl extends VBox {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

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

    @SuppressWarnings("LeakingThisInConstructor")
    public LanguageTagParseControl() {
        FXMLLoader loader = new FXMLLoader(LanguageTagParseControl.class.getResource("/devhelper/LanguageTagParse.fxml"), null);
        loader.setController(this);
        loader.setRoot(this);
        try {
            loader.load();
        } catch (IOException ex) {
            Logger.getLogger(DateFormattingControl.class.getName()).log(Level.SEVERE, "Error loading devhelper/LanguageTagParse.fxml", ex);
        }
    }
    
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

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert languageTagInputTextBox != null : "fx:id=\"languageTagInputTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert countryTextBox != null : "fx:id=\"countryTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert languageTextBox != null : "fx:id=\"languageTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert variantTextBox != null : "fx:id=\"variantTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert scriptTextBox != null : "fx:id=\"scriptTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert iso3CountryTextBox != null : "fx:id=\"iso3CountryTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert iso3LanguageTextBox != null : "fx:id=\"iso3LanguageTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert languageTagTextBox != null : "fx:id=\"languageTagTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert stringTextBox != null : "fx:id=\"stringTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert displayCountryTextBox != null : "fx:id=\"displayCountryTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert displayCountryInLocaleTextBox != null : "fx:id=\"displayCountryInLocaleTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert displayLanguageTextBox != null : "fx:id=\"displayLanguageTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert displayLanguageInLocaleTextBox != null : "fx:id=\"displayLanguageInLocaleTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert displayNameTextBox != null : "fx:id=\"displayNameTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert displayNameInLocaleTextBox != null : "fx:id=\"displayNameInLocaleTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert displayScriptTextBox != null : "fx:id=\"displayScriptTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert displayScriptInLocaleTextBox != null : "fx:id=\"displayScriptInLocaleTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert displayVariantTextBox != null : "fx:id=\"displayVariantTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";
        assert displayVariantInLocaleTextBox != null : "fx:id=\"displayVariantInLocaleTextBox\" was not injected: check your FXML file 'LanguageTagParse.fxml'.";

    }
}
