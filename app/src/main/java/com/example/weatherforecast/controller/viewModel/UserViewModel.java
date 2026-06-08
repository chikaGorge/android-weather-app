package com.example.weatherforecast.controller.viewModel;

import static com.example.weatherforecast.common.Constants.HANGZHOU;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.amap.api.maps.model.LatLng;
import com.example.weatherforecast.controller.GetLocationController;
import com.example.weatherforecast.data.model.User;
import com.example.weatherforecast.data.repository.UserRepository;


public class UserViewModel extends ViewModel {
    private UserRepository userRepository;
    private MutableLiveData<User> userLiveData = new MutableLiveData<>();
    private MutableLiveData<LatLng> defaultCityLiveData = new MutableLiveData<>();

    private GetLocationController getLocationController;
    private User currentUser;
    private LatLng currentLatLng;

    public UserViewModel(Context context) {
        this.getLocationController = new GetLocationController();
        this.userRepository = new UserRepository(context);
        Log.d("UserViewModel", "userRepository: " + (userRepository == null ? "null" : "initialized"));
    }

    //根据id查找用户
    public void getUserById(int userId) {
        if (userId <= 0) {
            Log.e("UserViewModel", "Invalid userId");
            userLiveData.postValue(null);  // 传递 null
            return;
        }

        // 调用 UserRepository 来获取用户数据
        userRepository.getUserById(userId, new UserRepository.Callback<User>() {
            @Override
            public void onResult(User user) {
                if (user != null) {
                    Log.d("UserViewModel", "User found: " + user.getUsername());
                } else {
                    Log.e("UserViewModel", "User not found with userId = " + userId);
                }
                // 将获取的用户对象通过 LiveData 传递给观察者
                userLiveData.postValue(user);
                currentUser=user;
            }
        });
    }

    // 检查用户名是否存在
    public void isUsernameExisted(String username, UserRepository.Callback<Boolean> callback) {
        if (userRepository == null) {
            Log.e("UserViewModel", "UserRepository is null");
            callback.onResult(false); // 如果 userRepository 为 null，直接返回 false
            return;
        }

        // 调用 UserRepository 来检查用户名是否存在
        userRepository.isUsernameExists(username, new UserRepository.Callback<Boolean>() {
            @Override
            public void onResult(Boolean exists) {
                callback.onResult(exists); // 通过回调传递结果
            }
        });
    }


    // 注册成功添加账户
    public void addAccount(User user) {
        if (userRepository == null) {
            Log.e("UserViewModel", "UserRepository is null");
//            callback.onResult(-1L);
            return;
        }
        userRepository.insertUser(user, new UserRepository.Callback<Long>() {
            @Override
            public void onResult(Long result) {
                if (result > 0) {
                    Log.d("UserViewModel", "User registered successfully");
                } else {
                    Log.e("UserViewModel", "User registration failed");
                }
//                callback.onResult(result);
            }
        });
    }

    // 根据用户名密码检查账户
    public void checkAccount(String username, String password, UserRepository.Callback<User> callback) {
        if (userRepository == null) {
            Log.e("UserViewModel", "UserRepository is null");
            callback.onResult(null);  // 如果 userRepository 为 null，返回 null
            return;
        }

        userRepository.checkAccount(username, password, new UserRepository.Callback<User>() {
            @Override
            public void onResult(User user) {
                if (user != null) {
                    currentUser=user;
                    userLiveData.postValue(user);  // 如果用户存在，将用户数据发布到 LiveData
                    fetchLatLngForCity(user.getDefaultCity());  // 可能根据城市获取坐标
                    Log.d("UserViewModel","get user for username="+user.getUsername());
                    Log.d("UserViewModel","get user for userLiveData username="+userLiveData.getValue().getUsername());

                }
                callback.onResult(user);  // 回调返回 User 对象或 null
            }
        });
    }


    // 通过微博登录检查微博用户是否注册过
    public void checkAccountByWb(String wbId, UserRepository.Callback<User> callback) {
        if (userRepository == null) {
            Log.e("UserViewModel", "UserRepository is null");
            callback.onResult(null);
            return;
        }

        userRepository.checkAccountByWb(wbId, new UserRepository.Callback<User>() {
            @Override
            public void onResult(User user) {
                if (user != null) {
                    currentUser=user;
                    userLiveData.postValue(user);
                    fetchLatLngForCity(user.getDefaultCity());
                    Log.d("UserViewModel","get user for wbId="+user.getWbId());
                }
                callback.onResult(user);
            }
        });
    }

    // 更改用户数据
    public void updateUser(User user) {
        if (userRepository == null) {
            Log.e("UserViewModel", "UserRepository is null");
//            callback.onResult(0);
            return;
        }

        userRepository.updateUser(user, new UserRepository.Callback<Integer>() {
            @Override
            public void onResult(Integer result) {
                if (result > 0) {
                    userLiveData.postValue(user);
                    currentUser=user;
                    Log.d("UserViewModel", "User updated successfully");
                } else {
                    Log.e("UserViewModel", "User update failed");
                }
//                callback.onResult(result);
            }
        });
    }

    // 更改默认城市
    public void updateDefaultCity(User user, String defaultCity) {
        if (userRepository == null) {
            Log.e("UserViewModel", "UserRepository is null");
//            callback.onResult(0);
            return;
        }

        if (user != null && !user.getDefaultCity().equals(defaultCity)) {
            user.setDefaultCity(defaultCity);
            userRepository.updateUser(user, new UserRepository.Callback<Integer>() {
                @Override
                public void onResult(Integer result) {
                    if (result > 0) {
                        userLiveData.postValue(user);
                        fetchLatLngForCity(user.getDefaultCity());
                        currentUser=user;
                    } else {
                        Log.e("UserViewModel", "Failed to update user with new city");
                    }
//                    callback.onResult(result);
                }
            });
        } else {
//            callback.onResult(0);  // No change needed
        }
    }

    // 获取LatLng
    private void fetchLatLngForCity(String city) {
        if (city == null || city.isEmpty()) {
            Log.e("UserViewModel", "City name is invalid");
            return;
        }

        getLocationController.fetchLatLngForCity(city, new GetLocationController.LatLngCallback() {
            @Override
            public void onLatRetrieved(LatLng latLng) {
                defaultCityLiveData.postValue(latLng);
                currentLatLng=latLng;
            }

            @Override
            public void onError(String error) {
                defaultCityLiveData.postValue(HANGZHOU); // 使用杭州市作为默认值
                currentLatLng=HANGZHOU;
                Log.e("UserViewModel", "Failed to fetch LatLng for city: " + city);
            }
        });
    }

    public LiveData<LatLng> getUserCityLatLng() {
        return defaultCityLiveData;
    }

    // set 和 get 方法
    public void setUser(User user) {
        if (user != null) {
            currentUser=user;
            userLiveData.postValue(user);
            fetchLatLngForCity(user.getDefaultCity());
        } else {
            Log.d("setUserError", "User is null");
        }
    }

    public LiveData<User> getUser() {
        return userLiveData;
    }
    //刷新数据
    public void refreshUserData() {
        userLiveData.postValue(currentUser);
    }
    //刷新经纬度
    public void refreshLatLng(){
        defaultCityLiveData.postValue(currentLatLng);
    }
}
