/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * An object that contains one or none of 3 possible value options.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <T> The primary value type.
 * @param <U> The secondary value type.
 * @param <S> The tertiary value type.
 */
public final class TernaryOptional<T, U, S> {

    private static final TernaryOptional<?, ?, ?> EMPTY = new TernaryOptional<>(null, false, false);

    /**
     * Create a new {@code TernaryOptional} object that contains the primary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param t The primary value.
     * @return A {@code TernaryOptional} object that contains the primary value option.
     * @throws NullPointerException if {@code t} is null.
     */
    public static <T, U, S> TernaryOptional<T, U, S> ofPrimary(T t) {
        return new TernaryOptional<>(Objects.requireNonNull(t), true, true);
    }

    /**
     * Create a new {@code TernaryOptional} object that contains a null primary value option value.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param t The primary value.
     * @return A {@code TernaryOptional} object that contains a null primary value option.
     */
    public static <T, U, S> TernaryOptional<T, U, S> ofPrimaryNullable(T t) {
        return new TernaryOptional<>(t, true, true);
    }

    /**
     * Create a new {@code TernaryOptional} object that optionally contains the primary value.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param t The optional primary value.
     * @return A {@code TernaryOptional} object that optionally contains the primary value.
     * @throws NullPointerException if {@code t} is null.
     */
    public static <T, U, S> TernaryOptional<T, U, S> ofPrimaryOptional(Optional<T> t) {
        if (t.isPresent()) {
            return new TernaryOptional<>(t.get(), true, true);
        }
        return empty();
    }

    /**
     * Create a new {@code TernaryOptional} object that contains the secondary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param u The secondary value.
     * @return A {@code TernaryOptional} object that contains the secondary value option.
     * @throws NullPointerException if {@code u} is null.
     */
    public static <T, U, S> TernaryOptional<T, U, S> ofSecondary(U u) {
        return new TernaryOptional<>(Objects.requireNonNull(u), true, false);
    }

    /**
     * Create a new {@code TernaryOptional} object that contains a null secondary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param u The secondary value.
     * @return A {@code TernaryOptional} object that contains a null secondary value option.
     */
    public static <T, U, S> TernaryOptional<T, U, S> ofSecondaryNullable(U u) {
        return new TernaryOptional<>(u, true, false);
    }

    /**
     * Create a new {@code TernaryOptional} object that optionally contains the secondary value.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param u The optional primary value.
     * @return A {@code TernaryOptional} object that optionally contains the secondary value.
     * @throws NullPointerException if {@code t} is null.
     */
    public static <T, U, S> TernaryOptional<T, U, S> ofSecondaryOptional(Optional<U> u) {
        if (u.isPresent()) {
            return new TernaryOptional<>(u.get(), true, false);
        }
        return empty();
    }

    /**
     * Create a new {@code TernaryOptional} object that contains the tertiary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param s The tertiary value.
     * @return A {@code TernaryOptional} object that contains the tertiary value option.
     * @throws NullPointerException if {@code s} is null.
     */
    public static <T, U, S> TernaryOptional<T, U, S> ofTertiary(S s) {
        return new TernaryOptional<>(Objects.requireNonNull(s), false, true);
    }

    /**
     * Create a new {@code TernaryOptional} object that contains a null tertiary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param s The tertiary value.
     * @return A {@code TernaryOptional} object that contains a null tertiary value option.
     */
    public static <T, U, S> TernaryOptional<T, U, S> ofTertiaryNullable(S s) {
        return new TernaryOptional<>(s, false, true);
    }

    /**
     * Create a new {@code TernaryOptional} object that optionally contains the tertiary value.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param <S> The tertiary value type.
     * @param s The optional tertiary value.
     * @return A {@code TernaryOptional} object that optionally contains the tertiary value.
     * @throws NullPointerException if {@code t} is null.
     */
    public static <T, U, S> TernaryOptional<T, U, S> ofTertiaryOptional(Optional<S> s) {
        if (s.isPresent()) {
            return new TernaryOptional<>(s.get(), false, true);
        }
        return empty();
    }

    public static <T, U, S> TernaryOptional<T, U, S> empty() {
        @SuppressWarnings("unchecked")
        TernaryOptional<T, U, S> t = (TernaryOptional<T, U, S>) EMPTY;
        return t;
    }
    private final Object value;
    private final boolean selection;
    private final boolean option;

