package com.example.rajan.coinprice.data;

import android.provider.BaseColumns;

/**
 * Created by rajan on 5/12/17.
 */

public class KoinexCurrentPricesContract {

    private KoinexCurrentPricesContract() {

    }

    public static class KoinexCurrentPrices implements BaseColumns {
        public static final String TABLE_NAME = "koinex_current_prices";
        public static final String COLUMN_NAME_BITCOIN = "bitcoin";
        public static final String COLUMN_NAME_ETHEREUM = "ethereum";
        public static final String COLUMN_NAME_BITCOIN_CASH = "bitcoin_cash";
        public static final String COLUMN_NAME_RIPPLE = "ripple";
        public static final String COLUMN_NAME_LITECOIN = "litecoin";
        public static final String COLUMN_NAME_IOTA = "iota";
        public static final String COLUMN_NAME_GNT = "gnt";
        public static final String COLUMN_NAME_OMG = "omg";
        public static final String COLUMN_NAME_TIMESTAMP = "timestamp";
    }
}


