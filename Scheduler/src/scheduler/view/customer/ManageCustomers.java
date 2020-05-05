package scheduler.view.customer;

import java.io.IOException;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableView;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import scheduler.controls.ItemEditTableCellFactory;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class for viewing a list of {@link CustomerModel} items.
 * <p>
 * The associated view is {@code /resources/scheduler/view/customer/ManageCustomers.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/customer/ManageCustomers")
@FXMLResource("/scheduler/view/customer/ManageCustomers.fxml")
public final class ManageCustomers extends BorderPane {

    private static final Logger LOG = Logger.getLogger(ManageCustomers.class.getName());

    public static ManageCustomers loadInto(MainController mainController, Stage stage, CustomerModelFilter filter,
            Object loadEventListener) throws IOException {
        throw new UnsupportedOperationException();
    }

    public static ManageCustomers loadInto(MainController mainController, Stage stage, CustomerModelFilter filter) throws IOException {
        throw new UnsupportedOperationException();
    }

    private final ObjectProperty<CustomerModelFilter> filter;
    
    private final ObservableList<CustomerModel> items;

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="rootStackPane"
    private StackPane rootStackPane; // Value injected by FXMLLoader

    @FXML // fx:id="headingLabel"
    private Label headingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="helpButton"
    private Button helpButton; // Value injected by FXMLLoader

    @FXML // fx:id="subHeadingLabel"
    private Label subHeadingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="listingTableView"
    private TableView<CustomerModel> listingTableView; // Value injected by FXMLLoader

    @FXML // fx:id="itemEditTableCellFactory"
    private ItemEditTableCellFactory<CustomerModel> itemEditTableCellFactory; // Value injected by FXMLLoader

    @FXML // fx:id="editMenuItem"
    private MenuItem editMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="deleteMenuItem"
    private MenuItem deleteMenuItem; // Value injected by FXMLLoader

    @FXML // fx:id="newButton"
    private Button newButton; // Value injected by FXMLLoader

    public ManageCustomers() {
        filter = new SimpleObjectProperty<>();
        items = FXCollections.observableArrayList();
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert headingLabel != null : "fx:id=\"headingLabel\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert helpButton != null : "fx:id=\"helpButton\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert subHeadingLabel != null : "fx:id=\"subHeadingLabel\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert listingTableView != null : "fx:id=\"listingTableView\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert itemEditTableCellFactory != null : "fx:id=\"itemEditTableCellFactory\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert editMenuItem != null : "fx:id=\"editMenuItem\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert deleteMenuItem != null : "fx:id=\"deleteMenuItem\" was not injected: check your FXML file 'ManageCustomers.fxml'.";
        assert newButton != null : "fx:id=\"newButton\" was not injected: check your FXML file 'ManageCustomers.fxml'.";

        listingTableView.setItems(items);
    }
    
    public CustomerModelFilter getFilter() {
        return filter.get();
    }

    public void setFilter(CustomerModelFilter value) {
        filter.set(value);
    }

    public ObjectProperty filterProperty() {
        return filter;
    }

}
