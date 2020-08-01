package scheduler.view.appointment;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import scheduler.dao.PartialCustomerDAO;
import scheduler.dao.PartialUserDAO;
import scheduler.model.AppointmentType;
import scheduler.model.ModelHelper.AppointmentHelper;
import scheduler.model.UserStatus;
import scheduler.model.fx.AppointmentModel;
import scheduler.model.fx.PartialCustomerModel;
import scheduler.model.fx.PartialUserModel;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentDay {

    public static Stream<AppointmentDay> create(AppointmentModel model) {
        Stream.Builder<AppointmentDay> builder = Stream.builder();
        LocalDate s = model.getStart().toLocalDate();
        LocalDate e = (model.getEnd().toLocalTime().equals(LocalTime.MIN)) ? model.getEnd().toLocalDate() : model.getEnd().toLocalDate().plusDays(1);
        for (LocalDate d = s; d.compareTo(e) < 0; d = d.plusDays(1)) {
            builder.accept(new AppointmentDay(model, d));
        }
        return builder.build();
    }

    private static Stream<AppointmentDay> find(int pk, Collection<AppointmentDay> source) {
        if (null == source) {
            return Stream.empty();
        }
        return source.stream().filter((item) -> item.model.get().getPrimaryKey() == pk);
    }

    public static int compareByDates(AppointmentDay a, AppointmentDay b) {
        if (null == a) {
            return (null == b) ? 0 : 1;
        }
        if (null == b) {
            return -1;
        }
        if (a == b) {
            return 0;
        }
        int result = a.date.get().compareTo(b.date.get());
        return (result == 0) ? AppointmentHelper.compareByDates(a.model.get(), b.model.get()) : 0;
    }

    public static boolean update(ListChangeListener.Change<? extends AppointmentModel> sourceChange, ObservableList<AppointmentDay> target) {
        if (sourceChange.wasPermutated() || sourceChange.wasUpdated()) {
            return false;
        }

        ArrayList<AppointmentDay> toAdd = new ArrayList<>();
        ArrayList<AppointmentDay> toRemove = new ArrayList<>();
        sourceChange.getRemoved().forEach((remitem) -> {
            find(remitem.getPrimaryKey(), target).forEach((t) -> toRemove.add(t));
        });
        sourceChange.getAddedSubList().forEach((additem) -> {
            find(additem.getPrimaryKey(), target).forEach((t) -> toRemove.add(t));
            create(additem).forEach((t) -> toAdd.add(t));
        });

        if (toRemove.isEmpty()) {
            if (toAdd.isEmpty()) {
                return false;
            }
        } else {
            target.removeAll(toRemove);
            if (toAdd.isEmpty()) {
                return false;
            }
        }
        target.addAll(toAdd);
        return true;
    }

    private final ReadOnlyObjectWrapper<LocalDate> date;
    private final ReadOnlyObjectWrapper<AppointmentModel> model;

    private AppointmentDay(AppointmentModel model, LocalDate date) {
        this.model = new ReadOnlyObjectWrapper<>(model);
        this.date = new ReadOnlyObjectWrapper<>(date);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public ReadOnlyObjectProperty<LocalDate> dateProperty() {
        return date.getReadOnlyProperty();
    }

    public AppointmentModel getModel() {
        return model.get();
    }

    public ReadOnlyObjectProperty<AppointmentModel> modelProperty() {
        return model.getReadOnlyProperty();
    }

    public PartialCustomerModel<? extends PartialCustomerDAO> getCustomer() {
        return model.get().getCustomer();
    }

    public ObjectProperty<? extends PartialCustomerModel<? extends PartialCustomerDAO>> customerProperty() {
        return model.get().customerProperty();
    }

    public String getCustomerName() {
        return model.get().getCustomerName();
    }

    public ReadOnlyStringProperty customerNameProperty() {
        return model.get().customerNameProperty();
    }

    public String getCustomerAddress1() {
        return model.get().getCustomerAddress1();
    }

    public ReadOnlyStringProperty customerAddress1Property() {
        return model.get().customerAddress1Property();
    }

    public String getCustomerAddress2() {
        return model.get().getCustomerAddress2();
    }

    public ReadOnlyStringProperty customerAddress2Property() {
        return model.get().customerAddress2Property();
    }

    public String getCustomerCityName() {
        return model.get().getCustomerCityName();
    }

    public ReadOnlyStringProperty customerCityNameProperty() {
        return model.get().customerCityNameProperty();
    }

    public String getCustomerCountryName() {
        return model.get().getCustomerCountryName();
    }

    public ReadOnlyStringProperty customerCountryNameProperty() {
        return model.get().customerCountryNameProperty();
    }

    public String getCustomerPostalCode() {
        return model.get().getCustomerPostalCode();
    }

    public ReadOnlyStringProperty customerPostalCodeProperty() {
        return model.get().customerPostalCodeProperty();
    }

    public String getCustomerPhone() {
        return model.get().getCustomerPhone();
    }

    public ReadOnlyStringProperty customerPhoneProperty() {
        return model.get().customerPhoneProperty();
    }

    public String getCustomerCityZipCountry() {
        return model.get().getCustomerCityZipCountry();
    }

    public ReadOnlyStringProperty customerCityZipCountryProperty() {
        return model.get().customerCityZipCountryProperty();
    }

    public String getCustomerAddressText() {
        return model.get().getCustomerAddressText();
    }

    public ReadOnlyStringProperty customerAddressTextProperty() {
        return model.get().customerAddressTextProperty();
    }

    public boolean isCustomerActive() {
        return model.get().isCustomerActive();
    }

    public ReadOnlyBooleanProperty customerActiveProperty() {
        return model.get().customerActiveProperty();
    }

    public PartialUserModel<? extends PartialUserDAO> getUser() {
        return model.get().getUser();
    }

    public ObjectProperty<PartialUserModel<? extends PartialUserDAO>> userProperty() {
        return model.get().userProperty();
    }

    public String getUserName() {
        return model.get().getUserName();
    }

    public ReadOnlyStringProperty userNameProperty() {
        return model.get().userNameProperty();
    }

    public UserStatus getUserStatus() {
        return model.get().getUserStatus();
    }

    public ReadOnlyObjectProperty<UserStatus> userStatusProperty() {
        return model.get().userStatusProperty();
    }

    public String getUserStatusDisplay() {
        return model.get().getUserStatusDisplay();
    }

    public ReadOnlyStringProperty userStatusDisplayProperty() {
        return model.get().userStatusDisplayProperty();
    }

    public String getTitle() {
        return model.get().getTitle();
    }

    public StringProperty titleProperty() {
        return model.get().titleProperty();
    }

    public String getDescription() {
        return model.get().getDescription();
    }

    public StringProperty descriptionProperty() {
        return model.get().descriptionProperty();
    }

    public String getLocation() {
        return model.get().getLocation();
    }

    public StringProperty locationProperty() {
        return model.get().locationProperty();
    }

    public String getEffectiveLocation() {
        return model.get().getEffectiveLocation();
    }

    public ReadOnlyStringProperty effectiveLocationProperty() {
        return model.get().effectiveLocationProperty();
    }

    public String getContact() {
        return model.get().getContact();
    }

    public StringProperty contactProperty() {
        return model.get().contactProperty();
    }

    public AppointmentType getType() {
        return model.get().getType();
    }

    public SimpleObjectProperty<AppointmentType> typeProperty() {
        return model.get().typeProperty();
    }

    public String getTypeDisplay() {
        return model.get().getTypeDisplay();
    }

    public ReadOnlyStringProperty typeDisplayProperty() {
        return model.get().typeDisplayProperty();
    }

    public String getUrl() {
        return model.get().getUrl();
    }

    public StringProperty urlProperty() {
        return model.get().urlProperty();
    }

    public LocalDateTime getStart() {
        return model.get().getStart();
    }

    public ObjectProperty<LocalDateTime> startProperty() {
        return model.get().startProperty();
    }

    public LocalDateTime getEnd() {
        return model.get().getEnd();
    }

    public ObjectProperty<LocalDateTime> endProperty() {
        return model.get().endProperty();
    }

}
