package com.example.weatherforecast.ui.settings;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.JsonReader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.weatherforecast.R;
import com.example.weatherforecast.controller.CityPickerController;
import com.example.weatherforecast.data.others.picker.CityForPicker;
import com.example.weatherforecast.data.others.picker.ProvinceForPicker;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
public class CityPickerBottomSheet extends BottomSheetDialogFragment {
    private NumberPicker npProvince, npCity, npDistrict;
    private Button btnConfirm;
    private OnCitySelectedListener listener;
    private CityPickerController controller;
    private List<ProvinceForPicker> provinceForPickerList = new ArrayList<>();
    private String selectedProvince, selectedCity, selectedDistrict;

    public CityPickerBottomSheet(Context context, OnCitySelectedListener listener) {
        this.listener = listener;
        this.controller = new CityPickerController();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_city_picker, container, false);

        npProvince = view.findViewById(R.id.np_province);
        npCity = view.findViewById(R.id.np_city);
        npDistrict = view.findViewById(R.id.np_district);
        btnConfirm = view.findViewById(R.id.btn_confirm);

        // 异步加载数据
        controller.loadCityDataAsync(requireContext(), R.raw.city_picker_data, data -> {
            provinceForPickerList = data;

            // 更新 UI
            String[] provinceNames = new String[provinceForPickerList.size()];
            for (int i = 0; i < provinceForPickerList.size(); i++) {
                provinceNames[i] = provinceForPickerList.get(i).getProvince();
            }

            npProvince.setDisplayedValues(provinceNames);
            npProvince.setMinValue(0);
            npProvince.setMaxValue(provinceNames.length - 1);

            // 默认选择第一个省
            updateCityList(0);
        });

        // 设置滚轮监听器
        npProvince.setOnValueChangedListener((picker, oldVal, newVal) -> updateCityList(newVal));
        npCity.setOnValueChangedListener((picker, oldVal, newVal) -> updateDistrictList(newVal));

        // 确认按钮点击
        btnConfirm.setOnClickListener(v -> {
            if (provinceForPickerList.isEmpty()) return;

            selectedProvince = provinceForPickerList.get(npProvince.getValue()).getProvince();
            selectedCity = provinceForPickerList.get(npProvince.getValue()).getCities().get(npCity.getValue()).getCity();
            selectedDistrict = provinceForPickerList.get(npProvince.getValue()).getCities().get(npCity.getValue()).getDistricts().get(npDistrict.getValue());

            if (listener != null) {
                listener.onCitySelected(selectedProvince, selectedCity, selectedDistrict);
            }
            dismiss();
        });

        return view;
    }

    private void updateCityList(int provinceIndex) {
        npCity.setEnabled(false);
        npDistrict.setEnabled(false);

        try {
            List<CityForPicker> cities = provinceForPickerList.get(provinceIndex).getCities();
            if (cities == null || cities.isEmpty()) return;

            String[] cityNames = new String[cities.size()];
            for (int i = 0; i < cities.size(); i++) {
                cityNames[i] = cities.get(i).getCity();
            }

            npCity.setDisplayedValues(null);
            npCity.setMinValue(0);
            npCity.setMaxValue(cityNames.length - 1);
            npCity.setDisplayedValues(cityNames);

            updateDistrictList(0);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            npCity.setEnabled(true);
            npDistrict.setEnabled(true);
        }
    }

    private void updateDistrictList(int cityIndex) {
        npDistrict.setEnabled(false);

        try {
            int provinceIndex = npProvince.getValue();
            List<CityForPicker> cities = provinceForPickerList.get(provinceIndex).getCities();
            if (cities == null || cityIndex < 0 || cityIndex >= cities.size()) return;

            List<String> districts = cities.get(cityIndex).getDistricts();
            if (districts == null || districts.isEmpty()) return;

            String[] districtNames = districts.toArray(new String[0]);

            npDistrict.setDisplayedValues(null);
            npDistrict.setMinValue(0);
            npDistrict.setMaxValue(districtNames.length - 1);
            npDistrict.setDisplayedValues(districtNames);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            npDistrict.setEnabled(true);
        }
    }

    public interface OnCitySelectedListener {
        void onCitySelected(String province, String city, String district);
    }
}
