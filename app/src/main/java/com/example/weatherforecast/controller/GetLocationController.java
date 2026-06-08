package com.example.weatherforecast.controller;

import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.example.weatherforecast.util.GetLocationUtil;

public class GetLocationController {
    private GetLocationUtil getLocationUtil;
    public GetLocationController(){
        this.getLocationUtil=new GetLocationUtil();
        Log.d("GetLocationService", "this.getLocationUtil is "+getLocationUtil!=null?"initialized":"null");

    }
    public void fetchLatLngForCity(String city,final LatLngCallback callback){
        String cityName=city.replaceAll("[\\s-]", "");
        getLocationUtil.getLocation(cityName, new GetLocationUtil.LocationCallBack() {
            @Override
            public void onSuccess(LatLng latLng) {
                callback.onLatRetrieved(latLng);
            }

            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    public interface LatLngCallback{
        void onLatRetrieved(LatLng latLng);
        void onError(String error);
    }
}
