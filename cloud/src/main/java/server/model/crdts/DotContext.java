package server.model.crdts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DotContext {
    private Map<Integer, Integer> cc = new HashMap<>();
    private Set<int[]> dc = new HashSet<>();

    /**
     * Constructor with a context
     *
     * @param context DotContext
     */
    public DotContext(DotContext context) {
        this.cc = new HashMap<>(context.cc);
        this.dc = new HashSet<>(context.dc);
    }

    /**
     * Get causal context (compact)
     *
     * @return Causal context
     */
    public Map<Integer, Integer> getCC() {
        return cc;
    }

    /**
     * Check if key-value pair is in causal context (compact or dot cloud)
     *
     * @param dot Tuple [key, value]
     * @return true if dot is in causal context
     */
    public boolean dotIn(int[] dot) {
        int key = dot[0];
        int value = dot[1];

        if (cc.containsKey(key)) {
            return value <= cc.get(key);
        }

        return dc.contains(dot);
    }

    /**
     * Attempts to compact causal context - transform DC to CC
     */
    public void compact() {
        boolean canCompact;

        // We do it in a loop because we may be able to compact more than once
        do {
            canCompact = false;
            for (int[] dot : dc) {
                int key = dot[0];
                int value = dot[1];

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
    public int[] makeDot(int id) {
        cc.merge(id, 1, Integer::sum);

        return new int[]{id, cc.get(id)};
    }

    /**
     * Adds a dot to the dot cloud
     *
     * @param dot Tuple [key, value]
     * @param compact Flag to indicate whether to compact
     */
    public void insertDot(int[] dot, boolean compact) {
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
        for (Map.Entry<Integer, Integer> entry : dotContext.getCC().entrySet()) {
            int key = entry.getKey();
            int value = entry.getValue();

            cc.merge(key, value, Integer::max);
        }

        // Add dots of otherDC to this
        for (int[] dot : dotContext.dc) {
            insertDot(dot, false);
        }

        compact();
    }
}
