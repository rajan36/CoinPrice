package com.example.rajan.coinprice.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by rajan on 5/12/17.
 */

public class KoinexCurrentPricesHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "koinex.db";
    private static final Integer DB_VERSION = 1;
    private static final String TAG = "KoinexCurrentPricesHelper";

    public KoinexCurrentPricesHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String TABLE_CREATE_STATEMENT = "CREATE TABLE " + KoinexCurrentPricesContract.KoinexCurrentPrices.TABLE_NAME + "( " + KoinexCurrentPricesContract.KoinexCurrentPrices._ID + " INTEGER PRIMARY KEY AUTOINCREMENT   , " +
                KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_BITCOIN + " DOUBLE NOT NULL , " +
                KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_ETHEREUM + " DOUBLE NOT NULL , " +
                KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_BITCOIN_CASH + " DOUBLE NOT NULL , " +
                KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_RIPPLE + " DOUBLE NOT NULL , " +
                KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_LITECOIN + " DOUBLE NOT NULL , " +
                KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_IOTA + " DOUBLE NOT NULL , " +
                KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_GNT + " DOUBLE NOT NULL , " +
                KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_OMG + " DOUBLE NOT NULL , " +
                KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP ) ";
        db.execSQL(TABLE_CREATE_STATEMENT);
        Log.d(TAG, "onCreate: Create table " + KoinexCurrentPricesContract.KoinexCurrentPrices.TABLE_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
