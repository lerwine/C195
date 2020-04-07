package scheduler.view.user;

import java.io.IOException;
import java.util.logging.Logger;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import scheduler.dao.UserDAO;
import scheduler.view.ListingController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.view.model.ItemModel;
import scheduler.view.MainController;

/**
 * FXML Controller class for viewing a list of {@link UserModelImpl} items.
 *
 * @author Leonard T. Erwine
 */
@GlobalizationResource("scheduler/view/user/ManageUsers")
@FXMLResource("/scheduler/view/user/ManageUsers.fxml")
public final class ManageUsers extends ListingController<UserDAO, UserModelImpl> implements ManageUsersConstants {

    private static final Logger LOG = Logger.getLogger(ManageUsers.class.getName());

    public static ManageUsers loadInto(MainController mainController, Stage stage, UserModelFilter filter,
            Object loadEventListener) throws IOException {
        return loadInto(ManageUsers.class, mainController, stage, filter, loadEventListener);
    }

    public static ManageUsers loadInto(MainController mainController, Stage stage, UserModelFilter filter) throws IOException {
        return loadInto(mainController, stage, filter, null);
    }

    @Override
    protected void onAddNewItem(Event event) throws IOException {
        getMainController().addNewUser((Stage)((Button)event.getSource()).getScene().getWindow());
    }

    @Override
    protected void onEditItem(Event event, UserModelImpl item) throws IOException {
        getMainController().editUser((Stage)((Button)event.getSource()).getScene().getWindow(), item);
    }

    @Override
    protected void onDeleteItem(Event event, UserModelImpl item) {
        getMainController().deleteUser((Stage)((Button)event.getSource()).getScene().getWindow(), item);
    }

    @Override
    protected UserModelImpl toModel(UserDAO dao) {
        return new UserModelImpl(dao);
    }

    @Override
    protected ItemModel.ModelFactory<UserDAO, UserModelImpl> getModelFactory() {
        return UserModelImpl.getFactory();
    }

}
