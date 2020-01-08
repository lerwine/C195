package view.country;

import java.time.LocalDateTime;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import model.db.CountryRow;
import util.Alerts;
import view.ListingController;
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/country/ManageCountries")
@FXMLResource("/view/country/ManageCountries.fxml")
public class ManageCountries extends ListingController<CountryRow> {
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

//    public static final String RESOURCEKEY_CREATEDBY = "createdBy";
//    public static final String RESOURCEKEY_CREATEDON = "createdOn";
    public static final String RESOURCEKEY_MANAGECOUNTRIES = "manageCountries";
//    public static final String RESOURCEKEY_NAME = "name";
//    public static final String RESOURCEKEY_UPDATEDBY = "updatedBy";
//    public static final String RESOURCEKEY_UPDATEDON = "updatedOn";

    //</editor-fold>
    
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
                    setText(item.format(scheduler.App.CURRENT.get().getShortDateTimeFormatter()));
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
                    setText(item.format(scheduler.App.CURRENT.get().getShortDateTimeFormatter()));
            }
        });
        lastUpdateByTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryRow.PROP_LASTUPDATEBY));
    }
    
    public static void setAsRootContent() {
        setAsRootContent(ManageCountries.class, (view.SchedulerController.ContentChangeContext<ManageCountries> context) -> {
            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_MANAGECOUNTRIES));
            Alerts.showErrorAlert("Not Implemented", "Need to initialize country list");
        });
    }

    @Override
    protected void onAddNewItem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onEditItem(CountryRow item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onDeleteItem(CountryRow item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
