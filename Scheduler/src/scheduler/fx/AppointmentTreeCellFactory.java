package scheduler.fx;

import com.sun.javafx.event.EventHandlerManager;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.NamedArg;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.util.LogHelper;
import scheduler.view.appointment.AppointmentDay;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentTreeCellFactory implements Callback<TreeView<AppointmentDay>, TreeCell<AppointmentDay>>, EventTarget {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentTreeCellFactory.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(AppointmentTreeCellFactory.class.getName());

    private final EventHandlerManager eventHandlerManager;
    private final ObjectProperty<EventHandler<AppointmentOpRequestEvent>> onItemActionRequest;

    private final BooleanProperty userNameExcluded;
    private final StringProperty timeFormat;
    private final StringProperty dateFormat;
    private DateTimeFormatter dateFormatter;
    private DateTimeFormatter timeFormatter;

    public AppointmentTreeCellFactory(@NamedArg("userNameExcluded") boolean userNameExcluded, @NamedArg("dateFormat") String dateFormat, @NamedArg("timeFormat") String timeFormat) {
        LOG.entering(getClass().getName(), "<init>");
        this.userNameExcluded = new SimpleBooleanProperty(this, "userNameExcluded", userNameExcluded);
        this.dateFormat = new SimpleStringProperty(this, "dateFormat", dateFormat);
        this.timeFormat = new SimpleStringProperty(this, "timeFormat", timeFormat);
        eventHandlerManager = new EventHandlerManager(this);
        onItemActionRequest = new SimpleObjectProperty<>(this, "onItemActionRequest");
        onItemActionRequest.addListener((observable, oldValue, newValue) -> {
            if (null != oldValue) {
                eventHandlerManager.removeEventHandler(AppointmentOpRequestEvent.APPOINTMENT_OP_REQUEST, oldValue);
            }
            if (null != newValue) {
                eventHandlerManager.addEventHandler(AppointmentOpRequestEvent.APPOINTMENT_OP_REQUEST, newValue);
            }
        });
        this.dateFormat.addListener(this::onDateFormatChanged);
        this.timeFormat.addListener(this::onTimeFormatChanged);
        onDateFormatChanged(this.dateFormat, null, this.dateFormat.get());
        onTimeFormatChanged(this.timeFormat, null, this.timeFormat.get());
        LOG.exiting(getClass().getName(), "<init>");
    }

    public boolean isUserNameExcluded() {
        return userNameExcluded.get();
    }

    public void setUserNameExcluded(boolean value) {
        userNameExcluded.set(value);
    }

    public BooleanProperty userNameExcludedProperty() {
        return userNameExcluded;
    }

    public String getTimeFormat() {
        return timeFormat.get();
    }

    public void setTimeFormat(String value) {
        timeFormat.set(value);
    }

    public StringProperty timeFormatProperty() {
        return timeFormat;
    }

    public String getDateFormat() {
        return dateFormat.get();
    }

    public void setDateFormat(String value) {
        dateFormat.set(value);
    }

    public StringProperty dateFormatProperty() {
        return dateFormat;
    }

    DateTimeFormatter getDateFormatter() {
        return dateFormatter;
    }

    DateTimeFormatter getTimeFormatter() {
        return timeFormatter;
    }

    public EventHandler<AppointmentOpRequestEvent> getOnItemActionRequest() {
        return onItemActionRequest.get();
    }

    public void setOnItemActionRequest(EventHandler<AppointmentOpRequestEvent> value) {
        onItemActionRequest.set(value);
    }

    public ObjectProperty<EventHandler<AppointmentOpRequestEvent>> onItemActionRequestProperty() {
        return onItemActionRequest;
    }

    @Override
    public TreeCell<AppointmentDay> call(TreeView<AppointmentDay> param) {
        LOG.entering(LOG.getName(), "call", param);
        AppointmentTreeCell tableCell = new AppointmentTreeCell(this, userNameExcluded.get());
        LOG.exiting(getClass().getName(), "call", tableCell);
        return tableCell;
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
        tail = tail.append(eventHandlerManager);
        LOG.exiting(getClass().getName(), "buildEventDispatchChain", tail);
        return tail;
    }

    public void addEventHandler(EventType<AppointmentOpRequestEvent> type, EventHandler<? super AppointmentOpRequestEvent> eventHandler) {
        eventHandlerManager.addEventHandler(type, eventHandler);
    }

    public void addEventFilter(EventType<AppointmentOpRequestEvent> type, EventHandler<? super AppointmentOpRequestEvent> eventHandler) {
        eventHandlerManager.addEventFilter(type, eventHandler);
    }

    public void removeEventHandler(EventType<AppointmentOpRequestEvent> type, EventHandler<? super AppointmentOpRequestEvent> eventHandler) {
        eventHandlerManager.removeEventHandler(type, eventHandler);
    }

    public void removeEventFilter(EventType<AppointmentOpRequestEvent> type, EventHandler<? super AppointmentOpRequestEvent> eventHandler) {
        eventHandlerManager.removeEventFilter(type, eventHandler);
    }

    private void onDateFormatChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (null == newValue || newValue.trim().isEmpty())
            dateFormatter = DateTimeFormatter.ofPattern("eeee, d", Locale.getDefault(Locale.Category.FORMAT)).withZone(ZoneId.systemDefault());
        else
            dateFormatter = DateTimeFormatter.ofPattern(newValue, Locale.getDefault(Locale.Category.FORMAT)).withZone(ZoneId.systemDefault());
    }

    private void onTimeFormatChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        if (null == newValue || newValue.trim().isEmpty())
            timeFormatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());
        else
            timeFormatter = DateTimeFormatter.ofPattern(newValue, Locale.getDefault(Locale.Category.FORMAT)).withZone(ZoneId.systemDefault());
    }

}
