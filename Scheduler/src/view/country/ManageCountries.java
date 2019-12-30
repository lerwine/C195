package view.country;

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
import model.db.DataRow;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/country/ManageCountries")
@FXMLResource("/view/country/ManageCountries.fxml")
public class ManageCountries extends view.ListingController {
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
    
    @Override
    protected void initialize() {
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryRow.PROP_NAME));
        createDateTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryRow.PROP_CREATEDATE));
        createDateTableColumn.setCellFactory(col -> new TableCell<CountryRow, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.getCurrent().getShortDateTimeFormatter()));
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
                    setText(item.format(scheduler.App.getCurrent().getShortDateTimeFormatter()));
            }
        });
        lastUpdateByTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryRow.PROP_LASTUPDATEBY));
    }
    
    public static void setAsRootContent() {
        setAsRootContent(ManageCountries.class, (view.Controller.SetContentContext<ManageCountries> context) -> {
            context.getStage().setTitle(context.getResources().getString("manageCountries"));
            scheduler.Util.showErrorAlert("Not Implemented", "Need to initialize country list");
        });
    }

    @Override
    protected void onAddNewItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onEditItem(DataRow item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onDeleteItem(DataRow item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
