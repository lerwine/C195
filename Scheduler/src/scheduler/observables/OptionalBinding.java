package scheduler.observables;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.util.BinarySelective;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 */
public abstract class OptionalBinding<T> extends ObjectBinding<Optional<T>> {

    private final ObservableList<Observable> dependencies;
    private final ObservableList<Observable> readOnlyDependencies;

    protected OptionalBinding(Observable... dependencies) {
        if (null != dependencies && dependencies.length > 0) {
            this.dependencies = FXCollections.observableArrayList(dependencies);
            super.bind(dependencies);
        } else {
            this.dependencies = FXCollections.observableArrayList();
        }
        readOnlyDependencies = FXCollections.unmodifiableObservableList(this.dependencies);
    }

    public BooleanBinding isPresent() {
        return Bindings.createBooleanBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            return null != b && b.isPresent();
        }, this);
    }

    public BooleanBinding isPresentAndEquals(T value) {
        return Bindings.createBooleanBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            return null != b && b.isPresent() && Objects.equals(b.get(), value);
        }, this);
    }

    public BooleanBinding isPresentAndEquals(ObjectBinding<T> value) {
        Objects.requireNonNull(value);
        return Bindings.createBooleanBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            T v = value.get();
            return null != b && b.isPresent() && Objects.equals(b.get(), v);
        }, this, value);
    }

    public BooleanBinding isPresentAndTest(Predicate<T> value) {
        Objects.requireNonNull(value);
        return Bindings.createBooleanBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            return null != b && b.isPresent() && value.test(b.get());
        }, this);
    }

    public ObjectBinding<T> orElseGet(Supplier<T> ifNotPresent) {
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            Optional<T> optional = OptionalBinding.this.get();
            if (null != optional) {
                return optional.orElseGet(ifNotPresent);
            }
            return ifNotPresent.get();
        }, this);
    }

    public ObjectBinding<T> orElse(T ifNotPresent) {
        return Bindings.createObjectBinding(() -> {
            Optional<T> optional = OptionalBinding.this.get();
            if (null != optional) {
                return optional.orElse(ifNotPresent);
            }
            return ifNotPresent;
        }, this);
    }

    public ObjectBinding<T> orElse(ObjectBinding<T> ifNotPresent) {
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            Optional<T> optional = OptionalBinding.this.get();
            T f = ifNotPresent.get();
            if (null != optional) {
                return optional.orElse(f);
            }
            return f;
        }, this, ifNotPresent);
    }

    public <U> BinarySelectiveBinding<T, U> ifElseGet(Supplier<U> ifNotPresent) {
        Objects.requireNonNull(ifNotPresent);
        return BindingHelper.createBinarySelectiveBinding(() -> {
            Optional<T> optional = OptionalBinding.this.get();
            if (null != optional && optional.isPresent()) {
                return BinarySelective.ofPrimary(optional.get());
            }
            return BinarySelective.ofSecondary(ifNotPresent.get());
        }, this);
    }

    public <U> BinarySelectiveBinding<T, U> ifElse(U ifNotPresent) {
        return BindingHelper.createBinarySelectiveBinding(() -> {
            Optional<T> optional = OptionalBinding.this.get();
            if (null != optional && optional.isPresent()) {
                return BinarySelective.ofPrimary(optional.get());
            }
            return BinarySelective.ofSecondary(ifNotPresent);
        }, this);
    }

    public <U> BinarySelectiveBinding<T, U> ifElse(ObjectBinding<U> ifNotPresent) {
        Objects.requireNonNull(ifNotPresent);
        return BindingHelper.createBinarySelectiveBinding(() -> {
            Optional<T> optional = OptionalBinding.this.get();
            U f = ifNotPresent.get();
            if (null != optional && optional.isPresent()) {
                return BinarySelective.ofPrimary(optional.get());
            }
            return BinarySelective.ofSecondary(f);
        }, this, ifNotPresent);
    }

    public <U> OptionalBinding<U> flatMap(Function<? super T, Optional<U>> mapper) {
        Objects.requireNonNull(mapper);
        return BindingHelper.createOptionalBinding(() -> {
            Optional<T> optional = OptionalBinding.this.get();
            if (null != optional) {
                return optional.flatMap(mapper);
            }
            return Optional.empty();
        }, this);
    }

    public <U> OptionalBinding<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return BindingHelper.createOptionalBinding(() -> {
            Optional<T> optional = OptionalBinding.this.get();
            if (null != optional) {
                return optional.map(mapper);
            }
            return Optional.empty();
        }, this);
    }

    public <U> BinarySelectiveBinding<T, U> toBinarySelective(U ifNotPresent) {
        return BindingHelper.createBinarySelectiveBinding(() -> {
            Optional<T> optional = OptionalBinding.this.get();
            if (null != optional && optional.isPresent()) {
                return BinarySelective.ofPrimary(optional.get());
            }
            return BinarySelective.ofSecondaryNullable(ifNotPresent);
        }, this);
    }

    public <U> BinarySelectiveBinding<T, U> toBinarySelective(ObjectBinding<U> ifNotPresent) {
        Objects.requireNonNull(ifNotPresent);
        return BindingHelper.createBinarySelectiveBinding(() -> {
            Optional<T> optional = OptionalBinding.this.get();
            U f = ifNotPresent.get();
            if (null != optional && optional.isPresent()) {
                return BinarySelective.ofPrimary(optional.get());
            }
            return BinarySelective.ofSecondaryNullable(f);
        }, this, ifNotPresent);
    }

    public StringBinding mapToString(Function<T, String> ifPresent, StringBinding ifNotPresent) {
        Objects.requireNonNull(ifPresent);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createStringBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            String f = ifNotPresent.get();
            if (null != b && b.isPresent()) {
                return ifPresent.apply(b.get());
            }
            return f;
        }, this, ifNotPresent);
    }

    public StringBinding mapToString(Function<T, String> ifPresent, String ifNotPresent) {
        Objects.requireNonNull(ifPresent);
        return Bindings.createStringBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            if (null != b && b.isPresent()) {
                return ifPresent.apply(b.get());
            }
            return ifNotPresent;
        }, this);
    }

    public StringBinding mapToString(StringBinding ifPresent, StringBinding ifNotPresent) {
        Objects.requireNonNull(ifPresent);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createStringBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            String t = ifPresent.get();
            String f = ifNotPresent.get();
            if (null != b && b.isPresent()) {
                return t;
            }
            return f;
        }, this, ifPresent, ifNotPresent);
    }

    public StringBinding mapToString(StringBinding ifPresent, String ifNotPresent) {
        Objects.requireNonNull(ifPresent);
        return Bindings.createStringBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            String t = ifPresent.get();
            if (null != b && b.isPresent()) {
                return t;
            }
            return ifNotPresent;
        }, this, ifPresent);
    }

    public StringBinding mapToStringGet(Function<T, String> ifPresent, Supplier<String> ifNotPresent) {
        Objects.requireNonNull(ifPresent);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createStringBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            if (null != b && b.isPresent()) {
                return ifPresent.apply(b.get());
            }
            return ifNotPresent.get();
        }, this);
    }

    public BooleanBinding mapToBoolean(Predicate<T> ifPresent, BooleanBinding ifNotPresent) {
        Objects.requireNonNull(ifPresent);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createBooleanBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            boolean f = ifNotPresent.get();
            if (null != b && b.isPresent()) {
                return ifPresent.test(b.get());
            }
            return f;
        }, this, ifNotPresent);
    }

    public BooleanBinding mapToBoolean(Predicate<T> ifPresent, boolean ifNotPresent) {
        Objects.requireNonNull(ifPresent);
        return Bindings.createBooleanBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            if (null != b && b.isPresent()) {
                return ifPresent.test(b.get());
            }
            return ifNotPresent;
        }, this);
    }

    public BooleanBinding mapToBoolean(BooleanBinding ifPresent, BooleanBinding ifNotPresent) {
        Objects.requireNonNull(ifPresent);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createBooleanBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            boolean t = ifPresent.get();
            boolean f = ifNotPresent.get();
            if (null != b && b.isPresent()) {
                return t;
            }
            return f;
        }, this, ifPresent, ifNotPresent);
    }

    public BooleanBinding mapToBoolean(BooleanBinding ifPresent, boolean ifNotPresent) {
        Objects.requireNonNull(ifPresent);
        return Bindings.createBooleanBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            boolean t = ifPresent.get();
            if (null != b && b.isPresent()) {
                return t;
            }
            return ifNotPresent;
        }, this, ifPresent);
    }

    public BooleanBinding mapToBoolean(boolean ifPresent, BooleanBinding ifNotPresent) {
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createBooleanBinding(() -> {
            Optional<T> b = OptionalBinding.this.get();
            boolean f = ifNotPresent.get();
            if (null != b && b.isPresent()) {
                return ifPresent;
            }
            return f;
        }, this, ifNotPresent);
    }

    protected boolean containsDependency(Observable observable) {
        return dependencies.contains(observable);
    }

    protected void addDependency(Observable observable) {
        if (!dependencies.contains(observable)) {
            super.bind(observable);
            dependencies.add(observable);
        }
    }

    protected void removeDependency(Observable observable) {
        if (dependencies.remove(observable)) {
            super.unbind(observable);
        }
    }

    @Override
    public ObservableList<Observable> getDependencies() {
        return readOnlyDependencies;
    }

    @Override
    public void dispose() {
        super.dispose();
        dependencies.forEach((t) -> super.unbind(t));
        dependencies.clear();
    }
}
