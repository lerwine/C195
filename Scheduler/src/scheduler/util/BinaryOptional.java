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
 * An object that contains one or none of 2 possible value options.
 * This is is somewhat similar to {@link Optional} with a secondary option value.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The primary value type.
 * @param <U> The secondary value type.
 */
public final class BinaryOptional<T, U> {
    
    private static final BinaryOptional<?, ?> EMPTY = new BinaryOptional<>(null, Optional.empty());
    
    /**
     * Create a new {@code BinaryOptional} object that contains the primary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param t The primary value.
     * @return A {@code BinaryOptional} object that contains the primary value option.
     */
    public static <T, U> BinaryOptional<T, U> ofPrimary(T t) {
        return new BinaryOptional<>(Objects.requireNonNull(t), Optional.of(true));
    }

    /**
     * Create a new {@code BinaryOptional} object that contains a null primary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param t The optional primary value.
     * @return A {@code BinaryOptional} object that contains a null primary value option.
     */
    public static <T, U> BinaryOptional<T, U> ofPrimaryNullable(T t) {
        return new BinaryOptional<>(t, Optional.of(true));
    }

    /**
     * Create a new {@code BinaryOptional} object that optionally contains the primary value.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param t The optional primary value.
     * @return A {@code BinaryOptional} object that optionally contains the primary value.
     */
    public static <T, U> BinaryOptional<T, U> ofPrimaryOptional(Optional<T> t) {
        if (t.isPresent())
            return new BinaryOptional<>(t.get(), Optional.of(true));
        return empty();
    }

    /**
     * Create a new {@code BinaryOptional} object that contains the secondary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param u The secondary value.
     * @return A {@code BinaryOptional} object that contains the secondary value option.
     */
    public static <T, U> BinaryOptional<T, U> ofSecondary(U u) {
        return new BinaryOptional<>(Objects.requireNonNull(u), Optional.of(false));
    }

    /**
     * Create a new {@code BinaryOptional} object that contains a null secondary value option.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param u The optional secondary value.
     * @return A {@code BinaryOptional} object that contains a null secondary value option.
     */
    public static <T, U> BinaryOptional<T, U> ofSecondaryNullable(U u) {
        return new BinaryOptional<>(u, Optional.of(false));
    }

    /**
     * Create a new {@code BinaryOptional} object that optionally contains the secondary value.
     *
     * @param <T> The primary value type.
     * @param <U> The secondary value type.
     * @param u The optional secondary value.
     * @return A {@code BinaryOptional} object that optionally contains the secondary value.
     */
    public static <T, U> BinaryOptional<T, U> ofSecondaryOptional(Optional<U> u) {
        if (u.isPresent())
            return new BinaryOptional<>(u.get(), Optional.of(false));
        return empty();
    }

    public static <T, U> BinaryOptional<T, U> empty() {
        @SuppressWarnings("unchecked")
        BinaryOptional<T, U> t = (BinaryOptional<T, U>) EMPTY;
        return t;
    }
    private final Object value;
    private final Optional<Boolean> primary;

    private BinaryOptional(Object obj, Optional<Boolean> primary) {
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
        return primary.isPresent() && primary.get();
    }

    /**
     * Indicates whether the current object contains the primary value option.
     *
     * @return {@code true} if this object contains the primary value option; otherwise, {@code false} to indicate that this object contains the
     * secondary value option.
     */
    public boolean isSecondary() {
        return primary.isPresent() && !primary.get();
    }

    /**
     * Indicates whether there is any value present.
     *
     * @return {@code true} if this object contains the primary or secondary value option;
     * otherwise, {@code false} to indicate that this object contains no value option.
     */
    public boolean isPresent() {
        return primary.isPresent();
    }

    /**
     * Gets the value of the primary option. This will only return a value if {@link #getPrimary()} is {@code true}.
     *
     * @return The primary value;
     * @throws NoSuchElementException if this object does not contain the primary value option.
     */
    @SuppressWarnings("unchecked")
    public T getPrimary() {
        if (isPrimary()) {
            return (T) value;
        }
        throw new NoSuchElementException("No value present");
    }

    /**
     * Gets the value of the secondary option. This will only return a value if {@link #getPrimary()} is {@code false}.
     *
     * @return The secondary value;
     * @throws NoSuchElementException if this object does not contain the secondary value option.
     */
    @SuppressWarnings("unchecked")
    public U getSecondary() {
        if (isPrimary()) {
            throw new NoSuchElementException("No value present");
        }
        return (U) value;
    }

    @SuppressWarnings("unchecked")
    public Optional<T> toPrimaryOption() {
        return (isPrimary()) ? Optional.of((T) value) : Optional.empty();
    }
    
