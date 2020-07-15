package scheduler.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An object that contains exactly one of 2 possible value options.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The primary value type.
 * @param <U> The secondary value type.
 */
public final class BinarySelective<T, U> {

    /**
     * Create a new {@code BinarySelective} object that contains the primary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param t The primary value.
     * @return A {@code BinarySelective} object that contains the primary value option.
     */
    public static <T, U> BinarySelective<T, U> ofPrimary(T t) {
        return new BinarySelective<>(Objects.requireNonNull(t), true);
    }

    /**
     * Create a new {@code BinarySelective} object that contains a null primary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param t The primary value.
     * @return A {@code BinarySelective} object that contains a null primary value option.
     */
    public static <T, U> BinarySelective<T, U> ofPrimaryNullable(T t) {
        return new BinarySelective<>(t, true);
    }

    /**
     * Create a new {@code BinarySelective} object that contains the secondary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param u The secondary value.
     * @return A {@code BinarySelective} object that contains the secondary value option.
     */
    public static <T, U> BinarySelective<T, U> ofSecondary(U u) {
        return new BinarySelective<>(Objects.requireNonNull(u), false);
    }

    /**
     * Create a new {@code BinarySelective} object that contains a nullable secondary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param u The secondary value.
     * @return A {@code BinarySelective} object that contains a nullable secondary value option.
     */
    public static <T, U> BinarySelective<T, U> ofSecondaryNullable(U u) {
        return new BinarySelective<>(u, false);
    }

    private final Object value;
    private final boolean primary;

    private BinarySelective(Object obj, boolean primary) {
        value = obj;
        this.primary = primary;
    }

    /**
     * Indicates whether the current object contains the primary value option.
     *
     * @return {@code true} if this object contains the primary value option; otherwise, {@code false} to indicate that this object contains the
     * secondary value option.
     */
    public boolean isPrimary() {
        return primary;
    }

    /**
     * Gets the value of the primary option. This will only return a value if {@link #getPrimary()} is {@code true}.
     *
     * @return The primary value;
     * @throws NoSuchElementException if this object does not contain the primary value option.
     */
    @SuppressWarnings("unchecked")
    public T getPrimary() {
        if (!primary) {
            throw new NoSuchElementException("No value present");
        }
        return (T) value;
    }

    /**
     * Gets the value of the secondary option. This will only return a value if {@link #getPrimary()} is {@code false}.
     *
     * @return The secondary value;
     * @throws NoSuchElementException if this object does not contain the secondary value option.
     */
    @SuppressWarnings("unchecked")
    public U getSecondary() {
        if (primary) {
            throw new NoSuchElementException("No value present");
        }
        return (U) value;
    }

