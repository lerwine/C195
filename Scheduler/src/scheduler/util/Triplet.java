package scheduler.util;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 * @param <S>
 */
public interface Triplet<T, U, S> extends Tuple<T, U> {

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
