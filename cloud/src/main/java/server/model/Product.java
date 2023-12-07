package server.model;

import org.json.JSONObject;
import server.model.crdts.CCounter;

public class Product {

    public String uid;
    public String name;
    public CCounter counter;

    /**
     *
     * @param {String} name - Product name
     * @param {String} u_id - User identifier
     */
    public Product(String name, String u_id) {
        this.uid = u_id;
        this.name = name;
        this.counter = new CCounter(u_id);
    }

    /**
     * Get product name
     *
     * @return Product name
     */
    public String getName() {
        return name;
    }

    /**
     * Get product amount
     *
     * @return Product amount
     */
    public CCounter getCounter() {
        return counter;
    }

    public void setID(String u_id) {
        this.uid = u_id;
        this.counter.setID(u_id);
    }

    /**
     * Get product amount
     *
     * @return  {Number} Product amount
     */
    public int value() {
        return this.counter.read();
    }

    /**
     * Increment product amount
     *
     * @param toSum Amount to increment
     */
    public void inc(int toSum) {
        counter.inc(toSum);
    }

    /**
     * Decrement product amount
     *
     * @param toSum Amount to decrement
     */
    public void dec(int toSum) {
        counter.dec(toSum);
    }

    /**
     * Merge two products
     *
     * @param product Product to merge
     */
    public void join(Product product) {
        counter.join(product.counter);
    }

    /**
     * Create a Product from a JSON object
     *
     * @param json JSON object in string format
     */
    public void fromJSON(JSONObject json) {
        /* test: { \"name\" : \"ShopList\", \"counter\" : \"18\" } */

        this.name = json.getString("name");
        this.counter = new CCounter();
        this.counter.fromJSON((JSONObject) json.get("counter"));
    }

    public JSONObject toJSON() {
        return this.counter.toJSON();
    }
}
