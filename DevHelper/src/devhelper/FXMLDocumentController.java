/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package devhelper;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import javafx.stage.StageStyle;

/**
 *
 * @author Leonard T. Erwine
 */
public class FXMLDocumentController implements Initializable {
    private static final Pattern PATTERN_BACKSLASH = Pattern.compile("\\\\");
    
    @FXML
    private Label label;
    
    private ObservableList<LoadedPropertySet> allPropertySets;
    private LoadedPropertySet targetPropertySet;
    
    public FXMLDocumentController() {
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    private static ArrayList<LoadedPropertySet> getBundleMatches(LoadedPropertySet target, boolean caseSensitive) throws Exception {
        if (!target.getLocale().isPresent())
            return new ArrayList<>();
        File file = target.getSource();
        String path = file.getAbsolutePath();
        if (file.isDirectory()) {
            if (!file.exists())
                return new ArrayList<>();
        } else if ((path = (new File(path)).getParent()) == null || !(file = new File(path)).exists())
            return new ArrayList<>();
        
        ArrayList<LoadedPropertySet> result = new ArrayList<>();
        File[] files = file.listFiles(LoadedPropertySet.getPropertiesFileFilter(target.getBaseName(), caseSensitive));
        if (files == null || files.length == 0) {
            result.add(target);
            return result;
        }
        final String fileName = target.getSource().getName();
        final String baseName = target.getBaseName();
        for (int i = 0; i < files.length; i++)
            result.add((fileName.equals(files[i].getName())) ? target : new LoadedPropertySet(files[i]));
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
        
        LoadedPropertySet newPropertySet;
        try {
            newPropertySet = new LoadedPropertySet(file);
        } catch (Exception ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        if (newPropertySet.getLocale().isPresent()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "File name matches a resource bundle pattern\nLoad as a resource bundle?", ButtonType.YES, ButtonType.NO);
            alert.initStyle(StageStyle.UTILITY);
            alert.setTitle("Resource type");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                ArrayList<LoadedPropertySet> lps = getBundleMatches(newPropertySet, false);
                allPropertySets = FXCollections.observableArrayList((lps == null) ? new ArrayList<>() : lps);
                targetPropertySet = newPropertySet;
                if (allPropertySets.isEmpty())
                    allPropertySets.add(newPropertySet);
                return true;
            }
        }
        
        targetPropertySet = newPropertySet;
        allPropertySets = FXCollections.observableArrayList();
        return true;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            if (!openBundle()) {
                DevHelper.getInstance().getPrimaryStage().close();
                return;
            }
        } catch (Exception ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            DevHelper.getInstance().getPrimaryStage().close();
            return;
        }
    }
}
