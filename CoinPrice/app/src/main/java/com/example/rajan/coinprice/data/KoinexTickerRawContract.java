package com.example.rajan.coinprice.data;

import android.provider.BaseColumns;

/**
 * Created by rajan on 19/12/17.
 */

public final class KoinexTickerRawContract {
    public static class KoinexTickerRaw implements BaseColumns {
        public static final String TABLE_NAME = "koinex_ticker_raw";
        public static final String COLUMN_JSON_DATA = "json_data";
        public static final String COLUMN_TIMESTAMP = "timestamp";
    }
}
