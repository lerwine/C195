package scheduler.util;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import scheduler.App;
import scheduler.AppResources;

/**
 * Utility class for validating and normalizing values.
 *
 * @author lerwi
 */
public class Values {
    //<editor-fold defaultstate="collapsed" desc="String validation and normalization">

    public static final Pattern REGEX_NON_NORMAL_WHITESPACES = Pattern.compile(" \\s+|(?! )\\s+");

    /**
     * Ensures a {@link String} value is not null.
     *
     * @param value The source {@link String} value.
     * @return The {@code value} if not null; otherwise, an empty {@link String}.
     */
    public static String asNonNull(String value) {
        return (null == value) ? "" : value;
    }

    private static class NonNullStringSupplier implements Supplier<String> {

        private final Supplier<String> baseSupplier;

        protected String getBase() {
            return baseSupplier.get();
        }

        NonNullStringSupplier(Supplier<String> source) {
            this.baseSupplier = Objects.requireNonNull(source);
        }

        @Override
        public String get() {
            return asNonNull(baseSupplier.get());
        }
    }

    /**
     * Composes a {@link Supplier} that returns the non-null value result of the source {@link Supplier} or an empty string.
     *
     * @param valueSupplier The source {@link Supplier}.
     * @return A non-null {@link String} value derived from the source {@code valueSupplier}.
     */
    public static Supplier<String> asNonNull(Supplier<String> valueSupplier) {
        if (Objects.requireNonNull(valueSupplier) instanceof NonNullStringSupplier) {
            return valueSupplier;
        }
        return new NonNullStringSupplier(valueSupplier);
    }

    /**
     * Ensures a {@link String} value is not null and does not contain extraneous white space characters.
     *
     * @param value The source {@link String} value.
     * @return The {@code value} with extraneous white space characters removed if not null; otherwise, an empty {@link String}.
     */
    public static String asNonNullAndTrimmed(String value) {
        return (null == value) ? "" : value.trim();
    }

    private static class NonNullAndTrimmedSupplier extends NonNullStringSupplier {

        NonNullAndTrimmedSupplier(Supplier<String> source) {
            super(source);
        }

        @Override
        public String get() {
            return asNonNullAndTrimmed(getBase());
        }
    }

    /**
     * Composes a {@link Supplier} that returns the non-null, trimmed value result of the source {@link Supplier} or an empty string.
     *
     * @param valueSupplier The source {@link Supplier}.
     * @return A non-null, trimmed {@link String} value derived from the source {@code valueSupplier}.
     */
    public static Supplier<String> asNonNullAndTrimmed(Supplier<String> valueSupplier) {
        if (Objects.requireNonNull(valueSupplier) instanceof NonNullAndTrimmedSupplier) {
            return valueSupplier;
        }
        return new NonNullAndTrimmedSupplier(valueSupplier);
    }

    /**
     * Ensures a {@link String} value is not null and that all white space is normalized. Leading and trailing whitespace will be removed. Consecutive whitespace characters will be
     * replaced with a single space characters. Other whitespace characters will be replaced by a normal space character.
     *
     * @param value The source {@link String} value.
     * @return The {@code value} with white space normalized if not null; otherwise, an empty {@link String}.
     */
    public static String asNonNullAndWsNormalized(String value) {
        if (value == null) {
            return "";
        }
        String[] ws;
        if ((value = value.trim()).isEmpty() || (ws = REGEX_NON_NORMAL_WHITESPACES.split(value)).length == 1) {
            return value;
        }
        return String.join(" ", ws);
    }

    private static class NonNullAndWsNormalizedSupplier extends NonNullAndTrimmedSupplier {

        NonNullAndWsNormalizedSupplier(Supplier<String> source) {
            super(source);
        }

        @Override
        public String get() {
            String s = super.get();
            String[] ws;
            if (s.isEmpty() || (ws = REGEX_NON_NORMAL_WHITESPACES.split(s)).length == 1) {
                return s;
            }
            return String.join(" ", ws);
        }
    }

