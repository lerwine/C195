package scheduler.model.ui;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder;
import scheduler.dao.DataRowState;
import scheduler.dao.IUserDAO;
import scheduler.dao.UserDAO;
import scheduler.model.UserStatus;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.ToStringPropertyBuilder;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedUser extends RelatedModel<IUserDAO> implements UserItem<IUserDAO> {

    private static final Logger LOG = Logger.getLogger(RelatedUser.class.getName());

    private final ReadOnlyJavaBeanStringProperty userName;
    private final ReadOnlyJavaBeanObjectProperty<UserStatus> status;
    private final ReadOnlyStringBindingProperty statusDisplay;

    public RelatedUser(IUserDAO rowData) {
        super(rowData);
        try {
            userName = ReadOnlyJavaBeanStringPropertyBuilder.create().bean(rowData).name(PROP_USERNAME).build();
            status = ReadOnlyJavaBeanObjectPropertyBuilder.<UserStatus>create().bean(rowData).name(PROP_STATUS).build();
        } catch (NoSuchMethodException ex) {
            LOG.log(Level.SEVERE, "Error creating property", ex);
            throw new RuntimeException(ex);
        }
        statusDisplay = new ReadOnlyStringBindingProperty(this, PROP_STATUSDISPLAY, () -> UserStatus.toDisplayValue(status.get()), status);
    }

    @Override
    public String getUserName() {
        return userName.get();
    }

    @Override
    public ReadOnlyStringProperty userNameProperty() {
        return userName;
    }

    @Override
    public UserStatus getStatus() {
        return status.get();
    }

    @Override
    public ReadOnlyObjectProperty<UserStatus> statusProperty() {
        return status;
    }

    @Override
    public String getStatusDisplay() {
        return statusDisplay.get();
    }

    @Override
    public ReadOnlyStringProperty statusDisplayProperty() {
        return statusDisplay;
    }

    @Override
    public int hashCode() {
        if (getRowState() == DataRowState.NEW) {
            int hash = 7;
            hash = 31 * hash + Objects.hashCode(this.userName);
            hash = 31 * hash + Objects.hashCode(this.status);
            return hash;
        }
        return getPrimaryKey();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (null != obj && obj instanceof UserItem) {
            @SuppressWarnings("unchecked")
            final UserItem<? extends UserDAO> other = (UserItem<? extends UserDAO>) obj;
            if (getRowState() == DataRowState.NEW) {
                return other.getRowState() == DataRowState.NEW && userName.isEqualTo(other.userNameProperty()).get()
                        && status.isEqualTo(other.statusProperty()).get();
            }
            return other.getRowState() != DataRowState.NEW && primaryKeyProperty().isEqualTo(other.primaryKeyProperty()).get();
        }
        return false;
    }

    @Override
    public String toString() {
        return toStringBuilder().build();
    }

    @Override
    public ToStringPropertyBuilder toStringBuilder() {
        return ToStringPropertyBuilder.create(this)
                .addNumber(primaryKeyProperty())
                .addString(userName)
                .addEnum(status);
    }

}
