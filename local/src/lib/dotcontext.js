/**
 * DotContext helps to manage causal context between maps
 * This implementation is heavily based on [the one by Professor Carlos Baquero](https://github.com/CBaquero/delta-enabled-crdts),
 * further infroamtion can be found in the [paper](https://repositorium.sdum.uminho.pt/bitstream/1822/51520/1/CRDT-Deltas-NETYS2015.pdf).
 */
export class DotContext {
    #cc = new Map();
    #dc = new Set();

    fromJSON(context) {
        this.#cc = context.cc;
        this.#dc = context.dc;
    }

    /**
     * Get causal context (compact)
     *
     * @returns {Map} causal context
     */
    getCC() {
        const res = this.#cc;
        return res;
    }

    /**
     * Check if key-value pair in causal context (compact or dot cloud)
     *
     * @param {Array} dot - Tuple [key, value]
     * @return {boolean} true if dot is in causal context
     */
    dotIn(dot) {
        const key = dot[0];
        const value = dot[1];
        let res = false;

        if (this.#cc.has(key)) {
            res = value <= this.#cc.get(key);
        }

        if (this.#dc.has(dot)) {
            res = true;
        }

        return res;
    }

    /**
     * Attempts to compact causal context - transform DC to CC
     *
     * @return void
     */
    compact() {
        let canCompact;

        // We do it in a loop because we may be able to compact more than once
        do {
            canCompact = false;
            for (const [key, value] of this.#dc) {
                const ccValue = this.#cc.get(key); // undefined if key not in cc

                if (ccValue == undefined && value == 1) {
                    // key not in cc and value is 1
                    this.#cc.set(key, value);
                    this.#dc.delete(key);
                    canCompact = true;
                } else if (ccValue != undefined) {
                    // key in cc
                    if (value == ccValue + 1) {
                        // value is continuous
                        this.#cc.set(key, value);
                        this.#dc.delete(key);
                        canCompact = true;
                    } else if (value <= ccValue) {
                        // already have a more "recent" value
                        this.#dc.delete(key);
                    }
                }
            }
        } while (canCompact);
    }

    /**
     * Adds a dot to the causal context or update it's value if it's already there
     *
     * @param {Number} id - ID of the dot
     * @return {Array} Tuple [key, value]
     */
    makeDot(id) {
        if (this.#cc.has(id)) {
            this.#cc.set(id, this.#cc.get(id) + 1);
        } else {
            this.#cc.set(id, 1);
        }

        return [id, this.#cc.get(id)];
    }

    /**
     * Adds a dot to the dot cloud
     *
     * @param {Array} dot - Tuple [key, value]
     * @return void
     */
    insertDot(dot, compact = true) {
        this.#dc.add(dot);
        if (compact) this.compact();
    }

    /**
     * Join two DotContext
     *
     * @param {*} dotcontext - DotContext to join with
     * @return void
     */
    join(dotcontext) {
        // if joining with itself, do nothing
        if (dotcontext == this) return;

        // add compacted dots of otherCC to this
        for (const [key, value] of dotcontext.getCC())
            this.#cc.has(key)
                ? this.#cc.set(key, Math.max(this.#cc.get(key), value))
                : this.#cc.set(key, value);

        // add dots of otherDC to this
        for (const dot of dotcontext.getCC()) this.insertDot(dot, false);

        this.compact();
    }
}
