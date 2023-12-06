package server.model;

import org.json.JSONObject;

import server.model.crdts.AWORMap;
import server.model.utils.Pair;

import java.util.Iterator;
import java.util.Map;

public class ShoppingList {
    private String id;
    private String name;
    private AWORMap products;

    /**
     * Constructor
     *
     * @param id Shopping list identifier
     */
    public ShoppingList(String id, String name) {
        this.id = id;
        this.name = name;
        this.products = new AWORMap();
    }

    /**
     * Get shopping list identifier
     *
     * @return Shopping list identifier
     */
    public String getId() {
        return id;
    }

    /**
     * Get shopping list display name
     *
     * @return Shopping list name
     */
    public String getName() {
        return name;
    }

    /**
     * Get shopping list products
     *
     * @return Products list
     */
    public AWORMap getProducts() {
        return products;
    }

    /**
     * Add a new product to the shopping list
     *
     * @param product Product to be added
     */
    public void addProduct(Product product) {
        products.add(product.getName(), product);
    }

    /**
     * Remove a product from the shopping list
     *
     * @param productName Name of the product to be removed
     */
    public void removeProduct(String productName) {
        products.rm(productName);
    }

    /**
     * Increment product amount
     *
     * @param {String} product_name
     * @param {Number} tosum
     */
    public void incProduct(String productName, int toSum) {
        Product product = this.products.get(productName);
        if (product != null && product.value() >= 0) {
            product.inc(toSum);
        }
    }

    /**
     * Decrement product amount
     *
     * @param {String} product_name
     * @param {Number} tosum
     */
    public void decProduct(String productName, int toSum) {
        Product product = this.products.get(productName);
        if (product != null && product.value() > 0) {
            product.dec(toSum);
        }
    }

    /**
     * Merge two shopping lists
     *
     * @param shoppingList Shopping list to be merged
     */
    public void join(ShoppingList shoppingList) {
        // Join products
        products.join(shoppingList.products);

        // Join counters
        for (Map.Entry<Pair<String, Integer>, Product> entry : products.entries()) {
            Product newProduct = shoppingList.get(entry.getKey().getKey()); // gets the element_id
            if (newProduct != null) {
                entry.getValue().join(newProduct);
            }
        }
    }

    /**
     * Get a product from the shopping list
     *
     * @param productName Name of the product to retrieve
     * @return Product
     */
    public Product get(String productName) {
        return products.get(productName);
    }

    /**
     * Create a Shopping List from a JSON object
     *
     * @param jsonString JSON string received
     */
    public void fromJSON(String jsonString) {
        JSONObject json = new JSONObject(jsonString);
        this.id = json.getString("id");
        this.name = json.getString("name");
        this.products = new AWORMap();
        this.products.fromJSON(json.getString("id"), json.getJSONObject("products"));
    }

    /*
    public String toJSON() {
        JSONObject res = new JSONObject();
        res.put("id", this.id);
        res.put("name", this.name);
        res.put("products", new JSONObject(this.products.toJSON()));
        return res.toString();
    }
    */
}
