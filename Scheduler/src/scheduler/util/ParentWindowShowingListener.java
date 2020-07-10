package scheduler.util;

import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.value.ObservableValue;
import javafx.stage.Window;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class ParentWindowShowingListener extends ParentWindowChangeListener {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(ParentWindowShowingListener.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(ParentWindowShowingListener.class.getName());

    private final ReadOnlyBooleanWrapper showing;

    public ParentWindowShowingListener() {
        super();
        showing = new ReadOnlyBooleanWrapper(false);
        showing.addListener(this::onShowingChanged);
    }

    public boolean isShowing() {
        return showing.get();
    }

    public ReadOnlyBooleanProperty showingProperty() {
        return showing.getReadOnlyProperty();
    }

    protected void onShowingChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        LOG.fine(() -> String.format("showing changed from %s to %s", oldValue, newValue));
    }

    private void onShowingChangedImpl(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        showing.set(newValue);
    }

    @Override
    protected void onWindowChanged(ObservableValue<? extends Window> observable, Window oldValue, Window newValue) {
        super.onWindowChanged(observable, oldValue, newValue);
        if (null != oldValue) {
            oldValue.showingProperty().removeListener(this::onShowingChangedImpl);
        }
        if (null != newValue) {
            newValue.showingProperty().addListener(this::onShowingChangedImpl);
            showing.set(newValue.isShowing());
        } else {
            showing.set(false);
        }
    }

}
