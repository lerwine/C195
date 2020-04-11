package scheduler.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 *
 * @author Leonard T. Erwine (Student ID 356334) &lt;lerwine@wgu.edu&gt;
 */
public class MapHelper {

    public static <T, K, V> void fillMap(Map<K, V> target, T[] source, BiConsumer<T, Map<K, V>> builder) {
        for (T t : source) {
            builder.accept(t, target);
        }
    }

    public static <T, K, V> void fillMap(Map<K, V> target, Iterator<T> source, BiConsumer<T, Map<K, V>> builder) {
        while (source.hasNext()) {
            builder.accept(source.next(), target);
        }
    }

    public static <T, K, V> void fillMap(Map<K, V> target, BiConsumer<T, Map<K, V>> builder, T... source) {
        fillMap(target, source, builder);
    }

    public static <T, K, V> void fillMap(Map<K, V> target, Iterable<T> source, BiConsumer<T, Map<K, V>> builder) {
        fillMap(target, source.iterator(), builder);
    }

    public static <T, K, V> void fillMap(Map<K, V> target, Stream<T> source, BiConsumer<T, Map<K, V>> builder) {
        fillMap(target, source.iterator(), builder);
    }

    public static <T, K, V> void fillMap(Map<K, V> target, T[] source, Function<T, K> getKey, Function<T, V> getValue) {
        fillMap(target, source, (T t, Map<K, V> m) -> m.put(getKey.apply(t), getValue.apply(t)));
    }

    public static <T, K, V> void fillMap(Map<K, V> target, Function<T, K> getKey, Function<T, V> getValue, T... source) {
        fillMap(target, source, getKey, getValue);
    }

    public static <T, K, V> void fillMap(Map<K, V> target, Iterator<T> source, Function<T, K> getKey, Function<T, V> getValue) {
        fillMap(target, source, (T t, Map<K, V> m) -> m.put(getKey.apply(t), getValue.apply(t)));
    }

    public static <T, K, V> void fillMap(Map<K, V> target, Iterable<T> source, Function<T, K> getKey, Function<T, V> getValue) {
        fillMap(target, source.iterator(), getKey, getValue);
    }

    public static <T, K, V> void fillMap(Map<K, V> target, Stream<T> source, Function<T, K> getKey, Function<T, V> getValue) {
        fillMap(target, source.iterator(), getKey, getValue);
    }

    public static <K, V> void fillMap(Map<K, V> target, V[] source, Function<V, K> getKey) {
        fillMap(target, source, (V v, Map<K, V> m) -> m.put(getKey.apply(v), v));
    }

    public static <K, V> void fillMap(Map<K, V> target, Function<V, K> getKey, V... source) {
        fillMap(target, source, getKey);
    }

    public static <K, V> void fillMap(Map<K, V> target, Iterator<V> source, Function<V, K> getKey) {
        fillMap(target, source, (V v, Map<K, V> m) -> m.put(getKey.apply(v), v));
    }

    public static <K, V> void fillMap(Map<K, V> target, Iterable<V> source, Function<V, K> getKey) {
        fillMap(target, source.iterator(), getKey);
    }

    public static <K, V> void fillMap(Map<K, V> target, Stream<V> source, Function<V, K> getKey) {
        fillMap(target, source.iterator(), getKey);
    }

    public static <T, K, V> HashMap<K, V> toMap(T[] source, BiConsumer<T, Map<K, V>> builder) {
        HashMap<K, V> result = new HashMap<>();
        fillMap(result, source, builder);
        return result;
    }

    public static <T, K, V> HashMap<K, V> toMap(BiConsumer<T, Map<K, V>> builder, T... source) {
        return toMap(source, builder);
    }

    public static <T, K, V> HashMap<K, V> toMap(Iterator<T> source, BiConsumer<T, Map<K, V>> builder) {
        HashMap<K, V> result = new HashMap<>();
        fillMap(result, source, builder);
        return result;
    }

