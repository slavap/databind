package gwt.interop.utils.server.collections;

import java.util.HashMap;
import java.util.Map;

import gwt.interop.utils.shared.collections.Array;
import gwt.interop.utils.shared.collections.StringMap;
import jsinterop.base.JsPropertyMap;

public class JavaStringMap<T> implements StringMap<T> {
    private Map<String, T> internalMap;

    public JavaStringMap(Map<String, T> from) { internalMap = from; }

    public JavaStringMap() {
        internalMap = new HashMap<>();
    }

	static JsPropertyMap<Object> of() {
		return new JavaStringMap<>();
	}

    /**
     * Return the value with the supplied string key
     *
     * @param key The key
     * @return The value or null if no value is defined for that supplied key
     */
    @Override
    public T get(String key) {

        return internalMap.get(key);
    }

    /**
     * Set the value for the supplied key
     *
     * @param key The key.
     * @param value The value to set
     */
    @Override
    public void set(String key, T value) {

        internalMap.put(key, value);
    }

    /**
     * Remove the value with the supplied key
     *
     * @param key The key of the value to remove
     */
    @Override
    public void delete(String key) {

        internalMap.remove(key);
    }

    /**
     * Returns is the supplied key is set in the map
     *
     * @param key The key to test
     * @return <code>true</code> if the key is set
     */
    @Override
    public boolean has(String key) {

        return internalMap.containsKey(key);
    }

    /**
     * Returns the keys defined in the map
     *
     * @return an Array of string keys
     */
    @Override
    public Array<String> keys() {

        Array<String> out = new JavaArray<>();

        for(String key : internalMap.keySet()) {
            out.push(key);
        }

        return out;
    }

    /**
     * Returns the values defined in the map
     *
     * @return an Array of values
     */
    @Override
    public Array<T> values() {
        Array<T> out = new JavaArray<>();

        for(T value : internalMap.values()) {
            out.push(value);
        }

        return out;
    }

    /**
     * Removes all entries from this map
     */
    @Override
    public void clear() {

        internalMap.clear();
    }

    @Override
    public int size() {
        return internalMap.size();
    }

    /**
     * Invokes the given callback for each key / value pair in the map
     *
     * @param forEachFn The callback to invoke
     */
    @Override
    public void forEach(StringMap.ForEachFn<T> forEachFn) {
        for(String key : internalMap.keySet()) {
            forEachFn.forEach(internalMap.get(key), key, this);
        }
    }

    /**
     * Copies all entries from the provided ObservableMap into this map
     *
     * @param toMerge The StringMap to merge
     */
    @Override
    public void merge(StringMap<T> toMerge) {
        toMerge.forEach((value, key, maps) -> set(key, value));
    }
}
