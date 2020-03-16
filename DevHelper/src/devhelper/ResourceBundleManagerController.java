/**
 * Sample Skeleton for 'ResourceBundleManager.fxml' Controller Class
 */
package devhelper;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ResourceBundleManagerController {

    private String rootPath;
    private ObservableList<BundleSet> bundleSets = FXCollections.observableArrayList();
    
    private void load(File rootPath) {
        ObservableList<BundleSet> list  = FXCollections.observableArrayList();
//        BundleSet.loadInto(rootPath, list);
    }
    
    
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="languageComboBox"
    private ComboBox<Locale> languageComboBox; // Value injected by FXMLLoader

    @FXML // fx:id="deleteLanguageButton"
    private Button deleteLanguageButton; // Value injected by FXMLLoader

    @FXML // fx:id="leftBundleTableView"
    private TableView<ResourceProperty> leftBundleTableView; // Value injected by FXMLLoader

    @FXML // fx:id="editLeftPropertyMenuItem"
    private MenuItem editLeftPropertyMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="deleteLeftPropertyMenuItem"
    private MenuItem deleteLeftPropertyMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="moveToRightButton"
    private Button moveToRightButton; // Value injected by FXMLLoader

    @FXML // fx:id="copyToRightButton"
    private Button copyToRightButton; // Value injected by FXMLLoader

    @FXML // fx:id="moveLeftUpButton"
    private Button moveLeftUpButton; // Value injected by FXMLLoader

    @FXML // fx:id="moveLeftDownButton"
    private Button moveLeftDownButton; // Value injected by FXMLLoader

    @FXML // fx:id="editLeftButton"
    private Button editLeftButton; // Value injected by FXMLLoader

    @FXML // fx:id="insertLeftButton"
    private Button insertLeftButton; // Value injected by FXMLLoader

    @FXML // fx:id="deleteLeftButton"
    private Button deleteLeftButton; // Value injected by FXMLLoader

    @FXML // fx:id="leftBaseNameLabel"
    private Label leftBaseNameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="leftPathLabel"
    private Label leftPathLabel; // Value injected by FXMLLoader

    @FXML // fx:id="moveToLeftButton"
    private Button moveToLeftButton; // Value injected by FXMLLoader

    @FXML // fx:id="copyToLeftButton"
    private Button copyToLeftButton; // Value injected by FXMLLoader

    @FXML // fx:id="moveRightUpButton"
    private Button moveRightUpButton; // Value injected by FXMLLoader

    @FXML // fx:id="moveRightDownButton"
    private Button moveRightDownButton; // Value injected by FXMLLoader

    @FXML // fx:id="editRightButton"
    private Button editRightButton; // Value injected by FXMLLoader

    @FXML // fx:id="insertRightButton"
    private Button insertRightButton; // Value injected by FXMLLoader

    @FXML // fx:id="deleteRightButton"
    private Button deleteRightButton; // Value injected by FXMLLoader

    @FXML // fx:id="rightBundleTableView"
    private TableView<ResourceProperty> rightBundleTableView; // Value injected by FXMLLoader

    @FXML // fx:id="rightBaseNameLabel"
    private Label rightBaseNameLabel; // Value injected by FXMLLoader

    @FXML // fx:id="rightPathLabel"
    private Label rightPathLabel; // Value injected by FXMLLoader

    @FXML
    void addLanguageButtonClick(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LanguagePicker.fxml"));
        Parent root;
        try {
            root = loader.load();
        } catch (IOException ex) {
            Logger.getLogger(ResourceBundleManagerController.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        LanguagePickerController controller = loader.getController();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initOwner(((Button) event.getSource()).getScene().getWindow());
        stage.setScene(scene);
        stage.showAndWait();
        Locale locale = controller.getSelectedLocale();
        if (null != locale) {
            String t = locale.toLanguageTag();
//            if (!bundleLocales.stream().anyMatch((i) -> i.toLanguageTag().equals(t))) {
//                leftBundle.addLanguage(locale);
//                rightBundle.addLanguage(locale);
//                bundleLocales.add(locale);
//                languageComboBox.getSelectionModel().select(locale);
//            }
        }
    }

    private void refresh() {

    }

    @FXML
    void addLeftButtonClick(ActionEvent event) {

    }

    @FXML
    void addRightButtonClick(ActionEvent event) {

    }

    @FXML
    void copyToLeftButtonClick(ActionEvent event) {

    }

    @FXML
    void copyToRightButtonClick(ActionEvent event) {

    }

    @FXML
    void deleteLanguageButtonClick(ActionEvent event) {

    }

    @FXML
    void deleteLeftButtonClick(ActionEvent event) {

    }

    @FXML
    void deleteLeftPropertyMenuItemClick(ActionEvent event) {

    }

    @FXML
    void deleteRightButtonClick(ActionEvent event) {

    }

    @FXML
    void editLeftButtonClick(ActionEvent event) {

    }

    @FXML
    void editLeftPropertyMenuItemClick(ActionEvent event) {

    }

    @FXML
    void editRightButtonClick(ActionEvent event) {

    }

    @FXML
    void insertLeftButtonClick(ActionEvent event) {

    }

    @FXML
    void insertRightButtonClick(ActionEvent event) {

    }

    @FXML
    void languageComboBoxChanged(ActionEvent event) {

    }

    @FXML
    void leftBundleTableViewKeyReleased(KeyEvent event) {

    }

    @FXML
    void moveLeftDownButtonClick(ActionEvent event) {

    }

    @FXML
    void moveLeftUpButtonClick(ActionEvent event) {

    }

    @FXML
    void moveRightDownButtonClick(ActionEvent event) {

    }

    @FXML
    void moveRightUpButtonClick(ActionEvent event) {

    }

    @FXML
    void moveToLeftButtonClick(ActionEvent event) {

    }

    @FXML
    void moveToRightButtonClick(ActionEvent event) {

    }

    @FXML
    void openLeftButtonClick(ActionEvent event) {

    }

    @FXML
    void openRightButtonClick(ActionEvent event) {

    }

    @FXML
    void rightBundleTableViewKeyReleased(KeyEvent event) {

    }

    @FXML
    void saveLeftButtonClick(ActionEvent event) {

    }

    @FXML
    void saveRightButtonClick(ActionEvent event) {

    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert languageComboBox != null : "fx:id=\"languageComboBox\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert deleteLanguageButton != null : "fx:id=\"deleteLanguageButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert leftBundleTableView != null : "fx:id=\"leftBundleTableView\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert editLeftPropertyMenuItem != null : "fx:id=\"editLeftPropertyMenuItem\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert deleteLeftPropertyMenuItem != null : "fx:id=\"deleteLeftPropertyMenuItem\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert moveToRightButton != null : "fx:id=\"moveToRightButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert copyToRightButton != null : "fx:id=\"copyToRightButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert moveLeftUpButton != null : "fx:id=\"moveLeftUpButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert moveLeftDownButton != null : "fx:id=\"moveLeftDownButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert editLeftButton != null : "fx:id=\"editLeftButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert insertLeftButton != null : "fx:id=\"insertLeftButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert deleteLeftButton != null : "fx:id=\"deleteLeftButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert leftBaseNameLabel != null : "fx:id=\"leftBaseNameLabel\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert leftPathLabel != null : "fx:id=\"leftPathLabel\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert moveToLeftButton != null : "fx:id=\"moveToLeftButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert copyToLeftButton != null : "fx:id=\"copyToLeftButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert moveRightUpButton != null : "fx:id=\"moveRightUpButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert moveRightDownButton != null : "fx:id=\"moveRightDownButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert editRightButton != null : "fx:id=\"editRightButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert insertRightButton != null : "fx:id=\"insertRightButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert deleteRightButton != null : "fx:id=\"deleteRightButton\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert rightBundleTableView != null : "fx:id=\"rightBundleTableView\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert rightBaseNameLabel != null : "fx:id=\"rightBaseNameLabel\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        assert rightPathLabel != null : "fx:id=\"rightPathLabel\" was not injected: check your FXML file 'ResourceBundleManager.fxml'.";
        languageComboBox.setCellFactory((ListView<Locale> param) -> {
            return new ListCell<Locale>() {
                @Override
                protected void updateItem(Locale item, boolean empty) {
                    super.updateItem(item, empty);
                    setText((null == item) ? "" : String.format("%s [%s]", item.getDisplayName(), item.toLanguageTag()));
                }
            };
        });
        languageComboBox.setButtonCell(new ListCell<Locale>() {
            @Override
            protected void updateItem(Locale item, boolean empty) {
                super.updateItem(item, empty);
                setText((null == item) ? "" : String.format("%s [%s]", item.getDisplayName(), item.toLanguageTag()));
            }
        });
//        leftBundle = new ObsoleteBundleSet();
//        leftProperties = FXCollections.observableArrayList();
//        rightBundle = new ObsoleteBundleSet();
//        leftProperties = FXCollections.observableArrayList();
//        bundleLocales = FXCollections.observableArrayList();

        Locale locale = Locale.getDefault(Locale.Category.FORMAT);
        String f = locale.getCountry();
        if (null == f || f.isEmpty()) {
            f = locale.getLanguage();
        } else {
            f = String.format("%s-%s", locale.getLanguage(), f);
        }
        locale = Locale.getDefault(Locale.Category.DISPLAY);
        String d = locale.getCountry();
        if (null == d || d.isEmpty()) {
            d = locale.getLanguage();
        } else {
            d = String.format("%s-%s", locale.getLanguage(), d);
        }
        locale = Locale.forLanguageTag(d);
//        bundleLocales.add(locale);
//        if (!f.equals(d)) {
//            bundleLocales.add(Locale.forLanguageTag(f));
//        }
//        languageComboBox.setItems(bundleLocales);
//        languageComboBox.getSelectionModel().select(locale);
//        leftBundle.addLanguage(locale);
//        rightBundle.addLanguage(locale);
//        leftBundleTableView.setItems(leftProperties);
//        rightBundleTableView.setItems(rightProperties);
    }

    public class ResourceProperty {

        private final ReadOnlyIntegerWrapper order = new ReadOnlyIntegerWrapper();

        public int getOrder() {
            return order.get();
        }

        public ReadOnlyIntegerProperty orderProperty() {
            return order.getReadOnlyProperty();
        }

        private final ReadOnlyStringWrapper key = new ReadOnlyStringWrapper();

        public String getKey() {
            return key.get();
        }

        public ReadOnlyStringProperty keyProperty() {
            return key.getReadOnlyProperty();
        }

        private final StringProperty value = new SimpleStringProperty();

        public String getValue() {
            return value.get();
        }

        public void setValue(String value) {
            this.value.set(value);
        }

        public StringProperty valueProperty() {
            return value;
        }

    }
}
