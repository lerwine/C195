/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.observables;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;
import scheduler.util.Values;

/**
 * A {@link SimpleIntegerProperty} that represents a valid data rows state value.
 * The value will be {@link Values#ROWSTATE_NEW}, {@link Values#ROWSTATE_UNMODIFIED},
 * {@link Values#ROWSTATE_MODIFIED} or {@link Values#ROWSTATE_DELETED}.
 * @author erwinel
 */
public class RowStateProperty extends SimpleIntegerProperty {

        private ReadOnlyIntegerProperty readOnlyProperty;
        
        /**
         * Returns the readonly property, that is synchronized with this {@code RowStateProperty}.
         *
         * @return the readonly property
         */
        public ReadOnlyIntegerProperty getReadOnlyProperty() {
            if (readOnlyProperty == null)
                readOnlyProperty = new ReadOnlyPropertyImpl();
            return readOnlyProperty;
        }

        public RowStateProperty() {
            this(Values.ROWSTATE_NEW);
        }

        public RowStateProperty(int initialValue) {
            super(Values.asValidRowState(initialValue));
        }

        public RowStateProperty(Object bean, String name) {
            this(bean, name, Values.ROWSTATE_NEW);
        }

        public RowStateProperty(Object bean, String name, int initialValue) {
            super(bean, name, Values.asValidRowState(initialValue));
        }

        @Override
        public void set(int newValue) { super.set(Values.asValidRowState(newValue)); }

        private class ReadOnlyPropertyImpl extends ReadOnlyIntegerPropertyBase {

            @Override
            public int get() { return RowStateProperty.this.get(); }
            
            @Override
            public Object getBean() { return RowStateProperty.this.getBean(); }

            @Override
            public String getName() { return RowStateProperty.this.getName(); }
            
            private ReadOnlyPropertyImpl() {
                RowStateProperty.this.addListener((observable, oldValue, newValue) -> {
                    super.fireValueChangedEvent();
                });
            }
        };
}
