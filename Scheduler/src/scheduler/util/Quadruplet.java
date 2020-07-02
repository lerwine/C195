package scheduler.util;

import java.util.Objects;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 * @param <S>
 * @param <V>
 */
public interface Quadruplet<T, U, S, V> extends Triplet<T, U, S> {

    public static <T, U, S, V> ObjectBinding<Quadruplet<T, U, S, V>> createBinding(ObservableValue<T> value1, ObservableValue<U> value2, ObservableValue<S> value3, ObservableValue<V> value4) {
        return Bindings.createObjectBinding(() -> {
            return Quadruplet.of(value1.getValue(), value2.getValue(), value3.getValue(), value4.getValue());
        }, Objects.requireNonNull(value1), Objects.requireNonNull(value2), Objects.requireNonNull(value3), Objects.requireNonNull(value4));
    }
    
    public static <T, U, S, V> Quadruplet<T, U, S, V> of(T value1, U value2, S value3, V value4) {
        return new Quadruplet<T, U, S, V>() {
            @Override
            public T getValue1() {
                return value1;
            }

            @Override
            public U getValue2() {
                return value2;
            }

            @Override
            public S getValue3() {
                return value3;
            }

            @Override
            public V getValue4() {
                return value4;
            }

        };
    }

    V getValue4();
}
