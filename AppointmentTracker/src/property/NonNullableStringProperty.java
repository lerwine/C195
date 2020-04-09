package property;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class NonNullableStringProperty extends SimpleStringProperty {

    private final BooleanBinding whiteSpace;

    public BooleanBinding isWhiteSpace() { return whiteSpace; }

    private final BooleanBinding notWhiteSpace;

    public BooleanBinding isNotWhiteSpace() { return notWhiteSpace; }

    public NonNullableStringProperty() { this(""); }

    public NonNullableStringProperty(String initialValue) {
        super((initialValue == null) ? "" : initialValue);
        whiteSpace = new IsWhiteSpaceBinding();
        notWhiteSpace = new NotWhiteSpaceBinding();
    }

    public NonNullableStringProperty(Object bean, String name) { this(bean, name, ""); }

    public NonNullableStringProperty(Object bean, String name, String initialValue) {
        super(bean, name, (initialValue == null) ? "" : initialValue);
        whiteSpace = new IsWhiteSpaceBinding();
        notWhiteSpace = new NotWhiteSpaceBinding();
    }

    @Override
    public void set(String newValue) { super.set((newValue == null) ? "" : newValue); }
    
    class IsWhiteSpaceBinding extends BooleanBinding {
        IsWhiteSpaceBinding() { super.bind(NonNullableStringProperty.this); }
        @Override
        protected boolean computeValue() { return NonNullableStringProperty.this.get().trim().isEmpty(); }

        @Override
        public ObservableList<?> getDependencies() {
            return javafx.collections.FXCollections.singletonObservableList(NonNullableStringProperty.this);
        }

        @Override
        public void dispose() {
            super.unbind(NonNullableStringProperty.this);
            super.dispose();
        }
    }
    
    class NotWhiteSpaceBinding extends BooleanBinding {
        NotWhiteSpaceBinding() { super.bind(NonNullableStringProperty.this); }
        @Override
        protected boolean computeValue() { return !NonNullableStringProperty.this.get().trim().isEmpty(); }

        @Override
        public ObservableList<?> getDependencies() {
            return javafx.collections.FXCollections.singletonObservableList(NonNullableStringProperty.this);
        }

        @Override
        public void dispose() {
            super.unbind(NonNullableStringProperty.this);
            super.dispose();
        }
    }
}
