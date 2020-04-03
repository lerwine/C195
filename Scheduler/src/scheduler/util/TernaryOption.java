package scheduler.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An object that can contain only one of 3 possible value options.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The primary value type.
 * @param <S> The secondary value type.
 * @param <U> The tertiary value type.
 */
public final class TernaryOption<T, S, U> {

    /**
     * Create a new TernaryOption object that contains the primary value option.
     *
     * @param <T> The primary value type.
     * @param <S> The secondary value type.
     * @param <U> The tertiary value type.
     * @param t The primary value.
     * @return A TernaryOption object that contains the primary value option.
     */
    public static <T, S, U> TernaryOption<T, S, U> ofPrimary(T t) {
        return new TernaryOption<>(Objects.requireNonNull(t), Optional.of(true));
    }

    /**
     * Create a new TernaryOption object that contains the secondary value option.
     *
     * @param <T> The primary value type.
     * @param <S> The secondary value type.
     * @param <U> The tertiary value type.
     * @param s The secondary value.
     * @return A TernaryOption object that contains the secondary value option.
     */
    public static <T, S, U> TernaryOption<T, S, U> ofSecondary(S s) {
        return new TernaryOption<>(Objects.requireNonNull(s), Optional.of(false));
    }

    /**
     * Create a new TernaryOption object that contains the tertiary value option.
     *
     * @param <T> The primary value type.
     * @param <S> The secondary value type.
     * @param <U> The tertiary value type.
     * @param u The tertiary value.
     * @return A TernaryOption object that contains the tertiary value option.
     */
    public static <T, S, U> TernaryOption<T, S, U> ofTertiary(U u) {
        return new TernaryOption<>(Objects.requireNonNull(u), Optional.of(false));
    }

    private final Object value;
    /**
     * Indicates the value option being stored: {@code true}=value is primary; {@code false}=value is secondary; {@link Optional#empty()}=tertiary.
     */
    private final Optional<Boolean> primary;

    private TernaryOption(Object obj, Optional<Boolean> primary) {
        value = obj;
        this.primary = primary;
    }

    /**
     * Indicates whether the current object contains the primary value option. If this returns {@code true}, then both {@link #isSecondary()} and
     * {@link #isTertiary()} will return {@code false}.
     *
     * @return {@code true} if this object contains the primary value option; otherwise, {@code false} to indicate that calling {@link #getPrimary()}
     * would thrown a {@link NoSuchElementException}.
     */
    public boolean isPrimary() {
        return primary.isPresent() && primary.get();
    }

    /**
     * Indicates whether the current object contains the secondary value option. If this returns {@code true}, then both {@link #isPrimary()} and
     * {@link #isTertiary()} will return {@code false}.
     *
     * @return {@code true} if this object contains the secondary value option; otherwise, {@code false} to indicate that calling
     * {@link #getSecondary()} would thrown a {@link NoSuchElementException}.
     */
    public boolean isSecondary() {
        return primary.isPresent() && !primary.get();
    }

    /**
     * Indicates whether the current object contains the tertiary value option. If this returns {@code true}, then both {@link #isPrimary()} and
     * {@link #isSecondary()} will return {@code false}.
     *
     * @return {@code true} if this object contains the tertiary value option; otherwise, {@code false} to indicate that calling
     * {@link #getTertiary()} would thrown a {@link NoSuchElementException}.
     */
    public boolean isTertiary() {
        return !primary.isPresent();
    }

    /**
     * Gets the value of the primary option. This will only return a value if {@link #getPrimary()} is {@code true}.
     *
     * @return The primary value;
     * @throws NoSuchElementException if this object does not contain the primary value option.
     */
    public T getPrimary() {
        if (!isPrimary()) {
            throw new NoSuchElementException("No value present");
        }
        return (T) value;
    }

    /**
     * Gets the value of the secondary option. This will only return a value if {@link #isSecondary()} is {@code true}.
     *
     * @return The secondary value;
     * @throws NoSuchElementException if this object does not contain the secondary value option.
     */
    public S getSecondary() {
        if (!isSecondary()) {
            throw new NoSuchElementException("No value present");
        }
        return (S) value;
    }

    /**
     * Gets the value of the tertiary option. This will only return a value if {@link #isTertiary()} is {@code true}.
     *
     * @return The tertiary value;
     * @throws NoSuchElementException if this object does not contain the tertiary value option.
     */
    public U getTertiary() {
        if (primary.isPresent()) {
            throw new NoSuchElementException("No value present");
        }
        return (U) value;
    }

    /**
     * If this contains the primary value option, invoke the specified consumer with the value; otherwise, do nothing. This is similar to
     * {@link Optional#ifPresent(Consumer)}.
     *
     * @param consumer The {@link Consumer} to invoke if this contains the primary value option,.
     */
    public void ifPrimary(Consumer<? super T> consumer) {
        if (isPrimary()) {
            consumer.accept((T) value);
        }
    }

