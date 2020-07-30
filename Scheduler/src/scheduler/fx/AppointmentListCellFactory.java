package scheduler.fx;

import com.sun.javafx.event.EventHandlerManager;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventDispatchChain;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Callback;
import scheduler.events.AppointmentOpRequestEvent;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.LogHelper;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class AppointmentListCellFactory implements Callback<ListView<AppointmentModel>, ListCell<AppointmentModel>>, EventTarget {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentListCellFactory.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(AppointmentListCellFactory.class.getName());

    private final DateTimeFormatter formatter;
    private final EventHandlerManager eventHandlerManager;
    private final BooleanProperty singleLine;
    private final ObjectProperty<EventHandler<AppointmentOpRequestEvent>> onItemActionRequest;

    public AppointmentListCellFactory() {
        LOG.entering(getClass().getName(), "<init>");
        formatter = DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM).withZone(ZoneId.systemDefault());
        eventHandlerManager = new EventHandlerManager(this);
        singleLine = new SimpleBooleanProperty(false);
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

    public boolean isSingleLine() {
        return singleLine.get();
    }

    public void setSingleLine(boolean value) {
        singleLine.set(value);
    }

    public BooleanProperty singleLineProperty() {
        return singleLine;
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
    public AppointmentListCell call(ListView<AppointmentModel> param) {
        LOG.entering(LOG.getName(), "call", param);
        AppointmentListCell tableCell = new AppointmentListCell(this);
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