    /**
     * Composes a {@link Supplier} that returns the non-null, whitespace-normalized result of the source {@link Supplier} or an empty string. Leading and trailing whitespace will
     * be removed. Consecutive whitespace characters will be replaced with a single space characters. Other whitespace characters will be replaced by a normal space character.
     *
     * @param valueSupplier The source {@link Supplier}.
     * @return A non-null, whitespace-normalized {@link String} value derived from the source {@code valueSupplier}.
     */
    public static Supplier<String> asNonNullAndWsNormalized(Supplier<String> valueSupplier) {
        if (Objects.requireNonNull(valueSupplier) instanceof NonNullAndWsNormalizedSupplier) {
            return valueSupplier;
        }
        return new NonNullAndWsNormalizedSupplier(valueSupplier);
    }

    /**
     * Tests whether a string is null, empty or contains only white space characters.
     *
     * @param value The {@link String} to test.
     * @return {@code} true if the {@code value} is null, empty or contains only white space characters; otherwise {@code false} if it contains at least one non-whitespace
     * character.
     */
    public static boolean isNullWhiteSpaceOrEmpty(String value) {
        return (value == null || value.isEmpty() || value.codePoints().allMatch((c) -> Character.isWhitespace(c)));
    }

    /**
     * Ensures a string is not null and contains at least one non-whitespace character or else returns a default value.
     *
     * @param sourceValue The source {@link String} value.
     * @param defaultValue The default {@link String} value to return if the {@code sourceValue} is null, empty or contains only whitespace characters.
     * @return {@code sourceValue} if it is not null or empty and contains at least one non-whitespace character; otherwise the {@code defaultValue} is returned.
     */
    public static String nonWhitespaceOrDefault(String sourceValue, String defaultValue) {
        return (isNullWhiteSpaceOrEmpty(sourceValue)) ? defaultValue : sourceValue;
    }

    /**
     * Ensures a string is not null and contains at least one non-whitespace character or else returns a default value.
     *
     * @param sourceValue The source {@link String} value.
     * @param defaultValueSupplier The {@link Supplier} that returns the default value.
     * @return {@code sourceValue} if it is not null or empty and contains at least one non-whitespace character; otherwise the result from the {@code defaultValueSupplier} is
     * returned.
     */
    public static String nonWhitespaceOrDefault(String sourceValue, Supplier<String> defaultValueSupplier) {
        return (isNullWhiteSpaceOrEmpty(sourceValue)) ? defaultValueSupplier.get() : sourceValue;
    }

    /**
     * Composes a {@link Supplier} that returns the non-whitespace result of the source supplier or a supplied default value.
     *
     * @param sourceSupplier The source {@link Supplier}.
     * @param defaultSupplier The {@link Supplier} to use if the {@code sourceSupplier} returns a null value or does not contain any non-whitespace characters.
     * @return A {@link Supplier} that returns the non-whitespace result of the {@code sourceSupplier} or a value supplied by the {@code defaultSupplier}.
     */
    public static Supplier<String> nonWhitespaceOrDefault(Supplier<String> sourceSupplier, Supplier<String> defaultSupplier) {
        Objects.requireNonNull(sourceSupplier);
        Objects.requireNonNull(defaultSupplier);
        return () -> {
            String s = sourceSupplier.get();
            return (isNullWhiteSpaceOrEmpty(s)) ? defaultSupplier.get() : s;
        };
    }

    /**
     * Ensures a string is not null, has extraneous white space removed, and contains at least one non-whitespace character or else returns a default value.
     *
     * @param sourceValue The source {@link String} value.
     * @param defaultValue The default {@link String} value to return if the {@code sourceValue} is null, empty or contains only whitespace characters.
     * @return {@code sourceValue} with extraneous white space removed if it is not null or empty and contains at least one non-whitespace character; otherwise the
     * {@code defaultValue} is returned.
     */
    public static String toNonWhitespaceTrimmedOrDefault(String sourceValue, String defaultValue) {
        return (null == sourceValue || (sourceValue = sourceValue.trim()).isEmpty()) ? defaultValue : sourceValue;
    }

    /**
     * Composes a {@link Supplier} that returns the non-whitespace, trimmed result of the source supplier or a supplied default value.
     *
     * @param sourceSupplier The source {@link Supplier}.
     * @param defaultSupplier The {@link Supplier} to use if the {@code sourceSupplier} returns a null value or does not contain any non-whitespace characters.
     * @return A {@link Supplier} that returns the non-whitespace, trimmed result of the {@code sourceSupplier} or a value supplied by the {@code defaultSupplier}.
     */
    public static Supplier<String> toNonWhitespaceTrimmedOrDefault(Supplier<String> sourceSupplier, Supplier<String> defaultSupplier) {
        Objects.requireNonNull(sourceSupplier);
        Objects.requireNonNull(defaultSupplier);
        return () -> {
            String s = sourceSupplier.get();
            return (null == s || (s = s.trim()).isEmpty()) ? defaultSupplier.get() : s;
        };
    }

