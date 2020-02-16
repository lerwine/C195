package scheduler.view.user;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.User;
import scheduler.observables.UserStatusDisplayProperty;
import scheduler.view.DataObjectReferenceModel;

/**
 *
 * @author lerwi
 */
public interface UserReferenceModel<T extends User> extends DataObjectReferenceModel<T> {
    String getUserName();
    ReadOnlyProperty<String> userNameProperty();
    String getPassword();
    ReadOnlyProperty<String> passwordProperty();
    int getStatus();
    ReadOnlyIntegerProperty statusProperty();
    String getStatusDisplay();
    UserStatusDisplayProperty statusDisplayProperty();
}
