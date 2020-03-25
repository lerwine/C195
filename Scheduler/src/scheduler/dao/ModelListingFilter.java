package scheduler.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Predicate;
import scheduler.dao.dml.WhereStatement;
import scheduler.view.ItemModel;

/**
 * Represents a data record filter.
 *
 * @author Leonard T. Erwine (Student ID 356334)
 * @param <D> The type of {@link DataObjectImpl} object that represents the data from the database.
 * @param <M> The type of {@link ItemModel} that corresponds to the {@link DataObjectImpl} type.
 */
public interface ModelListingFilter<D extends DataObjectImpl, M extends ItemModel<D>> extends RecordReader<D>, Predicate<M> {

    public static <D extends DataObjectImpl, M extends ItemModel<D>> boolean areEqual(ModelListingFilter<D, M> x, ModelListingFilter<D, M> y) {
        if (null == x)
            return null == y;
        if (null == y)
            return false;
        
        WhereStatement<D, M> a = x.getWhereStatement();
        WhereStatement<D, M> b = y.getWhereStatement();
        return (null == a) ? null == b : null != b && a.equals(b);
    }

    /**
     * Creates a {@link ModelListingFilter} that returns all items.
     *
     * @param <D> The type of {@link DataObjectImpl} object that represents the data from the database.
     * @param <M> The type of {@link ItemModel} that corresponds to the {@link DataObjectImpl} type.
     * @param factory The {@link DataObjectImpl.Factory} responsible for creating the result {@link DataObjectImpl} objects.
     * @param loadingMessage The message to display while data is being loaded from the database.
     * @param heading The heading to display in the items listing view.
     * @param subHeading The sub-heading to display in the items listing view.
     * @return The new {@link ModelListingFilter}.
     */
    public static <D extends DataObjectImpl, M extends ItemModel<D>> ModelListingFilter<D, M> all(DataObjectImpl.Factory<D, ? extends ItemModel<D>> factory,
            String loadingMessage, String heading, String subHeading) {
        if (null == subHeading) {
            return all(factory, loadingMessage, heading, "");
        }
        Objects.requireNonNull(loadingMessage);
        Objects.requireNonNull(heading);
        return new ModelListingFilter<D, M>() {
            @Override
            public String getHeading() {
                return heading;
            }

            @Override
            public String getSubHeading() {
                return subHeading;
            }

            @Override
            public String getLoadingMessage() {
                return subHeading;
            }

            @Override
            public WhereStatement<D, ? extends ItemModel<D>> getWhereStatement() {
                return null;
            }

            @Override
            public DataObjectImpl.Factory<D, ? extends ItemModel<D>> getFactory() {
                return factory;
            }

            @Override
            public int apply(PreparedStatement ps, int index) throws SQLException {
                return index;
            }

            @Override
            public boolean test(M t) {
                return true;
            }

        };
    }

    /**
     * Gets the heading to display in the items listing view.
     *
     * @return The heading to display in the items listing view.
     */
    String getHeading();

    /**
     * Gets the sub-heading to display in the items listing view.
     *
     * @return The sub-heading to display in the items listing view.
     */
    String getSubHeading();

}
