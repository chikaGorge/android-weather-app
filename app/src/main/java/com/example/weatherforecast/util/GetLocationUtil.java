package com.example.weatherforecast.util;

import com.amap.api.maps.model.LatLng;
import com.example.weatherforecast.common.service.LocationService;

import org.json.JSONObject;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import android.util.Log;

public class GetLocationUtil {
    public static final String GD_API_KEY = "d2aef9def6bfd3c85206a92b1b27ede6";
    public static final String GD_BASE_URL = "https://restapi.amap.com/";  // 参数为key=&city=

    private LocationService locationService;

    public GetLocationUtil() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(GD_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        locationService = retrofit.create(LocationService.class);
    }

    public void getLocation(String city, final LocationCallBack callBack) {
        Log.d("GetLocationUtil", "Requesting location for city: " + city);

        // 创建网络请求
        Call<ResponseBody> call = locationService.getLocation(GD_API_KEY, city);

        // 发起异步请求
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.d("GetLocationUtil", "Received response from server for city: " + city);

                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String responseStr = response.body().string();
                        JSONObject jsonObject = new JSONObject(responseStr);
                        String status = jsonObject.optString("status");
                        String infocode=jsonObject.optString("infocode");
                        if ("1".equals(status)) {
                            JSONObject geocode = jsonObject.getJSONArray("geocodes").getJSONObject(0);
                            String location = geocode.optString("location");

                            // 提取经纬度
                            if (location != null && !location.isEmpty()) {
                                String[] coords = location.split(",");
                                if (coords.length == 2) {
                                    double longitude = Double.parseDouble(coords[0]);
                                    double latitude = Double.parseDouble(coords[1]);
                                    LatLng latLng = new LatLng(latitude, longitude);

                                    // 返回经纬度
                                    Log.d("GetLocationUtil", "Location found: Latitude = " + latitude + ", Longitude = " + longitude);
                                    callBack.onSuccess(latLng);
                                }
                            } else {
                                Log.e("GetLocationUtil", "Location string is empty or invalid.");
                                callBack.onError("Location data is empty or invalid.");
                            }
                        } else {
                            Log.e("GetLocationUtil", "Failed to get valid location data for city: " + city+"infocode:"+infocode);
                            callBack.onError("Failed to get valid location data.");
                        }
                    } catch (Exception e) {
                        Log.e("GetLocationUtil", "Error parsing the response for city: " + city, e);
                        callBack.onError("Error parsing the response.");
                    }
                } else {
                    Log.e("GetLocationUtil", "Failed to get location. Response code: " + response.code());
                    callBack.onError("Failed to get location. Response code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable throwable) {
                Log.e("GetLocationUtil", "Error fetching location for city: " + city, throwable);
                callBack.onError("Error fetching location: " + throwable.getMessage());
            }
        });
    }

    public interface LocationCallBack {
        void onSuccess(LatLng latLng);
        void onError(String error);
    }
}
