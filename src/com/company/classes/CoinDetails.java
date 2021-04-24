package com.company.classes;

public class CoinDetails {
    private final double qty; // quantity of coin PURCHASED
    private final double price; // PURCHASE price
    private final double costUSDT; // price in USDT
    private final String time; // time of PURCHASE

    /** GETTERS **/
    public CoinDetails(double qty, double price, String time, double costUSDT) {
        this.qty = qty;
        this.price = price;
        this.time = time;
        this.costUSDT = costUSDT;
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

    public double getCostUSDT() {
        return costUSDT;
    }
}
