/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scene;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import model.ModelEvent;
import scene.annotations.FXMLResource;
import scene.annotations.GlobalizationResource;
import scheduler.EventHandlerList;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scene/Root")
@FXMLResource("/scene/Root.fxml")
public class RootController extends Controller {

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
    private BorderPane contentPane;
    
    public BorderPane getContentPane() { return contentPane; }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    private final ReadOnlyObjectWrapper<EventHandlerList<ModelEvent<model.db.AppointmentRow>>> appointmentAdded = new ReadOnlyObjectWrapper<>();
    public EventHandlerList<ModelEvent<model.db.AppointmentRow>> getAppointmentAdded() { return appointmentAdded.get(); }
    public ReadOnlyObjectProperty<EventHandlerList<ModelEvent<model.db.AppointmentRow>>> appointmentAddedProperty() { return appointmentAdded.getReadOnlyProperty(); }
    
    @FXML
    void newAppointmentMenuItemClick(ActionEvent event) {
        model.db.AppointmentRow model = scene.appointment.EditAppointment.addNew();
        if (model != null)
            appointmentAdded.get().invokeAll(() -> new ModelEvent<>(this, model, "appointmentAdded"));
    }
    
    @FXML
    void allAppointmentsMenuItemClick(ActionEvent event) { scene.appointment.ManageAppointments.setAsRootContent(); }
    
    private final ReadOnlyObjectWrapper<EventHandlerList<ModelEvent<model.db.CustomerRow>>> customerAdded = new ReadOnlyObjectWrapper<>();
    public EventHandlerList<ModelEvent<model.db.CustomerRow>> getCustomerAdded() { return customerAdded.get(); }
    public ReadOnlyObjectProperty<EventHandlerList<ModelEvent<model.db.CustomerRow>>> customerAddedProperty() { return customerAdded.getReadOnlyProperty(); }
    
    @FXML
    void newCustomerMenuItemClick(ActionEvent event) {
        model.db.CustomerRow model = scene.customer.EditCustomer.addNew();
        if (model != null)
            customerAdded.get().invokeAll(() -> new ModelEvent<>(this, model, "customerAdded"));
    }
    
    @FXML
    void allCustomersMenuItemClick(ActionEvent event) { scene.customer.ManageCustomers.setAsRootContent(); }
    
    private final ReadOnlyObjectWrapper<EventHandlerList<ModelEvent<model.db.CountryRow>>> countryAdded = new ReadOnlyObjectWrapper<>();
    public EventHandlerList<ModelEvent<model.db.CountryRow>> getCountryAdded() { return countryAdded.get(); }
    public ReadOnlyObjectProperty<EventHandlerList<ModelEvent<model.db.CountryRow>>> countryAddedProperty() { return countryAdded.getReadOnlyProperty(); }
    
    @FXML
    void newCountryMenuItemClick(ActionEvent event) {
        model.db.CountryRow model = scene.country.EditCountry.addNew();
        if (model != null)
            countryAdded.get().invokeAll(() -> new ModelEvent<>(this, model, "countryAdded"));
    }
    
    @FXML
    void allCountriesMenuItemClick(ActionEvent event) { scene.country.ManageCountries.setAsRootContent(); }
    
    private final ReadOnlyObjectWrapper<EventHandlerList<ModelEvent<model.db.CityRow>>> cityAdded = new ReadOnlyObjectWrapper<>();
    public EventHandlerList<ModelEvent<model.db.CityRow>> getCityAdded() { return cityAdded.get(); }
    public ReadOnlyObjectProperty<EventHandlerList<ModelEvent<model.db.CityRow>>> cityAddedProperty() { return cityAdded.getReadOnlyProperty(); }
    
    @FXML
    void newCityMenuItemClick(ActionEvent event) {
        model.db.CityRow model = scene.city.EditCity.addNew();
        if (model != null)
            cityAdded.get().invokeAll(() -> new ModelEvent<>(this, model, "cityAdded"));
    }
    
    private final ReadOnlyObjectWrapper<EventHandlerList<ModelEvent<model.db.AddressRow>>> addressAdded = new ReadOnlyObjectWrapper<>();
    public EventHandlerList<ModelEvent<model.db.AddressRow>> getAddressAdded() { return addressAdded.get(); }
    public ReadOnlyObjectProperty<EventHandlerList<ModelEvent<model.db.AddressRow>>> addressAddedProperty() { return addressAdded.getReadOnlyProperty(); }
    
