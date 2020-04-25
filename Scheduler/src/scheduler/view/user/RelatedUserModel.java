package scheduler.view.user;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.model.UserStatus;
import scheduler.observables.UserStatusDisplayProperty;
import scheduler.observables.UserStatusProperty;
import scheduler.view.model.RelatedItemModel;
import scheduler.model.db.UserRowData;
import scheduler.model.ui.UserItem;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedUserModel extends RelatedItemModel<UserRowData> implements UserItem<UserRowData> {

    private final ReadOnlyStringWrapper userName;
    private final UserStatusProperty status;
    private final UserStatusDisplayProperty statusDisplay;

    public RelatedUserModel(UserRowData dao) {
        super(dao);
        userName = new ReadOnlyStringWrapper(this, "userName", dao.getUserName());
        status = new UserStatusProperty(this, "status", dao.getStatus());
        statusDisplay = new UserStatusDisplayProperty(this, "statusDisplay", status);
    }

    @Override
    public String getUserName() {
        return userName.get();
    }

    @Override
    public ReadOnlyProperty<String> userNameProperty() {
        return userName.getReadOnlyProperty();
    }

    @Override
    public UserStatus getStatus() {
        return status.get();
    }

    @Override
    public ReadOnlyObjectProperty<UserStatus> statusProperty() {
        return status.getReadOnlyProperty();
    }

    @Override
    public String getStatusDisplay() {
        return statusDisplay.get();
    }

    @Override
    public UserStatusDisplayProperty statusDisplayProperty() {
        return statusDisplay;
    }

}
