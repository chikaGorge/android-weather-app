package com.example.weatherforecast.data.repository;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.weatherforecast.common.db.AppDatabase;
import com.example.weatherforecast.data.dao.CityWeatherDao;
import com.example.weatherforecast.data.model.CityWeather;

import java.util.List;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CityWeatherRepository {
    private CityWeatherDao cityWeatherDao;
    private ExecutorService executorService;

    // 构造函数
    public CityWeatherRepository(Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        cityWeatherDao = db.cityWeatherDao();
        executorService = Executors.newSingleThreadExecutor();  // 使用单线程池来执行数据库操作
    }

    // 插入城市天气数据
    public void insertCityWeather(CityWeather cityWeather) {
        if (cityWeather == null) {
            Log.e("CityWeatherRepository", "insertCityWeather: CityWeather is null");
            return;
        }
        executorService.execute(() -> {
            long result = cityWeatherDao.insertCityWeather(cityWeather);
            Log.d("CityWeatherRepository", "insertCityWeather: Result = " + result);
        });
    }

    // 删除城市天气数据
    public void deleteCityWeather(CityWeather cityWeather) {
        if (cityWeather == null) {
            Log.e("CityWeatherRepository", "deleteCityWeather: CityWeather is null");
            return;
        }
        executorService.execute(() -> {
            int result = cityWeatherDao.deleteCityWeather(cityWeather);
            Log.d("CityWeatherRepository", "deleteCityWeather: Result = " + result);
        });
    }

    // 更新城市天气数据
    public void updateCityWeather(CityWeather cityWeather) {
        if (cityWeather == null) {
            Log.e("CityWeatherRepository", "updateCityWeather: CityWeather is null");
            return;
        }
        executorService.execute(() -> {
            int result = cityWeatherDao.updateCityWeather(cityWeather);
            Log.d("CityWeatherRepository", "updateCityWeather: Result = " + result);
        });
    }

    // 查询历史天气数据
    public void checkHistoryWeather(int userId, int column, Callback<List<CityWeather>> callback) {
        executorService.execute(() -> {
            List<CityWeather> result = cityWeatherDao.checkHistoryWeather(userId, column);
            callback.onResult(result);  // 将结果回调到UI线程
        });
    }

    // 查询用户喜欢的城市天气
    public void checkLikeWeather(int userId, Callback<List<CityWeather>> callback) {
        executorService.execute(() -> {
            List<CityWeather> result = cityWeatherDao.checkLikeWeather(userId);
            callback.onResult(result);  // 将结果回调到UI线程
        });
    }

    // 判断城市天气是否存在
    public void isWeatherExisted(int userId, String city, Callback<CityWeather> callback) {
        if (city == null || city.isEmpty()) {
            Log.e("CityWeatherRepository", "isWeatherExisted: City is null or empty");
            return;
        }
        executorService.execute(() -> {
            CityWeather result = cityWeatherDao.isWeatherExisted(userId, city);
            callback.onResult(result);  // 将结果回调到UI线程
        });
    }

    // 获取所有城市天气数据
    public void getAllCityWeather(Callback<List<CityWeather>> callback) {
        executorService.execute(() -> {
            List<CityWeather> result = cityWeatherDao.getAllCityWeather();
            callback.onResult(result);  // 将结果回调到UI线程
        });
    }

    // 删除所有城市天气数据
    public void deleteAllCityWeather() {
        Log.d("CityWeatherRepository", "deleteAllCityWeather: Deleting all city weather data");
        executorService.execute(() -> {
            int result = cityWeatherDao.deleteAllCityWeather();
            Log.d("CityWeatherRepository", "deleteAllCityWeather: Result = " + result);
        });
    }

    // 定义回调接口
    public interface Callback<T> {
        void onResult(T result);
    }
}

