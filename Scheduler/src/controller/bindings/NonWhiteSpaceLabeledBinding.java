package controller.bindings;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Labeled;

public class NonWhiteSpaceLabeledBinding extends BooleanBinding {
    private final StringProperty source;
    private final Labeled labeled;
    private final String emptyMessage;

    public NonWhiteSpaceLabeledBinding(StringProperty source, Labeled labeled, String emptyMessage) {
        this.source = source;
        this.labeled = labeled;
        this.emptyMessage = emptyMessage;
        super.bind(source);
    }

    @Override
    protected boolean computeValue() {
        String value = source.get();
        if (value == null || value.trim().isEmpty()) {
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