package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.db.CountryRow;
import scheduler.Messages;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class ManageCountriesController implements Initializable {
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/ManageCountries.fxml";

    @FXML
    private TableView<CountryRow> countriesTableView;

    @FXML
    private TableColumn<CountryRow, String> nameTableColumn;

    @FXML
    private TableColumn<CountryRow, LocalDateTime> createDateTableColumn;

    @FXML
    private TableColumn<CountryRow, String> createdByTableColumn;

    @FXML
    private TableColumn<CountryRow, LocalDateTime> lastUpdateTableColumn;

    @FXML
    private TableColumn<CountryRow, String> lastUpdateByTableColumn;

    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Messages messages = Messages.current();
        nameTableColumn.setText(messages.getName());
        createDateTableColumn.setText(messages.getCreatedOn());
        createdByTableColumn.setText(messages.getCreatedBy());
        lastUpdateTableColumn.setText(messages.getUpdatedOn());
        lastUpdateByTableColumn.setText(messages.getUpdatedBy());
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryRow.PROP_NAME));
        createDateTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryRow.PROP_CREATEDATE));
        createDateTableColumn.setCellFactory(col -> new TableCell<CountryRow, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.getDateTimeFormatter(FormatStyle.SHORT)));
            }
        });
        createdByTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryRow.PROP_CREATEDBY));
        lastUpdateTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryRow.PROP_LASTUPDATE));
        lastUpdateTableColumn.setCellFactory(col -> new TableCell<CountryRow, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.getDateTimeFormatter(FormatStyle.SHORT)));
            }
        });
        lastUpdateByTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryRow.PROP_LASTUPDATEBY));
    }    
    
}
