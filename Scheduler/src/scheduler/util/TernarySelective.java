package scheduler.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An object that contains only one of 3 possible value options.
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 * @param <T> The primary value type.
 * @param <U> The secondary value type.
 * @param <S> The tertiary value type.
 */
public final class TernarySelective<T, U, S> {

    /**
     * Create a new {@code TernarySelective} object that contains the primary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param t The primary value.
     * @return A {@code TernarySelective} object that contains the primary value option.
     * @throws NullPointerException if {@code t} is null.
     */
    public static <T, U, S> TernarySelective<T, U, S> ofPrimary(T t) {
        return new TernarySelective<>(Objects.requireNonNull(t), Optional.of(true));
    }

    /**
     * Create a new {@code TernarySelective} object that contains a null primary value option value.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param t The primary value.
     * @return A {@code TernarySelective} object that contains a null primary value option.
     */
    public static <T, U, S> TernarySelective<T, U, S> ofPrimaryNullable(T t) {
        return new TernarySelective<>(t, Optional.of(true));
    }

    /**
     * Create a new {@code TernarySelective} object that contains the secondary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param u The secondary value.
     * @return A {@code TernarySelective} object that contains the secondary value option.
     * @throws NullPointerException if {@code u} is null.
     */
    public static <T, U, S> TernarySelective<T, U, S> ofSecondary(U u) {
        return new TernarySelective<>(Objects.requireNonNull(u), Optional.of(false));
    }

    /**
     * Create a new {@code TernarySelective} object that contains a null secondary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param u The secondary value.
     * @return A {@code TernarySelective} object that contains a null secondary value option.
     */
    public static <T, U, S> TernarySelective<T, U, S> ofSecondaryNullable(U t) {
        return new TernarySelective<>(t, Optional.of(false));
    }

    /**
     * Create a new {@code TernarySelective} object that contains the tertiary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param s The tertiary value.
     * @return A {@code TernarySelective} object that contains the tertiary value option.
     * @throws NullPointerException if {@code s} is null.
     */
    public static <T, U, S> TernarySelective<T, U, S> ofTertiary(S s) {
        return new TernarySelective<>(Objects.requireNonNull(s), Optional.of(false));
    }

    /**
     * Create a new {@code TernarySelective} object that contains a null tertiary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param s The tertiary value.
     * @return A {@code TernarySelective} object that contains a null tertiary value option.
     */
    public static <T, U, S> TernarySelective<T, U, S> ofTertiaryNullable(S s) {
        return new TernarySelective<>(s, Optional.of(false));
    }

    private final Object value;
    /**
     * Indicates the value option being stored: {@code true}=value is primary; {@code false}=value is secondary; {@link Optional#empty()}=tertiary.
     */
    private final Optional<Boolean> optionSpec;

    /**
     * Initializes a new {@code TernarySwitch} object.
     * <p>{@link #optionSpec} value definitions:
     * <dl>
     * <dt>{@code true}</dt>
     * <dd>{@link #value} contains the primary option value.</dd>
     * <dt>{@code false}</dt>
     * <dd>{@link #value} contains the secondary option value.</dd>
     * <dt>{@code empty}</dt>
     * <dd>{@link #value} contains the tertiary option value.</dd>
     * </dl></p>
     * 
     * @param obj The stored value.
     * @param optionSpec The optional specification.
     */
    private TernarySelective(Object obj, Optional<Boolean> optionSpec) {
        value = obj;
        this.optionSpec = optionSpec;
    }

    /**
     * Indicates whether the current object contains the primary value option. If this returns {@code true}, then both {@link #isSecondary()} and
     * {@link #isTertiary()} will return {@code false}.
     *
     * @return {@code true} if this object contains the primary value option; otherwise, {@code false} to indicate that calling {@link #getPrimary()}
     * would thrown a {@link NoSuchElementException}.
     */
    public boolean isPrimary() {
        return optionSpec.isPresent() && optionSpec.get();
    }

