package scheduler;

/**
 *
 * @author lerwi
 */
public interface AppConstants {

    /**
     * The name of the general application globalization resource bundle.
     */
    public static final String GLOBALIZATION_RESOURCE_NAME = "scheduler/App";

    //<editor-fold defaultstate="collapsed" desc="Resource file keys">
    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Appointment Scheduler"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSCHEDULER = "appointmentScheduler";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "File "%s" not found."}.
     */
    public static final String RESOURCEKEY_FILENOTFOUND = "fileNotFound";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Phone Conference"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_PHONE = "appointmentType_phone";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Virtual Meeting"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL = "appointmentType_virtual";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Customer Site"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER = "appointmentType_customer";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Home Office"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_HOME = "appointmentType_home";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Germany Office"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_GERMANY = "appointmentType_germany";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "India Office"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_INDIA = "appointmentType_india";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Honduras Office"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_HONDURAS = "appointmentType_honduras";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Other in-person meeting"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTTYPE_OTHER = "appointmentType_other";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "FXML Loader Error"}.
     */
    public static final String RESOURCEKEY_FXMLLOADERERRORTITLE = "fxmlLoaderErrorTitle";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Error loading login scene content..."}.
     */
    public static final String RESOURCEKEY_FXMLLOADERERRORMESSAGE = "fxmlLoaderErrorMessage";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Nothing selected"}.
     */
    public static final String RESOURCEKEY_NOTHINGSELECTED = "nothingSelected";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "No item was selected."}.
     */
    public static final String RESOURCEKEY_NOITEMWASSELECTED = "noItemWasSelected";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Confirm Delete"}.
     */
    public static final String RESOURCEKEY_CONFIRMDELETE = "confirmDelete";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Working"}.
     */
    public static final String RESOURCEKEY_WORKING = "working";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Please wait..."}.
     */
    public static final String RESOURCEKEY_PLEASEWAIT = "pleaseWait";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Abort"}.
     */
    public static final String RESOURCEKEY_ABORT = "abort";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Cancel"}.
     */
    public static final String RESOURCEKEY_CANCEL = "cancel";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Connecting to database"}.
     */
    public static final String RESOURCEKEY_CONNECTINGTODB = "connectingToDb";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Logging in..."}.
     */
    public static final String RESOURCEKEY_LOGGINGIN = "loggingIn";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Connected to database"}.
     */
    public static final String RESOURCEKEY_CONNECTEDTODB = "connectedToDb";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Database access error"}.
     */
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Error reading data from database. See logs for details."}.
     */
    public static final String RESOURCEKEY_DBREADERROR = "dbReadError";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Getting appointments"}.
     */
    public static final String RESOURCEKEY_GETTINGAPPOINTMENTS = "gettingAppointments";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Delete"}.
     */
    public static final String RESOURCEKEY_DELETE = "delete";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "This action cannot be undone!..."}.
     */
    public static final String RESOURCEKEY_AREYOUSUREDELETE = "areYouSureDelete";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Unexpected Error"}.
     */
    public static final String RESOURCEKEY_UNEXPECTEDERRORTITLE = "unexpectedErrorTitle";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "An unexpected error has occurred."}.
     */
    public static final String RESOURCEKEY_UNEXPECTEDERRORHEADING = "unexpectedErrorHeading";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "See application logs for technical details."}.
     */
    public static final String RESOURCEKEY_UNEXPECTEDERRORDETAILS = "unexpectedErrorDetails";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Type:"}.
     */
    public static final String RESOURCEKEY_TYPE = "type";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Message:"}.
     */
    public static final String RESOURCEKEY_MESSAGE = "message";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Error Code:"}.
     */
    public static final String RESOURCEKEY_ERRORCODE = "errorCode";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "State:"}.
     */
    public static final String RESOURCEKEY_STATE = "state";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Related Exceptions:"}.
     */
    public static final String RESOURCEKEY_RELATEDEXCEPTIONS = "relatedExceptions";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Stack Trace:"}.
     */
    public static final String RESOURCEKEY_STACKTRACE = "stackTrace";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Caused By:"}.
     */
    public static final String RESOURCEKEY_CAUSEDBY = "causedBy";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Warning"}.
     */
    public static final String RESOURCEKEY_WARNING = "warning";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Delete Failure"}.
     */
    public static final String RESOURCEKEY_DELETEFAILURE = "deleteFailure";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Deleting Record"}.
     */
    public static final String RESOURCEKEY_DELETINGRECORD = "deletingRecord";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Error deleting record from database..."}.
     */
    public static final String RESOURCEKEY_ERRORDELETINGFROMDB = "errorDeletingFromDb";

    /**
     * Resource key in {@link App#resources} that contains the text for
     * {@code "A database access error occurred while trying to save changes to the database..."}.
     */
    public static final String RESOURCEKEY_ERRORSAVINGCHANGES = "errorSavingChanges";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Unable to delete the record from the database..."}.
     */
    public static final String RESOURCEKEY_DELETEDEPENDENCYERROR = "deleteDependencyError";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Unable to save the record to the database..."}.
     */
    public static final String RESOURCEKEY_SAVEDEPENDENCYERROR = "saveDependencyError";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Record Save Failure"}.
     */
    public static final String RESOURCEKEY_SAVEFAILURE = "saveFailure";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Load Error"}.
     */
    public static final String RESOURCEKEY_LOADERRORTITLE = "loadErrorTitle";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Unexpected error trying to load child window..."}.
     */
    public static final String RESOURCEKEY_LOADERRORMESSAGE = "loadErrorMessage";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Saving Changes"}.
     */
    public static final String RESOURCEKEY_SAVINGCHANGES = "savingChanges";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Log Message:"}.
     */
    public static final String RESOURCEKEY_LOGMESSAGE = "logMessage";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Active"}.
     */
    public static final String RESOURCEKEY_ACTIVE = "active";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Inactive"}.
     */
    public static final String RESOURCEKEY_INACTIVE = "inactive";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Administrator"}.
     */
    public static final String RESOURCEKEY_AMINISTRATOR = "administrator";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Unknown"}.
     */
    public static final String RESOURCEKEY_UNKNOWN = "unknown";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "(none)"}.
     */
    public static final String RESOURCEKEY_NONE = "none";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Initializing, Please wait..."}.
     */
    public static final String RESOURCEKEY_INITIALIZING = "initializing";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "All Appointments"}.
     */
    public static final String RESOURCEKEY_ALLAPPOINTMENTS = "allAppointments";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "All Appointments for %s"}.
     */
    public static final String RESOURCEKEY_ALLAPPOINTMENTSFOR = "allAppointmentsFor";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "All Appointments for %s and %s"}.
     */
    public static final String RESOURCEKEY_ALLAPPOINTMENTSFORBOTH = "allAppointmentsForBoth";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Appointments Before %s"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSBEFORE = "appointmentsBefore";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Appointments Before %s for %s"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSBEFOREFOR = "appointmentsBeforeFor";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Appointments Before %s for %s and %s"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSBEFOREFORBOTH = "appointmentsBeforeForBoth";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Appointments On or After %s"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSAFTER = "appointmentsAfter";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Appointments On or After %s for %s"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSAFTERFOR = "appointmentsAfterFor";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Appointments On or After %s for %s and %s"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSAFTERFORBOTH = "appointmentsAfterForBoth";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Appointments Between %s and %s"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSBETWEEN = "appointmentsBetween";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Appointments Between %s and %s for %s"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSBETWEENFOR = "appointmentsBetweenFor";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Appointments Between %s and %s for %s and %s"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSBETWEENFORBOTH = "appointmentsBetweenForBoth";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "All Current and Future Appointments"}.
     */
    public static final String RESOURCEKEY_ALLCURRENTANDFUTURE = "allCurrentAndFuture";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Current and Future Appointments for %s"}.
     */
    public static final String RESOURCEKEY_CURRENTANDFUTUREFOR = "currentAndFutureFor";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Current and Future Appointments for %s and %s"}.
     */
    public static final String RESOURCEKEY_CURRENTANDFUTUREFORBOTH = "currentAndFutureForBoth";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "My Current and Fture Appointments"}.
     */
    public static final String RESOURCEKEY_MYCURRENTANDFUTURE = "myCurrentAndFuture";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "All past appointments"}.
     */
    public static final String RESOURCEKEY_PASTAPPOINTMENTS = "pastAppointments";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Past Appointments for %s"}.
     */
    public static final String RESOURCEKEY_PASTAPPOINTMENTSFOR = "pastAppointmentsFor";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Past Appointments for %s and %s"}.
     */
    public static final String RESOURCEKEY_PASTAPPOINTMENTSFORBOTH = "pastAppointmentsForBoth";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "My Past Appointments"}.
     */
    public static final String RESOURCEKEY_MYPASTAPPOINTMENTS = "myPastAppointments";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Loading appointments, please wait..."}.
     */
    public static final String RESOURCEKEY_LOADINGAPPOINTMENTS = "loadingAppointments";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "All of My Appointments"}.
     */
    public static final String RESOURCEKEY_ALLMYAPPOINTMENTS = "allMyAppointments";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Appointments on %s"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSON = "appointmentsOn";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Appointments on %s for %s"}.
     */
    public static final String RESOURCEKEY_APPOINTMENTSONFOR = "appointmentsOnFor";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Loading customers, please wait..."}.
     */
    public static final String RESOURCEKEY_LOADINGCUSTOMERS = "loadingCustomers";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Loading users, please wait..."}.
     */
    public static final String RESOURCEKEY_LOADINGUSERS = "loadingUsers";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "All Customers"}.
     */
    public static final String ALL_CUSTOMERS = "allCustomers";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Active Customers"}.
     */
    public static final String ACTIVE_CUSTOMERS = "activeCustomers";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Inactive Customers"}.
     */
    public static final String INACTIVE_CUSTOMERS = "inactiveCustomers";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "All Users"}.
     */
    public static final String ALL_USERS = "allUsers";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Active Users"}.
     */
    public static final String ACTIVE_USERS = "activeUsers";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Inactive Users"}.
     */
    public static final String INACTIVE_USERS = "inactiveUsers";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Loading countries, please wait..."}.
     */
    public static final String RESOURCEKEY_LOADINGCOUNTRIES = "loadingCountries";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "All Countries"}.
     */
    public static final String RESOURCEKEY_ALLCOUNTRIES = "allCountries";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "All Current Appointments"}.
     */
    public static final String RESOURCEKEY_CURRENTAPPOINTMENTS = "currentAppointments";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Current Appointments for %s"}.
     */
    public static final String RESOURCEKEY_CURRENTFOR = "currentFor";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Current Appointments for %s and %s"}.
     */
    public static final String RESOURCEKEY_CURRENTFORBOTH = "currentForBoth";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "My Current Appointments"}.
     */
    public static final String RESOURCEKEY_MYCURRENT = "myCurrent";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "My Appointments Before %s"}.
     */
    public static final String RESOURCEKEY_MYAPPOINTMENTSBEFORE = "myAppointmentsBefore";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "My Appointments On or After %s"}.
     */
    public static final String RESOURCEKEY_MYAPPOINTMENTSONORAFTER = "myAppointmensOnOrAfter";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "My Appointments on %s"}.
     */
    public static final String RESOURCEKEY_MYAPPOINTMENTSON = "myAppointmentsOn";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "My Appointments Between %s and %s"}.
     */
    public static final String RESOURCEKEY_MYAPPOINTMENTSBETWEEN = "myAppointmentsBetween";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Current"}.
     */
    public static final String RESOURCEKEY_CURRENT = "current";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Current and Future"}.
     */
    public static final String RESOURCEKEY_CURRENTANDFUTURE = "currentAndFuture";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "Past"}.
     */
    public static final String RESOURCEKEY_PAST = "past";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "All"}.
     */
    public static final String RESOURCEKEY_ALL = "all";

    /**
     * Resource key in {@link App#resources} that contains the text for {@code "By Range"}.
     */
    public static final String RESOURCEKEY_BYRANGE = "byRange";

    //</editor-fold>
}
