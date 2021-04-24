package com.company;

import com.company.classes.CoinDetails;
import com.company.classes.ListMap;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * USEFUL FILE STRUCTURES
 * Transaction = stores all interrelated data for a specific date
 *  **/

class OperationPair {
    private final String time;
    private final Double change;

    public OperationPair(String time, double change) {
        this.time = time;
        this.change = change;
    }

    public String getTime() {
        return time;
    }

    public double getChange() {
        return change;
    }
}

class Transaction {
    private final OperationPair buy;
    private final OperationPair sell;
    private final OperationPair fee;
    private final OperationPair commissionFee;

    public Transaction(OperationPair buy, OperationPair sell, OperationPair fee, OperationPair commissionFee) {
        this.buy = buy;
        this.sell = sell;
        this.fee = fee;
        this.commissionFee = commissionFee;
    }

    public OperationPair getBuy() {
        return buy;
    }

    public OperationPair getSell() {
        return sell;
    }

    public OperationPair getFee() {
        return fee;
    }

    public OperationPair getCommissionFee() {
        return commissionFee;
    }
}

/** BEGINNING OF CODE **/
public class TransactionReader {

    private static final ListMap<String, CoinDetails> coins = new ListMap<>();
    private static final List<String> coinNames = new ArrayList<>();

    public TransactionReader(String fileName) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String s = br.readLine(); // skip first line

        // initialize pairs
        OperationPair buyOperationPair = null;
        OperationPair sellOperationPair = null;
        OperationPair commissionFeeOperationPair = null;
        OperationPair feeOperationPair = null;

        // store the 4 transaction details in a map with the key being the time of transaction
        Map<String, Transaction> transactions = new HashMap<>();

        double costUSDT = -1;

        /* - READ FILE LINES - */
        while ((s = br.readLine()) != null) {
            String[] line = s.split(",");

            // only store values needed from the file
            String fileTime = line[0];
            String fileOperation = line[2];
            String fileCoin = line[3];
            double fileChange = Double.parseDouble(line[4]);

            /* - - - - - - - - - - - - - - - - - - - - - - - - - - - - */

            // do something if a deposit
//            if (fileOperation.equals("Transaction Related") && fileCoin.equals("USDT")) { }
            
            // store transaction values if not a deposit
            if (fileOperation.equals("Buy")) buyOperationPair = new OperationPair(fileTime, fileChange);
            if (fileOperation.equals("Sell")) {
                costUSDT = Math.abs(fileChange);
                sellOperationPair = new OperationPair(fileTime, fileChange);
            }
            if (fileOperation.equals("Fee")) feeOperationPair = new OperationPair(fileTime, fileChange);
            if (fileOperation.equals("Commission Fee Shared With You")) commissionFeeOperationPair = new OperationPair(fileTime, fileChange);

            // check if all rows have been received
            if (checkTriple(buyOperationPair, sellOperationPair, feeOperationPair, commissionFeeOperationPair)) {
                Transaction transaction = new Transaction(buyOperationPair, sellOperationPair, commissionFeeOperationPair, feeOperationPair);
                transactions.put(fileTime, transaction);

                // add coin name to the list - needed for looping purposes
                if (!coinNames.contains(fileCoin)) coinNames.add(fileCoin);

                // add the purchase for the coin
                double purchaseQty = getPurchaseQty(transactions, fileTime);
                double purchasePrice = getPurchasePrice(transactions, fileTime);
                addPurchase(fileCoin, purchaseQty, purchasePrice, fileTime, costUSDT);

                // reset values to avoid duplications being entered into the map
                buyOperationPair = null;
                sellOperationPair = null;
                feeOperationPair = null;
                commissionFeeOperationPair = null;
                costUSDT = -1;
            }
        }

        br.close();
    }

    public ListMap<String, CoinDetails> getCoinList () {
        return coins;
    }

    public List<String> getCoinNames () {
        return coinNames;
    }

    private boolean checkTriple(OperationPair buy, OperationPair sell, OperationPair fee, OperationPair commissionFee) {
        if (buy != null && sell != null && fee != null && commissionFee != null) {
            return buy.getTime().equals(sell.getTime()) && sell.getTime().equals(fee.getTime()) &&
                    fee.getTime().equals(commissionFee.getTime());
        }

        return false;
    }

    public double getCoinPrice (CoinDetails details) {
        return details.getPrice();
    }

    public double getCoinQty (CoinDetails details) {
        return details.getQty();
    }

    public void printCoins(){

        for (int i = 0; i < coins.size(); i++) {

            String name = coinNames.get(i);
            List<CoinDetails> coin = coins.get(name);

            System.out.println("   ***********************   ");
            System.out.println("   ***       " + name + "       ***   ");
            System.out.println("   ***********************   ");

            for (int j = 0; j < coin.size(); j++) {
                System.out.println("   Purchase: " + j + 1);
                System.out.println("   Qty: " + coin.get(j).getQty()  + " " + name);
                System.out.println("   Price: " + coin.get(j).getPrice() + " USDT");
                System.out.println();
            }
        }
    }

    // CALCULATE: Purchase Price
    private static double getPurchasePrice(Map<String, Transaction> map, String time) {
        Transaction t = map.get(time);
        return round(Math.abs(t.getSell().getChange() / t.getBuy().getChange()));
    }

    // CALCULATE: Quantity Purchased
    private static double getPurchaseQty(Map<String, Transaction> map, String time) {
        Transaction t = map.get(time);
        return round(Math.abs(t.getBuy().getChange() + t.getCommissionFee().getChange()) + t.getFee().getChange());
    }

    // cut off at eight decimal places
    private static double round(double n) {
        return (double) Math.round(n * 100000000d) / 100000000d;
    }

    private void addPurchase(String coinName, double qty, double price, String time, double costUSDT) {
        CoinDetails details = new CoinDetails(qty, price, time, costUSDT);
        coins.put(coinName, details);
    }

    public double getTotalCost(String coinName) {
        double sum = 0;

        for (int i = 0; i < coins.get(coinName).size(); i++) {
            sum += coins.get(coinName).get(i).getCostUSDT();
        }

        return sum;
    }
}