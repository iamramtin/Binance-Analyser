package com.company.classes;

public class CoinDetails {
    private final double qty; // quantity of coin PURCHASED
    private final double price; // PURCHASE price
    private final String time; // time of PURCHASE

    /** GETTERS **/
    public CoinDetails(double qty, double price, String time) {
        this.qty = qty;
        this.price = price;
        this.time = time;

    }

    /** GETTERS **/
    public double getQty() {
        return qty;
    }

    public double getPrice() {
        return price;
    }

    public String getTime() {
        return time;
    }
}
