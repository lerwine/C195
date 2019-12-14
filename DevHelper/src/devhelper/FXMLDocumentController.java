/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

import java.io.File;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;

/**
 *
 * @author Leonard T. Erwine
 */
public class FXMLDocumentController implements Initializable {
    private static final Pattern PATTERN_BACKSLASH = Pattern.compile("\\\\");
    
    @FXML
    private TableView<PropertiesFile> propertySetsTableView;

    @FXML
    private TableColumn<PropertiesFile, String> languageTableColumn;

    @FXML
    private TableColumn<PropertiesFile, LocalDateTime> updatedTableColumn;

    @FXML
    private TableColumn<PropertiesFile, String> missingKeysTableColumn;

    @FXML
    private TableColumn<PropertyModel, String> validationMessageTableColumn;

    @FXML
    private TableView<PropertyModel> currentPropertySetTableView;

    @FXML
    private TableColumn<PropertyModel, String> keyTableColumn;

    @FXML
    private TableColumn<PropertyModel, String> valueTableColumn;

    @FXML
    private TableColumn<PropertyModel, String> currentSetValidationMessageTableColumn;

    @FXML
    private TextField keyTextField;
    
    @FXML
    private Label errorMessageLabel;
    
    @FXML
    private TextArea valueTextArea;

    
    @FXML
    private Button addPropertySetButton;
    
    @FXML
    private Button deletePropertySetButton;
    
    @FXML
    private Button deletePropertyButton;
    
    private final ObservableList<PropertiesFile> allPropertySets = FXCollections.observableArrayList();
    private final ObservableList<PropertyModel> currentSetProperties = FXCollections.observableArrayList();;
    private PropertiesFile targetPropertySet;
    
    public FXMLDocumentController() {
    }
    
    @FXML
    private void keyTextFieldAction(ActionEvent event) {
    }
    
    @FXML
    private void translateButtonAction(ActionEvent event) {
    }
    
    @FXML
    private void addPropertySetButtonAction(ActionEvent event) {
    }
    
    @FXML
    private void deletePropertySetButtonAction(ActionEvent event) {
    }
    
    @FXML
    private void saveButtonAction(ActionEvent event) {
    }
    
    @FXML
    private void openButtonAction(ActionEvent event) {
        
    }
    
    @FXML
    private void newButtonAction(ActionEvent event) {
    }
    
    @FXML
    private void addPropertyButtonAction(ActionEvent event) {
    }
    
    @FXML
    private void deletePropertyButtonAction(ActionEvent event) {
    }
    
    private static ArrayList<PropertiesFile> getBundleMatches(PropertiesFile target, boolean caseSensitive) throws Exception {
        if (!target.getLocale().isPresent())
            return new ArrayList<>();
        File file = target.getSource();
        String path = file.getAbsolutePath();
        if (file.isDirectory()) {
            if (!file.exists())
                return new ArrayList<>();
        } else if ((path = (new File(path)).getParent()) == null || !(file = new File(path)).exists())
            return new ArrayList<>();
        
        ArrayList<PropertiesFile> result = new ArrayList<>();
        File[] files = file.listFiles(PropertiesFile.getPropertiesFileFilter(target.getBaseName(), caseSensitive));
        if (files == null || files.length == 0) {
            result.add(target);
            return result;
        }
        final String fileName = target.getSource().getName();
        Set<String> expectedKeys = target.getProperties().stringPropertyNames();
        for (File f : files)
            result.add((fileName.equals(f.getName())) ? target : PropertiesFile.load(f, expectedKeys, false));
        
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getSource().getName().equals(fileName)){
                result.set(i, target);
                return result;
            }
        }
        String lc = fileName.toLowerCase();
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getSource().getName().toLowerCase().equals(lc)) {
                result.set(i, target);
                return result;
            }
        }
        result.add(target);
        return result;
    }
    
    private boolean openBundle() throws Exception {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open Resource Bundle");
        File file = fc.showOpenDialog(DevHelper.getInstance().getPrimaryStage());
        if (file == null) {
            DevHelper.getInstance().getPrimaryStage().close();
            return false;
        }
        
        PropertiesFile newPropertySet;
        try {
            newPropertySet = PropertiesFile.load(file, null, false);
        } catch (Exception ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        if (!allPropertySets.isEmpty())
            allPropertySets.clear();
        if (!currentSetProperties.isEmpty())
            currentSetProperties.clear();
        if (!newPropertySet.isCritical() && newPropertySet.getLocale().isPresent()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "File name matches a resource bundle pattern\nLoad as a resource bundle?", ButtonType.YES, ButtonType.NO);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Resource type");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                ArrayList<PropertiesFile> lps = getBundleMatches(newPropertySet, false);
                if (lps == null || lps.isEmpty())
                    allPropertySets.add(newPropertySet);
                else
                    allPropertySets.addAll(lps);
                targetPropertySet = newPropertySet;
                onBundleLoaded();
                return true;
            }
        }
        
        allPropertySets.add(newPropertySet);
        targetPropertySet = newPropertySet;
        onBundleLoaded();
        return true;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            if (!openBundle()) {
                DevHelper.getInstance().getPrimaryStage().close();
            }
        } catch (Exception ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            DevHelper.getInstance().getPrimaryStage().close();
        }
    }

    private void onBundleLoaded() {
        if (targetPropertySet.getLocale().isPresent()) {
            addPropertySetButton.setVisible(true);
        }
    }
}
