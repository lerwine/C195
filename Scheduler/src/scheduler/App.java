package scheduler;

import com.mysql.jdbc.Connection;
import view.appointment.EditAppointment;
import java.sql.SQLException;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.application.Application;
import javafx.beans.InvalidationListener;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.beans.property.ReadOnlyMapWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.db.UserRow;

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
    public static final String RESOURCEKEY_AREYOURSUREDELETE = "areYourSureDelete";

    //</editor-fold>
    
    /**
     * The name of the general application globalization resource bundle.
     */
    public static final String GLOBALIZATION_RESOURCE_NAME = "scheduler/App";
    
    /**
     * The current application instance.
     */
    public static final ReadOnlyObjectWrapper<App> CURRENT;
    
    //<editor-fold defaultstate="collapsed" desc="primaryStage property">
    
    private final ReadOnlyObjectWrapper<Stage> primaryStage;
    
    /**
     * Gets the primary {@link javafx.stage.Stage} for the application.
     * 
     * @return
     *          The primary {@link javafx.stage.Stage} for the application.
     */
    public Stage getPrimaryStage() { return primaryStage.get(); }
    
    /**
     * The primary application {@link javafx.stage.Stage} property.
     * 
     * @return
     *          The primary application {@link javafx.stage.Stage} property.
     */
    public ReadOnlyObjectProperty<Stage> primaryStageProperty() { return primaryStage.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="Globalization Properties">

    //<editor-fold defaultstate="collapsed" desc="allLanguages property">
    
    private final ReadOnlyListWrapper<Locale> allLanguages;
    
    /**
     * Gets a list of {@link java.util.Locale} objects representing languages supported by the application.
     * 
     * @return
     *          A list of {@link java.util.Locale} objects representing languages supported by the application.
     */
    public ObservableList<Locale> getAllLanguages() { return allLanguages.get(); }
    
    /**
     * Supported {@link java.util.Locale} list property.
     * 
     * @return
     *          Supported {@link java.util.Locale} list property.
     */
    public ReadOnlyListProperty<Locale> allLanguagesProperty() { return allLanguages.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="originalDisplayLocale property">
    
    // Tracks the original locale settings at the time the app is started, so it can be restored when the app ends
    private final ReadOnlyObjectWrapper<Locale> originalDisplayLocale;
    
    /**
     * Gets the original display {@link java.util.Locale} at application start-up.
     * 
     * @return
     *          The original display {@link java.util.Locale} at application start-up.
     */
    public Locale getOriginalDisplayLocale() { return originalDisplayLocale.get(); }
    
    /**
     * The original display {@link java.util.Locale} property.
     * 
     * @return
     *          The original display {@link java.util.Locale} property.
     */
    public ReadOnlyObjectProperty originalDisplayLocaleProperty() {
        return originalDisplayLocale.getReadOnlyProperty();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="originalFormatLocale property">
    
    private final ReadOnlyObjectWrapper<Locale> originalFormatLocale;
    
    /**
     * Gets the original format {@link java.util.Locale} at application start-up.
     * 
     * @return
     *          The original format {@link java.util.Locale} at application start-up.
     */
    public Locale getOriginalFormatLocale() { return originalFormatLocale.get(); }
    
    /**
     * The original format {@link java.util.Locale} property.
     * 
     * @return
     *          The original format {@link java.util.Locale} property.
     */
    public ReadOnlyObjectProperty<Locale> originalFormatLocaleProperty() {
        return originalFormatLocale.getReadOnlyProperty();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="resources property">
    
    private final ReadOnlyObjectWrapper<ResourceBundle> resources;
    
    /**
     * Gets the application-global resource bundle for the current language.
     * 
     * @return
     *          The application-global resource bundle for the current language.
     */
    public ResourceBundle getResources() { return resources.get(); }
    
    /**
     * Application-global resource bundle property.
     * 
     * @return
     *          Application-global resource bundle property.
     */
    public ReadOnlyObjectProperty resourcesProperty() { return resources.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="currentLocale property">
    
    private final ObjectProperty<Locale> currentLocale;

    /**
     * Gets the currently selected {@link java.util.Locale} (language).
     * 
     * @return
     *          The currently selected {@link java.util.Locale} (language).
     */
    public Locale getCurrentLocale() { return currentLocale.get(); }

    /**
     * Sets the current application {@link java.util.Locale}.
     * 
     * @param value
     *          The new application {@link java.util.Locale}.
     */
    public void setCurrentLocale(Locale value) { currentLocale.set(value); }

    /**
     * The currently selected {@link java.util.Locale} property.
     * 
     * @return
     *          The currently selected {@link java.util.Locale} property.
     */
    public ObjectProperty<Locale> currentLocaleProperty() { return currentLocale; }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="appointmentTypes property">
    
    private final ReadOnlyMapWrapper<String, String> appointmentTypes;
    
    /**
     * Gets an observable map that maps the appointment type codes to the locale-specific display value.
     * 
     * @return
     *          An observable map that maps the appointment type codes to the locale-specific display value.
     */
    public ObservableMap<String, String> getAppointmentTypes() { return appointmentTypes.get(); }
    
    /**
     * Locale-specific appointment type code / display value mapping.
     * 
     * @return
     *          Locale-specific appointment type code / display value mapping.
     */
    public ReadOnlyMapProperty<String, String> appointmentTypesProperty() { return appointmentTypes.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fullTimeFormatter property">
    
    private final ReadOnlyObjectWrapper<DateTimeFormatter> fullTimeFormatter;
    
    /**
     * Gets the current locale-specific formatter for full time strings.
     * 
     * @return
     *          The current locale-specific formatter for full time strings.
     */
    public DateTimeFormatter getFullTimeFormatter() { return fullTimeFormatter.get(); }
    
    /**
     * Current full locale-specific time formatter property.
     * 
     * @return
     *          Current full locale-specific time formatter property.
     */
    public ReadOnlyObjectProperty<DateTimeFormatter> fullTimeFormatterProperty() {
        return fullTimeFormatter.getReadOnlyProperty();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fullDateFormatter property">
    
    private final ReadOnlyObjectWrapper<DateTimeFormatter> fullDateFormatter;
    
    /**
     * Gets the current locale-specific formatter for full date strings.
     * 
     * @return
     *          The current locale-specific formatter for full date strings.
     */
    public DateTimeFormatter getFullDateFormatter() { return fullDateFormatter.get(); }
    
    /**
     * Current full locale-specific date formatter property.
     * 
     * @return
     *          Current full locale-specific date formatter property.
     */
    public ReadOnlyObjectProperty<DateTimeFormatter> fullDateFormatterProperty() {
        return fullDateFormatter.getReadOnlyProperty();
    }
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="shortDateTimeFormatter property">
    
    private final ReadOnlyObjectWrapper<DateTimeFormatter> shortDateTimeFormatter;
    
    /**
     * Gets the current locale-specific formatter for short date/time strings.
     * 
     * @return
     *          The current locale-specific formatter for short date/time strings.
     */
    public DateTimeFormatter getShortDateTimeFormatter() { return shortDateTimeFormatter.get(); }
    
    /**
     * Current short locale-specific date/time formatter property.
     * 
     * @return
     *          Current short locale-specific date/time formatter property.
     */
    public ReadOnlyObjectProperty<DateTimeFormatter> shortDateTimeFormatterProperty() {
        return shortDateTimeFormatter.getReadOnlyProperty();
    }
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="fullDateTimeFormatter property">
    
    private final ReadOnlyObjectWrapper<DateTimeFormatter> fullDateTimeFormatter;
    
    /**
     * Gets the current locale-specific formatter for full date/time strings.
     * 
     * @return
     *          The current locale-specific formatter for full date/time strings.
     */
    public DateTimeFormatter getFullDateTimeFormatter() { return fullDateTimeFormatter.get(); }
    
    /**
     * Current full locale-specific date/time formatter property.
     * 
     * @return
     *          Current full locale-specific date/time formatter property.
     */
    public ReadOnlyObjectProperty<DateTimeFormatter> fullDateTimeFormatterProperty() {
        return fullDateTimeFormatter.getReadOnlyProperty();
    }
    
    //</editor-fold>
    
    //</editor-fold>
    
    //<editor-fold defaultstate="collapsed" desc="currentUser property">
    
    private final ReadOnlyObjectWrapper<UserRow> currentUser;

    /**
     * Gets the currently logged in user.
     * 
     * @return
     *          The currently logged in user.
     */
    public UserRow getCurrentUser() { return currentUser.get(); }

    /**
     * Current logged in user property.
     * 
     * @return
     *          Current logged in user property.
     */
    public ReadOnlyObjectProperty<UserRow> currentUserProperty() { return currentUser.getReadOnlyProperty(); }
    
    //</editor-fold>
    
    private static final Logger LOG;
    
    static {
        CURRENT = new ReadOnlyObjectWrapper<>();
        LOG = Logger.getLogger(App.class.getName());
    }
    
    public App() {
        primaryStage = new ReadOnlyObjectWrapper<>();
        allLanguages = new ReadOnlyListWrapper<>();
        // Store the original locale settings so they can be restored when app ends
        originalDisplayLocale = new ReadOnlyObjectWrapper<>(Locale.getDefault(Locale.Category.DISPLAY));
        originalFormatLocale = new ReadOnlyObjectWrapper<>(Locale.getDefault(Locale.Category.FORMAT));
        resources = new ReadOnlyObjectWrapper<>();
        fullTimeFormatter = new ReadOnlyObjectWrapper<>();
        fullDateFormatter = new ReadOnlyObjectWrapper<>();
        shortDateTimeFormatter = new ReadOnlyObjectWrapper<>();
        fullDateTimeFormatter = new ReadOnlyObjectWrapper<>();
        currentUser = new ReadOnlyObjectWrapper<>();
        currentLocale = new SimpleObjectProperty<>();
        appointmentTypes = new ReadOnlyMapWrapper<>(new AppointmentTypes());
        currentLocale.addListener((ObservableValue<? extends Locale> observable, Locale oldValue, Locale newValue) -> {
            // Set default locale so all controls will be displayed appropriate for the current locale.
            Locale.setDefault(Locale.Category.DISPLAY, newValue);
            Locale.setDefault(Locale.Category.FORMAT, newValue);
            resources.set(ResourceBundle.getBundle(GLOBALIZATION_RESOURCE_NAME, newValue));
            fullTimeFormatter.set(DateTimeFormatter.ofLocalizedTime(FormatStyle.FULL).withLocale(newValue).withZone(ZoneId.systemDefault()));
            fullDateFormatter.set(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(newValue).withZone(ZoneId.systemDefault()));
            shortDateTimeFormatter.set(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(newValue).withZone(ZoneId.systemDefault()));
            fullDateTimeFormatter.set(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(newValue).withZone(ZoneId.systemDefault()));
        });
        resources.addListener((ObservableValue<? extends ResourceBundle> observable, ResourceBundle oldValue, ResourceBundle newValue) -> {
            AppointmentTypes map = (AppointmentTypes)appointmentTypes.get();
            map.load(newValue);
        });
    }
    
    //<editor-fold defaultstate="collapsed" desc="App Lifecycle Members">
    
    /**
     * The app main entry point.
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    @SuppressWarnings("UseSpecificCatch")
    public void start(Stage stage) throws Exception {
        CURRENT.set(this);
        primaryStage.set(stage);
        
        // Ensure app config is freshly loaded.
        AppConfig.refresh();
        
        allLanguages.set(new AllLanguages(AppConfig.getLanguages()));
        
        try {
            ResourceBundle rb = ResourceBundle.getBundle(view.Controller.getGlobalizationResourceName(view.login.LoginScene.class), currentLocale.get());
            FXMLLoader loader = new FXMLLoader(view.login.LoginScene.class.getResource(view.Controller.getFXMLResourceName(view.login.LoginScene.class)), rb);
            Scene scene = new Scene(loader.load());
            primaryStage.get().setScene(scene);
            stage.show();
        } catch (Throwable ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
            Util.showErrorAlert(resources.get().getString(RESOURCEKEY_FXMLLOADERERRORTITLE), resources.get().getString(RESOURCEKEY_FXMLLOADERERRORMESSAGE));
        }
    }
    
    @Override
    public void stop() throws Exception {
        SqlConnectionDependency.forceClose();
        // Resotre original locale settings
        Locale.setDefault(Locale.Category.DISPLAY, originalDisplayLocale.get());
        Locale.setDefault(Locale.Category.FORMAT, originalFormatLocale.get());
        super.stop();
    }
    
    //</editor-fold>
    
    /**
     * Sets the currently logged in user if a user name and password match.
     * @param userName The user's login name.
     * @param password The user's actual password (not password hash).
     * @return {@code true} if a matching {@link UserRow#userName} is found, and the password hash matches the stored value; otherwise {@code false}.
     * @throws InvalidOperationException
     * @throws java.sql.SQLException
     */
    public boolean tryLoginUser(String userName, String password) throws InvalidOperationException, SQLException {
        Optional<UserRow> user = SqlConnectionDependency.get((Connection connection) -> {
            try {
                return UserRow.getByUserName(connection, userName);
            } catch (SQLException ex) {
                Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
                throw new RuntimeException("Error getting user by username", ex);
            }
        });
        if (user.isPresent()) {
            // The password string stored in the database is a base-64 string that contains a cryptographic hash of the password
            // along with the cryptographic seed. A hash will be created from password argument using the same cryptographic seed
            // as the stored password. If the password is correct, then the hash values will match.
            if (user.get().getPasswordHash().test(password)) {
                currentUser.set(user.get());
                return true;
            }
        } else
            LOG.log(Level.WARNING, "No matching userName found");
        return false;
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
            final String lt = originalDisplayLocale.get().toLanguageTag();
            // First look for one that is an exact match with the language tag.
            Optional<String> cl = Arrays.stream(languageIds).filter((String id) -> id.equals(lt)).findFirst();
            if (!cl.isPresent()) {
                // Look for one that matches the ISO3 language code.
                final String iso3 = originalDisplayLocale.get().getISO3Language();
                cl = Arrays.stream(languageIds).filter((String id) -> id.equals(iso3)).findFirst();
                if (!cl.isPresent()) {
                    // Look for one that matches the ISO2 language code.
                    final String ln = originalDisplayLocale.get().getLanguage();
                    cl = Arrays.stream(languageIds).filter((String id) -> id.equals(ln)).findFirst();
                }
            }

            // Populate list of Locale objects.
            Locale toSelect;
            if (cl.isPresent()) {
                for (String n : AppConfig.getLanguages())
                    backingList.add((n.equals(cl.get())) ? originalDisplayLocale.get() : new Locale(n));
                toSelect = originalDisplayLocale.get();
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
        public <Locale> Locale[] toArray(Locale[] a) { return backingList.toArray(a); }

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
