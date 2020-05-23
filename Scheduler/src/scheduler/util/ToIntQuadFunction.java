package scheduler.util;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 * @param <S>
 * @param <V>
 */
public interface ToIntQuadFunction<T, U, S, V> {

    int applyAsInt(T t, U u, S s, V v);
}
