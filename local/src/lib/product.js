import { PNCounter } from "./pncounter";

export class Product {
    #name = null;
    #counter = null;

    /**
     * 
     * @param {String} name - Product name
     * @param {Strinh} u_id - User identifier 
     */
    constructor(name, u_id) {
        this.#name = name;
        this.#counter = new PNCounter(u_id);
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
     * @param {JSON} json 
     */
    fromJSON(json) {
        this.#name = json.name;
        this.#counter = new PNCounter().fromJSON(json.counter);
    }
}