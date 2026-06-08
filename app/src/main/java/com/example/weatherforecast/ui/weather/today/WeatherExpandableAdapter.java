package com.example.weatherforecast.ui.weather.today;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.data.model.CityWeather;
import com.example.weatherforecast.data.others.weather.WeatherDetail;

import java.util.ArrayList;
import java.util.List;

public class WeatherExpandableAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<CityWeather> cityWeatherList;
    private CityWeatherAdapter cityWeatherAdapter;
    private WeatherDetailsAdapter weatherDetailsAdapter;
    private ExpandableListView expandableListView;

    public WeatherExpandableAdapter(Context context, List<CityWeather> cityWeatherList, ExpandableListView expandableListView) {
        this.context = context;
        this.cityWeatherList = cityWeatherList;
        this.expandableListView = expandableListView; // 存储 ExpandableListView 的引用
        this.cityWeatherAdapter = new CityWeatherAdapter(cityWeatherList, null);
    }


    @Override
    public int getGroupCount() {
        return cityWeatherList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        // 每个分组只有一个子项（RecyclerView）
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return cityWeatherList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        // 子项不存储数据，我们只返回null
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        CityWeather cityWeather = (CityWeather) getGroup(groupPosition);

        CityWeatherAdapter.ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_city_weather, parent, false);
            holder = new CityWeatherAdapter.ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (CityWeatherAdapter.ViewHolder) convertView.getTag();
        }

        // 绑定数据
        cityWeatherAdapter.onBindViewHolder(holder, groupPosition);

        // 处理点击事件
        convertView.setOnClickListener(v -> {
            boolean isExpandedNow = expandableListView.isGroupExpanded(groupPosition);
            if (isExpandedNow) {
                expandableListView.collapseGroup(groupPosition);
            } else {
                expandableListView.expandGroup(groupPosition);
            }
        });

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.child_item, parent, false);
        }

        RecyclerView recyclerView = convertView.findViewById(R.id.childRecyclerView);
        if (recyclerView.getLayoutManager() == null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }

        List<WeatherDetail> weatherDetails = getWeatherDetailsForCity(groupPosition);
        weatherDetailsAdapter = new WeatherDetailsAdapter(weatherDetails);
        recyclerView.setAdapter(weatherDetailsAdapter);

        // 设置 RecyclerView 的可见性
        boolean isExpanded = expandableListView.isGroupExpanded(groupPosition);
        recyclerView.setVisibility(isExpanded ? View.VISIBLE : View.GONE);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


    private List<WeatherDetail> getWeatherDetailsForCity(int groupPosition){
        CityWeather cityWeather=cityWeatherList.get(groupPosition);
        List<WeatherDetail> weatherDetails=new ArrayList<>();

        weatherDetails.add(new WeatherDetail("城市", cityWeather.getCity()+"-"+cityWeather.getProvince()+" "+cityWeather.getAdcode()));
        weatherDetails.add(new WeatherDetail("天气状况", cityWeather.getWeather()));
        weatherDetails.add(new WeatherDetail("气温", cityWeather.getTemperature() + "°C"));
        weatherDetails.add(new WeatherDetail("风力/风向", cityWeather.getWindpower() + "/" + cityWeather.getWinddirection()));
        weatherDetails.add(new WeatherDetail("空气湿度", cityWeather.getHumidity() + "%"));
        weatherDetails.add(new WeatherDetail("查询时间", cityWeather.getReportTime()));


        return weatherDetails;
    }
}
