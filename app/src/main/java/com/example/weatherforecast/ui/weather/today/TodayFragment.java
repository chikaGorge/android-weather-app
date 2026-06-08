package com.example.weatherforecast.ui.weather.today;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.controller.viewModel.CityWeatherViewModel;
import com.example.weatherforecast.controller.factory.CityWeatherViewModelFactory;
import com.example.weatherforecast.data.model.CityWeather;

import java.util.ArrayList;
import java.util.List;

public class TodayFragment extends Fragment{
    private ExpandableListView expandableListView;
    private WeatherExpandableAdapter adapter;
    private List<CityWeather> cityWeatherList=new ArrayList<>();
    //创建viewModel
    private CityWeatherViewModel cityWeatherViewModel;
    public TodayFragment(){}
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        View rootView=inflater.inflate(R.layout.fragment_today,container,false);
        expandableListView=rootView.findViewById(R.id.expandableListView);
        adapter=new WeatherExpandableAdapter(getContext(),cityWeatherList,expandableListView);
        expandableListView.setAdapter(adapter);

        //获取viewModel实例
        cityWeatherViewModel=new ViewModelProvider(requireActivity(),
                new CityWeatherViewModelFactory(requireActivity())
        ).get(CityWeatherViewModel.class);
        Log.d("TodayFragment", "cityWeatherViewModel: " + (cityWeatherViewModel == null ? "null" : "initialized"));

        cityWeatherViewModel.getLikeLiveData().observe(requireActivity(), new Observer<List<CityWeather>>() {
            @Override
            public void onChanged(List<CityWeather> cityWeathers) {
                if (cityWeathers != null && !cityWeathers.isEmpty()) {
                    Log.d("TodayFragment", "Received city weather data: " + cityWeathers.size());
                    cityWeatherList.clear();
                    cityWeatherList.addAll(cityWeathers);
                    adapter.notifyDataSetChanged();  // 更新适配器
                } else {
                    Log.d("TodayFragment", "No city weather data received");
                }
            }
        });


        expandableListView.setOnGroupClickListener((parent, v, groupPosition, id) -> {
            // 切换 RecyclerView 的可见性
            View groupView = parent.getChildAt(groupPosition);
            RecyclerView recyclerView = groupView.findViewById(R.id.childRecyclerView);
            boolean isExpanded = recyclerView.getVisibility() == View.VISIBLE;
            recyclerView.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
            return true; // 阻止默认的展开/折叠行为
        });
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        // 强制刷新数据
        if (cityWeatherViewModel != null) {
            Log.d("TodayFragment", "Refreshing city weather data...");
            cityWeatherViewModel.forceRefreshLikeCities();
        }
    }

}