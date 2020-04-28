package scheduler.view.user;

import java.io.IOException;
import javafx.stage.Stage;
import static scheduler.Scheduler.getMainController;
import scheduler.dao.UserDAO;
import scheduler.view.ListingController;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import scheduler.model.ui.FxRecordModel;

/**
 * FXML Controller class for viewing a list of {@link UserModel} items.
 * <p>
 * The associated view is {@code /resources/scheduler/view/user/ManageUsers.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/user/ManageUsers")
@FXMLResource("/scheduler/view/user/ManageUsers.fxml")
public final class ManageUsers extends ListingController<UserDAO, UserModel> {

    public static ManageUsers loadInto(MainController mainController, Stage stage, UserModelFilter filter,
            Object loadEventListener) throws IOException {
        return loadInto(ManageUsers.class, mainController, stage, filter, loadEventListener);
    }

    public static ManageUsers loadInto(MainController mainController, Stage stage, UserModelFilter filter) throws IOException {
        return loadInto(mainController, stage, filter, null);
    }

    @Override
    protected void onAddNewItem(Stage stage) throws IOException {
        getMainController(stage.getScene()).addNewUser(stage);
    }

    @Override
    protected void onEditItem(Stage stage, UserModel item) throws IOException {
        getMainController(stage.getScene()).editUser(stage, item);
    }

    @Override
    protected void onDeleteItem(Stage stage, UserModel item) {
        getMainController(stage.getScene()).deleteUser(stage, item);
    }

    @Override
    protected UserModel toModel(UserDAO dao) {
        return new UserModel(dao);
    }

    @Override
    protected FxRecordModel.ModelFactory<UserDAO, UserModel> getModelFactory() {
        return UserModel.getFactory();
    }

}
