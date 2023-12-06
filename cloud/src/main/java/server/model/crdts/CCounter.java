package server.model.crdts;

import server.model.Product;
import server.model.utils.Pair;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CCounter {

    private String id; // unique user identifier
    private Map<Pair<String, Integer>, Integer> map = new HashMap<>();
    private DotContext cc = new DotContext();

    public CCounter(String id) {
        this.id = id;
    }

    public Map<Pair<String, Integer>, Integer> getMap() {
        return map;
    }

    public DotContext getCC() {
        return cc;
    }

    public void setID(String id) {
        this.id = id;
    }

    public Set<Pair<String, Integer>> elements() {
        return map.keySet();
    }

    public void inc(int value) {
        int base = 0;
        for (Map.Entry<Pair<String, Integer>, Integer> entry : map.entrySet()) {
            Pair<String, Integer> key = entry.getKey(); // [element_id, dot_value]
            int _value = entry.getValue(); // element
            if (key.getKey().equals(id)) { // Should only ever be one
                base = Math.max(base, _value);
                map.remove(key);
            }
        }
        Pair<String, Integer> dot = cc.makeDot(id);
        map.put(dot, base + value);
    }

    public void dec(int value) {
        if (this.read() - value < 0) return;

        int base = 0;
        for (Map.Entry<Pair<String, Integer>, Integer> entry : map.entrySet()) {
            Pair<String, Integer> key = entry.getKey(); // [element_id, dot_value]
            int _value = entry.getValue(); // element
            if (key.getKey().equals(id)) { // Should only ever be one
                base = Math.max(base, _value);
                map.remove(key);
            }
        }
        Pair<String, Integer> dot = cc.makeDot(id);
        map.put(dot, base - value);
    }

    public int read() {
        int res = 0;
        for (int value : map.values()) {
            res += value;
        }
        return res;
    }

    public boolean hasKey(Pair<String, Integer> key) {
        for (Map.Entry<Pair<String, Integer>, Integer> entry : map.entrySet()) {
            Pair<String, Integer> _key = entry.getKey();
            System.out.println(key + " == " + _key + "?");
            if (_key.equals(key)) {
                System.out.println("Yea!!");
                return true;
            }
            System.out.println("Nope!!");
        }
        return false;
    }

    public void join(CCounter ccounter) {
        for (Map.Entry<Pair<String, Integer>, Integer> entry : map.entrySet()) {
            Pair<String, Integer> key = entry.getKey();
            int value = entry.getValue();
            if (!ccounter.hasKey(key) && ccounter.getCC().dotIn(key)) {
                map.remove(key, value);
            }
        }

        for (Map.Entry<Pair<String, Integer>, Integer> entry : ccounter.getMap().entrySet()) {
            Pair<String, Integer> key = entry.getKey();
            Integer value = entry.getValue();
            if (!cc.dotIn(key)) {
                map.put(key, value);
            }
        }

        cc.join(ccounter.getCC());
    }

    public void fromJSON() {
    }

    public void toJSON() {
    }
}
