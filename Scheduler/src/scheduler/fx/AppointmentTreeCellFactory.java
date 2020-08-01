package scheduler.fx;

import com.sun.javafx.event.EventHandlerManager;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
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

    private final DateTimeFormatter formatter;
    private final EventHandlerManager eventHandlerManager;
    private final ObjectProperty<EventHandler<AppointmentOpRequestEvent>> onItemActionRequest;

    public AppointmentTreeCellFactory() {
        LOG.entering(getClass().getName(), "<init>");
        formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());
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
        LOG.exiting(getClass().getName(), "<init>");
    }

    DateTimeFormatter getFormatter() {
        return formatter;
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
        AppointmentTreeCell tableCell = new AppointmentTreeCell(this);
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

}
