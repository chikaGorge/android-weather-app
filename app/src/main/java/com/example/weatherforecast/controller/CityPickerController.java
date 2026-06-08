package com.example.weatherforecast.controller;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.example.weatherforecast.data.others.picker.ProvinceForPicker;
import com.example.weatherforecast.util.JsonUtils;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CityPickerController {
    private List<ProvinceForPicker> provinceForPickerList;

    // 添加线程池和主线程 Handler
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final Handler mainHandler=new Handler(Looper.getMainLooper());

    public void loadCityDataAsync(Context context, int rawResourceId, OnDataLoadedListener listener) {
        executorService.execute(() -> {
            // 后台线程中加载数据
            List<ProvinceForPicker> data = JsonUtils.loadCityDataForPicker(context, rawResourceId);

            // 在主线程回调更新 UI
            mainHandler.post(() -> {
                provinceForPickerList = data;
                if (listener != null) {
                    listener.onDataLoaded(data);
                }
            });
        });
    }

    public interface OnDataLoadedListener {
        void onDataLoaded(List<ProvinceForPicker> data);
    }

    public List<ProvinceForPicker> getProvinceForPickerList() {
        return provinceForPickerList;
    }
}
