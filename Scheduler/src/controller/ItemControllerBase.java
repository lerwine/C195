package controller;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.db.DataRow;

/**
 *
 * @author Leonard T. Erwine
 * @param <T>
 */
public abstract class ItemControllerBase<T extends DataRow> implements Initializable {
    private T model;
    
    public T getModel() { return model; }
    
    protected final boolean setModel(T model) {
        this.model = model;
        DateTimeFormatter dtf = scheduler.App.getFullDateTimeFormatter();
        createDateValue.setText(dtf.format(model.getCreateDate()));
        lastUpdateValue.setText(dtf.format(model.getLastUpdate()));
        createdByValue.setText(model.getCreatedBy());
        lastUpdateByValue.setText(model.getLastUpdateBy());
        if (model.getRowState() == DataRow.ROWSTATE_NEW) {
            createdLabel.setVisible(false);
            createDateValue.setVisible(false);
            createdByLabel.setVisible(false);
            createdByValue.setVisible(false);
            lastUpdateLabel.setVisible(false);
            lastUpdateValue.setVisible(false);
            lastUpdateByLabel.setVisible(false);
            lastUpdateByValue.setVisible(false);
            return false;
        }
        createdLabel.setVisible(true);
        createDateValue.setVisible(true);
        createdByLabel.setVisible(true);
        createdByValue.setVisible(true);
        lastUpdateLabel.setVisible(true);
        lastUpdateValue.setVisible(true);
        lastUpdateByLabel.setVisible(true);
        lastUpdateByValue.setVisible(true);
        return true;
    }
    
    @FXML
    private Label createdLabel;

    @FXML
    private Label createDateValue;

    @FXML
    private Label createdByLabel;

    @FXML
    private Label createdByValue;

    @FXML
    private Label lastUpdateLabel;

    @FXML
    private Label lastUpdateValue;

    @FXML
    private Label lastUpdateByLabel;

    @FXML
    private Label lastUpdateByValue;

    @FXML
    private Button saveChangesButton;

    protected Button getSaveChangesButton() { return saveChangesButton; }
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    abstract void saveChangesClick(ActionEvent event);
    
    abstract void cancelClick(ActionEvent event);
}
