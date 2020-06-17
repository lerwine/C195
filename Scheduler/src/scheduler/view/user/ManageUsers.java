package scheduler.view.user;

import java.io.IOException;
import java.util.Comparator;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.ButtonType;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import scheduler.AppResourceKeys;
import scheduler.AppResources;
import scheduler.Scheduler;
import scheduler.dao.UserDAO;
import scheduler.fx.MainListingControl;
import scheduler.model.User;
import scheduler.model.ui.UserModel;
import scheduler.util.AlertHelper;
import scheduler.util.DbConnector;
import static scheduler.util.NodeUtil.collapseNode;
import static scheduler.util.NodeUtil.restoreNode;
import scheduler.view.MainController;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;
import events.UserEvent;
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
public final class ManageUsers extends MainListingControl<UserDAO, UserModel, UserEvent> {

    private static final Logger LOG = Logger.getLogger(ManageUsers.class.getName());

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
        restoreNode(userFilterBorderPane);
    }

    @FXML
    private void onHelpButtonAction(ActionEvent event) {
        restoreNode(helpBorderPane);
    }

    @FXML
    private void onHelpOKButtonAction(ActionEvent event) {
        collapseNode(helpBorderPane);
    }

    @FXML
    private void onUserFilterCancelButtonAction(ActionEvent event) {
        userFilterBorderPane.setVisible(false);
    }

    @FXML
    private void onUserFilterOKButtonAction(ActionEvent event) {
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
    protected void onEditItem(UserEvent event) {
        try {
            EditUser.edit(event.getModel(), getScene().getWindow());
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Error opening child window", ex);
        }
    }

    @Override
    protected void onDeleteItem(UserEvent event) {
        Optional<ButtonType> response = AlertHelper.showWarningAlert((Stage) getScene().getWindow(), LOG,
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONFIRMDELETE),
                AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_AREYOUSUREDELETE), ButtonType.YES, ButtonType.NO);
        if (response.isPresent() && response.get() == ButtonType.YES) {
            MainController.startBusyTaskNow(new DeleteTask(event));
        }
    }

    @Override
    protected EventType<UserEvent> getInsertedEventType() {
        return UserEvent.INSERTED_EVENT_TYPE;
    }

    @Override
    protected EventType<UserEvent> getUpdatedEventType() {
        return UserEvent.UPDATED_EVENT_TYPE;
    }

    @Override
    protected EventType<UserEvent> getDeletedEventType() {
        return UserEvent.DELETED_EVENT_TYPE;
    }

    /**
     * @todo use implementation of {@link scheduler.dao.DataAccessObject.DeleteTask}
     */
    @Deprecated
    private class DeleteTask extends Task<UserEvent> {

        private final UserEvent event;

        DeleteTask(UserEvent event) {
            updateTitle(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_DELETINGRECORD));
            this.event = event;
        }

        @Override
        protected void cancelled() {
            event.setCanceled();
            super.cancelled();
        }

        @Override
        protected void failed() {
            event.setFaulted("Operation failed", "Operation encountered an unexpected error");
            super.failed();
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            UserEvent e = getValue();
            switch (e.getStatus()) {
                case CANCELED:
                case EVALUATING:
                case SUCCEEDED:
                    break;
                default:
                    AlertHelper.showWarningAlert(getScene().getWindow(), LOG, e.getSummaryTitle(), e.getDetailMessage());
                    break;
            }
        }

        @Override
        protected UserEvent call() throws Exception {
            updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTINGTODB));
            try (DbConnector connector = new DbConnector()) {
                updateMessage(AppResources.getResourceString(AppResourceKeys.RESOURCEKEY_CONNECTEDTODB));
                return UserDAO.FACTORY.delete(event, connector.getConnection());
            }
        }
    }

}