    /**
     * Checks that the specified {@link String} is not {@code null} and contains at least one non-whitespace character.
     *
     * @param value The value to check for at least one non-whitespace character.
     * @param message The detail message to be used in the event that a {@code NullPointerException} or {@link AssertionError} is thrown.
     * @return {@code value} if not {@code null} and contains at least one non-whitespace character.
     * @throws NullPointerException if {@code value} is {@code null}.
     * @throws AssertionError if {@code value} does not contain at least one non-whitespace character.
     */
    public static String requireNonWhitespace(String value, String message) {
        assert !Objects.requireNonNull(value, message).isEmpty()
                && value.codePoints().anyMatch((c) -> !Character.isWhitespace(c)) : message;
        return value;
    }

    /**
     * Checks that the specified {@link String} is not {@code null} and contains at least one non-whitespace character.
     *
     * @param value The value to check for at least one non-whitespace character.
     * @param messageSupplier The supplier of the detail message to be used in the event that a {@code NullPointerException} or {@link AssertionError} is thrown.
     * @return {@code value} if not {@code null} and contains at least one non-whitespace character.
     * @throws NullPointerException if {@code value} is {@code null}.
     * @throws AssertionError if {@code value} does not contain at least one non-whitespace character.
     */
    public static String requireNonWhitespace(String value, Supplier<String> messageSupplier) {
        assert !Objects.requireNonNull(value, messageSupplier).isEmpty()
                && value.codePoints().anyMatch((c) -> !Character.isWhitespace(c)) : messageSupplier.get();
        return value;
    }

    //</editor-fold>
    //<editor-fold defaultstate="collapsed" desc="Appointment type values">
    /**
     * The value of {@link scheduler.dao.Appointment#getType()} when the appointment is a phone-based meeting. {@link scheduler.dao.Appointment#getUrl()} returns the telephone
     * number encoded as a URL using the format "tel:+" + international_code + "-" + phone_number and {@link scheduler.dao.Appointment#getLocation()} returns an empty string for
     * this appointment type.
     */
    public static final String APPOINTMENTTYPE_PHONE = "phone";

    /**
     * The value of {@link scheduler.dao.Appointment#getType()} when the appointment is an online virtual meeting. {@link scheduler.dao.Appointment#getUrl()} returns the internet
     * address of the virtual meeting and {@link scheduler.dao.Appointment#getLocation()} returns an empty string for this appointment type.
     */
    public static final String APPOINTMENTTYPE_VIRTUAL = "virtual";

    /**
     * The value of {@link scheduler.dao.Appointment#getType()} when the appointment located at the customer address. {@link scheduler.dao.Appointment#getUrl()} and
     * {@link scheduler.dao.Appointment#getLocation()} return empty strings for this appointment type.
     */
    public static final String APPOINTMENTTYPE_CUSTOMER = "customer";

    /**
     * The value of {@link scheduler.dao.Appointment#getType()} when the appointment is at the home (USA) office. {@link scheduler.dao.Appointment#getUrl()} and
     * {@link scheduler.dao.Appointment#getLocation()} return empty strings for this appointment type.
     */
    public static final String APPOINTMENTTYPE_HOME = "home";

    /**
     * The value of {@link scheduler.dao.Appointment#getType()} when the appointment is at the Germany office. {@link scheduler.dao.Appointment#getUrl()} and
     * {@link scheduler.dao.Appointment#getLocation()} return empty strings for this appointment type.
     */
    public static final String APPOINTMENTTYPE_GERMANY = "germany";

    /**
     * The value of {@link scheduler.dao.Appointment#getType()} when the appointment is at the India office. {@link scheduler.dao.Appointment#getUrl()} and
     * {@link scheduler.dao.Appointment#getLocation()} return empty strings for this appointment type.
     */
    public static final String APPOINTMENTTYPE_INDIA = "india";

    /**
     * The value of {@link scheduler.dao.Appointment#getType()} when the appointment is at the Honduras office. {@link scheduler.dao.Appointment#getUrl()} and
     * {@link scheduler.dao.Appointment#getLocation()} return empty strings for this appointment type.
     */
    public static final String APPOINTMENTTYPE_HONDURAS = "honduras";

