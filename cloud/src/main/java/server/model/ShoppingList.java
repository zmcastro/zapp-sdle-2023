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
    public ShoppingList(String id) {
        this.id = id;
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
    public Iterator<Product> getProducts() {
        return products.values();
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
     * @param product Product to be removed
     */
    public void removeProduct(Product product) {
        products.rm(product.getName());
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
        for (Map.Entry<Pair<String, Integer>, Product> entry : products.elements()) {
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
     * @param String JSON object
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
