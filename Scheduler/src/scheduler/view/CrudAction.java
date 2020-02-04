package scheduler.view;

//</editor-fold>

/**
 * Represents a Create, Update or Delete operation.
 * @param <T> The type of {@link ItemModel} that was affected.
 */
public final class CrudAction<T extends ItemModel<?>> {

    private final T model;

    /**
     * Gets the {@link ItemModel} that was affected.
     * @return The {@link ItemModel} that was affected.
     */
    public T getModel() {
        return model;
    }
    private final boolean delete;

    /**
     * Indicates whether the affected item was deleted.
     *
     * @return {@code true} if the item was deleted. if the {@link add} property is {@code true}, then the item was added; otherwise, it was modified.
     */
    public boolean isDelete() {
        return delete;
    }
    private final boolean add;

    /**
     * Indicates whether the affected item is newly added.
     *
     * @return {@code true} if the item was added. if the {@link delete} property is {@code true}, then the item was deleted; otherwise, it was modified.
     */
    public boolean isAdd() {
        return add;
    }

    /**
     * Creates a new CrudAction for an add or update operation.
     * @param row The {@link ItemModel} that was affected.
     * @param isAdd {@code true} if the affected item was added; otherwise {@code false} to indicate the item was updated.
     */
    public CrudAction(T row, boolean isAdd) {
        this.model = row;
        add = isAdd;
        delete = false;
    }

    /**
     * Creates a new CrudAction for a delete operation.
     * @param row The {@link ItemModel} that was affected.
     */
    public CrudAction(T row) {
        this.model = row;
        add = false;
        delete = true;
    }
    
}
