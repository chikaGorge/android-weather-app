package com.example.weatherforecast.controller.factory;

import android.content.Context;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.weatherforecast.controller.viewModel.CityWeatherViewModel;

public class CityWeatherViewModelFactory implements ViewModelProvider.Factory{
    private Context context;
    public CityWeatherViewModelFactory(Context context){
        this.context=context;
    }
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new CityWeatherViewModel(context);
    }
}
