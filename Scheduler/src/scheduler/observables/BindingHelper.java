package scheduler.observables;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.chrono.Chronology;
import java.time.format.FormatStyle;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.binding.ObjectExpression;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.util.StringConverter;
import javafx.util.converter.LocalDateStringConverter;
import scheduler.dao.DataAccessObject;
import scheduler.dao.DataRowState;
import scheduler.util.BinaryOptional;
import scheduler.util.BinarySelective;
import scheduler.util.Values;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class BindingHelper {

    private static final Logger LOG = Logger.getLogger(BindingHelper.class.getName());

    public static <T, U> BinarySelectiveBinding<T, U> createBinarySelectiveBinding(Callable<BinarySelective<T, U>> func, Observable... dependencies) {
        return new BinarySelectiveBinding<T, U>(dependencies) {
            @Override
            protected BinarySelective<T, U> computeValue() {
                try {
                    return func.call();
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Exception while evaluating binding", e);
                    return null;
                }
            }
        };
    }

    public static <T, U> BinaryOptionalBinding<T, U> createBinaryOptionalBinding(Callable<BinaryOptional<T, U>> func, Observable... dependencies) {
        return new BinaryOptionalBinding<T, U>(dependencies) {
            @Override
            protected BinaryOptional<T, U> computeValue() {
                try {
                    return func.call();
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Exception while evaluating binding", e);
                    return null;
                }
            }
        };
    }

    public static <T> OptionalBinding<T> createOptionalBinding(Callable<Optional<T>> func, Observable... dependencies) {
        return new OptionalBinding<T>(dependencies) {
            @Override
            protected Optional<T> computeValue() {
                try {
                    return func.call();
                } catch (Exception e) {
                    LOG.log(Level.WARNING, "Exception while evaluating binding", e);
                    return null;
                }
            }
        };
    }

    public static <T, U, S> BinarySelectiveBinding<U, S> test(final ObservableValue<T> observable, final Predicate<T> predicate,
            final Function<T, U> ifTrue, Supplier<S> ifFalse) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(ifTrue);
        Objects.requireNonNull(ifFalse);
        return createBinarySelectiveBinding(() -> {
            T value = observable.getValue();
            if (predicate.test(value)) {
                return BinarySelective.ofPrimary(ifTrue.apply(value));
            }
            return BinarySelective.ofSecondary(ifFalse.get());
        }, observable);
    }

    public static <T, U, S> BinarySelectiveBinding<U, S> test(final ObservableValue<T> observable, final Predicate<T> predicate,
            final Function<T, U> ifTrue, Function<T, S> ifFalse) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(ifTrue);
        Objects.requireNonNull(ifFalse);
        return createBinarySelectiveBinding(() -> {
            T value = observable.getValue();
            if (predicate.test(value)) {
                return BinarySelective.ofPrimary(ifTrue.apply(value));
            }
            return BinarySelective.ofSecondary(ifFalse.apply(value));
        }, observable);
    }

    public static <T, U> OptionalBinding<U> test(final ObservableValue<T> observable, final Predicate<T> predicate,
            final Function<T, U> ifTrue) {
        Objects.requireNonNull(predicate);
        Objects.requireNonNull(ifTrue);
        return createOptionalBinding(() -> {
            T value = observable.getValue();
            if (predicate.test(value)) {
                return Optional.of(ifTrue.apply(value));
            }
            return Optional.empty();
        }, observable);
    }

    private static final Pattern INT_PATTERN = Pattern.compile("^\\s*\\d{1,9}\\s*");
    public static BinaryOptionalBinding<Integer, ParseException> parseInt(final ObservableValue<String> observableString) {
        NumberFormat fmt = NumberFormat.getIntegerInstance();
        return createBinaryOptionalBinding(() -> {
            String s = observableString.getValue();
            String v;
            if (null == s || (v = s.trim()).isEmpty()) {
                return BinaryOptional.empty();
            }

            try {
                Matcher m = INT_PATTERN.matcher(v);
                if (!m.find())
                    throw new ParseException("Invalid number", s.length() - v.length());
                else if (m.end() < v.length())
                    throw new ParseException("Invalid number", m.end() + (s.length() - v.length()));
                Number parse = fmt.parse(v);
                return BinaryOptional.ofPrimary(parse.intValue());
            } catch (ParseException ex) {
                return BinaryOptional.ofSecondary(ex);
            }
        }, observableString);
    }

    public static BinaryOptionalBinding<LocalDate, DateTimeException> parseLocalDate(final ObservableValue<String> observableString,
            StringConverter<LocalDate> converter) {
        if (null == converter) {
            return parseLocalDate(observableString, new LocalDateStringConverter(FormatStyle.SHORT, null, Chronology.ofLocale(Locale.getDefault(Locale.Category.FORMAT))));
        }
        return createBinaryOptionalBinding(() -> {
            String s = observableString.getValue();
            if (null == s || (s = s.trim()).isEmpty()) {
                return BinaryOptional.empty();
            }

            try {
                LocalDate value = converter.fromString(s);
                return BinaryOptional.ofPrimary(value);
            } catch (DateTimeException ex) {
                return BinaryOptional.ofSecondary(ex);
            }
        }, observableString);
    }

    /**
     * A binding that maps a string value to a {@link BinaryOptional} value that will contain either a {@link URI} value if successfully parsed or a
     * {@link URISyntaxException} if there were parsing errors. If the input string is {@code null}, then this will be converted as
     * {@link BinaryOptional#EMPTY}.
     *
     * @param observableString A string binding to be parsed as a {@link URI}.
     * @return A {@link BinaryOptional} that will contain a {@link URI} or {@link URISyntaxException}, or will be {@link BinaryOptional#EMPTY}.
     */
    public static BinaryOptionalBinding<URI, URISyntaxException> parseURI(final ObservableValue<String> observableString) {
        return createBinaryOptionalBinding(() -> {
            String s = observableString.getValue();
            if (null == s) {
                return BinaryOptional.empty();
            }
            try {
                return BinaryOptional.ofPrimary(new URI(s));
            } catch (URISyntaxException ex) {
                return BinaryOptional.ofSecondary(ex);
            }
        }, observableString);
    }

    public static BinaryOptionalBinding<URL, Exception> asURLOrException(final ObservableValue<URI> uri) {
        return createBinaryOptionalBinding(() -> {
            URI u = uri.getValue();
            if (null == u) {
                return BinaryOptional.empty();
            }
            try {
                return BinaryOptional.ofPrimary(u.toURL());
            } catch (MalformedURLException | IllegalArgumentException ex) {
                return BinaryOptional.ofSecondary(ex);
            }
        }, uri);
    }

    public static <T> OptionalBinding<T> ifNotNullOrWhiteSpace(final ObservableValue<String> observableString,
            final Function<String, T> mapper) {
        return test(observableString, Values::isNotNullWhiteSpaceOrEmpty, mapper);
    }

    public static <T, U> BinarySelectiveBinding<T, U> ifNotNullOrWhiteSpace(final ObservableValue<String> observableString,
            final Function<String, T> notEmptyMapper, Supplier<U> emptyMapper) {
        return test(observableString, Values::isNotNullWhiteSpaceOrEmpty, notEmptyMapper, emptyMapper);
    }

    public static BinaryOptionalBinding<URL, Exception> parseUrl(final ObservableValue<String> observableString) {
        ObjectBinding<BinaryOptional<URI, URISyntaxException>> intermediate = parseURI(observableString);
        return createBinaryOptionalBinding(() -> {
            BinaryOptional<URI, URISyntaxException> opt = intermediate.get();
            if (opt.isPresent()) {
                if (opt.isPrimary()) {
                    try {
                        return BinaryOptional.ofPrimary(opt.getPrimary().toURL());
                    } catch (MalformedURLException | IllegalArgumentException ex) {
                        return BinaryOptional.ofSecondary(ex);
                    }
                }
                return BinaryOptional.ofSecondary(opt.getSecondary());
            }
            return BinaryOptional.empty();
        }, intermediate);
    }

    /**
     * Creates a new {@link javafx.beans.binding.StringBinding} that returns an string value with leading and trailing whitespace removed or an empty
     * string if the source value was null.
     *
     * @param observableString The {@link javafx.beans.property.StringProperty} to trim.
     * @return The new {@link javafx.beans.binding.StringBinding}.
     * @throws NullPointerException The source {@link javafx.beans.property.StringProperty} was {@code null}.
     */
    public static StringBinding asTrimmedAndNotNull(final ObservableValue<String> observableString) {
        return Bindings.createStringBinding(() -> Values.asNonNullAndTrimmed(observableString.getValue()), observableString);
    }

    public static StringBinding asNonNullAndWsNormalized(final ObservableValue<String> observableString) {
        return Bindings.createStringBinding(() -> Values.asNonNullAndWsNormalized(observableString.getValue()), observableString);
    }

    public static StringBinding nullIfEmptyOrWhiteSpaceOrTrimmed(final ObservableValue<String> observableString) {
        return Bindings.createStringBinding(() -> {
            String s = Values.asNonNullAndWsNormalized(observableString.getValue());
            return (Values.isNullWhiteSpaceOrEmpty(s)) ? null : s;
        }, observableString);
    }

    public static StringBinding nullIfEmptyOrWhiteSpace(final ObservableValue<String> observableString) {
        return Bindings.createStringBinding(() -> {
            String s = observableString.getValue();
            return (Values.isNullWhiteSpaceOrEmpty(s)) ? null : s;
        }, observableString);
    }

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if a given {@link StringProperty} is not null and contains at least one
     * non-whitespace character.
     *
     * @param observableString The {@link javafx.beans.property.StringProperty} to test.
     * @return The new {@link javafx.beans.binding.BooleanBinding}.
     * @throws NullPointerException The source {@link javafx.beans.property.StringProperty} was {@code null}.
     */
    public static BooleanBinding isNotNullOrWhiteSpace(final ObservableValue<String> observableString) {
        return Bindings.createBooleanBinding(() -> {
            String s = observableString.getValue();
            return null != s && !s.trim().isEmpty();
        }, observableString);
    }

    /**
     * Creates a new {@link BooleanBinding} that holds {@code true} if a given {@link StringProperty} is null, empty or contains all whitespace
     * characters.
     *
     * @param observableString The {@link javafx.beans.property.StringProperty} to test.
     * @return The new {@link javafx.beans.binding.BooleanBinding}.
     * @throws NullPointerException The source {@link javafx.beans.property.StringProperty} was {@code null}.
     */
    public static BooleanBinding isNullOrWhiteSpace(final ObservableValue<String> observableString) {
        return Bindings.createBooleanBinding(() -> {
            String s = observableString.getValue();
            return null == s || s.trim().isEmpty();
        }, observableString);
    }

    public static ObjectBinding<LocalDateTime> asLocalDateTime(final ObservableValue<LocalDate> date, final ObservableValue<Integer> hour,
            final ObjectExpression<Integer> minute) {
        return Bindings.createObjectBinding(() -> {
            LocalDate d = date.getValue();
            Integer h = hour.getValue();
            Integer m = minute.get();
            return (d == null || h == null || m == null) ? null : LocalDateTime.of(d, LocalTime.of(h, m, 0));
        }, date, hour, minute);
    }

    public static <R extends DataAccessObject> IntegerBinding primaryKeyBinding(final ObservableValue<R> observable) {
        return Bindings.createIntegerBinding(() -> {
            DataAccessObject record = observable.getValue();
            return (record == null || record.getRowState() == DataRowState.NEW) ? Integer.MIN_VALUE : record.getPrimaryKey();
        }, observable);
    }

}
