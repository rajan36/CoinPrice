package com.example.rajan.coinprice.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.rajan.coinprice.data.CoinPriceDbHelper;
import com.example.rajan.coinprice.data.CoinmarketcapTickerRawContract;
import com.example.rajan.coinprice.data.KoinexCurrentPricesContract;
import com.example.rajan.coinprice.data.KoinexTickerRawContract;

/**
 * Created by rajan on 19/12/17.
 */

public final class DbUtils {

    private static final String TAG = "DbUtils";
    private static SQLiteDatabase mCoinPriceDb;

    private static SQLiteDatabase getDbInstance(Context context) {
        if (mCoinPriceDb == null) {
            CoinPriceDbHelper dbHelper = new CoinPriceDbHelper(context);
            return dbHelper.getWritableDatabase();
        } else
            return mCoinPriceDb;
    }

    public static boolean insertKoinexTickerRaw(Context context, String json_data) {
        mCoinPriceDb = getDbInstance(context);
        ContentValues cv = new ContentValues();
        cv.put(KoinexTickerRawContract.KoinexTickerRaw.COLUMN_JSON_DATA, json_data);
        Log.d(TAG, "insertKoinexTickerRaw: Inserting in " + KoinexTickerRawContract.KoinexTickerRaw.TABLE_NAME);
        return mCoinPriceDb.insert(KoinexTickerRawContract.KoinexTickerRaw.TABLE_NAME, null, cv) > 0;
    }

    public static boolean insertCoinmarketcapTickerRaw(Context context, String json_data) {
        mCoinPriceDb = getDbInstance(context);
        ContentValues cv = new ContentValues();
        cv.put(CoinmarketcapTickerRawContract.CoinmarketcapTickerRaw.COLUMN_JSON_DATA, json_data);
        Log.d(TAG, "insertCoinmarketcapTickerRaw: Inserting in " + CoinmarketcapTickerRawContract.CoinmarketcapTickerRaw.TABLE_NAME);
        return mCoinPriceDb.insert(CoinmarketcapTickerRawContract.CoinmarketcapTickerRaw.TABLE_NAME, null, cv) > 0;
    }

    public static void getAllKoinexTickerRaw(Context context) {
        mCoinPriceDb = getDbInstance(context);
        Cursor cursor = mCoinPriceDb.query(
                KoinexTickerRawContract.KoinexTickerRaw.TABLE_NAME, null, null, null, null, null, KoinexTickerRawContract.KoinexTickerRaw.COLUMN_TIMESTAMP + " DESC");
        Log.d(TAG, "getAllKoinexTickerRaw: In Function");
        if (cursor.moveToFirst()) {
            do {
                Log.d(TAG, "getAllKoinexTickerRaw: " + cursor.getInt(cursor.getColumnIndex(KoinexTickerRawContract.KoinexTickerRaw._ID)) + " , " + cursor.getString(cursor.getColumnIndex(KoinexTickerRawContract.KoinexTickerRaw.COLUMN_TIMESTAMP)));
            } while (cursor.moveToNext());
        }

    }

    public static void getAllCoinMarketcapTickerRaw(Context context) {
        mCoinPriceDb = getDbInstance(context);
        Cursor cursor = mCoinPriceDb.query(
                CoinmarketcapTickerRawContract.CoinmarketcapTickerRaw.TABLE_NAME, null, null, null, null, null, CoinmarketcapTickerRawContract.CoinmarketcapTickerRaw.COLUMN_TIMESTAMP + " DESC");
        Log.d(TAG, "getAllCoinMarketcapTickerRaw: In Function");
        if (cursor.moveToFirst()) {
            do {
                Log.d(TAG, "getAllCoinMarketcapTickerRaw: " + cursor.getInt(cursor.getColumnIndex(CoinmarketcapTickerRawContract.CoinmarketcapTickerRaw._ID)) + " , " + cursor.getString(cursor.getColumnIndex(CoinmarketcapTickerRawContract.CoinmarketcapTickerRaw.COLUMN_TIMESTAMP)));
            } while (cursor.moveToNext());
        }
    }
}