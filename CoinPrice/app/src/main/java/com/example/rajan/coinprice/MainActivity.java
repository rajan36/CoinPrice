package com.example.rajan.coinprice;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.rajan.coinprice.Model.Currency;
import com.example.rajan.coinprice.Model.Prices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Scanner;

public class MainActivity extends AppCompatActivity {

    private final static String KOINEX_API_TICKER = "https://koinex.in/api/ticker";
    private static final String TAG = "MainActivity";
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        resultTextView = (TextView) findViewById(R.id.result_textview);
        new NetworkCall().execute(buildUrl());
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

    public class NetworkCall extends AsyncTask<URL, String, String> {

        @Override
        protected String doInBackground(URL... params) {
            URL koinexURL = null;
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
        protected void onPostExecute(String s) {
            if (s != null) {
                try {
                    JSONObject pricesObject = new JSONObject(s).getJSONObject("prices");
                    Iterator<?> keys = pricesObject.keys();
                    StringBuilder sb = new StringBuilder();
                    Prices currentValue = new Prices();
                    while (keys.hasNext()) {
                        String key = keys.next().toString();
                        Object valueObject = pricesObject.get(key);
                        Currency currency = Currency.fromString(key);
                        switch (currency) {
                            case BITCOIN:
                                currentValue.setBtc(new Double(valueObject.toString()));
                                break;
                            case BITCOINCASH:
                                currentValue.setBch(new Double(valueObject.toString()));
                                break;
                            case ETHERIUM:
                                currentValue.setEth(new Double(valueObject.toString()));
                                break;
                            case RIPPLE:
                                currentValue.setXrp(new Double(valueObject.toString()));
                                break;
                            case LITECOIN:
                                currentValue.setLtc(new Double(valueObject.toString()));
                                break;
                            case MIOTA:
                                currentValue.setMiota((Double) valueObject);
                                break;
                            case OMG:
                                currentValue.setOmg((Double) valueObject);
                                break;
                            case GNT:
                                currentValue.setGnt((Double) valueObject);
                                break;
                        }
                    }
                    resultTextView.setText(currentValue.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "Some problem in Json parsing");
                }
            } else
                resultTextView.setText("Error in request...");
        }
    }
}
