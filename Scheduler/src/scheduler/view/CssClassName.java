package scheduler.view;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import scheduler.util.MapHelper;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public enum CssClassName {
        SYMBOL_BUTTON("symbol-button"),
        COLLAPSED("collapsed", ExclusiveCssClassGroup.VALIDATION),
        BORDERED("bordered"),
        ERROR("error", ExclusiveCssClassGroup.VALIDATION),
        WARNING("warningMessage", ExclusiveCssClassGroup.VALIDATION),
        INFO("info", ExclusiveCssClassGroup.VALIDATION),
        VALIDATIONMSG("formControlValidationMessage", ExclusiveCssClassGroup.VALIDATION),
        LEFTCONTROLLABEL("leftControlLabel"),
        LEFTLABELEDCONTROL("leftLabeledControl");
        
        private static final Map<ExclusiveCssClassGroup, List<CssClassName>> byGroup;
        private final String value;
        private final List<ExclusiveCssClassGroup> exclusiveGroups;
        
        public List<ExclusiveCssClassGroup> getExclusiveGroups() {
            return exclusiveGroups;
        }
        
        @Override
        public String toString() {
            return value;
        }
        
        static {
            byGroup = Collections.unmodifiableMap(MapHelper.remap(MapHelper.groupMapFlat(CssClassName.values(), (t) -> (t.exclusiveGroups.isEmpty()) ?
                    Collections.singleton(ExclusiveCssClassGroup.NONE).iterator() : t.exclusiveGroups.iterator()), (t) -> Collections.unmodifiableList(t)));
            
        }
        
        public static List<CssClassName> ofGroup(ExclusiveCssClassGroup group) {
            return byGroup.get(group);
        }
        
        private CssClassName(String value, ExclusiveCssClassGroup ...exclusiveGroups) {
            this.value = value;
            this.exclusiveGroups = (null == exclusiveGroups || exclusiveGroups.length == 0) ? Collections.emptyList() : Collections.unmodifiableList(Arrays.asList(exclusiveGroups));
        }
        
        public static String[] toStringArray(CssClassName ...classNames) {
            if (null == classNames || classNames.length == 0)
                return new String[0];
            String[] result = new String[classNames.length];
            Arrays.setAll(result, (t) -> {
                return classNames[t].value;
            });
            return result;
        }
        
        public static <T> void forEachStringValue(Supplier<T> targetSupplier, BiConsumer<T, String> consumer, CssClassName ...classNames) {
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
        
        public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, BiPredicate<U, CssClassName> predicate, BiConsumer<U, String> consumer, CssClassName ...classNames) {
            if (null != classNames && classNames.length > 0) {
                U target = targetSupplier.apply(source);
                for (CssClassName e : classNames) {
                    if (predicate.test(target, e))
                        consumer.accept(target, e.value);
                }
            }
            return source;
        }
        
        public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, BiPredicate<U, CssClassName> predicate, BiConsumer<U, String> consumer, Collection<CssClassName> classNames) {
            if (null != classNames && !classNames.isEmpty()) {
                U target = targetSupplier.apply(source);
                classNames.stream().filter((e) -> (predicate.test(target, e))).forEachOrdered((e) -> {
                    consumer.accept(target, e.value);
                });
            }
            return source;
        }
        
        public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, Predicate<CssClassName> predicate, BiConsumer<U, String> consumer, CssClassName ...classNames) {
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
        
        public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, Predicate<CssClassName> predicate, BiConsumer<U, String> consumer, Collection<CssClassName> classNames) {
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
        
        public static <T, U> T applyEachStringValue(T source, Function<T, U> targetSupplier, BiConsumer<U, String> consumer, CssClassName ...classNames) {
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
        
        public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, BiPredicate<T, CssClassName> predicate, BiFunction<T, String, U> func, CssClassName ...classNames) {
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
        
        public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, BiPredicate<T, CssClassName> predicate, BiFunction<T, String, U> func, Collection<CssClassName> classNames) {
            if (null != classNames && !classNames.isEmpty()) {
                T target = targetSupplier.get();
                return classNames.stream().filter((t) -> predicate.test(target, t)).map((t) -> func.apply(target, t.value));
            }
            return Stream.empty();
        }
        
        public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, Predicate<CssClassName> predicate, BiFunction<T, String, U> func, CssClassName ...classNames) {
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
        
        public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, Predicate<CssClassName> predicate, BiFunction<T, String, U> func, Collection<CssClassName> classNames) {
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
        
        public static <T, U> Stream<U> mapStringValues(Supplier<T> targetSupplier, BiFunction<T, String, U> func, CssClassName ...classNames) {
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
        
}
