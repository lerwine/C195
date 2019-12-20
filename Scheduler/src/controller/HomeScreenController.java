package controller;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.db.AppointmentRow;
import scheduler.App;
import scheduler.InvalidArgumentException;
import scheduler.SqlConnectionDependency;
import model.annotations.ResourceKey;
import model.annotations.ResourceName;

/**
 * FXML Controller class
 * @author webmaster
 */
@ResourceName(HomeScreenController.RESOURCE_NAME)
public class HomeScreenController extends ControllerBase {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String RESOURCE_NAME = "globalization/homeScreen";

    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/HomeScreen.fxml";

    @FXML
    @ResourceKey("appointments")
    private Menu appointmentsMenu;
    
    @FXML
    @ResourceKey("new")
    private MenuItem newAppointmentMenuItem;

    @FXML
    @ResourceKey("allAppointments")
    private MenuItem allAppointmentsMenuItem;

    @FXML
    @ResourceKey("customers")
    private Menu customersMenu;
    
    @FXML
    @ResourceKey("new")
    private MenuItem newCustomerMenuItem;

    @FXML
    @ResourceKey("allCustomers")
    private MenuItem allCustomersMenuItem;

    @FXML
    @ResourceKey("address")
    private Menu addressMenu;
    
    @FXML
    @ResourceKey("newCountry")
    private MenuItem newCountryMenuItem;

    @FXML
    @ResourceKey("newCity")
    private MenuItem newCityMenuItem;

    @FXML
    @ResourceKey("newAddress")
    private MenuItem newAddressMenuItem;

    @FXML
    @ResourceKey("allCountries")
    private MenuItem allCountriesMenuItem;

    @FXML
    @ResourceKey("users")
    private Menu usersMenu;
    
    @FXML
    @ResourceKey("new")
    private MenuItem newUserMenuItem;

    @FXML
    @ResourceKey("allUsers")
    private MenuItem allUsersMenuItem;

    @FXML
    @ResourceKey("loadingData")
    private Label headingLabel;
    
    @FXML
    private TableView<AppointmentRow> todayAndFutureAppointmenstTableView;
    
    @FXML
    @ResourceKey("editAppointment")
    private MenuItem editAppointmentMenuItem;

    @FXML
    @ResourceKey("title")
    private TableColumn<AppointmentRow, String> titleTableColumn;
    
    @FXML
    @ResourceKey("start")
    private TableColumn<AppointmentRow, LocalDateTime> startTableColumn;
    
    @FXML
    @ResourceKey("end")
    private TableColumn<AppointmentRow, LocalDateTime> endTableColumn;
    
    @FXML
    @ResourceKey("type")
    private TableColumn<AppointmentRow, String> typeTableColumn;
    
    @FXML
    @ResourceKey("customer")
    private TableColumn<AppointmentRow, model.Customer> customerTableColumn;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) { super.initialize(url, rb); }    
    
    private Stage currentStage;
    
    /**
     * 
     * @param sourceNode
     */
    public static void setCurrentScene(Node sourceNode) {
        App.changeScene(sourceNode, VIEW_PATH, (Stage stage, HomeScreenController controller) -> {
            controller.currentStage = stage;
            ResourceBundle rb = ResourceBundle.getBundle(RESOURCE_NAME, scheduler.App.getCurrentLocale());
            stage.setTitle(rb.getString("appointmentScheduler"));
            // Get current and future appointments for current user
            ObservableList<AppointmentRow> items;
            try {
                // Open a new database connection dependency and close it when finished loading data.
                SqlConnectionDependency dep = new SqlConnectionDependency(true);
                try {
                    items = AppointmentRow.getTodayAndFutureByUser(dep.getconnection(),
                            App.getCurrentUser().get().getPrimaryKey());
                } finally { dep.close(); }
            } catch (SQLException ex) {
                // Set heading text to "Database access error", log error and exit
                controller.headingLabel.setText(rb.getString("appointmentScheduler"));
                Logger.getLogger(HomeScreenController.class.getName()).log(Level.SEVERE, null, ex);
                return;
            }
            
            // Set table view items
            controller.todayAndFutureAppointmenstTableView.setItems(items);
            
            // Set heading text to "My Current and Upcoming Appointments"
            controller.headingLabel.setText(rb.getString("myCurrentAndUpcoming"));
            
            // Make table view visible and initialize columns
            controller.todayAndFutureAppointmenstTableView.setVisible(true);
        });
    }
    
    @FXML
    void newAppointmentMenuItemClick(ActionEvent event) {
        try {
            EditAppointmentController.setCurrentScene(currentStage, new AppointmentRow(), VIEW_PATH);
        } catch (InvalidArgumentException ex) {
            Logger.getLogger(HomeScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void allAppointmentsMenuItemClick(ActionEvent event) {
        try {
            ManageAppointmentsController.setCurrentScene(currentStage, VIEW_PATH);
        } catch (InvalidArgumentException ex) {
            Logger.getLogger(HomeScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void newCustomerMenuItemClick(ActionEvent event) {
        try {
            EditCustomerController.setCurrentScene(currentStage, new model.db.CustomerRow(), VIEW_PATH);
        } catch (InvalidArgumentException ex) {
            Logger.getLogger(HomeScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void allCustomersMenuItemClick(ActionEvent event) {
        try {
            ManageCustomersController.setCurrentScene(currentStage, VIEW_PATH);
        } catch (InvalidArgumentException ex) {
            Logger.getLogger(HomeScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void newCountryMenuItemClick(ActionEvent event) {
        try {
            EditCountryController.setCurrentScene(currentStage, new model.db.CountryRow(), VIEW_PATH);
        } catch (InvalidArgumentException ex) {
            Logger.getLogger(HomeScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void newCityMenuItemClick(ActionEvent event) {
        try {
            EditCityController.setCurrentScene(currentStage, new model.db.CityRow(), VIEW_PATH);
        } catch (InvalidArgumentException ex) {
            Logger.getLogger(HomeScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void newAddressMenuItemClick(ActionEvent event) {
        try {
            EditAddressController.setCurrentScene(currentStage, new model.db.AddressRow(), VIEW_PATH);
        } catch (InvalidArgumentException ex) {
            Logger.getLogger(HomeScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void allCountriesMenuItemClick(ActionEvent event) {
        try {
            ManageCountriesController.setCurrentScene(currentStage, VIEW_PATH);
        } catch (InvalidArgumentException ex) {
            Logger.getLogger(HomeScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void newUserMenuItemClick(ActionEvent event) {
        try {
            EditUserController.setCurrentScene(currentStage, new model.db.UserRow(), VIEW_PATH);
        } catch (InvalidArgumentException ex) {
            Logger.getLogger(HomeScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void allUsersMenuItemClick(ActionEvent event) {
        try {
            ManageUsersController.setCurrentScene(currentStage, VIEW_PATH);
        } catch (InvalidArgumentException ex) {
            Logger.getLogger(HomeScreenController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    void exitButtonClick(ActionEvent event) {
        ((Button)event.getSource()).getScene().getWindow().hide();
    }
}