    @SuppressWarnings("unchecked")
    public Optional<U> toSecondaryOption() {
        return (isSecondary()) ? Optional.of((U) value) : Optional.empty();
    }
    
    /**
     * If this contains the primary value option, invoke the specified consumer with the value; otherwise, do nothing. This is similar to
     * {@link Optional#ifPresent(Consumer)}.
     *
     * @param consumer The {@link Consumer} to invoke if this contains the primary value option,.
     */
    @SuppressWarnings("unchecked")
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
    @SuppressWarnings("unchecked")
    public void ifSecondary(Consumer<? super U> consumer) {
        if (isSecondary()) {
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
    public void acceptIfPresent(Consumer<? super T> ifPrimary, Consumer<? super U> ifSecondary) {
        if (primary.isPresent()) {
            if (primary.get()) {
                ifPrimary.accept((T) value);
            } else {
                ifSecondary.accept((U) value);
            }
        }
    }

    /**
     * Applies a {@link Function} according to the option value being stored.
     *
     * @param <S> The type of value to be returned.
     * @param ifPrimary The {@link Function} to apply if this contains the primary option value.
     * @param ifSecondary The {@link Function} to apply if this contains the secondary option value.
     * @return The result from the {@link Function} that was applied or {@link Optional#EMPTY} if no value was present.
     */
    @SuppressWarnings("unchecked")
    public <S> Optional<S> map(Function<? super T, S> ifPrimary, Function<? super U, S> ifSecondary) {
        if (primary.isPresent()) {
            return Optional.ofNullable((primary.get()) ? ifPrimary.apply((T) value) : ifSecondary.apply((U) value));
        }
        return Optional.empty();
    }

    /**
     * Applies a {@link Function} according to the option value being stored.
     * <p>This is similar to {@link #map(java.util.function.Function, java.util.function.Function)},
     * except that it will throw a {@link NullPointerException} if either of the returned values are null.</p>
     *
     * @param <S> The type of value to be returned.
     * @param ifPrimary The {@link Function} to apply if this contains the primary option value.
     * @param ifSecondary The {@link Function} to apply if this contains the secondary option value.
     * @return The result from the {@link Function} that was applied or {@link Optional#EMPTY} if no value was present.
     */
    @SuppressWarnings("unchecked")
    public <S> Optional<S> flatMap(Function<? super T, S> ifPrimary, Function<? super U, S> ifSecondary) {
        if (primary.isPresent()) {
            return Optional.of((primary.get()) ? ifPrimary.apply((T) value) : ifSecondary.apply((U) value));
        }
        return Optional.empty();
    }

    /**
     * Applies a {@link Function} according to the option value being stored.
     *
     * @param <S> The type of value to be returned.
     * @param ifPrimary The {@link Function} to apply if this contains the primary option value.
     * @param ifSecondary The {@link Function} to apply if this contains the secondary option value.
     * @param ifNotPresent The {@link Supplier} for the result value if this does not contain any option value.
     * @return The result from the {@link Function} that was applied or from the {@link Supplier} if no value was present.
     */
    @SuppressWarnings("unchecked")
    public <S> S map(Function<? super T, S> ifPrimary, Function<? super U, S> ifSecondary, Supplier<S> ifNotPresent) {
        if (primary.isPresent()) {
            return (primary.get()) ? ifPrimary.apply((T) value) : ifSecondary.apply((U) value);
        }
        return ifNotPresent.get();
    }

    /**
     * Creates a {@code BinaryOptional} value by swapping primary and secondary options.
     *
     * @return A new {@code BinaryOptional} value with secondary and tertiary option values swapped.
     */
    public BinaryOptional<U, T> shift() {
        if (primary.isPresent())
            return new BinaryOptional<>(value, Optional.of(primary.get()));
        return empty();
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.value);
        hash = 29 * hash + Objects.hashCode(this.primary);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (null != obj && obj instanceof BinaryOptional) {
            BinaryOptional<?, ?> other = (BinaryOptional<?, ?>)obj;
            if (primary.isPresent())
                return other.primary.isPresent() && (boolean)primary.get() == (boolean)other.primary.get() &&  Objects.equals(value, other.value);
            return !other.primary.isPresent();
        }
        return false;
    }

    @Override
    public String toString() {
        if (primary.isPresent()) {
            if (primary.get())
                return (null == value) ? "BinaryOption.PRIMARY" : String.format("BinaryOption.PRIMARY[%s]", value);
            return (null == value) ? "BinaryOption.SECONDARY" : String.format("BinaryOption.SECONDARY[%s]", value);
        }
        return "BinaryOption.EMPTY";
    }
    
}
