package scheduler.observables;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.StringBinding;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import scheduler.util.BinarySelective;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 */
public abstract class BinarySelectiveBinding<T, U> extends ObjectBinding<BinarySelective<T, U>> {

    private final ObservableList<Observable> dependencies;
    private final ObservableList<Observable> readOnlyDependencies;

    protected BinarySelectiveBinding(Observable... dependencies) {
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
            BinarySelective<T, U> b = BinarySelectiveBinding.this.get();
            return null != b && b.isPrimary();
        }, this);
    }

    public <S> ObjectBinding<S> map(Function<T, S> ifPrimary, Function<U, S> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return Bindings.createObjectBinding(() -> {
            return BinarySelectiveBinding.this.get().map(ifPrimary, ifSecondary);
        }, this);
    }

    public <S> ObjectBinding<S> map(Function<T, S> ifPrimary, ObservableValue<S> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return Bindings.createObjectBinding(() -> {
            S s = ifSecondary.getValue();
            return BinarySelectiveBinding.this.get().map(ifPrimary, (u) -> s);
        }, this, ifSecondary);
    }

    public <S> ObjectBinding<S> map(Function<T, S> ifPrimary, S ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        return Bindings.createObjectBinding(() -> {
            return BinarySelectiveBinding.this.get().map(ifPrimary, (u) -> ifSecondary);
        }, this);
    }

    public <S> ObjectBinding<S> map(ObservableValue<S> ifPrimary, Function<U, S> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return Bindings.createObjectBinding(() -> {
            S p = ifPrimary.getValue();
            return BinarySelectiveBinding.this.get().map((t) -> p, ifSecondary);
        }, this, ifPrimary);
    }

    public <S> ObjectBinding<S> map(ObservableValue<S> ifPrimary, ObservableValue<S> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return Bindings.createObjectBinding(() -> {
            S p = ifPrimary.getValue();
            S s = ifSecondary.getValue();
            return BinarySelectiveBinding.this.get().map((t) -> p, (u) -> s);
        }, this, ifPrimary, ifSecondary);
    }

    public <S> ObjectBinding<S> map(ObservableValue<S> ifPrimary, S ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        return Bindings.createObjectBinding(() -> {
            S p = ifPrimary.getValue();
            return BinarySelectiveBinding.this.get().map((t) -> p, (u) -> ifSecondary);
        }, this, ifPrimary);
    }

    public <S> ObjectBinding<S> map(S ifPrimary, Function<U, S> ifSecondary) {
        Objects.requireNonNull(ifSecondary);
        return Bindings.createObjectBinding(() -> {
            return BinarySelectiveBinding.this.get().map((t) -> ifPrimary, ifSecondary);
        }, this);
    }

    public <S> ObjectBinding<S> map(S ifPrimary, ObservableValue<S> ifSecondary) {
        Objects.requireNonNull(ifSecondary);
        return Bindings.createObjectBinding(() -> {
            S s = ifSecondary.getValue();
            return BinarySelectiveBinding.this.get().map((t) -> ifPrimary, (u) -> s);
        }, this);
    }

    public <S> ObjectBinding<S> map(S ifPrimary, S ifSecondary) {
        return Bindings.createObjectBinding(() -> {
            return BinarySelectiveBinding.this.get().map((t) -> ifPrimary, (u) -> ifSecondary);
        }, this);
    }

    public StringBinding mapToString(Function<T, String> ifPrimary, Function<U, String> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return Bindings.createStringBinding(() -> {
            return BinarySelectiveBinding.this.get().map(ifPrimary, ifSecondary);
        }, this);
    }

    public StringBinding mapToString(Function<T, String> ifPrimary, ObservableValue<String> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return Bindings.createStringBinding(() -> {
            String s = ifSecondary.getValue();
            return BinarySelectiveBinding.this.get().map(ifPrimary, (u) -> s);
        }, this, ifSecondary);
    }

    public StringBinding mapToString(Function<T, String> ifPrimary, String ifSecondary) {
        return mapToString(ifPrimary, (u) -> ifSecondary);
    }

    public StringBinding mapToString(ObservableValue<String> ifPrimary, Function<U, String> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return Bindings.createStringBinding(() -> {
            String p = ifPrimary.getValue();
            return BinarySelectiveBinding.this.get().map((t) -> p, ifSecondary);
        }, this, ifPrimary);
    }

    public StringBinding mapToString(ObservableValue<String> ifPrimary, ObservableValue<String> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return Bindings.createStringBinding(() -> {
            String p = ifPrimary.getValue();
            String s = ifSecondary.getValue();
            return BinarySelectiveBinding.this.get().map((t) -> p, (u) -> s);
        }, this, ifPrimary, ifSecondary);
    }

    public StringBinding mapToString(ObservableValue<String> ifPrimary, String ifSecondary) {
        return mapToString(ifPrimary, (u) -> ifSecondary);
    }

    public StringBinding mapToString(String ifPrimary, Function<U, String> ifSecondary) {
        return mapToString((t) -> ifPrimary, ifSecondary);
    }

    public StringBinding mapToString(String ifPrimary, ObservableValue<String> ifSecondary) {
        return mapToString((t) -> ifPrimary, ifSecondary);
    }

    public StringBinding mapToString(String ifPrimary, String ifSecondary) {
        return mapToString(ifPrimary, (u) -> ifSecondary);
    }

    public BooleanBinding mapToBoolean(Predicate<T> ifPrimary, Predicate<U> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return Bindings.createBooleanBinding(() -> {
            return BinarySelectiveBinding.this.get().toBoolean(ifPrimary, ifSecondary);
        }, this);
    }

    public BooleanBinding mapToBoolean(Predicate<T> ifPrimary, ObservableValue<Boolean> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return Bindings.createBooleanBinding(() -> {
            Boolean s = ifSecondary.getValue();
            return BinarySelectiveBinding.this.get().toBoolean(ifPrimary, (u) -> s);
        }, this, ifSecondary);
    }

    public BooleanBinding mapToBoolean(Predicate<T> ifPrimary, boolean ifSecondary) {
        return mapToBoolean(ifPrimary, (u) -> ifSecondary);
    }

    public BooleanBinding mapToBoolean(ObservableValue<Boolean> ifPrimary, Predicate<U> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return Bindings.createBooleanBinding(() -> {
            Boolean p = ifPrimary.getValue();
            return BinarySelectiveBinding.this.get().toBoolean((t) -> p, ifSecondary);
        }, this, ifPrimary);
    }

    public BooleanBinding mapToBoolean(ObservableValue<Boolean> ifPrimary, ObservableValue<Boolean> ifSecondary) {
        Objects.requireNonNull(ifPrimary);
        Objects.requireNonNull(ifSecondary);
        return Bindings.createBooleanBinding(() -> {
            Boolean p = ifPrimary.getValue();
            Boolean s = ifSecondary.getValue();
            return BinarySelectiveBinding.this.get().toBoolean((t) -> p, (u) -> s);
        }, this, ifPrimary, ifSecondary);
    }

    public BooleanBinding mapToBoolean(ObservableValue<Boolean> ifPrimary, boolean ifSecondary) {
        return mapToBoolean(ifPrimary, (u) -> ifSecondary);
    }

    public BooleanBinding mapToBoolean(boolean ifPrimary, Predicate<U> ifSecondary) {
        return mapToBoolean((t) -> ifPrimary, ifSecondary);
    }

    public BooleanBinding mapToBoolean(boolean ifPrimary, ObservableValue<Boolean> ifSecondary) {
        return mapToBoolean((t) -> ifPrimary, ifSecondary);
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
