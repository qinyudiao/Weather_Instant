package com.example.weatherinstant;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.security.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {


    static TextView t1_temperature, t2_humidity, t3_windSpeed, t4_precipProbability,
            t5_visibility, t6_pressure, t7_precipitation, t8_summary,
            t10_averageTemperature,
            d1h, d1l, d2h, d2l, d3h, d3l, d4h, d4l, d5h, d5l, d6h, d6l, d7h, d7l, d1w, d2w,d3w, d4w, d5w, d6w, d7w,
            temp_feelsLike, t1,
            t_cityName, pdh, pdl, pdt;

    static TextView h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, h11, h12, h13, h14, h15, h16, h17, h18, h19, h20, h21, h22, h23, h24;
    static TextView[] h_1_24 = {h1, h2, h3, h4, h5, h6, h7, h8, h9, h10, h11, h12, h13, h14, h15, h16, h17, h18, h19, h20, h21, h22, h23, h24};

    static ImageView h1i, h2i, h3i, h4i, h5i, h6i, h7i, h8i, h9i, h10i, h11i, h12i, h13i, h14i, h15i, h16i, h17i, h18i, h19i, h20i, h21i, h22i, h23i, h24i;
    static ImageView[] h_icon = {h1i, h2i, h3i, h4i, h5i, h6i, h7i, h8i, h9i, h10i, h11i, h12i, h13i, h14i, h15i, h16i, h17i, h18i, h19i, h20i, h21i, h22i, h23i, h24i};

    static TextView h1t, h2t, h3t, h4t, h5t, h6t, h7t, h8t, h9t, h10t, h11t, h12t, h13t, h14t, h15t, h16t, h17t, h18t, h19t, h20t, h21t, h22t, h23t, h24t;
    static TextView[] h_time = {h1t, h2t, h3t, h4t, h5t, h6t, h7t, h8t, h9t, h10t, h11t, h12t, h13t, h14t, h15t, h16t, h17t, h18t, h19t, h20t, h21t, h22t, h23t, h24t};


    static TextView mDisplayDate, mDisplayHour;
    DatePickerDialog.OnDateSetListener mDateSetListener;
    TimePickerDialog.OnTimeSetListener mHourSetListener;

    static Date pastDate = new Date();
    static int pastDate_hour = 0;


    final String URLEndpoint = "https://api.darksky.net/forecast/";
    final String KEY = "bc9bf33f6e1f44d01a71a82ed8730ba9/";

    double latitude = 30.2885;
    double longitude = -97.7354 ;
    String COORDINATES = String.valueOf(latitude) + "," + String.valueOf(longitude);

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setDateSelector();
        setHourSelector();

        Button buttonRequest = findViewById(R.id.button);
        buttonRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "You have already granted this permission!",
                            Toast.LENGTH_SHORT).show();

                } else {
                    requestLocationPermission();
                }
            }
        });

        try{
            final LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            final String provider = locationManager.getBestProvider(new Criteria(), false);
            Location location = locationManager.getLastKnownLocation(provider);

            int lat = (int) (location.getLatitude()*10000);
            latitude = (double) lat/10000;
            int lon = (int) (location.getLongitude()*10000);
            longitude = (double) lon/10000;
            findCityName(location);

        } catch (Exception e) {
            e.printStackTrace();
        }


        findWeather();




        COORDINATES = String.valueOf(latitude) + "," + String.valueOf(longitude);
        t1 = (TextView) findViewById(R.id.coordinates_textView);
        t1.setText("(" + COORDINATES + ")");

        DownloadTask task = new DownloadTask();
        task.execute(URLEndpoint + KEY + COORDINATES);
        t1.setText("t0_successful");

        Long tsLong = pastDate.getTime()/1000L;
        String ts = tsLong.toString();

        DownloadTask2 task2 = new DownloadTask2();
        task2.execute(URLEndpoint + KEY + COORDINATES + "," + ts + "?exclude=currently,flags");


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void setDateSelector() {
        mDisplayDate = findViewById(R.id.tvDate);

        Calendar cal = Calendar.getInstance();
        int cur_month = (cal.get(Calendar.MONTH)+1);
        int cur_dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        int cur_year = cal.get(Calendar.YEAR);
        String date = cur_month + "/" + (cur_dayOfMonth-1) + "/" + cur_year;

        String str_date=cur_month+"-"+cur_dayOfMonth+"-"+cur_year;
        DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
        try {
            pastDate = (Date)formatter.parse(str_date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        mDisplayDate.setText(date);

        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year, month, day-1);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Log.d("MainActivity", "onDateSet: date: " + year + "/" + month + "/" + dayOfMonth + "/");

                String date = month+1 + "/" + dayOfMonth + "/" + year;

                String str_date=(month+1)+"-"+dayOfMonth+"-"+year;
                DateFormat formatter = new SimpleDateFormat("MM-dd-yyyy");
                try {
                    pastDate = (Date)formatter.parse(str_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Long tsLong = pastDate.getTime()/1000L;
                String ts = tsLong.toString();

                new DownloadTask2().execute(URLEndpoint + KEY + COORDINATES + "," + ts + "?exclude=currently,flags");

                mDisplayDate.setText(date);
            }
        };
    }

    private void setHourSelector() {
        mDisplayHour = findViewById(R.id.tvHour);
        Calendar cal = Calendar.getInstance();

        int hour = cal.get(Calendar.HOUR_OF_DAY);
        pastDate_hour = hour;
        String hour_str = String.valueOf(hour) + ":" + "00";
        mDisplayHour.setText(hour_str);

        mDisplayHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                TimePickerDialog dialog = new TimePickerDialog(
                        MainActivity.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mHourSetListener, hour, 0, true);

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mHourSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Log.d("MainActivity", "onTimeSet: time: " + hourOfDay + ":" + minute);

                String minute_str = minute > 9 ? String.valueOf(minute) : "0"+minute;
                String hour = hourOfDay + ":" + minute_str;

                pastDate_hour = hourOfDay;
                Long tsLong = pastDate.getTime()/1000L;
                String ts = tsLong.toString();

                new DownloadTask2().execute(URLEndpoint + KEY + COORDINATES + "," + ts + "?exclude=currently,flags");
                mDisplayHour.setText(hour);
            }
        };
    }

    public void findWeather(){
        t1_temperature = (TextView) findViewById(R.id.temperature_textView);
        t2_humidity = (TextView) findViewById(R.id.humidity_textView);
        t3_windSpeed = (TextView) findViewById(R.id.windSpeed_textView);
        t4_precipProbability = (TextView) findViewById(R.id.precipProbability_textView);

        t5_visibility = (TextView) findViewById(R.id.visibility_textView);
        t6_pressure = (TextView) findViewById(R.id.pressure_textView);
        t7_precipitation = (TextView) findViewById(R.id.precipitation_textView);

        t8_summary = (TextView) findViewById(R.id.summary_textView);

        h1 = findViewById(R.id.h1); h2 = findViewById(R.id.h2); h3 = findViewById(R.id.h3); h4 = findViewById(R.id.h4); h5 = findViewById(R.id.h5);
        h6 = findViewById(R.id.h6); h7 = findViewById(R.id.h7); h8 = findViewById(R.id.h8); h9 = findViewById(R.id.h9); h10 = findViewById(R.id.h10);
        h11 = findViewById(R.id.h11); h12 = findViewById(R.id.h12); h13 = findViewById(R.id.h13); h14 = findViewById(R.id.h14); h15 = findViewById(R.id.h15);
        h16 = findViewById(R.id.h16); h17 = findViewById(R.id.h17); h18 = findViewById(R.id.h18); h19 = findViewById(R.id.h19); h20 = findViewById(R.id.h20);
        h21 = findViewById(R.id.h21); h22 = findViewById(R.id.h22); h23 = findViewById(R.id.h23); h24 = findViewById(R.id.h24);

        h_1_24[0] = h1;  h_1_24[1] = h2;  h_1_24[2] = h3;  h_1_24[3] = h4;  h_1_24[4] = h5;  h_1_24[5] = h6;  h_1_24[6] = h7;  h_1_24[7] = h8;  h_1_24[8] = h9;
        h_1_24[9] = h10;  h_1_24[10] = h11;  h_1_24[11] = h12;  h_1_24[12] = h13; h_1_24[13] = h14; h_1_24[14] = h15; h_1_24[15] = h16;  h_1_24[16] = h17;
        h_1_24[17] = h18;  h_1_24[18] = h19; h_1_24[19] = h20; h_1_24[20] = h21; h_1_24[21] = h22; h_1_24[22] = h23; h_1_24[23] = h24;

        h1i = findViewById(R.id.h1i); h2i = findViewById(R.id.h2i); h3i = findViewById(R.id.h3i); h4i = findViewById(R.id.h4i); h5i = findViewById(R.id.h5i);
        h6i = findViewById(R.id.h6i); h7i = findViewById(R.id.h7i); h8i = findViewById(R.id.h8i); h9i = findViewById(R.id.h9i); h10i = findViewById(R.id.h10i);
        h11i = findViewById(R.id.h11i); h12i = findViewById(R.id.h12i); h13i = findViewById(R.id.h13i); h14i = findViewById(R.id.h14i); h15i = findViewById(R.id.h15i);
        h16i = findViewById(R.id.h16i); h17i = findViewById(R.id.h17i); h18i = findViewById(R.id.h18i); h19i = findViewById(R.id.h19i); h20i = findViewById(R.id.h20i);
        h21i = findViewById(R.id.h21i); h22i = findViewById(R.id.h22i); h23i = findViewById(R.id.h23i); h24i = findViewById(R.id.h24i);

        h_icon[0] = h1i;  h_icon[1] = h2i;  h_icon[2] = h3i;  h_icon[3] = h4i;  h_icon[4] = h5i;  h_icon[5] = h6i;  h_icon[6] = h7i;  h_icon[7] = h8i;  h_icon[8] = h9i;
        h_icon[9] = h10i;  h_icon[10] = h11i;  h_icon[11] = h12i;  h_icon[12] = h13i; h_icon[13] = h14i; h_icon[14] = h15i; h_icon[15] = h16i;  h_icon[16] = h17i;
        h_icon[17] = h18i;  h_icon[18] = h19i; h_icon[19] = h20i; h_icon[20] = h21i; h_icon[21] = h22i; h_icon[22] = h23i; h_icon[23] = h24i;

        h1t = findViewById(R.id.h1t); h2t = findViewById(R.id.h2t); h3t = findViewById(R.id.h3t); h4t = findViewById(R.id.h4t); h5t = findViewById(R.id.h5t);
        h6t = findViewById(R.id.h6t); h7t = findViewById(R.id.h7t); h8t = findViewById(R.id.h8t); h9t = findViewById(R.id.h9t); h10t = findViewById(R.id.h10t);
        h11t = findViewById(R.id.h11t); h12t = findViewById(R.id.h12t); h13t = findViewById(R.id.h13t); h14t = findViewById(R.id.h14t); h15t = findViewById(R.id.h15t);
        h16t = findViewById(R.id.h16t); h17t = findViewById(R.id.h17t); h18t = findViewById(R.id.h18t); h19t = findViewById(R.id.h19t); h20t = findViewById(R.id.h20t);
        h21t = findViewById(R.id.h21t); h22t = findViewById(R.id.h22t); h23t = findViewById(R.id.h23t); h24t = findViewById(R.id.h24t);

        h_time[0] = h1t;  h_time[1] = h2t;  h_time[2] = h3t;  h_time[3] = h4t;  h_time[4] = h5t;  h_time[5] = h6t;  h_time[6] = h7t;  h_time[7] = h8t;  h_time[8] = h9t;
        h_time[9] = h10t;  h_time[10] = h11t;  h_time[11] = h12t;  h_time[12] = h13t; h_time[13] = h14t; h_time[14] = h15t; h_time[15] = h16t;  h_time[16] = h17t;
        h_time[17] = h18t;  h_time[18] = h19t; h_time[19] = h20t; h_time[20] = h21t; h_time[21] = h22t; h_time[22] = h23t; h_time[23] = h24t;

        t10_averageTemperature = findViewById(R.id.AVERAGETEMP);

        d1h = findViewById(R.id.d1h);
        d2h = findViewById(R.id.d2h);
        d3h = findViewById(R.id.d3h);
        d4h = findViewById(R.id.d4h);
        d5h = findViewById(R.id.d5h);
        d6h = findViewById(R.id.d6h);
        d7h = findViewById(R.id.d7h);
        d1l = findViewById(R.id.d1l);
        d2l = findViewById(R.id.d2l);
        d3l = findViewById(R.id.d3l);
        d4l = findViewById(R.id.d4l);
        d5l = findViewById(R.id.d5l);
        d6l = findViewById(R.id.d6l);
        d7l = findViewById(R.id.d7l);
        d1w = findViewById(R.id.d1w);
        d2w = findViewById(R.id.d2w);
        d3w = findViewById(R.id.d3w);
        d4w = findViewById(R.id.d4w);
        d5w = findViewById(R.id.d5w);
        d6w = findViewById(R.id.d6w);
        d7w = findViewById(R.id.d7w);

        temp_feelsLike = findViewById(R.id.ydl);

        pdh = findViewById(R.id.pdh);
        pdl = findViewById(R.id.pdl);
        pdt = findViewById(R.id.pdt);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            ActivityCompat.requestPermissions(MainActivity.this,
//                                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1)  {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void findCityName(Location location){

        try {
            Geocoder geocoder = new Geocoder(this);
            List<Address> addresses = null;
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            String cityName = addresses.get(0).getLocality();
            t_cityName = findViewById(R.id.cityName_textView);
            t_cityName.setText(cityName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDateCurrentTimeZone(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }

    public static String getDateCurrentTime(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        }catch (Exception e) {
        }
        return "";
    }

    public static int getCurrentDayOfWeek(long timestamp) {
        try{
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timestamp * 1000);
            int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
            return dayOfWeek;
        }catch (Exception e) {
        }
        return -1;
    }
}
