package scheduler.util;

import java.beans.IndexedPropertyChangeEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.lang.Thread.UncaughtExceptionHandler;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;

/**
 * Base class for objects that support property binding through {@link PropertyChangeSupport}. This only actually fires events when the old and new
 * values are different.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class PropertyBindable implements IPropertyBindable {

    private static final Logger LOG = Logger.getLogger(PropertyBindable.class.getName());

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
        if (!Objects.equals(oldValue, newValue)) {
            propertyChangeSupport.fireIndexedPropertyChange(propertyName, index, oldValue, newValue);
        }
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
        if ((null == oldValue) ? null != newValue : !oldValue.equals(newValue)) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected void firePropertyChange(String propertyName, Timestamp oldValue, Timestamp newValue) {
        if ((null == oldValue) ? null != newValue : !oldValue.equals(newValue)) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected <T extends Enum<T>> void firePropertyChange(String propertyName, T oldValue, T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    protected <T> void firePropertyChange(String propertyName, T oldValue, T newValue) {
        if (!Objects.equals(oldValue, newValue)) {
            propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
        }
    }

    /**
     * This gets called when {@link PropertyChangeSupport#firePropertyChange(java.beans.PropertyChangeEvent)} is invoked.
     *
     * @param event The {@code PropertyChangeEvent} that was fired.
     * @throws java.lang.Exception Allows implementing classes to throw any exception, which will be caught by this class.
     */
    protected void onPropertyChange(PropertyChangeEvent event) throws Exception {
    }

    /**
     * Defers the firing of {@link PropertyChangeEvent}s until {@link #endChange()} is called.
     */
    protected void beginChange() {
        propertyChangeSupport.beginChange();
    }

    /**
     * Resumes the firing of {@link PropertyChangeEvent}s, immediately firing all deferred {@link PropertyChangeEvent}s. The new and old values of
     * changes are tracked, and only one event will be fired for each property. If a subsequent event is deferred for the same property, the original
     * {@link PropertyChangeEvent#oldValue} is retained by making a copy of the latest event with the original old value. If the final old and new
     * values are not different, then the event for that property is not fired.
     */
    protected void endChange() {
        propertyChangeSupport.endChange();
    }

    private class PropertyChangeSupportImpl extends PropertyChangeSupport {

        private static final long serialVersionUID = -5190875010028850398L;

        private int changing = 0;
        private final ArrayList<PropertyChangeEvent> orderedDeferredChanges = new ArrayList<>();
        private final HashMap<String, Integer> deferredChangeMap = new HashMap<>();
        private final HashMap<String, HashMap<Integer, Integer>> deferredIndexedChangeMap = new HashMap<>();
        private int noNameChange = -1;
        private final HashMap<Integer, Integer> noNameIndexedChange = new HashMap<>();

        PropertyChangeSupportImpl() {
            super(PropertyBindable.this);
        }

        protected synchronized final void beginChange() {
            changing++;
        }

        protected final void endChange() {
            PropertyChangeEvent[] changes = onEndChange();
            if (null != changes) {
                for (PropertyChangeEvent c : changes) {
                    firePropertyChangeImpl(c);
                }
            }
        }

        private synchronized PropertyChangeEvent[] onEndChange() {
            if (changing == 1) {
                changing = 0;
                if (!orderedDeferredChanges.isEmpty()) {
                    PropertyChangeEvent[] changes = orderedDeferredChanges.toArray(new PropertyChangeEvent[orderedDeferredChanges.size()]);
                    orderedDeferredChanges.clear();
                    deferredChangeMap.clear();
                    deferredIndexedChangeMap.clear();
                    noNameIndexedChange.clear();
                    noNameChange = -1;
                    return changes;
                }
            } else if (changing > 1) {
                changing--;
            }
            return null;
        }

        // We are handling any exceptions on purpose.
        @SuppressWarnings("UseSpecificCatch")
        private void firePropertyChangeImpl(PropertyChangeEvent event) {
            try {
                onPropertyChange(event);
            } catch (Throwable ex) {
                LOG.log(Level.SEVERE, String.format("Uncaught exception in implementing class handling %s property change event",
                        event.getPropertyName()), ex);
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), ex);
            } finally {
                // Temporarily replace exception handler so we can log any exceptions thrown, that might otherwise be untracked.
                UncaughtExceptionHandler eh = Thread.currentThread().getUncaughtExceptionHandler();
                Thread.currentThread().setUncaughtExceptionHandler((Thread t, Throwable e) -> {
                    LOG.log(Level.SEVERE, String.format("Uncaught exception firing %s property change event",
                            event.getPropertyName()), e);
                    eh.uncaughtException(t, e);
                });
                if (Platform.isFxApplicationThread()) {
                    try {
                        super.firePropertyChange(event);
                    } catch (Throwable ex) {
                        Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), ex);
                    } finally {
                        Thread.currentThread().setUncaughtExceptionHandler(eh);
                    }
                } else {
                    Platform.runLater(() -> {
                        try {
                            super.firePropertyChange(event);
                        } catch (Throwable ex) {
                            Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), ex);
                        } finally {
                            Thread.currentThread().setUncaughtExceptionHandler(eh);
                        }
                    });
                }
            }
        }

        private void setIndexedChange(HashMap<Integer, Integer> map, IndexedPropertyChangeEvent event) {
            int propertyIndex = event.getIndex();
            if (!map.containsKey(propertyIndex)) {
                map.put(propertyIndex, orderedDeferredChanges.size());
                orderedDeferredChanges.add(event);
                return;
            }
            int deferredIndex = map.get(propertyIndex);
            Object oldValue = (orderedDeferredChanges.get(deferredIndex)).getOldValue();
            if (!Objects.equals(oldValue, event.getOldValue())) {
                Object propagationId = event.getPropagationId();
                event = new IndexedPropertyChangeEvent(event.getSource(), event.getPropertyName(), oldValue,
                        event.getNewValue(), propertyIndex);
                event.setPropagationId(propagationId);
            }
            orderedDeferredChanges.set(deferredIndex, event);
        }

        private synchronized boolean checkFirePropertyChange(PropertyChangeEvent event) {
            if (changing == 0) {
                return true;
            }
            String propertyName = event.getPropertyName();
            orderedDeferredChanges.add(event);
            if (event instanceof IndexedPropertyChangeEvent) {
                if (null == propertyName) {
                    setIndexedChange(noNameIndexedChange, (IndexedPropertyChangeEvent) event);
                } else {
                    HashMap<Integer, Integer> map;
                    if (deferredIndexedChangeMap.containsKey(propertyName)) {
                        map = deferredIndexedChangeMap.get(propertyName);
                    } else {
                        map = new HashMap<>();
                        deferredIndexedChangeMap.put(propertyName, map);
                    }
                    setIndexedChange(map, (IndexedPropertyChangeEvent) event);
                }
                return false;
            }

            int deferredIndex;
            if (null == propertyName) {
                if ((deferredIndex = noNameChange) < 0) {
                    noNameChange = orderedDeferredChanges.size();
                    orderedDeferredChanges.add(event);
                    return false;
                }
            } else if (deferredChangeMap.containsKey(propertyName)) {
                deferredIndex = deferredChangeMap.get(propertyName);
            } else {
                deferredChangeMap.put(propertyName, orderedDeferredChanges.size());
                orderedDeferredChanges.add(event);
                return false;
            }
            Object oldValue = (orderedDeferredChanges.get(deferredIndex)).getOldValue();
            if (!Objects.equals(oldValue, event.getOldValue())) {
                Object propagationId = event.getPropagationId();
                event = new PropertyChangeEvent(event.getSource(), event.getPropertyName(), oldValue, event.getNewValue());
                event.setPropagationId(propagationId);
            }
            orderedDeferredChanges.set(deferredIndex, event);
            return false;
        }

        @Override
        public void firePropertyChange(PropertyChangeEvent event) {
            if (checkFirePropertyChange(event)) {
                firePropertyChangeImpl(event);
            }
        }

    }
}
