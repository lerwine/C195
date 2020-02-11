package scheduler.view.user;

import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.dao.AppointmentFactory;
import scheduler.dao.UserFactory;
import scheduler.filter.ModelFilter;
import scheduler.util.Alerts;
import scheduler.view.CrudAction;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.TaskWaiter;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * FXML Controller class
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/user/ManageUsers")
@FXMLResource("/scheduler/view/user/ManageUsers.fxml")
public class ManageUsers extends ListingController<UserModel> {
    
    private static final Logger LOG = Logger.getLogger(ManageUsers.class.getName());
    
    //<editor-fold defaultstate="collapsed" desc="Resource bundle keys">
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Edit"}.
     */
    public static final String RESOURCEKEY_EDIT = "edit";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Database Access Error"}.
     */
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Delete"}.
     */
    public static final String RESOURCEKEY_DELETE = "delete";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Created On"}.
     */
    public static final String RESOURCEKEY_CREATEDON = "createdOn";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Created By"}.
     */
    public static final String RESOURCEKEY_CREATEDBY = "createdBy";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Updated On"}.
     */
    public static final String RESOURCEKEY_UPDATEDON = "updatedOn";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Updated By"}.
     */
    public static final String RESOURCEKEY_UPDATEDBY = "updatedBy";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "New"}.
     */
    public static final String RESOURCEKEY_NEW = "new";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Manage Users"}.
     */
    public static final String RESOURCEKEY_MANAGEUSERS = "manageUsers";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Error loading users..."}.
     */
    public static final String RESOURCEKEY_ERRORLOADINGUSERS = "errorLoadingUsers";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Name"}.
     */
    public static final String RESOURCEKEY_NAME = "name";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Status"}.
     */
    public static final String RESOURCEKEY_STATUS = "status";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "Loading Users"}.
     */
    public static final String RESOURCEKEY_LOADINGUSERS = "loadingUsers";
    
    /**
     * Resource key in the current {@link java.util.ResourceBundle} that contains the text for {@code "That user is referenced in one or more appointments..."}.
     */
    public static final String RESOURCEKEY_USERHASAPPOINTMENTS = "userHasAppointments";

    //</editor-fold>
    public static void loadInto(MainController mc, Stage stage, ModelFilter<UserModel> filter) throws IOException {
        loadInto(ManageUsers.class, mc, stage, filter);
    }
    
    @Override
    protected void onAddNewItem(Event event) {
        getMainController().addNewUser(event);
    }

    @Override
    protected CrudAction<UserModel> onEditItem(Event event, UserModel item) {
        return getMainController().editUser(event, item);
    }

    @Override
    protected void onDeleteItem(Event event, UserModel item) {
        getMainController().deleteUser(event, item, (connection) -> {
            AppointmentFactory factory = new AppointmentFactory();
            if (factory.count(connection, AppointmentFactory.userIdIs(item.getDataObject().getPrimaryKey())) == 0)
                return "";
            return getResourceString(RESOURCEKEY_USERHASAPPOINTMENTS);
            
        });
    }

    @Override
    protected void onFilterChanged(Stage owner) {
        TaskWaiter.execute(new UsersLoadTask(owner));
    }
    
    private class UsersLoadTask extends ItemsLoadTask<UserFactory.UserImpl> {
        UsersLoadTask(Stage owner) {
            super(owner, getResourceString(RESOURCEKEY_LOADINGUSERS));
        }

        @Override
        protected void processNullResult(Window owner) {
            LOG.log(Level.SEVERE, String.format("\"%s\" operation returned null", getTitle()));
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGUSERS));
        }

        @Override
        protected UserModel toModel(UserFactory.UserImpl result) { return new UserModel(result); }

        @Override
        protected Iterable<UserFactory.UserImpl> getResult(Connection connection, ModelFilter<UserModel> filter) throws Exception {
            return (new UserFactory()).load(connection, filter);
        }
        
        @Override
        protected void processException(Throwable ex, Window owner) {
            super.processException(ex, owner);
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGUSERS));
        }
        
    }

}
