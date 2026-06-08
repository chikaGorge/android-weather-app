package com.example.weatherforecast.data.others.picker;

import java.util.List;

public class CityForPicker {
    private String city;
    private List<String> districts;

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<String> getDistricts() {
        return districts;
    }

    public void setDistricts(List<String> districts) {
        this.districts = districts;
    }
}