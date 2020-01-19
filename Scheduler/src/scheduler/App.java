package scheduler;

import util.DbConnector;
import view.appointment.EditAppointment;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import scheduler.dao.UserImpl;
import util.Alerts;
import util.PwHash;
import view.RootController;
import view.SchedulerController;
import view.TaskWaiter;
import view.login.LoginScene;

/**
 * Application class for Scheduler
 * @author Leonard T. Erwine
 */
public class App extends Application {
    
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

    public static final String RESOURCEKEY_APPOINTMENTSCHEDULER = "appointmentScheduler";
    public static final String RESOURCEKEY_FILENOTFOUND = "fileNotFound";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_PHONE = "appointmentType_phone";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_VIRTUAL = "appointmentType_virtual";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_CUSTOMER = "appointmentType_customer";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_HOME = "appointmentType_home";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_GERMANY = "appointmentType_germany";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_INDIA = "appointmentType_india";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_HONDURAS = "appointmentType_honduras";
    public static final String RESOURCEKEY_APPOINTMENTTYPE_OTHER = "appointmentType_other";
    public static final String RESOURCEKEY_FXMLLOADERERRORTITLE = "fxmlLoaderErrorTitle";
    public static final String RESOURCEKEY_FXMLLOADERERRORMESSAGE = "fxmlLoaderErrorMessage";
    public static final String RESOURCEKEY_NOTHINGSELECTED = "nothingSelected";
    public static final String RESOURCEKEY_NOITEMWASSELECTED = "noItemWasSelected";
    public static final String RESOURCEKEY_CONFIRMDELETE = "confirmDelete";
    public static final String RESOURCEKEY_AREYOUSUREDELETE = "areYouSureDelete";
    public static final String RESOURCEKEY_WORKING = "working";
    public static final String RESOURCEKEY_PLEASEWAIT = "pleaseWait";
    public static final String RESOURCEKEY_ABORT = "abort";
    public static final String RESOURCEKEY_CANCEL = "cancel";
    public static final String RESOURCEKEY_CONNECTINGTODB = "connectingToDb";
    public static final String RESOURCEKEY_LOGGINGIN = "loggingIn";
    public static final String RESOURCEKEY_CONNECTEDTODB = "connectedToDb";
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";
    public static final String RESOURCEKEY_DBREADERROR = "dbReadError";
    public static final String RESOURCEKEY_GETTINGAPPOINTMENTS = "gettingAppointments";

    //</editor-fold>
    
    /**
     * The name of the general application globalization resource bundle.
     */
    public static final String GLOBALIZATION_RESOURCE_NAME = "scheduler/App";
    
    /**
     * The current application instance.
     */
    private static App current;
    
    public static final App getCurrent() { return current; }
    
    //<editor-fold defaultstate="collapsed" desc="primaryStage property">
    
    private Stage primaryStage;
    
