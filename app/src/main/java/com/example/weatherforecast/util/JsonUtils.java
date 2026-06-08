package com.example.weatherforecast.util;

import android.content.Context;
import android.util.JsonReader;

import com.example.weatherforecast.data.others.picker.CityForPicker;
import com.example.weatherforecast.data.others.picker.ProvinceForPicker;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
public class JsonUtils {
    public static List<ProvinceForPicker> loadCityDataForPicker(Context context, int rawResourceId) {
        List<ProvinceForPicker> provinceForPickerList = new ArrayList<>();
        try (InputStream is = context.getResources().openRawResource(rawResourceId);
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             JsonReader reader = new JsonReader(isr)) {

            Gson gson = new Gson();
            reader.beginArray();
            while (reader.hasNext()) {
                reader.beginObject();
                ProvinceForPicker province = new ProvinceForPicker();

                while (reader.hasNext()) {
                    String name = reader.nextName();
                    if ("province".equals(name)) {
                        province.setProvince(reader.nextString());
                    } else if ("cities".equals(name)) {
                        province.setCities(parseCities(reader));
                    }
                }
                reader.endObject();
                provinceForPickerList.add(province);
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return provinceForPickerList;
    }

    private static List<CityForPicker> parseCities(JsonReader reader) throws IOException {
        List<CityForPicker> cities = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            CityForPicker city = new CityForPicker();
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if ("city".equals(name)) {
                    city.setCity(reader.nextString());
                } else if ("districts".equals(name)) {
                    city.setDistricts(parseDistricts(reader));
                }
            }
            reader.endObject();
            cities.add(city);
        }
        reader.endArray();
        return cities;
    }

    private static List<String> parseDistricts(JsonReader reader) throws IOException {
        List<String> districts = new ArrayList<>();
        reader.beginArray();
        while (reader.hasNext()) {
            districts.add(reader.nextString());
        }
        reader.endArray();
        return districts;
    }


}
