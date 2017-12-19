package com.example.rajan.coinprice.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by rajan on 5/12/17.
 */

public class CoinPriceDbHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "coinprice.db";
    private static final Integer DB_VERSION = 1;
    private static final String TAG = "CoinPriceDbHelper";

    public CoinPriceDbHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String KOINEX_TICKER_RAW_TABLE_CREATE_STATEMENT = "CREATE TABLE " + KoinexTickerRawContract.KoinexTickerRaw.TABLE_NAME + "( " + KoinexTickerRawContract.KoinexTickerRaw._ID + " INTEGER PRIMARY KEY, " + KoinexTickerRawContract.KoinexTickerRaw.COLUMN_JSON_DATA + " TEXT ," + KoinexTickerRawContract.KoinexTickerRaw.COLUMN_TIMESTAMP + " DEFAULT CURRENT_TIMESTAMP );";
        final String COINMARKETCAP_TICKER_RAW_TABLE_CREATE_STATEMENT = "CREATE TABLE " + CoinmarketcapTickerRawContract.CoinmarketcapTickerRaw.TABLE_NAME + "( " + CoinmarketcapTickerRawContract.CoinmarketcapTickerRaw._ID + " INTEGER PRIMARY KEY, " + CoinmarketcapTickerRawContract.CoinmarketcapTickerRaw.COLUMN_JSON_DATA + " TEXT ," + CoinmarketcapTickerRawContract.CoinmarketcapTickerRaw.COLUMN_TIMESTAMP + " DEFAULT CURRENT_TIMESTAMP );";

        db.execSQL(KOINEX_TICKER_RAW_TABLE_CREATE_STATEMENT);
        db.execSQL(COINMARKETCAP_TICKER_RAW_TABLE_CREATE_STATEMENT);
        Log.d(TAG, "onCreate: Both Tables Created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade: Do Nothing for now");
    }
}
