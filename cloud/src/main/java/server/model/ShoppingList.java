package server.model;

import java.util.Iterator;

public class ShoppingList {
    private String id;
    private AWORMap products;

    /**
     * Constructor
     *
     * @param id Shopping list identifier
     */
    public ShoppingList(String id) {
        this.id = id;
        this.products = new AWORMap(id);
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
        for (Product currProduct : products.elements()) {
            Product newProduct = shoppingList.get(currProduct.getName());
            if (newProduct != null) {
                currProduct.join(newProduct);
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
     * @param json JSON object
     */
    public void fromJSON(JSON json) {
        this.products = new AWORMap(json.getJSONObject("products"));
    }
}
