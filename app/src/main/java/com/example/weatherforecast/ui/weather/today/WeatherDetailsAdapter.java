package com.example.weatherforecast.ui.weather.today;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.data.others.weather.WeatherDetail;

import java.util.List;

public class WeatherDetailsAdapter extends RecyclerView.Adapter<WeatherDetailsAdapter.ViewHolder> {

    private List<WeatherDetail> weatherDetails;

    public WeatherDetailsAdapter(List<WeatherDetail> weatherDetails) {
        this.weatherDetails = weatherDetails;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_weather_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        WeatherDetail detail = weatherDetails.get(position);
        holder.tvLabel.setText(detail.getLabel());
        holder.tvValue.setText(detail.getValue());
    }

    @Override
    public int getItemCount() {
        return weatherDetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvValue;

        public ViewHolder(View itemView) {
            super(itemView);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            tvValue = itemView.findViewById(R.id.tvValue);
        }
    }
}

