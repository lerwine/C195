/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

import java.io.File;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class NewResourceBundleController implements Initializable {
    private String currentSubdirectory;

    public String getCurrentSubdirectory() { return currentSubdirectory; }

    public void setCurrentSubdirectory(String path) {
        currentSubdirectory = (path == null) ? "" : path;
        if (subdirectoryTextField != null)
            subdirectoryTextField.setText(currentSubdirectory);
    }

    private String currentBaseName;

    public String getCurrentBaseName() { return currentBaseName; }

    public void setCurrentBaseName(String baseName) {
        currentBaseName = (baseName == null) ? "" : baseName;
        if (baseNameTextField != null)
            baseNameTextField.setText(currentBaseName);
    }

    @FXML
    private TextField subdirectoryTextField;

    public NewResourceBundleController() {
        this.languageOptions = FXCollections.observableArrayList();
        currentSubdirectory = (new File(".")).getAbsoluteFile().getParent();
        currentBaseName = "";
        selectedFileNames = new ArrayList<>();
    }
    
    private boolean canceled = true;

    /**
     * Get the value of canceled
     *
     * @return the value of canceled
     */
    public boolean isCanceled() { return canceled; }

    private boolean subdirIsValid = false;
    
    private void onSubdirectoryTextFieldChanged(ObservableValue<? extends String> property, String oldText, String newText) {
        currentSubdirectory = newText;
        if (newText == null || newText.trim().isEmpty())
            subdirectoryErrorLabel.setText("* Required");
        else {
            File f;
            try {
                Path p = Paths.get(newText);
                f = ((p.isAbsolute()) ? p : p.toAbsolutePath()).toFile();
            } catch (InvalidPathException ex) { f = null; }
            if (f == null)
                subdirectoryErrorLabel.setText("* Invalid path");
            else if (f.exists()) {
                if (f.isDirectory()) {
                    currentSubdirectory = f.getAbsolutePath();
                    subdirectoryErrorLabel.setVisible(false);
                    subdirectoryErrorLabel.setText("");
                    subdirectoryErrorLabel.setMinHeight(0);
                    subdirectoryErrorLabel.setPrefHeight(0);
                    subdirectoryErrorLabel.setMaxHeight(0);
                    subdirIsValid = true;
                    if (baseNameIsValid)
                        okButton.setDisable(false);
                    return;
                }
                subdirectoryErrorLabel.setText("* Must refer to subdirectory");
            } else
                subdirectoryErrorLabel.setText("* Subdirectory not found");            }
        
        subdirIsValid = false;
        okButton.setDisable(true);
        subdirectoryErrorLabel.setMaxHeight(Label.USE_COMPUTED_SIZE);
        subdirectoryErrorLabel.setPrefHeight(Label.USE_COMPUTED_SIZE);
        subdirectoryErrorLabel.setMinHeight(Label.USE_COMPUTED_SIZE);
        subdirectoryErrorLabel.setVisible(true);
    }
    
    @FXML
    private void browseSubdirectoryButtonAction(ActionEvent event) {
        DirectoryChooser dc = new DirectoryChooser();
        dc.setTitle("Select subdirectory");
        File file = dc.showDialog(((Button)event.getSource()).getScene().getWindow());
        if (file != null)
            subdirectoryTextField.setText(file.getAbsolutePath());
    }
    
    @FXML
    private Label subdirectoryErrorLabel;
    
    @FXML
    private TextField baseNameTextField;
    
    private boolean baseNameIsValid = false;
    
    private void onBaseNameTextFieldChanged(ObservableValue<? extends String> property, String oldText, String newText) {
        Matcher m;
        currentBaseName = newText;
        
        if (newText == null || newText.trim().isEmpty())
            baseNameErrorLabel.setText("* Required");
        else if (!(m = FXMLDocumentController.PATTERN_RESOURCE_BUNDLE.matcher(newText)).matches() || m.group(2) != null || m.group(3) != null)
            baseNameErrorLabel.setText("* Invalid base name - Cannot contain underscore or period character");
        else {
            Path p;
            try { p = Paths.get(".", newText); }
            catch (InvalidPathException ex) { p = null; }
            if (p == null || !(p.getFileName().toString().equals(newText) && p.getParent().toString().equals(".")))
                baseNameErrorLabel.setText("* Does not represent a valid base file name");
            else {
                baseNameErrorLabel.setVisible(false);
                baseNameErrorLabel.setText("");
                baseNameErrorLabel.setMinHeight(0);
                baseNameErrorLabel.setPrefHeight(0);
                baseNameErrorLabel.setMaxHeight(0);
                baseNameIsValid = true;
                if (subdirIsValid)
                    okButton.setDisable(false);
                return;
            }
        }
        baseNameIsValid = false;
        okButton.setDisable(true);
        baseNameErrorLabel.setMaxHeight(Label.USE_COMPUTED_SIZE);
        baseNameErrorLabel.setPrefHeight(Label.USE_COMPUTED_SIZE);
        baseNameErrorLabel.setMinHeight(Label.USE_COMPUTED_SIZE);
        baseNameErrorLabel.setVisible(true);
    }
    
    @FXML
    private Label baseNameErrorLabel;
    
    @FXML
    private ListView<Locale> languagesListView;
    
    @FXML
    private Button okButton;
    
    @FXML
    private void okButtonAction(ActionEvent event) {
        canceled = false;
        
        final String path = currentSubdirectory;
        final String baseName = currentBaseName;
        File f = new File(baseName + ".properties");
        if (f.exists() || Arrays.stream((new File(path)).list()).anyMatch((String n) -> FXMLDocumentController.PATTERN_RESOURCE_BUNDLE.matcher(n).matches())) {
            baseNameErrorLabel.setMaxHeight(Label.USE_COMPUTED_SIZE);
            baseNameErrorLabel.setPrefHeight(Label.USE_COMPUTED_SIZE);
            baseNameErrorLabel.setMinHeight(Label.USE_COMPUTED_SIZE);
            baseNameErrorLabel.setText(String.format("At least one file matches the specified base name", path));
            baseNameErrorLabel.setVisible(true);
            baseNameIsValid = false;
            okButton.setDisable(true);
            return;
        }
            
        selectedFileNames.clear();
        languagesListView.getSelectionModel().getSelectedItems().stream().forEach((Locale l) ->
            selectedFileNames.add(Paths.get(path, String.format("%s_%s.properties", baseName, l.toLanguageTag())).toFile()));
        if (selectedFileNames.isEmpty())
            selectedFileNames.add(f);
        ((Button)event.getSource()).getScene().getWindow().hide();
    }
    
    @FXML
    private void cancelButtonAction(ActionEvent event) {
        canceled = true;
        selectedFileNames.clear();
        ((Button)event.getSource()).getScene().getWindow().hide();
    }
    
    static class LocaleCell extends ListCell<Locale> {
        @Override
        public void updateItem(Locale item, boolean empty) {
            super.updateItem(item, empty);
            setText((item == null) ? "" : item.getDisplayLanguage());
        }
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        subdirectoryTextField.setText(currentSubdirectory);
        baseNameTextField.setText(currentBaseName);
        Arrays.stream(Locale.getAvailableLocales()).forEach((Locale l) -> {
            String t = l.toLanguageTag();
            if (t != null && t.trim().length() > 0) {
                t = l.getDisplayLanguage();
                if (t != null && t.trim().length() > 0)
                    languageOptions.add(l);
            }
        });
        languagesListView.setCellFactory((ListView<Locale> l) -> new LocaleCell());
        languagesListView.setItems(languageOptions);
        languagesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        selectedFileNames.clear();
        subdirectoryTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldText, String newText) -> {
            onSubdirectoryTextFieldChanged(observable, oldText, newText);
        });
        baseNameTextField.textProperty().addListener((ObservableValue<? extends String> observable, String oldText, String newText) -> {
            onBaseNameTextFieldChanged(observable, oldText, newText);
        });
        onSubdirectoryTextFieldChanged(subdirectoryTextField.textProperty(), null, subdirectoryTextField.getText());
        onBaseNameTextFieldChanged(baseNameTextField.textProperty(), null, baseNameTextField.getText());
    }    
    
    private final ObservableList<Locale> languageOptions;
    
    private final ArrayList<File> selectedFileNames;
    
    public ArrayList<File> getSelectedFileNames() { return selectedFileNames; }
}
