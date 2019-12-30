package view;

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
import view.annotations.FXMLResource;
import view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("view/Root")
@FXMLResource("/view/Root.fxml")
public class RootController extends Controller {
    //<editor-fold defaultstate="collapsed" desc="FXML controls">
    
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
    
    //</editor-fold>
    
    private static RootController current;
    
    public static RootController getCurrent() { return current; }
    
    //<editor-fold defaultstate="collapsed" desc="Event handler delegates">
    
    private final ReadOnlyObjectWrapper<model.db.AppointmentRow> appointmentAdded;
    public model.db.AppointmentRow getAppointmentAdded() { return appointmentAdded.get(); }
    public ReadOnlyObjectProperty<model.db.AppointmentRow> appointmentAddedProperty() { return appointmentAdded.getReadOnlyProperty(); }
       
    private final ReadOnlyObjectWrapper<model.db.CustomerRow> customerAdded;
    public model.db.CustomerRow getCustomerAdded() { return customerAdded.get(); }
    public ReadOnlyObjectProperty<model.db.CustomerRow> customerAddedProperty() { return customerAdded.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<model.db.CountryRow> countryAdded;
    public model.db.CountryRow getCountryAdded() { return countryAdded.get(); }
    public ReadOnlyObjectProperty<model.db.CountryRow> countryAddedProperty() { return countryAdded.getReadOnlyProperty(); }
     
    private final ReadOnlyObjectWrapper<model.db.CityRow> cityAdded;
    public model.db.CityRow getCityAdded() { return cityAdded.get(); }
    public ReadOnlyObjectProperty<model.db.CityRow> cityAddedProperty() { return cityAdded.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<model.db.AddressRow> addressAdded;
    public model.db.AddressRow getAddressAdded() { return addressAdded.get(); }
    public ReadOnlyObjectProperty<model.db.AddressRow> addressAddedProperty() { return addressAdded.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<model.db.UserRow> userAdded;
    public model.db.UserRow getUserAdded() { return userAdded.get(); }
    public ReadOnlyObjectProperty<model.db.UserRow> userAddedProperty() { return userAdded.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<Controller> contentControllerChanging;
    public Controller getContentControllerChanging() { return contentControllerChanging.get(); }
    public ReadOnlyObjectProperty<Controller> contentControllerChangingProperty() { return contentControllerChanging.getReadOnlyProperty(); }
    
    private final ReadOnlyObjectWrapper<Controller> currentContentController;
    public Controller getCurrentContentController() { return currentContentController.get(); }
    public ReadOnlyObjectProperty<Controller> currentContentControllerProperty() { return currentContentController.getReadOnlyProperty(); }

    //</editor-fold>
    
    public RootController() {
        appointmentAdded = new ReadOnlyObjectWrapper<>();
        customerAdded = new ReadOnlyObjectWrapper<>();
        countryAdded = new ReadOnlyObjectWrapper<>();
        cityAdded = new ReadOnlyObjectWrapper<>();
        addressAdded = new ReadOnlyObjectWrapper<>();
        userAdded = new ReadOnlyObjectWrapper<>();
        currentContentController = new ReadOnlyObjectWrapper<>();
        contentControllerChanging = new ReadOnlyObjectWrapper<>();
    }
    
    @FXML // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert appointmentsMenu != null : String.format("fx:id=\"appointmentsMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert newAppointmentMenuItem != null : String.format("fx:id=\"newAppointmentMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert allAppointmentsMenuItem != null : String.format("fx:id=\"allAppointmentsMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert customersMenu != null : String.format("fx:id=\"customersMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert newCustomerMenuItem != null : String.format("fx:id=\"newCustomerMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert allCustomersMenuItem != null : String.format("fx:id=\"allCustomersMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert addressMenu != null : String.format("fx:id=\"addressMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert newCountryMenuItem != null : String.format("fx:id=\"newCountryMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert newCityMenuItem != null : String.format("fx:id=\"newCityMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert newAddressMenuItem != null : String.format("fx:id=\"newAddressMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert allCountriesMenuItem != null : String.format("fx:id=\"allCountriesMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert usersMenu != null : String.format("fx:id=\"usersMenu\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert newUserMenuItem != null : String.format("fx:id=\"newUserMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert allUsersMenuItem != null : String.format("fx:id=\"allUsersMenuItem\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));
        assert contentPane != null : String.format("fx:id=\"contentPane\" was not injected: check your FXML file '%s'.",
                getFXMLResourceName(getClass()));

        current = this;
        
        view.appointment.ManageAppointments.setAsRootContent(new model.db.AppointmentsFilter(scheduler.App.getCurrent().getCurrentUser().get(), true));
    }
    
    //<editor-fold defaultstate="collapsed" desc="Event handler methods">
    
    @FXML
    void newAppointmentMenuItemClick(ActionEvent event) { addNewAppointment(); }

    public model.db.AppointmentRow addNewAppointment() {
        model.db.AppointmentRow model = view.appointment.EditAppointment.addNew();
        if (model != null)
            appointmentAdded.set(model);
        return model;
    }
    
    @FXML
    void allAppointmentsMenuItemClick(ActionEvent event) { view.appointment.ManageAppointments.setAsRootContent(); }
         
    @FXML
    void newCustomerMenuItemClick(ActionEvent event) { addNewCustomer(); }
    
    public model.db.CustomerRow addNewCustomer() {
        model.db.CustomerRow model = view.customer.EditCustomer.addNew();
        if (model != null)
            customerAdded.set(model);
        return model;
    }
    
    @FXML
    void allCustomersMenuItemClick(ActionEvent event) { view.customer.ManageCustomers.setAsRootContent(); }
        
    @FXML
    void newCountryMenuItemClick(ActionEvent event) { addNewCountry(); }
    
    public model.db.CountryRow addNewCountry() {
        model.db.CountryRow model = view.country.EditCountry.addNew();
        if (model != null)
            countryAdded.set(model);
        return model;
    }
    
    @FXML
    void allCountriesMenuItemClick(ActionEvent event) { view.country.ManageCountries.setAsRootContent(); }
    
    @FXML
    void newCityMenuItemClick(ActionEvent event) { addNewCity(); }
    
    public model.db.CityRow addNewCity() {
        model.db.CityRow model = view.city.EditCity.addNew();
        if (model != null)
            cityAdded.set(model);
        return model;
    }
    
    @FXML
    void newAddressMenuItemClick(ActionEvent event) { addNewAddress(); }
    
    public model.db.AddressRow addNewAddress() {
        model.db.AddressRow model = view.address.EditAddress.addNew();
        if (model != null)
            addressAdded.set(model);
        return model;
    }
    
    @FXML
    void newUserMenuItemClick(ActionEvent event) { addNewUser(); }
    
    public model.db.UserRow addNewUser() {
        model.db.UserRow model = view.user.EditUser.addNew();
        if (model != null)
            userAdded.set(model);
        return model;
    }
    
    @FXML
    void allUsersMenuItemClick(ActionEvent event) { view.user.ManageUsers.setAsRootContent(); }
    
    @FXML
    void exitButtonClick(ActionEvent event) { scheduler.App.getCurrent().getRootStage().hide(); }
    
    //</editor-fold>
    
    public void setContent(Node content, Controller controller) {
        contentControllerChanging.set(controller);
        contentPane.getChildren().clear();
        contentPane.setCenter(content);
        currentContentController.set(controller);
    }
    
    @SuppressWarnings("UseSpecificCatch")
    public static void setAsRootStageScene() {
        scheduler.App app = scheduler.App.getCurrent(); 
        try {
            ResourceBundle rb = ResourceBundle.getBundle(view.Controller.getGlobalizationResourceName(RootController.class), app.getCurrentLocale());
            FXMLLoader loader = new FXMLLoader(RootController.class.getResource(view.Controller.getFXMLResourceName(RootController.class)), rb);
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
