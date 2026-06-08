package com.example.weatherforecast.util;

import android.graphics.Color;
import android.util.Log;

import com.example.weatherforecast.data.model.CityWeather;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.BubbleChartData;
import lecho.lib.hellocharts.model.BubbleValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.ComboLineColumnChartData;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SubcolumnValue;

public class ChartUtils {
    //创建气温的折线柱形组合图
    public static ComboLineColumnChartData setupTemperatureComboChart(List<CityWeather> cityWeatherList){
        //创建组合数据
        ComboLineColumnChartData data=new ComboLineColumnChartData();

        //创建柱状图数据
        List<Column> columns=new ArrayList<>();
        List<Line> lines=new ArrayList<>();
        List<AxisValue> axisValues=new ArrayList<>();
        //存储所有数据点的列表
        List<PointValue> pointValues=new ArrayList<>();
        //设置柱状图和折线图数据
        for(int i=0;i<cityWeatherList.size();i++){
            CityWeather cityWeather=cityWeatherList.get(i);
            //设置柱状图（显示温度）
            try{
                //添加柱子列表
                float temperature =Float.parseFloat(cityWeather.getTemperature());
                SubcolumnValue subcolumnValue=new SubcolumnValue(temperature);
                subcolumnValue.setLabel(cityWeather.getTemperature());
                Column column=new Column(Arrays.asList(subcolumnValue));
                columns.add(column);
                //添加数据点到折线图
                pointValues.add(new PointValue(i,temperature));
                //设置x轴标签
                axisValues.add(new AxisValue(i).setLabel(cityWeather.getCity()));
            }catch (NumberFormatException e){
                Log.e("WeatherChartFragment", "Invalid temperature value: " + cityWeather.getTemperature());
            }
        }
        //创建折线图数据
        Line line=new Line(pointValues).setColor(Color.BLUE).setCubic(false);
        lines.add(line);
        //设置柱状图数据
        ColumnChartData columnChartData=new ColumnChartData(columns);
        data.setColumnChartData(columnChartData);
        //设置折线图数据
        LineChartData lineChartData=new LineChartData(lines);
        data.setLineChartData(lineChartData);
        //创建x，y轴
        Axis axisX=new Axis(axisValues)
                .setName("城市")
                .setHasTiltedLabels(true)
                .setTextColor(Color.BLACK);
        data.setAxisXBottom(axisX);
        Axis axisY=new Axis()
                .setName("温度 (°C)")
                .setTextColor(Color.BLACK);
        data.setAxisYLeft(axisY);

        return data;
    }
    //创建空气湿度的柱状图
    public static ColumnChartData setupHumidityColumnChart(List<CityWeather> cityWeatherList){
        ColumnChartData data=new ColumnChartData();

        List<Column> columns=new ArrayList<>();
        List<AxisValue> axisValues=new ArrayList<>();
        for (int i=0;i<cityWeatherList.size();i++){
            CityWeather cityWeather=cityWeatherList.get(i);
            try {
                float humidity=Float.parseFloat(cityWeather.getHumidity());
                SubcolumnValue subcolumnValue=new SubcolumnValue(humidity);
                subcolumnValue.setLabel(cityWeather.getHumidity());
                Column column=new Column(Arrays.asList(subcolumnValue));
                columns.add(column);
                axisValues.add(new AxisValue(i).setLabel(cityWeather.getCity()));
            }catch (NumberFormatException e){
                Log.e("WeatherChartFragment", "Invalid humidity value: " + cityWeather.getHumidity());
            }
        }
        data.setColumns(columns);
        Axis axisX = new Axis(axisValues)
                .setName("城市")
                .setHasTiltedLabels(true)
                .setTextColor(Color.BLACK);
        data.setAxisXBottom(axisX);
        Axis axisY = new Axis()
                .setName("湿度 (%)")
                .setTextColor(Color.BLACK);
        data.setAxisYLeft(axisY);

        return data;
    }
    //创建风力/风向的泡泡图
    public static BubbleChartData setupWindBubbleChart(List<CityWeather> cityWeatherList){
        BubbleChartData data=new BubbleChartData();

        List<BubbleValue> pointValues=new ArrayList<>();
        List<AxisValue> axisValues=new ArrayList<>();
        //固定的风向顺序
        List<String> orderedWindDirections=Arrays.asList(
                "东", "南", "西", "北", "东南", "东北", "西南", "西北", "无风向", "旋转不定"
        );
        //创建y轴标签
        List<AxisValue> yAxisValues=new ArrayList<>();
        for (int i=0;i<orderedWindDirections.size();i++){
            AxisValue axisValue=new AxisValue(i);
            axisValue.setLabel(orderedWindDirections.get(i));
            yAxisValues.add(axisValue);
        }
        //遍历城市数据并生成气泡图的点值
        for (int i=0;i<cityWeatherList.size();i++){
            CityWeather cityWeather=cityWeatherList.get(i);
            String windDirection=cityWeather.getWinddirection();
            try{
                //获取风力
                float windPower=adjustWindPower(cityWeather);
                int windDirectionIndex=orderedWindDirections.indexOf(windDirection);
                if(windDirectionIndex==-1){
                    windDirectionIndex=orderedWindDirections.indexOf("无风向");
                }
                //创建气泡数据
                BubbleValue value=new BubbleValue(i,windDirectionIndex,windPower,Color.GRAY);
                value.setLabel(cityWeather.getWindpower());
                pointValues.add(value);
                //设置x标签
                axisValues.add(new AxisValue(i).setLabel(cityWeather.getCity()));
            }catch (NumberFormatException e){
                Log.e("WeatherChartFragment", "Invalid wind value: " + cityWeather.getWindpower());
            }
        }
        data.setValues(pointValues);
        //设置标签显示
        data.setHasLabels(true);
        // 设置X轴
        Axis axisX = new Axis(axisValues)
                .setName("城市")
                .setTextColor(Color.BLACK)
                .setHasLines(true)  // 启用X轴上的表格线
                .setHasTiltedLabels(true)
                .setLineColor(Color.GRAY);
        data.setAxisXBottom(axisX);
        // 设置Y轴，添加风向标签
        Axis axisY = new Axis();
        axisY.setName("风向")
                .setTextColor(Color.BLACK)
                .setHasLines(true)  // 启用Y轴上的表格线
                .setHasTiltedLabels(true)
                .setLineColor(Color.GRAY);
        // 设置Y轴的标签
        axisY.setValues(yAxisValues);
        data.setAxisYLeft(axisY);

        return data;
    }
    private static float adjustWindPower(CityWeather cityWeather){
        if(cityWeather.getWindpower().equals("≤3")){
            return 3;
        }else {
            return Float.parseFloat(cityWeather.getWindpower());
        }
    }
}
