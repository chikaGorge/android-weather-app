package com.example.weatherforecast.controller;

import android.content.Context;
import android.util.Log;

import com.example.weatherforecast.R;
import com.example.weatherforecast.common.db.AppDatabase;
import com.example.weatherforecast.data.model.CityWeather;
import com.example.weatherforecast.data.others.response.WeatherResponse;
import com.example.weatherforecast.util.GetWeatherUtil;
import com.google.gson.Gson;

public class GetWeatherController {
    private GetWeatherUtil getWeatherUtil;

    public GetWeatherController() {
        this.getWeatherUtil = new GetWeatherUtil();
        Log.d("GetWeatherService", "this.getWeatherUtil is "+getWeatherUtil!=null?"initialized":"null");

    }

    // 异步获取天气信息并返回CityWeather对象
    // 异步获取天气信息并返回CityWeather对象
    public void getCityWeather(final String city, final int userId, final WeatherCallback callback) {
        String[] cityParts = city.split("-");
        String cityname;
        if (cityParts.length > 2) { // 检查数组长度是否大于2
            cityname = cityParts[2]; // 使用数组索引访问元素，而不是函数调用
        } else if(cityParts.length>1){
            cityname = cityParts[1]; // 如果没有区县信息，直接使用城市变量
        }else{
            cityname=city;
        }
        getWeatherUtil.getWeather(cityname, new GetWeatherUtil.WeatherCallback() {
            @Override
            public void onSuccess(WeatherResponse weatherResponse) {
                Log.d("GetWeatherService", "Received weather response: " + new Gson().toJson(weatherResponse));

                if (weatherResponse != null && !weatherResponse.getLives().isEmpty()) {
                    com.example.weatherforecast.data.others.response.WeatherResponse.Lives live = weatherResponse.getLives().get(0);
                    int weatherIcon = adjustIcon(live.getWeather());
                    CityWeather cityWeather = new CityWeather(
                            city,
                            userId,
                            live.getProvince(),
                            live.getAdcode(),
                            live.getWeather(),
                            weatherIcon,
                            live.getTemperature(),
                            live.getWinddirection(),
                            live.getWindpower(),
                            live.getHumidity(),
                            live.getReporttime());
                    // 返回获取到的CityWeather对象
                    callback.onSuccess(cityWeather);
                } else {
                    Log.e("WeatherService", "Weather data is incomplete or invalid for city: " + city);
                    callback.onError("Weather data is incomplete or invalid.");
                }
            }

            @Override
            public void onError(String error) {
                // 打印错误日志并将错误信息返回给回调
                Log.e("WeatherService", "Error fetching weather data for city: " + city + ". Error: " + error);
                callback.onError(error);
            }
        });
    }


    // 调整天气图标
    private int adjustIcon(String weather) {
        switch (weather) {
            case "晴":
            case "少云":
                return R.drawable.ic_weather_sunny;
            case "多云":
            case "平静":
            case "阴":
            case "晴间多云":
                return R.drawable.ic_weather_cloudy;
            case "浮尘":
            case "扬沙":
            case "沙尘暴":
            case "强沙尘暴":
            case "龙卷风":
                return R.drawable.ic_weather_dust;
            case "雾":
            case "浓雾":
            case "强浓雾":
            case "轻雾":
            case "大雾":
            case "特强浓雾":
                return R.drawable.ic_weather_foggy;
            case "霾":
            case "中度霾":
            case "重度霾":
            case "严重霾":
                return R.drawable.ic_weather_haze;
            case "阵雨":
            case "小雨":
            case "大雨":
            case "中雨":
            case "暴雨":
            case "大暴雨":
            case "雨":
                return R.drawable.ic_weather_rainy;
            case "雪":
            case "阵雪":
            case "小雪":
            case "中雪":
            case "大雪":
            case "暴雪":
            case "小雪-中雪":
            case "中雪-大雪":
            case "大雪-暴雪":
                return R.drawable.ic_weather_snowy;
            case "强雷阵雨":
                return R.drawable.ic_weather_thunderstorm;
            case "有风":
            case "微风":
            case "和风":
            case "清风":
            case "强风/劲风":
            case "疾风":
            case "大风":
            case "烈风":
            case "风暴":
            case "狂暴风":
            case "飓风":
            case "热带风暴":
                return R.drawable.ic_weather_windy;
            default:
                return R.drawable.ic_weather_unknow;
        }
    }

    // 回调接口，通知结果
    public interface WeatherCallback {
        void onSuccess(CityWeather cityWeather);
        void onError(String error);
    }
}
