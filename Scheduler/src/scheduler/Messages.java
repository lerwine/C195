package scheduler;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;

/**
 * Gets messages that are specific to the current {@link Locale}.
 * @author Leonard T. Erwine
 */
public class Messages {
    private static Messages _current;
    private final ResourceBundle _resourceBundle;
    
    public static Messages current() { return _current; }
    
    public ResourceBundle resourceBundle() { return _resourceBundle; }
    
    public static void setCurrent(Messages messages) {
        if (messages == null)
            throw new InternalException("Messages object cannot be null");
        _current = messages;
    }
        
    public Messages(Locale locale) {
        _resourceBundle = ResourceBundle.getBundle("Messages", locale);
    }
    
    public Messages(ResourceBundle resourceBundle) {
        _resourceBundle = resourceBundle;
    }
    
    /**
     * Gets the message "Active State" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "activeState" key.
     */
    public final String getActiveState() { return _resourceBundle.getString("activeState"); }
    
    /**
     * Gets the message "Add New" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "addNew" key.
     */
    public final String getAddNew() { return _resourceBundle.getString("addNew"); }
    
    /**
     * Gets the message "Add New User" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "addNewUser" key.
     */
    public final String getAddNewUser() { return _resourceBundle.getString("addNewUser"); }
    
    /**
     * Gets the message "Address" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "address" key.
     */
    public final String getAddress() { return _resourceBundle.getString("address"); }
      
    /**
     * Gets the message "Administrative user" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "adminstrativeUser" key.
     */
    public final String getAdminstrativeUser() { return _resourceBundle.getString("adminstrativeUser"); }
    
    /**
     * Gets the message "All Appointments" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "allAppointments" key.
     */
    public final String getAllAppointments() { return _resourceBundle.getString("allAppointments"); }
    
    /**
     * Gets the message "All Countries" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "allCountries" key.
     */
    public final String getAllCountries() { return _resourceBundle.getString("allCountries"); }
    
    /**
     * Gets the message "All Customers" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "allCustomers" key.
     */
    public final String getAllCustomers() { return _resourceBundle.getString("allCustomers"); }
    
    /**
     * Gets the message "All Users" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "allUsers" key.
     */
    public final String getAllUsers() { return _resourceBundle.getString("allUsers"); }
    
    /**
     * Gets the message "Appointments" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "appointments" key.
     */
    public final String getAppointments() { return _resourceBundle.getString("appointments"); }
    
    /**
     * Gets the message "Appointment Scheduler" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "appointmentScheduler" key.
     */
    public final String getAppointmentScheduler() { return _resourceBundle.getString("appointmentScheduler"); }
    
    /**
     * Gets the message "Appointment Scheduler Login" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "appointmentSchedulerLogin" key.
     */
    public final String getAppointmentSchedulerLogin() { return _resourceBundle.getString("appointmentSchedulerLogin"); }
    
    /**
     * Gets the message "Authentication" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "authentication" key.
     */
    public final String getAuthentication() { return _resourceBundle.getString("authentication"); }
    
    /**
     * Gets the message "Authentication Error" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "authError" key.
     */
    public final String getAuthError() { return _resourceBundle.getString("authError"); }
    
    /**
     * Gets the message "Cancel" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "cancel" key.
     */
    public final String getCancel() { return _resourceBundle.getString("cancel"); }
    
    /**
     * Gets the message "Change Password" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "changePassword" key.
     */
    public final String getChangePassword() { return _resourceBundle.getString("changePassword"); }
    
    /**
     * Gets the message "Confirm Password" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "confirmPassword" key.
     */
    public final String getConfirmPassword() { return _resourceBundle.getString("confirmPassword"); }
    
    /**
     * Gets the message "Created By" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "createdBy" key.
     */
    public final String getCreatedBy() { return _resourceBundle.getString("createdBy"); }
    
    /**
     * Gets the message "Created On" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "createdOn" key.
     */
    public final String getCreatedOn() { return _resourceBundle.getString("createdOn"); }
    
    /**
     * Gets the message "Customer" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "customer" key.
     */
    public final String getCustomer() { return _resourceBundle.getString("customer"); }
    
