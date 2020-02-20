package scheduler.util;

import java.util.TreeMap;

/**
 *
 * @author erwinel
 * @param <K>
 * @param <V>
 */
public class ItemLinkHashMap<K, V extends ItemLinkHashMap.Item<K, V, ? extends ItemLinkHashMap<K, V>>> extends TreeMap<K, V> {

    public static <K, V extends Item<K, V, P>, P extends ItemLinkHashMap<K, V>> K getItemKey(Item<K, V, P> item) {
        return (item == null) ? null : item.key;
    }

    public static <K, V extends Item<K, V, P>, P extends ItemLinkHashMap<K, V>> P getItemParent(Item<K, V, P> item) {
        return (item == null) ? null : item.parent;
    }

    public static abstract class Item<K, V extends Item<K, V, P>, P extends ItemLinkHashMap<K, V>> {

        private K key;
        private P parent;
    }
}
