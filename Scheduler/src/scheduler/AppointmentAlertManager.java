package scheduler;

import com.sun.javafx.event.EventHandlerManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventDispatchChain;
import javafx.event.EventTarget;
import static scheduler.Scheduler.getCurrentUser;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.events.AppointmentSuccessEvent;
import scheduler.model.Appointment;
import scheduler.model.ModelHelper.AppointmentHelper;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.DateTimeUtil;
import scheduler.util.DbConnector;
import scheduler.util.LogHelper;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.AppointmentAlertManager}
 */
public class AppointmentAlertManager implements EventTarget {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentAlertManager.class.getName()), Level.FINEST);
//    private static final Logger LOG = Logger.getLogger(AppointmentAlertManager.class.getName());

    public static final AppointmentAlertManager INSTANCE = new AppointmentAlertManager();

    private final DateTimeFormatter formatter;
    private final HashMap<Integer, Item> allItems;
    private final ObservableList<AppointmentModel> backingAlertList;
    private final ReadOnlyListWrapper<AppointmentModel> activeAlerts;
    private final ReadOnlyBooleanWrapper alerting;
    private final ReadOnlyBooleanWrapper checking;
    private final ReadOnlyObjectWrapper<LocalDateTime> lastCheck;
    private final ReadOnlyObjectWrapper<Throwable> fault;
    private final EventHandlerManager eventHandlerManager;
    private final int alertLeadtimeMinutes;
    private final int checkFrequencySeconds;
    private Item firstSnoozed = null;
    private Item lastSnoozed = null;
    private Timer appointmentCheckTimer = null;
    private boolean checkingFlag = false;

    // Singleton
    private AppointmentAlertManager() {
        LOG.entering(getClass().getName(), "<init>");
        formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withZone(ZoneId.systemDefault());
        allItems = new HashMap<>();
        backingAlertList = FXCollections.observableArrayList();
        checking = new ReadOnlyBooleanWrapper(this, "checking", false);
        lastCheck = new ReadOnlyObjectWrapper<>(this, "lastCheck");
        activeAlerts = new ReadOnlyListWrapper<>(this, "activeAlerts", FXCollections.unmodifiableObservableList(backingAlertList));
        alerting = new ReadOnlyBooleanWrapper(this, "alerting", false);
        fault = new ReadOnlyObjectWrapper<>(this, "fault");
        eventHandlerManager = new EventHandlerManager(this);
        int i;
        try {
            i = AppResources.getAppointmentAlertLeadTime();
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, "Error getting alert lead time from settings", ex);
            i = 15;
        }
        alertLeadtimeMinutes = i;
        try {
            i = AppResources.getAppointmentCheckFrequency();
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, "Error getting alert lead time from settings", ex);
            i = 120;
        }
        checkFrequencySeconds = i;
        eventHandlerManager.addEventHandler(AppointmentSuccessEvent.INSERT_SUCCESS, (event) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "<init>", "INSERT_SUCCESS"), "handle", event);
            AppointmentModel model = event.getEntityModel();
            if (Platform.isFxApplicationThread()) {
                onAppointmentInserted(model);
            } else {
                Platform.runLater(() -> onAppointmentInserted(model));
            }
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "<init>", "INSERT_SUCCESS"), "handle");
        });
        eventHandlerManager.addEventHandler(AppointmentSuccessEvent.UPDATE_SUCCESS, (event) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "<init>", "UPDATE_SUCCESS"), "handle", event);
            AppointmentModel model = event.getEntityModel();
            if (Platform.isFxApplicationThread()) {
                onAppointmentUpdated(model);
            } else {
                Platform.runLater(() -> onAppointmentUpdated(model));
            }
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "<init>", "UPDATE_SUCCESS"), "handle");
        });
        eventHandlerManager.addEventHandler(AppointmentSuccessEvent.DELETE_SUCCESS, (event) -> {
            LOG.entering(LogHelper.toLambdaSourceClass(LOG, "<init>", "DELETE_SUCCESS"), "handle", event);
            AppointmentModel model = event.getEntityModel();
            if (Platform.isFxApplicationThread()) {
                onAppointmentDeleted(model);
            } else {
                Platform.runLater(() -> onAppointmentDeleted(model));
            }
            LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "<init>", "DELETE_SUCCESS"), "handle");
        });
        LOG.exiting(getClass().getName(), "<init>");
    }

    public boolean isAlerting() {
        return alerting.get();
    }

    public ReadOnlyBooleanProperty alertingProperty() {
        return alerting.getReadOnlyProperty();
    }

    public ObservableList<AppointmentModel> getActiveAlerts() {
        return activeAlerts.get();
    }

    public ReadOnlyListProperty<AppointmentModel> activeAlertsProperty() {
        return activeAlerts;
    }

    public boolean isChecking() {
        return checking.get();
    }

    public ReadOnlyBooleanProperty checkingProperty() {
        return checking.getReadOnlyProperty();
    }

    private synchronized boolean setChecking(boolean value) {
        LOG.entering(getClass().getName(), "setChecking", value);
        if (checkingFlag == value) {
            LOG.exiting(getClass().getName(), "setChecking", false);
            return false;
        }
        checkingFlag = value;
        if (Platform.isFxApplicationThread()) {
            checking.set(value);
        } else {
            Platform.runLater(() -> checking.set(value));
        }
        LOG.exiting(getClass().getName(), "setChecking", true);
        return true;
    }

    public LocalDateTime getLastCheck() {
        return lastCheck.get();
    }

    public ReadOnlyObjectProperty<LocalDateTime> lastCheckProperty() {
        return lastCheck.getReadOnlyProperty();
    }

    private void setLastCheck(LocalDateTime value) {
        LOG.entering(getClass().getName(), "setChecking", value);
        if (Platform.isFxApplicationThread()) {
            if (!value.equals(lastCheck.get())) {
                lastCheck.set(value);
            }
        } else {
            Platform.runLater(() -> {
                LOG.entering(LogHelper.toLambdaSourceClass(LOG, "setLastCheck", "Platform.runLater"), "run");
                if (!value.equals(lastCheck.get())) {
                    lastCheck.set(value);
                }
                LOG.exiting(LogHelper.toLambdaSourceClass(LOG, "setLastCheck", "Platform.runLater"), "run");
            });
        }
        LOG.exiting(getClass().getName(), "setChecking");
    }

    public Throwable getFault() {
        return fault.get();
    }

    public ReadOnlyObjectProperty<Throwable> faultProperty() {
        return fault.getReadOnlyProperty();
    }

    private synchronized void setFault(Throwable value) {
        fault.set(value);
    }

    public synchronized boolean clearFault() {
        LOG.entering(getClass().getName(), "clearFault");
        if (null == fault.get()) {
            LOG.exiting(getClass().getName(), "clearFault", false);
            return false;
        }
        start(false);
        fault.set(null);
        LOG.exiting(getClass().getName(), "clearFault", true);
        return true;
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        LOG.entering(LOG.getName(), "buildEventDispatchChain", tail);
        EventDispatchChain result = tail.append(eventHandlerManager);
        LOG.exiting(LOG.getName(), "buildEventDispatchChain", result);
        return result;
    }

    private synchronized void setSnooze(Item item, LocalDateTime snoozeUntil) {
        LOG.entering(LOG.getName(), "setSnooze", new Object[]{item, snoozeUntil});
        item.setSnoozedUntil(snoozeUntil).ifPresent((t) -> {
            LOG.entering(LOG.getName(), "setSnooze#item.setSnoozedUntil(LocalDateTime)=>ifPresent", t);
            if (t) {
                LOG.finer(() -> String.format("Removing %s from backingAlertList", item.model));
                backingAlertList.remove(item.model);
                if (backingAlertList.isEmpty()) {
                    LOG.finer("Setting alerting to false");
                    alerting.set(false);
                }
            } else {
                LOG.finer(() -> String.format("Adding %s to backingAlertList", item.model));
                backingAlertList.add(item.model);
                if (backingAlertList.size() > 1) {
                    LOG.finer("Sorting backingAlertList");
                    backingAlertList.sort(AppointmentHelper::compareByDates);
                }
                if (!alerting.get()) {
                    LOG.finer("Setting alerting to true");
                    alerting.set(true);
                }
            }
            LOG.exiting(LOG.getName(), "setSnooze#item.setSnoozedUntil(LocalDateTime)=>ifPresent");
        });
        LOG.exiting(LOG.getName(), "setSnooze");
    }

    private void insertAppointment(AppointmentModel model) {
        LOG.finer(() -> String.format("Adding %s to allItems and backingAlertList", model));
        allItems.put(model.getPrimaryKey(), new Item(model));
        backingAlertList.add(model);
        LOG.finer("Sorting backingAlertList");
        backingAlertList.sort(AppointmentHelper::compareByDates);
        if (!alerting.get()) {
            LOG.finer("Setting alerting to true");
            alerting.set(true);
        }
    }

    private void updateAppointment(AppointmentModel model) {
        int primaryKey = model.getPrimaryKey();
        Item item = allItems.get(primaryKey);
        if (model.getEnd().compareTo(LocalDateTime.now()) >= 0 && model.getStart().compareTo(LocalDateTime.now().plusMinutes(alertLeadtimeMinutes)) <= 0) {
            if (item.dismissed) {
                item.model = model;
                if (item.start.equals(model.getStart()) && item.end.equals(model.getEnd())) {
                    item.start = model.getStart();
                    item.end = model.getEnd();
                    return;
                }
                item.dismissed = false;
                backingAlertList.add(model);
            } else if (null != item.snoozedUntil) {
                item.model = model;
                LOG.finer(() -> String.format("Removing %s from snoozed item chain", item));
                item.setSnoozedUntil(null);
                LOG.finer(() -> String.format("Adding %s to backingAlertList", model));
                backingAlertList.add(model);
            } else if (item.model != model) {

                LOG.finer(() -> String.format("Removing %s from backingAlertList", item.model));
                backingAlertList.remove(item.model);
                LOG.finer(() -> String.format("Adding %s to backingAlertList", model));
                backingAlertList.add(model);
            } else if (item.start.equals(model.getStart()) && item.end.equals(model.getEnd())) {
                return;
            }
            item.start = model.getStart();
            item.end = model.getEnd();
            LOG.finer("Sorting backingAlertList");
            backingAlertList.sort(AppointmentHelper::compareByDates);
            if (!alerting.get()) {
                LOG.finer("Setting alerting to true");
                alerting.set(true);
            }
        } else if (item.dismissed) {
            LOG.finer(() -> String.format("Removing %s from allItems", item));
            allItems.remove(primaryKey);
        } else if (item.start.equals(model.getStart()) && item.end.equals(model.getEnd())) {
            if (null == item.snoozedUntil && item.model != model) {
                LOG.finer(() -> String.format("Removing %s from backingAlertList", item.model));
                backingAlertList.remove(item.model);
                item.model = model;
                LOG.finer(() -> String.format("Adding %s to backingAlertList", model));
                backingAlertList.add(model);
            } else {
                item.model = model;
            }
        } else {
            LOG.finer(() -> String.format("Removing %s from allItems", item));
            allItems.remove(primaryKey);
            if (null != item.snoozedUntil) {
                LOG.finer(() -> String.format("Removing %s from snoozed item chain", item));
                item.setSnoozedUntil(null);
            } else {
                LOG.finer(() -> String.format("Removing %s from backingAlertList", item.model));
                backingAlertList.remove(item.model);
                if (backingAlertList.isEmpty()) {
                    LOG.finer("Setting alerting to false");
                    alerting.set(false);
                }
            }
        }
    }

    private synchronized void onAppointmentInserted(AppointmentModel model) {
        LOG.entering(LOG.getName(), "onAppointmentInserted", model);
        if (model.getEnd().compareTo(LocalDateTime.now()) >= 0 && model.getStart().compareTo(LocalDateTime.now().plusMinutes(alertLeadtimeMinutes)) <= 0) {
            insertAppointment(model);
        }
        LOG.exiting(LOG.getName(), "onAppointmentInserted");
    }

    private synchronized void onAppointmentUpdated(AppointmentModel model) {
        LOG.entering(LOG.getName(), "onAppointmentUpdated", model);
        int primaryKey = model.getPrimaryKey();
        if (allItems.containsKey(primaryKey)) {
            updateAppointment(model);
        } else if (model.getEnd().compareTo(LocalDateTime.now()) >= 0 && model.getStart().compareTo(LocalDateTime.now().plusMinutes(alertLeadtimeMinutes)) <= 0) {
            insertAppointment(model);
        }
        LOG.exiting(LOG.getName(), "onAppointmentUpdated");
    }

    private synchronized void onAppointmentDeleted(AppointmentModel model) {
        LOG.entering(LOG.getName(), "onAppointmentDeleted", model);
        int primaryKey = model.getPrimaryKey();
        if (allItems.containsKey(primaryKey)) {
            Item item = allItems.get(primaryKey);
            LOG.finer(() -> String.format("Removing %s from allItems", item));
            allItems.remove(primaryKey);
            if (null != item.snoozedUntil) {
                LOG.finer(() -> String.format("Removing %s from snoozed item chain", item));
                item.setSnoozedUntil(null);
            } else if (!item.dismissed) {
                LOG.finer(() -> String.format("Removing %s from backingAlertList", item.model));
                backingAlertList.remove(item.model);
                if (backingAlertList.isEmpty()) {
                    LOG.finer("Setting alerting to false");
                    alerting.set(false);
                }
            }
        }
        LOG.exiting(LOG.getName(), "onAppointmentDeleted");
    }

    void start() {
        start(true);
    }

    void stop() {
        stop(true);
    }

    private synchronized void start(boolean isInitial) {
        LOG.entering(LOG.getName(), "start", isInitial);
        if (null != appointmentCheckTimer) {
            if (isInitial) {
                LOG.exiting(LOG.getName(), "start");
                return;
            }
            appointmentCheckTimer.purge();
        }
        appointmentCheckTimer = new Timer();
        appointmentCheckTimer.schedule(new CheckAppointmentsTask(), 0, checkFrequencySeconds * 60_000L);
        LOG.exiting(LOG.getName(), "start");
    }

    private synchronized boolean stop(boolean isPermanent) {
        LOG.entering(LOG.getName(), "stop", isPermanent);
        if (null == appointmentCheckTimer) {
            LOG.exiting(LOG.getName(), "stop", false);
            return false;
        }
        LOG.fine("Canceling appointmentCheckTimer");
        appointmentCheckTimer.cancel();
        if (isPermanent) {
            LOG.fine("Purging appointmentCheckTimer");
            appointmentCheckTimer.purge();
            appointmentCheckTimer = null;
        }
        LOG.exiting(LOG.getName(), "stop", true);
        return true;
    }

    private synchronized void onPeriodicCheckFinished(List<AppointmentDAO> appointments) {
        LOG.entering(LOG.getName(), "onPeriodicCheckFinished", appointments);
        HashMap<Integer, Item> notChecked = new HashMap<>();
        ArrayList<AppointmentModel> newItems = new ArrayList<>();
        ArrayList<AppointmentModel> dateChanged = new ArrayList<>();
        notChecked.putAll(allItems);
        boolean needsSort = appointments.stream().filter((t) -> {
            LOG.finest(() -> String.format("Processing %s", t));
            AppointmentModel model = t.cachedModel(true);
            int pk = t.getPrimaryKey();
            if (notChecked.containsKey(pk)) {
                LOG.finest("Appointment already exists in allItems");
                Item a = notChecked.remove(pk);
                if (a.dismissed) {
                    a.model = model;
                    if (!(a.start.equals(model.getStart()) && a.end.equals(model.getEnd()))) {
                        a.start = model.getStart();
                        a.end = model.getEnd();
                        a.dismissed = false;
                        LOG.finest(() -> String.format("Marking %s to be added to backingAlertList", a.model));
                        dateChanged.add(model);
                    }
                    return false;
                }
                if (a.model != model) {
                    if (null == a.snoozedUntil) {
                        LOG.finest(() -> String.format("Removing %s from backingAlertList", a.model));
                        backingAlertList.remove(a.model);
                        a.model = model;
                        a.start = model.getStart();
                        a.end = model.getEnd();
                        LOG.finest(() -> String.format("Marking %s to be added to backingAlertList", a.model));
                        dateChanged.add(model);
                        return false;
                    }
                    a.model = model;
                }
                if (!(a.start.equals(model.getStart()) && a.end.equals(model.getEnd()))) {
                    a.start = model.getStart();
                    a.end = model.getEnd();
                    if (null == a.snoozedUntil) {
                        LOG.finest("Appointment is date-change only");
                        return true;
                    }
                    LOG.finest(() -> String.format("Marking %s to be added to backingAlertList", a.model));
                    dateChanged.add(model);
                }
            } else {
                LOG.finest("Appointment does not yet exist in allItems");
                newItems.add(model);
            }
            return false;
        }).count() > 0 || !(dateChanged.isEmpty() && newItems.isEmpty());
        notChecked.keySet().forEach((t) -> {
            LOG.finest(() -> String.format("Removing %d from backingAlertList", t));
            Item item = allItems.remove(t);
            if (null != item) {
                if (null != item.snoozedUntil) {
                    LOG.finer(() -> String.format("Removing %s from snoozed item chain", item));
                    item.setSnoozedUntil(null);
                } else if (!item.dismissed) {
                    LOG.finest(() -> String.format("Removing %s from backingAlertList", item.model));
                    backingAlertList.remove(item.model);
                }
            }
        });
        LOG.finest("Adding marked items to backingAlertList");
        backingAlertList.addAll(dateChanged);
        newItems.forEach((t) -> {
            LOG.finer(() -> String.format("Adding %s to allItems and backingAlertList", t));
            allItems.put(t.getPrimaryKey(), new Item(t));
            backingAlertList.add(t);
        });
        if (backingAlertList.isEmpty()) {
            if (alerting.get()) {
                LOG.finer("Setting alerting to false");
                alerting.set(false);
            }
        } else {
            if (backingAlertList.size() > 1 && needsSort) {
                LOG.finer("Sorting backingAlertList");
                backingAlertList.sort(AppointmentHelper::compareByDates);
            }
            if (!alerting.get()) {
                LOG.finer("Setting alerting to true");
                alerting.set(true);
            }
        }
        LOG.exiting(LOG.getName(), "onPeriodicCheckFinished");
    }

    public synchronized void snooze(Appointment<?> appointment, Duration duration) {
        LOG.entering(LOG.getName(), "snooze", new Object[] {appointment, duration});
        int primaryKey = appointment.getPrimaryKey();
        if (allItems.containsKey(primaryKey)) {
            setSnooze(allItems.get(primaryKey), LocalDateTime.now().plus(duration));
        }
        LOG.exiting(LOG.getName(), "snooze");
    }

    public synchronized void snoozeAll(Duration duration) {
        LOG.entering(LOG.getName(), "snoozeAll", duration);
        LocalDateTime snoozedUntil = LocalDateTime.now().plus(duration);
        allItems.values().stream().filter((t) -> null == t.snoozedUntil && !t.dismissed).forEach((t) -> {
            t.setSnoozedUntil(snoozedUntil);
        });
        backingAlertList.clear();
        if (alerting.get()) {
            LOG.finer("Setting alerting to false");
            alerting.set(false);
        }
        LOG.exiting(LOG.getName(), "snoozeAll");
    }

    public synchronized void dismissAll() {
        LOG.entering(LOG.getName(), "dismissAll");
        allItems.values().stream().filter((t) -> null == t.snoozedUntil).forEach((t) -> t.dismissed = true);
        backingAlertList.clear();
        if (alerting.get()) {
            LOG.finer("Setting alerting to false");
            alerting.set(false);
        }
        LOG.exiting(LOG.getName(), "dismissAll");
    }

    public synchronized void dismiss(Appointment<?> appointment) {
        LOG.entering(LOG.getName(), "dismiss", appointment);
        int primaryKey = appointment.getPrimaryKey();
        if (allItems.containsKey(primaryKey)) {
            Item item = allItems.get(primaryKey);
            if (!item.dismissed) {
                item.dismissed = true;
                if (null == item.snoozedUntil) {
                    backingAlertList.remove(item.model);
                    if (backingAlertList.isEmpty() && alerting.get()) {
                        alerting.set(false);
                    }
                } else {
                    item.setSnoozedUntil(null);
                }
            }
        }
        LOG.exiting(LOG.getName(), "dismiss");
    }

    private class Item {

        private AppointmentModel model;
        private LocalDateTime snoozedUntil = null;
        private LocalDateTime start;
        private LocalDateTime end;
        private Item previousSnoozed;
        private Item nextSnoozed;
        private boolean dismissed = false;

        Item(AppointmentModel model) {
            this.model = Objects.requireNonNull(model);
            start = model.getStart();
            end = model.getEnd();
        }

        void onSnoozeCleared() {
            if (null == previousSnoozed) {
                if (null == (firstSnoozed = nextSnoozed)) {
                    lastSnoozed = null;
                } else {
                    firstSnoozed.previousSnoozed = nextSnoozed = null;
                }
            } else {
                if (null == (previousSnoozed.nextSnoozed = nextSnoozed)) {
                    lastSnoozed = previousSnoozed;
                } else {
                    nextSnoozed.previousSnoozed = previousSnoozed;
                    nextSnoozed = null;
                }
                previousSnoozed = null;
            }
        }

        void onSnoozeSet(Item target) {
            while (target.snoozedUntil.compareTo(snoozedUntil) > 0) {
                if (null == (target = target.previousSnoozed)) {
                    firstSnoozed = (nextSnoozed = firstSnoozed).previousSnoozed = this;
                    return;
                }
            }
            if (null == (nextSnoozed = (previousSnoozed = target).nextSnoozed)) {
                lastSnoozed = this;
            } else {
                nextSnoozed.previousSnoozed = this;
            }
            previousSnoozed.nextSnoozed = this;
        }

        /**
         *
         * @param value
         * @return {@link Optional} of {@code true} if item was {@link #snoozedUntil} was changed from a {@code null} value; {@link Optional} of {@code false} if
         * {@link #snoozedUntil} was changed to a {@code null} value; otherwise {@link Optional#EMPTY}.
         */
        Optional<Boolean> setSnoozedUntil(LocalDateTime value) {
            LOG.entering(getClass().getName(), "setSnoozedUntil", value);
            Optional<Boolean> result;
            if (null == value) {
                if (null != snoozedUntil) {
                    LOG.finer("Clearing snooze");
                    snoozedUntil = null;
                    onSnoozeCleared();
                    result = Optional.of(false);
                    LOG.exiting(LOG.getName(), "setSnoozedUntil", result);
                    return result;
                }
            } else {
                if (null == snoozedUntil) {
                    snoozedUntil = value;
                    if (null != lastSnoozed) {
                        onSnoozeSet(lastSnoozed);
                        result = Optional.of(true);
                        LOG.exiting(LOG.getName(), "setSnoozedUntil", result);
                        return result;
                    }
                } else if (!value.equals(snoozedUntil)) {
                    snoozedUntil = value;
                    onSnoozeCleared();
                    onSnoozeSet(lastSnoozed);
                }
            }
            result = Optional.empty();
            LOG.exiting(LOG.getName(), "setSnoozedUntil", result);
            return result;
        }

        @Override
        public synchronized String toString() {
            if (null == previousSnoozed) {
                if (null == nextSnoozed) {
                    return String.format("Item{model=%d, snoozedUntil=%s, start=%s, end=%s, previousSnoozed=(null), nextSnoozed=(null), dismissed=%s}",
                            model.getPrimaryKey(), (null == snoozedUntil) ? "(null)" : formatter.format(snoozedUntil), formatter.format(start), formatter.format(end), dismissed);
                }
                return String.format("Item{model=%d, snoozedUntil=%s, start=%s, end=%s, previousSnoozed=(null),%n"
                        + "\tnextSnoozed={model=%d, snoozedUntil=%s, start=%s, end=%s, dismissed=%s},%n"
                        + "\tdismissed=%s}", model.getPrimaryKey(), (null == snoozedUntil) ? "(null)" : formatter.format(snoozedUntil), formatter.format(start),
                        formatter.format(end), nextSnoozed.model.getPrimaryKey(), (null == nextSnoozed.snoozedUntil) ? "(null)" : formatter.format(nextSnoozed.snoozedUntil),
                        formatter.format(nextSnoozed.start), formatter.format(nextSnoozed.end), nextSnoozed.dismissed, dismissed);
            }
            if (null == nextSnoozed) {
                    return String.format("Item{model=%d, snoozedUntil=%s, start=%s, end=%s,%n"
                            + "\tpreviousSnoozed={model=%d, snoozedUntil=%s, start=%s, end=%s, dismissed=%s},%n"
                            + "\tnextSnoozed=(null), dismissed=%s}", model.getPrimaryKey(), (null == snoozedUntil) ? "(null)" : formatter.format(snoozedUntil),
                            formatter.format(start), formatter.format(end), previousSnoozed.model.getPrimaryKey(),
                            (null == previousSnoozed.snoozedUntil) ? "(null)" : formatter.format(previousSnoozed.snoozedUntil), formatter.format(previousSnoozed.start),
                            formatter.format(previousSnoozed.end), previousSnoozed.dismissed, dismissed);
            }
            return String.format("Item{model=%d, snoozedUntil=%s, start=%s, end=%s,%n"
                    + "\tpreviousSnoozed={model=%d, snoozedUntil=%s, start=%s, end=%s, dismissed=%s},%n"
                    + "\tnextSnoozed={model=%d, snoozedUntil=%s, start=%s, end=%s, dismissed=%s},%n"
                    + "\tdismissed=%s}", model.getPrimaryKey(), (null == snoozedUntil) ? "(null)" : formatter.format(snoozedUntil), formatter.format(start), formatter.format(end),
                    previousSnoozed.model.getPrimaryKey(), (null == previousSnoozed.snoozedUntil) ? "(null)" : formatter.format(previousSnoozed.snoozedUntil),
                    formatter.format(previousSnoozed.start), formatter.format(previousSnoozed.end), previousSnoozed.dismissed, nextSnoozed.model.getPrimaryKey(),
                    (null == nextSnoozed.snoozedUntil) ? "(null)" : formatter.format(nextSnoozed.snoozedUntil), formatter.format(nextSnoozed.start),
                    formatter.format(nextSnoozed.end), nextSnoozed.dismissed, dismissed);
        }

    }

    private class CheckAppointmentsTask extends TimerTask {

        private final UserDAO user;
        private final AppointmentDAO.FactoryImpl factory;

        private CheckAppointmentsTask() {
            user = Objects.requireNonNull(getCurrentUser());
            factory = AppointmentDAO.FACTORY;
        }

        @Override
        public void run() {
            LOG.entering(getClass().getName(), "run");
            if (setChecking(true)) {
                LocalDateTime start = LocalDateTime.now();
                setLastCheck(start);
                try {
                    LOG.fine("Connecting to database");
                    List<AppointmentDAO> appointments = DbConnector.apply((t) -> {
                        LOG.fine("Connected to database");
                        List<AppointmentDAO> result = factory.load(t, AppointmentFilter.of(AppointmentFilter.expressionOf(DateTimeUtil.toUtcTimestamp(start),
                                DateTimeUtil.toUtcTimestamp(start.plusMinutes(alertLeadtimeMinutes))).and(AppointmentFilter.expressionOf(user))));
                        LOG.fine(() -> String.format("Returning %s", result));
                        return result;
                    });
                    Platform.runLater(() -> onPeriodicCheckFinished(appointments));
                } catch (SQLException | ClassNotFoundException ex) {
                    LOG.log(Level.SEVERE, "Error checking impending appointments", ex);
                    Platform.runLater(() -> {
                        if (stop(false)) {
                            setFault(ex);
                        }
                    });
                } finally {
                    setChecking(false);
                }
            } else {
                LOG.warning("Another task is still running");
            }
            LOG.exiting(getClass().getName(), "run");
        }
    }

}
