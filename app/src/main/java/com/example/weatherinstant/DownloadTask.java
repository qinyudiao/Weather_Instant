package com.example.weatherinstant;

import android.Manifest;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class DownloadTask extends AsyncTask<String, Void, String> {




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
        MainActivity.t1.setText("t1_successful");
        try {
            JSONObject jsonObject = new JSONObject(result);

            JSONObject currently = new JSONObject(jsonObject.getString("currently"));


            double cur_temperature = Double.parseDouble(currently.getString("temperature"));
            double cur_humidity = Double.parseDouble(currently.getString("humidity"));
            double cur_windSpeed = Double.parseDouble(currently.getString("windSpeed"));
            double cur_precipProbability = Double.parseDouble(currently.getString("precipProbability"));

            MainActivity.t1_temperature.setText(String.valueOf(Math.round(cur_temperature)) + "ºF");
            MainActivity.t2_humidity.setText(String.valueOf(cur_humidity) + "%");
            MainActivity.t3_windSpeed.setText(String.valueOf(cur_windSpeed) + " mph");
            MainActivity.t4_precipProbability.setText(String.valueOf(cur_precipProbability) + "%");

            String cur_summary = currently.getString("summary");
            MainActivity.t8_summary.setText(cur_summary);

            double cur_visibility = Double.parseDouble(currently.getString("visibility"));
            double cur_pressure = Double.parseDouble(currently.getString("pressure"));
            double cur_precipitation = Double.parseDouble(currently.getString("precipIntensity"));

            MainActivity.t5_visibility.setText(String.valueOf(cur_visibility) + " mi");
            MainActivity.t6_pressure.setText(String.valueOf(cur_pressure) + " inHg");
            MainActivity.t7_precipitation.setText(String.valueOf(cur_precipitation) + " in");

            double cur_feelsLike = Double.parseDouble(currently.getString("apparentTemperature"));
            MainActivity.temp_feelsLike.setText("Right now, it feels like " + Math.round(cur_feelsLike) + "º");

            JSONObject hourly = new JSONObject(jsonObject.getString("hourly"));
            JSONArray hourly_data = hourly.getJSONArray("data");
            String[] hourly_temperatures = new String[24];
            String[] hourly_icons = new String[24];
            double sum_temperatures = 0;
            for(int i = 0; i<48; i++){
                if(i<24) {
                    Integer temperature = (int) Double.parseDouble(hourly_data.getJSONObject(i).getString("temperature"));
                    hourly_temperatures[i] = String.valueOf(temperature);
                    hourly_icons[i] = hourly_data.getJSONObject(i).getString("icon");
                }
                sum_temperatures += Double.parseDouble(hourly_data.getJSONObject(i).getString("temperature"));
            }
            int average_temperature_48 = (int)Math.round(sum_temperatures/48);

            MainActivity.h_1_24[0].setText(String.valueOf(Math.round(cur_temperature)) + "º");
            for(int i = 1; i<MainActivity.h_1_24.length; i++){
                MainActivity.h_1_24[i].setText(hourly_temperatures[i] + "º");
            }

            for(int i = 0; i<MainActivity.h_icon.length; i++) {
                switch (hourly_icons[i]) {
                    case "clear-day":
                        MainActivity.h_icon[i].setImageResource(R.drawable.sunny);
                        break;
                    case "clear-night":
                        MainActivity.h_icon[i].setImageResource(R.drawable.moon);
                        break;
                    case "cloudy":
                        MainActivity.h_icon[i].setImageResource(R.drawable.cloud);
                        break;
                    case "partly-cloudy-day":
                        MainActivity.h_icon[i].setImageResource(R.drawable.partly_cloudy_day);
                        break;
                    case "partly-cloudy-night":
                        MainActivity.h_icon[i].setImageResource(R.drawable.partly_cloudy_night);
                        break;
                    case "wind":
                        MainActivity.h_icon[i].setImageResource(R.drawable.wind);
                        break;
                    case "rain":
                        MainActivity.h_icon[i].setImageResource(R.drawable.rain);
                        break;
                    case "storm":
                        MainActivity.h_icon[i].setImageResource(R.drawable.storm);
                        break;
                    default:
                        MainActivity.h_icon[i].setImageResource(R.drawable.cloud);
                }
            }

            MainActivity.t10_averageTemperature.setText("The average temperature for the next 48 hours: " + (String.valueOf(average_temperature_48)) + "º");


            JSONObject daily = new JSONObject(jsonObject.getString("daily"));
            JSONArray daily_data = daily.getJSONArray("data");
            String[] daily_tempHigh = new String[7];
            String[] daily_tempLow = new String[7];
            for(int i = 0; i<7; i++){
                Integer temperature = (int) Double.parseDouble(daily_data.getJSONObject(i).getString("temperatureHigh"));
                daily_tempHigh[i] = String.valueOf(temperature);
                temperature = (int) Double.parseDouble(daily_data.getJSONObject(i).getString("temperatureLow"));
                daily_tempLow[i] = String.valueOf(temperature);
            }

            MainActivity.d1h.setText(daily_tempHigh[0] + "º");
            MainActivity.d2h.setText(daily_tempHigh[1] + "º");
            MainActivity.d3h.setText(daily_tempHigh[2] + "º");
            MainActivity.d4h.setText(daily_tempHigh[3] + "º");
            MainActivity.d5h.setText(daily_tempHigh[4] + "º");
            MainActivity.d6h.setText(daily_tempHigh[5] + "º");
            MainActivity.d7h.setText(daily_tempHigh[6] + "º");

            MainActivity.d1l.setText(daily_tempLow[0] + "º");
            MainActivity.d2l.setText(daily_tempLow[1] + "º");
            MainActivity.d3l.setText(daily_tempLow[2] + "º");
            MainActivity.d4l.setText(daily_tempLow[3] + "º");
            MainActivity.d5l.setText(daily_tempLow[4] + "º");
            MainActivity.d6l.setText(daily_tempLow[5] + "º");
            MainActivity.d7l.setText(daily_tempLow[6] + "º");


            long cur_time_long = System.currentTimeMillis()/1000;
            String cur_data_and_time = MainActivity.getDateCurrentTime(cur_time_long);
            String cur_time = cur_data_and_time.split(" ", 2)[1];

            String cur_hour = cur_time.substring(0,2);

            String [] weekdays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            int dayOfWeek_today = MainActivity.getCurrentDayOfWeek(cur_time_long);

            MainActivity.d1w.setText(weekdays[dayOfWeek_today-1]);
            MainActivity.d2w.setText(weekdays[dayOfWeek_today]);
            MainActivity.d3w.setText(weekdays[dayOfWeek_today+1]);
            MainActivity.d4w.setText(weekdays[dayOfWeek_today+2]);
            MainActivity.d5w.setText(weekdays[dayOfWeek_today+3]);
            MainActivity.d6w.setText(weekdays[dayOfWeek_today+4]);
            MainActivity.d7w.setText(weekdays[dayOfWeek_today+5]);

            MainActivity.h1t.setText("Now");
            for(int i = 1; i < MainActivity.h_time.length; i++) {
                int temp_hour = Integer.parseInt(cur_hour) + i;
                if(temp_hour >= 24)
                    temp_hour -= 24;
                if (temp_hour == 0) {
                    MainActivity.h_time[i].setText("12am");
                } else if (temp_hour < 12) {
                    MainActivity.h_time[i].setText(temp_hour + "am");
                } else if (temp_hour > 12) {
                    MainActivity.h_time[i].setText((temp_hour - 12) + "pm");
                } else {
                    MainActivity.h_time[i].setText("12pm");
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
