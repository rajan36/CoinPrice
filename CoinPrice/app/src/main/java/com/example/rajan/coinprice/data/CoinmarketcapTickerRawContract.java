package com.example.rajan.coinprice.data;

import android.provider.BaseColumns;

/**
 * Created by rajan on 19/12/17.
 */

public class CoinmarketcapTickerRawContract {
    public static class CoinmarketcapTickerRaw implements BaseColumns {
        public static final String TABLE_NAME = "coinmarketcap_ticker_raw";
        public static final String COLUMN_JSON_DATA = "json_data";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
