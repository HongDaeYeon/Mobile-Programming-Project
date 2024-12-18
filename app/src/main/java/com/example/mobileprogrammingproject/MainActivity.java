package com.example.mobileprogrammingproject;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.EditText;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 1;
    private static final String API_KEY = "ecc7e53de2fb86db5c9df3a23633bf1f";
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/";

    private FusedLocationProviderClient fusedLocationClient;
    private TextView weatherTextView;
    private EditText cityEditText;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        weatherTextView = findViewById(R.id.weatherTextView);
        cityEditText = findViewById(R.id.cityEditText);
        searchButton = findViewById(R.id.searchButton);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        } else {
            getCurrentLocation();
        }

        searchButton.setOnClickListener(v -> {
            String cityName = cityEditText.getText().toString();
            if (!cityName.isEmpty()) {
                getWeatherByCityName(cityName);
                getWeatherForecastByCityName(cityName);
            } else {
                Toast.makeText(MainActivity.this, "도시 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getCurrentLocation() {
        Task<Location> locationResult = fusedLocationClient.getLastLocation();
        locationResult.addOnSuccessListener(this, location -> {
            if (location != null) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                getWeatherData(latitude, longitude);
            } else {
                Toast.makeText(MainActivity.this, "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWeatherData(double latitude, double longitude) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService weatherService = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = weatherService.getCurrentWeather(latitude, longitude, API_KEY, "metric");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    String weatherInfo = "현재 날씨: " + weatherResponse.getWeather().get(0).getDescription() + "\n" +
                            "온도: " + weatherResponse.getMain().getTemp() + "°C";
                    weatherTextView.setText(weatherInfo);
                } else {
                    Toast.makeText(MainActivity.this, "날씨 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "API 호출 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWeatherByCityName(String cityName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService weatherService = retrofit.create(WeatherService.class);
        Call<WeatherResponse> call = weatherService.getWeatherByCityName(cityName, API_KEY);

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    String weatherInfo = "현재 날씨: " + weatherResponse.getWeather().get(0).getDescription() + "\n" +
                            "온도: " + weatherResponse.getMain().getTemp() + "°C";
                    weatherTextView.setText(weatherInfo);
                } else {
                    Toast.makeText(MainActivity.this, "도시를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "API 호출 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getWeatherForecastByCityName(String cityName) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        WeatherService weatherService = retrofit.create(WeatherService.class);
        Call<ForecastResponse> call = weatherService.getForecastByCityName(cityName, API_KEY, "metric");

        call.enqueue(new Callback<ForecastResponse>() {
            @Override
            public void onResponse(Call<ForecastResponse> call, Response<ForecastResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ForecastResponse forecastResponse = response.body();
                    if (forecastResponse.getList() == null) {
                        Toast.makeText(MainActivity.this, "예보 데이터를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    List<ForecastResponse.Forecast> forecastList = forecastResponse.getList();
                    StringBuilder forecastInfo = new StringBuilder("5일간의 날씨 예보:\n");
                    Set<String> dateSet = new HashSet<>();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    int forecastCount = 0;

                    for (ForecastResponse.Forecast forecast : forecastList) {
                        String date = dateFormat.format(new Date(forecast.getDt() * 1000L));
                        if (!dateSet.contains(date)) {
                            forecastInfo.append(date).append(" - ")
                                    .append("최고 온도: ").append(forecast.getMain().getTempMax()).append("°C, ")
                                    .append("최저 온도: ").append(forecast.getMain().getTempMin()).append("°C, ")
                                    .append("상태: ").append(forecast.getWeather().get(0).getDescription())
                                    .append("\n");

                            dateSet.add(date);
                            forecastCount++;

                            if (forecastCount >= 5) {
                                break;
                            }
                        }
                    }

                    weatherTextView.setText(forecastInfo.toString());
                } else {
                    Toast.makeText(MainActivity.this, "날씨 예보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ForecastResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "API 호출 실패: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
