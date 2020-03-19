package scheduler.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 *
 * @author lerwi
 */
class TernaryOption<T, S, U> {

    public static <T, S, U> TernaryOption<T, S, U> ofPrimary(T t) {
        return new TernaryOption<>(Objects.requireNonNull(t), Optional.of(true));
    }

    public static <T, S, U> TernaryOption<T, S, U> ofSecondary(S s) {
        return new TernaryOption<>(Objects.requireNonNull(s), Optional.of(false));
    }

    public static <T, S, U> TernaryOption<T, S, U> ofTertiary(U u) {
        return new TernaryOption<>(Objects.requireNonNull(u), Optional.of(false));
    }

    private final Object value;
    private final Optional<Boolean> primary;

    private TernaryOption(Object obj, Optional<Boolean> primary) {
        value = obj;
        this.primary = primary;
    }

    public boolean isPrimary() {
        return primary.isPresent() && primary.get();
    }

    public boolean isSecondary() {
        return primary.isPresent() && !primary.get();
    }

    public boolean isTertiary() {
        return !primary.isPresent();
    }

    public T getPrimary() {
        if (!isPrimary()) {
            throw new NoSuchElementException("No value present");
        }
        return (T) value;
    }

    public S getSecondary() {
        if (!isSecondary()) {
            throw new NoSuchElementException("No value present");
        }
        return (S) value;
    }

    public U getTertiary() {
        if (primary.isPresent()) {
            throw new NoSuchElementException("No value present");
        }
        return (U) value;
    }

    public void ifPrimary(Consumer<? super T> consumer) {
        if (isPrimary()) {
            consumer.accept((T) value);
        }
    }

    public void ifSecondary(Consumer<? super S> consumer) {
        if (isSecondary()) {
            consumer.accept((S) value);
        }
    }

    public void ifTertiary(Consumer<? super U> consumer) {
        if (!primary.isPresent()) {
            consumer.accept((U) value);
        }
    }

    public void accept(Consumer<? super T> primary, Consumer<? super S> secondary, Consumer<? super U> tertiary) {
        if (this.primary.isPresent()) {
            if (this.primary.get()) {
                primary.accept((T) value);
            } else {
                secondary.accept((S) value);
            }
        } else {
            tertiary.accept((U) value);
        }
    }

    public <V> V map(Function<? super T, V> primary, Function<? super S, V> secondary, Function<? super U, V> tertiary) {
        if (this.primary.isPresent()) {
            if (this.primary.get()) {
                return primary.apply((T) value);
            }
            return secondary.apply((S) value);
        }
        return tertiary.apply((U) value);
    }

    public <S, U, T> TernaryOption<T, S, U> shiftLeft() {
        return new TernaryOption<>(value, (primary.isPresent()) ? ((primary.get()) ? Optional.empty() : Optional.of(true)) : Optional.of(false));
    }

    public <U, T, S> TernaryOption<T, S, U> shiftRight() {
        return new TernaryOption<>(value, (primary.isPresent()) ? ((primary.get()) ? Optional.of(false) : Optional.empty()) : Optional.of(true));
    }

    public <S, T, U> TernaryOption<T, S, U> fromSecondary() {
        return new TernaryOption<>(value, (primary.isPresent()) ? ((primary.get()) ? Optional.of(false) : Optional.of(true)) : Optional.empty());
    }

    public <U, S, T> TernaryOption<T, S, U> fromTertiary() {
        return new TernaryOption<>(value, (primary.isPresent()) ? ((primary.get()) ? Optional.empty() : Optional.of(false)) : Optional.of(true));
    }

}
