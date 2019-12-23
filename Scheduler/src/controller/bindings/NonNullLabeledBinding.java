package controller.bindings;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Labeled;

/**
 * Binding which indicates whether a target property does not contain a null value.
 * This will also update a specified error message label with the error message.
 * If the target property valie is not null, the error message label will be collapsed.
 * @param <T>
 *        The type of target property value.
 */
public class NonNullLabeledBinding<T> extends BooleanBinding {
    private final ReadOnlyObjectProperty<T> source;
    private final Labeled labeled;
    private final String emptyMessage;

    /**
     * Creates a new NonNullLabeledBinding object.
     * @param source
     *        The source property to bind to.
     * @param labeled
     *        The target error message label.
     * @param emptyMessage
     *        The message to display in the target error message label when the target property value is null.
     */
    public NonNullLabeledBinding(ReadOnlyObjectProperty<T> source, Labeled labeled, String emptyMessage) {
        this.source = source;
        this.labeled = labeled;
        this.emptyMessage = emptyMessage;
        super.bind(source);
    }

    @Override
    protected boolean computeValue() {
        T value = source.get();
        if (value == null) {
            scheduler.util.restoreLabeledVertical(labeled, emptyMessage);
            return false;
        }
        scheduler.util.collapseLabeledVertical(labeled);
        return true;
    }

    @Override
    public ObservableList<?> getDependencies() { return FXCollections.singletonObservableList(source); }

    @Override
    public void dispose() { super.unbind(source); }
}