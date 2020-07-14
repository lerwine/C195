package scheduler.view.report;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collection;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.ViewControllerLoader;
import scheduler.view.annotations.FXMLResource;
import scheduler.view.annotations.GlobalizationResource;

/**
 * List item for {@link ConsultantSchedule#appointmentScheduleListView} to display an ordered list of consultant appointments for a single day.
 * <p>
 * {@link scheduler.fx.DailyAppointmentsListCell} will use this object as the graphic for the {@link javafx.scene.control.ListView} items.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/ManageAppointments")
@FXMLResource("/scheduler/view/report/DailyAppointmentsBorderPane.fxml")
public final class DailyAppointmentsBorderPane extends BorderPane {

    private static final Logger LOG = Logger.getLogger(DailyAppointmentsBorderPane.class.getName());

    private final ReadOnlyObjectWrapper<LocalDate> date;
    private final ReadOnlyListWrapper<AppointmentModel> appointments;

    @FXML // fx:id="headingLabel"
    private Label headingLabel; // Value injected by FXMLLoader

    @FXML // fx:id="listingTableView"
    private TableView<AppointmentModel> listingTableView; // Value injected by FXMLLoader

    @SuppressWarnings("LeakingThisInConstructor")
    public DailyAppointmentsBorderPane(LocalDate date, Collection<AppointmentModel> appointments) {
        this.date = new ReadOnlyObjectWrapper<>(Objects.requireNonNull(date));
        ObservableList<AppointmentModel> items = FXCollections.observableArrayList();
        if (null != appointments && !appointments.isEmpty()) {
            LOG.info("Adding appointments");
            if (appointments.size() == 1) {
                items.add(appointments.iterator().next());
            } else {
                appointments.stream().sorted(AppointmentModel::compareByDates).forEach((t) -> items.add(t));
            }
            LOG.info("Appointments added");
        }
        this.appointments = new ReadOnlyListWrapper<>(FXCollections.unmodifiableObservableList(items));
        try {
            ViewControllerLoader.initializeCustomControl(this);
        } catch (IOException ex) {
            Logger.getLogger(DailyAppointmentsBorderPane.class.getName()).log(Level.SEVERE, "Error loading view", ex);
        }
    }

    public DailyAppointmentsBorderPane() {
        this(LocalDate.now(), null);
    }

    @FXML // This method is called by the FXMLLoader when initialization is complete
    private void initialize() {
        assert headingLabel != null : "fx:id=\"headingLabel\" was not injected: check your FXML file 'DailyAppointments.fxml'.";
        assert listingTableView != null : "fx:id=\"listingTableView\" was not injected: check your FXML file 'DailyAppointments.fxml'.";
        headingLabel.setText(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).format(date.get()));
        listingTableView.setItems(appointments);
        listingTableView.setPrefHeight((double) appointments.size() * listingTableView.getFixedCellSize() + 24.0);
    }

    public LocalDate getDate() {
        return date.get();
    }

    public ReadOnlyObjectProperty<LocalDate> dateProperty() {
        return date.getReadOnlyProperty();
    }

    public ObservableList<AppointmentModel> getAppointments() {
        return appointments.get();
    }

    public ReadOnlyListProperty<AppointmentModel> appointmentsProperty() {
        return appointments.getReadOnlyProperty();
    }

}
