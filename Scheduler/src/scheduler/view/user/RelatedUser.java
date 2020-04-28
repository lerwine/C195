package scheduler.view.user;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import scheduler.dao.DataRowState;
import scheduler.model.ModelHelper;
import scheduler.model.UserStatus;
import scheduler.model.db.UserRowData;
import scheduler.model.ui.UserItem;
import scheduler.observables.RowStateProperty;
import scheduler.observables.UserStatusDisplayProperty;
import scheduler.observables.UserStatusProperty;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedUser implements UserItem<UserRowData> {

    private final ReadOnlyObjectWrapper<UserRowData> dataObject;
    private final ReadOnlyIntegerWrapper primaryKey;
    private final RowStateProperty rowState;
    private final ReadOnlyStringWrapper userName;
    private final UserStatusProperty status;
    private final UserStatusDisplayProperty statusDisplay;

    public RelatedUser(UserRowData rowData) {
        primaryKey = new ReadOnlyIntegerWrapper(this, "primaryKey", rowData.getPrimaryKey());
        dataObject = new ReadOnlyObjectWrapper<>(this, "dataObject", rowData);
        rowState = new RowStateProperty(this, "rowState", ModelHelper.getRowState(rowData));
        userName = new ReadOnlyStringWrapper(this, "userName", rowData.getUserName());
        status = new UserStatusProperty(this, "status", rowData.getStatus());
        statusDisplay = new UserStatusDisplayProperty(this, "statusDisplay", status);
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
        return statusDisplay.get();
    }

    @Override
    public UserStatusDisplayProperty statusDisplayProperty() {
        return statusDisplay;
    }

    @Override
    public int getPrimaryKey() {
        return primaryKey.get();
    }

    @Override
    public ReadOnlyIntegerProperty primaryKeyProperty() {
        return primaryKey.getReadOnlyProperty();
    }

    @Override
    public UserRowData getDataObject() {
        return dataObject.get();
    }

    @Override
    public ReadOnlyObjectProperty<UserRowData> dataObjectProperty() {
        return dataObject.getReadOnlyProperty();
    }

    @Override
    public DataRowState getRowState() {
        return rowState.get();
    }

    @Override
    public ReadOnlyProperty<? extends DataRowState> rowStateProperty() {
        return rowState.getReadOnlyProperty();
    }

}