    /**
     * The value of {@link scheduler.dao.Appointment#getType()} when the appointment is at an explicit address returned by {@link scheduler.dao.Appointment#getLocation()}.
     * {@link scheduler.dao.Appointment#getUrl()} returns an empty string for this appointment type.
     */
    public static final String APPOINTMENTTYPE_OTHER = "other";

    public static String asValidAppointmentType(String value) {
        if (value != null) {
            if ((value = value.trim()).equalsIgnoreCase(APPOINTMENTTYPE_CUSTOMER)) {
                return APPOINTMENTTYPE_CUSTOMER;
            }
            if (value.equalsIgnoreCase(APPOINTMENTTYPE_GERMANY)) {
                return APPOINTMENTTYPE_GERMANY;
            }
            if (value.equalsIgnoreCase(APPOINTMENTTYPE_HOME)) {
                return APPOINTMENTTYPE_HOME;
            }
            if (value.equalsIgnoreCase(APPOINTMENTTYPE_HONDURAS)) {
                return APPOINTMENTTYPE_HONDURAS;
            }
            if (value.equalsIgnoreCase(APPOINTMENTTYPE_INDIA)) {
                return APPOINTMENTTYPE_INDIA;
            }
            if (value.equalsIgnoreCase(APPOINTMENTTYPE_PHONE)) {
                return APPOINTMENTTYPE_PHONE;
            }
            if (value.equalsIgnoreCase(APPOINTMENTTYPE_VIRTUAL)) {
                return APPOINTMENTTYPE_VIRTUAL;
            }
        }
        return APPOINTMENTTYPE_OTHER;
    }

    private static final ObservableMap<String, String> APPOINTMENT_TYPES = FXCollections.observableHashMap();
    private static ObservableMap<String, String> appointmentTypes = null;
    private static String appointmentTypesLocale = null;

    public static ObservableMap<String, String> getAppointmentTypes() {
        synchronized (APPOINTMENT_TYPES) {
            if (null == appointmentTypes) {
                appointmentTypes = FXCollections.unmodifiableObservableMap(APPOINTMENT_TYPES);
            } else if (null != appointmentTypesLocale && appointmentTypesLocale.equals(Locale.getDefault(Locale.Category.DISPLAY).toLanguageTag())) {
                return appointmentTypes;
            }
            Locale locale = Locale.getDefault(Locale.Category.DISPLAY);
            appointmentTypesLocale = locale.toLanguageTag();
            ResourceBundle rb = AppResources.getResources();
            Stream.of(Values.APPOINTMENTTYPE_PHONE, Values.APPOINTMENTTYPE_VIRTUAL, Values.APPOINTMENTTYPE_CUSTOMER, Values.APPOINTMENTTYPE_HOME,
                    Values.APPOINTMENTTYPE_GERMANY, Values.APPOINTMENTTYPE_INDIA, Values.APPOINTMENTTYPE_HONDURAS,
                    Values.APPOINTMENTTYPE_OTHER).forEach((String key) -> {
                        APPOINTMENT_TYPES.put(key, (rb.containsKey(key)) ? rb.getString(key) : key);
                    });
        }
        return appointmentTypes;
    }
    //</editor-fold>

    //<editor-fold defaultstate="collapsed" desc="Row state values">
    /**
     * Value of {@link scheduler.dao.DataObject#getRowState()} when the current data object has been deleted from the database.
     */
    public static final int ROWSTATE_DELETED = -1;

    /**
     * Value of {@link scheduler.dao.DataObject#getRowState()} when the current data object has not yet been added to the database.
     */
    public static final int ROWSTATE_NEW = 0;

    /**
     * Value of {@link scheduler.dao.DataObject#getRowState()} when the properties of the current data object has not been modified since it was last synchronized with the
     * database.
     */
    public static final int ROWSTATE_UNMODIFIED = 1;

    /**
     * Value of {@link scheduler.dao.DataObject#getRowState()} when the properties of the current data object differ from the data stored in the database.
     */
    public static final int ROWSTATE_MODIFIED = 2;

    public static int asValidRowState(int value) {
        switch (value) {
            case ROWSTATE_NEW:
            case ROWSTATE_UNMODIFIED:
            case ROWSTATE_MODIFIED:
            case ROWSTATE_DELETED:
                return value;
        }
        return (value < ROWSTATE_DELETED) ? ROWSTATE_DELETED : ROWSTATE_MODIFIED;
    }

