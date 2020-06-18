package scheduler.util;

/**
 * A functional interface similar to {@link java.util.function.Supplier} that can throw an error.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> the type of results supplied by this supplier.
 * @param <E> the type of exception that could be thrown.
 */
@FunctionalInterface
public interface ThrowableSupplier<T, E extends Throwable> {

    /**
     * Gets a result.
     *
     * @return a result
     * @throws E the exception (if any) thrown by the supplier.
     */
    T get() throws E;
}
