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
 * Base class for objects that support property binding through {@link PropertyChangeSupport} and reduces the risk of race conditions when used with
 * the JavaFX API. This only actually fires events when the old and new values are different, and fires all {@link PropertyChangeEvent}s on the JavaFX
 * Application Thread.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class PropertyBindable implements IPropertyBindable {

    private static final Logger LOG = Logger.getLogger(PropertyBindable.class.getName());

    private transient final PropertyChangeSupportImpl propertyChangeSupport = new PropertyChangeSupportImpl();

    /**
     * Gets the {@link PropertyChangeSupport} object for supporting bound properties. The {@link PropertyChangeSupport} for this class fires all
     * {@link PropertyChangeEvent}s on the JavaFX Application Thread.
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

    protected boolean arePropertyChangeEventsDeferred() {
        return null != propertyChangeSupport.first;
    }

    /**
     * This gets called just before a {@link PropertyChangeEvent} is fired for {@link PropertyChangeSupport}.
     * <p>
     * If {@link PropertyChangeEvent}s are being deferred, this will be called for each deferred {@link PropertyChangeEvent} once they are no longer
     * being deferred. In this case, this will be called on the same thread that the {@link ChangeEventDeferral#close()} method was called.</p>
     * <p>
     * Otherwise, if {@link PropertyChangeEvent}s are not being deferred, this will be called immediately after
     * {@link PropertyChangeSupport#firePropertyChange(PropertyChangeEvent)} is called, and on the same thread.</p>
     *
     * @param event The {@code PropertyChangeEvent} that is being fired.
     * @throws Exception Allows implementing classes to throw any exception, which will be caught by the {@link Thread#uncaughtExceptionHandler}.
     */
    protected void onPropertyChange(PropertyChangeEvent event) throws Exception {
    }

    /**
     * Creates a {@link ChangeEventDeferral} object that defers {@link PropertyChangeEvent} firing on the current {@code PropertyBindable} object.
     * Deferred {@link PropertyChangeEvent}s will be fired and subsequent {@link PropertyChangeEvent} firing will resume when all
     * {@link ChangeEventDeferral}s for the current {@code PropertyBindable} object have been closed.
     *
     * @return A {@link ChangeEventDeferral} object that defers {@link PropertyChangeEvent} firing on the current {@code PropertyBindable} object.
     */
    protected ChangeEventDeferral deferChangeEvents() {
        ChangeEventDeferral result = new ChangeEventDeferral();
        result.open(this);
        return result;
    }

    /**
     * Allows the firing {@link PropertyChangeEvent}s to be deferred on a {@code PropertyBindable} object.
     */
    public static class ChangeEventDeferral implements AutoCloseable {

        private PropertyBindable target;
        private ChangeEventDeferral previous;
        private ChangeEventDeferral next;

        protected boolean isOpen() {
            return null != target;
        }

        /**
         * Defers {@link PropertyChangeEvent}s on the specified {@code PropertyBindable} object.
         *
         * @param target The {@code PropertyBindable} object on which {@link PropertyChangeEvent}s are to be deferred.
         * @throws IllegalStateException if this is already in an opened state.
         */
        protected synchronized void open(PropertyBindable target) {
            if (null != target) {
                throw new IllegalStateException("Change deferral has already been opened");
            }
            (this.target = target).propertyChangeSupport.registerDeferral(this);
        }

        @Override
        public void close() {
            if (null != target) {
                PropertyChangeSupportImpl pcs = target.propertyChangeSupport;
                target = null;
                PropertyChangeEvent[] deferredEvents = pcs.unregisterDeferral(this);
                if (null != deferredEvents) {
                    for (PropertyChangeEvent evt : deferredEvents) {
                        pcs.firePropertyChangeImpl(evt);
                    }
                }
            }
        }
    }

    private class PropertyChangeSupportImpl extends PropertyChangeSupport {

        private static final long serialVersionUID = -5190875010028850398L;

        private ChangeEventDeferral first = null;
        private ChangeEventDeferral last = null;
        private final ArrayList<PropertyChangeEvent> orderedDeferredChanges = new ArrayList<>();
        private final HashMap<String, Integer> deferredChangeMap = new HashMap<>();
        private final HashMap<String, HashMap<Integer, Integer>> deferredIndexedChangeMap = new HashMap<>();
        private int noNameChange = -1;
        private final HashMap<Integer, Integer> noNameIndexedChange = new HashMap<>();

        PropertyChangeSupportImpl() {
            super(PropertyBindable.this);
        }

        // We are handling any exceptions on purpose.
        @SuppressWarnings("UseSpecificCatch")
        private void firePropertyChangeImpl(PropertyChangeEvent event) {
            try {
                onPropertyChange(event);
            } catch (Throwable ex) {
                LOG.log(Level.SEVERE, String.format("Uncaught exception in implementing class handling %s property change event", event.getPropertyName()), ex);
                Thread.currentThread().getUncaughtExceptionHandler().uncaughtException(Thread.currentThread(), ex);
            } finally {
                // Temporarily replace exception handler so we can log any exceptions thrown, that might otherwise be untracked.
                UncaughtExceptionHandler eh = Thread.currentThread().getUncaughtExceptionHandler();
                Thread.currentThread().setUncaughtExceptionHandler((Thread t, Throwable e) -> {
                    LOG.log(Level.SEVERE, String.format("Uncaught exception firing %s property change event", event.getPropertyName()), e);
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
            if (null == last) {
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

        private synchronized void registerDeferral(ChangeEventDeferral deferral) {
            deferral.target = PropertyBindable.this;
            if (null == (deferral.previous = last)) {
                first = last = deferral;
            } else {
                deferral.previous.next = last = deferral;
            }
        }

        private synchronized PropertyChangeEvent[] unregisterDeferral(ChangeEventDeferral deferral) {
            deferral.target = null;
            if (null == deferral.previous) {
                if (null == (first = deferral.next)) {
                    last = null;
                    if (!orderedDeferredChanges.isEmpty()) {
                        PropertyChangeEvent[] deferred = orderedDeferredChanges.toArray(new PropertyChangeEvent[orderedDeferredChanges.size()]);
                        orderedDeferredChanges.clear();
                        deferredChangeMap.clear();
                        deferredIndexedChangeMap.clear();
                        noNameIndexedChange.clear();
                        noNameChange = -1;
                        return deferred;
                    }
                } else {
                    deferral.next = first.previous = null;
                }
            } else if (null == (deferral.previous.next = deferral.next)) {
                deferral.previous = (last = deferral.previous).next = null;
            } else {
                deferral.next.previous = deferral.previous;
                deferral.previous = deferral.next = null;
            }
            return null;
        }

    }
}
