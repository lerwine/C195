
package scheduler.observables;

import scheduler.util.ThrowableFunction;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleObjectProperty;
import scheduler.util.ThrowableBiFunction;

/**
 *
 * @author erwinel
 * @param <T>
 */
public class OptionalValueProperty<T> extends SimpleObjectProperty<Optional<T>> {

    private final ReadOnlyBooleanWrapper present;

    public final boolean isPresent() { return present.get(); }

    public final ReadOnlyBooleanProperty presentProperty() { return present.getReadOnlyProperty(); }

    public final void ifPresent(Consumer<? super T> consumer) { get().ifPresent(consumer); }
    
    public final <R> R fromPresence(Function<? super T, R> ifPresent, Supplier<R> notPresent) {
        Optional<T> value = get();
        if (value.isPresent())
            return ifPresent.apply(value.get());
        return notPresent.get();
    }
    
    public final <R, E extends Exception> R withPresenceOrDefault(ThrowableFunction<? super T, R, E> function, R defaultValue) throws E {
        Optional<T> value = get();
        if (value.isPresent())
            return function.apply(value.get());
        return defaultValue;
    }
    
    public final <U, R, E extends Exception> R fromPresentOrDefault(U u, ThrowableBiFunction<? super T, U, R, E> function, R defaultValue) throws E {
        Optional<T> value = get();
        if (value.isPresent())
            return function.apply(value.get(), u);
        return defaultValue;
    }
    
    public OptionalValueProperty() {
        super(Optional.empty());
        present = new ReadOnlyBooleanWrapper(false);
    }

    public OptionalValueProperty(Optional<T> initialValue) {
        super(Objects.requireNonNull(initialValue, "Initial value cannot be null"));
        present = new ReadOnlyBooleanWrapper(get().isPresent());
    }

    public OptionalValueProperty(Object bean, String name) {
        super(bean, name, Optional.empty());
        present = new ReadOnlyBooleanWrapper(false);
    }

    public OptionalValueProperty(Object bean, String name, Optional<T> initialValue) {
        super(bean, name, Objects.requireNonNull(initialValue, "Initial value cannot be null"));
        present = new ReadOnlyBooleanWrapper(get().isPresent());
    }

    @Override
    public void set(Optional<T> newValue) {
        try { super.set(Objects.requireNonNull(newValue, "Value cannot be null")); }
        finally { present.set(get().isPresent()); }
    }
}