    /**
     * Gets the message "Customers" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "customers" key.
     */
    public final String getCustomers() { return _resourceBundle.getString("customers"); }
    
    /**
     * Gets the message "Database access error" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "dbAccessError" key.
     */
    public final String getDbAccessError() { return _resourceBundle.getString("dbAccessError"); }
    
    /**
     * Gets the message "A database error occurred while validating user credentials. See system logs for details." translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "dbCredentialAccessError" key.
     */
    public final String getDbCredentialAccessError() { return _resourceBundle.getString("dbCredentialAccessError"); }
    
    /**
     * Gets the message "Edit User %s" translated for the current {@link Locale}.
     * @param userName The user name to be incorporated into the message.
     * @return The {@link Locale}-specific message associated with the "editUser" key.
     */
    public final String getEditUser(String userName) { return String.format(_resourceBundle.getString("editUser"), userName); }
    
    /**
     * Gets the message "Password cannot be empty" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "emptyPassword" key.
     */
    public final String getEmptyPassword() { return _resourceBundle.getString("emptyPassword"); }
    
    /**
     * Gets the message "User name cannot be empty" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "emptyUserName" key.
     */
    public final String getEmptyUserName() { return _resourceBundle.getString("emptyUserName"); }
    
    /**
     * Gets the message "End" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "end" key.
     */
    public final String getEnd() { return _resourceBundle.getString("end"); }
    
    /**
     * Gets the message "Exit" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "exit" key.
     */
    public final String getExit() { return _resourceBundle.getString("exit"); }
    
    /**
     * Gets the message "Validation for field %s failed" translated for the current {@link Locale}.
     * @param fieldName The name of the field to be incorporated into the message.
     * @return The {@link Locale}-specific message associated with the "fieldValidationFailed" key.
     */
    public final String getFieldValidationFailed(String fieldName) { return String.format(_resourceBundle.getString("fieldValidationFailed"), fieldName); }
    
    /**
     * Gets the message "Please correct this error before saving." translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "fieldValidationInstruct" key.
     */
    public final String getFieldValidationInstruct() { return _resourceBundle.getString("fieldValidationInstruct"); }
    
    /**
     * Gets the message "File "%s" not found." translated for the current {@link Locale}.
     * @param fileName The name of the file to be incorporated into the message.
     * @return The {@link Locale}-specific message associated with the "fileNotFound" key.
     */
    public final String getFileNotFound(String fileName) { return String.format(_resourceBundle.getString("fileNotFound"), fileName); }
    
    /**
     * Gets the message "Inactive" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "inactive" key.
     */
    public final String getInactive() { return _resourceBundle.getString("inactive"); }
    
    /**
     * Gets the message "An internal error occurred while validating user credentials. See system logs for details." translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "internalCredentialError" key.
     */
    public final String getInternalCredentialError() { return _resourceBundle.getString("internalCredentialError"); }
    
    /**
     * Gets the message "Invalid username or password" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "invalidCredentials" key.
     */
    public final String getInvalidCredentials() { return _resourceBundle.getString("invalidCredentials"); }
    
    /**
     * Gets the message "Language" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "language" key.
     */
    public final String getLanguage() { return _resourceBundle.getString("language"); }
    
    /**
     * Gets the message "Log In" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "login" key.
     */
    public final String getLogin() { return _resourceBundle.getString("login"); }
    
    /**
     * Gets the message "Name" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "name" key.
     */
    public final String getName() { return _resourceBundle.getString("name"); }
    
    /**
     * Gets the message "New" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "new" key.
     */
    public final String getNew() { return _resourceBundle.getString("new"); }
    
    /**
     * Gets the message "New Address" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "newAddress" key.
     */
    public final String getNewAddress() { return _resourceBundle.getString("newAddress"); }
    
    /**
     * Gets the message "New City" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "newCity" key.
     */
    public final String getNewCity() { return _resourceBundle.getString("newCity"); }
    
    /**
     * Gets the message "New Country" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "newCountry" key.
     */
    public final String getNewCountry() { return _resourceBundle.getString("newCountry"); }
    
