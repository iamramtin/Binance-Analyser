package com.company;

import com.company.classes.CoinDetails;
import com.company.classes.ListMap;

import java.io.IOException;
import java.util.List;

/**
 -------------
 THINGS TO ADD
 -------------

 - lowest price bought
 - highest price bought
 - lowest price coin has been sold
 - highest price coin has been sold
 - add total qty & individual qty to get accurate total cost and equity
 - add total equity (usdt) and total cost (usdt)

 * **/

public class Main {

    private static final String fileName = "transaction-history.txt";

    public static void main(String[] args) throws IOException {
        TransactionReader fr = new TransactionReader(fileName);

        ListMap<String, CoinDetails> coins = fr.getCoinList();
        List<String> coinNames = fr.getCoinNames();

//        printCoins(coins, coinNames);

        Table table = new Table(coins, coinNames);
        table.printTable();
    }

    public static void printCoins(ListMap<String, CoinDetails> coins, List<String> coinNames) {

        for (int i = 0; i < coins.size(); i++) {

            String name = coinNames.get(i);
            List<CoinDetails> coin = coins.get(name);

            System.out.println("   ***********************   ");
            System.out.println("   ***       " + name + "       ***   ");
            System.out.println("   ***********************   ");

            for (int j = 0; j < coin.size(); j++) {
                System.out.println("   Purchase: " + j + 1);
                System.out.println("   Qty: " + coin.get(j).getQty() + " " + name);
                System.out.println("   Price: " + coin.get(j).getPrice() + " USDT");
                System.out.println();
            }
        }
    }
}

/*
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
| COIN                              | QTY                               | PURCHASE PRICE                    | TOTAL EQUITY (USDT)               | TOTAL COST (USDT)
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
| USDT                              | 0.11723197                        |                                   | 0                                 | 0
| HOT                               | 7777.9935                         | 0.017654, 0.017654, 0.0171473     | 0                                 | 0
| WIN                               | 46383.2175                        | 0.00145525                        | 0                                 | 0
| VET                               | 408.6319                          | 0.167686                          | 0                                 | 0
--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
*/