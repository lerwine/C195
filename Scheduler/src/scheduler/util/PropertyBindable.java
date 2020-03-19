package scheduler.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * Base class for objects that support property binding through {@link PropertyChangeSupport}.
 *
 * @author lerwi
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

    protected <T> void firePropertyChange(String propertyName, T oldValue, T newValue) {
        propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
    }

    /**
     * This gets called when {@link PropertyChangeSupport#firePropertyChange(java.beans.PropertyChangeEvent) is invoked.\
     *
     * @param event The {@code PropertyChangeEvent} that was fired.
     */
    protected void onPropertyChange(PropertyChangeEvent event) {
    }

    private class PropertyChangeSupportImpl extends PropertyChangeSupport {

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
