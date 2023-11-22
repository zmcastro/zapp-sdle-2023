import { AWORMap } from './awormap.js';
import { Product } from './product.js';

export class ShoppingList {
    #id = null;
    #products = null;

    /**
     * 
     * @param {String} id - Shopping list identifier
     */
    constructor(id) {
        this.#id = id;
        this.#products = new AWORMap();
    }

    /**
     * Get shopping list identifier
     * 
     * @returns shopping list identifier
     */
    getId() {
        return this.#id;
    }

    /**
     * Get shopping list products
     * 
     * @returns Products list
     */
    getProducts() {
        return this.#products.values();
    }
    
    /**
     * Add new product to shopping list
     * 
     * @param {Product} product 
     */
    addProduct(product) {
        this.#products.add(product.getName(), product);
    }

    /**
     * Remove product from shopping list
     * 
     * @param {Product} product 
     */
    removeProduct(product) {
        this.#products.rm(product.getName());
    }

    /**
     * Merge shopping lists
     * 
     * @param {ShoppingList} shoppinglist
     */
    join(shoppinglist) {
        // join products
        this.#products.join(shoppinglist.#products);

        // join counters
        for (const curr_product of this.#products.elements()) {
            const new_product = shoppinglist.getProducts().get(curr_product.getName());
            if (new_product) curr_product.join(new_product);
        }
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
        this.#products = new AWORMap(json.products);
    }
}