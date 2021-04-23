package com.company;

import com.company.classes.CoinDetails;
import com.company.classes.ListMap;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

class Table {

    private static final List< List<String> > table = new ArrayList<>();
    private static final String[] headings = {"COIN", "QUANTITY", "PURCHASE PRICE"};
    private static int maxFormattingLen = 0; // spacing to use in table format

    public Table(ListMap<String, CoinDetails> coins, List<String> coinNames) throws IOException {
        createTable();
        initializeTable(coinNames);
        populateTableData(coins, coinNames);
    }

    public void createTable() {
        /* add row headings to table */

        table.add(new ArrayList<>());
        for (String h : headings) {
            table.get(0).add(h);
        }
    }

    public void initializeTable(List<String> coinNames) {
        /* add coin names to the first column and fill the rest of the columns w/ 0s */

        for (int i = 0; i < coinNames.size(); i++) {
            table.add(new ArrayList<>());
            table.get(i + 1).add(coinNames.get(i)); // coin name

            for (int j = 0; j < headings.length - 1; j++) {
                // add columns for the other values such as qty and price
                table.get(i + 1).add("");
            }
        }
    }

    /** WORKING: Change -> don't store values as String, rather as a List<Double> **/
    public void populateTableData(ListMap<String, CoinDetails> coins, List<String> coinNames) {

        for (int i = 0; i < coins.size(); i++) {

            String currCoin = coinNames.get(i);

            for (int j = 0; j < coins.get(currCoin).size(); j++) {
                int row = getCoinRow(currCoin), qtyCol = 1, priceCol = 2;

                // UPDATE ROW QTY
                String tableQty = table.get(row).get(qtyCol);
                String tablePrice = table.get(row).get(priceCol);

                if (j == 0) {
                    // if entry is empty, then overwrite the emptiness with the value
                    table.get(row).set(qtyCol, coins.get(currCoin).get(0).getQty() + "");
                    table.get(row).set(priceCol, coins.get(currCoin).get(0).getPrice() + "");
                }
                else {
                    // if entries already exist, then append the additional values
                    table.get(row).set(qtyCol, tableQty + ", " + coins.get(currCoin).get(0).getQty() + "");
                    table.get(row).set(priceCol, tablePrice + ", " + coins.get(currCoin).get(0).getPrice() + "");
                }

                /* FOR FORMATTING: update row length */
                String currPrice = table.get(row).get(priceCol);
                if (currPrice.length() > maxFormattingLen) maxFormattingLen = currPrice.length() + 5;
            }
        }
    }

    /***************** USEFUL FUNCTIONS ***************/

    public double round(double n) {
        return (double) Math.round(n * 100000000d) / 100000000d;
    }

    public int getCoinRow(String coin) {
        int row;

        for (row = 1; row < table.size(); row++) {
            int col = table.get(row).indexOf(coin);
            if (col != -1) {
                return row;
            }
        }

        return -1;
    }


    /****************** OUTPUT-RELATED ****************/

    public void printTable() {
        System.out.println();

        drawTableLines();
        for (int r = 0; r < table.size(); r++) {
            if (r == 1) drawTableLines();

            for (int c = 0; c < table.get(r).size(); c++) {
                String format = "| %-" + (maxFormattingLen) + "s";
                System.out.printf(format, table.get(r).get(c));
            }
            System.out.println();
        }
        drawTableLines();
    }

    public void drawTableLines() {
        System.out.print("-");
        for (int i = 0; i < table.size() - 1; i++) {
            for (int j = 0; j < maxFormattingLen; j++) {
                System.out.print("-");
            }
            System.out.print("-");
        }
        System.out.println();
    }
}
