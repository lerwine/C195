package scheduler.util;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 * @param <S>
 * @param <V>
 */
public interface Quadruplet<T, U, S, V> extends Triplet<T, U, S> {
    V getValue4();
    
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
}
