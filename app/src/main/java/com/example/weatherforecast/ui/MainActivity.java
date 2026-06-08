package com.example.weatherforecast.ui;

import static com.amap.api.maps.model.BitmapDescriptorFactory.getContext;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.example.weatherforecast.R;
import com.example.weatherforecast.common.db.AppDatabase;
import com.example.weatherforecast.controller.GetLocationController;
import com.example.weatherforecast.controller.GetWeatherController;
import com.example.weatherforecast.controller.viewModel.CityWeatherViewModel;
import com.example.weatherforecast.controller.factory.CityWeatherViewModelFactory;
import com.example.weatherforecast.controller.viewModel.UserViewModel;
import com.example.weatherforecast.controller.factory.UserViewModelFactory;
import com.example.weatherforecast.data.model.CityWeather;
import com.example.weatherforecast.data.model.User;
import com.example.weatherforecast.data.repository.CityWeatherRepository;
import com.example.weatherforecast.data.repository.UserRepository;
import com.example.weatherforecast.ui.settings.CustomUserPreference;
import com.example.weatherforecast.ui.settings.SettingsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    private NavController loginNavController;
    private NavController mainNavController;

    private UserRepository userRepository;
    private CityWeatherRepository cityWeatherRepository;

    private UserViewModel userViewModel;
    private CityWeatherViewModel cityWeatherViewModel;

    boolean isUserLoggedIn = false;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取底部导航栏和两个 NavHostFragment
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        loginNavController = Navigation.findNavController(this, R.id.login_nav_host_fragment);
        mainNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        // 初始时主界面的 NavHostFragment 和底部导航栏不可见
        findViewById(R.id.nav_host_fragment).setVisibility(View.GONE);
        findViewById(R.id.bottom_navigation).setVisibility(View.GONE);
        // 创建数据库
        AppDatabase database = AppDatabase.getInstance(getApplicationContext());
        // 检查数据库是否成功初始化
        if (database == null) {
            Log.e("MainActivity", "onCreate: database is null");
            return;  // 如果数据库为 null，直接返回
        }
        // 初始化仓库
        Application application = this.getApplication();
        userRepository = new UserRepository(application);
        cityWeatherRepository = new CityWeatherRepository(application);
        // 检查仓库是否正确初始化
        if (userRepository == null) {
            Log.e("MainActivity", "onCreate: userRepository is null");
            return;
        }
        if (cityWeatherRepository == null) {
            Log.e("MainActivity", "onCreate: cityWeatherRepository is null");
            return;
        }
        // 创建 ViewModelFactory 并初始化 ViewModel
        // 初始化 ViewModel
        userViewModel = new ViewModelProvider(this, new UserViewModelFactory(getContext())).get(UserViewModel.class);
        cityWeatherViewModel = new ViewModelProvider(this, new CityWeatherViewModelFactory(getContext())).get(CityWeatherViewModel.class);
//        checkUserLoggedIn();

    }


    //手动设置登陆成功（用于测试）
    public void loginSuccess(Boolean isLoginIn,User user){
        if(isUserLoggedIn==true)return;
        isUserLoggedIn = isLoginIn;
        this.user=user;
        if(user!=null&&userViewModel!=null){
            Log.d("MainActivity","userid="+user.getId());
            userViewModel.setUser(user);
        }
        checkUserLoggedIn();
    }
    // 检查用户是否已登录
    private void checkUserLoggedIn() {
        if (isUserLoggedIn&&user != null) {
            Log.d("MainActivity", "user is " + (user != null ? "not null" : "null"));
            //显示默认城市地图
            if(user!=null) {
                GetWeatherController getWeatherController=new GetWeatherController();
                // 获取城市天气并保存
                getWeatherController.getCityWeather(user.getDefaultCity(), user.getId(), new GetWeatherController.WeatherCallback() {
                    @Override
                    public void onSuccess(CityWeather cityWeather) {
                        // 将获取的天气数据保存到数据库
                        cityWeatherViewModel.addLikeCity(user.getId(),user.getDefaultCity(),cityWeather);
                        cityWeatherViewModel.setUserId(user.getId());
                    }

                    @Override
                    public void onError(String error) {
                        // 处理错误情况
                        Log.e("SomeFragment", "Error fetching weather: " + error);
                    }
                });
            }
            switchToMainNavController();

        } else {
            // 如果未登录，显示登录界面
            loginNavController.navigate(R.id.loginFragment);
        }
    }
    // 登录成功后，切换到主界面并结束登录部分的 NavController
    private void switchToMainNavController() {
        // 隐藏登录部分的 NavHostFragment
        findViewById(R.id.login_nav_host_fragment).setVisibility(View.GONE);
        // 显示主界面部分的 NavHostFragment 和底部导航栏
        findViewById(R.id.nav_host_fragment).setVisibility(View.VISIBLE);
        findViewById(R.id.bottom_navigation).setVisibility(View.VISIBLE);
        // 启动主界面导航
        mainNavController.navigate(R.id.mapFragment);
        // 绑定底部导航栏与主界面的 NavController
        NavigationUI.setupWithNavController(bottomNavigationView, mainNavController);
    }
    @Override
    protected void onStart() {
        super.onStart();
        if(isUserLoggedIn==false) checkUserLoggedIn();
    }

}
