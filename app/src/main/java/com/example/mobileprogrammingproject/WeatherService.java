package com.example.mobileprogrammingproject;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("weather")
    Call<WeatherResponse> getCurrentWeather(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

    @GET("weather")
    Call<WeatherResponse> getWeatherByCityName(
            @Query("q") String cityName,
            @Query("appid") String apiKey
    );

    @GET("forecast")
    Call<ForecastResponse> getForecastByCityName(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

    @GET("forecast/daily")
    Call<ForecastResponse> getWeatherForecast(
            @Query("lat") double latitude,
            @Query("lon") double longitude,
            @Query("cnt") int days,
            @Query("appid") String apiKey,
            @Query("units") String units
    );

}