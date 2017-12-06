package com.example.rajan.coinprice;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
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

import com.example.rajan.coinprice.Model.Currency;
import com.example.rajan.coinprice.Model.Prices;
import com.example.rajan.coinprice.data.KoinexCurrentPricesContract;
import com.example.rajan.coinprice.data.KoinexCurrentPricesHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private final static String KOINEX_API_TICKER = "https://koinex.in/api/ticker";
    private static final String TAG = "MainActivity";
    private TextView mResultTextView;
    private RecyclerView mRecyclerView;
    private String[] mPriceData;
    private Button mRefreshButton;
    private Button mTestButton;
    private ProgressBar mLoadingIndicator;
    private SQLiteDatabase mKoinexdb;

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
                if (isNetworkAvailable())
                    new NetworkCall().execute(buildUrl());
                else
                    Toast.makeText(getApplicationContext(), "Unable to get data, No Internet Connection...", Toast.LENGTH_SHORT).show();
            }
        });

        mTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Prices> allPricesData = getAllPricesDB();
                Log.d(TAG, "onClick: Koinextable data");

                for (Prices price : allPricesData) {
                    Log.d(TAG, price.toString());
                }
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

        if (isNetworkAvailable())
            new NetworkCall().execute(buildUrl());
        else
            Toast.makeText(getApplicationContext(), "Unable to get data, No Internet Connection...", Toast.LENGTH_SHORT).show();


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

    private static URL buildUrl() {
        Uri koinexUri = Uri.parse(KOINEX_API_TICKER).buildUpon().build();

        try {
            URL koinexUrl = new URL(koinexUri.toString());
            Log.v(TAG, "URL: " + koinexUrl);
            return koinexUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
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

    private class NetworkCall extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL koinexURL;
            if (params.length == 0) {
                return null;
            }
            koinexURL = params[0];
            String result = null;
            try {
                result = getResponseFromHttpUrl(koinexURL);
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
                try {
                    JSONObject pricesObject = new JSONObject(s).getJSONObject("prices");
                    Iterator<?> keys = pricesObject.keys();
                    StringBuilder sb = new StringBuilder();
                    Prices currentValue = new Prices();
                    mPriceData = new String[pricesObject.length()];
                    int i = 0;
                    while (keys.hasNext()) {
                        String key = keys.next().toString();
                        Object valueObject = pricesObject.get(key);
                        Currency currency = Currency.fromString(key);
                        if (currency == null) {
                            continue;
                        }
                        switch (currency) {
                            case BITCOIN:
                                currentValue.setBtc(Double.valueOf(valueObject.toString()));
                                mPriceData[i] = "" + currency.name() + " (" + currency.getText() + ") : " + currentValue.getBtc();
                                break;
                            case BITCOINCASH:
                                currentValue.setBch(Double.valueOf(valueObject.toString()));
                                mPriceData[i] = "" + currency.name() + " (" + currency.getText() + ") : " + currentValue.getBch();
                                break;
                            case ETHERIUM:
                                currentValue.setEth(Double.valueOf(valueObject.toString()));
                                mPriceData[i] = "" + currency.name() + " (" + currency.getText() + ") : " + currentValue.getEth();
                                break;
                            case RIPPLE:
                                currentValue.setXrp(Double.valueOf(valueObject.toString()));
                                mPriceData[i] = "" + currency.name() + " (" + currency.getText() + ") : " + currentValue.getXrp();
                                break;
                            case LITECOIN:
                                currentValue.setLtc(Double.valueOf(valueObject.toString()));
                                mPriceData[i] = "" + currency.name() + " (" + currency.getText() + ") : " + currentValue.getLtc();
                                break;
                            case MIOTA:
                                currentValue.setMiota((Double) valueObject);
                                mPriceData[i] = "" + currency.name() + " (" + currency.getText() + ") : " + currentValue.getMiota();
                                break;
                            case OMG:
                                currentValue.setOmg((Double) valueObject);
                                mPriceData[i] = "" + currency.name() + " (" + currency.getText() + ") : " + currentValue.getOmg();
                                break;
                            case GNT:
                                currentValue.setGnt((Double) valueObject);
                                mPriceData[i] = "" + currency.name() + " (" + currency.getText() + ") : " + currentValue.getGnt();
                                break;
                        }
                        i++;
                    }
                    CurrencyPriceAdapter adapter = new CurrencyPriceAdapter();
                    adapter.setPriceData(mPriceData);
                    mRecyclerView.swapAdapter(adapter, true);
                    mResultTextView.setText(currentValue.toString());
                    addPricesDataDB(currentValue);
                    Toast.makeText(getApplicationContext(), "Request Successfull...", Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Some problem in Json parsing");
                    mPriceData = new String[1];
                    mPriceData[0] = "Empty Data";
                    CurrencyPriceAdapter adapter = new CurrencyPriceAdapter();
                    adapter.setPriceData(mPriceData);
                    mRecyclerView.swapAdapter(adapter, true);
                    Toast.makeText(getApplicationContext(), "Error in Parsing data...", Toast.LENGTH_SHORT).show();
                }
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
}
