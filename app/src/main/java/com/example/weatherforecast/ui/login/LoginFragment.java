package com.example.weatherforecast.ui.login;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.example.weatherforecast.R;
import com.example.weatherforecast.controller.viewModel.UserViewModel;
import com.example.weatherforecast.controller.factory.UserViewModelFactory;
import com.example.weatherforecast.data.model.User;
import com.example.weatherforecast.data.repository.UserRepository;
import com.example.weatherforecast.ui.MainActivity;


public class LoginFragment extends Fragment {
    private EditText etUsername, etPassword;
    private Button btnLogin, btnRegister,btnWbLogin;
    private UserViewModel userViewModel;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);

        // 初始化视图
        etUsername = root.findViewById(R.id.et_username);
        etPassword = root.findViewById(R.id.et_password);
        btnLogin = root.findViewById(R.id.btn_login);
        btnRegister = root.findViewById(R.id.btn_register);
        btnWbLogin=root.findViewById(R.id.weibo_login_button);

        // 获取 UserViewModel
        userViewModel = new ViewModelProvider(requireActivity(),
                new UserViewModelFactory(requireActivity())
                ).get(UserViewModel.class);
        Log.d("LoginFragment", "userViewModel: " + (userViewModel == null ? "null" : "initialized"));

        //检测自动登陆

        // 登录按钮点击事件
        btnLogin.setOnClickListener(v -> loginUser());

        // 跳转到注册页面
        btnRegister.setOnClickListener(v -> {
            Navigation.findNavController(root).navigate(R.id.action_loginFragment_to_registerFragment);
        });
        //跳转到微博登陆界面
        btnWbLogin.setOnClickListener(view ->{
            Navigation.findNavController(root).navigate(R.id.action_loginFragment_to_wbLoginFragment);
        } );
        return root;
    }

    //通过账号密码登陆
    private void loginUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // 校验输入的用户名和密码是否为空
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "请输入用户名和密码", Toast.LENGTH_SHORT).show();
            return;
        }
        // 检查账户是否匹配
        userViewModel.checkAccount(username, password, new UserRepository.Callback<User>() {
            @Override
            public void onResult(User user) {
                // 确保 UI 更新发生在主线程
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (user == null) {
                        // 登录失败
                        Toast.makeText(getContext(), "用户名或密码错误", Toast.LENGTH_SHORT).show();
                    } else {
                        // 登录成功
                        Toast.makeText(getContext(), "登录成功", Toast.LENGTH_SHORT).show();
                        ((MainActivity) requireActivity()).loginSuccess(true,user);
                    }
                });
            }
        });
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 在视图加载完成后调用loginSuccess()
        checkAutoLogin();
    }
    private void checkAutoLogin() {
        SharedPreferences preferences = requireActivity()
                .getSharedPreferences("app_preferences", Context.MODE_PRIVATE);
        boolean isAutoLogonEnabled = preferences
                .getBoolean("automatic_logon_enabled", false);

        if (isAutoLogonEnabled) {
            // 取出用户数据
            int userId = preferences.getInt("id", -1);

            userViewModel.getUserById(userId);

            // 使用 LiveData 来监听数据更新
            userViewModel.getUser().observe(getViewLifecycleOwner(), user -> {
                if (user != null) {
                    // 确保 MainActivity 已经初始化，并调用 loginSuccess
                    Activity activity = getActivity();
                    if (activity != null) {
                        ((MainActivity) requireActivity()).loginSuccess(true, user);
                    }
                } else {
                    // 处理用户未找到的情况
                    Log.e("LoginFragment", "User not found in auto-login");
                }
            });
        }
    }

}

