package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import model.db.CountryRow;
import scheduler.InvalidArgumentException;
import model.annotations.ResourceKey;
import model.annotations.ResourceName;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@ResourceName(ManageCountriesController.RESOURCE_NAME)
public class ManageCountriesController extends ControllerBase {
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
    @ResourceKey("name")
    private TableColumn<CountryRow, String> nameTableColumn;

    @FXML
    @ResourceKey("createdOn")
    private TableColumn<CountryRow, LocalDateTime> createDateTableColumn;

    @FXML
    @ResourceKey("createdBy")
    private TableColumn<CountryRow, String> createdByTableColumn;

    @FXML
    @ResourceKey("updatedOn")
    private TableColumn<CountryRow, LocalDateTime> lastUpdateTableColumn;

    @FXML
    @ResourceKey("updatedBy")
    private TableColumn<CountryRow, String> lastUpdateByTableColumn;
    
    private String returnViewPath;

    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        super.initialize(url, rb);
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

    public static void setCurrentScene(Node sourceNode, String returnViewPath) throws InvalidArgumentException {
        scheduler.App.changeScene(sourceNode, VIEW_PATH, (Stage stage, ManageCountriesController controller) -> {
            stage.setTitle(ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale())
                .getString("manageCountries"));
            controller.returnViewPath = returnViewPath;
        });
    }
}
