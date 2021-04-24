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

            int row = getCoinRow(currCoin), qtyCol = 1, priceCol = 2;

            // get row number of each cryptocurrency
            for (int j = 0; j < coins.get(currCoin).size(); j++) {

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
            }
        }
    }

    public void fillTableDetails() {

        for (int i = 0; i < coins.size(); i++) {
            String currCoin = coinNames.get(i);

            int row = getCoinRow(currCoin);
            int totalQtyCol = 1, totalCostCol = 2, avePriceCol = 3;

            for (int j = 0; j < coins.get(currCoin).size(); j++) {

                table.get(row).set(totalQtyCol, getTotalQty(currCoin) + "");
                table.get(row).set(totalCostCol, getTotalCost(currCoin) + "");
                table.get(row).set(avePriceCol, getAvePrice(currCoin) + "");
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

    /******************* CALCULATIONS *****************/

    public double getTotalQty(String coinName) {
        double sum = 0;

        for (int i = 0; i < coins.get(coinName).size(); i++) {
            sum += coins.get(coinName).get(i).getQty();
        }

        return round(sum);
    }

    public double getTotalCost(String coinName) {
        double sum = 0;

        for (int i = 0; i < coins.get(coinName).size(); i++) {
            sum += coins.get(coinName).get(i).getCostUSDT();
        }

        return round(sum);
    }

    public double getAvePrice(String coinName) {
        double sum = 0;

        for (int i = 0; i < coins.get(coinName).size(); i++) {
            sum += coins.get(coinName).get(i).getPrice();
        }

        return round(sum / coins.get(coinName).size());
    }


    /****************** OUTPUT-RELATED ****************/

    public void setFormat() {
        /* FOR FORMATTING: update row length to match longest values */

        for (List<String> strings : table) {
            for (int c = 0; c < strings.size(); c++) {
                String s = strings.get(c);
                if (s.length() > formatLen[c]) formatLen[c] = s.length() + 3;
            }
        }
    }

    public void drawTableLines() {
        System.out.print("-");

        for (int len : formatLen) {
            for (int i = 0; i < len; i++) {
                System.out.print("-");
            }
        }

        for (int i = 0; i < formatLen.length * 2; i++) {
            System.out.print("-");
        }

        System.out.println();
    }


    public void printTable(String title) {
        setFormat();

        /* TITLE */
        int totalLen = 0;
        for (int len : formatLen) totalLen += len;


        String style = "==", spacing = "        ";
        title = style + spacing + title.toUpperCase() + spacing + style;

        String styleHeader = "";
        for (int i = 0; i < title.length(); i++) styleHeader += "=";

        System.out.printf("%" + ((totalLen + styleHeader.length() + 3) / 2) + "s", styleHeader);
        System.out.printf("\n%" + ((totalLen + title.length() + 3) / 2) + "s\n", title);
        System.out.printf("%" + ((totalLen + styleHeader.length() + 3) / 2) + "s\n", styleHeader);

        /* **************************** */

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
        System.out.println();
    }
}
