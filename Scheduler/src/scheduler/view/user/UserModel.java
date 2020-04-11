package scheduler.view.user;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.dao.UserStatus;
import scheduler.observables.UserStatusDisplayProperty;
import scheduler.dao.UserElement;
import scheduler.view.model.ElementModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public interface UserModel<T extends UserElement> extends ElementModel<T> {

    String getUserName();

    ReadOnlyProperty<String> userNameProperty();

    UserStatus getStatus();

    ReadOnlyObjectProperty<UserStatus> statusProperty();

    String getStatusDisplay();

    UserStatusDisplayProperty statusDisplayProperty();
}
