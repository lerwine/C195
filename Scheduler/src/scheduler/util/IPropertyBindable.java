package scheduler.util;

import java.beans.PropertyChangeListener;

/**
 * Interface for providing property binding through {@link java.beans.PropertyChangeSupport}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface IPropertyBindable {

    /**
     * Add a {@link PropertyChangeListener} to the listener list. The listener is registered for all properties. The same listener object may be added
     * more than once, and will be called as many times as it is added. If {@code listener} is null, no exception is thrown and no action is taken.
     *
     * @param listener The {@link PropertyChangeListener} to be added.
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Remove a {@link PropertyChangeListener} from the listener list. This removes a {@link PropertyChangeListener} that was registered for all
     * properties. If {@code listener} was added more than once to the same event source, it will be notified one less time after being removed. If
     * {@code listener} is null, or was never added, no exception is thrown and no action is taken.
     *
     * @param listener The {@link PropertyChangeListener} to be removed.
     */
    void removePropertyChangeListener(PropertyChangeListener listener);
}
