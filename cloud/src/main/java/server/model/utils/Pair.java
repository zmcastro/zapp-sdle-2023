package server.model.utils;

import java.util.Objects;

/**
 * Basic Pair implementation to be used in tandem with the CRDTs,
 */
public class Pair<K, V> {

    public final K key;
    public final V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public static <K, V> Pair<K, V> of(K key, V value) {
        return new Pair<>(key, value);
    }


    public K getKey() { return key; }
    public V getValue() { return value; }

    public boolean equals(Object o) {
        return o instanceof Pair && Objects.equals(key, ((Pair<?,?>)o).key) && Objects.equals(value, ((Pair<?,?>)o).value);
    }

    public int hashCode() {
        return 31 * Objects.hashCode(key) + Objects.hashCode(value);
    }

    public String toString() {
        return key + "=" + value;
    }
}