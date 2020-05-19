package scheduler.view.appointment;

import com.sun.javafx.collections.ImmutableObservableList;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.chrono.Chronology;
import java.time.format.FormatStyle;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.TimeZone;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyListProperty;
import javafx.beans.property.ReadOnlyListWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.DatePicker;
import javafx.util.Pair;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import scheduler.dao.DataAccessObject;
import scheduler.model.Customer;
import scheduler.model.User;
import scheduler.model.ui.CustomerItem;
import scheduler.model.ui.FxRecordModel;
import scheduler.model.ui.UserItem;
import scheduler.util.BinarySelective;
import scheduler.util.LogHelper;
import scheduler.util.ResourceBundleHelper;
import scheduler.util.TernarySelective;
import scheduler.view.annotations.GlobalizationResource;
import static scheduler.view.appointment.EditAppointmentResourceKeys.*;
import scheduler.view.customer.CustomerModel;
import scheduler.view.user.UserModel;

/**
 * Binding to validate date and time controls.
 * <p>
 * Produces a {@link TernarySelective} with one of 3 possible values:</p>
 * <dl>
 * <dt>{@link ZonedDateTime} value</dt>
 * <dd>Start date/time is valid and there are no schedule conflicts</dd>
 * <dt>A {@link Pair} with a {@link ZonedDateTime} key and a {@link String} message.</dt>
 * <dd>Start date/time is valid, but there are scheduling conflicts described by the string value.</dd>
 * <dt>{@link String} value</dt>
 * <dd>Invalidation message - describes why the a component of the date/time is not valid.</dd>
 * </dl>
 * <p>
 * This object also contains an {@link ObservableList} of {@link AppointmentModel} objects that represent a scheduling conflict so that the resulting
 * validation message can include scheduling conflicts if the is able to construct a {@link ZonedDateTime} from the input
 * {@link ObjectExpression}s.</p>
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
@GlobalizationResource("scheduler/view/appointment/EditAppointment")
public final class StartDateValidationBinding extends ObjectBinding<TernarySelective<ZonedDateTime, Pair<ZonedDateTime, String>, String>> {

    private static final Logger LOG = Logger.getLogger(StartDateValidationBinding.class.getName());
    private final ResourceBundle rb;
    private final ObjectExpression<CustomerModel> customer;
    private final ObjectExpression<UserModel> user;
    private final ReadOnlyListWrapper<AppointmentModel> conflictingAppointments;
    private final IntermediaryBinding intermediary;
    private final IntValueBinding customerConflictCount;
    private final IntValueBinding userConflictCount;
    private final MessageBinding message;
    private final ObjectValueBinding<ZonedDateTime> zonedDateTime;
    private final ObjectValueBinding<Pair<Boolean, String>> typeAndMessage;
    private final DateTimeValidBinding dateTimeValid;

    /**
     * Initializes a new {@code StartDateValidationBinding} object.
     *
     * @param customer The {@link ObjectExpression} that gets the currently selected {@link UserModel}.
     * @param user The {@link ObjectExpression} that gets the currently selected {@link CustomerModel}.
     * @param datePicker The {@link DatePicker} control that is used for getting a {@link LocalDate} value.
     * @param hourText The {@link ObjectExpression} that gets the 12-hour portion of the time as string.
     * @param minuteText The {@link ObjectExpression} that gets the minute portion of the time as string.
     * @param timeZone The {@link ObjectExpression} that gets the currently selected {@link TimeZone}.
     * @param isPm The {@link ObjectExpression} that returns {@link Boolean#TRUE} if the time is PM or {@link Boolean#FALSE} if it is AM.
     */
    public StartDateValidationBinding(ObjectExpression<CustomerModel> customer, ObjectExpression<UserModel> user,
            DatePicker datePicker, StringExpression hourText, StringExpression minuteText, ObjectExpression<TimeZone> timeZone,
            ObjectExpression<Boolean> isPm) {
        this.conflictingAppointments = new ReadOnlyListWrapper<>(FXCollections.observableArrayList());
        rb = ResourceBundleHelper.getBundle(getClass());
        this.customer = customer;
        this.user = user;
        intermediary = new IntermediaryBinding(datePicker, hourText, minuteText, timeZone, isPm);
        this.dateTimeValid = new DateTimeValidBinding();
        this.typeAndMessage = new ObjectValueBinding<>("typeAndMessage", (t) -> t.map(
                (p) -> new Pair<>(Boolean.FALSE, ""),
                (s) -> new Pair<>(Boolean.FALSE, s.getValue()),
                (u) -> new Pair<>(Boolean.TRUE, u)
        ));
        this.zonedDateTime = new ObjectValueBinding<>("zonedDateTime", (t) -> t.map(
                (p) -> {
                    assert p instanceof ZonedDateTime : "Type check failure on primary";
                    return p;
                },
                (s) -> {
                    assert s.getKey() instanceof ZonedDateTime : "Type check failure on secondary";
                    return s.getKey();
                },
                (u) -> (ZonedDateTime) null
        ));
        conflictingAppointments.addListener(intermediary::invalidated);
        customer.addListener(intermediary::invalidated);
        user.addListener(intermediary::invalidated);
        bind(intermediary);
        intermediary.invalidated(conflictingAppointments);
        customerConflictCount = new IntValueBinding("customerConflictCount", (Pair<Integer, Integer> i) -> i.getKey());
        userConflictCount = new IntValueBinding("userConflictCount", (Pair<Integer, Integer> i) -> i.getValue());
        message = new MessageBinding();
    }

    /**
     * Gets the list that contains appointments with conflicting schedules.
     * <p>
     * It is assumed that items added to this list will be associated with the {@link CustomerModel}, the {@link UserModel} or both.
     *
     * @return The {@link ObservableList} that contains {@link AppointmentModel} objects whose date ranges conflict with another date range.
     */
    public ObservableList<AppointmentModel> getConflictingAppointments() {
        return conflictingAppointments.get();
    }

    public ReadOnlyListProperty<AppointmentModel> conflictingAppointmentsProperty() {
        return conflictingAppointments.getReadOnlyProperty();
    }

    /**
     * Gets the number of customer appointments that conflict with the date range.
     *
     * @return The number of {@link AppointmentModel} items in the {@link #conflictingAppointments} {@link ObservableList} that fall within the target
     * date range that are associated with the target {@link CustomerModel}.
     */
    public int getCustomerConflictCount() {
        return customerConflictCount.get();
    }

    /**
     * Gets a property that returns the number of customer appointments that conflict with the date range.
     *
     * @return A {@link ReadOnlyProperty} that returns the number of {@link AppointmentModel} items in the
     * {@link #conflictingAppointments} {@link ObservableList} that fall within the target date range that are associated with the target
     * {@link CustomerModel}.
     */
    public ReadOnlyProperty<Number> customerConflictCountProperty() {
        return customerConflictCount;
    }

    /**
     * Gets a binding that returns the number of customer appointments that conflict with the date range.
     *
     * @return An {@link IntegerBinding} that returns the number of {@link AppointmentModel} items in the
     * {@link #conflictingAppointments} {@link ObservableList} that fall within the target date range that are associated with the target
     * {@link CustomerModel}.
     */
    public IntegerBinding customerConflictCountBinding() {
        return customerConflictCount;
    }

    /**
     * Gets the number of user appointments that conflict with the date range.
     *
     * @return The number of {@link AppointmentModel} items in the {@link #conflictingAppointments} {@link ObservableList} that fall within the target
     * date range that are associated with the target {@link UserModel}.
     */
    public int getUserConflictCount() {
        return userConflictCount.get();
    }

    /**
     * Gets a property that returns the number of user appointments that conflict with the date range.
     *
     * @return A {@link ReadOnlyProperty} that returns the number of {@link AppointmentModel} items in the
     * {@link #conflictingAppointments} {@link ObservableList} that fall within the target date range that are associated with the target
     * {@link UserModel}.
     */
    public ReadOnlyProperty<Number> userConflictCountProperty() {
        return userConflictCount;
    }

    /**
     * Gets a binding that returns the number of user appointments that conflict with the date range.
     *
     * @return An {@link IntegerBinding} that returns the number of {@link AppointmentModel} items in the
     * {@link #conflictingAppointments} {@link ObservableList} that fall within the target date range that are associated with the target
     * {@link UserModel}.
     */
    public IntegerBinding userConflictCountBinding() {
        return userConflictCount;
    }

    /**
     * Gets the date/time validation message.
     *
     * @return The validation message for building the target {@link ZonedDateTime} from the dependent bindings or an empty string if the
     * {@link ZonedDateTime} could be created and there are no {@link AppointmentModel} items in the
     * {@link #conflictingAppointments} {@link ObservableList}.
     */
    public String getMessage() {
        return message.get();
    }

    /**
     * Gets a property that returns the date/time validation message.
     *
     * @return A {@link ReadOnlyProperty} that returns the validation message for building the target {@link ZonedDateTime} from the dependent
     * bindings or an empty string if the {@link ZonedDateTime} could be created and there are no {@link AppointmentModel} items in the
     * {@link #conflictingAppointments} {@link ObservableList}.
     */
    public ReadOnlyProperty<String> messageProperty() {
        return message;
    }

    /**
     * Gets a binding that returns the date/time validation message.
     *
     * @return A {@link StringBinding} that returns the validation message for building the target {@link ZonedDateTime} from the dependent bindings
     * or an empty string if the {@link ZonedDateTime} could be created and there are no {@link AppointmentModel} items in the
     * {@link #conflictingAppointments} {@link ObservableList}.
     */
    public StringBinding messageBinding() {
        return message;
    }

    /**
     * Gets the constructed {@link ZonedDateTime}.
     *
     * @return The constructed {@link ZonedDateTime} or {@code null} if there were any parsing or range errors.
     */
    public ZonedDateTime getZonedDateTime() {
        return zonedDateTime.get();
    }

    /**
     * Gets a property that returns the constructed {@link ZonedDateTime}.
     *
     * @return A {@link ReadOnlyProperty} that returns the constructed {@link ZonedDateTime} or {@code null} if there were any parsing or range
     * errors.
     */
    public ReadOnlyProperty<ZonedDateTime> zonedDateTimeProperty() {
        return zonedDateTime;
    }

    /**
     * Gets a binding that returns the the constructed {@link ZonedDateTime}.
     *
     * @return An {@link ObjectBinding} that returns the constructed {@link ZonedDateTime} or {@code null} if there were any parsing or range errors.
     */
    public ObjectBinding<ZonedDateTime> zonedDateTimeBinding() {
        return zonedDateTime;
    }

    /**
     * Gets a pair of values that contains the message and a value to indicate if it is an error message.
     *
     * @return A {@link Pair} of values where the {@link String} value contains the message, and the {@link Boolean} value is {@code true} if the
     * message represents an error message; otherwise, the message is instructional.
     */
    public Pair<Boolean, String> getTypeAndMessage() {
        return typeAndMessage.get();
    }

    /**
     * Gets a property that returns a pair of values which contains the message and a value to indicate if it is an error message.
     *
     * @return {@link ReadOnlyProperty} that returns a {@link Pair} of values where the {@link String} value contains the message, and the
     * {@link Boolean} value is {@code true} if the message represents an error message; otherwise, the message is instructional.
     */
    public ReadOnlyProperty<Pair<Boolean, String>> typeAndMessageProperty() {
        return typeAndMessage;
    }

    /**
     * Gets a binding that returns a pair of values which contains the message and a value to indicate if it is an error message.
     *
     * @return An {@link ObjectBinding} that returns a {@link Pair} of values where the {@link String} value contains the message, and the
     * {@link Boolean} value is {@code true} if the message represents an error message; otherwise, the message is instructional.
     */
    public ObjectBinding<Pair<Boolean, String>> typeAndMessageBinding() {
        return typeAndMessage;
    }

    /**
     * Gets a value to indicate whether a date/time object could be constructed.
     *
     * @return {@code true} if the {@link ZonedDateTime} could be constructed or {@code false} if there were any parsing or range errors.
     */
    public boolean isDateTimeValid() {
        return dateTimeValid.get();
    }

    /**
     * Gets a property that returns a value to indicate whether a date/time object could be constructed.
     *
     * @return {@link ReadOnlyProperty} that returns {@code true} if the {@link ZonedDateTime} could be constructed or {@code false} if there were any
     * parsing or range errors.
     */
    public ReadOnlyProperty<Boolean> dateTimeValidProperty() {
        return dateTimeValid;
    }

    /**
     * Gets a binding that returns a value to indicate whether a date/time object could be constructed.
     *
     * @return An {@link ObjectBinding} that returns {@code true} if the {@link ZonedDateTime} could be constructed or {@code false} if there were any
     * parsing or range errors.
     */
    public BooleanBinding dateTimeValidBinding() {
        return dateTimeValid;
    }

    @Override
    protected TernarySelective<ZonedDateTime, Pair<ZonedDateTime, String>, String> computeValue() {
        LOG.info(String.format("%s invalidated", getClass().getName()));
        BinarySelective<Pair<ZonedDateTime, Pair<Integer, Integer>>, String> result = intermediary.get();
        if (result.isPrimary()) {
            LOG.info(String.format("intermediary.get().isPrimary() = true"));
            Pair<ZonedDateTime, Pair<Integer, Integer>> primary = result.getPrimary();
            LOG.info(String.format("primary.getKey()=%s", LogHelper.toLogText(primary.getKey())));
            Pair<Integer, Integer> value = primary.getValue();
            LOG.info(String.format("primary.getValue()=%s", LogHelper.toLogText(value)));
            int c = value.getKey();
            LOG.info(String.format("value.getKey()=%s", LogHelper.toLogText(c)));
            int u = value.getValue();
            LOG.info(String.format("value.getValue()=%s", LogHelper.toLogText(u)));
            switch (c) {
                case 0:
                    if (u == 1) {
                        return TernarySelective.ofSecondary(new Pair<>(primary.getKey(), rb.getString(RESOURCEKEY_CONFLICTCUSTOMER1)));
                    }
                    if (u > 1) {
                        return TernarySelective.ofSecondary(new Pair<>(primary.getKey(), String.format(rb.getString(RESOURCEKEY_CONFLICTUSERN), u)));
                    }
                    break;
                case 1:
                    switch (u) {
                        case 0:
                            return TernarySelective.ofSecondary(new Pair<>(primary.getKey(), rb.getString(RESOURCEKEY_CONFLICTCUSTOMER1)));
                        case 1:
                            return TernarySelective.ofSecondary(new Pair<>(primary.getKey(), rb.getString(RESOURCEKEY_CONFLICTCUSTOMER1USER1)));
                        default:
                            return TernarySelective.ofSecondary(new Pair<>(primary.getKey(), String.format(rb.getString(RESOURCEKEY_CONFLICTCUSTOMER1USERN), u)));
                    }
                default:
                    switch (u) {
                        case 0:
                            return TernarySelective.ofSecondary(new Pair<>(primary.getKey(), String.format(rb.getString(RESOURCEKEY_CONFLICTCUSTOMERN), c)));
                        case 1:
                            return TernarySelective.ofSecondary(new Pair<>(primary.getKey(), String.format(rb.getString(RESOURCEKEY_CONFLICTCUSTOMERNUSER1), c)));
                        default:
                            return TernarySelective.ofSecondary(new Pair<>(primary.getKey(), String.format(rb.getString(RESOURCEKEY_CONFLICTCUSTOMERNUSERN), c, u)));
                    }
            }
            return TernarySelective.ofPrimary(primary.getKey());
        }
        LOG.info("intermediary.get().isPrimary() = false");
        String rbKey = result.getSecondary();
        LOG.info(String.format("secondary.get() = %s", LogHelper.toLogText(rbKey)));
        return TernarySelective.ofTertiary(rb.getString(rbKey));
    }

    @Override
    public void dispose() {
        conflictingAppointments.removeListener(intermediary::invalidated);
        customer.removeListener(intermediary::invalidated);
        user.removeListener(intermediary::invalidated);
        super.unbind(intermediary);
    }

    @Override
    public ObservableList<?> getDependencies() {
        return FXCollections.singletonObservableList(intermediary);
    }

    private class IntermediaryBinding extends ObjectBinding<BinarySelective<Pair<ZonedDateTime, Pair<Integer, Integer>>, String>> {

        private final ObjectProperty<LocalDate> selectedDate;
        private final StringProperty startDateText;
        private final ObjectProperty<StringConverter<LocalDate>> converter;
        private final StringExpression hourText;
        private final StringExpression minuteText;
        private final ObjectExpression<TimeZone> timeZone;
        private final NumberFormat formatter;
        private final ObjectExpression<Boolean> isPm;
        private final ReadOnlyIntegerWrapper customerConflicts;
        private final ReadOnlyIntegerWrapper userConflicts;

        private IntermediaryBinding(DatePicker datePicker, StringExpression hourText, StringExpression minuteText,
                ObjectExpression<TimeZone> timeZone, ObjectExpression<Boolean> isPm) {
            customerConflicts = new ReadOnlyIntegerWrapper(0);
            userConflicts = new ReadOnlyIntegerWrapper(0);
            selectedDate = datePicker.valueProperty();
            startDateText = datePicker.getEditor().textProperty();
            converter = datePicker.converterProperty();
            this.hourText = hourText;
            this.minuteText = minuteText;
            this.timeZone = timeZone;
            this.isPm = isPm;
            formatter = NumberFormat.getIntegerInstance();
            bind(selectedDate, startDateText, converter, hourText, minuteText, timeZone, isPm, customerConflicts, userConflicts);
        }

        @SuppressWarnings("unchecked")
        private void invalidated(Observable observable) {
            ObservableList<AppointmentModel> list;
            CustomerModel sc;
            UserModel su;

            if (observable instanceof ReadOnlyObjectProperty) {
                list = conflictingAppointments;
                FxRecordModel<? extends DataAccessObject> obj = ((ReadOnlyObjectProperty<? extends FxRecordModel<? extends DataAccessObject>>) observable).get();
                if (null != obj) {
                    if (obj instanceof CustomerModel) {
                        sc = (CustomerModel) obj;
                        su = user.get();
                    } else {
                        su = (UserModel) obj;
                        sc = customer.get();
                    }
                } else {
                    su = user.get();
                    sc = customer.get();
                }
            } else {
                list = (ObservableList<AppointmentModel>) observable;
                su = user.get();
                sc = customer.get();
            }
            if (list.isEmpty()) {
                customerConflicts.set(0);
                userConflicts.set(0);
                return;
            }
            int cc;
            int uc;
            if (null == sc) {
                cc = 0;
                uc = (null == su) ? 0 : list.size();
            } else if (null == su) {
                cc = list.size();
                uc = 0;
            } else {
                cc = uc = 0;
                Iterator<AppointmentModel> iterator = list.iterator();
                int cpk = sc.getPrimaryKey();
                int upk = su.getPrimaryKey();
                while (iterator.hasNext()) {
                    AppointmentModel m = iterator.next();
                    CustomerItem<? extends Customer> cm = m.getCustomer();
                    UserItem<? extends User> um = m.getUser();
                    if (null == cm || cm.getPrimaryKey() != cpk) {
                        uc++;
                    } else {
                        cc++;
                        if (null != um && um.getPrimaryKey() == upk) {
                            uc++;
                        }
                    }
                }
            }
            if (cc != customerConflicts.get()) {
                customerConflicts.set(cc);
            }
            if (uc != userConflicts.get()) {
                userConflicts.set(uc);
            }
        }

        @Override
        protected BinarySelective<Pair<ZonedDateTime, Pair<Integer, Integer>>, String> computeValue() {
            LOG.info(String.format("%s invalidated", getClass().getName()));

            LocalDate d = selectedDate.get();
            LOG.info(String.format("selectedDate=%s", LogHelper.toLogText(d)));
            String t = startDateText.get();
            LOG.info(String.format("startDateText=%s", LogHelper.toLogText(t)));
            StringConverter<LocalDate> x = converter.get();
            LOG.info(String.format("converter=%s", LogHelper.toLogText(x)));
            String ht = hourText.get();
            LOG.info(String.format("hourText=%s", LogHelper.toLogText(ht)));
            String mt = minuteText.get();
            LOG.info(String.format("minuteText=%s", LogHelper.toLogText(mt)));
            TimeZone z = timeZone.get();
            LOG.info(String.format("timeZone=%s", LogHelper.toLogText(z)));
            Boolean p = isPm.get();
            LOG.info(String.format("isPm=%s", LogHelper.toLogText(p)));
            int c = customerConflicts.get();
            LOG.info(String.format("customerConflicts=%s", LogHelper.toLogText(c)));
            int u = userConflicts.get();
            LOG.info(String.format("userConflicts=%s", LogHelper.toLogText(u)));
            if (null == d) {
                if (null != t && !t.trim().isEmpty()) {
                    LOG.info("Date control has text, but no date");
                    return BinarySelective.ofSecondary(RESOURCEKEY_INVALIDSTARTDATE);
                }
                return BinarySelective.ofSecondary(RESOURCEKEY_STARTDATENOTSPECIFIED);
            }
            if (null != t && !t.trim().isEmpty()) {
                if (null == x) {
                    x = new LocalDateStringConverter(FormatStyle.SHORT, null, Chronology.from(d));
                }
                LOG.info("Parsing date string");
                LocalDate v;
                try {
                    v = x.fromString(t);
                } catch (DateTimeException ex) {
                    LOG.log(Level.FINE, ex, () -> "Caught date parse exception");
                    v = null;
                }
                LOG.info(String.format("Parsed date=%s", LogHelper.toLogText(v)));
                if (null == v) {
                    LOG.info("Parsed date not equal to original");
                    return BinarySelective.ofSecondary(RESOURCEKEY_INVALIDSTARTDATE);
                }
            }
            if (null == ht || ht.trim().isEmpty()) {
                return BinarySelective.ofSecondary(RESOURCEKEY_STARTHOURNOTSPECIFIED);
            }
            LOG.info("Parsing hour string");
            Number n;
            try {
                n = formatter.parse(ht);
            } catch (ParseException ex) {
                LOG.log(Level.INFO, ex, () -> "Caught hour parse exception");
                n = null;
            }
            int hv;
            if (null == n || (hv = n.intValue()) < 1 || hv > 12) {
                return BinarySelective.ofSecondary(RESOURCEKEY_INVALIDSTARTHOUR);
            }
            if (null == mt || mt.trim().isEmpty()) {
                return BinarySelective.ofSecondary(RESOURCEKEY_STARTMINUTENOTSPECIFIED);
            }
            LOG.info("Parsing minute string");
            try {
                n = formatter.parse(mt);
            } catch (ParseException ex) {
                LOG.log(Level.INFO, ex, () -> "Caught minute parse exception");
                n = null;
            }
            int mv;
            if (null == n || (mv = n.intValue()) < 0 || mv > 59) {
                return BinarySelective.ofSecondary(RESOURCEKEY_INVALIDSTARTMINUTE);
            }
            if (null == z) {
                return BinarySelective.ofSecondary(RESOURCEKEY_TIMEZONENOTSPECIFIED);
            }
            if (null == p) {
                return BinarySelective.ofSecondary(RESOURCEKEY_AMPMDESIGNATORNOTSPECIFIED);
            }
            return BinarySelective.ofPrimary(
                    new Pair<>(
                            ZonedDateTime.of(LocalDateTime.of(d, LocalTime.of((hv == 12) ? ((p) ? 12 : 0) : ((p) ? hv + 12 : hv), mv, 0, 0)),
                                    z.toZoneId()),
                            new Pair<>(c, u)
                    )
            );
        }

        @Override
        public ObservableList<?> getDependencies() {
            return new ImmutableObservableList<Observable>(selectedDate, startDateText, converter, hourText, minuteText, timeZone, isPm, customerConflicts,
                    userConflicts);
        }

        @Override
        public void dispose() {
            unbind(selectedDate, startDateText, converter, hourText, minuteText, timeZone, isPm, customerConflicts, userConflicts);
            super.dispose();
        }

    }

    private class MessageBinding extends StringBinding implements ReadOnlyProperty<String> {

        private final String name;

        MessageBinding() {
            this.name = "messageBinding";
            bind(StartDateValidationBinding.this);
        }

        @Override
        protected String computeValue() {
            return StartDateValidationBinding.this.get().map(
                    (t) -> "",
                    (u) -> u.getValue(),
                    (s) -> s
            );
        }

        @Override
        public Object getBean() {
            return StartDateValidationBinding.this;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.singletonObservableList(StartDateValidationBinding.this);
        }

        @Override
        public void dispose() {
            unbind(StartDateValidationBinding.this);
            super.dispose();
        }

    }

    private class IntValueBinding extends IntegerBinding implements ReadOnlyProperty<Number> {

        private final String name;
        private final ToIntFunction<Pair<Integer, Integer>> getValue;

        IntValueBinding(String name, ToIntFunction<Pair<Integer, Integer>> getValue) {
            this.name = name;
            this.getValue = getValue;
            bind(intermediary);
        }

        @Override
        protected int computeValue() {
            BinarySelective<Pair<ZonedDateTime, Pair<Integer, Integer>>, String> result = intermediary.get();
            if (result.isPrimary()) {
                return getValue.applyAsInt(result.getPrimary().getValue());
            }
            return 0;
        }

        @Override
        public Object getBean() {
            return StartDateValidationBinding.this;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.singletonObservableList(intermediary);
        }

        @Override
        public void dispose() {
            unbind(intermediary);
            super.dispose();
        }

    }

    private class ObjectValueBinding<U> extends ObjectBinding<U> implements ReadOnlyProperty<U> {

        private final String name;
        private final Function<TernarySelective<ZonedDateTime, Pair<ZonedDateTime, String>, String>, U> func;

        ObjectValueBinding(String name, Function<TernarySelective<ZonedDateTime, Pair<ZonedDateTime, String>, String>, U> func) {
            this.name = name;
            this.func = func;
            bind(StartDateValidationBinding.this);
        }

        @Override
        protected U computeValue() {
            return func.apply(StartDateValidationBinding.this.get());
        }

        @Override
        public Object getBean() {
            return StartDateValidationBinding.this;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.singletonObservableList(StartDateValidationBinding.this);
        }

        @Override
        public void dispose() {
            unbind(StartDateValidationBinding.this);
            super.dispose();
        }

    }

    private class DateTimeValidBinding extends BooleanBinding implements ReadOnlyProperty<Boolean> {

        private final String name;

        DateTimeValidBinding() {
            this.name = "dateTimeValidBinding";
            bind(StartDateValidationBinding.this);
        }

        @Override
        protected boolean computeValue() {
            return !StartDateValidationBinding.this.get().isTertiary();
        }

        @Override
        public Object getBean() {
            return StartDateValidationBinding.this;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public ObservableList<?> getDependencies() {
            return FXCollections.singletonObservableList(StartDateValidationBinding.this);
        }

        @Override
        public void dispose() {
            unbind(StartDateValidationBinding.this);
            super.dispose();
        }

    }

}
