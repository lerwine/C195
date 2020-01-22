/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package expressions;

import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyIntegerPropertyBase;
import javafx.beans.property.SimpleIntegerProperty;
import scheduler.dao.DataObject;
import scheduler.dao.DataObjectFactory;

/**
 *
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
            this(DataObjectFactory.ROWSTATE_NEW);
        }

        public RowStateProperty(int initialValue) {
            super(asValidValue(initialValue));
        }

        public RowStateProperty(Object bean, String name) {
            this(bean, name, DataObjectFactory.ROWSTATE_NEW);
        }

        public RowStateProperty(Object bean, String name, int initialValue) {
            super(bean, name, asValidValue(initialValue));
        }

        public static int asValidValue(int value) {
            return (value < DataObjectFactory.ROWSTATE_DELETED) ? DataObjectFactory.ROWSTATE_DELETED :
                    ((value > DataObjectFactory.ROWSTATE_MODIFIED) ? DataObjectFactory.ROWSTATE_MODIFIED : value);
        }
        
        @Override
        public void set(int newValue) {
            super.set(asValidValue(newValue));
        }

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
