// Optimized Add-Wins Observed-Remove Map

import { DotContext } from "./dotcontext";

/**
 * Add-Wins Observed-Remove Map (AWORMap) - A map that stores [element_id, dot_value] =>
 * element and keeps causal context using a DotContext that maps element_id => dot_value.
 * This is optimized thanks to the DotContext implementation.
 */
export class AWORMap {
    #id; // this is most likely deprecated
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

    elements() {
        return this.#map.keys();
    }

    /**
     * Returns the elements of the map that are in the causal context
     *
     * @returns {IterableIterator} elements
     */
    values() {
        const cc = this.#cc.getCC();
        return new Map(
            [...this.#map].filter(([key, value]) => cc.get(key[0]) == key[1]),
        ).values();
    }

    /**
     * Adds a new element to the map, keeping the causal context
     *
     * @param {*} element_id Identifier of the element
     * @param {*} element Element to be added
     */
    add(element_id, element) {
        const dot = this.#cc.makeDot(element_id);
        this.#map.set([dot[0], dot[1]], element);
    }

    /**
     * Removes an element from the map, keeping the causal context
     *
     * @param {*} element_id Identifier of the element
     */
    rm(element_id) {
        for (const [key, value] of this.#map) {
            if (key[0] == element_id) this.#map.delete(key);
        }
    }

    get(id) {
        return this.#map.get(id);
    }

    /**
     * Join two AWORMaps
     *
     * @param {AWORMap} awormap
     */
    join(awormap) {
        this.#map = new Map(
            [...this.#map].filter(
                ([key, value]) =>
                    awormap.getMap().has(key) || !awormap.getCC().dotIn(key),
            ),
        );

        for (const [key, value] of awormap.getMap()) {
            if (!this.#cc.dotIn(key, value[1])) {
                this.#map.set(key, value);
            }
        }

        this.#cc.join(awormap.getCC());
    }
}
