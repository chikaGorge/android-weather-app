package com.example.weatherforecast.controller.viewModel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.weatherforecast.data.model.CityWeather;
import com.example.weatherforecast.data.repository.CityWeatherRepository;

import java.util.List;


public class CityWeatherViewModel extends ViewModel {
    private MutableLiveData<List<CityWeather>> cityWeatherLiveData = new MutableLiveData<>();
    private MutableLiveData<List<CityWeather>> historyWeatherLiveData = new MutableLiveData<>();
//    private int currentUserId,currentColumn=1;
    private CityWeatherRepository cityWeatherRepository;
    private MutableLiveData<Integer> currentUserId=new MutableLiveData<>();
    private MutableLiveData<Integer> currentColumn=new MutableLiveData<>(1);
    // 构造函数
    public CityWeatherViewModel(Context context) {
        this.cityWeatherRepository = new CityWeatherRepository(context);
        Log.d("UserViewModel", "userRepository: " + (cityWeatherRepository == null ? "null" : "initialized"));
    }

    // 添加喜欢的城市
    public void addLikeCity(int userId, String city, CityWeather cityWeather) {
        if (cityWeather == null || city == null || city.isEmpty()) {
            Log.e("CityWeatherViewModel", "addLikeCity: CityWeather or city is null");
            return;
        }
        Log.d("CityWeatherViewModel", "addLikeCity: userId = " + userId + ", city = " + city);

        // 使用异步方式检查天气是否已存在
        cityWeatherRepository.isWeatherExisted(userId, city, new CityWeatherRepository.Callback<CityWeather>() {
            @Override
            public void onResult(CityWeather isExisted) {
                if (isExisted == null) {
                    Log.d("CityWeatherViewModel", "addLikeCity: City does not exist, inserting new weather data");
                    cityWeatherRepository.insertCityWeather(cityWeather);
                } else {
                    Log.d("CityWeatherViewModel", "addLikeCity: City already exists, updating weather data");
                    cityWeather.setId(isExisted.getId());
                    cityWeatherRepository.updateCityWeather(cityWeather);
                }
                //
                currentUserId.postValue(userId);
                // 更新 LiveData
                fetchLikeCities(userId);  // 调用方法获取并更新喜欢的城市数据
            }
        });
    }

    // 查询历史城市天气
    public void checkHistoryCity(int userId, int column) {
        if (userId <= 0 || column <= 0) {
            Log.e("CityWeatherViewModel", "checkHistoryCity: Invalid userId or column");
            return;
        }

        Log.d("CityWeatherViewModel", "checkHistoryCity: userId = " + userId + ", column = " + column);

        // 使用异步方式获取历史天气数据
        cityWeatherRepository.checkHistoryWeather(userId, column, new CityWeatherRepository.Callback<List<CityWeather>>() {
            @Override
            public void onResult(List<CityWeather> cityWeatherList) {
                if (cityWeatherList == null) {
                    Log.e("CityWeatherViewModel", "checkHistoryCity: Failed to fetch historical weather data");
                } else {
                    Log.d("CityWeatherViewModel", "checkHistoryCity: Fetched " + cityWeatherList.size() + " historical cities");
                    historyWeatherLiveData.postValue(cityWeatherList);
                    currentColumn.postValue(column);
                    currentUserId.postValue(userId);
                }
            }
        });
    }

    // 获取喜欢的城市 LiveData
    public LiveData<List<CityWeather>> getLikeLiveData() {
        return cityWeatherLiveData;
    }

    // 获取历史城市 LiveData
    public LiveData<List<CityWeather>> getHistoryLiveData() {
        return historyWeatherLiveData;
    }

    // 内部方法：异步获取喜欢的城市数据并更新 LiveData
    private void fetchLikeCities(int userId) {
        cityWeatherRepository.checkLikeWeather(userId, new CityWeatherRepository.Callback<List<CityWeather>>() {
            @Override
            public void onResult(List<CityWeather> cityWeatherList) {
                if (cityWeatherList == null) {
                    Log.e("CityWeatherViewModel", "fetchLikeCities: Failed to fetch liked weather data");
                } else {
                    Log.d("CityWeatherViewModel", "fetchLikeCities: Fetched " + cityWeatherList.size() + " liked cities");
                    cityWeatherLiveData.postValue(cityWeatherList);
                    currentUserId.postValue(userId);
                }
            }
        });
    }
    // 强制刷新感兴趣的城市天气列表
    public void forceRefreshLikeCities() {
        Integer userId = currentUserId.getValue();  // 获取 currentUserId 的值
        Log.d("CityWeatherViewModel", "forceRefreshLikeCities: userId = " + userId);

        if (userId != null) {  // 检查 userId 是否为 null
            fetchLikeCities(userId);  // 重新请求感兴趣城市天气数据
        } else {
            Log.e("CityWeatherViewModel", "currentUserId is null, cannot refresh cities.");
        }
    }

    // 强制刷新历史城市天气列表
    public void forceRefreshHistoryCities() {
        Integer userId = currentUserId.getValue();  // 获取 currentUserId 的值
        Integer column = currentColumn.getValue();  // 获取 currentColumn 的值
        Log.d("CityWeatherViewModel", "forceRefreshHistoryCities: userId = " + userId + ", column = " + column);

        if (userId != null && column != null) {  // 检查 userId 和 column 是否为 null
            checkHistoryCity(userId, column);  // 重新请求历史城市天气数据
        } else {
            Log.e("CityWeatherViewModel", "currentUserId or currentColumn is null, cannot refresh history cities.");
        }
    }

    public void setUserId(int id){
        currentUserId.postValue(id);
    }
}
