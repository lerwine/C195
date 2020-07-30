package scheduler.view.appointment;

import java.time.LocalDateTime;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import scheduler.model.AppointmentType;
import scheduler.model.fx.AppointmentModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class CalendarCellData {

    private final ObjectProperty<AppointmentModel> model;
    private final ReadOnlyStringWrapper title;
    private final ReadOnlyStringWrapper customerName;
    private final ReadOnlyObjectWrapper<AppointmentType> type;
    private final ReadOnlyStringWrapper location;
    private final ReadOnlyStringWrapper effectiveLocation;
    private final ReadOnlyStringWrapper url;
    private final ReadOnlyObjectWrapper<LocalDateTime> start;
    private final ReadOnlyObjectWrapper<LocalDateTime> end;
    private final BooleanProperty continuedFromPrevious;
    private final BooleanProperty continuedOnNext;

    public CalendarCellData(AppointmentModel model, boolean continuedFromPrevious, boolean continuedOnNext) {
        this.model = new SimpleObjectProperty<>(this, "model", model);
        title = new ReadOnlyStringWrapper(this, "title", "");
        customerName = new ReadOnlyStringWrapper(this, "customerName", "");
        type = new ReadOnlyObjectWrapper<>(this, "type", AppointmentType.OTHER);
        location = new ReadOnlyStringWrapper(this, "location", "");
        effectiveLocation = new ReadOnlyStringWrapper(this, "effectiveLocation", "");
        url = new ReadOnlyStringWrapper(this, "url", "");
        start = new ReadOnlyObjectWrapper<>(this, "start");
        end = new ReadOnlyObjectWrapper<>(this, "end");
        this.continuedFromPrevious = new SimpleBooleanProperty(this, "continuedFromPrevious", continuedFromPrevious);
        this.continuedOnNext = new SimpleBooleanProperty(this, "continuedOnNext", continuedOnNext);
        this.model.addListener(this::onModelChanged);
        if (null != model) {
            onModelChanged(this.model, null, model);
        }
    }

    public AppointmentModel getModel() {
        return model.get();
    }

    public void setModel(AppointmentModel value) {
        model.set(value);
    }

    public ObjectProperty<AppointmentModel> modelProperty() {
        return model;
    }

    public String getTitle() {
        return title.get();
    }

    public ReadOnlyStringProperty titleProperty() {
        return title.getReadOnlyProperty();
    }

    public String getCustomerName() {
        return customerName.get();
    }

    public ReadOnlyStringProperty customerNameProperty() {
        return customerName.getReadOnlyProperty();
    }

    public AppointmentType getType() {
        return type.get();
    }

    public ReadOnlyObjectProperty<AppointmentType> typeProperty() {
        return type.getReadOnlyProperty();
    }

    public String getLocation() {
        return location.get();
    }

    public ReadOnlyStringProperty locationProperty() {
        return location.getReadOnlyProperty();
    }

    public String getEffectiveLocation() {
        return effectiveLocation.get();
    }

    public ReadOnlyStringProperty effectiveLocationProperty() {
        return effectiveLocation.getReadOnlyProperty();
    }

    public String getUrl() {
        return url.get();
    }

    public ReadOnlyStringProperty urlProperty() {
        return url.getReadOnlyProperty();
    }

    public LocalDateTime getStart() {
        return start.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> startProperty() {
        return start.getReadOnlyProperty();
    }

    public LocalDateTime getEnd() {
        return end.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> endProperty() {
        return end.getReadOnlyProperty();
    }

    public boolean isContinuedFromPrevious() {
        return continuedFromPrevious.get();
    }

    public void setContinuedFromPrevious(boolean value) {
        continuedFromPrevious.set(value);
    }

    public BooleanProperty continuedFromPreviousProperty() {
        return continuedFromPrevious;
    }

    public boolean isContinuedOnNext() {
        return continuedOnNext.get();
    }

    public void setContinuedOnNext(boolean value) {
        continuedOnNext.set(value);
    }

    public BooleanProperty continuedOnNextProperty() {
        return continuedOnNext;
    }

    private void onModelChanged(ObservableValue<? extends AppointmentModel> observable, AppointmentModel oldValue, AppointmentModel newValue) {
        if (null == newValue) {
            title.unbind();
            customerName.unbind();
            type.unbind();
            location.unbind();
            effectiveLocation.unbind();
            url.unbind();
            start.unbind();
            end.unbind();
        } else {
            title.bind(newValue.titleProperty());
            customerName.bind(newValue.customerNameProperty());
            type.bind(newValue.typeProperty());
            location.bind(newValue.locationProperty());
            effectiveLocation.bind(newValue.effectiveLocationProperty());
            url.bind(newValue.urlProperty());
            start.bind(newValue.startProperty());
            end.bind(newValue.endProperty());
        }
    }

}
