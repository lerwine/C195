package scheduler.util;

/**
 * A functional interface similar to {@link java.util.function.BiFunction} that can throw an error.
 * 
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> the type of the first argument to the function
 * @param <U> the type of the second argument to the function
 * @param <R> the type of the result of the function
 * @param <E> the type of exception that can be thrown.
 */
@FunctionalInterface
public interface ThrowableBiFunction<T, U, R, E extends Throwable> {

    /**
     * Applies this function to the given arguments.
     *
     * @param t the first function argument
     * @param u the second function argument
     * @return the function result
     * @throws E
     */
    R apply(T t, U u) throws E;

}
