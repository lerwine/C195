package scheduler.filter;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.function.Function;
import java.util.function.Supplier;
import scheduler.util.ThrowableBiConsumer;
import scheduler.view.ItemModel;

/**
 * Defines an object that gets the name of the corresponding data column, gets the data column value from a {@link ItemModel}
 * and sets the corresponding parameter value in a {@link PreparedStatement} if applicable.
 * @author erwinel
 * @param <M> The type of {@link ItemModel} being accessed.
 * @param <V> The type of value being accessed.
 */
public interface ValueAccessor<M extends ItemModel<?>, V> extends Function<M, V>, Supplier<String>,
        ThrowableBiConsumer<V, ParameterConsumer, SQLException> {
    
    default String getSqlValue() { return "?"; }
}