    /**
     * Gets the message "Normal user" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "normalUser" key.
     */
    public final String getNormalUser() { return _resourceBundle.getString("normalUser"); }
    
    /**
     * Gets the message "Password" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "password" key.
     */
    public final String getPassword() { return _resourceBundle.getString("password"); }
    
    /**
     * Gets the message "Password and confirmation do not match" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "passwordMismatch" key.
     */
    public final String getPasswordMismatch() { return _resourceBundle.getString("passwordMismatch"); }
    
    /**
     * Gets the message "Properties Load Error" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "propertiesLoadError" key.
     */
    public final String getPropertiesLoadError() { return _resourceBundle.getString("propertiesLoadError"); }
    
    /**
     * Gets the message "Save Changes" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "saveChanges" key.
     */
    public final String getSaveChanges() { return _resourceBundle.getString("saveChanges"); }
    
    /**
     * Gets the message "Start" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "end" key.
     */
    public final String getStart() { return _resourceBundle.getString("start"); }
    
    /**
     * Gets the message "Title" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "end" key.
     */
    public final String getTitle() { return _resourceBundle.getString("title"); }
    
    /**
     * Gets the message "Type" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "end" key.
     */
    public final String getType() { return _resourceBundle.getString("type"); }
    
    /**
     * Gets the message "Updated By" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "updatedBy" key.
     */
    public final String getUpdatedBy() { return _resourceBundle.getString("updatedBy"); }
    
    /**
     * Gets the message "Updated On" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "updatedOn" key.
     */
    public final String getUpdatedOn() { return _resourceBundle.getString("updatedOn"); }
    
    /**
     * Gets the message "User Name" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "userName" key.
     */
    public final String getUserName() { return _resourceBundle.getString("userName"); }
    
    /**
     * Gets the message "That user name is not available" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "userNameNotAvailable" key.
     */
    public final String getUserNameNotAvailable() { return _resourceBundle.getString("userNameNotAvailable"); }
    
    /**
     * Gets the message "Users" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "users" key.
     */
    public final String getUsers() { return _resourceBundle.getString("users"); }
       
    /**
     * Gets the message "Validation Warning" translated for the current {@link Locale}.
     * @return The {@link Locale}-specific message associated with the "validationWarning" key.
     */
    public final String getValidationWarning() { return _resourceBundle.getString("validationWarning"); }
    
    /**
     * Shows {@link Alert} to notify user that there was a non-database error while trying to validate their credentials.
     */
    public final void notifyCredentialValidationError() {
        Alert alert = new Alert(Alert.AlertType.ERROR, getInternalCredentialError(), ButtonType.OK);
        alert.setTitle(getAuthError());
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
    
    /**
     * Shows {@link Alert} to notify user that there was a database-related error while trying to validate their credentials.
     */
    public final void notifyDbCredentialAccessError() {
        Alert alert = new Alert(Alert.AlertType.ERROR, getDbCredentialAccessError(), ButtonType.OK);
        alert.setTitle(getAuthError());
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
    
    /**
     * Shows {@link Alert} to notify user that validation for a specific field failed.
     * @param fieldName The name of the field that failed.
     */
    public final void notifyFieldValidationFailed(String fieldName) {
        Alert alert = new Alert(Alert.AlertType.ERROR, getFieldValidationInstruct(), ButtonType.OK);
        alert.setTitle(getValidationWarning());
        alert.setHeaderText(getFieldValidationFailed(fieldName));
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
    
    /**
     * Shows {@link Alert} to notify user that the username or password was invalid.
     */
    public final void notifyInvalidCredentials() {
        Alert alert = new Alert(Alert.AlertType.ERROR, getInvalidCredentials(), ButtonType.OK);
        alert.setTitle(getAuthError());
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
    
    /**
     * Shows {@link Alert} to notify user that there was an error trying to load the application properties file.
     * @param fileName The name of the properties file.
     */
    public final void notifyPropertyLoadError(String fileName) {
        Alert alert = new Alert(Alert.AlertType.ERROR, getFileNotFound(fileName), ButtonType.OK);
        alert.setTitle(getPropertiesLoadError());
        alert.initStyle(StageStyle.UTILITY);
        alert.showAndWait();
    }
}
