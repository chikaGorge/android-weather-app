package com.example.weatherforecast.ui.settings;

import static com.example.weatherforecast.ui.settings.CustomUserPreference.REQUEST_CODE_PICK_IMAGE;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreference;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.weatherforecast.R;
import com.example.weatherforecast.controller.GetWeatherController;
import com.example.weatherforecast.controller.viewModel.CityWeatherViewModel;
import com.example.weatherforecast.controller.factory.CityWeatherViewModelFactory;
import com.example.weatherforecast.controller.viewModel.UserViewModel;
import com.example.weatherforecast.controller.factory.UserViewModelFactory;
import com.example.weatherforecast.data.model.CityWeather;
import com.example.weatherforecast.data.model.User;
import com.example.weatherforecast.ui.MainActivity;

import android.view.View;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragmentCompat {

    private CustomUserPreference userInfoPref;
    private User user;
    private static final int REQUEST_CODE_PICK_IMAGE = 1001;

    // 创建 ViewModel
    private CityWeatherViewModel cityWeatherViewModel;
    private UserViewModel userViewModel;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
//        super.onCreatePreferences(savedInstanceState, rootKey);
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences= getPreferenceManager().getSharedPreferences();

        // 获取 ViewModel 实例
        cityWeatherViewModel = new ViewModelProvider(requireActivity(), new CityWeatherViewModelFactory(requireActivity()))
                .get(CityWeatherViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity(), new UserViewModelFactory(requireActivity()))
                .get(UserViewModel.class);

        // 确保 userInfoPref 已初始化
        userInfoPref = findPreference("user_profile");
        if (userInfoPref == null) {
            Log.e("SettingsFragment", "userInfoPref is null");
            return;  // 如果 userInfoPref 是 null，直接返回
        }
        userInfoPref.setParentFragment(this);
        // 设置头像和用户信息更新监听器
        userInfoPref.setOnAvatarChangedListener(new CustomUserPreference.OnAvatarChangedListener() {

            @Override
            public void onAvatarChanged(Uri imageUri) {
                String headerPath = imageUri.toString();
                if (user != null) {
                    user.setHeadPath(headerPath);
                    userViewModel.updateUser(user);
                }
            }

            @Override
            public void onUsernameChanged(String newUsername) {
                if (user != null) {
                    user.setUsername(newUsername);
                    userViewModel.updateUser(user);
                }
            }

            @Override
            public void onPasswordChanged(String newPassword) {
                if (user != null) {
                    user.setPassword(newPassword);
                    userViewModel.updateUser(user);
                }
            }
        });

        // 用户信息加载
        userViewModel.getUser().observe(getViewLifecycleOwner(), user1 -> {
            if (user1 != null && userInfoPref != null) {
                Log.d("SettingsFragment", "usernameForViewModel:" + user1.getUsername());
                user = user1;
                Log.d("SettingsFragment", "usernameAfterRefresh:" + user.getUsername());

                userInfoPref.setUsernameText(user1.getUsername() != null ? user1.getUsername() : "");
                userInfoPref.setPasswordText(user1.getPassword() != null ? user1.getPassword() : "");

                if (user1.getHeadPath() != null) {
                    Uri headUri = Uri.parse(user1.getHeadPath());
                    userInfoPref.setAvatarUri(headUri);
                } else {
                    userInfoPref.setAvatarDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_weather_unknow));
                }
            }
        });

        // 设置默认城市相关逻辑
        setupDefaultCity(sharedPreferences);
        setupInterestedCity(sharedPreferences);
        setupHistoryCityCount(sharedPreferences);
        setupAutomaticLogin();
    }

    @Override
    public void onStart() {
        super.onStart();

        // 强制刷新用户数据
        if (user == null && userViewModel != null) {
            userViewModel.refreshUserData();
        }
    }

    private void setupDefaultCity(SharedPreferences sharedPreferences) {
        final Preference cityPickerPref = findPreference("default_city_picker");
        if (cityPickerPref != null) {
            // 检查是否已有值，没有则设置动态默认值
            if (!sharedPreferences.contains("default_city_picker")) {
                String defaultCity = "浙江省 - 杭州市 - 余杭区"; // 动态默认值
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("city_picker", defaultCity);
                editor.apply();
                cityPickerPref.setSummary(defaultCity);
            } else {
                // 加载已保存的值
                String savedCity = sharedPreferences.getString("default_city_picker", "未选择城市");
                cityPickerPref.setSummary(savedCity);
            }

            cityPickerPref.setOnPreferenceClickListener(preference -> {
                if(user==null) return false;
                CityPickerBottomSheet picker = new CityPickerBottomSheet(
                        requireContext(),
                        (province, city, district) -> {
                            String result = province + " - " + city + " - " + district;

                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("default_city_picker", result);
                            editor.apply();

                            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
                            cityPickerPref.setSummary(result);

                            // 修改用户感兴趣的城市
                            String defaultCity=province + "-" + city + "-" + district;
                            user.setDefaultCity(result);
                            userViewModel.updateUser(user);
                            userViewModel.updateDefaultCity(user,defaultCity);
                            //更新天气
                            GetWeatherController getWeatherController = new GetWeatherController();
                            // 获取城市天气并保存
                            getWeatherController.getCityWeather(defaultCity, user.getId(), new GetWeatherController.WeatherCallback() {
                                @Override
                                public void onSuccess(CityWeather cityWeather) {
                                    // 将获取的天气数据保存到数据库
                                    cityWeatherViewModel.addLikeCity(user.getId(),user.getDefaultCity(),cityWeather);
                                }

                                @Override
                                public void onError(String error) {
                                    // 处理错误情况
                                    Log.e("SomeFragment", "Error fetching weather: " + error);
                                }
                            });
                        }
                );
                picker.show(getParentFragmentManager(), "CityPicker");
                return true;
            });
        }
    }

    private void setupInterestedCity(SharedPreferences sharedPreferences) {
        final Preference interestedCityPref = findPreference("interested_city");
        if (interestedCityPref != null) {
            interestedCityPref.setSummary("点击添加感兴趣的城市");
            interestedCityPref.setOnPreferenceClickListener(preference -> {
                if(user==null) return false;

                CityPickerBottomSheet picker = new CityPickerBottomSheet(
                        requireContext(),
                        (province, city, district) -> {
                            String result = province + " - " + city + " - " + district;
                            Toast.makeText(requireContext(), result, Toast.LENGTH_SHORT).show();
                            interestedCityPref.setSummary(result);

                            // 添加感兴趣的城市
                            String addCity = province +"-"+ city +"-"+ district;
                            GetWeatherController getWeatherController = new GetWeatherController();
                            // 获取城市天气并保存
                            getWeatherController.getCityWeather(addCity, user.getId(), new GetWeatherController.WeatherCallback() {
                                @Override
                                public void onSuccess(CityWeather cityWeather) {
                                    // 将获取的天气数据保存到数据库
                                    cityWeatherViewModel.addLikeCity(user.getId(),user.getDefaultCity(),cityWeather);
                                }

                                @Override
                                public void onError(String error) {
                                    // 处理错误情况
                                    Log.e("SomeFragment", "Error fetching weather: " + error);
                                }
                            });
                        }
                );
                picker.show(getParentFragmentManager(), "CityPicker");
                return true;
            });
        }
    }

    private void setupHistoryCityCount(SharedPreferences sharedPreferences) {
        final EditTextPreference historyCityCountPref = findPreference("history_city_count");
        if (historyCityCountPref != null) {
            // 检查是否有值，没有则设置动态默认值
            if (!sharedPreferences.contains("history_city_count")) {
                String defaultCount = "3";
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("history_count", defaultCount);
                editor.apply();
                historyCityCountPref.setSummary("历史城市的显示数量：" + defaultCount);
            } else {
                // 加载保存的值
                String savedCount = sharedPreferences.getString("history_count", "3");
                historyCityCountPref.setSummary("历史城市的显示数量：" + savedCount);
            }

            // 监听 Preference 的变化
            historyCityCountPref.setOnPreferenceChangeListener((preference, newValue) -> {
                // newValue 是用户输入的新值
                String input = newValue.toString();
                if (!input.matches("\\d+")) {
                    Toast.makeText(getContext(), "请输入正整数", Toast.LENGTH_SHORT).show();
                    return false;
                }
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("history_count", input);
                editor.apply();

                // 更新历史列表
                int column = Integer.parseInt(input);
                cityWeatherViewModel.checkHistoryCity(user.getId(), column);

                // 更新 summary 以显示新值
                historyCityCountPref.setSummary("历史城市的显示数量：" + input);
                return true;  // 返回 true 以保存新的 Preference 值
            });
        }
    }
    private void setupAutomaticLogin() {
        SwitchPreference automaticLogonPref = findPreference("automatic_logon_enabled");

        SharedPreferences sharedPreferences=getActivity().getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        if (automaticLogonPref != null) {
            // 设置SwitchPreference的监听器
            automaticLogonPref.setOnPreferenceChangeListener((preference, newValue) -> {
                if (user == null) {
                    // 给出提示信息
                    Toast.makeText(getContext(), "无法启用自动登录，因为用户信息未加载", Toast.LENGTH_SHORT).show();
                    return false;  // 取消切换开关的操作
                }
                boolean isEnabled = (Boolean) newValue;
                if(isEnabled){
                    Toast.makeText(getContext(), "启用自动登陆", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "取消自动登陆", Toast.LENGTH_SHORT).show();
                }
                // 存储用户的自动登录设置
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("automatic_logon_enabled", isEnabled);

                // 如果开启了自动登录，执行登录操作
                if (isEnabled) {
                    // 检查字段是否为 null，如果是，使用空字符串（可以改为 null 来避免空字符串）
                    editor.putInt("id", user.getId());
                    Log.d("SettingsFragment","user is put in app_preferences");
                } else {
                    // 如果禁用自动登录，清除相关信息
                    editor.remove("id");
                    Log.d("SettingsFragment","user is remove in app_preferences");
                }
                editor.apply();
                return true;
            });

            // 设置默认值（如果没有设置过）
            boolean isAutoLogonEnabled = sharedPreferences.getBoolean("automatic_logon_enabled", false);
            automaticLogonPref.setChecked(isAutoLogonEnabled);

        }
    }


    // 更新头像的方法，由 MainActivity 调用
    public void updateAvatar(Uri imageUri) {
        // 更新用户头像
        if (userInfoPref != null) {
            userInfoPref.setAvatarUri(imageUri);  // 更新界面上的头像
        }

        // 更新数据库中的用户信息
        if (user != null) {
            user.setHeadPath(imageUri.toString());
            userViewModel.updateUser(user);  // 保存头像路径到数据库
        }
    }
    //处理头像选择结果
    public void openImagePickerInFragment() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickIntent, REQUEST_CODE_PICK_IMAGE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // 判断是否是我们发起的相册请求
        if (requestCode == REQUEST_CODE_PICK_IMAGE
                && resultCode == Activity.RESULT_OK
                && data != null) {
            Uri imageUri = data.getData();

            // 更新头像 UI
            if (userInfoPref != null) {
                userInfoPref.setAvatarUri(imageUri);
            }
            // 更新数据库
            if (user != null) {
                user.setHeadPath(imageUri.toString());
                userViewModel.updateUser(user);
            }
        }
    }
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        // 清理资源，防止内存泄漏
//        userViewModel.getUser().removeObservers(getViewLifecycleOwner());
//    }
}