    public static <T, K, V> HashMap<K, V> toMap(Iterable<T> source, BiConsumer<T, Map<K, V>> builder) {
        return toMap(source.iterator(), builder);
    }

    public static <T, K, V> HashMap<K, V> toMap(Stream<T> source, BiConsumer<T, Map<K, V>> builder) {
        return toMap(source.iterator(), builder);
    }

    public static <T, K, V> HashMap<K, V> toMap(T[] source, Function<T, K> getKey, Function<T, V> getValue) {
        return toMap(source, (T t, Map<K, V> m) -> m.put(getKey.apply(t), getValue.apply(t)));
    }

    public static <T, K, V> HashMap<K, V> toMap(Function<T, K> getKey, Function<T, V> getValue, T... source) {
        return toMap(source, getKey, getValue);
    }

    public static <T, K, V> HashMap<K, V> toMap(Iterator<T> source, Function<T, K> getKey, Function<T, V> getValue) {
        return toMap(source, (T t, Map<K, V> m) -> m.put(getKey.apply(t), getValue.apply(t)));
    }

    public static <T, K, V> HashMap<K, V> toMap(Iterable<T> source, Function<T, K> getKey, Function<T, V> getValue) {
        return toMap(source.iterator(), getKey, getValue);
    }

    public static <T, K, V> HashMap<K, V> toMap(Stream<T> source, Function<T, K> getKey, Function<T, V> getValue) {
        return toMap(source.iterator(), getKey, getValue);
    }

    public static <K, V> HashMap<K, V> toMap(V[] source, Function<V, K> getKey) {
        return toMap(source, (V v, Map<K, V> m) -> m.put(getKey.apply(v), v));
    }

    public static <K, V> HashMap<K, V> toMap(Function<V, K> getKey, V... source) {
        return toMap(source, getKey);
    }

    public static <K, V> HashMap<K, V> toMap(Iterator<V> source, Function<V, K> getKey) {
        return toMap(source, (V v, Map<K, V> m) -> m.put(getKey.apply(v), v));
    }

    public static <K, V> HashMap<K, V> toMap(Iterable<V> source, Function<V, K> getKey) {
        return toMap(source.iterator(), getKey);
    }

    public static <K, V> HashMap<K, V> toMap(Stream<V> source, Function<V, K> getKey) {
        return toMap(source.iterator(), getKey);
    }

    public static <T, K, V> HashMap<K, ArrayList<V>> groupMap(T[] source, Function<T, K> getKey, Function<T, V> getValue) {
        return toMap(source, (T t, Map<K, ArrayList<V>> m) -> {
            K key = getKey.apply(t);
            if (m.containsKey(key)) {
                m.get(key).add(getValue.apply(t));
            } else {
                ArrayList<V> list = new ArrayList<>();
                list.add(getValue.apply(t));
                m.put(key, list);
            }
        });
    }

    public static <T, K, V> HashMap<K, ArrayList<V>> groupMap(Function<T, K> getKey, Function<T, V> getValue, T... source) {
        return groupMap(source, getKey, getValue);
    }

    public static <T, K, V> HashMap<K, ArrayList<V>> groupMap(Iterator<T> source, Function<T, K> getKey, Function<T, V> getValue) {
        return toMap(source, (T t, Map<K, ArrayList<V>> m) -> {
            K key = getKey.apply(t);
            if (m.containsKey(key)) {
                m.get(key).add(getValue.apply(t));
            } else {
                ArrayList<V> list = new ArrayList<>();
                list.add(getValue.apply(t));
                m.put(key, list);
            }
        });
    }

    public static <T, K, V> HashMap<K, ArrayList<V>> groupMap(Iterable<T> source, Function<T, K> getKey, Function<T, V> getValue) {
        return groupMap(source.iterator(), getKey, getValue);
    }

