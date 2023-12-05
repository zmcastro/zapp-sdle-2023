package server.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;

import org.json.JSONObject;
import server.model.crdts.PNCounter;

public class Product {

    public String uid;
    public String name;
    public PNCounter counter;

    /**
     *
     * @param {String} name - Product name
     * @param {String} u_id - User identifier
     */
    public Product(String name, String u_id) {
        this.uid = u_id;
        this.name = name;
        this.counter = new PNCounter(u_id);
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
    public int getCounter() {
        return counter.read();
    }

    public void setUUID(String u_id) {
        this.uid = u_id;
        this.counter.setUUID(u_id);
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
        // this.counter = new PNCounter(json.get("counter"));
    }
}
