package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText inputWeather;
    private Button buttonSearch;

    // Konstanta URL API dan API key
    private static final String API_KEY = "33b25af9af4f581e2c6caa84e537a1ca";
    private static final String API_BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=";

    // Deklarasi elemen UI untuk menampilkan data cuaca
    private TextView nameCity;
    private TextView lblTemp;
    private TextView detail;
    private TextView lblMinTemp;
    private TextView lblMaxTemp;
    private TextView lblPressure;
    private TextView lblHumidity;


    private class WeatherTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String apiUrl = params[0];
            try {
                return getDataFromApi(apiUrl);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        protected void onPostExecute(String jsonResponse) {
            if (jsonResponse != null) {
                processApiResponse(jsonResponse);
            } else {
                Toast.makeText(MainActivity.this, "Not Found.", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inisialisasi elemen UI
        inputWeather = findViewById(R.id.inputWeather);
        buttonSearch = findViewById(R.id.buttonSearch);

        // Set nilai default ke "Manado"
        inputWeather.setText("Manado");

        // Inisialisasi elemen UI untuk menampilkan data cuaca
        nameCity = findViewById(R.id.nameCity);
        lblTemp = findViewById(R.id.lblTemp);
        detail = findViewById(R.id.detail);
        lblMinTemp = findViewById(R.id.lblMinTemp);
        lblMaxTemp = findViewById(R.id.lblMaxTemp);
        lblPressure = findViewById(R.id.lblPressure);
        lblHumidity = findViewById(R.id.lblHumidity);

        // Buat URL untuk permintaan API
        String cityName = inputWeather.getText().toString();
        String apiUrl = API_BASE_URL + cityName + "&appid=" + API_KEY;

        // Menjalankan permintaan di latar belakang
        new WeatherTask().execute(apiUrl);

        // Menambahkan tindakan klik pada tombol pencarian
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String cityName = inputWeather.getText().toString();

                if (!cityName.isEmpty()) {
                    // Buat URL untuk permintaan API
                    String apiUrl = API_BASE_URL + cityName + "&appid=" + API_KEY;
                    new WeatherTask().execute(apiUrl); // Menjalankan permintaan di latar belakang
                } else {
                    // Kasus jika input kosong
                    Toast.makeText(MainActivity.this, "Silakan masukkan nama kota terlebih dahulu", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    // Melakukan permintaan API dan mengembalikan respons JSON sebagai string
    private String getDataFromApi(String apiUrl) throws IOException {
        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Mendapatkan respons dari API
        InputStream inputStream = connection.getInputStream();
        Scanner scanner = new Scanner(inputStream).useDelimiter("\\A");
        boolean hasInput = scanner.hasNext();
        if (hasInput) {
            return scanner.next();
        } else {
            return null;
        }
    }

    // Memproses respons JSON dan menampilkan data cuaca
    private void processApiResponse(String jsonResponse) {
        try {
            JSONObject response = new JSONObject(jsonResponse);

            // Mengambil data dari JSON
            nameCity.setText(response.optString("name"));
            double kelvinTemp = response.getJSONObject("main").getDouble("temp");
            double celsiusTemp = kelvinTemp - 273.15;
            lblTemp.setText(String.format("%.0f", celsiusTemp));
            JSONArray weatherArray = response.getJSONArray("weather");
            if (weatherArray.length() > 0) {
                JSONObject weatherObject = weatherArray.getJSONObject(0);
                String weatherDescription = weatherObject.optString("main");
                detail.setText(weatherDescription);
            }
            double kelvinMinTemp = response.getJSONObject("main").getDouble("temp_min");
            double celsiusMinTemp = kelvinMinTemp - 273.15;
            lblMinTemp.setText(String.format("%.0f °C", celsiusMinTemp));
            double kelvinMaxTemp = response.getJSONObject("main").getDouble("temp_max");
            double celsiusMaxTemp = kelvinMaxTemp - 273.15;
            lblMaxTemp.setText(String.format("%.0f °C", celsiusMaxTemp));
            lblPressure.setText(response.getJSONObject("main").optString("pressure"));
            lblHumidity.setText(response.getJSONObject("main").optString("humidity"));
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Gagal memproses respons JSON.", Toast.LENGTH_SHORT).show();
        }
    }
}