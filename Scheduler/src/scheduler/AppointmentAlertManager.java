package scheduler;

import com.sun.javafx.event.EventHandlerManager;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventDispatchChain;
import javafx.event.EventTarget;
import static scheduler.Scheduler.getCurrentUser;
import scheduler.dao.AppointmentDAO;
import scheduler.dao.UserDAO;
import scheduler.dao.filter.AppointmentFilter;
import scheduler.model.ui.AppointmentModel;
import scheduler.util.AlertHelper;
import scheduler.util.DB;
import scheduler.util.DbConnector;
import scheduler.util.Tuple;
import events.AppointmentEvent;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 * @todo Implement {@code scheduler.AppointmentAlertManager}
 */
public class AppointmentAlertManager implements EventTarget {

    private static final Logger LOG = Logger.getLogger(AppointmentAlertManager.class.getName());

    public static final AppointmentAlertManager INSTANCE = new AppointmentAlertManager();

    private final EventHandlerManager eventHandlerManager;
    private final int alertLeadtime;
    private final int checkFrequency;
    private final ObservableList<AppointmentModel> alertingList;
    private final HashMap<Integer, LocalDateTime> dismissedMap;
    private final HashMap<Integer, Tuple<LocalDateTime, AppointmentModel>> snoozedMap;
    private final ReadOnlyBooleanWrapper alerting;
    private Timer appointmentCheckTimer;

