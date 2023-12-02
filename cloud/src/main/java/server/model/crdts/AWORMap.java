package server.model.crdts;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;


public class AWORMap {
    private String id; // This is most likely deprecated
    /**
     * Map that stores [element_id, dot_value] => element
     */
    private HashMap<ArrayList<Object>, Product> map;
    /**
     * DotContext that stores element_id => dot_value
     */
    private DotContext cc;

    /**
     * Constructor with an identifier
     *
     * @param id Identifier
     */
    public AWORMap(String id) {
        this.id = id;
        map = new HashMap<ArrayList<Object>, Product>();
    }

    /**
     * Get the map
     *
     * @return Map
     */
    public Map<ArrayList<Object>, Product> getMap() {
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
     * Get the elements of the map
     *
     * @return Iterator of elements
     */
    public Iterator<ArrayList<Object>> elements() {
        return map.keySet().iterator();
    }

    /**
     * Get the values of the map that are in the causal context
     *
     * @return Iterator of values
     */
    public Iterator<Product> values() {
        Map<int[], Product> filteredMap = new HashMap<>();
        for (Map.Entry<int[], Product> entry : map.entrySet()) {
            int[] key = entry.getKey();
            Product value = entry.getValue();
            if (cc.getCC().containsKey(key[0]) && cc.getCC().get(key[0]) == key[1]) {
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
    public void add(int element_id, Product element) {
        int[] dot = cc.makeDot(element_id);
        map.put(new Tuple(dot, element), element);
    }

    /**
     * Remove an element from the map, keeping the causal context
     *
     * @param element_id Identifier of the element
     */
    public void rm(int element_id) {
        Iterator<Map.Entry<int[], Product>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Tuple, Product> entry = iterator.next();
            String key = entry.getKey();
            if (key[0].equals(element_id)) {
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
    public Product get(int element_id) {
        for (Map.Entry<int[], Product> entry : map.entrySet()) {
            int[] key = entry.getKey();
            if (key[0] == element_id) {
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
        Iterator<Map.Entry<int[], Product>> iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<int[], Product> entry = iterator.next();
            int[] key = entry.getKey();
            Product value = entry.getValue();
            if (!awormap.getMap().containsKey(key) || !awormap.getCC().dotIn(key)) {
                iterator.remove();
            }
        }

        for (Map.Entry<int[], Product> entry : awormap.getMap().entrySet()) {
            int[] key = entry.getKey();
            Product value = entry.getValue();
            if (!cc.dotIn(key, value.getCounter())) {
                map.put(key, value);
            }
        }

        cc.join(awormap.getCC());
    }
}

