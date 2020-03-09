package scheduler.view.user;

import java.io.IOException;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.stage.Stage;
import scheduler.dao.UserImpl;
import scheduler.view.EditItem;
import scheduler.view.ListingController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.dao.ModelFilter;
import scheduler.dao.UserFilter;
import scheduler.util.ItemEvent;
import scheduler.util.ItemEventManager;
import scheduler.view.MainController;

/**
 * FXML Controller class for viewing a list of {@link UserModel} items. This is loaded as content of {@link MainController} using
 * {@link #setContent(scheduler.view.MainController, javafx.stage.Stage, scheduler.dao.UserFilter)}.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/user/ManageUsers")
@FXMLResource("/scheduler/view/user/ManageUsers.fxml")
public final class ManageUsers extends ListingController<UserImpl, UserModel> implements ManageUsersConstants {

    private static final Logger LOG = Logger.getLogger(ManageUsers.class.getName());

    /**
     * Loads {@link UserModel} listing view and controller into the {@link MainController}.
     *
     * @param mainController The {@link MainController} to contain the {@link UserModel} listing.
     * @param stage The {@link Stage} for the view associated with the current main controller.
     * @param filter The {@link ModelFilter} to use for loading and filtering {@link UserModel} items.
     * @throws IOException if unable to load the view.
     */
    public static void setContent(MainController mainController, Stage stage, UserFilter filter) throws IOException {
        setContent(mainController, ManageUsers.class, stage, filter);
    }

    @Override
    protected void onAddNewItem(Event event) {
        getMainController().addNewUser(event);
    }

    @Override
    protected EditItem.ShowAndWaitResult<UserModel> onEditItem(Event event, UserModel item) {
        return getMainController().editUser(event, item);
    }

    @Override
    protected void onDeleteItem(Event event, UserModel item) {
        getMainController().deleteUser(event, item);
    }

    @Override
    protected UserModel toModel(UserImpl dao) {
        return new UserModel(dao);
    }

    @Override
    protected UserImpl.FactoryImpl getDaoFactory() {
        return UserImpl.getFactory();
    }

    @Override
    protected ItemEventManager<ItemEvent<UserModel>> getItemAddManager() {
        return getMainController().getUserAddManager();
    }

    @Override
    protected ItemEventManager<ItemEvent<UserModel>> getItemRemoveManager() {
        return getMainController().getUserRemoveManager();
    }

}
