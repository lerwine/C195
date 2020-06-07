package scheduler.model.ui;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanObjectPropertyBuilder;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringProperty;
import javafx.beans.property.adapter.ReadOnlyJavaBeanStringPropertyBuilder;
import scheduler.dao.IUserDAO;
import scheduler.model.UserStatus;
import scheduler.observables.property.ReadOnlyBooleanBindingProperty;
import scheduler.observables.property.ReadOnlyStringBindingProperty;
import scheduler.util.Values;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class RelatedUser extends RelatedModel<IUserDAO> implements UserItem<IUserDAO> {

    private static final Logger LOG = Logger.getLogger(RelatedUser.class.getName());

    private final ReadOnlyJavaBeanStringProperty userName;
    private final ReadOnlyJavaBeanObjectProperty<UserStatus> status;
    private final ReadOnlyStringBindingProperty statusDisplay;
    private final ReadOnlyBooleanBindingProperty valid;

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
        valid = new ReadOnlyBooleanBindingProperty(this, PROP_VALID,
                Bindings.createBooleanBinding(() -> Values.isNotNullWhiteSpaceOrEmpty(userName.get()), userName)
                        .and(status.isNull()));
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
    public boolean isValid() {
        return valid.get();
    }

    @Override
    public ReadOnlyBooleanProperty validProperty() {
        return valid;
    }
}
