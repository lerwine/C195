package controller;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.db.DataRow;
import scheduler.InvalidArgumentException;
import scheduler.Messages;

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
            saveChangesButton.setText(Messages.current().getActiveState());
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
            saveChangesButton.setText(Messages.current().getSaveChanges());
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
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        createDateLabel.setText(Messages.current().getCreatedOn());
        createdByLabel.setText(Messages.current().getCreatedBy());
        lastUpdateLabel.setText(Messages.current().getUpdatedOn());
        lastUpdateByLabel.setText(Messages.current().getUpdatedBy());
        cancelButton.setText(Messages.current().getCancel());
    }

    protected abstract void applyModelAsNew(T model);
    protected abstract void applyModelAsEdit(T model);
    
}
