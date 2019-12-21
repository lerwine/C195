package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.db.CountryRow;
import scheduler.InvalidArgumentException;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
public class ManageCountriesController implements Initializable {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "/globalization/manageCountries";

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
    
    private String returnViewPath;

    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryRow.PROP_NAME));
        createDateTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryRow.PROP_CREATEDATE));
        createDateTableColumn.setCellFactory(col -> new TableCell<CountryRow, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.getShortDateTimeFormatter()));
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
                    setText(item.format(scheduler.App.getShortDateTimeFormatter()));
            }
        });
        lastUpdateByTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryRow.PROP_LASTUPDATEBY));
    }

    public static void setCurrentScene(Stage sourceStage, String returnViewPath) throws InvalidArgumentException {
        scheduler.App.setScene(sourceStage, VIEW_PATH, RESOURCE_NAME, (Stage stage, ResourceBundle rb, ManageCountriesController controller) -> {
            stage.setTitle(rb.getString("manageCountries"));
            controller.returnViewPath = returnViewPath;
        });
    }
}
