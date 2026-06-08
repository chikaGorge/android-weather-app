package com.example.weatherforecast.common.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.weatherforecast.data.dao.CityWeatherDao;
import com.example.weatherforecast.data.dao.UserDao;
import com.example.weatherforecast.data.model.CityWeather;
import com.example.weatherforecast.data.model.User;

@Database(entities = {User.class, CityWeather.class},version = 3,exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract CityWeatherDao cityWeatherDao();

    private static volatile AppDatabase instance;
    public static AppDatabase getInstance(final Context context){
        if(instance==null){
            synchronized (AppDatabase.class){
                if(instance==null){
                    instance= Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "db_user_weather"
                    )
                            // 数据库更新时删除原有数据
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
