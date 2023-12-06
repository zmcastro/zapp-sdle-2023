// Optimized Add-Wins Observed-Remove Map

import { DotContext } from "../crdt/dotcontext";
import { Product } from "../product";

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
     * DEPRECATED: #map is now correct
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
        for (const [key, value] of this.#map) {
            if (key[0] == element_id) {
                this.#map.delete(key);
            }
        }
        this.#map.set([dot[0], dot[1]], element); // [product.getName(), 1] => Product
    }

    /**
     * Removes an element from the map, keeping the causal context
     *
     * @param {*} element_id Identifier of the element
     */
    rm(element_id) {
        const dot = this.#cc.makeDot(element_id);

        for (const [key, value] of this.#map) {
            if (key[0] == element_id) {
                this.#map.delete(key);
            }
        }
    }

    get(element_id) {
        for (const [key, value] of this.#map) {
            if (key[0] == element_id) return value;
        }
        return undefined;
    }

    /**
     * Checks if [key,value] is in the map
     * Note: I'm aware this is preposterous code but apparently two arrays ["banana", 1] and ["banana", 1] are not equal
     *
     * @param {Array} key
     * @returns {Boolean}
     */
    hasKey(key) {
        for (const [_key, value] of this.#map) {
            console.log(
                _key,
                key,
                value,
                _key[0] == key[0] && _key[1] == key[1],
            );
            if (_key[0] == key[0] && _key[1] == key[1]) return true;
        }
        return false;
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
                    awormap.hasKey(key) || !awormap.getCC().dotIn(key),
            ),
        );

        for (const [key, value] of awormap.getMap()) {
            if (!this.#cc.dotIn(key)) {
                this.#map.set(key, value);
            }
        }

        this.#cc.join(awormap.getCC());
    }

    fromJSON(id, products) {
        this.#id = id;

        for (const jproduct of products.map) {
            const dot = [jproduct.name, jproduct.context];
            // TODO: Move this to the ShoppingList class
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
            map: [],
            context: {},
        };

        for (const [key, value] of this.#map) {
            const product = value.toJSON();
            product.name = key[0];
            product.context = key[1];
            res.map.push(product);
        }

        res.context = this.#cc.toJSON();

        return res;
    }

    toFrontendJSON() {
        const res = [];

        for (const [key, value] of this.#map) {
            res.push({
                name: value.getName(),
                count: value.value(),
            });
        }

        return res;
    }
}
