import { AWORMap } from "$lib/crdt/awormap.js";
import { Product } from "$lib/product.js";

export class ShoppingList {
    #id = null;
    #products = null;
    #name = null;
    #remove = null;

    /**
     *
     * @param {String} id - Shopping list identifier
     */
    constructor(id, name, remove = false) {
        this.#id = id;
        this.#products = new AWORMap();
        this.#name = name;
        this.#remove = remove;
    }

    setName(name) {
        this.#name = name;
    }

    getName() {
        return this.#name;
    }

    setRemove(remove) {
        this.#remove = remove;
    }

    getRemove() {
        return this.#remove;
    }

    /**
     * Get shopping list identifier
     *
     * @returns shopping list identifier
     */
    getID() {
        const res = this.#id;
        return res;
    }

    /**
     * Get shopping list products
     *
     * @returns Products AWORMap
     */
    getProducts() {
        const res = this.#products;
        return res;
    }

    /**
     * Get specific product
     *
     * @param {String} product_name
     *
     * @returns Products list
     */
    getProduct(product_name) {
        return this.#products.get(product_name);
    }

    /**
     * Add new product to shopping list
     *
     * @param {Product} product
     */
    addProduct(product) {
        const exists = this.#products.get(product.getName());
        if (!exists) this.#products.add(product.getName(), product);
        this.incProduct(product.getName());
    }

    /**
     * Remove product from shopping list
     *
     * @param {Product} product
     */
    removeProduct(product_name) {
        this.#products.rm(product_name);
    }

    /**
     * Increment product amount
     *
     * @param {String} product_name
     * @param {Number} tosum
     */
    incProduct(product_name, tosum = 1) {
        const product = this.#products.get(product_name);
        if (product) product.inc(tosum);
    }

    /**
     * Decrement product amount
     *
     * @param {String} product_name
     * @param {Number} tosum
     */
    decProduct(product_name, tosum = 1) {
        const product = this.#products.get(product_name);
        if (product && product.value() > 0) product.dec(tosum);
        if (product && product.value() <= 0 && this.#remove)
            this.removeProduct(product_name);
    }

    /**
     * Merge shopping lists
     *
     * @param {ShoppingList} shoppinglist
     */
    join(shoppinglist) {
        // join counters
        for (const [key, value] of this.#products.getMap()) {
            const new_product = shoppinglist.getProduct(key[0]);
            if (new_product) {
                value.join(new_product);
            }
        }

        // join products
        this.#products.join(shoppinglist.getProducts());
    }

    /**
     * Get product from shopping list
     *
     * @param {String} product_name
     * @returns Product
     */
    get(product_name) {
        return this.#products.get(product_name);
    }

    /**
     * Create a Shopping List from a JSON object
     *
     * @param {JSON} json
     */

    fromJSON(json) {
        this.#id = json.id;
        this.#name = json.name;
        this.#remove = json.remove;
        this.#products = new AWORMap();
        // TODO: Iterate over products here
        this.#products.fromJSON(json.id, json.products);
    }

    toJSON() {
        const res = {
            id: this.#id,
            name: this.#name,
            remove: this.#remove,
            products: this.#products.toJSON(),
        };
        return res;
    }

    toFrontendJSON() {
        const res = {
            id: this.#id,
            name: this.#name,
            products: this.#products.toFrontendJSON(),
        };
        return res;
    }

    setUUID(uuid) {
        for (const [key, value] of this.#products.getMap()) {
            value.setUUID(uuid);
        }
    }
}
