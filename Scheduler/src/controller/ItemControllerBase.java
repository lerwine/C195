package controller;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.db.DataRow;
import model.annotations.ResourceKey;
import model.annotations.ResourceName;

/**
 *
 * @author Leonard T. Erwine
 * @param <T>
 */
public abstract class ItemControllerBase<T extends DataRow> extends ControllerBase {
    private T model;
    
    public T getModel() { return model; }
    
    protected final boolean setModel(T model) {
        this.model = model;
        DateTimeFormatter dtf = scheduler.App.getDateTimeFormatter(FormatStyle.FULL);
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
    @ResourceName("item")
    @ResourceKey("created")
    private Label createdLabel;

    @FXML
    private Label createDateValue;

    @FXML
    @ResourceName("item")
    @ResourceKey("by")
    private Label createdByLabel;

    @FXML
    private Label createdByValue;

    @FXML
    @ResourceName("item")
    @ResourceKey("updated")
    private Label lastUpdateLabel;

    @FXML
    private Label lastUpdateValue;

    @FXML
    @ResourceName("item")
    @ResourceKey("by")
    private Label lastUpdateByLabel;

    @FXML
    private Label lastUpdateByValue;

    @FXML
    @ResourceName("item")
    @ResourceKey("save")
    private Button saveChangesButton;

    @FXML
    @ResourceName("item")
    @ResourceKey("cancel")
    private Button cancelButton;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
    }
    
    abstract void saveChangesClick(ActionEvent event);
    
    abstract void cancelClick(ActionEvent event);
}
