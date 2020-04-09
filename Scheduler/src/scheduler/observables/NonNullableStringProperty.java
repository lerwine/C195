package scheduler.observables;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringPropertyBase;
import javafx.beans.property.SimpleStringProperty;

/**
 * A {@link SimpleStringProperty} that stores non-null {@link String} values. Null values supplied to this property will be converted to empty
 * strings.
 *
 * @author Leonard T. Erwine (Student ID 356334) <lerwine@wgu.edu>
 */
public class NonNullableStringProperty extends SimpleStringProperty {

    ReadOnlyStringProperty readOnlyProperty;
    private final boolean trimming;

    /**
     * Initializes a new string property with an empty string.
     *
     * @param trim {@code true} to trim extraneous whitespace for this property when modified; otherwise {@code false} to store string values as-is.
     */
    public NonNullableStringProperty(boolean trim) {
        super("");
        this.trimming = trim;
    }

    /**
     * Initializes a new string property with an empty string and will not trim extraneous whitespace when modified.
     */
    public NonNullableStringProperty() {
        this(false);
    }

    /**
     * Initializes a new string property with the specified initial value.
     *
     * @param initialValue The initial value of the property.
     * @param trim {@code true} to trim extraneous whitespace for this property; otherwise {@code false} to store string values as-is.
     */
    public NonNullableStringProperty(String initialValue, boolean trim) {
        super((initialValue == null) ? "" : ((trim) ? initialValue.trim() : initialValue));
        this.trimming = trim;
    }

    /**
     * Initializes a new string property with the specified initial value, and will not trim extraneous whitespace when modified.
     *
     * @param initialValue The initial value of the property.
     */
    public NonNullableStringProperty(String initialValue) {
        this(initialValue, false);
    }

    /**
     * Initializes a new string property with an empty string.
     *
     * @param bean the bean of this property.
     * @param name the name of this property.
     * @param trim {@code true} to trim extraneous whitespace for this property when modified; otherwise {@code false} to store string values as-is.
     */
    public NonNullableStringProperty(Object bean, String name, boolean trim) {
        super(bean, name, "");
        this.trimming = trim;
    }

    /**
     * Initializes a new string property with an empty string and will not trim extraneous whitespace when modified.
     *
     * @param bean the bean of this property.
     * @param name the name of this property.
     */
    public NonNullableStringProperty(Object bean, String name) {
        this(bean, name, false);
    }

    /**
     * Initializes a new string property with the specified initial value.
     *
     * @param bean the bean of this property.
     * @param name the name of this property.
     * @param initialValue The initial value of the property.
     * @param trim {@code true} to trim extraneous whitespace for this property; otherwise {@code false} to store string values as-is.
     */
    public NonNullableStringProperty(Object bean, String name, String initialValue, boolean trim) {
        super(bean, name, (initialValue == null) ? "" : ((trim) ? initialValue.trim() : initialValue));
        this.trimming = trim;
    }

    /**
     * Initializes a new string property with the specified initial value and will not trim extraneous whitespace.
     *
     * @param bean the bean of this property.
     * @param name the name of this property.
     * @param initialValue The initial value of the property.
     */
    public NonNullableStringProperty(Object bean, String name, String initialValue) {
        this(bean, name, initialValue, false);
    }

    /**
     * Returns the readonly property, that is synchronized with this {@code NonNullableStringProperty}.
     *
     * @return the readonly property
     */
    public ReadOnlyStringProperty getReadOnlyProperty() {
        if (readOnlyProperty == null) {
            readOnlyProperty = new NonNullableStringProperty.ReadOnlyPropertyImpl();
        }
        return readOnlyProperty;
    }

    /**
     * Gets a {@link BooleanBinding} that returns {@code true} if the current value does not contain any non-whitespace characters.
     *
     * @return a {@link BooleanBinding} that returns {@code true} if the current value does not contain any non-whitespace characters.
     */
    public BooleanBinding isWhiteSpaceOrEmpty() {
        return (trimming) ? isEmpty() : Bindings.createBooleanBinding(() -> get().trim().isEmpty(), this);
    }

    /**
     * Gets a value that indicates whether this property trims extraneous whitespace when modified.
     *
     * @return {@code true} if this property trims extraneous whitespace when modified; otherwise, {@code false}.
     */
    public boolean isTrimming() {
        return trimming;
    }

    @Override
    public void set(String newValue) {
        super.set((newValue == null) ? "" : ((trimming) ? newValue.trim() : newValue));
    }

    @Override
    public void setValue(String v) {
        super.set((v == null) ? "" : ((trimming) ? v.trim() : v));
    }

    private class ReadOnlyPropertyImpl extends ReadOnlyStringPropertyBase {

        @Override
        public String get() {
            return NonNullableStringProperty.this.get();
        }

        @Override
        public Object getBean() {
            return NonNullableStringProperty.this.getBean();
        }

        @Override
        public String getName() {
            return NonNullableStringProperty.this.getName();
        }

        private ReadOnlyPropertyImpl() {
            NonNullableStringProperty.this.addListener((observable, oldValue, newValue) -> {
                super.fireValueChangedEvent();
            });
        }
    };
}
