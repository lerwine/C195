package scheduler.fx;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * CSS class names defined in {@code scheduler/default.css}.
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum CssClassName {
    /**
     * The {@code "symbol"} CSS class for nodes that display text using the {@code Segoe UI Symbol} font.
     */
    SYMBOL("symbol"),
    /**
     * The {@code "symbol-button"} CSS class for buttons that display text using the {@code Segoe UI Symbol} font.
     */
    SYMBOL_BUTTON("symbol-button"),
    /**
     * The {@code "symbol-button"} CSS class for buttons that display text using the {@code Segoe UI Symbol} font.
     */
    SMALL_CONTROL("small-control"),
    /**
     * The {@code "collapsed"} CSS class that sets visibility to {@code false} and collapses the height and width to zero.
     */
    COLLAPSED("collapsed"),
    /**
     * The {@code "bordered"} CSS class for nodes that will contain a normal border.
     */
    BORDERED("bordered"),
    /**
     * The {@code "message"} CSS class for bold text.
     */
    MESSAGE("message"),
    /**
     * The {@code "error"} CSS class for nodes that display in red text, aligned to the upper-left and have no padding.
     */
    ERROR("error"),
    /**
     * The {@code "warning"} CSS class for nodes that display in yellow italicized text.
     */
    WARNING("warning"),
    /**
     * The {@code "information"} CSS class for nodes that display in diminished-color italicized text.
     */
    INFO("information"),
    /**
     * The {@code "error"} CSS class for nodes that display in red text, aligned to the upper-left and have no padding.
     */
    PROGRESS("progress"),
    /**
     * The {@code "validationMessage"} CSS class for nodes that display in red text.
     */
    VALIDATIONMSG("validationMessage"),
    /**
     * The {@code "leftControlLabel"} CSS class for {@link javafx.scene.control.Labeled} nodes aligned to the left of the control they label.
     */
    LEFTCONTROLLABEL("leftControlLabel"),
    /**
     * The {@code "innerLeftControlLabel"} CSS class for {@link javafx.scene.control.Labeled} nodes aligned to the left of the control they label.
     */
    INNERLEFTCONTROLLABEL("innerLeftControlLabel"),
    /**
     * The {@code "leftLabeledControl"} CSS class for nodes aligned to the right their label.
     */
    LEFTLABELEDCONTROL("leftLabeledControl"),
    /**
     * The {@code "topControlLabel"} CSS class for {@link javafx.scene.control.Labeled} nodes aligned to the left of the control they label.
     */
    TOPCONTROLLABEL("topControlLabel"),
    /**
     * The {@code "topLabeledControl"} CSS class for nodes aligned to the right their label.
     */
    TOPLABELEDCONTROL("topLabeledControl"),
    /**
     * The {@code "h1"} CSS class.
     */
    H1("h1"),
    /**
     * The {@code "h2"} CSS class.
     */
    H2("h2"),
    /**
     * The {@code "h3"} CSS class.
     */
    H3("h3"),
    /**
     * The {@code "normal-font"} CSS class.
     */
    NORMAL_FONT("normal-font"),
    /**
     * The {@code "boldText"} CSS class for bold text.
     */
    BOLD_TEXT("boldText"),
    /**
     * The {@code "first-item"} CSS class.
     */
    FIRST_ITEM("first-item"),
    /**
     * The {@code "day-group"} CSS class.
     */
    DAY_GROUP("day-group"),
    /**
     * The {@code "appointments"} CSS class.
     */
    APPOINTMENTS("appointments"),
    /**
     * The {@code "month-gridpane"} CSS class for bold text.
     */
    MONTH_GRIDPANE("month-gridpane"),
    /**
     * The {@code "empty-cell"} CSS class for bold text.
     */
    EMPTY_CELL("empty-cell"),
    /**
     * The {@code "alt-dark-text"} CSS class for bold text.
     */
    ALT_DARK_TEXT("alt-dark-text");

    public static final List<CssClassName> VALIDATION_CSS_CLASSES = Collections.unmodifiableList(Arrays.asList(CssClassName.INFO, CssClassName.WARNING, CssClassName.ERROR));
    public static final CssClassName VALIDATION_MESSAGE_CSS_CLASS = CssClassName.VALIDATIONMSG;

    public static String[] toStringArray(CssClassName... classNames) {
        if (null == classNames || classNames.length == 0) {
            return new String[0];
        }
        String[] result = new String[classNames.length];
        Arrays.setAll(result, (t) -> {
            return classNames[t].value;
        });
        return result;
    }

    public static <T> void forEachStringValue(Supplier<T> targetSupplier, BiConsumer<T, String> consumer, CssClassName... classNames) {
        if (null != classNames && classNames.length > 0) {
            T target = targetSupplier.get();
            for (CssClassName e : classNames) {
                consumer.accept(target, e.value);
            }
        }
    }

    public static <T> void forEachStringValue(Supplier<T> targetSupplier, BiConsumer<T, String> consumer, Collection<CssClassName> classNames) {
        if (null != classNames && !classNames.isEmpty()) {
            T target = targetSupplier.get();
            classNames.forEach((t) -> consumer.accept(target, t.value));
        }
    }

    @SuppressWarnings("overloads")
    public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, BiPredicate<U, CssClassName> predicate,
            BiConsumer<U, String> consumer, CssClassName... classNames) {
        if (null != classNames && classNames.length > 0) {
            U target = targetSupplier.apply(source);
            for (CssClassName e : classNames) {
                if (predicate.test(target, e)) {
                    consumer.accept(target, e.value);
                }
            }
        }
        return source;
    }

    @SuppressWarnings("overloads")
    public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, BiPredicate<U, CssClassName> predicate,
            BiConsumer<U, String> consumer, Collection<CssClassName> classNames) {
        if (null != classNames && !classNames.isEmpty()) {
            U target = targetSupplier.apply(source);
            classNames.stream().filter((e) -> (predicate.test(target, e))).forEachOrdered((e) -> {
                consumer.accept(target, e.value);
            });
        }
        return source;
    }

    public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, Predicate<CssClassName> predicate,
            BiConsumer<U, String> consumer, CssClassName... classNames) {
        int z;
        if (null != classNames && (z = classNames.length) > 0) {
            for (int i = 0; i < z; i++) {
                CssClassName e = classNames[i];
                if (predicate.test(e)) {
                    U target = targetSupplier.apply(source);
                    consumer.accept(target, e.value);
                    while (++i < z) {
                        e = classNames[i];
                        if (predicate.test(e)) {
                            consumer.accept(target, e.value);
                        }
                    }
                    break;
                }
            }
        }
        return source;
    }

    public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, Predicate<CssClassName> predicate,
            BiConsumer<U, String> consumer, Collection<CssClassName> classNames) {
        if (null != classNames && !classNames.isEmpty()) {
            Iterator<CssClassName> iterator = classNames.iterator();
            do {
                CssClassName e = iterator.next();
                if (predicate.test(e)) {
                    U target = targetSupplier.apply(source);
                    consumer.accept(target, e.value);
                    while (iterator.hasNext()) {
                        e = iterator.next();
                        if (predicate.test(e)) {
                            consumer.accept(target, e.value);
                        }
                    }
                    break;
                }
            } while (iterator.hasNext());
        }
        return source;
    }

    @SuppressWarnings("overloads")
    public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, BiConsumer<U, String> consumer, CssClassName... classNames) {
        if (null != classNames && classNames.length > 0) {
            U target = targetSupplier.apply(source);
            for (CssClassName e : classNames) {
                consumer.accept(target, e.value);
            }
        }
        return source;
    }

    public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, BiConsumer<U, String> consumer, Collection<CssClassName> classNames) {
        if (null != classNames && !classNames.isEmpty()) {
            U target = targetSupplier.apply(source);
            classNames.forEach((t) -> consumer.accept(target, t.value));
        }
        return source;
    }

    @SuppressWarnings("overloads")
    public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, BiPredicate<T, CssClassName> predicate, BiFunction<T, String, U> func,
            CssClassName... classNames) {
        int z;
        if (null != classNames && (z = classNames.length) > 0) {
            T target = targetSupplier.get();
            for (int i = 0; i < z; i++) {
                CssClassName e = classNames[i];
                if (predicate.test(target, e)) {
                    Stream.Builder<U> builder = Stream.builder();
                    builder.accept(func.apply(target, e.value));
                    while (++i < z) {
                        e = classNames[i];
                        if (predicate.test(target, e)) {
                            builder.accept(func.apply(target, e.value));
                        }
                    }
                    return builder.build();
                }
            }
        }
        return Stream.empty();
    }

    @SuppressWarnings("overloads")
    public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, BiPredicate<T, CssClassName> predicate, BiFunction<T, String, U> func,
            Collection<CssClassName> classNames) {
        if (null != classNames && !classNames.isEmpty()) {
            T target = targetSupplier.get();
            return classNames.stream().filter((t) -> predicate.test(target, t)).map((t) -> func.apply(target, t.value));
        }
        return Stream.empty();
    }

    public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, Predicate<CssClassName> predicate, BiFunction<T, String, U> func,
            CssClassName... classNames) {
        int z;
        if (null != classNames && (z = classNames.length) > 0) {
            for (int i = 0; i < z; i++) {
                CssClassName e = classNames[i];
                if (predicate.test(e)) {
                    Stream.Builder<U> builder = Stream.builder();
                    T target = targetSupplier.get();
                    builder.accept(func.apply(target, e.value));
                    while (++i < z) {
                        e = classNames[i];
                        if (predicate.test(e)) {
                            builder.accept(func.apply(target, e.value));
                        }
                    }
                    return builder.build();
                }
            }
        }
        return Stream.empty();
    }

    public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, Predicate<CssClassName> predicate, BiFunction<T, String, U> func,
            Collection<CssClassName> classNames) {
        if (null != classNames && !classNames.isEmpty()) {
            Iterator<CssClassName> iterator = classNames.iterator();
            do {
                CssClassName e = iterator.next();
                if (predicate.test(e)) {
                    Stream.Builder<U> builder = Stream.builder();
                    T target = targetSupplier.get();
                    builder.accept(func.apply(target, e.value));
                    while (iterator.hasNext()) {
                        e = iterator.next();
                        if (predicate.test(e)) {
                            builder.accept(func.apply(target, e.value));
                        }
                    }
                    return builder.build();
                }
            } while (iterator.hasNext());
        }
        return Stream.empty();
    }

    @SuppressWarnings("overloads")
    public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, BiFunction<T, String, U> func, CssClassName... classNames) {
        if (null != classNames && classNames.length > 0) {
            T target = targetSupplier.get();
            return Arrays.stream(classNames).map((e) -> func.apply(target, e.value));
        }
        return Stream.empty();
    }

    public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, BiFunction<T, String, U> func, Collection<CssClassName> classNames) {
        if (null != classNames && !classNames.isEmpty()) {
            T target = targetSupplier.get();
            return classNames.stream().map((e) -> func.apply(target, e.value));
        }
        return Stream.empty();
    }

    public static <T> Stream<T> mapStringValues(Function<String, T> func, Collection<CssClassName> classNames) {
        if (null != classNames && !classNames.isEmpty()) {
            return classNames.stream().map((e) -> func.apply(e.value));
        }
        return Stream.empty();
    }
    private final String value;

    private CssClassName(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}