    /**
     * Indicates whether the current object contains the secondary value option. If this returns {@code true}, then both {@link #isPrimary()} and
     * {@link #isTertiary()} will return {@code false}.
     *
     * @return {@code true} if this object contains the secondary value option; otherwise, {@code false} to indicate that calling
     * {@link #getSecondary()} would thrown a {@link NoSuchElementException}.
     */
    public boolean isSecondary() {
        return optionSpec.isPresent() && !optionSpec.get();
    }

    /**
     * Indicates whether the current object contains the tertiary value option. If this returns {@code true}, then both {@link #isPrimary()} and
     * {@link #isSecondary()} will return {@code false}.
     *
     * @return {@code true} if this object contains the tertiary value option; otherwise, {@code false} to indicate that calling
     * {@link #getTertiary()} would thrown a {@link NoSuchElementException}.
     */
    public boolean isTertiary() {
        return !optionSpec.isPresent();
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
    public U getSecondary() {
        if (!isSecondary()) {
            throw new NoSuchElementException("No value present");
        }
        return (U) value;
    }

    /**
     * Gets the value of the tertiary option. This will only return a value if {@link #isTertiary()} is {@code true}.
     *
     * @return The tertiary value;
     * @throws NoSuchElementException if this object does not contain the tertiary value option.
     */
    public S getTertiary() {
        if (optionSpec.isPresent()) {
            throw new NoSuchElementException("No value present");
        }
        return (S) value;
    }

    public Optional<T> toPrimaryOption() {
        return (isPrimary()) ? Optional.of((T) value) : Optional.empty();
    }
    
    public Optional<U> toSecondaryOption() {
        return (isSecondary()) ? Optional.of((U) value) : Optional.empty();
    }
    
    public Optional<S> toTertiaryOption() {
        return (isTertiary()) ? Optional.of((S) value) : Optional.empty();
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
    public void ifSecondary(Consumer<? super U> consumer) {
        if (isSecondary()) {
            consumer.accept((U) value);
        }
    }

    /**
     * If this contains the tertiary value option, invoke the specified consumer with the value; otherwise, do nothing. This is similar to
     * {@link Optional#ifPresent(Consumer)}.
     *
     * @param consumer The {@link Consumer} to invoke if this contains the tertiary value option.
     */
    public void ifTertiary(Consumer<? super S> consumer) {
        if (!optionSpec.isPresent()) {
            consumer.accept((S) value);
        }
    }

    /**
     * Invokes a {@link Consumer} according to the option value being stored.
     *
     * @param ifPrimary The {@link Consumer} to execute if this contains the primary option value.
     * @param ifSecondary The {@link Consumer} to execute if this contains the secondary option value.
     * @param ifTertiary The {@link Consumer} to execute if this contains the tertiary option value.
     */
    public void accept(Consumer<? super T> ifPrimary, Consumer<? super U> ifSecondary, Consumer<? super S> ifTertiary) {
        if (this.optionSpec.isPresent()) {
            if (this.optionSpec.get()) {
                ifPrimary.accept((T) value);
            } else {
                ifSecondary.accept((U) value);
            }
        } else {
            ifTertiary.accept((S) value);
        }
    }

    /**
     * Applies a {@link Function} according to the option value being stored.
     *
     * @param <V> The type of value to be returned.
     * @param ifPrimary The {@link Function} to apply if this contains the primary option value.
     * @param ifSecondary The {@link Function} to apply if this contains the secondary option value.
     * @param ifTertiary The {@link Function} to apply if this contains the tertiary option value.
     * @return The result from the {@link Function} that was applied.
     */
    public <V> V map(Function<? super T, V> ifPrimary, Function<? super U, V> ifSecondary, Function<? super S, V> ifTertiary) {
        if (this.optionSpec.isPresent()) {
            if (this.optionSpec.get()) {
                return ifPrimary.apply((T) value);
            }
            return ifSecondary.apply((U) value);
        }
        return ifTertiary.apply((S) value);
    }

    /**
     * Creates a {@code TernarySelective} value by promoting secondary and tertiary values upward and primary to tertiary.
     * <ul>
     * <li>If the original value was the primary value, it will be the tertiary value in the result object.</li>
     * <li>If the original value was the secondary value, it will be the primary value in the result object.</li>
     * <li>If the original value was the tertiary value, it will be the secondary value in the result object.</li>
     * </ul>
     *
     * @return A new {@code TernarySelective} value by promoting secondary and tertiary values upward and primary to tertiary.
     */
    public TernarySelective<U, S, T> shiftUp() {
        return new TernarySelective<>(value, (optionSpec.isPresent()) ? ((optionSpec.get()) ? Optional.empty() : Optional.of(true)) : Optional.of(false));
    }

    /**
     * Creates a {@code TernarySelective} value by demoting primary and secondary option values downward and tertiary to primary.
     * <ul>
     * <li>If the original value was the primary option, it will represent the secondary option in the result object.</li>
     * <li>If the original value was the secondary option, it will represent the tertiary option in the result object.</li>
     * <li>If the original value was the tertiary option, it will represent the primary option in the result object.</li>
     * </ul>
     *
     * @return A new {@code TernarySelective} value with promoting secondary and tertiary option values promoted upward and primary to tertiary.
     */
    public TernarySelective<S, T, U> shiftDown() {
        return new TernarySelective<>(value, (optionSpec.isPresent()) ? ((optionSpec.get()) ? Optional.of(false) : Optional.empty()) : Optional.of(true));
    }

    /**
     * Creates a {@code TernarySelective} value by swapping primary and secondary options.
     * <ul>
     * <li>If the original value was the primary option, it will represent the secondary option in the result object.</li>
     * <li>If the original value was the secondary option, it will represent the primary option in the result object.</li>
     * <li>If the original value was the tertiary option, then it will still represent the tertiary option in the result object.</li>
     * </ul>
     *
     * @return A new {@code TernarySelective} value with secondary and tertiary option values swapped.
     */
    public TernarySelective<U, T, S> fromSecondary() {
        return new TernarySelective<>(value, (optionSpec.isPresent()) ? ((optionSpec.get()) ? Optional.of(false) : Optional.of(true)) : Optional.empty());
    }

    /**
     * Creates a {@code TernarySelective} value by swapping primary and tertiary options.
     * <ul>
     * <li>If the original value was the primary option, it will represent the tertiary option in the result object.</li>
     * <li>If the original value was the secondary option, then it will still represent the secondary option in the result object.</li>
     * <li>If the original value was the tertiary option, it will represent the primary option in the result object.</li>
     * </ul>
     *
     * @return A new {@code TernarySelective} value with secondary and tertiary option values swapped.
     */
    public TernarySelective<S, U, T> fromTertiary() {
        return new TernarySelective<>(value, (optionSpec.isPresent()) ? ((optionSpec.get()) ? Optional.empty() : Optional.of(false)) : Optional.of(true));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(this.value);
        hash = 43 * hash + Objects.hashCode(this.optionSpec);
        return hash;
    }


    @Override
    public boolean equals(Object obj) {
        if (null != obj && obj instanceof TernarySelective) {
            TernarySelective<?, ?, ?> other = (TernarySelective<?, ?, ?>)obj;
            if (optionSpec.isPresent()) {
                return other.optionSpec.isPresent() && (boolean)optionSpec.get() == (boolean)other.optionSpec.get() && Objects.equals(value, other.value);
            }
            return !other.optionSpec.isPresent() && Objects.equals(value, other.value);
        }
        return false;
    }

    @Override
    public String toString() {
        if (optionSpec.isPresent()) {
            if (optionSpec.get())
                return (null == value) ? "TernarySwitch.PRIMARY" : String.format("TernarySwitch.PRIMARY[%s]", value);
            return (null == value) ? "TernarySwitch.SECONDARY" : String.format("TernarySwitch.SECONDARY[%s]", value);
        }
        return (null == value) ? "TernarySwitch.TERTIARY" : String.format("TernarySwitch.TERTIARY[%s]", value);
    }

}
