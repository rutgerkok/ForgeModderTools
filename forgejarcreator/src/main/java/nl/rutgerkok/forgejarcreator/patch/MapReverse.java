package nl.rutgerkok.forgejarcreator.patch;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapReverse {
    /**
     * Reverses all keys and values in a map. So <code>{"a":"x","b":"y"}</code>
     * becomes <code>{"x":"a","y":"b"}</code>. The orignial map is not modified.
     * 
     * @param toReverse
     *            The map to reverse.
     * @return A copy of the map with all keys and values reversed.
     */
    public static <T> Map<T, T> execute(Map<T, T> toReverse) {
        Map<T, T> reversed = new HashMap<T, T>();
        for (Entry<T, T> entry : toReverse.entrySet()) {
            reversed.put(entry.getValue(), entry.getKey());
        }
        return reversed;
    }
}
