package com.example.weatherforecast.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "city_weather",
    indices = {@Index("user_id")},
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "id",
                childColumns = "user_id",
                onDelete = ForeignKey.CASCADE
        )
)
public class CityWeather {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String city;
    private String province;
    private String adcode;
    private String weather;
    private int weatherIcon;
    private int user_id;
    private String temperature;
    private String winddirection;
    private String windpower;
    private String humidity;
    private String reportTime;

    public CityWeather(){}

    @Ignore
    public CityWeather(String city,int user_id,String province,String adcode, String weather, int weatherIcon, String temperature,String winddirection, String windpower, String humidity, String reportTime) {
        this.city = city;
        this.user_id=user_id;
        this.province=province;
        this.adcode=adcode;
        this.weather = weather;
        this.weatherIcon = weatherIcon;
        this.temperature = temperature;
        this.winddirection=winddirection;
        this.windpower = windpower;
        this.humidity = humidity;
        this.reportTime = reportTime;
    }

    public String getCity() {
        return city;
    }

    public String getWeather() {
        return weather;
    }

    public String getProvince() {
        return province;
    }

    public String getAdcode() {
        return adcode;
    }
    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }
    public String getWinddirection() {
        return winddirection;
    }

    public int getWeatherIcon() {
        return weatherIcon;
    }

    public String getTemperature() {
        return temperature;
    }

    public String getWindpower() {
        return windpower;
    }

    public String getHumidity() {
        return humidity;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public void setWeatherIcon(int weatherIcon) {
        this.weatherIcon = weatherIcon;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setWinddirection(String winddirection) {
        this.winddirection = winddirection;
    }

    public void setWindpower(String windpower) {
        this.windpower = windpower;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }
}
