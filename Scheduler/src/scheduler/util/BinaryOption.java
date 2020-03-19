package scheduler.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author lerwi
 * @param <T>
 * @param <S>
 */
public final class BinaryOption<T, S> {

    public static <T, S> BinaryOption<T, S> ofPrimary(T t) {
        return new BinaryOption<>(Objects.requireNonNull(t), true);
    }

    public static <T, S> BinaryOption<T, S> ofSecondary(S s) {
        return new BinaryOption<>(Objects.requireNonNull(s), false);
    }

    private final Object value;
    private final boolean primary;

    private BinaryOption(Object obj, boolean primary) {
        value = obj;
        this.primary = primary;
    }

    public boolean isPrimary() {
        return primary;
    }

    public T getPrimary() {
        if (!primary) {
            throw new NoSuchElementException("No value present");
        }
        return (T) value;
    }

    public S getSecondary() {
        if (primary) {
            throw new NoSuchElementException("No value present");
        }
        return (S) value;
    }

    public void ifPrimary(Consumer<? super T> consumer) {
        if (primary) {
            consumer.accept((T) value);
        }
    }

    public void ifSecondary(Consumer<? super S> consumer) {
        if (!primary) {
            consumer.accept((S) value);
        }
    }

    public void accept(Consumer<? super T> primary, Consumer<? super S> secondary) {
        if (this.primary) {
            primary.accept((T) value);
        } else {
            secondary.accept((S) value);
        }
    }

    public <U> U map(Function<? super T, U> primary, Function<? super S, U> secondary) {
        if (this.primary) {
            return primary.apply((T) value);
        }
        return secondary.apply((S) value);
    }

    public BinaryOption<S, T> shift() {
        return new BinaryOption<>(value, !primary);
    }

}