    @SuppressWarnings("unchecked")
    public Optional<T> toPrimaryOption() {
        return (primary) ? Optional.of((T) value) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    public Optional<U> toSecondaryOption() {
        return (primary) ? Optional.empty() : Optional.of((U) value);
    }

    /**
     * If this contains the primary value option, invoke the specified consumer with the value; otherwise, do nothing. This is similar to
     * {@link Optional#ifPresent(Consumer)}.
     *
     * @param consumer The {@link Consumer} to invoke if this contains the primary value option,.
     */
    @SuppressWarnings("unchecked")
    public void ifPrimary(Consumer<? super T> consumer) {
        if (primary) {
            consumer.accept((T) value);
        }
    }

    /**
     * If this contains the secondary value option, invoke the specified consumer with the value; otherwise, do nothing. This is similar to
     * {@link Optional#ifPresent(Consumer)}.
     *
     * @param consumer The {@link Consumer} to invoke if this contains the secondary value option.
     */
    @SuppressWarnings("unchecked")
    public void ifSecondary(Consumer<? super U> consumer) {
        if (!primary) {
            consumer.accept((U) value);
        }
    }

    /**
     * Invokes a {@link Consumer} according to the option value being stored.
     *
     * @param ifPrimary The {@link Consumer} to execute if this contains the primary option value.
     * @param ifSecondary The {@link Consumer} to execute if this contains the secondary option value.
     */
    @SuppressWarnings("unchecked")
    public void accept(Consumer<? super T> ifPrimary, Consumer<? super U> ifSecondary) {
        if (this.primary) {
            ifPrimary.accept((T) value);
        } else {
            ifSecondary.accept((U) value);
        }
    }

    @SuppressWarnings("unchecked")
    public T asPrimary(Function<? super U, T> ifSecondary) {
        if (this.primary)
            return (T) value;
        return ifSecondary.apply((U) value);
    }
    
    @SuppressWarnings("unchecked")
    public T toPrimary(T ifSecondary) {
        if (this.primary)
            return (T) value;
        return ifSecondary;
    }
    
    @SuppressWarnings("unchecked")
    public U asSecondary(Function<? super T, U> ifPrimary) {
        if (this.primary)
            return ifPrimary.apply((T) value);
        return (U) value;
    }
    
    
    @SuppressWarnings("unchecked")
    public U toSecondary(U ifPrimary) {
        if (this.primary)
            return ifPrimary;
        return (U) value;
    }
    
    /**
     * Applies a {@link Function} according to the option value being stored.
     *
     * @param <S> The type of value to be returned.
     * @param primary The {@link Function} to apply if this contains the primary option value.
     * @param secondary The {@link Function} to apply if this contains the secondary option value.
     * @return The result from the {@link Function} that was applied.
     */
    @SuppressWarnings("unchecked")
    public <S> S map(Function<? super T, S> primary, Function<? super U, S> secondary) {
        if (this.primary) {
            return primary.apply((T) value);
        }
        return secondary.apply((U) value);
    }

    @SuppressWarnings("unchecked")
    public <S> BinarySelective<S, U> mapPrimary(Function<? super T, S> ifPrimary) {
        if (this.primary) {
            return BinarySelective.ofPrimary(ifPrimary.apply((T) value));
        }
        return BinarySelective.ofSecondary((U) value);
    }

    @SuppressWarnings("unchecked")
    public <S> BinarySelective<T, S> mapSecondary(Function<? super U, S> ifSecondary) {
        if (this.primary) {
            return BinarySelective.ofPrimary((T) value);
        }
        return BinarySelective.ofSecondary(ifSecondary.apply((U) value));
    }

    /**
     * Applies a {@link Predicate} according to the option value being stored.
     *
     * @param primary The {@link Predicate} to test if this contains the primary option value.
     * @param secondary The {@link Predicate} to test if this contains the secondary option value.
     * @return The result from the {@link Predicate} that was applied.
     */
    @SuppressWarnings("unchecked")
    public boolean toBoolean(Predicate<? super T> primary, Predicate<? super U> secondary) {
        if (this.primary) {
            return primary.test((T) value);
        }
        return secondary.test((U) value);
    }

    /**
     * Creates a {@code BinarySelective} value by swapping primary and secondary options.
     *
     * @return A new {@code BinarySelective} value with secondary and tertiary option values swapped.
     */
    public BinarySelective<U, T> shift() {
        return new BinarySelective<>(value, !primary);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(value);
        if (primary) {
            hash = 37 * hash + 1;
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (null != obj && obj instanceof BinarySelective) {
            BinarySelective<?, ?> other = (BinarySelective<?, ?>) obj;
            return primary == other.primary && Objects.equals(value, other.value);
        }
        return false;
    }

    @Override
    public String toString() {
        if (primary) {
            return (null == value) ? "BinarySwitch.PRIMARY" : String.format("BinarySwitch.PRIMARY[%s]", value);
        }
        return (null == value) ? "BinarySwitch.SECONDARY" : String.format("BinarySwitch.SECONDARY[%s]", value);
    }

}