    /**
     * Creates new {@code TernaryOption} object.
     * <table>
     * <thead><tr><th>{@link #selection}</th><th>{@link #option}</th></tr><th>Meaning</th></thead>
     * <tbody>
     * <tr><td>{@code true}</td><td>{@code true}</td><td>{@link #value} contains the primary option value.</td></tr>
     * <tr><td>{@code true}</td><td>{@code false}</td><td>{@link #value} contains the secondary option value.</td></tr>
     * <tr><td>{@code false}</td><td>{@code true}</td><td>{@link #value} contains the tertiary option value.</td></tr>
     * <tr><td>{@code false}</td><td>{@code false}</td><td>Contains no value.</td></tr>
     * </tbody>
     * </table>
     *
     * @param obj The stored value.
     * @param selection The selection flag.
     * @param option The option flag.
     */
    private TernaryOptional(Object obj, boolean selection, boolean option) {
        value = obj;
        this.selection = selection;
        this.option = option;
    }

    /**
     * Indicates whether the current object contains the primary value option. If this returns {@code true}, then both {@link #isSecondary()} and
     * {@link #isTertiary()} will return {@code false}.
     *
     * @return {@code true} if this object contains the primary value option; otherwise, {@code false} to indicate that calling {@link #getPrimary()}
     * would thrown a {@link NoSuchElementException}.
     */
    public boolean isPrimary() {
        return selection && option;
    }

    /**
     * Indicates whether the current object contains the secondary value option. If this returns {@code true}, then both {@link #isPrimary()} and
     * {@link #isTertiary()} will return {@code false}.
     *
     * @return {@code true} if this object contains the secondary value option; otherwise, {@code false} to indicate that calling
     * {@link #getSecondary()} would thrown a {@link NoSuchElementException}.
     */
    public boolean isSecondary() {
        return selection && !option;
    }

    /**
     * Indicates whether the current object contains the tertiary value option. If this returns {@code true}, then both {@link #isPrimary()} and
     * {@link #isSecondary()} will return {@code false}.
     *
     * @return {@code true} if this object contains the tertiary value option; otherwise, {@code false} to indicate that calling
     * {@link #getTertiary()} would thrown a {@link NoSuchElementException}.
     */
    public boolean isTertiary() {
        return option && !selection;
    }

    /**
     * Gets the value of the primary option. This will only return a value if {@link #getPrimary()} is {@code true}.
     *
     * @return The primary value;
     * @throws NoSuchElementException if this object does not contain the primary value option.
     */
    public T getPrimary() {
        if (isPrimary()) {
            return (T) value;
        }
        throw new NoSuchElementException("No value present");
    }

    /**
     * Gets the value of the secondary option. This will only return a value if {@link #isSecondary()} is {@code true}.
     *
     * @return The secondary value;
     * @throws NoSuchElementException if this object does not contain the secondary value option.
     */
    public U getSecondary() {
        if (isSecondary()) {
            return (U) value;
        }
        throw new NoSuchElementException("No value present");
    }

