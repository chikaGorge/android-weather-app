package com.example.weatherforecast.ui.history;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.view.BubbleChartView;
import lecho.lib.hellocharts.view.ColumnChartView;
import lecho.lib.hellocharts.view.ComboLineColumnChartView;

import com.example.weatherforecast.R;
import com.example.weatherforecast.controller.viewModel.CityWeatherViewModel;
import com.example.weatherforecast.controller.factory.CityWeatherViewModelFactory;
import com.example.weatherforecast.data.model.CityWeather;
import com.example.weatherforecast.util.ChartUtils;
public class HistoryFragment extends Fragment {
    private TableLayout tableCities;
    private ComboLineColumnChartView comboLineColumnChartTemperature;
    private ColumnChartView columnChartView;
    private BubbleChartView bubbleChartWind;
    private CityWeatherViewModel cityWeatherViewModel;
    private List<CityWeather> cityWeatherList;

    // 创建一个固定大小的线程池来执行后台任务
    private ExecutorService executorService = Executors.newFixedThreadPool(4);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        tableCities = view.findViewById(R.id.table_cities);
        comboLineColumnChartTemperature = view.findViewById(R.id.combo_chart_temperature);
        columnChartView = view.findViewById(R.id.column_chart_humidity);
        bubbleChartWind = view.findViewById(R.id.bubble_chart_wind);

        // 获取ViewModel 实例
        cityWeatherViewModel=new ViewModelProvider(requireActivity(),
                new CityWeatherViewModelFactory(requireActivity())
                ).get(CityWeatherViewModel.class);
        Log.d("HistoryFragment", "cityWeatherViewModel: " + (cityWeatherViewModel == null ? "null" : "initialized"));


        // 观察 cityWeatherList 变化，更新视图
        cityWeatherViewModel.getHistoryLiveData().observe(requireActivity(), new Observer<List<CityWeather>>() {
            @Override
            public void onChanged(List<CityWeather> cityWeathers) {
                if (cityWeathers != null && cityWeathers.size() > 0) {
                    cityWeatherList = cityWeathers;
                    // 使用线程池异步更新 UI
                    updateUI();
                }
            }
        });

        return view;
    }

    // 更新 UI 方法
    private void updateUI() {
        // 确保线程池未关闭
        if (executorService != null && !executorService.isShutdown()) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    // 在后台线程中处理图表数据
                    ComboLineColumnChartData comboLineColumnChartData = ChartUtils.setupTemperatureComboChart(cityWeatherList);
                    BubbleChartData bubbleChartData = ChartUtils.setupWindBubbleChart(cityWeatherList);
                    ColumnChartData humidityColumnChartData = ChartUtils.setupHumidityColumnChart(cityWeatherList);

                    // 在主线程中更新 UI
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            // 更新图表
                            comboLineColumnChartTemperature.setComboLineColumnChartData(comboLineColumnChartData);
                            bubbleChartWind.setBubbleChartData(bubbleChartData);
                            columnChartView.setColumnChartData(humidityColumnChartData);

                            // 更新表格
                            updateTable(cityWeatherList);
                        }
                    });
                }
            });
        } else {
            Log.e("HistoryFragment", "ExecutorService is already shut down, cannot submit task.");
        }
    }



    // 更新表格的方法
    private void updateTable(List<CityWeather> cityWeatherList) {
        // 清空现有表格内容
        tableCities.removeAllViews();

        // 动态添加表格行和列
        for (CityWeather cityWeather : cityWeatherList) {
            TableRow row = new TableRow(getContext());

            TextView cityTextView = new TextView(getContext());
            cityTextView.setText(cityWeather.getCity());
            row.addView(cityTextView);

            TextView weatherTextView = new TextView(getContext());
            weatherTextView.setText(cityWeather.getWeather());
            row.addView(weatherTextView);

            TextView timeTextView = new TextView(getContext());
            timeTextView.setText(cityWeather.getReportTime());
            row.addView(timeTextView);

            tableCities.addView(row);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 取消所有正在进行的任务
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdownNow();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        // 在 onResume 中强制刷新数据
        if(cityWeatherViewModel!=null) cityWeatherViewModel.forceRefreshHistoryCities();
    }
}