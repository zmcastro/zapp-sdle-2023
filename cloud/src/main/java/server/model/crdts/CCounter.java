package server.model.crdts;

import org.json.JSONArray;
import org.json.JSONObject;
import server.model.Product;
import server.model.utils.Pair;

import java.util.*;

public class CCounter {

    private String id; // unique user identifier
    private Map<Pair<String, Integer>, Integer> map = new HashMap<>();
    private DotContext cc = new DotContext();

    public CCounter(String id) {
        this.id = id;
    }

    public CCounter() {

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

    public void fromJSON(JSONObject json) {

        for (Object product : json.getJSONArray("map")) {
            JSONObject jProduct = (JSONObject) product;
            Pair<String, Integer> dot = new Pair<>(jProduct.getString("name"), jProduct.getInt("context"));

            map.put(dot, (Integer) jProduct.get("value"));
        }

        for (Object dot : json.getJSONObject("context").getJSONArray("cc")) {
            JSONArray jDot = (JSONArray) dot;
            cc.getCC().put(jDot.getString(0), jDot.getInt(1)); // CHECKME
        }

        if (!json.getJSONObject("context").getJSONArray("dc").isEmpty()) {
            for (Object dot : json.getJSONObject("context").getJSONArray("dc")) {
                JSONArray jDot = (JSONArray) dot;
                Pair<String, Integer> pairDot = new Pair<>(jDot.getString(0), jDot.getInt(1));
                cc.insertDot(pairDot, false);
                // TESTME
            }
        }
    }

    public JSONObject toJSON() {
        JSONObject res = new JSONObject();
        ArrayList<JSONObject> map = new ArrayList<JSONObject>();

        for (Map.Entry<Pair<String, Integer>, Integer> entry : this.map.entrySet()) {
            JSONObject productJSON = new JSONObject();
            productJSON.put("name", entry.getKey().getKey());
            productJSON.put("context", entry.getKey().getValue());
            productJSON.put("value", entry.getValue());
            map.add(productJSON);
        }

        res.put("map", new JSONArray(map));
        res.put("context", this.cc.toJSON());

        return res;
    }
}
