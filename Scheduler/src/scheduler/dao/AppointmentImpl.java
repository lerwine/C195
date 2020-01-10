/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package scheduler.dao;

import expressions.AppointmentTypeProperty;
import expressions.NonNullableStringProperty;
import expressions.NonNullableTimestampProperty;
import java.net.URL;
import java.sql.Timestamp;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author erwinel
 */
public class AppointmentImpl extends DataObjectImpl implements Appointment {

    private final ReadOnlyObjectWrapper<Customer> customer = new ReadOnlyObjectWrapper<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public Customer getCustomer() {
        return customer.get();
    }

    public ReadOnlyObjectProperty<Customer> customerProperty() {
        return customer.getReadOnlyProperty();
    }
    private final ReadOnlyObjectWrapper<User> user;

    /**
     * {@inheritDoc}
     */
    @Override
    public User getUser() {
        return user.get();
    }

    public ReadOnlyObjectProperty<User> userProperty() {
        return user.getReadOnlyProperty();
    }
    private final ReadOnlyStringWrapper title;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return title.get();
    }

    public ReadOnlyStringProperty titleProperty() {
        return title.getReadOnlyProperty();
    }
    private final ReadOnlyStringWrapper description;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description.get();
    }

    public ReadOnlyStringProperty descriptionProperty() {
        return description.getReadOnlyProperty();
    }
    private final ReadOnlyStringWrapper location;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getLocation() {
        return location.get();
    }

    public ReadOnlyStringProperty locationProperty() {
        return location.getReadOnlyProperty();
    }
    private final ReadOnlyStringWrapper contact;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getContact() {
        return contact.get();
    }

    public ReadOnlyStringProperty contactProperty() {
        return contact.getReadOnlyProperty();
    }
    private final AppointmentTypeProperty type;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getType() {
        return type.get();
    }

    public ReadOnlyStringProperty typeProperty() {
        return type.getReadOnlyProperty();
    }
    private final ReadOnlyObjectWrapper<URL> url;

    /**
     * {@inheritDoc}
     */
    @Override
    public URL getUrl() {
        return url.get();
    }

    public ReadOnlyObjectProperty<URL> urlProperty() {
        return url.getReadOnlyProperty();
    }
    private final NonNullableTimestampProperty start;

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp getStart() {
        return start.get();
    }

    public ReadOnlyObjectProperty<Timestamp> startProperty() {
        return start.getReadOnlyProperty();
    }
    private final NonNullableTimestampProperty end;

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp getEnd() {
        return end.get();
    }

    public ReadOnlyObjectProperty<Timestamp> endProperty() {
        return end.getReadOnlyProperty();
    }

    public AppointmentImpl() {
        this.user = new ReadOnlyObjectWrapper<>();
        this.title = new ReadOnlyStringWrapper();
        this.description = new ReadOnlyStringWrapper();
        this.location = new ReadOnlyStringWrapper();
        this.contact = new ReadOnlyStringWrapper();
        this.type = new AppointmentTypeProperty();
        this.url = new ReadOnlyObjectWrapper<>();
        this.start = new NonNullableTimestampProperty();
        this.end = new NonNullableTimestampProperty();
    }

    public Editable createEditable() { return new Editable(); }
    
    public class Editable extends EditableBase implements Appointment {

        private final ObjectProperty<Customer> customer;

        /**
         * {@inheritDoc}
         */
        @Override
        public Customer getCustomer() {
            return customer.get();
        }

        public void setCustomer(Customer value) {
            customer.set(value);
        }

        public ObjectProperty customerProperty() {
            return customer;
        }
        private final ObjectProperty<User> user;

        /**
         * {@inheritDoc}
         */
        @Override
        public User getUser() {
            return user.get();
        }

        public void setUser(User value) {
            user.set(value);
        }

        public ObjectProperty userProperty() {
            return user;
        }
        private final StringProperty title;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getTitle() {
            return title.get();
        }

        public void setTitle(String value) {
            title.set(value);
        }

        public StringProperty titleProperty() {
            return title;
        }
        private final StringProperty description;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDescription() {
            return description.get();
        }

        public void setDescription(String value) {
            description.set(value);
        }

        public StringProperty descriptionProperty() {
            return description;
        }
        private final StringProperty location;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getLocation() {
            return location.get();
        }

        public void setLocation(String value) {
            location.set(value);
        }

        public StringProperty locationProperty() {
            return location;
        }
        private final StringProperty contact;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getContact() {
            return contact.get();
        }

        public void setContact(String value) {
            contact.set(value);
        }

        public StringProperty contactProperty() {
            return contact;
        }
        private final StringProperty type;

        /**
         * {@inheritDoc}
         */
        @Override
        public String getType() {
            return type.get();
        }

        public void setType(String value) {
            type.set(value);
        }

        public StringProperty typeProperty() {
            return type;
        }
        private final ObjectProperty<URL> url;

        /**
         * {@inheritDoc}
         */
        @Override
        public URL getUrl() {
            return url.get();
        }

        public void setUrl(URL value) {
            url.set(value);
        }

        public ObjectProperty<URL> urlProperty() {
            return url;
        }
        private final NonNullableTimestampProperty start;

        /**
         * {@inheritDoc}
         */
        @Override
        public Timestamp getStart() {
            return start.get();
        }

        public void setStart(Timestamp value) {
            start.set(value);
        }

        public ObjectProperty<Timestamp> startProperty() {
            return start;
        }
        private final NonNullableTimestampProperty end;

        /**
         * {@inheritDoc}
         */
        @Override
        public Timestamp getEnd() {
            return end.get();
        }

        public void setEnd(Timestamp value) {
            end.set(value);
        }

        public ObjectProperty<Timestamp> endProperty() {
            return end;
        }

        public Editable() {
            this.customer = new SimpleObjectProperty<>(AppointmentImpl.this.getCustomer());
            this.user = new SimpleObjectProperty<>(AppointmentImpl.this.getUser());
            this.title = new NonNullableStringProperty(AppointmentImpl.this.getTitle());
            this.description = new NonNullableStringProperty(AppointmentImpl.this.getDescription());
            this.location = new NonNullableStringProperty(AppointmentImpl.this.getLocation());
            this.contact = new NonNullableStringProperty(AppointmentImpl.this.getContact());
            this.type = new AppointmentTypeProperty(AppointmentImpl.this.getType());
            this.url = new SimpleObjectProperty<>(AppointmentImpl.this.getUrl());
            this.start = new NonNullableTimestampProperty(AppointmentImpl.this.getStart());
            this.end = new NonNullableTimestampProperty(AppointmentImpl.this.getEnd());
        }

        @Override
        public BooleanBinding isValid() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void applyChanges() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void undoChanges() {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

    }
}
