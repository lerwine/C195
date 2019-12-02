/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;
import com.mysql.jdbc.Connection;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.FormatStyle;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.db.AppointmentRow;
import scheduler.App;
import scheduler.Messages;
import scheduler.SqlConnectionDependency;

/**
 * FXML Controller class
 *
 * @author webmaster
 */
public class HomeScreenController implements Initializable {
    /**
     * The path of the View associated with this controller.
     */
    public static final String VIEW_PATH = "/view/HomeScreen.fxml";

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

    private TableView<AppointmentRow> todayAndFutureAppointmenstTableView;
    
    private TableColumn<AppointmentRow, String> titleTableColumn;
    
    private TableColumn<AppointmentRow, LocalDateTime> startTableColumn;
    
    private TableColumn<AppointmentRow, LocalDateTime> endTableColumn;
    
    private TableColumn<AppointmentRow, String> typeTableColumn;
    
    private TableColumn<AppointmentRow, Integer> customerTableColumn;
    
    private ObservableList<AppointmentRow> currentUserAppointments;
    
    /**
     * Initializes the controller class.
     * @param url The URL of the associated view.
     * @param rb The resources provided by the {@link javafx.fxml.FXMLLoader}
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Messages messages = Messages.current();
        appointmentsMenu.setText(messages.getAppointments());
        newAppointmentMenuItem.setText(messages.getNew());
        allAppointmentsMenuItem.setText(messages.getAllAppointments());
        customersMenu.setText(messages.getCustomers());
        newCustomerMenuItem.setText(messages.getNew());
        allCustomersMenuItem.setText(messages.getAllCustomers());
        addressMenu.setText(messages.getAddress());
        newCountryMenuItem.setText(messages.getNewCountry());
        newCityMenuItem.setText(messages.getNewCity());
        newAddressMenuItem.setText(messages.getNewAddress());
        allCountriesMenuItem.setText(messages.getAllCountries());
        usersMenu.setText(messages.getUsers());
        newUserMenuItem.setText(messages.getNew());
        allUsersMenuItem.setText(messages.getAllUsers());
        titleTableColumn.setText(messages.getTitle());
        startTableColumn.setText(messages.getStart());
        endTableColumn.setText(messages.getEnd());
        typeTableColumn.setText(messages.getType());
        customerTableColumn.setText(messages.getCustomer());
        titleTableColumn.setCellValueFactory(new PropertyValueFactory<>(AppointmentRow.PROP_TITLE));
        startTableColumn.setCellValueFactory(new PropertyValueFactory<>(AppointmentRow.PROP_START));
        startTableColumn.setCellFactory(col -> new TableCell<AppointmentRow, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.getDateTimeFormatter(FormatStyle.SHORT)));
            }
        });
        endTableColumn.setCellValueFactory(new PropertyValueFactory<>(AppointmentRow.PROP_END));
        endTableColumn.setCellFactory(col -> new TableCell<AppointmentRow, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    setText(item.format(scheduler.App.getDateTimeFormatter(FormatStyle.SHORT)));
            }
        });
        typeTableColumn.setCellValueFactory(new PropertyValueFactory<>(AppointmentRow.PROP_TYPE));
        customerTableColumn.setCellValueFactory(new PropertyValueFactory<>(AppointmentRow.PROP_CUSTOMERID));
        customerTableColumn.setCellFactory(col -> new TableCell<AppointmentRow, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty)
                    setText(null);
                else
                    throw new RuntimeException("Get customer name from ID");
            }
        });
        currentUserAppointments = SqlConnectionDependency.get((Connection connection) -> {
            try {
                return AppointmentRow.getTodayAndFutureByUser(connection, App.getCurrentUser().get().getPrimaryKey());
            } catch (SQLException ex) {
                Logger.getLogger(HomeScreenController.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }    
    
    @FXML
    void newAppointmentMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), UserAppointmentsController.VIEW_PATH, (UserAppointmentsController controller) -> {
            controller.applyModel(scheduler.App.getCurrentUser().get());
        });
    }
    
    @FXML
    void allAppointmentsMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), UserAppointmentsController.VIEW_PATH, (UserAppointmentsController controller) -> {
            controller.applyModel(scheduler.App.getCurrentUser().get());
        });
    }
    
    @FXML
    void newCustomerMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageCustomersController.VIEW_PATH);
    }
    
    @FXML
    void allCustomersMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageCustomersController.VIEW_PATH);
    }
    
    @FXML
    void newCountryMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageCountriesController.VIEW_PATH);
    }
    
    @FXML
    void newCityMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageCountriesController.VIEW_PATH);
    }
    
    @FXML
    void newAddressMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageCountriesController.VIEW_PATH);
    }
    
    @FXML
    void allCountriesMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageCountriesController.VIEW_PATH);
    }
    
    @FXML
    void newUserMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageUsersController.VIEW_PATH);
    }
    
    @FXML
    void allUsersMenuItemClick(ActionEvent event) {
        scheduler.App.changeScene((Node)event.getSource(), ManageUsersController.VIEW_PATH);
    }
    
    @FXML
    void exitButtonClick(ActionEvent event) {
        ((Button)event.getSource()).getScene().getWindow().hide();
    }
}
