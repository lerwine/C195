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
 */
public interface Triplet<T, U, S> extends Tuple<T, U> {

    public static <T, U, S> ObjectBinding<Triplet<T, U, S>> createBinding(ObservableValue<T> value1, ObservableValue<U> value2, ObservableValue<S> value3) {
        return Bindings.createObjectBinding(() -> {
            return Triplet.of(value1.getValue(), value2.getValue(), value3.getValue());
        }, Objects.requireNonNull(value1), Objects.requireNonNull(value2), Objects.requireNonNull(value3));
    }
    
    public static <T, U, S> Triplet<T, U, S> of(T value1, U value2, S value3) {
        return new Triplet<T, U, S>() {
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

        };
    }

    S getValue3();

}