    // Singleton
    private AppointmentAlertManager() {
        alerting = new ReadOnlyBooleanWrapper(false);
        alertingList = FXCollections.observableArrayList();
        snoozedMap = new HashMap<>();
        dismissedMap = new HashMap<>();
        eventHandlerManager = new EventHandlerManager(this);
        int i;
        try {
            i = AppResources.getAppointmentAlertLeadTime();
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, "Error getting alert lead time from settings", ex);
            i = 15;
        }
        alertLeadtime = i;
        try {
            i = AppResources.getAppointmentCheckFrequency();
        } catch (ParseException ex) {
            LOG.log(Level.SEVERE, "Error getting alert lead time from settings", ex);
            i = 2;
        }
        checkFrequency = i;
    }

    public ObservableList<AppointmentModel> getAlertingList() {
        return alertingList;
    }

    public boolean isAlerting() {
        return alerting.get();
    }

    public ReadOnlyBooleanProperty alertingProperty() {
        return alerting.getReadOnlyProperty();
    }

    @Override
    public EventDispatchChain buildEventDispatchChain(EventDispatchChain tail) {
        return tail.append(eventHandlerManager);
    }

    private AppointmentModel getModel(int key) {
        if (snoozedMap.containsKey(key)) {
            return snoozedMap.get(key).getValue2();
        }
        for (AppointmentModel model : alertingList) {
            if (model.getPrimaryKey() == key) {
                return model;
            }
        }
        return null;
    }

    private synchronized boolean checkInsert(AppointmentDAO dao, boolean sortOnChange) {
        LocalDateTime start = LocalDateTime.now();
        if (start.compareTo(DB.toLocalDateTime(dao.getEnd())) < 0) {
            LocalDateTime end = start.plusMinutes(alertLeadtime);
            if (end.compareTo(DB.toLocalDateTime(dao.getStart())) >= 0) {
                alertingList.add(new AppointmentModel(dao));
                if (sortOnChange) {
                    alertingList.sort(AppointmentModel::compareByDates);
                }
                return alertingList.size() == 1;
            }
        }
        return false;
    }

    private synchronized boolean checkUpdate(AppointmentDAO dao, boolean sortOnChange) {
        int pk = dao.getPrimaryKey();
        if (dismissedMap.containsKey(pk)) {
            dismissedMap.remove(pk);
        }
        if (snoozedMap.containsKey(pk)) {
            return false;
        }
        AppointmentModel item = getModel(pk);
        if (null == item) {
            return checkInsert(dao, sortOnChange);
        }
        LocalDateTime now = LocalDateTime.now();
        if (now.compareTo(item.getEnd()) < 0 && now.plusMinutes(alertLeadtime).compareTo(item.getStart()) >= 0) {
            if (alertingList.contains(item) && sortOnChange) {
                alertingList.sort(AppointmentModel::compareByDates);
            }
        } else if (alertingList.contains(item)) {
            alertingList.remove(item);
            return alertingList.isEmpty();
        }
        return false;
    }

    private synchronized boolean checkDelete(int pk) {
        if (dismissedMap.containsKey(pk)) {
            dismissedMap.remove(pk);
            return false;
        }
        if (snoozedMap.containsKey(pk)) {
            snoozedMap.remove(pk);
            return false;
        }
        for (AppointmentModel model : alertingList) {
            if (model.getPrimaryKey() == pk) {
                alertingList.remove(model);
                return alertingList.isEmpty();
            }
        }
        return false;
    }

    private void onAppointmentInserted(AppointmentEvent event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        // XXX: Check to see if we need to get model, instead
        if (checkInsert(event.getDataAccessObject(), true)) {
            alerting.set(true);
        }
    }

    private void onAppointmentUpdated(AppointmentEvent event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        // XXX: Check to see if we need to get model, instead
        if (checkUpdate(event.getDataAccessObject(), true)) {
            alerting.set(!alertingList.isEmpty());
        }
    }

    private void onAppointmentDeleted(AppointmentEvent event) {
        LOG.fine(() -> String.format("%s event handled", event.getEventType().getName()));
        // XXX: Check to see if we need to get model, instead
        if (checkDelete(event.getDataAccessObject().getPrimaryKey())) {
            alerting.set(false);
        }
    }

    public void start() {
        start(true);
    }

    private synchronized void start(boolean isInitial) {
        eventHandlerManager.addEventFilter(AppointmentEvent.INSERTED_EVENT_TYPE, this::onAppointmentInserted);
        eventHandlerManager.addEventFilter(AppointmentEvent.UPDATED_EVENT_TYPE, this::onAppointmentUpdated);
        eventHandlerManager.addEventFilter(AppointmentEvent.DELETED_EVENT_TYPE, this::onAppointmentDeleted);
        if (null != appointmentCheckTimer) {
            if (isInitial) {
                return;
            }
            appointmentCheckTimer.purge();
        }
        appointmentCheckTimer = new Timer();
        appointmentCheckTimer.schedule(new CheckAppointmentsTask(alertLeadtime), 0, (long) checkFrequency * 60_000L);
    }

    private synchronized boolean stop(boolean isPermanent) {
        eventHandlerManager.removeEventFilter(AppointmentEvent.INSERTED_EVENT_TYPE, this::onAppointmentInserted);
        eventHandlerManager.removeEventFilter(AppointmentEvent.UPDATED_EVENT_TYPE, this::onAppointmentUpdated);
        eventHandlerManager.removeEventFilter(AppointmentEvent.DELETED_EVENT_TYPE, this::onAppointmentDeleted);
        if (null == appointmentCheckTimer) {
            return false;
        }
        appointmentCheckTimer.cancel();
        if (isPermanent) {
            appointmentCheckTimer.purge();
            appointmentCheckTimer = null;
        }
        return true;
    }

    private void onCheckAppointmentsTaskError(Throwable ex) {
        if (stop(false)) {
            try {
                LOG.log(Level.SEVERE, "Error while checking for new appointments", ex);
                AlertHelper.showErrorAlert("Error loading appointments",
                        String.format("An unexpected error occurred while checking for upcoming appointments: %s", ex));
            } finally {
                start(false);
            }
        }
    }

    private synchronized boolean checkPeriodicCheckResult(List<AppointmentDAO> appointments) {
        final LocalDateTime now = LocalDateTime.now();
        boolean wasEmpty = alertingList.isEmpty();
        snoozedMap.keySet().stream().filter((t) -> snoozedMap.get(t).getValue1().compareTo(now) <= 0).forEach((t) -> {
            alertingList.add(snoozedMap.get(t).getValue2());
            snoozedMap.remove(t);
        });
        HashMap<Integer, AppointmentModel> notChecked = new HashMap<>();
        if (!alertingList.isEmpty()) {
            alertingList.forEach((t) -> notChecked.put(t.getPrimaryKey(), t));
        }
        appointments.forEach((t) -> {
            int pk = t.getPrimaryKey();
            if (!dismissedMap.containsKey(pk) && !snoozedMap.containsKey(pk)) {
                if (notChecked.containsKey(pk)) {
                    notChecked.remove(pk);
                }
                checkUpdate(t, false);
            }
        });
        if (!notChecked.isEmpty()) {
            notChecked.keySet().forEach((t) -> alertingList.remove(notChecked.get(t)));
        }
        dismissedMap.keySet().stream().filter((t) -> dismissedMap.get(t).compareTo(now) < 0).forEach((t) -> dismissedMap.remove(t));
        if (alertingList.isEmpty()) {
            return !wasEmpty;
        }
        if (alertingList.size() > 1) {
            alertingList.sort(AppointmentModel::compareByDates);
        }
        return wasEmpty;
    }

    private void onPeriodicCheckFinished(List<AppointmentDAO> appointments) {
        if (checkPeriodicCheckResult(appointments)) {
            alerting.set(!alertingList.isEmpty());
        }
    }

    private class CheckAppointmentsTask extends TimerTask {

        private final UserDAO user;
        private final AppointmentDAO.FactoryImpl factory;
        private final int alertLeadTime;

        private CheckAppointmentsTask(int alertLeadTime) {
            this.alertLeadTime = alertLeadTime;
            user = Objects.requireNonNull(getCurrentUser());
            factory = AppointmentDAO.FACTORY;
        }

        @Override
        public void run() {
            List<AppointmentDAO> appointments;
            try {
                appointments = DbConnector.apply((t) -> {
                    LocalDateTime start = LocalDateTime.now();
                    return factory.load(t, AppointmentFilter.of(AppointmentFilter.expressionOf(DB.toUtcTimestamp(start),
                            DB.toUtcTimestamp(start.plusMinutes(alertLeadTime))).and(AppointmentFilter.expressionOf(user))));
                });
            } catch (SQLException | ClassNotFoundException ex) {
                LOG.log(Level.SEVERE, "Error checking impending appointments", ex);
                Platform.runLater(() -> onCheckAppointmentsTaskError(ex));
                return;
            }
            Platform.runLater(() -> onPeriodicCheckFinished(appointments));
        }
    }

}
