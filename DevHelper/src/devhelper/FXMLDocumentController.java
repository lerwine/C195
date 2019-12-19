/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Leonard T. Erwine
 */
public class FXMLDocumentController implements Initializable {
    private static final Pattern PATTERN_BACKSLASH = Pattern.compile("\\\\");
    static final Pattern PATTERN_RESOURCE_BUNDLE = Pattern.compile("^([^_.]+)(?:_([^.]+))?(.properties)?");
    
    //<editor-fold defaultstate="collapsed" desc="Property Sets Pane">
    
    private File currentLocation;
    
    private final ObservableList<PropertiesFile> allPropertiesFiles = FXCollections.observableArrayList();

    @FXML
    private TableView<PropertiesFile> propertiesFilesTableView;
    
    @FXML
    private TableColumn<PropertiesFile, String> languageTableColumn;
    
    @FXML
    private TableColumn<PropertiesFile, LocalDateTime> updatedTableColumn;
    
    @FXML
    private TableColumn<PropertiesFile, String> propertiesFileStatusTableColumn;
    
    @FXML
    private Button translateResourceBundleButton;
    
    @FXML
    private void translateResourceBundleButtonAction(ActionEvent event) {
    }
    
    @FXML
    private Button addPropertiesFileButton;
    
    @FXML
    private void addPropertiesFileButtonAction(ActionEvent event) {
    }
    
    @FXML
    private Button saveResourceBundleButton;
    
    @FXML
    private void saveResourceBundleButtonAction(ActionEvent event) {
    }
    
    @FXML
    private void openResourceBundleButtonAction(ActionEvent event) {
        
    }
    
    @FXML
    private void newResourceBundleButtonAction(ActionEvent event) {
        Parent root;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("NewResourceBundle.fxml"));
            root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("New Resource Bundle");
            stage.setScene(new Scene(root, 320, 240));
            NewResourceBundleController controller = (NewResourceBundleController)loader.getController();
            controller.setCurrentSubdirectory(currentLocation.getAbsolutePath());
            stage.showAndWait();
            if (!controller.isCanceled())
                load(controller.getSelectedFileNames());
            
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Current Property Set Pane">
    
    private ObservableList<PropertyItem> currentFileProperties = FXCollections.observableArrayList();
    
    //private PropertiesFile getSelectedPropertiesFile() { return propertiesFilesTableView.getSelectionModel().getSelectedItem(); }
    
    //private void setSelectedPropertiesFile(PropertiesFile item) { propertiesFilesTableView.getSelectionModel().select(item); }
    
    @FXML
    private TableView<PropertyItem> currentPropertiesFileTableView;
    
    @FXML
    private TableColumn<PropertyItem, String> keyTableColumn;
    
    @FXML
    private TableColumn<PropertyItem, String> valueTableColumn;
    
    @FXML
    private TableColumn<PropertyItem, String> propertyStatusTableColumn;
    
    // private PropertyItem getSelectedPropertyModel() { return currentPropertiesFileTableView.getSelectionModel().getSelectedItem(); }
    
    // private void setSelectedPropertyModel(PropertyItem item) { currentPropertiesFileTableView.getSelectionModel().select(item); }
    
    @FXML
    private TextField keyTextField;
    
    @FXML
    private void keyTextFieldAction(ActionEvent event) {
        PropertyItem item = currentPropertiesFileTableView.getSelectionModel().getSelectedItem();
        item.setKey(keyTextField.getText());
        errorMessageLabel.setText(item.getMessage());
        errorMessageLabel.setVisible(!item.isValid());
    }
    
    @FXML
    private Label errorMessageLabel;
    
    @FXML
    private TextArea valueTextArea;

    @FXML
    private void valueTextAreaAction(ActionEvent event) {
        PropertyItem model = currentPropertiesFileTableView.getSelectionModel().getSelectedItem();
        model.setValue(valueTextArea.getText());
    }
    
    @FXML
    private Button addPropertyButton;
    
    @FXML
    private void addPropertyButtonAction(ActionEvent event) {
        PropertyItem model = new PropertyItem("", "");
        currentFileProperties.add(model);
        currentPropertiesFileTableView.getSelectionModel().select(model);
    }
    
    @FXML
    private Button deletePropertyButton;
    
