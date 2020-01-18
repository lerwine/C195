package view.country;

import java.time.LocalDateTime;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduler.dao.Country;
import scheduler.dao.CountryImpl;
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
public class ManageCountries extends ListingController<CountryImpl> {
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Manage Countries"}.
     */
    public static final String RESOURCEKEY_MANAGECOUNTRIES = "manageCountries";

    @FXML
    private TableColumn<CountryImpl, String> nameTableColumn;

    @FXML
    private TableColumn<CountryImpl, LocalDateTime> createDateTableColumn;

    @FXML
    private TableColumn<CountryImpl, String> createdByTableColumn;

    @FXML
    private TableColumn<CountryImpl, LocalDateTime> lastUpdateTableColumn;

    @FXML
    private TableColumn<CountryImpl, String> lastUpdateByTableColumn;
    
    @Override
    protected void initialize() {
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>(Country.PROP_NAME));
        createDateTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryImpl.PROP_CREATEDATE));
        createDateTableColumn.setCellFactory(col -> new TableCell<CountryImpl, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.CURRENT.get().getShortDateTimeFormatter()));
            }
        });
        createdByTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryImpl.PROP_CREATEDBY));
        lastUpdateTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryImpl.PROP_LASTMODIFIEDDATE));
        lastUpdateTableColumn.setCellFactory(col -> new TableCell<CountryImpl, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.CURRENT.get().getShortDateTimeFormatter()));
            }
        });
        lastUpdateByTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryImpl.PROP_LASTMODIFIEDBY));
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
    protected void onEditItem(CountryImpl item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onDeleteItem(CountryImpl item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
