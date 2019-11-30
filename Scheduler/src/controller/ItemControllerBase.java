/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.db.DataRow;
import utils.InvalidArgumentException;

/**
 *
 * @author Leonard T. Erwine
 * @param <T>
 */
public abstract class ItemControllerBase<T extends DataRow> implements Initializable {
    private T model;
    
    public T getModel() { return model; }
    
    public final void setModel(T model) throws InvalidArgumentException {
        if (model == null)
            throw new InvalidArgumentException("model");
        this.model = model;
        DateTimeFormatter dtf = scheduler.App.getDateTimeFormatter(FormatStyle.FULL);
        createDateValue.setText(dtf.format(model.getCreateDate()));
        lastUpdateValue.setText(dtf.format(model.getLastUpdate()));
        createdByValue.setText(model.getCreatedBy());
        lastUpdateByValue.setText(model.getLastUpdateBy());
        if (model.getRowState() == DataRow.ROWSTATE_NEW) {
            saveChangesButton.setText(scheduler.App.getMessage("addNew"));
            createDateLabel.setVisible(false);
            createDateValue.setVisible(false);
            createdByLabel.setVisible(false);
            createdByValue.setVisible(false);
            lastUpdateLabel.setVisible(false);
            lastUpdateValue.setVisible(false);
            lastUpdateByLabel.setVisible(false);
            lastUpdateByValue.setVisible(false);
            applyModelAsNew(model);
        } else {
            saveChangesButton.setText(scheduler.App.getMessage("saveChanges"));
            createDateLabel.setVisible(true);
            createDateValue.setVisible(true);
            createdByLabel.setVisible(true);
            createdByValue.setVisible(true);
            lastUpdateLabel.setVisible(true);
            lastUpdateValue.setVisible(true);
            lastUpdateByLabel.setVisible(true);
            lastUpdateByValue.setVisible(true);
            applyModelAsEdit(model);
        }
    }
    
    @FXML
    private Label createDateLabel;

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

    @FXML
    private Button cancelButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ResourceBundle rb = scheduler.App.getMessagesRB();
        createDateLabel.setText(rb.getString("createdOn"));
        createdByLabel.setText(rb.getString("createdBy"));
        lastUpdateLabel.setText(rb.getString("updatedOn"));
        lastUpdateByLabel.setText(rb.getString("updatedBy"));
        cancelButton.setText(rb.getString("cancel"));
    }

    protected abstract void applyModelAsNew(T model);
    protected abstract void applyModelAsEdit(T model);
    
}