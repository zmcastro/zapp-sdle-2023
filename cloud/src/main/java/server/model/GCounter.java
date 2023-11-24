package server.model;

import java.util.HashMap;
import java.util.Map;

public class GCounter {
    private Map<String, Integer> map = new HashMap<>();
    private String id;

    // Constructor with id
    public GCounter(String id) {
        this.id = id;
        this.map.put(id, 0);
    }

    // Constructor for creating a copy of the counter
    public GCounter(GCounter counter) {
        this.map = new HashMap<>(counter.map);
        this.id = counter.id;
    }

    // Set key
    public void setKey(String id) {
        this.id = id;
        this.map.put(id, 0);
    }

    // Get map
    public Map<String, Integer> get() {
        return map;
    }

    // Read local counter value
    public int local() {
        return map.get(id);
    }

    // Read counter value
    public int read() {
        int res = 0;
        for (int value : map.values()) {
            res += value;
        }
        return res;
    }

    // Increment value
    public void inc(int toSum) {
        map.put(id, map.get(id) + toSum);
    }

    // Merge counters
    public void join(GCounter gCounter) {
        for (Map.Entry<String, Integer> entry : gCounter.map.entrySet()) {
            String entryId = entry.getKey();
            int entryValue = entry.getValue();
            map.put(entryId, Math.max(map.getOrDefault(entryId, 0), entryValue));
        }
    }
}