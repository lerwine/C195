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
 */
public interface Tuple<T, U> {

    public static <T, U> ObjectBinding<Tuple<T, U>> createBinding(ObservableValue<T> value1, ObservableValue<U> value2) {
        return Bindings.createObjectBinding(() -> {
            return Tuple.of(value1.getValue(), value2.getValue());
        }, Objects.requireNonNull(value1), Objects.requireNonNull(value2));
    }
    
    public static <T, U> Tuple<T, U> of(T value1, U value2) {
        return new Tuple<T, U>() {
            @Override
            public T getValue1() {
                return value1;
            }

            @Override
            public U getValue2() {
                return value2;
            }
        };
    }

    T getValue1();

    U getValue2();

}
