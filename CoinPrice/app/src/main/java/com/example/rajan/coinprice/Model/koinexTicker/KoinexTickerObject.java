package com.example.rajan.coinprice.Model.koinexTicker;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by rajan on 11/12/17.
 */

public class KoinexTickerObject {

    @SerializedName("prices")
    @Expose
    private Prices prices;
    @SerializedName("stats")
    @Expose
    private Stats stats;

    public Prices getPrices() {
        return prices;
    }

    public void setPrices(Prices prices) {
        this.prices = prices;
    }

    public Stats getStats() {
        return stats;
    }

    public void setStats(Stats stats) {
        this.stats = stats;
    }

    @Override
    public String toString() {
        return "KoinexTickerObject{" +
                "prices=" + prices +
                ", stats=" + stats +
                '}';
    }
}