    @FXML
    void newAddressMenuItemClick(ActionEvent event) {
        model.db.AddressRow model = scene.address.EditAddress.addNew();
        if (model != null)
            addressAdded.get().invokeAll(() -> new ModelEvent<>(this, model, "addressAdded"));
    }
    
    private final ReadOnlyObjectWrapper<EventHandlerList<ModelEvent<model.db.UserRow>>> userAdded = new ReadOnlyObjectWrapper<>();
    public EventHandlerList<ModelEvent<model.db.UserRow>> getUserAdded() { return userAdded.get(); }
    public ReadOnlyObjectProperty<EventHandlerList<ModelEvent<model.db.UserRow>>> userAddedProperty() { return userAdded.getReadOnlyProperty(); }
    
    @FXML
    void newUserMenuItemClick(ActionEvent event) {
        model.db.UserRow model = scene.user.EditUser.addNew();
        if (model != null)
            userAdded.get().invokeAll(() -> new ModelEvent<>(this, model, "userAdded"));
    }
    
    @FXML
    void allUsersMenuItemClick(ActionEvent event) { scene.user.ManageUsers.setAsRootContent(); }
    
    @FXML
    void exitButtonClick(ActionEvent event) { scheduler.App.getCurrent().getRootStage().hide(); }
    
    private final ReadOnlyObjectWrapper<Controller> currentContentController = new ReadOnlyObjectWrapper<>();
    public Controller getCurrentContentController() { return currentContentController.get(); }
    public ReadOnlyObjectProperty<Controller> currentContentControllerProperty() { return currentContentController.getReadOnlyProperty(); }

    private final ReadOnlyObjectWrapper<EventHandlerList<ControllerChangeEvent>> contentControllerChanging = new ReadOnlyObjectWrapper<>();
    public EventHandlerList<ControllerChangeEvent> getContentControllerChanging() { return contentControllerChanging.get(); }
    public ReadOnlyObjectProperty<EventHandlerList<ControllerChangeEvent>> contentControllerChangingProperty() { return contentControllerChanging.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<EventHandlerList<ControllerChangeEvent>> contentControllerChanged = new ReadOnlyObjectWrapper<>();
    public EventHandlerList<ControllerChangeEvent> getContentControllerChanged() { return contentControllerChanged.get(); }
    public ReadOnlyObjectProperty<EventHandlerList<ControllerChangeEvent>> contentControllerChangedProperty() { return contentControllerChanged.getReadOnlyProperty(); }
    
    public void setContent(Node content, Controller controller) {
        Controller previous = currentContentController.get();
        
        contentControllerChanging.get().invokeAll(() -> new ControllerChangeEvent(this, previous, controller, "contentControllerChanging"));

        contentPane.getChildren().clear();
        contentPane.setCenter(content);
        currentContentController.set(controller);
        
        contentControllerChanged.get().invokeAll(() -> new ControllerChangeEvent(this, previous, controller, "contentControllerChanged"));
    }
    
    private static RootController current;
    
    public static RootController getCurrent() { return current; }
    
    @SuppressWarnings("UseSpecificCatch")
    public static void setAsRootStageScene() {
        scheduler.App app = scheduler.App.getCurrent(); 
        try {
            ResourceBundle rb = ResourceBundle.getBundle(scene.Controller.getGlobalizationResourceName(RootController.class), app.getCurrentLocale());
            FXMLLoader loader = new FXMLLoader(RootController.class.getResource(scene.Controller.getFXMLResourceName(RootController.class)), rb);
            Scene scene = new Scene(loader.load());
            current = (RootController)loader.getController();
            app.getRootStage().setScene(scene);
        } catch (Exception ex) {
            Logger.getLogger(RootController.class.getName()).log(Level.SEVERE, null, ex);
            ResourceBundle arb = app.getAppResourceBundle();
            scheduler.Util.showErrorAlert(arb.getString("fxmlLoaderErrorTitle"), arb.getString("fxmlLoaderErrorMessage"));
        }
    }
    
}
