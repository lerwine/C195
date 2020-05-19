package scheduler.view.user;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.IUserDAO;
import scheduler.dao.UserDAO;
import scheduler.model.RelatedModel;
import scheduler.model.UserStatus;
import scheduler.model.ui.UserItem;
import scheduler.observables.UserStatusProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedUser extends RelatedModel<IUserDAO> implements UserItem<IUserDAO> {

    private final ReadOnlyStringWrapper userName;
    private final UserStatusProperty status;

    public RelatedUser(IUserDAO rowData) {
        super(rowData);
        userName = new ReadOnlyStringWrapper(this, "userName", rowData.getUserName());
        status = new UserStatusProperty(this, "status", rowData.getStatus());
    }

    @Override
    public String getUserName() {
        return userName.get();
    }

    @Override
    public ReadOnlyStringProperty userNameProperty() {
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
        return status.getDisplayText();
    }

    @Override
    public ReadOnlyStringProperty statusDisplayProperty() {
        return status.displayTextProperty();
    }

    @Override
    protected void onDataObjectPropertyChanged(IUserDAO dao, String propertyName) {
        switch (propertyName) {
            case UserDAO.PROP_USERNAME:
                userName.set(dao.getUserName());
                break;
            case UserDAO.PROP_STATUS:
                status.set(dao.getStatus());
                break;
        }
    }

}
