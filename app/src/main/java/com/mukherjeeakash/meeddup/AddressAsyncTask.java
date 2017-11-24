package com.mukherjeeakash.meeddup;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Created by akash on 11/22/2017.
 */

public class AddressAsyncTask extends AsyncTask<String, Integer, List<Double>> {
    public AddressAsyncTask() {
        super();
    }

    @Override
    protected List<Double> doInBackground(String... urls) {
        try {
            URL url = new URL(urls[0]);
            URLConnection connection = url.openConnection();
            connection.connect();

            Log.d("Debug", "working");
            InputStream inputStream = connection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,
                    Charset.forName("UTF-8"));

            Gson gson = new Gson();
            AddressResults addresses = gson.fromJson(inputStreamReader, AddressResults.class);
            return addresses.getResults()[0].getGeometry().getLocation().getCoordinates();
        } catch (Exception e) {
            return null;
        }
    }
}
