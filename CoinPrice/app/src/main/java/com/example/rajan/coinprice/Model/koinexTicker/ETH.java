package com.example.rajan.coinprice.Model.koinexTicker;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ETH {

    @SerializedName("last_traded_price")
    @Expose
    private String lastTradedPrice;
    @SerializedName("lowest_ask")
    @Expose
    private String lowestAsk;
    @SerializedName("highest_bid")
    @Expose
    private String highestBid;
    @SerializedName("min_24hrs")
    @Expose
    private String min24hrs;
    @SerializedName("max_24hrs")
    @Expose
    private String max24hrs;
    @SerializedName("vol_24hrs")
    @Expose
    private String vol24hrs;

    public String getLastTradedPrice() {
        return lastTradedPrice;
    }

    public void setLastTradedPrice(String lastTradedPrice) {
        this.lastTradedPrice = lastTradedPrice;
    }

    public String getLowestAsk() {
        return lowestAsk;
    }

    public void setLowestAsk(String lowestAsk) {
        this.lowestAsk = lowestAsk;
    }

    public String getHighestBid() {
        return highestBid;
    }

    public void setHighestBid(String highestBid) {
        this.highestBid = highestBid;
    }

    public String getMin24hrs() {
        return min24hrs;
    }

    public void setMin24hrs(String min24hrs) {
        this.min24hrs = min24hrs;
    }

    public String getMax24hrs() {
        return max24hrs;
    }

    public void setMax24hrs(String max24hrs) {
        this.max24hrs = max24hrs;
    }

    public String getVol24hrs() {
        return vol24hrs;
    }

    public void setVol24hrs(String vol24hrs) {
        this.vol24hrs = vol24hrs;
    }

    @Override
    public String toString() {
        return "ETH{" +
                "lastTradedPrice='" + lastTradedPrice + '\'' +
                ", lowestAsk='" + lowestAsk + '\'' +
                ", highestBid='" + highestBid + '\'' +
                ", min24hrs='" + min24hrs + '\'' +
                ", max24hrs='" + max24hrs + '\'' +
                ", vol24hrs='" + vol24hrs + '\'' +
                '}';
    }
}
