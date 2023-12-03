package server.model.crdts;


import server.model.utils.Pair;

import java.util.*;

public class DotContext {
    private Map<String, Integer> cc = new HashMap<>();
    private Set<Pair<String, Integer>> dc = new HashSet<>();

    /**
     * Get causal context (compact)
     *
     * @return Causal context
     */
    public Map<String, Integer> getCC() {
        return cc;
    }
    public Set<Pair<String, Integer>> getDC() {
        return dc;
    }

    /**
     * Check if key-value pair is in causal context (compact or dot cloud)
     *
     * @param dot Tuple [key, value]
     * @return true if dot is in causal context
     */
    public boolean dotIn(Pair<String, Integer> dot) {
        String key = dot.getKey();
        int value = dot.getValue();
        boolean res = false;

        if (cc.containsKey(key)) {
            res = value <= cc.get(key);
        }

        if (dc.contains(dot)) {
            res = true;
        }

        return res;
    }

    /**
     * Attempts to compact causal context - transform DC to CC
     */
    public void compact() {
        boolean canCompact;

        // We do it in a loop because we may be able to compact more than once
        do {
            canCompact = false;
            for (Pair<String, Integer> dot : new HashSet<>(dc)) {
                String key = dot.getKey();
                int value = dot.getValue();

                Integer ccValue = cc.get(key);

                if (ccValue == null && value == 1) {
                    // key not in cc and value is 1
                    cc.put(key, value);
                    dc.remove(dot);
                    canCompact = true;
                } else if (ccValue != null) {
                    // key in cc
                    if (value == ccValue + 1) {
                        // value is continuous
                        cc.put(key, value);
                        dc.remove(dot);
                        canCompact = true;
                    } else if (value <= ccValue) {
                        // already have a more "recent" value
                        dc.remove(dot);
                    }
                }
            }
        } while (canCompact);
    }

    /**
     * Adds a dot to the causal context or updates its value if it's already there
     *
     * @param id ID of the dot
     * @return Tuple [key, value]
     */
    public Pair<String, Integer> makeDot(String id) {
        int val = 1;
        if (cc.containsKey(id)) val += cc.get(id);

        cc.put(id, val);
        return new Pair<>(id, val);
    }

    /**
     * Adds a dot to the dot cloud
     *
     * @param dot Tuple [key, value]
     * @param compact Flag to indicate whether to compact
     */
    public void insertDot(Pair<String, Integer> dot, boolean compact) {
        dc.add(dot);
        if (compact) compact();
    }

    /**
     * Join two DotContexts
     *
     * @param dotContext DotContext to join with
     */
    public void join(DotContext dotContext) {
        // If joining with itself, do nothing
        if (dotContext == this) return;

        // Add compacted dots of otherCC to this
        for (Map.Entry<String, Integer> entry : dotContext.getCC().entrySet()) {
            String key = entry.getKey();
            int value = entry.getValue();
            cc.merge(key, value, Integer::max); // CHECK: if this works as intended
        }

        // Add dots of otherDC to this
        for (Pair<String, Integer> dot : dotContext.dc) {
            insertDot(dot, false);
        }

        compact();
    }

    /*
    public void fromJSON(DotContext context) {
        this.cc = context.getCC();
        this.dc = context.getDC();
    }

    public Map<String, Object> toJSON() {
        Map<String, Object> res = new HashMap<>();
        List<Map.Entry<String, Integer>> ccList = new ArrayList<>(cc.entrySet());

        res.put("cc", ccList);
        res.put("dc", new ArrayList<>(dc));

        return res;
    }
    */
}
