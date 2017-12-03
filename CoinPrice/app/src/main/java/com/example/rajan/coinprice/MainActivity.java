package com.example.rajan.coinprice;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import java.util.logging.Logger;

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
            if (s != null)
                resultTextView.setText(s);
            else
                resultTextView.setText("Error in request...");
        }
    }
}