    @FXML
    private void deletePropertyButtonAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO);
        alert.setTitle("Confirm Delete");
        alert.showAndWait().ifPresent((ButtonType r) -> {
            if (r.equals(ButtonType.YES)) {
                PropertiesFile file = propertiesFilesTableView.getSelectionModel().getSelectedItem();
                PropertyItem model = currentPropertiesFileTableView.getSelectionModel().getSelectedItem();
                file.source.remove(model.getKey());
                int index = currentPropertiesFileTableView.getSelectionModel().getSelectedIndex();
                currentFileProperties.remove(model);
                currentPropertiesFileTableView.getSelectionModel().clearAndSelect((index < currentFileProperties.size()) ? index : index - 1);
            }
        });
    }

    private void load(ArrayList<File> files) {
        allPropertiesFiles.clear();
        propertiesFilesTableView.getSelectionModel().clearSelection();
        if (files != null && !files.isEmpty()) {
            ArrayList<PropertiesFile> items = new ArrayList<>();
            files.stream().forEach((File f) -> {
                items.add(new PropertiesFile(f));
            });
            allPropertiesFiles.addAll(items);
        }
        updateFileSelection();
    }
    
    private PropertiesFile selectedFile;
    
    private void updateFileSelection() {
        if (selectedFile != null)
            selectedFile.valid.removeListener(fileValidationChanged);
        if (allPropertiesFiles.isEmpty()) {
            translateResourceBundleButton.setDisable(true);
            addPropertiesFileButton.setDisable(true);
            saveResourceBundleButton.setDisable(true);
            currentFileProperties = FXCollections.observableArrayList();
        } else {
            selectedFile = propertiesFilesTableView.getSelectionModel().getSelectedItem();
            if (selectedFile == null) {
                selectedFile = allPropertiesFiles.get(0);
                propertiesFilesTableView.getSelectionModel().select(selectedFile);
            } else
                selectedFile.valid.addListener(fileValidationChanged);
            if (selectedFile.isValid()) {
                translateResourceBundleButton.setDisable(false);
                saveResourceBundleButton.setDisable(allPropertiesFiles.stream().anyMatch((PropertiesFile f) -> !f.isValid()));
            } else {
                translateResourceBundleButton.setDisable(true);
                saveResourceBundleButton.setDisable(true);
            }
            addPropertiesFileButton.setDisable(false);
            currentFileProperties = selectedFile.getItems();
        }
        currentPropertiesFileTableView.getSelectionModel().clearSelection();
        currentPropertiesFileTableView.setItems(currentFileProperties);
        updatePropertySelection();
    }
    
    private void updatePropertySelection() {
        if (currentFileProperties.isEmpty()) {
            keyTextField.setText("");
            keyTextField.setDisable(true);
            valueTextArea.setText("");
            valueTextArea.setDisable(true);
            addPropertyButton.setDisable(true);
            deletePropertyButton.setDisable(true);
            errorMessageLabel.setVisible(false);
            errorMessageLabel.setText("");
            errorMessageLabel.setMinHeight(0);
            errorMessageLabel.setPrefHeight(0);
            errorMessageLabel.setMaxHeight(0);
        }
    }

    private void onKeyTextFieldChanged(ObservableValue<? extends String> observable, String oldText, String newText) {
        if (currentFileProperties.isEmpty())
            return;
        PropertyItem item = currentPropertiesFileTableView.getSelectionModel().getSelectedItem();
        if (item == null)
            return;
        item.setKey(newText);
        if (item.isValid()) {
            errorMessageLabel.setVisible(false);
            errorMessageLabel.setText("");
            errorMessageLabel.setMinHeight(0);
            errorMessageLabel.setPrefHeight(0);
            errorMessageLabel.setMaxHeight(0);
        } else {
            errorMessageLabel.setMaxHeight(Label.USE_COMPUTED_SIZE);
            errorMessageLabel.setPrefHeight(Label.USE_COMPUTED_SIZE);
            errorMessageLabel.setMinHeight(Label.USE_COMPUTED_SIZE);
            errorMessageLabel.setText(item.getMessage());
            errorMessageLabel.setVisible(true);
        }
    }
    
    private void onValueTextAreaChanged(ObservableValue<? extends String> observable, String oldText, String newText) {
        if (currentFileProperties.isEmpty())
            return;
        PropertyItem item = currentPropertiesFileTableView.getSelectionModel().getSelectedItem();
        if (item == null)
            return;
        item.setValue(newText);
        if (item.isValid()) {
            errorMessageLabel.setVisible(false);
            errorMessageLabel.setText("");
            errorMessageLabel.setMinHeight(0);
            errorMessageLabel.setPrefHeight(0);
            errorMessageLabel.setMaxHeight(0);
        } else {
            errorMessageLabel.setMaxHeight(Label.USE_COMPUTED_SIZE);
            errorMessageLabel.setPrefHeight(Label.USE_COMPUTED_SIZE);
            errorMessageLabel.setMinHeight(Label.USE_COMPUTED_SIZE);
            errorMessageLabel.setText(item.getMessage());
            errorMessageLabel.setVisible(true);
        }
    }

    private final FileValidationChanged fileValidationChanged;
    
    private static class FileValidationChanged implements ChangeListener<Boolean> {
        private final FXMLDocumentController controller;
        private FileValidationChanged(FXMLDocumentController controller) { this.controller = controller; }

        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            
        }
    }

    public static class PropertiesFile {
        private FXMLDocumentController parent;
        private final Properties source;
        
        private static FilenameFilter getPropertiesFileFilter(String name, boolean caseSensitive) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
        
        private final ReadOnlyStringWrapper name;

        public String getName() { return name.get(); }

        public ReadOnlyStringProperty nameProperty() { return name.getReadOnlyProperty(); }
        
        private final ReadOnlyObjectProperty<Optional<Locale>> locale;

        public Optional<Locale> getLocale() { return locale.get(); }

        public ReadOnlyObjectProperty<Optional<Locale>> localeProperty() { return locale; }
        
        private final ReadOnlyStringWrapper message;

        public String getMessage() { return message.get(); }

        public ReadOnlyStringProperty messageProperty() { return message.getReadOnlyProperty(); }
        
        private final ReadOnlyBooleanWrapper valid;

        public boolean isValid() { return valid.get(); }

        public ReadOnlyBooleanProperty validProperty() { return valid.getReadOnlyProperty(); }
        
        private final ReadOnlyListProperty<PropertyItem> items;

        public ObservableList<PropertyItem> getItems() { return items.get(); }

        public ReadOnlyListProperty<PropertyItem> itemsProperty() { return items; }
        
        private final ReadOnlyBooleanWrapper fileValid;

        public boolean isFileValid() { return fileValid.get(); }

        public ReadOnlyBooleanProperty fileValidProperty() { return fileValid.getReadOnlyProperty(); }
        
        private final ReadOnlyBooleanWrapper anyItemInvalid;

        public boolean isAnyItemInvalid() { return anyItemInvalid.get(); }

        public ReadOnlyBooleanProperty anyItemInvalidProperty() { return anyItemInvalid.getReadOnlyProperty(); }
        private final ReadOnlyObjectProperty<File> file;

        public File getFile() { return file.get(); }

        public ReadOnlyObjectProperty fileProperty() { return file; }
        
        private PropertiesFile(File file) {
            anyItemInvalid = new ReadOnlyBooleanWrapper(false);
            valid = new ReadOnlyBooleanWrapper(true);
            ObservableList<PropertyItem> list = FXCollections.observableArrayList();
            source = new Properties();
            String n = file.getName();
            this.file = new ReadOnlyObjectWrapper<>(file);
            Matcher m = PATTERN_RESOURCE_BUNDLE.matcher(n);
            Optional<Locale> l;
            if (m.matches()) {
                n = m.group(1);
                l = (m.group(2) != null) ? Optional.of(new Locale(m.group(2))) : Optional.empty();
            }
            else
                l = Optional.empty();
            name = new ReadOnlyStringWrapper(n);
            locale = new ReadOnlyObjectWrapper<>(l);
            list.addListener((Change<? extends PropertyItem> c) -> {
                if (c.wasAdded())
                    c.getAddedSubList().stream().forEach((PropertyItem added) -> {
                        if (added.parent != null)
                            getItems().remove(added);
                        else {
                            added.parent = this;
                            added.onKeyChanged(null, added.getKey());
                            if (isValid() && !added.isValid())
                                valid.setValue(false);
                        }
                    });
                if (c.wasRemoved())
                    c.getRemoved().stream().forEach((PropertyItem removed) -> {
                        try {
                            removed.parent = null;
                            if (removed.prevDuplicateKey != null) {
                                if ((removed.prevDuplicateKey.nextDuplicateKey = removed.nextDuplicateKey) != null) {
                                    removed.nextDuplicateKey.prevDuplicateKey = removed.prevDuplicateKey;
                                    removed.nextDuplicateKey = null;
                                }
                                removed.prevDuplicateKey = null;
                            } else if (removed.nextDuplicateKey != null) {
                                try {
                                    removed.nextDuplicateKey.prevDuplicateKey = null;
                                    removed.nextDuplicateKey.onKeyChanged(removed.getKey(), removed.nextDuplicateKey.getKey());
                                } finally { removed.nextDuplicateKey = null; }
                            }
                        } finally {
                            if (isFileValid() && !removed.isValid() && list.stream().allMatch((PropertyItem item) -> item.isValid()))
                                valid.setValue(true);
                        }
                    });
            });
            items = new ReadOnlyListWrapper<>(list);
            if (file.exists()) {
                try {
                    FileInputStream stream = new FileInputStream(file);
                    try { source.load(stream); }
                    finally { stream.close(); }
                } catch (IOException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                    message = new ReadOnlyStringWrapper(String.format("Error loading file: %s", ex.getMessage()));
                    fileValid = new ReadOnlyBooleanWrapper(false);
                    valid.setValue(false);
                    return;
                }
                message = new ReadOnlyStringWrapper("");
                fileValid = new ReadOnlyBooleanWrapper(true);
                if (!source.isEmpty())
                    source.stringPropertyNames().stream().forEach((String key) -> {
                        getItems().add(new PropertyItem(key, source.getProperty(key, "")));
                    });
                if (!anyItemInvalid.getValue())
                    valid.setValue(false);
            } else {
                message = new ReadOnlyStringWrapper("");
                fileValid = new ReadOnlyBooleanWrapper(true);
            }
        }

        private void onPropertyValidionChanged(PropertyItem item) {
            if (item.isValid()) {
                if (isFileValid() && !isValid() && getItems().stream().allMatch((PropertyItem p) -> p.isValid()))
                    valid.setValue(true);
            } else if (isValid())
                valid.setValue(false);
        }
    }
    
    public static class PropertyItem {
        private PropertyItem prevDuplicateKey;
        private PropertyItem nextDuplicateKey;

        private PropertiesFile parent;
        
        private final StringProperty key;

        public String getKey() { return key.get(); }

        public void setKey(String value) { key.set(value); }

        public StringProperty keyProperty() { return key; }
        
        private final StringProperty value;

        public String getValue() {
            String v = value.get();
            return (v == null) ? "" : v;
        }

        public void setValue(String value) { this.value.set(value); }

        public StringProperty valueProperty() { return value; }
        
        private final ReadOnlyStringWrapper message;

        public String getMessage() { return message.get(); }

        public ReadOnlyStringProperty messageProperty() { return message.getReadOnlyProperty(); }
        
        private final ReadOnlyBooleanWrapper valid;

        public boolean isValid() { return valid.get(); }

        public ReadOnlyBooleanProperty validProperty() { return valid.getReadOnlyProperty(); }
        
        private void onKeyChanged(String oldValue, String newValue) {
            if (parent == null) {
                if (newValue == null || newValue.trim().isEmpty()) {
                    message.setValue("Key cannot be empty.");
                    valid.setValue(false);
                } else {
                    message.setValue("");
                    valid.setValue(true);
                }
                return;
            }
            
            String msg;
            
            if (prevDuplicateKey != null) {
                if ((prevDuplicateKey.nextDuplicateKey = nextDuplicateKey) != null) {
                    nextDuplicateKey.prevDuplicateKey = prevDuplicateKey;
                    nextDuplicateKey = null;
                }
                prevDuplicateKey = null;
            } else if (nextDuplicateKey != null) {
                PropertyItem item = nextDuplicateKey;
                nextDuplicateKey = item.prevDuplicateKey = null;
                try { item.onKeyChanged(oldValue, item.getKey()); }
                finally { onKeyChanged(oldValue, newValue); }
                return;
            } else if (newValue == null || newValue.trim().isEmpty()) {
                msg = "Key cannot be empty.";
                if (!(valid.getValue() || this.message.getValue().equals(msg)))
                    return;
                message.setValue(msg);
                valid.setValue(false);
                parent.onPropertyValidionChanged(this);
                return;
            }
            
            if (newValue.equalsIgnoreCase(oldValue)) {
                if (prevDuplicateKey != null)
                    return;
            } else {
                if (prevDuplicateKey != null) {
                    if ((prevDuplicateKey.nextDuplicateKey = nextDuplicateKey) != null) {
                        nextDuplicateKey.prevDuplicateKey = prevDuplicateKey;
                        nextDuplicateKey = null;
                    }
                    prevDuplicateKey = null;
                } else if (nextDuplicateKey != null) {
                    PropertyItem item = nextDuplicateKey;
                    nextDuplicateKey = item.prevDuplicateKey = null;
                    try { item.onKeyChanged(oldValue, item.getKey()); }
                    finally { onKeyChanged(oldValue, newValue); }
                    return;
                }

                Optional<PropertyItem> keyMatch = parent.items.stream().filter((PropertyItem item) -> newValue.equalsIgnoreCase(item.getKey())).findFirst();
                if (keyMatch.isPresent()) {
                    prevDuplicateKey = keyMatch.get();
                    while (prevDuplicateKey.nextDuplicateKey != null)
                        prevDuplicateKey = prevDuplicateKey.nextDuplicateKey;
                    prevDuplicateKey.nextDuplicateKey = this;
                    msg = "Duplicate key";
                    if (!(valid.getValue() || this.message.getValue().equals(msg)))
                        return;
                    message.setValue(msg);
                    valid.setValue(false);
                    parent.onPropertyValidionChanged(this);
                    return;
                }
            }
            try { parent.source.put(newValue, getValue()); }
            catch (Exception ex) {
                msg = String.format("Invalid property: %s", ex.getMessage());
                if (!(valid.getValue() || this.message.getValue().equals(msg)))
                    return;
                message.setValue(msg);
                valid.setValue(false);
                parent.onPropertyValidionChanged(this);
                return;
            }
            if (valid.getValue())
                return;
            message.setValue("");
            valid.setValue(true);
            parent.onPropertyValidionChanged(this);
        }
        
        private void onValueChanged(String oldValue, String newValue) {
            if (parent == null || prevDuplicateKey != null)
                return;
            
            String msg = getKey();
            if (msg == null || msg.trim().isEmpty())
                return;
            
            try { parent.source.put(msg, (newValue == null) ? "" : newValue); }
            catch (Exception ex) {
                msg = String.format("Invalid property: %s", ex.getMessage());
                if (!(valid.getValue() || this.message.getValue().equals(msg)))
                    return;
                message.setValue(msg);
                valid.setValue(false);
                parent.onPropertyValidionChanged(this);
                return;
            }
            if (valid.getValue())
                return;
            message.setValue("");
            valid.setValue(true);
            parent.onPropertyValidionChanged(this);
        }
        
        private PropertyItem(String key, String value) {
            this();
            this.key.setValue(key);
            this.value.setValue(value);
        }
        
        public PropertyItem() {
            parent = null;
            prevDuplicateKey = nextDuplicateKey = null;
            key = new SimpleStringProperty("");
            value = new SimpleStringProperty("");
            message = new ReadOnlyStringWrapper("");
            valid = new ReadOnlyBooleanWrapper(true);
            onKeyChanged(null, "");
            key.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                onKeyChanged(oldValue, newValue);
            });
            value.addListener((ObservableValue<? extends String> observable, String oldValue, String newValue) -> {
                onValueChanged(oldValue, newValue);
            });
        }
    }
    
    //</editor-fold>
    
    public FXMLDocumentController() {
        selectedFile = null;
        currentLocation = (new File(".")).getAbsoluteFile().getParentFile();
        fileValidationChanged = new FileValidationChanged(this);
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        keyTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldText, String newText) -> {
            onKeyTextFieldChanged(observable, oldText, newText);
        });
        valueTextArea.textProperty().addListener((ObservableValue<? extends String> observable, String oldText, String newText) -> {
            onValueTextAreaChanged(observable, oldText, newText);
        });
        load(null);
    }
}
