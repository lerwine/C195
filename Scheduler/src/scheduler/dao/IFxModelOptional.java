package scheduler.dao;

import scheduler.model.ui.FxRecordModel;

/**
 * An object that specifies a {@link DataAccessObject} and may also specify the associated {@link FxRecordModel.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 *
 * @param <D> The target {@link DataAccessObject} type.
 * @param <M> The associated {@link FxRecordModel} type.
 */
public interface IFxModelOptional<D extends DataAccessObject, M extends FxRecordModel<D>> {

    /**
     * Gets the target {@link DataAccessObject}.
     *
     * @return The target {@link DataAccessObject}.
     */
    D getDataAccessObject();

    /**
     * Gets the {@link FxRecordModel} that wraps the target {@link DataAccessObject}.
     *
     * @return The {@link FxRecordModel} that wraps the target {@link DataAccessObject} or {@code null} if only the target {@link DataAccessObject}
     * was provided.
     */
    M getFxRecordModel();

}
