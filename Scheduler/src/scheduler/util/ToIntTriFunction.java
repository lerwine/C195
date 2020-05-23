package scheduler.util;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 * @param <S>
 */
public interface ToIntTriFunction<T, U, S> {

    int applyAsInt(T t, U u, S s);
}