    public static <T, K, V> HashMap<K, ArrayList<V>> groupMap(Stream<T> source, Function<T, K> getKey, Function<T, V> getValue) {
        return groupMap(source.iterator(), getKey, getValue);
    }

    public static <K, V> HashMap<K, ArrayList<V>> groupMap(V[] source, Function<V, K> getKey) {
        return toMap(source, (V t, Map<K, ArrayList<V>> m) -> {
            K key = getKey.apply(t);
            if (m.containsKey(key)) {
                m.get(key).add(t);
            } else {
                ArrayList<V> list = new ArrayList<>();
                list.add(t);
                m.put(key, list);
            }
        });
    }

    public static <K, V> HashMap<K, ArrayList<V>> groupMap(Function<V, K> getKey, V... source) {
        return groupMap(source, getKey);
    }

    public static <K, V> HashMap<K, ArrayList<V>> groupMap(Iterator<V> source, Function<V, K> getKey) {
        return toMap(source, (V t, Map<K, ArrayList<V>> m) -> {
            K key = getKey.apply(t);
            if (m.containsKey(key)) {
                m.get(key).add(t);
            } else {
                ArrayList<V> list = new ArrayList<>();
                list.add(t);
                m.put(key, list);
            }
        });
    }

    public static <K, V> HashMap<K, ArrayList<V>> groupMap(Iterable<V> source, Function<V, K> getKey) {
        return groupMap(source.iterator(), getKey);
    }

    public static <K, V> HashMap<K, ArrayList<V>> groupMap(Stream<V> source, Function<V, K> getKey) {
        return groupMap(source.iterator(), getKey);
    }

    public static <K, V, T> HashMap<V, T> flipMap(Map<K, V> source, BiFunction<K, V, T> createCollector, BiConsumer<K, T> appendKey) {
        return toMap(source.keySet(), (originalKey, collector) -> {
            V resultKey = source.get(originalKey);
            if (collector.containsKey(resultKey)) {
                appendKey.accept(originalKey, collector.get(resultKey));
            } else {
                collector.put(resultKey, createCollector.apply(originalKey, resultKey));
            }
        });
    }

    public static <K, V, T> HashMap<V, T> flipMap(Map<K, V> source, Function<K, T> createCollector, BiConsumer<K, T> appendKey) {
        return toMap(source.keySet(), (originalKey, collector) -> {
            V resultKey = source.get(originalKey);
            if (collector.containsKey(resultKey)) {
                appendKey.accept(originalKey, collector.get(resultKey));
            } else {
                collector.put(resultKey, createCollector.apply(originalKey));
            }
        });
    }

    public static <K, V> HashMap<V, ArrayList<K>> flipMap(Map<K, V> source) {
        return flipMap(source, (K k) -> {
            ArrayList<K> result = new ArrayList<>();
            result.add(k);
            return result;
        }, (k, c) -> c.add(k));
    }

    public static <K, V, T, U> HashMap<T, U> remap(Map<K, V> source, ReMapper<K, V, HashMap<T, U>> mapValue) {
        HashMap<T, U> result = new HashMap<>();
        source.keySet().forEach((k) -> mapValue.accept(k, source.get(k), result));
        return result;
    }

    public static <K, V, T> HashMap<K, T> remap(Map<K, V> source, BiFunction<K, V, T> mapValue) {
        HashMap<K, T> result = new HashMap<>();
        source.keySet().forEach((k) -> result.put(k, mapValue.apply(k, source.get(k))));
        return result;
    }

    public static <K, V, T> HashMap<K, T> remap(Map<K, V> source, Function<V, T> mapValue) {
        HashMap<K, T> result = new HashMap<>();
        source.keySet().forEach((k) -> result.put(k, mapValue.apply(source.get(k))));
        return result;
    }

    @FunctionalInterface
    public interface ReMapper<K, V, T extends Map<?, ?>> {

        void accept(K key, V value, T target);
    }
}
