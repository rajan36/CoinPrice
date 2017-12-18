package com.example.rajan.coinprice;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.rajan.coinprice.Model.CoinMarketCapObject;
import com.example.rajan.coinprice.Model.Currency;
import com.example.rajan.coinprice.Model.Prices;
import com.example.rajan.coinprice.Model.koinexTicker.KoinexTickerObject;
import com.example.rajan.coinprice.data.KoinexCurrentPricesContract;
import com.example.rajan.coinprice.data.KoinexCurrentPricesHelper;
import com.example.rajan.coinprice.network.MySingleton;
import com.example.rajan.coinprice.network.NetworkCallIntentService;
import com.example.rajan.coinprice.utilities.JobSchedulerUtils;
import com.example.rajan.coinprice.utilities.NotificationUtils;
import com.example.rajan.coinprice.utilities.PreferenceUtilities;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private final static String KOINEX_API_TICKER = "https://koinex.in/api/ticker";
    private final static String COINMARKETCAP_API_TICKER = "https://api.coinmarketcap.com/v1/ticker/?convert=INR&limit=6";
    private static final String TAG = "MainActivity";
    private TextView mResultTextView;
    private RecyclerView mRecyclerView;
    private String[] mPriceData;
    private Button mRefreshButton;
    private Button mTestButton;
    private ProgressBar mLoadingIndicator;
    private SQLiteDatabase mKoinexdb;
    private final Gson gson = new Gson();

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mResultTextView = (TextView) findViewById(R.id.result_textview);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_coinprice);
        mRefreshButton = (Button) findViewById(R.id.refresh_action);
        mTestButton = (Button) findViewById(R.id.test_action);
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);


        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationUtils.priceAlert(getApplicationContext());
                if (isNetworkAvailable()) {
                    showLoading();
                    Intent service = new Intent(getApplicationContext(), NetworkCallIntentService.class);
                    service.setAction(NetworkCallTask.ACTION_TRIGGER_API_CALL);
                    startService(service);
                    Log.d(TAG, "onClick: service started");
//                    volleyCall();
                } else {
                    mPriceData = new String[1];
                    mPriceData[0] = "Empty Data";
                    CurrencyPriceAdapter adapter = new CurrencyPriceAdapter();
                    adapter.setPriceData(mPriceData);
                    mRecyclerView.swapAdapter(adapter, true);
                    Toast.makeText(getApplicationContext(), "Unable to get data, No Internet Connection...", Toast.LENGTH_SHORT).show();
                }
            }
        });


        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(getApplicationContext(), NetworkCallIntentService.class);
                service.setAction(NetworkCallTask.ACTION_TRIGGER_API_CALL);
                startService(service);
                Log.d(TAG, "onClick: service started");
            }
        });

        mPriceData = new String[1];
        mPriceData[0] = "Empty Data";

        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        CurrencyPriceAdapter adapter = new CurrencyPriceAdapter();

        adapter.setPriceData(mPriceData);

        mRecyclerView.setAdapter(adapter);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(false);

        KoinexCurrentPricesHelper dbHelper = new KoinexCurrentPricesHelper(this);
        mKoinexdb = dbHelper.getWritableDatabase();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        JobSchedulerUtils.scheduleNetworkCall(this);
    }

    private ArrayList<Prices> getAllPricesDB() {
        Cursor cursor = mKoinexdb.query(KoinexCurrentPricesContract.KoinexCurrentPrices.TABLE_NAME, null, null, null, null, null, KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_TIMESTAMP);
        ArrayList<Prices> allPricesData = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                Prices curVal = new Prices();
                curVal.setBtc(cursor.getDouble(cursor.getColumnIndex(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_BITCOIN)));
                curVal.setEth(cursor.getDouble(cursor.getColumnIndex(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_ETHEREUM)));
                curVal.setBch(cursor.getDouble(cursor.getColumnIndex(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_BITCOIN_CASH)));
                curVal.setXrp(cursor.getDouble(cursor.getColumnIndex(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_RIPPLE)));
                curVal.setLtc(cursor.getDouble(cursor.getColumnIndex(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_LITECOIN)));
                curVal.setMiota(cursor.getDouble(cursor.getColumnIndex(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_IOTA)));
                curVal.setGnt(cursor.getDouble(cursor.getColumnIndex(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_GNT)));
                curVal.setOmg(cursor.getDouble(cursor.getColumnIndex(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_OMG)));
                curVal.setId(cursor.getInt(cursor.getColumnIndex(KoinexCurrentPricesContract.KoinexCurrentPrices._ID)));
                curVal.setTimestamp(cursor.getString(cursor.getColumnIndex(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_TIMESTAMP)));
                Log.d(TAG, curVal.toString());
                allPricesData.add(curVal);

            } while (cursor.moveToNext());
        }

        return allPricesData;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(PreferenceUtilities.KEY_SUCCESSFUL_REQUEST_COUNT)) {
            if (PreferenceUtilities.isCoinMarketCapJsonSet(this) && PreferenceUtilities.isKoinexJsonSet(this)) {
                String koinexJson = PreferenceUtilities.getKoinexJson(this);
                String coinMarketCapJson = PreferenceUtilities.getCoinMarketCapJson(this);
                Log.d(TAG, "onSharedPreferenceChanged: " + koinexJson + "\n" + coinMarketCapJson);
                generateUI(koinexJson, coinMarketCapJson);

            } else {
                Log.d(TAG, "onSharedPreferenceChanged: key is " + key);
                mPriceData = new String[1];
                mPriceData[0] = "Empty Data";
                CurrencyPriceAdapter adapter = new CurrencyPriceAdapter();
                adapter.setPriceData(mPriceData);
                mRecyclerView.swapAdapter(adapter, true);
//                hideLoading();
            }
            hideLoading();
        } else if (key.equals(PreferenceUtilities.KEY_FAILURE_REQUEST_COUNT)) {
            Toast.makeText(getApplicationContext(), key + " api request Failed", Toast.LENGTH_SHORT).show();

            mPriceData = new String[1];
            mPriceData[0] = "Empty Data";
            CurrencyPriceAdapter adapter = new CurrencyPriceAdapter();
            adapter.setPriceData(mPriceData);
            mRecyclerView.swapAdapter(adapter, true);
            hideLoading();

        }
    }

    private class NetworkCall extends AsyncTask<URL, String, String> {

        private String urlInCall;

        @Override
        protected String doInBackground(URL... params) {
            URL url;
            if (params.length == 0) {
                return null;
            }
            url = params[0];
            urlInCall = url.toString();
            String result = null;
            try {
                result = getResponseFromHttpUrl(url);
                Log.d(TAG, "Response from the api is " + result);
            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG, "Response from the api is null");
            }

            return result;
        }

        @Override
        protected void onPreExecute() {
            showLoading();
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String s) {
            if (s != null) {

            } else {
                mPriceData = new String[1];
                mPriceData[0] = "Empty Data";
                CurrencyPriceAdapter adapter = new CurrencyPriceAdapter();
                adapter.setPriceData(mPriceData);
                mRecyclerView.swapAdapter(adapter, true);
                Toast.makeText(getApplicationContext(), "Error in request...", Toast.LENGTH_SHORT).show();
                mResultTextView.setText("Error in request...");
            }
            hideLoading();
        }


    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRefreshButton.setEnabled(false);
    }

    private void hideLoading() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRefreshButton.setEnabled(true);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private boolean addPricesDataDB(Prices currentPrice) {
        ContentValues cv = new ContentValues();
        cv.put(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_BITCOIN, currentPrice.getBtc());
        cv.put(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_ETHEREUM, currentPrice.getEth());
        cv.put(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_BITCOIN_CASH, currentPrice.getBch());
        cv.put(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_RIPPLE, currentPrice.getXrp());
        cv.put(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_LITECOIN, currentPrice.getLtc());
        cv.put(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_IOTA, currentPrice.getMiota());
        cv.put(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_GNT, currentPrice.getGnt());
        cv.put(KoinexCurrentPricesContract.KoinexCurrentPrices.COLUMN_NAME_OMG, currentPrice.getOmg());
        return mKoinexdb.insert(KoinexCurrentPricesContract.KoinexCurrentPrices.TABLE_NAME, null, cv) > 0;
    }

    private void generateUI(KoinexTickerObject koinexTickerObject, ArrayList<CoinMarketCapObject> coinMarketCapObjects) {
        mPriceData = new String[6];
        mPriceData[0] = "" + Currency.BITCOIN.name() + "(" + Currency.BITCOIN.getText() + ") : " + koinexTickerObject.getPrices().getBTC() + "  ;  " + coinMarketCapObjects.get(0).getPriceInr();
        mPriceData[1] = "" + Currency.ETHERIUM.name() + "(" + Currency.ETHERIUM.getText() + ") : " + koinexTickerObject.getPrices().getETH() + "  ;  " + coinMarketCapObjects.get(1).getPriceInr();
        mPriceData[2] = "" + Currency.BITCOINCASH.name() + "(" + Currency.BITCOINCASH.getText() + ") : " + koinexTickerObject.getPrices().getBCH() + "  ;  " + coinMarketCapObjects.get(2).getPriceInr();
        mPriceData[3] = "" + Currency.MIOTA.name() + "(" + Currency.MIOTA.getText() + ") : " + koinexTickerObject.getPrices().getMIOTA() + "  ;  " + coinMarketCapObjects.get(3).getPriceInr();
        mPriceData[4] = "" + Currency.RIPPLE.name() + "(" + Currency.RIPPLE.getText() + ") : " + koinexTickerObject.getPrices().getXRP() + "  ;  " + coinMarketCapObjects.get(4).getPriceInr();
        mPriceData[5] = "" + Currency.LITECOIN.name() + "(" + Currency.LITECOIN.getText() + ") : " + koinexTickerObject.getPrices().getLTC() + "  ;  " + coinMarketCapObjects.get(5).getPriceInr();
        Log.d(TAG, "generateUI: Hua kuch");
        CurrencyPriceAdapter adapter = new CurrencyPriceAdapter();
        adapter.setPriceData(mPriceData);
        mRecyclerView.swapAdapter(adapter, true);
    }

    private ArrayList<CoinMarketCapObject> getCoinMarketCapObjects(String json) {
        ArrayList<CoinMarketCapObject> coinMarketCapObjects = new ArrayList<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                CoinMarketCapObject object = gson.fromJson(jsonArray.get(i).toString(), CoinMarketCapObject.class);
                Log.d(TAG, "Response is: " + object.toString());
                coinMarketCapObjects.add(object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return coinMarketCapObjects;
    }

    private KoinexTickerObject getKoinexTickerObject(String json) {
        KoinexTickerObject koinexTickerObject = gson.fromJson(json, KoinexTickerObject.class);
        Log.d(TAG, "Response is: " + koinexTickerObject.toString());
        return koinexTickerObject;
    }

    private void generateUI(String koinexJson, String coinMarketCapJson) {
        KoinexTickerObject koinexTickerObject = getKoinexTickerObject(koinexJson);
        ArrayList<CoinMarketCapObject> coinMarketCapObjects = getCoinMarketCapObjects(coinMarketCapJson);
        mPriceData = new String[6];
        mPriceData[0] = "" + Currency.BITCOIN.name() + "(" + Currency.BITCOIN.getText() + ") : " + koinexTickerObject.getPrices().getBTC() + "  ;  " + coinMarketCapObjects.get(0).getPriceInr();
        mPriceData[1] = "" + Currency.ETHERIUM.name() + "(" + Currency.ETHERIUM.getText() + ") : " + koinexTickerObject.getPrices().getETH() + "  ;  " + coinMarketCapObjects.get(1).getPriceInr();
        mPriceData[2] = "" + Currency.BITCOINCASH.name() + "(" + Currency.BITCOINCASH.getText() + ") : " + koinexTickerObject.getPrices().getBCH() + "  ;  " + coinMarketCapObjects.get(2).getPriceInr();
        mPriceData[3] = "" + Currency.MIOTA.name() + "(" + Currency.MIOTA.getText() + ") : " + koinexTickerObject.getPrices().getMIOTA() + "  ;  " + coinMarketCapObjects.get(3).getPriceInr();
        mPriceData[4] = "" + Currency.RIPPLE.name() + "(" + Currency.RIPPLE.getText() + ") : " + koinexTickerObject.getPrices().getXRP() + "  ;  " + coinMarketCapObjects.get(4).getPriceInr();
        mPriceData[5] = "" + Currency.LITECOIN.name() + "(" + Currency.LITECOIN.getText() + ") : " + koinexTickerObject.getPrices().getLTC() + "  ;  " + coinMarketCapObjects.get(5).getPriceInr();
        Log.d(TAG, "generateUI: Hua kuch");
        CurrencyPriceAdapter adapter = new CurrencyPriceAdapter();
        adapter.setPriceData(mPriceData);
        mRecyclerView.swapAdapter(adapter, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

}
