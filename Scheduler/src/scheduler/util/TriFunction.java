package scheduler.util;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T>
 * @param <U>
 * @param <S>
 * @param <R>
 */
@FunctionalInterface
public interface TriFunction<T, U, S, R> {
    R apply(T t, U u, S s);
}
