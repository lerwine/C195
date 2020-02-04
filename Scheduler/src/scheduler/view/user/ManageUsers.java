package scheduler.view.user;

import java.io.IOException;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.stage.Stage;
import javafx.stage.Window;
import scheduler.dao.UserFactory;
import scheduler.dao.UserImpl;
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
    
    //<editor-fold defaultstate="collapsed" desc="Resource keys">

    public static final String RESOURCEKEY_MANAGEUSERS = "manageUsers";
    public static final String RESOURCEKEY_LOADINGUSERS = "loadingUsers";
    public static final String RESOURCEKEY_ERRORLOADINGUSERS = "errorLoadingUsers";
    public static final String RESOURCEKEY_DBACCESSERROR = "dbAccessError";
    
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
        getMainController().deleteUser(event, item);
    }

    @Override
    protected void onFilterChanged(Stage owner) {
        TaskWaiter.execute(new UsersLoadTask(owner));
    }
    
    private class UsersLoadTask extends ItemsLoadTask<UserImpl> {
        UsersLoadTask(Stage owner) {
            super(owner, getResourceString(RESOURCEKEY_LOADINGUSERS));
        }

        @Override
        protected void processNullResult(Window owner) {
            LOG.log(Level.SEVERE, String.format("\"%s\" operation returned null", getTitle()));
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGUSERS));
        }

        @Override
        protected UserModel toModel(UserImpl result) { return new UserModel(result); }

        @Override
        protected Iterable<UserImpl> getResult(Connection connection, ModelFilter<UserModel> filter) throws Exception {
            return (new UserFactory()).load(connection, filter);
        }
        
        @Override
        protected void processException(Throwable ex, Window owner) {
            super.processException(ex, owner);
            Alerts.showErrorAlert(getResourceString(RESOURCEKEY_DBACCESSERROR), getResourceString(RESOURCEKEY_ERRORLOADINGUSERS));
        }
        
    }

}
