package server.model.crdts;

import org.json.JSONArray;
import org.json.JSONObject;
import server.model.Product;
import server.model.utils.Pair;

import java.io.IOException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class AWORMap {
    private String id; // This is most likely deprecated
    /**
     * Map that stores [element_id, dot_value] => element
     */
    private HashMap<Pair<String, Integer>, Product> map;
    /**
     * DotContext that stores element_id => dot_value
     */
    private DotContext cc;

    /**
     * Constructor with an identifier
     *
     * @param id Identifier
     */
    public AWORMap() {
        map = new HashMap<>();
    }

    /**
     * Get the map
     *
     * @return Map
     */
    public Map<Pair<String, Integer>, Product> getMap() {
        return map;
    }

    /**
     * Get the causal context
     *
     * @return Causal context
     */
    public DotContext getCC() {
        return cc;
    }

    /**
     * Get the entries of the map
     *
     * @return Set of map entries
     */
    public Set<Map.Entry<Pair<String, Integer>, Product>> elements() {
        return map.entrySet();
    }

    /**
     * Get the values of the map that are in the causal context
     *
     * @return Iterator of values
     */
    public Iterator<Product> values() {
        Map<Pair<String, Integer>, Product> filteredMap = new HashMap<>();
        Map<String, Integer> context = cc.getCC();

        for (Map.Entry<Pair<String, Integer>, Product> entry : map.entrySet()) {
            Pair<String, Integer> key = entry.getKey();
            Product value = entry.getValue();
            // if causal context contains element_id and its associated pr
            if (cc.getCC().containsKey(key.getKey()) && cc.getCC().get(key.getKey()) == key.getValue()) {
                filteredMap.put(key, value);
            }
        }
        return filteredMap.values().iterator();
    }

    /**
     * Add a new element to the map, keeping the causal context
     *
     * @param element_id Identifier of the element
     * @param element    Element to be added
     */
    public void add(String element_id, Product element) {
        Pair<String, Integer> dot = cc.makeDot(element_id);
        map.put(dot, element);
    }

    /**
     * Remove an element from the map, keeping the causal context
     *
     * @param element_id Identifier of the element
     */
    public void rm(String element_id) {
        Iterator<Map.Entry<Pair<String, Integer>, Product>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Pair<String, Integer>, Product> entry = iterator.next();
            Pair<String, Integer> key = entry.getKey();
            if (key.getKey().equals(element_id)) {
                iterator.remove();
            }
        }
    }

    /**
     * Get an element from the map
     *
     * @param element_id Identifier of the element
     * @return Element
     */
    public Product get(String element_id) {
        for (Map.Entry<Pair<String, Integer>, Product> entry : map.entrySet()) {
            Pair<String, Integer> key = entry.getKey();
            if (key.getKey().equals(element_id)) {
                return entry.getValue();
            }
        }
        return null;
    }

    /**
     * Join two AWORMaps
     *
     * @param awormap AWORMap to join
     */
    public void join(AWORMap awormap) {
        Iterator<Map.Entry<Pair<String, Integer>, Product>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Pair<String, Integer>, Product> entry = iterator.next();
            Pair<String, Integer> key = entry.getKey();

            if (!awormap.getMap().containsKey(key) || !awormap.getCC().dotIn(key)) {
                iterator.remove();
            }
        }

        for (Map.Entry<Pair<String, Integer>, Product> entry : awormap.getMap().entrySet()) {
            Pair<String, Integer> key = entry.getKey();
            Product value = entry.getValue();
            if (!cc.dotIn(key)) {
                map.put(key, value);
            }
        }

        cc.join(awormap.getCC());
    }

    public void fromJSON(String id, JSONObject products) {
        this.id = id;

        for (Object product : products.getJSONArray("map")) {
            JSONObject jProduct = (JSONObject) product;
            Pair<String, Integer> dot = new Pair<>(jProduct.getString("name"), jProduct.getInt("context"));
            Product newProduct = new Product(jProduct.getString("name"), id);
            newProduct.fromJSON(jProduct);

            map.put(dot, newProduct);
        }

        for (Object dot : products.getJSONObject("context").getJSONArray("cc")) {
            JSONObject jDot = (JSONObject) dot;

             // cc.getCC().put(dot[0], dot[1]); CHECKME
        }

        if (!products.getJSONObject("context").getJSONArray("dc").isEmpty()) {
            for (Object dot : products.getJSONObject("context").getJSONArray("dc")) {
                JSONObject jDot = (JSONObject) dot;
                // cc.insertDot(dot, false);
            }
        }
    }

    /*
    public String toJSON() {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> res = new HashMap<>();
        res.put("map", new ArrayList<>());
        res.put("context", new HashMap<>());

        for (Map.Entry<String[], Product> entry : map.entrySet()) {
            Map<String, Object> product = entry.getValue().toJSON();
            product.put("name", entry.getKey()[0]);
            product.put("context", entry.getKey()[1]);
            ((List<Map<String, Object>>) res.get("map")).add(product);
        }

        res.put("context", cc.toJSON());

        try {
            return mapper.writeValueAsString(res);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return "{}";
        }
    }
    */
}

