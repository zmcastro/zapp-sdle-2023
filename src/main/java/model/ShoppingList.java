package model;

import java.util.ArrayList;

public class ShoppingList {
    private int id;
    private String name;
    private ArrayList<Product> products;

    public ShoppingList(int id) {
        this.id = id;
        this.name = "Shopping List " + this.id;
        this.products = new ArrayList<>();
    }

    public ShoppingList(int id, String name) {
        this.id = id;
        this.name = name;
        this.products = new ArrayList<>();
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Product> getProducts() {
        return products;
    }
    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
}