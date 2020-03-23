package scheduler.view.user;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.User;
import scheduler.dao.UserStatus;
import scheduler.observables.UserStatusDisplayProperty;
import scheduler.view.DataObjectReferenceModel;

/**
 *
 * @author lerwi
 * @param <T>
 */
public interface UserReferenceModel<T extends User> extends DataObjectReferenceModel<T> {

    String getUserName();

    ReadOnlyProperty<String> userNameProperty();

    String getPassword();

    ReadOnlyProperty<String> passwordProperty();

    UserStatus getStatus();

    ReadOnlyObjectProperty<UserStatus> statusProperty();

    String getStatusDisplay();

    UserStatusDisplayProperty statusDisplayProperty();
}
