package scheduler.observables.validation;

import scheduler.util.BooleanConsumer;
import com.sun.javafx.binding.ExpressionHelper;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableBooleanValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public abstract class ValidationNode<T> implements ReadOnlyProperty<T> {

    protected final Object syncRoot;
    private final Object bean;
    private final String name;
    private ExpressionHelper<T> helper = null;
    private final ValidValueProperty validValue;

    protected ValidationNode(Object bean, String name, boolean isValidValue) {
        syncRoot = new Object();
        this.validValue = new ValidValueProperty(isValidValue);
        this.bean = bean;
        this.name = (name == null) ? "" : name;
    }

    public boolean isValidValue() {
        return validValue.get();
    }

    protected void setValidValue(boolean value) {
        synchronized (validValue) {
            if (value == validValue.value) {
                return;
            }
            validValue.value = value;
        }
        fireValidValueChangedEvent();
    }

    protected void fireValueChangedEvent() {
        ExpressionHelper.fireValueChangedEvent(helper);
    }
    
    private void fireValidValueChangedEvent() {
        onValidValueChanged(validValue.value);
        ExpressionHelper.fireValueChangedEvent(validValue.vHelper);
    }
    
    protected void onValidValueChanged(boolean isValidValue) { }
    
    protected void updateValidity(Consumer<BooleanConsumer> action) {
        synchronized (validValue) {
            boolean oldValue = validValue.value;
            action.accept((t) -> validValue.value = t);
            if (oldValue == validValue.value) {
                return;
            }
        }
        fireValidValueChangedEvent();
    }

    protected boolean testUpdateValidity(Predicate<BooleanConsumer> predicate) {
        boolean result;
        synchronized (validValue) {
            boolean oldValue = validValue.value;
            result = predicate.test((t) -> validValue.value = t);
            if (oldValue == validValue.value) {
                return result;
            }
        }
        ExpressionHelper.fireValueChangedEvent(validValue.vHelper);
        return result;
    }

    protected <U> U applyValidity(Function<BooleanConsumer, U> func) {
        U result;
        synchronized (validValue) {
            boolean oldValue = validValue.value;
            result = func.apply((t) -> validValue.value = t);
            if (oldValue == validValue.value) {
                return result;
            }
        }
        ExpressionHelper.fireValueChangedEvent(validValue.vHelper);
        return result;
    }

    public ReadOnlyBooleanProperty validValueProperty() {
        return validValue;
    }

    @Override
    public final void addListener(ChangeListener<? super T> listener) {
        helper = ExpressionHelper.addListener(helper, this, listener);
    }

    @Override
    public final void removeListener(ChangeListener<? super T> listener) {
        helper = ExpressionHelper.removeListener(helper, listener);
    }

    @Override
    public final void addListener(InvalidationListener listener) {
        helper = ExpressionHelper.addListener(helper, this, listener);
    }

    @Override
    public final void removeListener(InvalidationListener listener) {
        helper = ExpressionHelper.removeListener(helper, listener);
    }

    @Override
    public final Object getBean() {
        return bean;
    }

    @Override
    public final String getName() {
        return name;
    }

    public class ValidValueProperty extends ReadOnlyBooleanProperty {

        private ExpressionHelper<Boolean> vHelper = null;
        private boolean value;

        private ValidValueProperty(boolean initialValue) {
            value = initialValue;
        }

        @Override
        public boolean get() {
            return value;
        }

        @Override
        public void addListener(ChangeListener<? super Boolean> listener) {

            vHelper = ExpressionHelper.addListener(vHelper, this, listener);
        }

        @Override
        public void removeListener(ChangeListener<? super Boolean> listener) {
            vHelper = ExpressionHelper.removeListener(vHelper, listener);
        }

        @Override
        public void addListener(InvalidationListener listener) {
            vHelper = ExpressionHelper.addListener(vHelper, this, listener);
        }

        @Override
        public void removeListener(InvalidationListener listener) {
            vHelper = ExpressionHelper.removeListener(vHelper, listener);
        }

        @Override
        public Object getBean() {
            return ValidationNode.this;
        }

        @Override
        public String getName() {
            return "validValue";
        }

    }

    public static class Chain extends ValidationNode<Boolean> implements ObservableBooleanValue, Collection<ValidationNode<?>> {

        private final boolean defaultValue;
        private final LinkedList<ValidationNode<?>> backingList;

        public Chain(Object bean, String name, boolean defaultValue) {
            super(bean, name, defaultValue);
            backingList = new LinkedList<>();
            this.defaultValue = defaultValue;
        }

        @Override
        public Boolean getValue() {
            return super.isValidValue();
        }

        @Override
        public boolean get() {
            return super.isValidValue();
        }

        @Override
        public int size() {
            return backingList.size();
        }

        @Override
        public boolean isEmpty() {
            return backingList.isEmpty();
        }

        public boolean contains(Object o, boolean recursive) {
            if (null != o && o instanceof ValidationNode && !backingList.isEmpty()) {
                ValidationNode<?> node = (ValidationNode<?>) o;
                return backingList.contains(node) || (recursive && backingList.stream()
                        .anyMatch((t) -> t instanceof Chain && ((Chain) t).contains(node, true)));
            }
            return false;
        }

        @Override
        public boolean contains(Object o) {
            if (null != o && o instanceof ValidationNode) {
                return backingList.contains((ValidationNode<?>) o);
            }
            return false;
        }

        @Override
        public Iterator<ValidationNode<?>> iterator() {
            return new IteratorImpl(backingList.iterator());
        }

        @Override
        public Object[] toArray() {
            return backingList.toArray();
        }

        @Override
        @SuppressWarnings("SuspiciousToArrayCall")
        public <T> T[] toArray(T[] a) {
            return backingList.toArray(a);
        }

        private boolean tryAdd(ValidationNode<?> e) {
            if (null != e && !backingList.contains(e)) {
                if ((!backingList.isEmpty() && backingList.stream().anyMatch((t) -> t instanceof Chain && ((Chain) t).contains(e, true)))
                        || (e instanceof Chain && (e == this || ((Chain) e).contains(this, true)))) {
                    throw new IllegalArgumentException();
                }
                if (backingList.add(e)) {
                    e.validValueProperty().addListener(this::nodeInvalidated);
                    return true;
                }
            }
            return false;
        }

        private boolean tryAdd(int index, ValidationNode<?> e) {
            if (null != e && !backingList.contains(e)) {
                if ((!backingList.isEmpty() && backingList.stream().anyMatch((t) -> t instanceof Chain && ((Chain) t).contains(e, true)))
                        || (e instanceof Chain && (e == this || ((Chain) e).contains(this, true)))) {
                    throw new IllegalArgumentException();
                }
                backingList.add(index, e);
                e.validValueProperty().addListener(this::nodeInvalidated);
                return true;
            }
            return false;
        }

        private boolean tryRemove(ValidationNode<?> e) {
            if (null != e && backingList.contains(e) && backingList.remove(e)) {
                e.validValueProperty().removeListener(this::nodeInvalidated);
                return true;
            }
            return false;
        }

        @Override
        public boolean add(ValidationNode<?> e) {
            if (null == e) {
                return false;
            }
            return testUpdateValidity((t) -> {
                if (tryAdd(e)) {
                    if (e.isValidValue()) {
                        if (backingList.size() == 1) {
                            t.accept(true);
                        }
                    } else {
                        t.accept(false);
                    }
                    return true;
                }
                return false;
            });
        }

        private void nodeInvalidated(Observable observable) {
            updateValidity((t) -> {
                if (((ReadOnlyBooleanProperty) observable).get()) {
                    if (!isValidValue()) {
                        t.accept(backingList.stream().allMatch((u) -> u.isValidValue()));
                    }
                } else {
                    t.accept(false);
                }
            });
        }

        @Override
        public boolean remove(Object o) {
            if (null != o && o instanceof ValidationNode) {
                return testUpdateValidity((t) -> {
                    ValidationNode<?> e = (ValidationNode<?>) o;
                    if (tryRemove(e)) {
                        if (backingList.isEmpty()) {
                            t.accept(defaultValue);
                        } else if (!e.isValidValue()) {
                            t.accept(backingList.stream().allMatch((u) -> u.isValidValue()));
                        }
                        return true;
                    }
                    return false;
                });
            }
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return null != c && backingList.containsAll(c);
        }

        @Override
        public boolean addAll(Collection<? extends ValidationNode<?>> c) {
            if (null == c || c.isEmpty()) {
                return false;
            }
            return testUpdateValidity((t) -> {
                boolean changed = false;
                Iterator<? extends ValidationNode<?>> iterator = c.iterator();
                while (iterator.hasNext()) {
                    ValidationNode<?> e = iterator.next();
                    if (tryAdd(e)) {
                        if (e.isValidValue()) {
                            if (backingList.size() == 1) {
                                t.accept(true);
                            }
                        } else {
                            t.accept(false);
                        }
                        changed = true;
                    }
                }
                return changed;
            });
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            if (null != c && !c.isEmpty()) {
                return testUpdateValidity((t) -> {
                    boolean modified = false;
                    Iterator<?> iterator = c.iterator();
                    boolean r = false;
                    while (iterator.hasNext()) {
                        Object o = iterator.next();
                        if (null != o && o instanceof ValidationNode) {
                            ValidationNode<?> e = (ValidationNode<?>) o;
                            if (tryRemove(e)) {
                                modified = true;
                                if (backingList.isEmpty()) {
                                    t.accept(defaultValue);
                                    return true;
                                }
                                if (!e.isValidValue()) {
                                    r = true;
                                }
                            }
                        }
                        if (r) {
                            t.accept(backingList.stream().allMatch((u) -> u.isValidValue()));
                        }
                    }
                    return modified;
                });
            }
            return false;
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            if (null != c && !c.isEmpty()) {
                return testUpdateValidity((t) -> {
                    boolean modified = false;
                    Iterator<ValidationNode<?>> iterator = iterator();
                    boolean r = false;
                    while (iterator.hasNext()) {
                        ValidationNode<?> e = iterator.next();
                        if (!c.contains(e) && tryRemove(e)) {
                            modified = true;
                            if (backingList.isEmpty()) {
                                t.accept(defaultValue);
                                return true;
                            }
                            if (!e.isValidValue()) {
                                r = true;
                            }
                        }
                    }
                    if (r) {
                        t.accept(backingList.stream().allMatch((u) -> u.isValidValue()));
                    }
                    return modified;
                });
            }
            return false;
        }

        @Override
        public void clear() {
            if (!backingList.isEmpty()) {
                updateValidity((t) -> {
                    backingList.forEach((u) -> u.validValueProperty().removeListener(this::nodeInvalidated));
                    backingList.clear();
                    t.accept(defaultValue);
                });
            }
        }

        private class IteratorImpl implements Iterator<ValidationNode<?>> {

            private final Iterator<ValidationNode<?>> backingIterator;
            private ValidationNode<?> current = null;

            private IteratorImpl(Iterator<ValidationNode<?>> backingIterator) {
                this.backingIterator = backingIterator;
            }

            @Override
            public boolean hasNext() {
                return backingIterator.hasNext();
            }

            @Override
            public ValidationNode<?> next() {
                synchronized (backingIterator) {
                    current = backingIterator.next();
                    return current;
                }
            }

            @Override
            public void remove() {
                ValidationNode<?> node;
                synchronized (backingIterator) {
                    node = current;
                    current = null;
                }
                if (null == node) {
                    throw new IllegalStateException();
                }
                Chain.this.updateValidity((t) -> {
                    backingIterator.remove();
                    node.validValueProperty().removeListener(Chain.this::nodeInvalidated);
                    if (Chain.this.backingList.isEmpty()) {
                        t.accept(defaultValue);
                    } else if (!node.isValidValue()) {
                        t.accept(backingList.stream().allMatch((u) -> u.isValidValue()));
                    }
                });
            }

        }

    }
}
