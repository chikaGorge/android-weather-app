package com.example.weatherforecast.common.service;


import com.example.weatherforecast.data.others.response.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {
    @GET("v3/weather/weatherInfo")
    Call<WeatherResponse> getWeather(@Query("key")String apiKey,
                                     @Query("city")String city);

}
