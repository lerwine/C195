package scheduler.model;

import java.util.Objects;
import java.util.function.Function;
import scheduler.dao.DataAccessObject;
import scheduler.model.ui.FxRecordModel;

/**
 * An object that specifies a {@link DataAccessObject} and may also specify the associated {@link FxRecordModel}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 *
 * @param <D> The target {@link DataAccessObject} type.
 * @param <M> The associated {@link FxRecordModel} type.
 */
public interface RecordModelContext<D extends DataAccessObject, M extends FxRecordModel<D>> {

    public static <D extends DataAccessObject, M extends FxRecordModel<D>> RecordModelContext<D, M> of(M fxRecordModel) {
        Objects.requireNonNull(fxRecordModel);
        return new RecordModelContext<D, M>() {
            @Override
            public D getDataAccessObject() {
                return fxRecordModel.dataObject();
            }

            @Override
            public M getFxRecordModel() {
                return fxRecordModel;
            }

        };
    }

    public static <D extends DataAccessObject, M extends FxRecordModel<D>> RecordModelContext<D, M> of(D dataAccessObject) {
        return () -> dataAccessObject;
    }

    /**
     * Gets the target {@link DataAccessObject}.
     *
     * @return The target {@link DataAccessObject}.
     */
    D getDataAccessObject();

    /**
     * Gets the {@link FxRecordModel} that wraps the target {@link DataAccessObject}.
     *
     * @return The {@link FxRecordModel} that wraps the target {@link DataAccessObject} or {@code null} if only the target {@link DataAccessObject} is present.
     */
    default M getFxRecordModel() {
        return null;
    }

    default <T> T applyModel(Function<M, T> ifModelPresent, Function<D, T> otherwise) {
        M m = getFxRecordModel();
        if (null == m) {
            return otherwise.apply(getDataAccessObject());
        }
        return ifModelPresent.apply(m);
    }
}
