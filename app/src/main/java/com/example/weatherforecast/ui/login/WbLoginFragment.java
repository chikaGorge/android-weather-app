package com.example.weatherforecast.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.weatherforecast.R;
import com.example.weatherforecast.controller.viewModel.UserViewModel;
import com.example.weatherforecast.controller.factory.UserViewModelFactory;
import com.example.weatherforecast.data.model.User;
import com.example.weatherforecast.data.repository.UserRepository;
import com.example.weatherforecast.ui.MainActivity;


public class WbLoginFragment extends Fragment {

    private Button goWb, backLogin;
    private static final int REQUEST_LOGIN_BY_WB = 1; // 定义请求码
    private UserViewModel userViewModel;
    private User currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_wb_login, container, false);

        goWb = root.findViewById(R.id.go_wb);
        backLogin = root.findViewById(R.id.back_login);

        // 确保 ViewModelFactory 和 ViewModelProvider 使用正确
        userViewModel = new ViewModelProvider(requireActivity(),
                new UserViewModelFactory(requireActivity())
        ).get(UserViewModel.class);
        Log.d("WbLoginFragment", "userViewModel: " + (userViewModel == null ? "null" : "initialized"));

        // 返回登录
        backLogin.setOnClickListener(v -> Navigation.findNavController(getView()).navigate(R.id.action_wbLoginFragment_to_loginFragment));

        // 前往微博
        goWb.setOnClickListener(view -> {
            // 启动 LoginByWbActivity
            Intent intent = new Intent(getActivity(), WbLoginActivity.class);
            startActivityForResult(intent, REQUEST_LOGIN_BY_WB);
        });

        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOGIN_BY_WB) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String token = data.getStringExtra("token");
                String wbUid = data.getStringExtra("wbUid");
                String wbScreenName = data.getStringExtra("wbScreenName");
                Log.d("WbLoginFragment", "onActivityResult: token = " + token);
                if (token != null) {
                    Log.d("WbLoginFragment", "ReturnWbToken:\n" + token);
                    // 检查微博UID是否已注册
                    userViewModel.checkAccountByWb(wbUid, new UserRepository.Callback<User>() {
                        @Override
                        public void onResult(User user) {
                            if (user != null) {
                                // 如果用户已存在，直接登录
                                Log.d("WbLoginFragment", "User found, logging in...");
                                userViewModel.setUser(user);
                                currentUser=user;
                                // 登录成功
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    // 登录成功
                                    Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                                    ((MainActivity) requireActivity()).loginSuccess(true, currentUser);
                                });
                            } else {
                                // 如果用户不存在，创建新用户
                                Log.d("WbLoginFragment", "User not found, creating a new user...");
                                User newUser = new User(null, null, null, wbUid, "浙江省-杭州市-余杭区");
                                // 异步检查用户名是否存在
                                userViewModel.isUsernameExisted(wbScreenName, new UserRepository.Callback<Boolean>() {
                                    @Override
                                    public void onResult(Boolean exists) {
                                        // 确保 UI 更新在主线程
                                        new Handler(Looper.getMainLooper()).post(() -> {
                                            if (!exists) {
                                                // 如果用户名不存在，设置用户名
                                                newUser.setUsername(wbScreenName);
                                            }
                                            // 创建账户并添加
                                            userViewModel.addAccount(newUser);
                                            // 再次检查创建的用户
                                            userViewModel.checkAccountByWb(wbUid, new UserRepository.Callback<User>() {
                                                @Override
                                                public void onResult(User user) {
                                                    if (user != null) {
                                                        // 如果用户已存在，直接登录
                                                        Log.d("WbLoginFragment", "User found after account creation, logging in...");
                                                        userViewModel.setUser(user);
                                                        // 登录成功
                                                        currentUser=user;
                                                        new Handler(Looper.getMainLooper()).post(() -> {
                                                            // 登录成功
                                                            Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                                                            ((MainActivity) requireActivity()).loginSuccess(true, currentUser);
                                                        });
                                                    }
                                                }
                                            });
                                        });
                                    }
                                });
                            }
                        }

                    });
                }

            } else {
                Log.e("WbLoginFragment", "Login failed or canceled");
            }
        }
    }

}

