package scheduler.view.country;

import java.time.LocalDateTime;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduler.dao.Country;
import scheduler.dao.CountryImpl;
import scheduler.util.Alerts;
import scheduler.view.ListingController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/country/ManageCountries")
@FXMLResource("/scheduler/view/country/ManageCountries.fxml")
public class ManageCountries extends ListingController<CountryModel> {
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Manage Countries"}.
     */
    public static final String RESOURCEKEY_MANAGECOUNTRIES = "manageCountries";

    @FXML
    private TableColumn<CountryModel, String> nameTableColumn;

    @FXML
    private TableColumn<CountryModel, LocalDateTime> createDateTableColumn;

    @FXML
    private TableColumn<CountryModel, String> createdByTableColumn;

    @FXML
    private TableColumn<CountryModel, LocalDateTime> lastUpdateTableColumn;

    @FXML
    private TableColumn<CountryModel, String> lastUpdateByTableColumn;
    
    @Override
    protected void initialize() {
        nameTableColumn.setCellValueFactory(new PropertyValueFactory<>(Country.PROP_NAME));
        createDateTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryImpl.PROP_CREATEDATE));
        createDateTableColumn.setCellFactory(col -> new TableCell<CountryModel, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.getCurrent().getShortDateTimeFormatter()));
            }
        });
        createdByTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryImpl.PROP_CREATEDBY));
        lastUpdateTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryImpl.PROP_LASTMODIFIEDDATE));
        lastUpdateTableColumn.setCellFactory(col -> new TableCell<CountryModel, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.getCurrent().getShortDateTimeFormatter()));
            }
        });
        lastUpdateByTableColumn.setCellValueFactory(new PropertyValueFactory<>(CountryImpl.PROP_LASTMODIFIEDBY));
    }
    
    @Deprecated
    public static void setAsRootContent() {
//        setAsRootContent(ManageCountries.class, (scheduler.view.SchedulerController.ContentChangeContext<ManageCountries> context) -> {
//            context.setWindowTitle(context.getResources().getString(RESOURCEKEY_MANAGECOUNTRIES));
//            Alerts.showErrorAlert("Not Implemented", "Need to initialize country list");
//        });
    }

    @Override
    protected void onAddNewItem(Event event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onEditItem(Event event, CountryModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void onDeleteItem(Event event, CountryModel item) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
