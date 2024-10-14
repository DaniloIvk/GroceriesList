package com.example.grocerieslist;

public final class Product {
    private final String name;
    private final int amount;
    private final double price;

    /**
     * The default constructor.
     */
    public Product() {
        this.name = "Item";
        this.amount = 1;
        this.price = 0;
    }

    /**
     * Constructor with parameters for name, amount and price.
     *
     * @param name   Product name
     * @param amount Product amount
     * @param price  Product price
     */
    public Product(String name, int amount, double price) {
        this.name = name;
        this.amount = amount;
        this.price = price;
    }

    /**
     * Get the product name.
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get the product amount.
     *
     * @return int
     */
    public int getAmount() {
        return this.amount;
    }

    /**
     * Get the product price.
     *
     * @return double
     */
    public double getPrice() {
        return this.price;
    }

    /**
     * Get the total product price.
     * <span style="font-style:italic">(price &times; amount)</span>
     *
     * @return double
     */
    public double getTotalPrice() {
        return this.price * this.amount;
    }
}
