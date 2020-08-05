package scheduler.view.appointment;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import scheduler.model.ModelHelper.AppointmentHelper;
import scheduler.model.fx.AppointmentModel;
import scheduler.util.LogHelper;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public final class AppointmentDay {

    private static final Logger LOG = LogHelper.setLoggerAndHandlerLevels(Logger.getLogger(AppointmentDay.class.getName()), Level.FINER);
//    private static final Logger LOG = Logger.getLogger(AppointmentDay.class.getName());

    public static Stream<AppointmentDay> create(AppointmentModel model) {
        Stream.Builder<AppointmentDay> builder = Stream.builder();
        LocalDate s = model.getStart().toLocalDate();
        LocalDate e = (model.getEnd().toLocalTime().equals(LocalTime.MIN)) ? model.getEnd().toLocalDate() : model.getEnd().toLocalDate().plusDays(1);
        for (LocalDate d = s; d.compareTo(e) < 0; d = d.plusDays(1)) {
            builder.accept(new AppointmentDay(model, d));
        }
        return builder.build();
    }

    public static Stream<TreeItem<AppointmentDay>> createBranches(Collection<AppointmentDay> collection) {
        HashMap<LocalDate, ArrayList<TreeItem<AppointmentDay>>> map = new HashMap<>();
        collection.forEach((t) -> {
            LocalDate d = t.date.get();
            if (map.containsKey(d)) {
                map.get(d).add(new TreeItem<>(t));
            } else {
                ArrayList<TreeItem<AppointmentDay>> list = new ArrayList<>();
                list.add(new TreeItem<>(t));
                map.put(d, list);
            }
        });

        return map.keySet().stream().sorted().map((t) -> {
            TreeItem<AppointmentDay> a = new TreeItem<>(new AppointmentDay(null, t));
            a.setExpanded(true);
            ObservableList<TreeItem<AppointmentDay>> c = a.getChildren();
            c.addAll(map.get(t));
            if (!c.isEmpty()) {
                c.get(0).getValue().firstItem.set(true);
                c.stream().skip(1).forEach((u) -> u.getValue().firstItem.set(false));
            }
            return a;
        });
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

    public static boolean importSourceChanges(ListChangeListener.Change<? extends AppointmentModel> sourceChange, ObservableList<AppointmentDay> target) {
        LOG.entering(LOG.getName(), "update");
        boolean hasChange = false;
        while (sourceChange.next()) {
            if (sourceChange.wasPermutated() || sourceChange.wasUpdated()) {
                LOG.finer(() -> String.format("Iterating change: wasPermutated=%s; wasUpdated=%s", sourceChange.wasPermutated(), sourceChange.wasUpdated()));
                continue;
            }

            LOG.finer(() -> String.format("Iterating change: wasAdded=%s; wasRemoved=%s; wasReplaced=%s", sourceChange.wasAdded(), sourceChange.wasRemoved(),
                    sourceChange.wasReplaced()));
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
                    LOG.fine("Nothing added or removed for current change iteration");
                    continue;
                }
            } else {
                target.removeAll(toRemove);
                if (toAdd.isEmpty()) {
                    LOG.finer(() -> String.format("%d items removed and 0 items added for current change iteration", toRemove.size()));
                    continue;
                }
            }
            target.addAll(toAdd);
            LOG.finer(() -> String.format("%d items removed and %d items added for current change iteration", toRemove.size(), toAdd.size()));
            hasChange = true;
        }
        LOG.exiting(LOG.getName(), "update", hasChange);
        return hasChange;
    }

    private final ReadOnlyObjectWrapper<LocalDate> date;
    private final ReadOnlyObjectWrapper<AppointmentModel> model;
    private final ReadOnlyBooleanWrapper firstItem;

    private AppointmentDay(AppointmentModel model, LocalDate date) {
        this.firstItem = new ReadOnlyBooleanWrapper(false);
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

    public boolean isFirstItem() {
        return firstItem.get();
    }

    public ReadOnlyBooleanProperty firstItemProperty() {
        return firstItem.getReadOnlyProperty();
    }

}
