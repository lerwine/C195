package scheduler.util;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 */
public interface Tuple<T, U> {
    T getValue1();
    U getValue2();
    
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
    
}
