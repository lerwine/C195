package scheduler.dao;

import scheduler.model.PartialDataEntity;
import scheduler.model.fx.PartialEntityModel;
import scheduler.util.IPropertyBindable;

/**
 * Base interface for all data access objects.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public interface PartialDataAccessObject extends PartialDataEntity, IPropertyBindable {
    /**
     * Gets the cached {@link PartialEntityModel} for the current {@link PartialDataAccessObject}.
     * 
     * @param create {@code true} to create a new associated {@link PartialEntityModel} if the cached {@link PartialEntityModel} is not set; otherwise {@code false}.
     * @return The cached {@link PartialEntityModel} or {@link null} if the cached {@link PartialEntityModel} is not set and {@code create} is {@code false};
     */
    PartialEntityModel<? extends PartialDataAccessObject> cachedModel(boolean create);
}
