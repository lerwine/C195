package scheduler.dao;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author lerwi
 */
public class PropertyChangeNotifiable {

    private transient final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupportImpl();

    protected final PropertyChangeSupport getPropertyChangeSupport() {
        return propertyChangeSupport;
    }

    /**
     * Add PropertyChangeListener.
     *
     * @param listener
     */
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    /**
     * Remove PropertyChangeListener.
     *
     * @param listener
     */
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    protected void onPropertyChange(PropertyChangeEvent event) {
    }

    private class PropertyChangeSupportImpl extends PropertyChangeSupport {

        PropertyChangeSupportImpl() {
            super(PropertyChangeNotifiable.this);
        }

        @Override
        public void firePropertyChange(PropertyChangeEvent event) {
            onPropertyChange(event);
            super.firePropertyChange(event);
        }

        @Override
        public void fireIndexedPropertyChange(String propertyName, int index, Object oldValue, Object newValue) {
            if (null != oldValue || null != newValue) {
                super.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
            }
        }

        @Override
        public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
            if (null != oldValue || null != newValue) {
                super.firePropertyChange(propertyName, oldValue, newValue);
            }
        }

    }
}