    /**
     * If this contains the secondary value option, invoke the specified consumer with the value; otherwise, do nothing. This is similar to
     * {@link Optional#ifPresent(Consumer)}.
     *
     * @param consumer The {@link Consumer} to invoke if this contains the secondary value option.
     */
    public void ifSecondary(Consumer<? super S> consumer) {
        if (isSecondary()) {
            consumer.accept((S) value);
        }
    }

    /**
     * If this contains the tertiary value option, invoke the specified consumer with the value; otherwise, do nothing. This is similar to
     * {@link Optional#ifPresent(Consumer)}.
     *
     * @param consumer The {@link Consumer} to invoke if this contains the tertiary value option.
     */
    public void ifTertiary(Consumer<? super U> consumer) {
        if (!primary.isPresent()) {
            consumer.accept((U) value);
        }
    }

    /**
     * Invokes a {@link Consumer} according to the option value being stored.
     *
     * @param ifPrimary The {@link Consumer} to execute if this contains the primary option value.
     * @param ifSecondary The {@link Consumer} to execute if this contains the secondary option value.
     * @param ifTertiary The {@link Consumer} to execute if this contains the tertiary option value.
     */
    public void accept(Consumer<? super T> ifPrimary, Consumer<? super S> ifSecondary, Consumer<? super U> ifTertiary) {
        if (this.primary.isPresent()) {
            if (this.primary.get()) {
                ifPrimary.accept((T) value);
            } else {
                ifSecondary.accept((S) value);
            }
        } else {
            ifTertiary.accept((U) value);
        }
    }

    /**
     * Applies a {@link Function} according to the option value being stored.
     *
     * @param <V> The type of value to be returned.
     * @param primary The {@link Function} to apply if this contains the primary option value.
     * @param secondary The {@link Function} to apply if this contains the secondary option value.
     * @param tertiary The {@link Function} to apply if this contains the tertiary option value.
     * @return The result from the {@link Function} that was applied.
     */
    public <V> V map(Function<? super T, V> primary, Function<? super S, V> secondary, Function<? super U, V> tertiary) {
        if (this.primary.isPresent()) {
            if (this.primary.get()) {
                return primary.apply((T) value);
            }
            return secondary.apply((S) value);
        }
        return tertiary.apply((U) value);
    }

    /**
     * Creates a {@code TernaryOption} value by promoting secondary and tertiary values upward and primary to tertiary.
     * <ul>
     * <li>If the original value was the primary value, it will be the tertiary value in the result object.</li>
     * <li>If the original value was the secondary value, it will be the primary value in the result object.</li>
     * <li>If the original value was the tertiary value, it will be the secondary value in the result object.</li>
     * </ul>
     *
     * @return A new {@code TernaryOption} value by promoting secondary and tertiary values upward and primary to tertiary.
     */
    public TernaryOption<S, U, T> shiftUp() {
        return new TernaryOption<>(value, (primary.isPresent()) ? ((primary.get()) ? Optional.empty() : Optional.of(true)) : Optional.of(false));
    }

    /**
     * Creates a {@code TernaryOption} value by promoting primary and secondary option values upward and tertiary to primary.
     * <ul>
     * <li>If the original value was the primary option, it will represent the secondary option in the result object.</li>
     * <li>If the original value was the secondary option, it will represent the tertiary option in the result object.</li>
     * <li>If the original value was the tertiary option, it will represent the primary option in the result object.</li>
     * </ul>
     *
     * @return A new {@code TernaryOption} value with promoting secondary and tertiary option values promoted upward and primary to tertiary.
     */
    public TernaryOption<U, T, S> shiftDown() {
        return new TernaryOption<>(value, (primary.isPresent()) ? ((primary.get()) ? Optional.of(false) : Optional.empty()) : Optional.of(true));
    }

    /**
     * Creates a {@code TernaryOption} value by swapping primary and secondary options.
     * <ul>
     * <li>If the original value was the primary option, it will represent the secondary option in the result object.</li>
     * <li>If the original value was the secondary option, it will represent the primary option in the result object.</li>
     * <li>If the original value was the tertiary option, then it will still represent the tertiary option in the result object.</li>
     * </ul>
     *
     * @return A new {@code TernaryOption} value with secondary and tertiary option values swapped.
     */
    public TernaryOption<S, T, U> fromSecondary() {
        return new TernaryOption<>(value, (primary.isPresent()) ? ((primary.get()) ? Optional.of(false) : Optional.of(true)) : Optional.empty());
    }

    /**
     * Creates a {@code TernaryOption} value by swapping primary and tertiary options.
     * <ul>
     * <li>If the original value was the primary option, it will represent the tertiary option in the result object.</li>
     * <li>If the original value was the secondary option, then it will still represent the secondary option in the result object.</li>
     * <li>If the original value was the tertiary option, it will represent the primary option in the result object.</li>
     * </ul>
     *
     * @return A new {@code TernaryOption} value with secondary and tertiary option values swapped.
     */
    public TernaryOption<U, S, T> fromTertiary() {
        return new TernaryOption<>(value, (primary.isPresent()) ? ((primary.get()) ? Optional.empty() : Optional.of(false)) : Optional.of(true));
    }

}
