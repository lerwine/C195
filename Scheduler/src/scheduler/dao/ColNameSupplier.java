package scheduler.dao;

import java.util.function.Supplier;

/**
 *
 * @author lerwi
 */
public interface ColNameSupplier extends Supplier<String> {
    default String getAlias() {
        return get();
    }
}
