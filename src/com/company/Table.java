package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Table {

    private static final List< List<String> > table = new ArrayList<>();
    private static final String[] headings = {"COIN", "QTY", "PURCHASE PRICE", "TOTAL EQUITY (USDT)", "TOTAL COST (USDT)"};
    private static int maxFormattingLen = 0; // spacing to use in table format

    public Table(String fileName) throws IOException {
        initializeTable();
        fillTable(fileName);
        populateTableData(fileName);
    }

    public void fillTable(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        Map<Integer, String> coinMap = new HashMap<>();

        String s;

        int coinIndex = 0;

        while((s = br.readLine()) != null) {
            String [] line = s.split(",");
            String coin = line[3];

            /* ADD COINS */
            if (!coinMap.containsValue(coin) && !coin.equals("ZAR")) {
                coinMap.put(coinIndex, coin);
                coinIndex++;
            }
        }

        // Add coinMap to table and fill rest of columns with 0s
//        for (int index = 0; index < coinMap.size(); index++) {
//            table.add(new ArrayList<>());
//            table.get(index + 1).add(coinMap.get(index)); // coin name
//            table.get(index + 1).add("0"); // qty
//            table.get(index + 1).add(""); // purchase price
//            table.get(index + 1).add("0"); // total equity
//            table.get(index + 1).add("0"); // total cost
//        }

        for (int index = 0; index < coinMap.size(); index++) {
            table.add(new ArrayList<>());
            table.get(index + 1).add(coinMap.get(index)); // coin name
            table.get(index + 1).add("0"); // qty
            table.get(index + 1).add(""); // purchase price
            table.get(index + 1).add("0"); // total equity
            table.get(index + 1).add("0"); // total cost
        }

        br.close();
    }

    public int[] getAxes(String coin) {
        int row = -1, col = -1;

        for (int r = 0; r < table.size(); r++) {
            col = table.get(r).indexOf(coin);
            if (col != -1) {
                row = r;
                break;
            }
        }

        return new int[]{row, col};
    }

    public void updateQty(String coin, String change) {
        int[] axes = getAxes(coin);
        int row = axes[0], col = axes[1] + 1;

        String qty = table.get(row).get(col);
        double newPriceQty = round(Double.parseDouble(qty) + Double.parseDouble(change));
        table.get(row).set(col, newPriceQty + "");
    }


    public void updatePrice(String coin, List<Double> priceList) {
        int[] axes = getAxes(coin);
        int row = axes[0], col = axes[1] + 2;

        String currPrice = table.get(row).get(col);
        double newPrice = round(priceList.get(0) / priceList.get(2));

        if (currPrice.equals("")) table.get(row).set(col, newPrice + "");
        else table.get(row).set(col, currPrice + ", " + newPrice);

        // ?? LATEST ADDITION! I think it's working! ??
        updateQty(coin, (priceList.get(4) - priceList.get(6)) + "");

        // formatting: update row length
        currPrice = table.get(row).get(col);
        if (currPrice.length() > maxFormattingLen) maxFormattingLen = currPrice.length() + 5;
    }

    public double round(double n) {
        return (double) Math.round(n * 100000000d) / 100000000d;
    }

    public void populateTableData(String fileName) throws IOException {

        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String s = br.readLine(); // skip first line

        // list and variables to store prices
        List<Double> priceList = new ArrayList<Double>();
        String priceCoin = "";
        priceList.add(-1.0); // [0]: total usdt price charge
        priceList.add(0.0); //  [1]: check if value found
        priceList.add(-1.0); // [2]: crypto qty bought
        priceList.add(0.0); //  [3]: check if value found
        priceList.add(-1.0); // [4]: commission fee
        priceList.add(0.0); //  [5]: check if value found
        priceList.add(-1.0); // [6]: fee
        priceList.add(0.0); //  [7]: check if value found

        while((s = br.readLine()) != null) {
            String [] currLine = s.split(",");

            // only variables from the imported file being used
            String operation = currLine[2];
            String coin = currLine[3];
            String change = currLine[4];

            /** UPDATE QUANTITIES **/
            if (operation.equals("Transaction Related") && coin.equals("USDT")) updateQty(coin, change);

            if (operation.equals("Sell")) {
                updateQty(coin, change); // update quantity

                // if crypto price not yet stored
                if (priceList.get(1) == 0.0) {
                    if (!coin.equals("USDT")) priceCoin = coin; // store coin name
                    priceList.set(0, Math.abs(Double.parseDouble(change))); // update priceList -> usdt price
                    priceList.set(1, 1.0); // update priceList -> usdt check
                }
            }

            if (operation.equals("Buy")) {
                updateQty(coin, change);

                // if usdt price not yet stored
                if (priceList.get(3) == 0.0) {
                    if (!coin.equals("USDT")) priceCoin = coin; // store coin name
                    priceList.set(2, Double.parseDouble(change)); // update priceList -> crypto price
                    priceList.set(3, 1.0); // update priceList -> crypto check
                }
            }

            if (operation.equals("Commission Fee Shared With You")) {
                // if commission fee price not yet stored
                if (priceList.get(5) == 0.0) {
                    priceList.set(4, Double.parseDouble(change)); // update priceList -> commission fee
                    priceList.set(5, 1.0); // update priceList -> commission fee check
                }
            }

            if (operation.equals("Fee")) {
                // if fee price not yet stored
                if (priceList.get(7) == 0.0) {
                    priceList.set(6, Math.abs(Double.parseDouble(change))); // update priceList -> fee
                    priceList.set(7, 1.0); // update priceList -> fee check
                }
            }

            /** UPDATE PRICES **/
            if (priceList.get(1) == 1 && priceList.get(3) == 1
                    && priceList.get(5) == 1 && priceList.get(7) == 1) updatePrice(priceCoin, priceList);

            // RESET values when both prices are found (checks set to true)
            if (priceList.get(1) == 1 && priceList.get(3) == 1
                    && priceList.get(5) == 1 && priceList.get(7) == 1) {
                priceList.set(0, -1.0);
                priceList.set(1, 0.0);
                priceList.set(2, -1.0);
                priceList.set(3, 0.0);
                priceList.set(4, -1.0);
                priceList.set(5, 0.0);
                priceList.set(6, -1.0);
                priceList.set(7, 0.0);
            }
        }

        br.close();
    }

    // ADD ROW HEADINGS TO TABLE
    public void initializeTable() throws IOException {
        table.add(new ArrayList<>());
        for (String h : headings) {
            table.get(0).add(h);
        }
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
