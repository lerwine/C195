package scheduler.observables;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.util.BinaryOptional;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public abstract class BinaryOptionalBinding<T, U> extends ObjectBinding<BinaryOptional<T, U>> {

    private final ObservableList<Observable> dependencies;
    private final ObservableList<Observable> readOnlyDependencies;

    protected BinaryOptionalBinding(Observable... dependencies) {
        if (null != dependencies && dependencies.length > 0) {
            this.dependencies = FXCollections.observableArrayList(dependencies);
            super.bind(dependencies);
        } else {
            this.dependencies = FXCollections.observableArrayList();
        }
        readOnlyDependencies = FXCollections.unmodifiableObservableList(this.dependencies);
    }

    public BooleanBinding isPrimary() {
        return Bindings.createBooleanBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            return null != b && b.isPrimary();
        }, this);
    }

    public BooleanBinding isSecondary() {
        return Bindings.createBooleanBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            return null != b && b.isSecondary();
        }, this);
    }

    public BooleanBinding isPresent() {
        return Bindings.createBooleanBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            return null != b && b.isPresent();
        }, this);
    }

    public <S> ObjectBinding<S> flatMap(Function<T, S> ifPrimary, Function<U, S> ifSecondary, Supplier<S> ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            if (null != b) {
                return b.map(ifPrimary, ifSecondary, ifNotPresent);
            }
            return ifNotPresent.get();
        }, this);
    }

    public <S> ObjectBinding<S> flatMap(Function<T, S> ifPrimary, Function<U, S> ifSecondary, ObservableValue<S> ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            S n = ifNotPresent.getValue();
            if (null != b) {
                return b.map(ifPrimary, ifSecondary, () -> n);
            }
            return n;
        }, this);
    }

    public <S> ObjectBinding<S> flatMap(Function<T, S> ifPrimary, Function<U, S> ifSecondary, S ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            if (null != b) {
                return b.map(ifPrimary, ifSecondary, () -> ifNotPresent);
            }
            return ifNotPresent;
        }, this);
    }

    public <S> ObjectBinding<S> flatMap(Function<T, S> ifPrimary, S ifSecondary, Supplier<S> ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            if (null != b) {
                return b.map(ifPrimary, (u) -> ifSecondary, ifNotPresent);
            }
            return ifNotPresent.get();
        }, this);
    }

    public <S> ObjectBinding<S> flatMap(Function<T, S> ifPrimary, S ifSecondary, ObservableValue<S> ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            S n = ifNotPresent.getValue();
            if (null != b) {
                return b.map(ifPrimary, (u) -> ifSecondary, () -> n);
            }
            return n;
        }, this);
    }

    public <S> ObjectBinding<S> flatMap(Function<T, S> ifPrimary, S ifSecondary, S ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            if (null != b) {
                return b.map(ifPrimary, (u) -> ifSecondary, () -> ifNotPresent);
            }
            return ifNotPresent;
        }, this);
    }

    public <S> ObjectBinding<S> flatMap(S ifPrimary, Function<U, S> ifSecondary, Supplier<S> ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            if (null != b) {
                return b.map((t) -> ifPrimary, ifSecondary, ifNotPresent);
            }
            return ifNotPresent.get();
        }, this);
    }

    public <S> ObjectBinding<S> flatMap(S ifPrimary, Function<U, S> ifSecondary, ObservableValue<S> ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            S n = ifNotPresent.getValue();
            if (null != b) {
                return b.map((t) -> ifPrimary, ifSecondary, () -> n);
            }
            return n;
        }, this);
    }

    public <S> ObjectBinding<S> flatMap(S ifPrimary, Function<U, S> ifSecondary, S ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            if (null != b) {
                return b.map((t) -> ifPrimary, ifSecondary, () -> ifNotPresent);
            }
            return ifNotPresent;
        }, this);
    }

    public <S> ObjectBinding<S> flatMap(S ifPrimary, S ifSecondary, Supplier<S> ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            if (null != b) {
                return b.map((t) -> ifPrimary, (u) -> ifSecondary, ifNotPresent);
            }
            return ifNotPresent.get();
        }, this);
    }

    public <S> ObjectBinding<S> flatMap(S ifPrimary, S ifSecondary, ObservableValue<S> ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            S n = ifNotPresent.getValue();
            if (null != b) {
                return b.map((t) -> ifPrimary, (u) -> ifSecondary, () -> n);
            }
            return n;
        }, this);
    }

    public <S> ObjectBinding<S> flatMap(S ifPrimary, S ifSecondary, S ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createObjectBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            if (null != b) {
                return b.map((t) -> ifPrimary, (u) -> ifSecondary, () -> ifNotPresent);
            }
            return ifNotPresent;
        }, this);
    }

    public <S> OptionalBinding<S> map(Function<T, S> ifPrimary, Function<U, S> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return BindingHelper.createOptionalBinding(() -> {
            BinaryOptional<T, U> optional = BinaryOptionalBinding.this.getValue();
            if (null != optional) {
                return optional.map(ifPrimary, ifSecondary);
            }
            return Optional.empty();
        }, this);
    }

    public <S> OptionalBinding<S> map(Function<T, S> ifPrimary, S ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return BindingHelper.createOptionalBinding(() -> {
            BinaryOptional<T, U> optional = BinaryOptionalBinding.this.getValue();
            if (null != optional) {
                return optional.map(ifPrimary, (u) -> ifSecondary);
            }
            return Optional.empty();
        }, this);
    }

    public <S> OptionalBinding<S> map(S ifPrimary, Function<U, S> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return BindingHelper.createOptionalBinding(() -> {
            BinaryOptional<T, U> optional = BinaryOptionalBinding.this.getValue();
            if (null != optional) {
                return optional.map((t) -> ifPrimary, ifSecondary);
            }
            return Optional.empty();
        }, this);
    }

    public <S> OptionalBinding<S> map(S ifPrimary, S ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return BindingHelper.createOptionalBinding(() -> {
            BinaryOptional<T, U> optional = BinaryOptionalBinding.this.getValue();
            if (null != optional) {
                return optional.map((t) -> ifPrimary, (u) -> ifSecondary);
            }
            return Optional.empty();
        }, this);
    }

    public StringBinding mapToString(Function<T, String> ifPrimary, Function<U, String> ifSecondary, Supplier<String> ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createStringBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            if (null != b) {
                return b.map(ifPrimary, ifSecondary, ifNotPresent);
            }
            return ifNotPresent.get();
        }, this);
    }

    public StringBinding mapToString(Function<T, String> ifPrimary, Function<U, String> ifSecondary, ObservableValue<String> ifNotPresent) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        Objects.requireNonNull(ifNotPresent);
        return Bindings.createStringBinding(() -> {
            BinaryOptional<T, U> b = BinaryOptionalBinding.this.get();
            String n = ifNotPresent.getValue();
            if (null != b) {
                return b.map(ifPrimary, ifSecondary, () -> n);
            }
            return n;
        }, this);
    }

    public StringBinding mapToString(Function<T, String> ifPrimary, Function<U, String> ifSecondary, String ifNotPresent) {
        return mapToString(ifPrimary, ifSecondary, () -> ifNotPresent);
    }

    public StringBinding mapToString(Function<T, String> ifPrimary, String ifSecondary, Supplier<String> ifNotPresent) {
        return mapToString(ifPrimary, (u) -> ifSecondary, ifNotPresent);
    }

    public StringBinding mapToString(Function<T, String> ifPrimary, String ifSecondary, ObservableValue<String> ifNotPresent) {
        return mapToString(ifPrimary, (u) -> ifSecondary, ifNotPresent);
    }

    public StringBinding mapToString(Function<T, String> ifPrimary, String ifSecondary, String ifNotPresent) {
        return mapToString(ifPrimary, ifSecondary, () -> ifNotPresent);
    }

    public StringBinding mapToString(String ifPrimary, Function<U, String> ifSecondary, Supplier<String> ifNotPresent) {
        return mapToString((t) -> ifPrimary, ifSecondary, ifNotPresent);
    }

    public StringBinding mapToString(String ifPrimary, Function<U, String> ifSecondary, ObservableValue<String> ifNotPresent) {
        return mapToString((t) -> ifPrimary, ifSecondary, ifNotPresent);
    }

    public StringBinding mapToString(String ifPrimary, Function<U, String> ifSecondary, String ifNotPresent) {
        return mapToString(ifPrimary, ifSecondary, () -> ifNotPresent);
    }

    public StringBinding mapToString(String ifPrimary, String ifSecondary, Supplier<String> ifNotPresent) {
        return mapToString(ifPrimary, (u) -> ifSecondary, ifNotPresent);
    }

    public StringBinding mapToString(String ifPrimary, String ifSecondary, ObservableValue<String> ifNotPresent) {
        return mapToString(ifPrimary, (u) -> ifSecondary, ifNotPresent);
    }

    public StringBinding mapToString(String ifPrimary, String ifSecondary, String ifNotPresent) {
        return mapToString(ifPrimary, ifSecondary, () -> ifNotPresent);
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
