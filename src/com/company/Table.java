package com.company;

import com.company.classes.CoinDetails;
import com.company.classes.ListMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class Table {

    private final List< List<String> > table = new ArrayList<>();
    private int[] formatLen; // spacing to use in table format

    private final ListMap<String, CoinDetails> coins;
    private final List<String> coinNames;
    private final String[] headings;

    public Table(ListMap<String, CoinDetails> coins, List<String> coinNames, String[] headings) {
        this.coins = coins;
        this.coinNames = coinNames;
        this.headings = headings;

        formatLen = new int[headings.length];
        Arrays.fill(formatLen, 0);

        createTable();
        initializeTable();
    }

    public void createTable() {
        /* add row headings to table */

        table.add(new ArrayList<>());
        for (String h : headings) {
            table.get(0).add(h);
        }
    }

    public void initializeTable() {
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

    public void fillTable() {

        for (int i = 0; i < coins.size(); i++) {

            String currCoin = coinNames.get(i);

            // get row number of each cryptocurrency
            for (int j = 0; j < coins.get(currCoin).size(); j++) {
                int row = getCoinRow(currCoin), qtyCol = 1, priceCol = 2;

                // UPDATE ROW QTY
                String tableQty = table.get(row).get(qtyCol);
                String tablePrice = table.get(row).get(priceCol);

                if (j == 0) {
                    // if entry is empty, then overwrite the emptiness with the value
                    table.get(row).set(qtyCol, coins.get(currCoin).get(j).getQty() + "");
                    table.get(row).set(priceCol, coins.get(currCoin).get(j).getPrice() + "");
                }
                else {
                    // if entries already exist, then append the additional values
                    table.get(row).set(qtyCol, tableQty + ", " + coins.get(currCoin).get(j).getQty() + "");
                    table.get(row).set(priceCol, tablePrice + ", " + coins.get(currCoin).get(j).getPrice() + "");
                }

//                setFormat(row, priceCol);
            }
        }
    }

    public void fillTableTotals() {

        for (int i = 0; i < coins.size(); i++) {

            String currCoin = coinNames.get(i);

            double sum = 0;
            double initValue = -1;

            for (int j = 0; j < coins.get(currCoin).size(); j++) {
                int row = getCoinRow(currCoin), qtyCol = 1, priceCol = 2;

                // UPDATE ROW QTY
                String tableQty = table.get(row).get(qtyCol);

                if (j == 0) {
                    // if entry is empty, then overwrite the emptiness with the value
                    table.get(row).set(qtyCol, coins.get(currCoin).get(j).getQty() + "");
                }
                else {
                    // if entries already exist, then append the additional values
                    table.get(row).set(qtyCol, round(Double.parseDouble(tableQty) + coins.get(currCoin).get(j).getQty()) + "");
                }

                // get average cost
                sum += coins.get(currCoin).get(j).getPrice();
                if (j == coins.get(currCoin).size() - 1) {
                    // print average to table once full sum has been accumulated
                    table.get(row).set(priceCol,  round(sum  / coins.get(currCoin).size()) + "");
                }

//                setFormat(row, qtyCol);
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

    public void setFormat() {
        /* FOR FORMATTING: update row length to match longest values */

        for (List<String> strings : table) {
            for (int c = 0; c < strings.size(); c++) {
                String s = strings.get(c);
                if (s.length() > formatLen[c]) formatLen[c] = s.length() + 5;
            }
        }
    }

    public void printTable() {
        setFormat();
        System.out.println();

        drawTableLines();
        for (int r = 0; r < table.size(); r++) {
            if (r == 1) drawTableLines();

            for (int c = 0; c < table.get(r).size(); c++) {
                String format = "| %-" + (formatLen[c]) + "s";
                System.out.printf(format, table.get(r).get(c));
            }
            System.out.println();
        }
        drawTableLines();
    }

    public void drawTableLines() {
        System.out.print("-");

        for (int r = 0; r < table.size() - 1; r++) {
            for (int c = 0; c < formatLen[r]; c++) {
                System.out.print("-");
            }
            System.out.print("-");
        }
        System.out.println();
    }
}
