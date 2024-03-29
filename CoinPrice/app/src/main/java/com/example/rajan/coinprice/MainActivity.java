package com.example.rajan.coinprice;

import android.app.ActivityManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.rajan.coinprice.Model.CoinMarketCapObject;
import com.example.rajan.coinprice.Model.Currency;
import com.example.rajan.coinprice.Model.Prices;
import com.example.rajan.coinprice.Model.koinexTicker.KoinexTickerObject;
import com.example.rajan.coinprice.data.CoinPriceDbHelper;
import com.example.rajan.coinprice.data.KoinexCurrentPricesContract;
import com.example.rajan.coinprice.network.NetworkCallIntentService;
import com.example.rajan.coinprice.utilities.DbUtils;
import com.example.rajan.coinprice.utilities.JobSchedulerUtils;
import com.example.rajan.coinprice.utilities.NotificationUtils;
import com.example.rajan.coinprice.utilities.PreferenceUtilities;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

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
    private MenuItem mRefreshItem;
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
        mRefreshItem = (MenuItem) findViewById(R.id.action_refresh);

        mRefreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationUtils.dummyNotification(getApplicationContext());
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

        JobSchedulerUtils.scheduleNetworkCallCustomInterval(this);
        DbUtils.getAllKoinexTickerRaw(this);
        DbUtils.getAllCoinMarketcapTickerRaw(this);
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: Preference Change Listener Regisetered");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Preference Change Listener Unregistered");
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        preferences.unregisterOnSharedPreferenceChangeListener(this);
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

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
        mRefreshButton.setEnabled(false);
        mRefreshItem.setEnabled(false);
    }

    private void hideLoading() {
        mRecyclerView.setVisibility(View.VISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRefreshButton.setEnabled(true);
        mRefreshItem.setEnabled(true);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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

    private TreeMap<String, CoinMarketCapObject> getCoinMarketCapObjects(String json) {
        ArrayList<CoinMarketCapObject> coinMarketCapObjects = new ArrayList<>();
        TreeMap<String, CoinMarketCapObject> mapCoinMarketCapObjects = new TreeMap<>();
        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(json);

            for (int i = 0; i < jsonArray.length(); i++) {
                CoinMarketCapObject object = gson.fromJson(jsonArray.get(i).toString(), CoinMarketCapObject.class);
                Log.d(TAG, "Response is: " + object.toString());
                coinMarketCapObjects.add(object);
                mapCoinMarketCapObjects.put(object.getSymbol(), object);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mapCoinMarketCapObjects;
    }

    private KoinexTickerObject getKoinexTickerObject(String json) {
        KoinexTickerObject koinexTickerObject = gson.fromJson(json, KoinexTickerObject.class);
        Log.d(TAG, "Response is: " + koinexTickerObject.toString());
        return koinexTickerObject;
    }

    private void generateUI(String koinexJson, String coinMarketCapJson) {
        KoinexTickerObject koinexTickerObject = getKoinexTickerObject(koinexJson);
        Map<String, CoinMarketCapObject> coinMarketCapObjects = getCoinMarketCapObjects(coinMarketCapJson);
        mPriceData = new String[6];
        mPriceData[0] = "" + Currency.BITCOIN.name() + "(" + Currency.BITCOIN.getText() + ") : " + koinexTickerObject.getPrices().getBTC() + "  ;  " + coinMarketCapObjects.get(Currency.BITCOIN.getText()).getPriceInr();
        mPriceData[1] = "" + Currency.ETHERIUM.name() + "(" + Currency.ETHERIUM.getText() + ") : " + koinexTickerObject.getPrices().getETH() + "  ;  " + coinMarketCapObjects.get(Currency.ETHERIUM.getText()).getPriceInr();
        mPriceData[2] = "" + Currency.BITCOINCASH.name() + "(" + Currency.BITCOINCASH.getText() + ") : " + koinexTickerObject.getPrices().getBCH() + "  ;  " + coinMarketCapObjects.get(Currency.BITCOINCASH.getText()).getPriceInr();
        mPriceData[3] = "" + Currency.MIOTA.name() + "(" + Currency.MIOTA.getText() + ") : " + koinexTickerObject.getPrices().getMIOTA() + "  ;  " + coinMarketCapObjects.get(Currency.MIOTA.getText()).getPriceInr();
        mPriceData[4] = "" + Currency.RIPPLE.name() + "(" + Currency.RIPPLE.getText() + ") : " + koinexTickerObject.getPrices().getXRP() + "  ;  " + coinMarketCapObjects.get(Currency.RIPPLE.getText()).getPriceInr();
        mPriceData[5] = "" + Currency.LITECOIN.name() + "(" + Currency.LITECOIN.getText() + ") : " + koinexTickerObject.getPrices().getLTC() + "  ;  " + coinMarketCapObjects.get(Currency.LITECOIN.getText()).getPriceInr();
        Log.d(TAG, "generateUI: Hua kuch");
        CurrencyPriceAdapter adapter = new CurrencyPriceAdapter();
        adapter.setPriceData(mPriceData);
        mRecyclerView.swapAdapter(adapter, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.coinprice_menu, menu);
        mRefreshItem = menu.findItem(R.id.action_refresh);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            if (!isMyServiceRunning(NetworkCallIntentService.class)) {
                if (isNetworkAvailable()) {
                    showLoading();
                    Intent service = new Intent(getApplicationContext(), NetworkCallIntentService.class);
                    service.setAction(NetworkCallTask.ACTION_TRIGGER_API_CALL);
                    startService(service);
                    item.setEnabled(false);
                    Log.d(TAG, "Fetching ticker details...");
                } else {
                    mPriceData = new String[1];
                    mPriceData[0] = "Empty Data";
                    CurrencyPriceAdapter adapter = new CurrencyPriceAdapter();
                    adapter.setPriceData(mPriceData);
                    mRecyclerView.swapAdapter(adapter, true);
                    Toast.makeText(getApplicationContext(), "Unable to get data, No Internet Connection...", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d(TAG, "Request is processing...");
            }
            return true;
        } else if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
