package com.example.weatherforecast.data.others.picker;

import java.util.List;

public class ProvinceForPicker {
    private String province;
    private List<CityForPicker> cities;

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public List<CityForPicker> getCities() {
        return cities;
    }

    public void setCities(List<CityForPicker> cities) {
        this.cities = cities;
    }
}
