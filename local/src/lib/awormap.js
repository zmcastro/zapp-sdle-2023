// Optimized Add-Wins Observed-Remove Map

import { DotContext } from "./dotcontext";
import { Product } from "./product";

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
     * Returns a map of the elements that are in the causal context
     *
     * @returns {Map} elements
     */
    flatten() {
        const cc = this.#cc.getCC();
        return new Map(
            [...this.#map].filter(([key, value]) => cc.get(key[0]) == key[1]),
        );
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

    get(element_id) {
        for (const [key, value] of this.flatten()) {
            if (key[0] == element_id) return value;
        }
        return null;
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
            if (!this.#cc.dotIn(key[0], key[1])) {
                this.#map.set(key, value);
            }
        }

        this.#cc.join(awormap.getCC());
    }

    fromJSON(id, products) {
        this.#id = id;

        for (const jproduct of products.map) {
            const dot = [jproduct.name, jproduct.context];
            const product = new Product();

            product.fromJSON(jproduct);

            this.#map.set(dot, product);
        }

        // FIXME: this seems to be an edge case, where due to the fact that we are reconstructing a map from a JSON, we need to
        // insert the dots in the CC, but using insertDot() won't work due to how the compacting works, there may be a better way
        // to do this, or not it could be fine as it is as it doesn't seem to be a common issue
        for (const dot of products.context.cc)
            this.#cc.getCC().set(dot[0], dot[1]);

        if (products.context.dc.length > 0)
            for (const dot of products.context.dc)
                this.#cc.insertDot(dot, false);

    }

    toJSON() {
        const res = {
            products: {
                map: [],
                context: {
                },
            },
        };

        for (const [key, value] of this.#map) {
            const product = value.toJSON();
            product.name = key[0];
            product.context = key[1];
            res.products.map.push(product);
        }

        res.products.context = this.#cc.toJSON()
        
        return res;
    }
}
