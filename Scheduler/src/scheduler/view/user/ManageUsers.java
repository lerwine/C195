package scheduler.view.user;

import java.util.Comparator;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import scheduler.Scheduler;
import static scheduler.Scheduler.getMainController;
import scheduler.dao.UserDAO;
import scheduler.dao.event.UserDaoEvent;
import scheduler.fx.MainListingControl;
import scheduler.model.User;
import scheduler.model.ui.UserModel;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.user.ManageUsersResourceKeys.*;

/**
 * FXML Controller class for viewing a list of {@link UserModel} items.
 * <p>
 * The associated view is {@code /resources/scheduler/view/user/ManageUsers.fxml}.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/user/ManageUsers")
@FXMLResource("/scheduler/view/user/ManageUsers.fxml")
public final class ManageUsers extends MainListingControl<UserDAO, UserModel, UserDaoEvent> {

    public static ManageUsers loadIntoMainContent(UserModelFilter filter) {
        ManageUsers newContent = new ManageUsers();
        Scheduler.getMainController().replaceContent(newContent);
        newContent.setFilter(filter);
        return newContent;
    }

    @FXML // fx:id="userFilterBorderPane"
    private BorderPane userFilterBorderPane; // Value injected by FXMLLoader

    @FXML // fx:id="activeUsersRadioButton"
    private RadioButton activeUsersRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="userFilterToggleGroup"
    private ToggleGroup userFilterToggleGroup; // Value injected by FXMLLoader

    @FXML // fx:id="inactiveUsersRadioButton"
    private RadioButton inactiveUsersRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="allUsersRadioButton"
    private RadioButton allUsersRadioButton; // Value injected by FXMLLoader

    @FXML // fx:id="helpBorderPane"
    private BorderPane helpBorderPane; // Value injected by FXMLLoader

    @FXML
    void filterButtonClick(ActionEvent event) {
        restoreNode(userFilterBorderPane);
    }

    @FXML
    void onHelpButtonAction(ActionEvent event) {
        restoreNode(helpBorderPane);
    }

    @FXML
    void onHelpOKButtonAction(ActionEvent event) {
        collapseNode(helpBorderPane);
    }

    @FXML
    void onUserFilterCancelButtonAction(ActionEvent event) {
        userFilterBorderPane.setVisible(false);
    }

    @FXML
    void onUserFilterOKButtonAction(ActionEvent event) {
        if (inactiveUsersRadioButton.isSelected()) {
            setFilter(UserModelFilter.inactive());
        } else if (allUsersRadioButton.isSelected()) {
            setFilter(UserModelFilter.all());
        } else {
            setFilter(UserModelFilter.active());
        }
        collapseNode(userFilterBorderPane);
    }

    @Override
    protected void initialize() {
        super.initialize();
        assert userFilterBorderPane != null : "fx:id=\"userFilterBorderPane\" was not injected: check your FXML file 'ManageUsers.fxml'.";
        assert activeUsersRadioButton != null : "fx:id=\"activeUsersRadioButton\" was not injected: check your FXML file 'ManageUsers.fxml'.";
        assert userFilterToggleGroup != null : "fx:id=\"userFilterToggleGroup\" was not injected: check your FXML file 'ManageUsers.fxml'.";
        assert inactiveUsersRadioButton != null : "fx:id=\"inactiveUsersRadioButton\" was not injected: check your FXML file 'ManageUsers.fxml'.";
        assert allUsersRadioButton != null : "fx:id=\"allUsersRadioButton\" was not injected: check your FXML file 'ManageUsers.fxml'.";
        assert helpBorderPane != null : "fx:id=\"helpBorderPane\" was not injected: check your FXML file 'ManageUsers.fxml'.";

    }

    @Override
    protected Comparator<? super UserDAO> getComparator() {
        return User::compare;
    }

    @Override
    protected UserModel.Factory getModelFactory() {
        return UserModel.getFactory();
    }

    @Override
    protected String getLoadingTitle() {
        return getResources().getString(RESOURCEKEY_LOADINGUSERS);
    }

    @Override
    protected String getFailMessage() {
        return getResources().getString(RESOURCEKEY_ERRORLOADINGUSERS);
    }

    @Override
    protected void onNewItem() {
        getMainController().addNewUser(getScene().getWindow(), true);
    }

    @Override
    protected void onEditItem(UserModel item) {
        getMainController().editUser(item, getScene().getWindow());
    }

    @Override
    protected void onDeleteItem(UserModel item) {
        getMainController().deleteUser(item, null);
    }

    @Override
    protected EventType<UserDaoEvent> getInsertedEventType() {
        return UserDaoEvent.USER_DAO_INSERT;
    }

    @Override
    protected EventType<UserDaoEvent> getUpdatedEventType() {
        return UserDaoEvent.USER_DAO_UPDATE;
    }

    @Override
    protected EventType<UserDaoEvent> getDeletedEventType() {
        return UserDaoEvent.USER_DAO_DELETE;
    }

}
