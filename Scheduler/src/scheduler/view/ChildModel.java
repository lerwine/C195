package scheduler.view;

import java.util.Objects;
import scheduler.dao.DataObject;

/**
 * Java FX object model for a child item in a foreign key relationship.
 *
 * @author erwinel
 * @param <R> The type of object that is used for data operations.
 */
public interface ChildModel<R extends DataObject> extends DataObjectReferenceModel<R> {

    /**
     * Checks that {@link DataObject#isExisting()} from {@link #getDataObject()} returns true, throwing an exception if otherwise.
     *
     * @param <M> The type of {@link ChildModel} to check.
     * @param model The {@link ChildModel} to check.
     * @param displayName The name describing the object, which is used to format any exception message that might be thrown.
     * @return {@code model} if no exception thrown.
     * @throws NullPointerException if the target {@link ChildModel} is null.
     * @throws AssertionError if {@link DataObject#isExisting()} from {@link #getDataObject()} on the target {@link ChildModel} returns false.
     */
    public static <M extends ChildModel<?>> M requireExisting(M model, String displayName) throws NullPointerException, AssertionError {
        assert !Objects.requireNonNull(model, () -> String.format("%s cannot be null", displayName)).getDataObject().isExisting() : String.format("%s cannot be a new object", displayName);
        return model;
    }
}
