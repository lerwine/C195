package scheduler.view.user;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import scheduler.model.UserStatus;
import scheduler.observables.UserStatusDisplayProperty;
import scheduler.view.model.ElementModel;
import scheduler.model.db.UserRowData;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @deprecated Use {@link scheduler.model.ui.UserItem}, instead.
 */
public interface UserModel<T extends UserRowData> extends ElementModel<T> {

    String getUserName();

    ReadOnlyProperty<String> userNameProperty();

    UserStatus getStatus();

    ReadOnlyObjectProperty<UserStatus> statusProperty();

    String getStatusDisplay();

    UserStatusDisplayProperty statusDisplayProperty();
}
