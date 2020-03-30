package scheduler.view.user;

import java.util.logging.Logger;
import javafx.event.Event;
import scheduler.dao.UserImpl;
import scheduler.view.ListingController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.ItemModel;
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

    @Override
    protected void onAddNewItem(Event event) {
        getMainController().addNewUser(event);
    }

    @Override
    protected void onEditItem(Event event, UserModel item) {
        getMainController().editUser(event, item);
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
    protected ItemModel.ModelFactory<UserImpl, UserModel> getModelFactory() {
        return UserModel.getFactory();
    }

}
