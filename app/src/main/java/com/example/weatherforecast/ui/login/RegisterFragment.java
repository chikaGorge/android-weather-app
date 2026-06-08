package com.example.weatherforecast.ui.login;

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

public class RegisterFragment extends Fragment {
    private EditText etUsername, etPassword, etConfirmPassword;
    private Button btnRegister,btnBackLogin;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_register, container, false);

        // 初始化视图
        etUsername = root.findViewById(R.id.et_username);
        etPassword = root.findViewById(R.id.et_password);
        etConfirmPassword = root.findViewById(R.id.et_confirm_password);
        btnRegister = root.findViewById(R.id.btn_register);
        btnBackLogin=root.findViewById(R.id.btn_back_to_login);
        // 获取 UserViewModel
        userViewModel = new ViewModelProvider(requireActivity(),
                new UserViewModelFactory(requireActivity())
        ).get(UserViewModel.class);
        Log.d("RegisterFragment", "userViewModel: " + (userViewModel == null ? "null" : "initialized"));


        // 注册按钮点击事件
        btnRegister.setOnClickListener(v -> registerUser());

        btnBackLogin.setOnClickListener(view -> {
            Navigation.findNavController(getView()).navigate(R.id.action_registerFragment_to_loginFragment);
        });
        return root;
    }

    private void registerUser() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(getContext(), "请填写完整的注册信息", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(getContext(), "密码不一致", Toast.LENGTH_SHORT).show();
            return;
        }
        // 检查用户名是否已存在
        userViewModel.isUsernameExisted(username, new UserRepository.Callback<Boolean>() {
            @Override
            public void onResult(Boolean exists) {
                // 确保 UI 更新发生在主线程
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (!exists) {
                        // 如果用户名已存在
                        Toast.makeText(getContext(), "用户名已存在", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // 创建新用户
        User newUser = new User(username, password,null,null,"浙江省-杭州市-余杭区");
        userViewModel.addAccount(newUser);
        Log.d("RegisterFragment","newUser:"+newUser.toString());
        Toast.makeText(getContext(), "注册成功", Toast.LENGTH_SHORT).show();

        // 注册成功后返回登录界面
        Navigation.findNavController(getView()).navigate(R.id.action_registerFragment_to_loginFragment);
    }
}

