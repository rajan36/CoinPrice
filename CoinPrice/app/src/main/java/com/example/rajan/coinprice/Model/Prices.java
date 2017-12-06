package com.example.rajan.coinprice.Model;

/**
 * Created by rajan on 4/12/17.
 */

public class Prices {

    private Integer id;

    private Double btc;

    private Double bch;

    private Double eth;

    private Double xrp;

    private Double ltc;

    private Double miota;

    private Double omg;

    private Double gnt;

    private String timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getBtc() {
        return btc;
    }

    public void setBtc(Double btc) {
        this.btc = btc;
    }

    public Double getBch() {
        return bch;
    }

    public void setBch(Double bch) {
        this.bch = bch;
    }

    public Double getEth() {
        return eth;
    }

    public void setEth(Double eth) {
        this.eth = eth;
    }

    public Double getXrp() {
        return xrp;
    }

    public void setXrp(Double xrp) {
        this.xrp = xrp;
    }

    public Double getLtc() {
        return ltc;
    }

    public void setLtc(Double ltc) {
        this.ltc = ltc;
    }

    public Double getMiota() {
        return miota;
    }

    public void setMiota(Double miota) {
        this.miota = miota;
    }

    public Double getOmg() {
        return omg;
    }

    public void setOmg(Double omg) {
        this.omg = omg;
    }

    public Double getGnt() {
        return gnt;
    }

    public void setGnt(Double gnt) {
        this.gnt = gnt;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Prices{" +
                "id=" + id +
                ", btc=" + btc +
                ", bch=" + bch +
                ", eth=" + eth +
                ", xrp=" + xrp +
                ", ltc=" + ltc +
                ", miota=" + miota +
                ", omg=" + omg +
                ", gnt=" + gnt +
                ", timestamp='" + timestamp + '\'' +
                '}';
    }
}
