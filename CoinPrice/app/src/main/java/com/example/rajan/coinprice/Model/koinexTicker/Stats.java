package com.example.rajan.coinprice.Model.koinexTicker;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Stats {

    @SerializedName("ETH")
    @Expose
    private ETH eTH;
    @SerializedName("BTC")
    @Expose
    private BTC bTC;
    @SerializedName("LTC")
    @Expose
    private LTC lTC;
    @SerializedName("XRP")
    @Expose
    private XRP xRP;
    @SerializedName("BCH")
    @Expose
    private BCH bCH;

    public ETH getETH() {
        return eTH;
    }

    public void setETH(ETH eTH) {
        this.eTH = eTH;
    }

    public BTC getBTC() {
        return bTC;
    }

    public void setBTC(BTC bTC) {
        this.bTC = bTC;
    }

    public LTC getLTC() {
        return lTC;
    }

    public void setLTC(LTC lTC) {
        this.lTC = lTC;
    }

    public XRP getXRP() {
        return xRP;
    }

    public void setXRP(XRP xRP) {
        this.xRP = xRP;
    }

    public BCH getBCH() {
        return bCH;
    }

    public void setBCH(BCH bCH) {
        this.bCH = bCH;
    }

    @Override
    public String toString() {
        return "Stats{" +
                "eTH=" + eTH +
                ", bTC=" + bTC +
                ", lTC=" + lTC +
                ", xRP=" + xRP +
                ", bCH=" + bCH +
                '}';
    }
}
