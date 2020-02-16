package com.example.weatherinstant;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;

public class DownloadTask2 extends AsyncTask<String, Void, String> {




    @Override
    protected String doInBackground(String... urls){
        String result = "";
        OkHttpGetPost okHttpGP= new OkHttpGetPost();
        try{
            result = okHttpGP.sendGet(urls[0]);
            return result;
        }
        catch(Exception e){
            System.out.println("");
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        try {
            JSONObject jsonObject = new JSONObject(result);

            JSONObject daily = new JSONObject(jsonObject.getString("daily"));
            JSONObject daily_data = daily.getJSONArray("data").getJSONObject(0);

            double high = Double.parseDouble(daily_data.getString("temperatureHigh"));
            double low = Double.parseDouble(daily_data.getString("temperatureLow"));

            MainActivity.pdh.setText(Math.round(high) + "º");
            MainActivity.pdl.setText(Math.round(low) + "º");

            JSONObject hourly = new JSONObject(jsonObject.getString("hourly"));
            int past_hour = MainActivity.pastDate_hour;

            JSONObject hourly_data = hourly.getJSONArray("data").getJSONObject(past_hour);

            double temperature = Double.parseDouble(hourly_data.getString("temperature"));
            MainActivity.pdt.setText(Math.round(temperature) + "º");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
