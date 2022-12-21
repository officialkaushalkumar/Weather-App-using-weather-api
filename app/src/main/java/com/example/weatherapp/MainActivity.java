package com.example.weatherapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.method.TextKeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

//---------------------declaration---------------------------------

    private RelativeLayout homeRL;
    private ProgressBar lodingpb;
    private TextView citynametv, tempraturetv, conditiontv;
    private RecyclerView weatherrv;
    private TextInputEditText cityedt;
    private ImageView backiv, iconiv, searchiv;
    private ArrayList<WeatherRVModel> weatherRVModels;
    private WeatherRVAdapter weatherRVAdapter;
    private LocationManager locationManager;
    private int PERMISSION_CODE = 1;
    private String cityName;

//----------------------declaration ends----------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_main);


        initializeVariables();
        getWeatherInfo(cityName);
        searchiv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city  =cityedt.getText().toString();
                if(city.isEmpty()){
                    Toast.makeText(MainActivity.this, "enter city name", Toast.LENGTH_SHORT).show();
                }
                else{
                    citynametv.setText(cityName);
                    getWeatherInfo(city);
//                    Toast.makeText(MainActivity.this,city,Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public void initializeVariables() {

        homeRL = findViewById(R.id.idrlhome);
        lodingpb = findViewById(R.id.idpbloading);
        citynametv = findViewById(R.id.idtvcityname);
        tempraturetv = findViewById(R.id.idtvtemprature);
        conditiontv = findViewById(R.id.idtvcondition);
        weatherrv = findViewById(R.id.idrvweather);
        cityedt = findViewById(R.id.idedtcity);
        backiv = findViewById(R.id.idivback);
        iconiv = findViewById(R.id.idivicon);
        searchiv = findViewById(R.id.idivsearch);

        weatherRVModels = new ArrayList<>();
        weatherRVAdapter = new WeatherRVAdapter(this, weatherRVModels);
        weatherrv.setAdapter(weatherRVAdapter);


        //----------------Location purpose---------------------------------------------------------------------------------

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_CODE);
        }
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        cityName = getCityName(location.getLongitude(),location.getLatitude());
//        Toast.makeText(MainActivity.this, cityName, Toast.LENGTH_SHORT).show();

        //-----------------end location purpose --------------------------------------------------------------------------

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==PERMISSION_CODE){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "permission granted", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "please provide the permissions", Toast.LENGTH_SHORT).show();
                finish();
            }
        }

    }

    //----------------------finding city name-----------------------------------------------------------------------

    public String getCityName(double longitude,double latitude){
        String cityName = "Not Found";
        Geocoder gcd = new Geocoder(getBaseContext(), Locale.getDefault());
        try{
            List<Address> addresses = gcd.getFromLocation(latitude,longitude,10);
            for(Address adr : addresses){
                if(adr!=null){
                    String city = adr.getLocality();
                    if(city!=null && !city.equals("")){
                        cityName= city;
                    }
                    else{
                        Log.d("tag","city not found");
                        Toast.makeText(this, "user city not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }

    //-------------------------end finding city name-----------------------------------------------------------


    //------------------------getting info from api-------------------------------------------------------------

    public void getWeatherInfo(String cityName) {
        String url = "https://api.weatherapi.com/v1/forecast.json?key=8bce8c98c0714a03926204846221912&q=" + cityName + "&days=1&aqi=yes&alerts=yes";
        citynametv.setText(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,url,null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                Toast.makeText(MainActivity.this, "response is working", Toast.LENGTH_SHORT).show();
                lodingpb.setVisibility(View.GONE);
                homeRL.setVisibility(View.VISIBLE);
                weatherRVModels.clear();

                try {
                    String temprature = response.getJSONObject("current").getString("temp_c");
                    tempraturetv.setText(temprature+ "°c");
                    int isday = response.getJSONObject("current").getInt("is_day");
                    String condition = response.getJSONObject("current").getJSONObject("condition").getString("text");
                    String conditionicon = response.getJSONObject("current").getJSONObject("condition").getString("icon");
                    Picasso.get().load("https:".concat(conditionicon)).into(iconiv);
                    conditiontv.setText(condition);
                    if(isday==1){
                        //morning
                        Picasso.get().load("https://images.unsplash.com/photo-1610981899074-5d5c549bf857?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1887&q=80").into(backiv);
                    }
                    else {
                        //night
                        Picasso.get().load("https://images.unsplash.com/photo-1530508777238-14544088c3ed?ixlib=rb-4.0.3&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1887&q=80").into(backiv);
                    }

                    JSONObject forecastObj = response.getJSONObject("forecast");
                    JSONObject forecastO =forecastObj.getJSONArray("forecastday").getJSONObject(0);
                    JSONArray hourArray = forecastO.getJSONArray("hour");

                    for(int i=0;i<hourArray.length();i++){
                        JSONObject hourObj = hourArray.getJSONObject(i);
                        String time = hourObj.getString("time");
                        String temper = hourObj.getString("temp_c");
                        String img =  hourObj.getJSONObject("condition").getString("icon");
                        String wind = hourObj.getString("wind_kph");
                        weatherRVModels.add(new WeatherRVModel(time,temper,img,wind));
                    }
                    weatherRVAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(jsonObjectRequest);
    }
    //-------------------------getting info ends ------------------------------------------------------------------
}