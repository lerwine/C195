package scheduler.view;

import java.util.function.Predicate;
import scheduler.dao.DataAccessObject;
import scheduler.dao.filter.DaoFilter;
import scheduler.model.fx.EntityModel;

/**
 * View model filter interface.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @param <T> The {@link DataAccessObject} type that the model supports.
 * @param <U> The type of {@link EntityModel}.
 * @param <S> The type of underlying {@link DaoFilter}.
 */
public interface ModelFilter<T extends DataAccessObject, U extends EntityModel<T>, S extends DaoFilter<T>> extends Predicate<U> {

    /**
     * Gets the heading text to display in listing views.
     *
     * @return The heading text to display in listing views.
     */
    String getHeadingText();

    /**
     * Gets the sub-heading text to display in listing views.
     *
     * @return The sub-heading text to display in listing views or an empty string if no sub-heading is to be displayed.
     */
    default String getSubHeadingText() {
        return "";
    }

    /**
     * Gets the underlying {@link DaoFilter} that is used to retrieve {@link DataAccessObject} items from the database.
     *
     * @return The underlying {@link DaoFilter} that is used to retrieve {@link DataAccessObject} items from the database.
     */
    S getDaoFilter();
}
