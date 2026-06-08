package com.example.weatherforecast.ui.weather.today;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.data.model.CityWeather;

import java.util.List;
public class CityWeatherAdapter extends RecyclerView.Adapter<CityWeatherAdapter.ViewHolder> {

    private List<CityWeather> cityWeatherList;
    private OnCityClickListener listener;

    public interface OnCityClickListener {
        void onCityClick(CityWeather cityWeather);
    }

    public CityWeatherAdapter(List<CityWeather> cityWeatherList, OnCityClickListener listener) {
        this.cityWeatherList = cityWeatherList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_weather, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CityWeather cityWeather = cityWeatherList.get(position);
        holder.tvCityName.setText(cityWeather.getCity());
        holder.tvWeather.setText(cityWeather.getWeather());
        holder.ivWeatherIcon.setImageResource(cityWeather.getWeatherIcon());

        // 点击事件，传递城市数据到回调方法
        holder.itemView.setOnClickListener(v -> listener.onCityClick(cityWeather));
    }

    @Override
    public int getItemCount() {
        return cityWeatherList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCityName, tvWeather;
        ImageView ivWeatherIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCityName = itemView.findViewById(R.id.tvCityName);
            tvWeather = itemView.findViewById(R.id.tvWeather);
            ivWeatherIcon = itemView.findViewById(R.id.ivWeatherIcon);
        }
    }
}