    /**
     * Gets the value of the tertiary option. This will only return a value if {@link #isTertiary()} is {@code true}.
     *
     * @return The tertiary value;
     * @throws NoSuchElementException if this object does not contain the tertiary value option.
     */
    public S getTertiary() {
        if (isTertiary()) {
            return (S) value;
        }
        throw new NoSuchElementException("No value present");
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
        if (!isTertiary()) {
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
        if (selection) {
            if (option) {
                ifPrimary.accept((T) value);
            } else {
                ifSecondary.accept((U) value);
            }
        } else if (option) {
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
    public <V> Optional<V> map(Function<? super T, V> ifPrimary, Function<? super U, V> ifSecondary, Function<? super S, V> ifTertiary) {
        if (selection) {
            if (option) {
                return Optional.ofNullable(ifPrimary.apply((T) value));
            }
            return Optional.ofNullable(ifSecondary.apply((U) value));
        }
        if (option) {
            Optional.ofNullable(ifTertiary.apply((S) value));
        }
        return Optional.empty();
    }

    /**
     * Applies a {@link Function} according to the option value being stored.
     *
     * @param <V> The type of value to be returned.
     * @param ifPrimary The {@link Function} to apply if this contains the primary option value.
     * @param ifSecondary The {@link Function} to apply if this contains the secondary option value.
     * @param ifTertiary The {@link Function} to apply if this contains the tertiary option value.
     * @param ifNotPresent The {@link Supplier} for the result value if this does not contain any option value.
     * @return The result from the {@link Function} that was applied or from the {@link Supplier} if no value was present.
     */
    public <V> V map(Function<? super T, V> ifPrimary, Function<? super U, V> ifSecondary, Function<? super S, V> ifTertiary, Supplier<V> ifNotPresent) {
        if (selection) {
            if (option) {
                return ifPrimary.apply((T) value);
            }
            return ifSecondary.apply((U) value);
        }
        if (option) {
            ifTertiary.apply((S) value);
        }
        return ifNotPresent.get();
    }

    /**
     * Applies a {@link Function} according to the option value being stored.
     * <p>
     * This is similar to {@link #map(java.util.function.Function, java.util.function.Function, java.util.function.Function)}, except that it will
     * throw a {@link NullPointerException} if any of the returned values are null.</p>
     *
     * @param <V> The type of value to be returned.
     * @param ifPrimary The {@link Function} to apply if this contains the primary option value.
     * @param ifSecondary The {@link Function} to apply if this contains the secondary option value.
     * @param ifTertiary The {@link Function} to apply if this contains the tertiary option value.
     * @return The result from the {@link Function} that was applied.
     */
    public <V> Optional<V> flatMap(Function<? super T, V> ifPrimary, Function<? super U, V> ifSecondary, Function<? super S, V> ifTertiary) {
        if (selection) {
            if (option) {
                return Optional.of(ifPrimary.apply((T) value));
            }
            return Optional.of(ifSecondary.apply((U) value));
        }
        if (option) {
            Optional.of(ifTertiary.apply((S) value));
        }
        return Optional.empty();
    }

    /**
     * Creates a {@code TernaryOptional} value by promoting secondary and tertiary values upward and primary to tertiary.
     * <ul>
     * <li>If the original value was the primary value, it will be the tertiary value in the result object.</li>
     * <li>If the original value was the secondary value, it will be the primary value in the result object.</li>
     * <li>If the original value was the tertiary value, it will be the secondary value in the result object.</li>
     * </ul>
     *
     * @return A new {@code TernaryOptional} value by promoting secondary and tertiary values upward and primary to tertiary.
     */
    public TernaryOptional<U, S, T> shiftUp() {
        return new TernaryOptional<>(value, selection != option, selection);
    }

    /**
     * Creates a {@code TernaryOptional} value by demoting primary and secondary option values downward and tertiary to primary.
     * <ul>
     * <li>If the original value was the primary option, it will represent the secondary option in the result object.</li>
     * <li>If the original value was the secondary option, it will represent the tertiary option in the result object.</li>
     * <li>If the original value was the tertiary option, it will represent the primary option in the result object.</li>
     * </ul>
     *
     * @return A new {@code TernaryOptional} value with promoting secondary and tertiary option values promoted upward and primary to tertiary.
     */
    public TernaryOptional<S, T, U> shiftDown() {
        return new TernaryOptional<>(value, option, selection != option);
    }

    /**
     * Creates a {@code TernaryOptional} value by swapping primary and secondary options.
     * <ul>
     * <li>If the original value was the primary option, it will represent the secondary option in the result object.</li>
     * <li>If the original value was the secondary option, it will represent the primary option in the result object.</li>
     * <li>If the original value was the tertiary option, then it will still represent the tertiary option in the result object.</li>
     * </ul>
     *
     * @return A new {@code TernaryOptional} value with secondary and tertiary option values swapped.
     */
    public TernaryOptional<U, T, S> fromSecondary() {
        return new TernaryOptional<>(value, selection, selection != option);
    }

    /**
     * Creates a {@code TernaryOptional} value by swapping primary and tertiary options.
     * <ul>
     * <li>If the original value was the primary option, it will represent the tertiary option in the result object.</li>
     * <li>If the original value was the secondary option, then it will still represent the secondary option in the result object.</li>
     * <li>If the original value was the tertiary option, it will represent the primary option in the result object.</li>
     * </ul>
     *
     * @return A new {@code TernaryOptional} value with secondary and tertiary option values swapped.
     */
    public TernaryOptional<S, U, T> fromTertiary() {
        return new TernaryOptional<>(value, selection != option, option && !selection);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(value);
        if (selection) {
            hash = 17 * hash + 1;
        }
        if (option) {
            hash = 17 * hash + 1;
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (null != obj && obj instanceof TernaryOptional) {
            TernaryOptional<?, ?, ?> other = (TernaryOptional<?, ?, ?>) obj;
            if (selection == other.selection && option == other.option) {
                if (selection || option) {
                    return Objects.equals(value, other.value);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        if (selection) {
            if (option) {
                return (null == value) ? "TernaryOption.PRIMARY" : String.format("TernaryOption.PRIMARY[%s]", value);
            }
            return (null == value) ? "TernaryOption.SECONDARY" : String.format("TernaryOption.SECONDARY[%s]", value);
        }
        if (option) {
            return (null == value) ? "TernaryOption.TERTIARY" : String.format("TernaryOption.TERTIARY[%s]", value);
        }
        return "TernaryOption.EMPTY";
    }

}
