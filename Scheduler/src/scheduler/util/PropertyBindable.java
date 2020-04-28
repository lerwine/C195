package scheduler.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.sql.Timestamp;
import scheduler.model.DataModel;

/**
 * Base class for objects that support property binding through {@link PropertyChangeSupport}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PropertyBindable implements IPropertyBindable {

    private transient final PropertyChangeSupportImpl propertyChangeSupport = new PropertyChangeSupportImpl();

    /**
     * Gets the {@link PropertyChangeSupport} object for supporting bound properties.
     *
     * @return The {@link PropertyChangeSupport} object for supporting bound properties.
     */
    protected final PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected <T> void fireIndexedPropertyChange(String propertyName, int index, T oldValue, T newValue) {
        propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
    }

    protected void firePropertyChange(String propertyName, int oldValue, int newValue) {
        if (newValue != oldValue) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        if (newValue != oldValue) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String propertyName, String oldValue, String newValue) {
        if ((null == oldValue) ? null != newValue : oldValue.equals(newValue)) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String propertyName, Timestamp oldValue, Timestamp newValue) {
        if ((null == oldValue) ? null != newValue : oldValue.equals(newValue)) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected <T extends Enum<T>> void firePropertyChange(String propertyName, T oldValue, T newValue) {
        if ((null == oldValue) ? null != newValue : oldValue == newValue) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected <T extends DataModel> void firePropertyChange(String propertyName, T oldValue, T newValue) {
        if ((null == oldValue) ? null != newValue : oldValue.equals(newValue)) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * This gets called when {@link PropertyChangeSupport#firePropertyChange(java.beans.PropertyChangeEvent)} is invoked.
     *
     * @param event The {@code PropertyChangeEvent} that was fired.
     */
    protected void onPropertyChange(PropertyChangeEvent event) {
    }

    private class PropertyChangeSupportImpl extends PropertyChangeSupport {

        /**
         *
         */
        private static final long serialVersionUID = -5190875010028850398L;

        PropertyChangeSupportImpl() {
            super(PropertyBindable.this);
        }

        @Override
        public void firePropertyChange(PropertyChangeEvent event) {
            onPropertyChange(event);
            super.firePropertyChange(event);
        }

    }
}
