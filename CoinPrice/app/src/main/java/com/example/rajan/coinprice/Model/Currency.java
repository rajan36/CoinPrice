package com.example.rajan.coinprice.Model;

/**
 * Created by rajan on 4/12/17.
 */

public enum Currency {
    BITCOIN("BTC"), ETHERIUM("ETH"), BITCOINCASH("BCH"), RIPPLE("XRP"), LITECOIN("LTC"), MIOTA("MIOTA"), OMG("OMG"), GNT("GNT");

    private String text;

    Currency(String name) {
        this.text = name;
    }

    public static Currency fromString(String text) {
        for (Currency b : Currency.values()) {
            if (b.text.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }

    public String getText() {
        return this.text;
    }

}
