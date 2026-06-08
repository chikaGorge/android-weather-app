package com.example.weatherforecast.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.weatherforecast.data.model.CityWeather;

import java.util.List;

@Dao
public interface CityWeatherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertCityWeather(CityWeather cityWeather);
    @Delete
    int deleteCityWeather(CityWeather cityWeather);
    @Update
    int updateCityWeather(CityWeather cityWeather);
    @Query("SELECT * FROM city_weather WHERE user_id=:userId ORDER BY reportTime DESC LIMIT :column")
    List<CityWeather> checkHistoryWeather(int userId,int column);
    @Query("SELECT * FROM city_weather WHERE user_id=:userId ORDER BY reportTime DESC")
    List<CityWeather> checkLikeWeather(int userId);
    @Query("SELECT * FROM city_weather WHERE user_id=:userId AND city=:city")
    CityWeather isWeatherExisted(int userId,String city);
    @Query("SELECT * FROM city_weather")
    List<CityWeather> getAllCityWeather();
    @Query("DELETE FROM city_weather")
    int deleteAllCityWeather();
}