    /**
     * Checks that the specified value is a valid data row state value.
     *
     * @param value The value to test.
     * @param message The detail message to be used in the event that an {@link AssertionError} is thrown.
     * @return {@code value} if it is equal to {@link #ROWSTATE_NEW}, {@link #ROWSTATE_UNMODIFIED}, {@link #ROWSTATE_MODIFIED} or {@link #ROWSTATE_DELETED}.
     * @throws AssertionError if {@code value} is not equal to {@link #ROWSTATE_NEW}, {@link #ROWSTATE_UNMODIFIED},
     * {@link #ROWSTATE_MODIFIED} or {@link #ROWSTATE_DELETED}.
     */
    public static int requireValidRowState(int value, String message) {
        assert value == ROWSTATE_NEW || value == ROWSTATE_UNMODIFIED || value == ROWSTATE_MODIFIED || value == ROWSTATE_DELETED :
                nonWhitespaceOrDefault(message, "Invalid row state value");
        return value;
    }

    /**
     * Checks that the specified value is a valid data row state value.
     *
     * @param value The value to test.
     * @param messageSupplier The supplier of the detail message to be used in the event that an {@link AssertionError} is thrown.
     * @return {@code value} if it is equal to {@link #ROWSTATE_NEW}, {@link #ROWSTATE_UNMODIFIED}, {@link #ROWSTATE_MODIFIED} or {@link #ROWSTATE_DELETED}.
     * @throws AssertionError if {@code value} is not equal to {@link #ROWSTATE_NEW}, {@link #ROWSTATE_UNMODIFIED},
     * {@link #ROWSTATE_MODIFIED} or {@link #ROWSTATE_DELETED}.
     */
    public static int requireValidRowState(int value, Supplier<String> messageSupplier) {
        assert value == ROWSTATE_NEW || value == ROWSTATE_UNMODIFIED || value == ROWSTATE_MODIFIED || value == ROWSTATE_DELETED :
                nonWhitespaceOrDefault(messageSupplier, () -> "Invalid row state value");
        return value;
    }

    //</editor-fold>
    public static final String OPERATOR_EQUALS = "=";
    public static final String OPERATOR_NOT_EQUALS = "<>";
    public static final String OPERATOR_LIKE = "LIKE";
    public static final String OPERATOR_NOT_LIKE = "NOT LIKE";
    public static final String OPERATOR_STARTS_WITH = "LIKE ?%";
    public static final String OPERATOR_ENDS_WITH = "LIKE %?";
    public static final String OPERATOR_CONTAINS = "LIKE %?%";
    public static final String OPERATOR_GREATER_THAN = ">";
    public static final String OPERATOR_NOT_GREATER_THAN = "<=";
    public static final String OPERATOR_LESS_THAN = "<";
    public static final String OPERATOR_NOT_LESS_THAN = ">=";
    public static final String OPERATOR_NONE = "";

    //<editor-fold defaultstate="collapsed" desc="User Status values">
    /**
     * Value of {@link scheduler.dao.User#getStatus()} for an inactive status.
     */
    public static final short USER_STATUS_INACTIVE = 0;

    /**
     * Value of {@link scheduler.dao.User#getStatus()} for a normal user account.
     */
    public static final short USER_STATUS_NORMAL = 1;

    /**
     * Value of {@link scheduler.dao.User#getStatus()} for an administrative user account.
     */
    public static final short USER_STATUS_ADMIN = 2;

    /**
     * Ensures an integer valid is a valid user status value.
     *
     * @param value The source value.
     * @return {@link #USER_STATUS_NORMAL}, {@link #USER_STATUS_ADMIN} or {@link #USER_STATUS_INACTIVE}.
     */
    public static int asValidUserStatus(int value) {
        switch (value) {
            case USER_STATUS_INACTIVE:
            case USER_STATUS_NORMAL:
            case USER_STATUS_ADMIN:
                return value;
        }
        return (value < USER_STATUS_INACTIVE) ? USER_STATUS_INACTIVE : USER_STATUS_NORMAL;
    }

