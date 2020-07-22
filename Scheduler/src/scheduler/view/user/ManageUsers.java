package scheduler.view.user;

import java.io.IOException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.UserDAO;
import scheduler.events.ModelFailedEvent;
import scheduler.events.UserEvent;
import scheduler.events.UserFailedEvent;
import scheduler.events.UserSuccessEvent;
import scheduler.model.ModelHelper.UserHelper;
import scheduler.model.fx.UserModel;
import scheduler.util.AlertHelper;
import scheduler.util.LogHelper;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.MainController;
import scheduler.view.MainListingControl;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.user.ManageUsersResourceKeys.*;

/**
 * FXML Controller class for viewing a list of {@link UserModel} items.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/user/ManageUsers")
@FXMLResource("/scheduler/view/user/ManageUsers.fxml")
public final class ManageUsers extends MainListingControl<UserDAO, UserModel> {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ManageUsers.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(ManageUsers.class.getName());

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
    private void filterButtonClick(ActionEvent event) {
        LOG.entering(LOG.getName(), "filterButtonClick", event);
        restoreNode(userFilterBorderPane);
    }

    @FXML
    private void onHelpButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onHelpButtonAction", event);
        restoreNode(helpBorderPane);
    }

    @FXML
    private void onHelpOKButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onHelpOKButtonAction", event);
        collapseNode(helpBorderPane);
    }

    @FXML
    private void onUserFilterCancelButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onUserFilterCancelButtonAction", event);
        userFilterBorderPane.setVisible(false);
    }

    @FXML
    private void onUserFilterOKButtonAction(ActionEvent event) {
        LOG.entering(LOG.getName(), "onUserFilterOKButtonAction", event);
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
        return UserHelper::compare;
    }

    @Override
    protected UserModel.Factory getModelFactory() {
        return UserModel.FACTORY;
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
        try {
            EditUser.editNew(getScene().getWindow(), false);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @Override
    protected void onEditItem(UserModel model) {
        try {
            Window w = getScene().getWindow();
            EditUser.edit(model, w);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @Override
    protected void onDeleteItem(UserModel item) {
        AlertHelper.showWarningAlert(getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO).ifPresent((response) -> {
            if (response == ButtonType.YES) {
                UserDAO.DeleteTask task = new UserDAO.DeleteTask(item, false);
                task.setOnSucceeded((event) -> {
                    UserEvent userEvent = (UserEvent) task.getValue();
                    if (userEvent instanceof UserFailedEvent) {
                        scheduler.util.AlertHelper.showWarningAlert(getScene().getWindow(), "Delete Failure",
                                ((ModelFailedEvent<UserDAO, UserModel>) userEvent).getMessage(), ButtonType.OK);
                    }
                });
                MainController.startBusyTaskNow(task);
            }
        });
    }

    @Override
    protected EventType<UserSuccessEvent> getInsertedEventType() {
        return UserSuccessEvent.INSERT_SUCCESS;
    }

    @Override
    protected EventType<UserSuccessEvent> getUpdatedEventType() {
        return UserSuccessEvent.UPDATE_SUCCESS;
    }

    @Override
    protected EventType<UserSuccessEvent> getDeletedEventType() {
        return UserSuccessEvent.DELETE_SUCCESS;
    }

}
