// Optimized Add-Wins Observed-Remove Map

import { DotContext } from "../crdt/dotcontext";

export class CCounter {
    /**
     * The unique identifier for the current user
     * @type {string}
     */
    #id;

    /**
     * Map that stores [element_id, dot_value] => element
     */
    #map = new Map();

    /**
     * DotContext that stores element_id => dot_value
     */
    #cc = new DotContext();

    constructor(id) {
        this.#id = id;
    }

    /**
     *
     * @returns {Map} map
     */
    getMap() {
        const res = this.#map;
        return res;
    }

    /**
     *
     * @returns {DotContext} causal context
     */
    getCC() {
        const res = this.#cc;
        return res;
    }

    setID(id) {
        this.#id = id;
    }

    elements() {
        return this.#map.keys();
    }

    inc(value = 1) {
        let base = 0;
        for (const [key, _value] of this.#map) {
            if (key[0] == this.#id) { // Should only ever be one
                base = Math.max(base, _value);
                this.#map.delete(key);
            }
        }
        const dot = this.#cc.makeDot(this.#id);
        this.#map.set(dot, base + value);
    }

    dec(value = 1) {
        if (this.read() - value < 0) return;
        let base = 0;
        for (const [key, _value] of this.#map) {
            if (key[0] == this.#id) { // Should only ever be one
                base = Math.max(base, _value);
                this.#map.delete(key);
            }
        }
        const dot = this.#cc.makeDot(this.#id);
        this.#map.set(dot, base - value);
    }

    read() {
        let res = 0;
        for (const [key, value] of this.#map) {
            res += value;
        }
        return res;
    }

    /**
     * Checks if [key,value] is in the map
     * Note: I'm aware this is preposterous code but apparently two arrays ["banana", 1] and ["banana", 1] are not equal
     * 
     * @param {Array} key 
     * @returns 
     */
    hasKey(key) {
        for (const [_key, value] of this.#map) {
            if (_key[0] == key[0] && _key[1] == key[1]) return true;
        }
        return false;
    }

    /**
     * Join two CCounter
     *
     * @param {CCounter} ccounter
     */
    join(ccounter) {
        this.#map = new Map(
            [...this.#map].filter(
                ([key, value]) =>
                ccounter.hasKey(key) || !ccounter.getCC().dotIn(key)
            ),
        );

        for (const [key, value] of ccounter.getMap()) {
            if (!this.#cc.dotIn(key)) {
                this.#map.set(key, value);
            }
        }

        this.#cc.join(ccounter.getCC());
    }

    fromJSON(counter) {
        for (const count of counter.map) {
            const dot = [count.name, count.context];

            this.#map.set(dot, count.value);
        }

        // FIXME: this seems to be an edge case, where due to the fact that we are reconstructing a map from a JSON, we need to
        // insert the dots in the CC, but using insertDot() won't work due to how the compacting works, there may be a better way
        // to do this, or not it could be fine as it is as it doesn't seem to be a common issue
        for (const dot of counter.context.cc)
            this.#cc.getCC().set(dot[0], dot[1]);

        if (counter.context.dc.length > 0)
            for (const dot of counter.context.dc)
                this.#cc.insertDot(dot, false);
    }

    toJSON() {
        const res = {
            map: [],
            context: {},
        };

        for (const [key, value] of this.#map) {
            res.map.push({
                name: key[0],
                context: key[1],
                value: value,
            });
        }

        res.context = this.#cc.toJSON();

        return res;
    }
}
