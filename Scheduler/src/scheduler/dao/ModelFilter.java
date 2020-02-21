package scheduler.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Predicate;
import scheduler.view.ItemModel;

/**
 * Represents a data record filter.
 *
 * @author lerwi
 * @param <D> The type of {@link DataObjectImpl} object that represents the data from the database.
 * @param <M> The type of {@link ItemModel} that corresponds to the {@link DataObjectImpl} type.
 */
public interface ModelFilter<D extends DataObjectImpl, M extends ItemModel<D>> extends RecordReader<D>, Predicate<M> {

    /**
     * Gets the heading to display in the items listing view.
     * @return The heading to display in the items listing view.
     */
    String getHeading();

    /**
     * Gets the sub-heading to display in the items listing view.
     * @return The sub-heading to display in the items listing view.
     */
    String getSubHeading();
    
    public static <D extends DataObjectImpl, M extends ItemModel<D>> ModelFilter<D, M> all(DataObjectImpl.Factory<D, ? extends ItemModel<D>> factory,
            String loadingMessage, String heading, String subHeading) {
        if (null == subHeading)
            return all(factory, loadingMessage, heading, "");
        Objects.requireNonNull(loadingMessage);
        Objects.requireNonNull(heading);
        return new ModelFilter<D, M>() {
            @Override
            public String getHeading() { return heading; }

            @Override
            public String getSubHeading() { return subHeading; }

            @Override
            public String getLoadingMessage() { return subHeading; }

            @Override
            public String getSqlFilterExpr() { return ""; }

            @Override
            public DataObjectImpl.Factory<D, ? extends ItemModel<D>> getFactory() { return factory; }

            @Override
            public int apply(PreparedStatement ps, int index) throws SQLException { return index; }

            @Override
            public boolean test(M t) { return true; }
            
        };
    }

}
