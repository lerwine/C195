package scheduler.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanPropertyBase;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableBooleanValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class BooleanAggregate {

    private final ReadOnlyBooleanWrapper anyTrue;
    private Node firstTrue;
    private Node lastTrue;
    private Node firstFalse;
    private Node lastFalse;
    private final ReadOnlyBooleanWrapper anyFalse;

    public BooleanAggregate() {
        this.anyTrue = new ReadOnlyBooleanWrapper(false);
        this.anyFalse = new ReadOnlyBooleanWrapper(false);
    }

    public boolean isAnyTrue() {
        return anyTrue.get();
    }

    public ReadOnlyBooleanProperty anyTrueProperty() {
        return anyTrue.getReadOnlyProperty();
    }

    public boolean isAnyFalse() {
        return anyFalse.get();
    }

    public ReadOnlyBooleanProperty anyFalseProperty() {
        return anyFalse.getReadOnlyProperty();
    }

    /**
     * Registers a boolean input node from an {@link ObservableValue} and a bean pattern.
     *
     * @param <T> The {@link ObservableValue} type.
     * @param observable The {@link ObservableValue} object.
     * @param target The target bean object.
     * @param propertyName The property name on the {@code target}.
     * @param predicate Calculates the {@code boolean} node value from the {@code observable} and {@code target} objects.
     * @param bean The bean for the registered {@link Node}.
     * @param name The property name.
     * @return The registered {@link Node} that is listening for changes.
     */
    public synchronized <T> Node register(ObservableValue<T> observable, IPropertyBindable target, String propertyName,
            BiPredicate<T, PropertyChangeEvent> predicate, Object bean, String name) {
        T initialValue = observable.getValue();
        PropertyChangeEvent evt = new PropertyChangeEvent(this, Objects.requireNonNull(propertyName), null,
                Arrays.stream(target.getClass().getDeclaredFields()).filter((t) -> t.getName().equals(propertyName)).findAny().map((t) -> {
                    boolean accessible = t.isAccessible();
                    try {
                        if (!accessible) {
                            t.setAccessible(true);
                        }
                        return t.get(target);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(BooleanAggregate.class.getName()).log(Level.SEVERE,
                                String.format("Error reading property %s", propertyName), ex);
                    } finally {
                        if (!accessible) {
                            t.setAccessible(false);
                        }
                    }
                    return null;
                }).orElse(null));
        BeanToFxListenerNode<T> result = new BeanToFxListenerNode<T>(bean, name, predicate.test(initialValue, evt), target) {
            PropertyChangeEvent lastEvent = evt;
            T lastValue = initialValue;

            @Override
            void onUnregister() {
                observable.removeListener(this);
                target.removePropertyChangeListener(getProxy());
            }

            @Override
            public synchronized void changed(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                lastValue = newValue;
                set(predicate.test(lastValue, lastEvent));
            }

            @Override
            public synchronized void propertyChange(PropertyChangeEvent evt) {
                if (propertyName.equals(evt.getPropertyName())) {
                    lastEvent = evt;
                    set(predicate.test(lastValue, lastEvent));
                }
            }

        };
        add(result);
        observable.addListener(result);
        target.addPropertyChangeListener(result.getProxy());
        return result;
    }

    /**
     * Registers a boolean input node from an {@link ObservableValue} and a bean pattern.
     *
     * @param <T> The {@link ObservableValue} type.
     * @param observable The {@link ObservableValue} object.
     * @param target The target bean object.
     * @param propertyName The property name on the {@code target}.
     * @param predicate Calculates the {@code boolean} node value from the {@code observable} and {@code target} objects.
     * @param name The property name.
     * @return The registered {@link Node} that is listening for changes.
     */
    public <T> Node register(ObservableValue<T> observable, IPropertyBindable target, String propertyName,
            BiPredicate<T, PropertyChangeEvent> predicate, String name) {
        return register(observable, target, propertyName, predicate, this, name);
    }

    /**
     * Registers a boolean input node from an {@link ObservableValue} and a bean pattern.
     *
     * @param <T> The {@link ObservableValue} type.
     * @param observable The {@link ObservableValue} object.
     * @param target The target bean object.
     * @param propertyName The property name on the {@code target}.
     * @param predicate Calculates the {@code boolean} node value from the {@code observable} and {@code target} objects.
     * @return The registered {@link Node} that is listening for changes.
     */
    public <T> Node register(ObservableValue<T> observable, IPropertyBindable target, String propertyName,
            BiPredicate<T, PropertyChangeEvent> predicate) {
        return register(observable, target, propertyName, predicate, this, propertyName);
    }

    /**
     * Registers a boolean input node from a bean pattern.
     *
     * @param target The target bean object.
     * @param propertyName The property name on the {@code target}.
     * @param predicate Calculates the {@code boolean} node value from the {@code target} object.
     * @param bean The bean for the registered {@link Node}.
     * @param name The property name.
     * @return The registered {@link Node} that is listening for changes.
     */
    public synchronized Node register(IPropertyBindable target, String propertyName, Predicate<PropertyChangeEvent> predicate, Object bean, String name) {
        PropertyChangeEvent evt = new PropertyChangeEvent(this, Objects.requireNonNull(propertyName), null,
                Arrays.stream(target.getClass().getDeclaredFields()).filter((t) -> t.getName().equals(propertyName)).findAny().map((t) -> {
                    boolean accessible = t.isAccessible();
                    try {
                        if (!accessible) {
                            t.setAccessible(true);
                        }
                        return t.get(target);
                    } catch (IllegalAccessException ex) {
                        Logger.getLogger(BooleanAggregate.class.getName()).log(Level.SEVERE,
                                String.format("Error reading property %s", propertyName), ex);
                    } finally {
                        if (!accessible) {
                            t.setAccessible(false);
                        }
                    }
                    return null;
                }).orElse(null));
        BeanListenerNode result = new BeanListenerNode(bean, name, predicate.test(evt), target) {
            @Override
            void onUnregister() {
                target.removePropertyChangeListener(getProxy());
            }

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (propertyName.equals(evt.getPropertyName())) {
                    set(predicate.test(evt));
                }
            }
        };
        add(result);
        target.addPropertyChangeListener(result.getProxy());
        return result;
    }

    /**
     * Registers a boolean input node from a bean pattern.
     *
     * @param target The target bean object.
     * @param propertyName The property name on the {@code target}.
     * @param predicate Calculates the {@code boolean} node value from the {@code target} object.
     * @param name The property name.
     * @return The registered {@link Node} that is listening for changes.
     */
    public Node register(IPropertyBindable target, String propertyName, Predicate<PropertyChangeEvent> predicate, String name) {
        return register(target, propertyName, predicate, this, name);
    }

    /**
     * Registers a boolean input node from a bean pattern.
     *
     * @param target The target bean object.
     * @param propertyName The property name on the {@code target}.
     * @param predicate Calculates the {@code boolean} node value from the {@code target} object.
     * @return The registered {@link Node} that is listening for changes.
     */
    public Node register(IPropertyBindable target, String propertyName, Predicate<PropertyChangeEvent> predicate) {
        return register(target, propertyName, predicate, this, propertyName);
    }

    /**
     * Registers a boolean input node from two inter-dependent {@link ObservableValue}s.
     *
     * @param <T> The primary {@link ObservableValue} type.
     * @param <U> The secondary {@link ObservableValue} type.
     * @param primary The primary {@link ObservableValue}.
     * @param secondary The secondary {@link ObservableValue}.
     * @param predicate Calculates the {@code boolean} node value from the {@code primary} and {@code secondary} values.
     * @param bean The bean for the registered {@link Node}.
     * @param name The property name.
     * @return The registered {@link Node} that is listening for changes.
     */
    public synchronized <T, U> Node register(ObservableValue<T> primary, ObservableValue<U> secondary, BiPredicate<T, U> predicate, Object bean, String name) {
        DependentFxListenerNode<T, U> result = new DependentFxListenerNode<T, U>(bean, name, predicate.test(primary.getValue(), secondary.getValue())) {
            T lastPrimary = primary.getValue();
            U lastSecondary = secondary.getValue();

            @Override
            void primaryChanged(ObservableValue<? extends T> observable, T oldValue, T newValue) {
                lastPrimary = newValue;
                set(predicate.test(lastPrimary, lastSecondary));
            }

            @Override
            void secondaryChanged(ObservableValue<? extends U> observable, U oldValue, U newValue) {
                lastSecondary = newValue;
                set(predicate.test(lastPrimary, lastSecondary));
            }

            @Override
            void onUnregister() {
                primary.removeListener(this::primaryChanged);
                secondary.removeListener(this::secondaryChanged);
            }

        };
        add(result);
        primary.addListener(result::primaryChanged);
        secondary.addListener(result::secondaryChanged);
        return result;
    }

    /**
     * Registers a boolean input node from two inter-dependent {@link ObservableValue}s.
     *
     * @param <T> The primary {@link ObservableValue} type.
     * @param <U> The secondary {@link ObservableValue} type.
     * @param primary The primary {@link ObservableValue}.
     * @param secondary The secondary {@link ObservableValue}.
     * @param predicate Calculates the {@code boolean} node value from the {@code primary} and {@code secondary} values.
     * @param name The property name.
     * @return The registered {@link Node} that is listening for changes.
     */
    public <T, U> Node register(ObservableValue<T> primary, ObservableValue<U> secondary, BiPredicate<T, U> predicate, String name) {
        return register(primary, secondary, predicate, this, name);
    }

    /**
     * Registers a boolean input node from two inter-dependent {@link ObservableValue}s.
     *
     * @param <T> The primary {@link ObservableValue} type.
     * @param <U> The secondary {@link ObservableValue} type.
     * @param primary The primary {@link ObservableValue}.
     * @param secondary The secondary {@link ObservableValue}.
     * @param predicate Calculates the {@code boolean} node value from the {@code primary} and {@code secondary} values.
     * @return The registered {@link Node} that is listening for changes.
     */
    public <T, U> Node register(ObservableValue<T> primary, ObservableValue<U> secondary, BiPredicate<T, U> predicate) {
        if (primary instanceof ReadOnlyProperty) {
            ReadOnlyProperty<T> p = (ReadOnlyProperty<T>) primary;
            return register(p, secondary, predicate, p.getBean(), p.getName());
        }
        return register(primary, secondary, predicate, this, "");
    }

    /**
     * Registers a boolean input node from an {@link ObservableValue}.
     *
     * @param <T> The {@link ObservableValue} type.
     * @param observable The {@link ObservableValue}.
     * @param predicate Calculates the {@code boolean} node value from the {@code observable} value.
     * @param bean The bean for the registered {@link Node}.
     * @param name The property name.
     * @return The registered {@link Node} that is listening for changes.
     */
    public synchronized <T> Node register(ObservableValue<T> observable, Predicate<T> predicate, Object bean, String name) {
        FxListenerNode<T> result = new FxListenerNode<T>(bean, name, predicate.test(observable.getValue())) {
            @Override
            void onUnregister() {
                observable.removeListener(this);
            }

            @Override
            public void changed(ObservableValue<? extends T> o, T oldValue, T newValue) {
                if (!Objects.equals(oldValue, newValue)) {
                    set(predicate.test(newValue));
                }
            }

        };
        add(result);
        observable.addListener(result);
        return result;
    }

    /**
     * Registers a boolean input node from an {@link ObservableValue}.
     *
     * @param <T> The {@link ObservableValue} type.
     * @param observable The {@link ObservableValue}.
     * @param predicate Calculates the {@code boolean} node value from the {@code observable} value.
     * @param name The property name.
     * @return The registered {@link Node} that is listening for changes.
     */
    public <T> Node register(ObservableValue<T> observable, Predicate<T> predicate, String name) {
        return register(observable, predicate, (observable instanceof ReadOnlyProperty) ? ((ReadOnlyProperty<T>) observable).getBean() : this, name);
    }

    /**
     * Registers a boolean input node from an {@link ObservableValue}.
     *
     * @param <T> The {@link ObservableValue} type.
     * @param observable The {@link ObservableValue}.
     * @param predicate Calculates the {@code boolean} node value from the {@code observable} value.
     * @return The registered {@link Node} that is listening for changes.
     */
    public <T> Node register(ObservableValue<T> observable, Predicate<T> predicate) {
        if (observable instanceof ReadOnlyProperty) {
            ReadOnlyProperty<T> p = (ReadOnlyProperty<T>) observable;
            return register(observable, predicate, p.getBean(), p.getName());
        }
        return register(observable, predicate, this, "");
    }

    /**
     * Registers a boolean input node from an {@link ObservableBooleanValue}.
     *
     * @param observable The source {@link ObservableBooleanValue}.
     * @param bean The bean for the registered {@link Node}.
     * @param name The property name.
     * @return The registered {@link Node} that is listening for changes.
     */
    public synchronized Node register(ObservableBooleanValue observable, Object bean, String name) {
        FxListenerNode<Boolean> result = new FxListenerNode<Boolean>(bean, name, observable.getValue()) {
            @Override
            public void changed(ObservableValue<? extends Boolean> o, Boolean oldValue, Boolean newValue) {
                if (!Objects.equals(oldValue, newValue)) {
                    set(newValue);
                }
            }

            @Override
            void onUnregister() {
                observable.removeListener(this);
            }

        };
        add(result);
        observable.addListener(result);
        return result;
    }

    /**
     * Registers a boolean input node from an {@link ObservableBooleanValue}.
     *
     * @param observable The source {@link ObservableBooleanValue}.
     * @param name The property name.
     * @return The registered {@link Node} that is listening for changes.
     */
    public Node register(ObservableBooleanValue observable, String name) {
        return register(observable, (observable instanceof ReadOnlyProperty) ? ((ReadOnlyProperty<Boolean>) observable).getBean() : this, name);
    }

    /**
     * Registers a boolean input node from an {@link ObservableBooleanValue}.
     *
     * @param observable The source {@link ObservableBooleanValue}.
     * @return The registered {@link Node} that is listening for changes.
     */
    public Node register(ObservableBooleanValue observable) {
        if (observable instanceof ReadOnlyProperty) {
            ReadOnlyProperty<Boolean> p = (ReadOnlyProperty<Boolean>) observable;
            return register(observable, p.getBean(), p.getName());
        }
        return register(observable, this, "");
    }

    /**
     * Registers a new {@link WritableNode} that can be manually updated.
     *
     * @param initialValue The initial node value.
     * @param bean The bean for the registered {@link Node}.
     * @param name The property name.
     * @return The registered {@link WritableNode}.
     */
    public WritableNode register(boolean initialValue, Object bean, String name) {
        WritableNode result = new WritableNode(bean, name, initialValue);
        add(result);
        return result;
    }

    /**
     * Registers a new {@link WritableNode} that can be manually updated.
     *
     * @param initialValue The initial node value.
     * @param name The property name.
     * @return The registered {@link WritableNode}.
     */
    public WritableNode register(boolean initialValue, String name) {
        return register(initialValue, this, name);
    }

    /**
     * Registers a new {@link WritableNode} that can be manually updated.
     *
     * @param initialValue The initial node value.
     * @return The registered {@link WritableNode}.
     */
    public WritableNode register(boolean initialValue) {
        return register(initialValue, this, "");
    }

    public void unregister(Node node) {
        if (null == node.previous) {
            if (Objects.equals(node, firstFalse)) {
                if (null == (firstFalse = node.next)) {
                    lastFalse = null;
                    anyFalse.set(false);
                } else {
                    node.next = firstFalse.previous = null;
                }
            } else if (Objects.equals(node, firstTrue)) {
                if (null == (firstTrue = node.next)) {
                    lastTrue = null;
                    anyTrue.set(false);
                } else {
                    node.next = firstTrue.previous = null;
                }
            } else {
                return;
            }
        } else {
            Node n = node;
            while (null != n.next) {
                n = n.next;
            }
            if (Objects.equals(n, lastFalse)) {
                if (null == (node.previous.next = node.next)) {
                    node.previous = (lastFalse = node.previous).next = null;
                } else {
                    node.next.previous = node.previous;
                    node.next = node.previous = null;
                }
            } else if (Objects.equals(n, lastTrue)) {
                if (null == (node.previous.next = node.next)) {
                    node.previous = (lastTrue = node.previous).next = null;
                } else {
                    node.next.previous = node.previous;
                    node.next = node.previous = null;
                }
            } else {
                return;
            }
        }
        if (node instanceof ListenerNode) {
            ((ListenerNode) node).onUnregister();
        }
        node.removeListener(this::onNodeChanged);
    }

    private synchronized void add(Node node) {
        if (node.get()) {
            if (null == (node.previous = lastTrue)) {
                firstTrue = lastTrue = node;
                anyTrue.set(true);
            } else {
                lastTrue = node.previous.next = node;
            }
        } else if (null == (node.previous = lastFalse)) {
            firstFalse = lastFalse = node;
            anyFalse.set(true);
        } else {
            lastFalse = node.previous.next = node;
        }
        node.addListener(this::onNodeChanged);
    }

    private synchronized boolean isRegisteredTrue(Node node) {
        if (null == node.previous) {
            return Objects.equals(firstTrue, node);
        }
        while (null != node.next) {
            node = node.next;
        }
        return Objects.deepEquals(lastTrue, node);
    }

    private synchronized void onNodeChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        @SuppressWarnings("unchecked")
        Node node = (Node) observable;
        if (node.get()) {
            if (!isRegisteredTrue(node)) {
                if (null == node.previous) {
                    if (null == (firstFalse = node.next)) {
                        lastFalse = null;
                        anyFalse.set(false);
                    } else {
                        firstFalse.previous = node.next = null;
                    }
                } else if (null == (node.previous.next = node.next)) {
                    (lastFalse = node.previous).next = null;
                } else {
                    node.next.previous = node.previous;
                    node.next = null;
                }

                if (null == (node.previous = lastTrue)) {
                    firstTrue = lastTrue = node;
                    node.next = null;
                    anyTrue.set(true);
                } else {
                    lastTrue = lastTrue.next = node;
                }
            }
        } else if (isRegisteredTrue(node)) {
            if (null == node.previous) {
                if (null == (firstTrue = node.next)) {
                    lastTrue = null;
                    anyTrue.set(false);
                } else {
                    firstTrue.previous = node.next = null;
                }
            } else if (null == (node.previous.next = node.next)) {
                (lastFalse = node.previous).next = null;
            } else {
                node.next.previous = node.previous;
                node.next = null;
            }

            if (null == (node.previous = lastFalse)) {
                firstFalse = lastFalse = node;
                node.next = null;
                anyFalse.set(true);
            } else {
                lastFalse = lastFalse.next = node;
            }
        }
    }

    public class Node extends ReadOnlyBooleanPropertyBase {

        private Node previous;
        private Node next;
        private final Object bean;
        private final String name;
        private boolean value;

        private Node(Object bean, String name, boolean initialValue) {
            this.bean = (null == bean) ? BooleanAggregate.this : bean;
            this.name = (null == name) ? "" : name;
            value = initialValue;
        }

        @Override
        public boolean get() {
            return value;
        }

        protected synchronized void set(boolean value) {
            if (value != this.value) {
                this.value = value;
                this.fireValueChangedEvent();
            }
        }

        @Override
        public Object getBean() {
            return bean;
        }

        @Override
        public String getName() {
            return name;
        }

    }

    private abstract class DependentFxListenerNode<T, U> extends ListenerNode {

        public DependentFxListenerNode(Object bean, String name, boolean initialValue) {
            super(bean, name, initialValue);
        }

        abstract void primaryChanged(ObservableValue<? extends T> observable, T oldValue, T newValue);

        abstract void secondaryChanged(ObservableValue<? extends U> observable, U oldValue, U newValue);
    }

    private abstract class BeanToFxListenerNode<T> extends BeanListenerNode implements ChangeListener<T> {

        public BeanToFxListenerNode(Object bean, String name, boolean initialValue, IPropertyBindable source) {
            super(bean, name, initialValue, source);
        }

    }

    private abstract class BeanListenerNode extends ListenerNode implements PropertyChangeListener {

        WeakPropertyChangeListenerProxy proxy;

        public BeanListenerNode(Object bean, String name, boolean initialValue, IPropertyBindable source) {
            super(bean, name, initialValue);
            proxy = new WeakPropertyChangeListenerProxy(source, this);
        }

        public WeakPropertyChangeListenerProxy getProxy() {
            return proxy;
        }

    }

    private abstract class FxListenerNode<T> extends ListenerNode implements ChangeListener<T> {

        public FxListenerNode(Object bean, String name, boolean initialValue) {
            super(bean, name, initialValue);
        }

    }

    private abstract class ListenerNode extends Node {

        public ListenerNode(Object bean, String name, boolean initialValue) {
            super(bean, name, initialValue);
        }

        abstract void onUnregister();
    }

    public class WritableNode extends Node implements WritableBooleanValue {

        public WritableNode(Object bean, String name, boolean initialValue) {
            super(bean, name, initialValue);
        }

        @Override
        public void set(boolean value) {
            super.set(value);
        }

        @Override
        public void setValue(Boolean value) {
            set(value);
        }

    }
}