    public static String toUserStatusDisplay(int value) {
        switch (value) {
            case USER_STATUS_INACTIVE:
                return AppResources.getResourceString(AppResources.RESOURCEKEY_INACTIVE);
            case USER_STATUS_NORMAL:
                return AppResources.getResourceString(AppResources.RESOURCEKEY_ACTIVE);
            case USER_STATUS_ADMIN:
                return AppResources.getResourceString(AppResources.RESOURCEKEY_ADMINISTRATOR);
        }
        return String.format("%s: %d", AppResources.getResourceString(AppResources.RESOURCEKEY_UNKNOWN), value);
    }

    public static String toAppointmentTypeDisplay(String type) {
        if (null == type || (type = type.trim()).isEmpty()) {
            return AppResources.getResourceString(AppResources.RESOURCEKEY_NONE);
        }
        ObservableMap<String, String> map = getAppointmentTypes();
        if (map.containsKey(type)) {
            return map.get(type);
        }
        return String.format("%s: %s", AppResources.getResourceString(AppResources.RESOURCEKEY_UNKNOWN), type);
    }

    /**
     * Checks that the specified value is a valid {@link scheduler.dao.User} status value.
     *
     * @param value The value to test.
     * @param message The detail message to be used in the event that an {@link AssertionError} is thrown.
     * @return {@code value} if it is equal to {@link #USER_STATUS_NORMAL}, {@link #USER_STATUS_ADMIN} or {@link #USER_STATUS_INACTIVE}.
     * @throws AssertionError if {@code value} is not equal to {@link #USER_STATUS_NORMAL}, {@link #USER_STATUS_ADMIN} or {@link #USER_STATUS_INACTIVE}.
     */
    public static int requireValidUserStatus(int value, String message) {
        assert (value == USER_STATUS_INACTIVE || value == USER_STATUS_NORMAL || value == USER_STATUS_ADMIN) : nonWhitespaceOrDefault(message, "Invalid user status value");
        return value;
    }

    /**
     * Checks that the specified value is a valid {@link scheduler.dao.User} status value.
     *
     * @param value The value to test.
     * @param messageSupplier The supplier of the detail message to be used in the event that an {@link AssertionError} is thrown.
     * @return {@code value} if it is equal to {@link #USER_STATUS_NORMAL}, {@link #USER_STATUS_ADMIN} or {@link #USER_STATUS_INACTIVE}.
     * @throws AssertionError if {@code value} is not equal to {@link #USER_STATUS_NORMAL}, {@link #USER_STATUS_ADMIN} or {@link #USER_STATUS_INACTIVE}.
     */
    public static int requireValidUserStatus(int value, Supplier<String> messageSupplier) {
        assert (value == USER_STATUS_INACTIVE || value == USER_STATUS_NORMAL || value == USER_STATUS_ADMIN) :
                nonWhitespaceOrDefault(messageSupplier, () -> "Invalid user status value");
        return value;
    }

    /**
     * Checks that the specified value is not present or is a valid {@link scheduler.dao.User} status value.
     *
     * @param value The value to test.
     * @param message The detail message to be used in the event that an {@link AssertionError} is thrown.
     * @return {@code value} if it is not present or is equal to {@link #USER_STATUS_NORMAL}, {@link #USER_STATUS_ADMIN} or {@link #USER_STATUS_INACTIVE}.
     * @throws AssertionError if {@code value} is present and is not equal to {@link #USER_STATUS_NORMAL}, {@link #USER_STATUS_ADMIN} or {@link #USER_STATUS_INACTIVE}.
     */
    public static Optional<Integer> requireValidUserStatus(Optional<Integer> value, String message) {
        value.ifPresent((t) -> requireValidUserStatus(t, message));
        return value;
    }

    /**
     * Checks that the specified value is not present or is a valid {@link scheduler.dao.User} status value.
     *
     * @param value The value to test.
     * @param messageSupplier The supplier of the detail message to be used in the event that an {@link AssertionError} is thrown.
     * @return {@code value} if it is not present or is equal to {@link #USER_STATUS_NORMAL}, {@link #USER_STATUS_ADMIN} or {@link #USER_STATUS_INACTIVE}.
     * @throws AssertionError if {@code value} is present and is not equal to {@link #USER_STATUS_NORMAL}, {@link #USER_STATUS_ADMIN} or {@link #USER_STATUS_INACTIVE}.
     */
    public static Optional<Integer> requireValidUserStatus(Optional<Integer> value, Supplier<String> messageSupplier) {
        value.ifPresent((t) -> requireValidUserStatus(t, messageSupplier));
        return value;
    }

    //</editor-fold>
}
