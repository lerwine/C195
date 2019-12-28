package scene.home;

import scene.address.EditAddress;
import scene.city.EditCity;
import scene.country.EditCountry;
import scene.customer.EditCustomer;
import scene.user.EditUser;
import scene.country.ManageCountries;
import scene.customer.ManageCustomers;
import scene.user.ManageUsers;
import scene.appointment.ManageAppointments;
import scene.appointment.EditAppointment;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.db.AppointmentRow;
import scheduler.SqlConnectionDependency;

/**
 * FXML Controller class
 * @author webmaster
 */
public class HomeScene implements Initializable {
    /**
     * The name of the globalization resource bundle for this controller.
     */
    public static final String GLOBALIZATION_RESOURCE_NAME = "scene/home/HomeScene";

    /**
     * The path of the View associated with this controller.
     */
    public static final String FXML_RESOURCE_NAME = "/scene/home/HomeScene.fxml";

    @FXML
    private Menu appointmentsMenu;
    
    @FXML
    private MenuItem newAppointmentMenuItem;

    @FXML
    private MenuItem allAppointmentsMenuItem;

    @FXML
    private Menu customersMenu;
    
    @FXML
    private MenuItem newCustomerMenuItem;

    @FXML
    private MenuItem allCustomersMenuItem;

    @FXML
    private Menu addressMenu;
    
    @FXML
    private MenuItem newCountryMenuItem;

    @FXML
    private MenuItem newCityMenuItem;

    @FXML
    private MenuItem newAddressMenuItem;

    @FXML
    private MenuItem allCountriesMenuItem;

    @FXML
    private Menu usersMenu;
    
    @FXML
    private MenuItem newUserMenuItem;

    @FXML
    private MenuItem allUsersMenuItem;

    @FXML
    private Label headingLabel;
    
    @FXML
    private TableView<AppointmentRow> todayAndFutureAppointmenstTableView;
    
    @FXML
    private MenuItem editAppointmentMenuItem;

    @FXML
    private TableColumn<AppointmentRow, String> titleTableColumn;
    
    @FXML
    private TableColumn<AppointmentRow, LocalDateTime> startTableColumn;
    
    @FXML
    private TableColumn<AppointmentRow, LocalDateTime> endTableColumn;
    
    @FXML
    private TableColumn<AppointmentRow, String> typeTableColumn;
    
    @FXML
    private TableColumn<AppointmentRow, model.Customer> customerTableColumn;
    
    private final ObservableList<AppointmentRow> currentAndFutureAppointments = FXCollections.observableArrayList();
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Set heading text to "My Current and Upcoming Appointments"
        headingLabel.setText(rb.getString("myCurrentAndUpcoming"));
        if (reloadCurrentAndFutureAppointments())
            // Make table view visible and initialize columns
            todayAndFutureAppointmenstTableView.setVisible(true);
    }

    private boolean reloadCurrentAndFutureAppointments() {
        currentAndFutureAppointments.clear();
        // Get current and future appointments for current user
        try {
            // Open a new database connection dependency and close it when finished loading data.
            SqlConnectionDependency dep = new SqlConnectionDependency(true);
            try {
                currentAndFutureAppointments.addAll(AppointmentRow.getTodayAndFutureByUser(dep.getconnection(),
                        scheduler.App.getCurrent().getCurrentUser().get().getPrimaryKey()));
            } finally { dep.close(); }
        } catch (SQLException ex) {
            Logger.getLogger(HomeScene.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        // Set table view items
        todayAndFutureAppointmenstTableView.setItems(currentAndFutureAppointments);
        return true;
    }
    
    @FXML
    void newAppointmentMenuItemClick(ActionEvent event) {
        AppointmentRow model = EditAppointment.addNew();
        if (model != null && model.getEnd().toLocalDate().compareTo(LocalDate.now()) >= 0)
            currentAndFutureAppointments.add(model);
    }
    
    @FXML
    void allAppointmentsMenuItemClick(ActionEvent event) { ManageAppointments.setAsRootStageScene(); }
    
    @FXML
    void newCustomerMenuItemClick(ActionEvent event) { EditCustomer.addNew(); }
    
    @FXML
    void allCustomersMenuItemClick(ActionEvent event) { ManageCustomers.setAsRootStageScene(); }
    
    @FXML
    void newCountryMenuItemClick(ActionEvent event) { EditCountry.addNew(); }
    
    @FXML
    void newCityMenuItemClick(ActionEvent event) { EditCity.addNew(); }
    
    @FXML
    void newAddressMenuItemClick(ActionEvent event) { EditAddress.addNew(); }
    
    @FXML
    void allCountriesMenuItemClick(ActionEvent event) { ManageCountries.setAsRootStageScene(); }
    
    @FXML
    void newUserMenuItemClick(ActionEvent event) { EditUser.addNew(); }
    
    @FXML
    void allUsersMenuItemClick(ActionEvent event) { ManageUsers.setAsRootStageScene(); }
    
    @FXML
    void exitButtonClick(ActionEvent event) { scheduler.App.getCurrent().getRootStage().hide(); }
    
    public static void setAsRootStageScene() {
        scheduler.App.getCurrent().changeRootStageScene(GLOBALIZATION_RESOURCE_NAME, FXML_RESOURCE_NAME, (ResourceBundle rb, Stage stage) -> {
            stage.setTitle(rb.getString("appointmentScheduler"));
        });
    }
}
