package com.example.weatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText input;
    TextView resultText;

    //weather variable
    String main;
    String description;
    //main variable
    String temp;
    String pressure;
    String humidity;
    //City
    String city;

    public void getWeather(View view){
        InputMethodManager methodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        methodManager.hideSoftInputFromWindow(input.getWindowToken(), 0);

        try {
            String encodedCity = URLEncoder.encode(input.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("https://openweathermap.org/data/2.5/weather?q=" + encodedCity + "&appid=b6907d289e10d714a6e88b30761fae22");
        } catch (UnsupportedEncodingException e) {
            Toast.makeText(MainActivity.this, "Unable to find weather",Toast.LENGTH_SHORT).show();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data!=-1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e) {
                Toast.makeText(MainActivity.this, "Unable to find weather",Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject object = new JSONObject(result);

                //City
                city = object.getString("name");


                //Weather
                String weather = object.getString("weather");
                JSONArray jsonWeather = new JSONArray(weather);
                for (int i=0;i<jsonWeather.length();i++){
                    JSONObject part = jsonWeather.getJSONObject(i);
                    main = part.getString("main");
                    description = part.getString("description");
                }

                //Main
                JSONObject jsonMain = object.getJSONObject("main");
                temp = jsonMain.getString("temp");
                pressure = jsonMain.getString("pressure");
                humidity = jsonMain.getString("humidity");

                if (main !="" && temp !=""){
                    resultText.setText(
                    "City : " + city + "\n"
                            + "Status : "+ main + "\n"
                            + "Description : " + description + "\n"
                            + "Temperature : " + temp + "\n"
                            + "Pressure : " + pressure + "\n"
                            + "Humidity : " + humidity
                    );
                }
                else {
                    Toast.makeText(MainActivity.this, "Unable to find weather",Toast.LENGTH_SHORT).show();
                }


            } catch (JSONException e) {
                Toast.makeText(MainActivity.this, "Unable to find weather",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input = findViewById(R.id.input);
        resultText = findViewById(R.id.result);
    }
}
