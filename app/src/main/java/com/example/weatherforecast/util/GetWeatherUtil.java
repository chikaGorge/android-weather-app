package com.example.weatherforecast.util;

import com.example.weatherforecast.common.service.WeatherService;
import com.example.weatherforecast.data.others.response.WeatherResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.util.Log;

public class GetWeatherUtil {
    public static final String GD_WEATHER_KEY = "d2aef9def6bfd3c85206a92b1b27ede6";
    public static final String GD_WEATHER_BASEURL = "https://restapi.amap.com/";  // 参数为key=&city=
    private WeatherService weatherService;

    public GetWeatherUtil() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GD_WEATHER_BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherService = retrofit.create(WeatherService.class);
    }

    public void getWeather(String city, final WeatherCallback callback) {
        Log.d("GetWeatherUtil", "Requesting weather for city: " + city);

        // 创建网络请求
        Call<WeatherResponse> call = weatherService.getWeather(GD_WEATHER_KEY, city);

        // 发起异步请求
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                Log.d("GetWeatherUtil", "Received response from server for city: " + city);

                if (response.isSuccessful() && response.body() != null) {
                    Log.d("GetWeatherUtil", "Weather data retrieved successfully: " + response.body());
                    callback.onSuccess(response.body());
                } else {
                    // 请求失败或响应体为空
                    Log.e("GetWeatherUtil", "Failed to get weather data. Response code: " + response.code());
                    callback.onError("Failed to get weather data. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                // 网络请求失败，打印错误信息
                Log.e("GetWeatherUtil", "Error fetching weather data for city: " + city, t);
                callback.onError("Error fetching weather data: " + t.getMessage());
            }
        });
    }

    public interface WeatherCallback {
        void onSuccess(WeatherResponse weatherResponse);
        void onError(String error);
    }
}
