import { CCounter } from "$lib/crdt/ccounter";
import { PNCounter } from "$lib/crdt/pncounter";

export class Product {
    #name = null;
    #counter = null;
    #uid = null;

    /**
     *
     * @param {String} name - Product name
     * @param {Strinh} u_id - User identifier
     */
    constructor(name, u_id) {
        this.#uid = u_id;
        this.#name = name;
        this.#counter = new CCounter(u_id);
    }

    setUUID(u_id) {
        this.#uid = u_id;
        this.#counter.setID(u_id);
    }

    /**
     * Get product name
     *
     * @returns {String} Product name
     */
    getName() {
        return this.#name;
    }

    /**
     * Get product counter
     *
     * @returns {PNCounter} Product counter
     */
    getCounter() {
        const res = this.#counter;
        return res;
    }

    /**
     * Get product amount
     *
     * @returns {Number} Product amount
     */
    value() {
        return this.#counter.read();
    }

    /**
     * Increment product amount
     *
     * @param {Number} tosum
     */
    inc(tosum = 1) {
        this.#counter.inc(tosum);
    }

    /**
     * Decrement product amount
     *
     * @param {Number} tosum
     */
    dec(tosum = 1) {
        this.#counter.dec(tosum);
    }

    /**
     * Merge two products
     *
     * @param {Product} product
     */
    join(product) {
        this.#counter.join(product.#counter);
    }

    /**
     * Create a Product from a JSON object
     *
     * @param {JSON} product
     */
    fromJSON(product) {
        this.#name = product.name;
        this.#counter = new CCounter(this.#uid);
        this.#counter.fromJSON(product.counter);
    }

    toJSON() {
        const res = {
            counter: this.#counter.toJSON(),
        };

        return res;
    }
}