    /**
     * Gets the primary {@link javafx.stage.Stage} for the application.
     * @return The primary {@link javafx.stage.Stage} for the application.
     */
    public Stage getPrimaryStage() { return primaryStage; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Globalization Properties">

    //<editor-fold defaultstate="collapsed" desc="allLanguages property">
    
    private ObservableList<Locale> allLanguages;
    
    /**
     * Gets a list of {@link java.util.Locale} objects representing languages supported by the application.
     * 
     * @return
     *          A list of {@link java.util.Locale} objects representing languages supported by the application.
     */
    public ObservableList<Locale> getAllLanguages() { return allLanguages; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="originalDisplayLocale property">
    
    // Tracks the original locale settings at the time the app is started, so it can be restored when the app ends
    private final Locale originalDisplayLocale;
    
    /**
     * Gets the original display {@link java.util.Locale} at application start-up.
     * 
     * @return
     *          The original display {@link java.util.Locale} at application start-up.
     */
    public Locale getOriginalDisplayLocale() { return originalDisplayLocale; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="originalFormatLocale property">
    
    private final Locale originalFormatLocale;
    
    /**
     * Gets the original format {@link java.util.Locale} at application start-up.
     * 
     * @return
     *          The original format {@link java.util.Locale} at application start-up.
     */
    public Locale getOriginalFormatLocale() { return originalFormatLocale; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="resources property">
    
    private ResourceBundle resources;
    
    /**
     * Gets the application-global resource bundle for the current language.
     * 
     * @return
     *          The application-global resource bundle for the current language.
     */
    public ResourceBundle getResources() { return resources; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="appointmentTypes property">
    
    private final AppointmentTypes appointmentTypes;
    
    /**
     * Gets an observable map that maps the appointment type codes to the locale-specific display value.
     * 
     * @return
     *          An observable map that maps the appointment type codes to the locale-specific display value.
     */
    public ObservableMap<String, String> getAppointmentTypes() { return appointmentTypes; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fullTimeFormatter property">
    
    private DateTimeFormatter fullTimeFormatter;
    
    /**
     * Gets the current locale-specific formatter for full time strings.
     * 
     * @return
     *          The current locale-specific formatter for full time strings.
     */
    public DateTimeFormatter getFullTimeFormatter() { return fullTimeFormatter; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fullDateFormatter property">
    
    private DateTimeFormatter fullDateFormatter;
    
    /**
     * Gets the current locale-specific formatter for full date strings.
     * 
     * @return
     *          The current locale-specific formatter for full date strings.
     */
    public DateTimeFormatter getFullDateFormatter() { return fullDateFormatter; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="shortDateTimeFormatter property">
    
    private DateTimeFormatter shortDateTimeFormatter;
    
    /**
     * Gets the current locale-specific formatter for short date/time strings.
     * 
     * @return
     *          The current locale-specific formatter for short date/time strings.
     */
    public DateTimeFormatter getShortDateTimeFormatter() { return shortDateTimeFormatter; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fullDateTimeFormatter property">
    
    private DateTimeFormatter fullDateTimeFormatter;
    
    /**
     * Gets the current locale-specific formatter for full date/time strings.
     * 
     * @return
     *          The current locale-specific formatter for full date/time strings.
     */
    public DateTimeFormatter getFullDateTimeFormatter() { return fullDateTimeFormatter; }
    
    //</editor-fold>
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="currentUser property">
    
    private static UserImpl currentUser = null;
    
    public static UserImpl getCurrentUser() { return currentUser; }
    
    //</editor-fold>
    
    private static final Logger LOG;
    
    static {
        LOG = Logger.getLogger(App.class.getName());
    }
    
    /**
     * Sets the current application {@link java.util.Locale}.
     * 
     * @param value
     *          The new application {@link java.util.Locale}.
     */
    public static void setCurrentLocale(Locale value) {
        Locale.setDefault(Locale.Category.DISPLAY, value);
        Locale.setDefault(Locale.Category.FORMAT, value);
        if (current != null)
            current.setResources(ResourceBundle.getBundle(GLOBALIZATION_RESOURCE_NAME, value));
    }

    private void setResources(ResourceBundle bundle) {
        resources = bundle;
        appointmentTypes.load(bundle);
        Locale locale = Locale.getDefault(Locale.Category.DISPLAY);
        fullTimeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
        fullDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
        shortDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(locale).withZone(ZoneId.systemDefault());
        fullDateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(locale).withZone(ZoneId.systemDefault());
    }
    
    public App() {
        // Store the original locale settings so they can be restored when app ends
        originalDisplayLocale = Locale.getDefault(Locale.Category.DISPLAY);
        originalFormatLocale = Locale.getDefault(Locale.Category.FORMAT);
        appointmentTypes = new AppointmentTypes();
    }
    
    //<editor-fold defaultstate="collapsed" desc="App Lifecycle Members">
    
    /**
     * The application main entry point.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    @SuppressWarnings("UseSpecificCatch")
    public void start(Stage stage) throws Exception {
        current = this;
        primaryStage = stage;
        
        // Ensure app config is freshly loaded.
        AppConfig.refresh();
        
        allLanguages = new AllLanguages(AppConfig.getLanguages());
        
        try {
            ResourceBundle rb = ResourceBundle.getBundle(SchedulerController.getGlobalizationResourceName(LoginScene.class), Locale.getDefault(Locale.Category.DISPLAY));
            FXMLLoader loader = new FXMLLoader(LoginScene.class.getResource(SchedulerController.getFXMLResourceName(LoginScene.class)), rb);
            Scene scene = new Scene(loader.load());
            primaryStage.setScene(scene);
            stage.show();
        } catch (Throwable ex) {
            LOG.log(Level.SEVERE, null, ex);
            Alerts.showErrorAlert(resources.getString(RESOURCEKEY_FXMLLOADERERRORTITLE), resources.getString(RESOURCEKEY_FXMLLOADERERRORMESSAGE));
        }
    }
    
    @Override
    public void stop() throws Exception {
        DbConnector.forceClose();
        // Resotre original locale settings
        Locale.setDefault(Locale.Category.DISPLAY, originalDisplayLocale);
        Locale.setDefault(Locale.Category.FORMAT, originalFormatLocale);
        super.stop();
    }

    //</editor-fold>

    private class LoginTask extends TaskWaiter<UserImpl> {
        private final String userName, password;
        LoginTask(String userName, String password) {
            super(getPrimaryStage(), getResources().getString(RESOURCEKEY_CONNECTINGTODB), getResources().getString(RESOURCEKEY_LOGGINGIN));
            this.userName = userName;
            this.password = password;
        }

        @Override
        protected UserImpl getResult() throws Exception {
            LOG.log(Level.INFO, "Task getResult overload invoked");
            Optional<UserImpl> result;
            try (DbConnector dep = new DbConnector()) {
                if (dep.getState() != DbConnector.STATE_CONNECTED) {
                    LOG.log(Level.INFO, "Not connected");
                    return null;
                }
                Platform.runLater(() -> {
                    LOG.log(Level.INFO, "Updating message");
                    updateMessage(getResources().getString(RESOURCEKEY_CONNECTEDTODB));
                });
                LOG.log(Level.INFO, "Invoking UserImpl.getByUserName");
                result = UserImpl.getByUserName(dep.getConnection(), userName);
            }
            if (result.isPresent()) {
                LOG.log(Level.INFO, "User found");
                // The password string stored in the database is a base-64 string that contains a cryptographic hash of the password
                // along with the cryptographic seed. A hash will be created from password argument using the same cryptographic seed
                // as the stored password. If the password is correct, then the hash values will match.
                PwHash hash = new PwHash(result.get().getPassword(), false);
                if (hash.test(password)) {
                    LOG.log(Level.INFO, "Password matched");
                    currentUser = result.get();
                    LOG.log(Level.INFO, "Returning from tryLoginUser");
                    return result.get();
                }
                LOG.log(Level.WARNING, "Password mismatch");
            } else
                LOG.log(Level.WARNING, "No matching userName found");
            LOG.log(Level.INFO, "Returning from tryLoginUser");
            return null;
        }
        
    }
    
    /**
     * Looks up a user from the database and sets the current logged in user for the application if the password hash matches.
     * @param userName The login name for the user to look up.
     * @param password The raw password provided by the user.
     * @param onNotSucceeded Handles login failures. The {@link Exception} argument will be null if there were no exceptions
     * and either the login was not found or the password hash did not match.
     */
    public void tryLoginUser(String userName, String password, Consumer<Exception> onNotSucceeded) {
        LoginTask task = new LoginTask(userName, password);
        EventHandler<WorkerStateEvent> handler = (event) -> {
            LOG.log(Level.INFO, "Task completion handler invoked");
            try {
                UserImpl user = task.get();
                if (user == null) {
                    if (onNotSucceeded != null) {
                        if (Platform.isFxApplicationThread())
                            onNotSucceeded.accept(null);
                        else
                            Platform.runLater(() -> {
                                onNotSucceeded.accept(null);
                            });
                    }
                } else if (Platform.isFxApplicationThread()) {
                    RootController.setAsRootStageScene();
                } else
                    Platform.runLater(() -> {
                        RootController.setAsRootStageScene();
                    });
            } catch (InterruptedException | ExecutionException ex) {
                LOG.log(Level.SEVERE, null, ex);
                if (onNotSucceeded != null) {
                    if (Platform.isFxApplicationThread())
                        onNotSucceeded.accept(ex);
                    else
                        Platform.runLater(() -> {
                            onNotSucceeded.accept(ex);
                        });
                }
            }
        };
        task.setOnCancelled(handler);
        task.setOnFailed(handler);
        task.setOnSucceeded(handler);
        TaskWaiter.execute(task);
    }
    
    private class AppointmentTypes implements ObservableMap<String, String> {
        private final ObservableMap<String, String> backingMap;
        private final ObservableMap<String, String> readOnlyMap;
        AppointmentTypes()
        {
            backingMap = FXCollections.observableHashMap();
            Stream.of(EditAppointment.APPOINTMENT_CODE_PHONE, EditAppointment.APPOINTMENT_CODE_VIRTUAL, EditAppointment.APPOINTMENT_CODE_CUSTOMER,
                EditAppointment.APPOINTMENT_CODE_HOME, EditAppointment.APPOINTMENT_CODE_GERMANY, EditAppointment.APPOINTMENT_CODE_INDIA,
                EditAppointment.APPOINTMENT_CODE_HONDURAS, EditAppointment.APPOINTMENT_CODE_OTHER).forEach((String key) -> {
                    backingMap.put(key, key);
                });
            readOnlyMap = FXCollections.unmodifiableObservableMap(backingMap);
        }
        
        private void load(ResourceBundle rb) {
            backingMap.entrySet().forEach((Map.Entry<String, String> t) -> {
                String key = "appointmentType_" + t.getKey();
                t.setValue((rb.containsKey(key)) ? rb.getString(key) : t.getKey());
            });
        }

        @Override
        public void addListener(MapChangeListener<? super String, ? super String> listener) {
            readOnlyMap.addListener(listener);
        }

        @Override
        public void removeListener(MapChangeListener<? super String, ? super String> listener) {
            readOnlyMap.removeListener(listener);
        }

        @Override
        public int size() { return readOnlyMap.size(); }

        @Override
        public boolean isEmpty() { return readOnlyMap.isEmpty(); }

        @Override
        public boolean containsKey(Object key) {
            return key != null && key instanceof String && backingMap.containsKey(((String)key).trim().toLowerCase());
        }

        @Override
        public boolean containsValue(Object value) { return readOnlyMap.containsValue(value); }

        @Override
        public String get(Object key) {
            String code;
            if (key == null || !(key instanceof String) || (code = ((String)key).trim()).isEmpty())
                return "";
            String lc = code.toLowerCase();
            return (backingMap.containsKey(lc)) ? backingMap.get(lc) : code;
        }

        @Override
        public String put(String key, String value) { throw new UnsupportedOperationException(); }

        @Override
        public String remove(Object key) { throw new UnsupportedOperationException(); }

        @Override
        public void putAll(Map<? extends String, ? extends String> m) { throw new UnsupportedOperationException(); }

        @Override
        public void clear() { throw new UnsupportedOperationException(); }

        @Override
        public Set<String> keySet() { return readOnlyMap.keySet(); }

        @Override
        public Collection<String> values() { return readOnlyMap.values(); }

        @Override
        public Set<Map.Entry<String, String>> entrySet() { return readOnlyMap.entrySet(); }

        @Override
        public void addListener(InvalidationListener listener) { readOnlyMap.addListener(listener); }

        @Override
        public void removeListener(InvalidationListener listener) { readOnlyMap.removeListener(listener); }
    }
    
    private class AllLanguages implements ObservableList<Locale> {
        final ObservableList<Locale> backingList;
        final ObservableList<Locale> readOnlyList;

        private AllLanguages(String[] languageIds) {
            this.backingList = FXCollections.observableArrayList();
            this.readOnlyList = FXCollections.unmodifiableObservableList(backingList);

            // Attempt to find a match for the current display language amongst the languages supported by the app.
            final String lt = originalDisplayLocale.toLanguageTag();
            // First look for one that is an exact match with the language tag.
            Optional<String> cl = Arrays.stream(languageIds).filter((String id) -> id.equals(lt)).findFirst();
            if (!cl.isPresent()) {
                // Look for one that matches the ISO3 language code.
                final String iso3 = originalDisplayLocale.getISO3Language();
                cl = Arrays.stream(languageIds).filter((String id) -> id.equals(iso3)).findFirst();
                if (!cl.isPresent()) {
                    // Look for one that matches the ISO2 language code.
                    final String ln = originalDisplayLocale.getLanguage();
                    cl = Arrays.stream(languageIds).filter((String id) -> id.equals(ln)).findFirst();
                }
            }

            // Populate list of Locale objects.
            Locale toSelect;
            if (cl.isPresent()) {
                for (String n : AppConfig.getLanguages())
                    backingList.add((n.equals(cl.get())) ? originalDisplayLocale : new Locale(n));
                toSelect = originalDisplayLocale;
            } else {
                for (String n : AppConfig.getLanguages())
                    backingList.add(new Locale(n));
                toSelect = backingList.get(0);
            }

            setCurrentLocale(toSelect);
        }
        
        @Override
        public void addListener(ListChangeListener<? super Locale> listener) { readOnlyList.addListener(listener); }

        @Override
        public void removeListener(ListChangeListener<? super Locale> listener) { readOnlyList.removeListener(listener); }

        @Override
        public boolean addAll(Locale... elements) { throw new UnsupportedOperationException(); }

        @Override
        public boolean setAll(Locale... elements) { throw new UnsupportedOperationException(); }

        @Override
        public boolean setAll(Collection<? extends Locale> col) { throw new UnsupportedOperationException(); }

        @Override
        public boolean removeAll(Locale... elements) { throw new UnsupportedOperationException(); }

        @Override
        public boolean retainAll(Locale... elements) { throw new UnsupportedOperationException(); }

        @Override
        public void remove(int from, int to) { throw new UnsupportedOperationException(); }

        @Override
        public int size() { return backingList.size(); }

        @Override
        public boolean isEmpty() { return backingList.isEmpty(); }

        @Override
        public boolean contains(Object o) { return backingList.contains(o); }

        @Override
        public Iterator<Locale> iterator() { return backingList.iterator(); }

        @Override
        public Object[] toArray() { return backingList.toArray(); }

        @Override
        @SuppressWarnings("SuspiciousToArrayCall")
        public <T> T[] toArray(T[] a) { return backingList.toArray(a); }

        @Override
        public boolean add(Locale e) { throw new UnsupportedOperationException(); }

        @Override
        public boolean remove(Object o) { throw new UnsupportedOperationException(); }

        @Override
        public boolean containsAll(Collection<?> c) { return backingList.containsAll(c); }

        @Override
        public boolean addAll(Collection<? extends Locale> c) { throw new UnsupportedOperationException(); }

        @Override
        public boolean addAll(int index, Collection<? extends Locale> c) { throw new UnsupportedOperationException(); }

        @Override
        public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }

        @Override
        public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }

        @Override
        public void clear() { throw new UnsupportedOperationException(); }

        @Override
        public Locale get(int index) { return backingList.get(index); }

        @Override
        public Locale set(int index, Locale element) { throw new UnsupportedOperationException(); }

        @Override
        public void add(int index, Locale element) { throw new UnsupportedOperationException(); }

        @Override
        public Locale remove(int index) { throw new UnsupportedOperationException(); }

        @Override
        public int indexOf(Object o) { return backingList.indexOf(o); }

        @Override
        public int lastIndexOf(Object o) { return backingList.lastIndexOf(o); }

        @Override
        public ListIterator<Locale> listIterator() { return readOnlyList.listIterator(); }

        @Override
        public ListIterator<Locale> listIterator(int index) { return readOnlyList.listIterator(index); }

        @Override
        public List<Locale> subList(int fromIndex, int toIndex) { return readOnlyList.subList(fromIndex, toIndex); }

        @Override
        public void addListener(InvalidationListener listener) { readOnlyList.addListener(listener); }

        @Override
        public void removeListener(InvalidationListener listener) { readOnlyList.removeListener(listener); }
    }
}
