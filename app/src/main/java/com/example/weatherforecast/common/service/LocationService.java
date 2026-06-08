package com.example.weatherforecast.common.service;


import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LocationService {

    @GET("v3/geocode/geo")
    Call<ResponseBody> getLocation(@Query("key")String key,
                                   @Query("address")String address);
}
