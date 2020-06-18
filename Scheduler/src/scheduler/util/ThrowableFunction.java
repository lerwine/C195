package scheduler.util;

/**
 * A functional interface similar to {@link java.util.function.Function} that can throw an error.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The type of the argument to the function.
 * @param <R> The type of the result of the function.
 * @param <E> The type of exception that can be thrown.
 */
@FunctionalInterface
public interface ThrowableFunction<T, R, E extends Throwable> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the function argument.
     * @return the function result.
     * @throws E if exception was thrown while applying this function.
     */
    R apply(T t) throws E;
}
