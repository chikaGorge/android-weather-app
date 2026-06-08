package com.example.weatherforecast.ui.settings;

import static android.app.Activity.RESULT_OK;
import static androidx.activity.result.ActivityResultCallerKt.registerForActivityResult;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.result.ActivityResult;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.weatherforecast.R;

public class CustomUserPreference extends Preference {

    private ImageView userAvatar;
    private EditText userUsername;
    private EditText userPassword;

    public static final int REQUEST_CODE_PICK_IMAGE = 1001;  // 请求码
    private OnAvatarChangedListener mListener;
    private Fragment parentFragment;  // 引用父 Fragment

    public CustomUserPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setLayoutResource(R.layout.user_header); // 设置自定义布局
    }
    public void setParentFragment(Fragment fragment) {
        this.parentFragment = fragment;
    }
    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);

        userAvatar = (ImageView) holder.findViewById(R.id.user_avatar);
        userUsername = (EditText) holder.findViewById(R.id.user_account);
        userPassword = (EditText) holder.findViewById(R.id.user_password);

        // 加载默认头像
        Glide.with(getContext())
                .load(R.drawable.image_default_avatar)
                .apply(RequestOptions.circleCropTransform())
                .into(userAvatar);

        // 设置头像点击事件
        userAvatar.setOnClickListener(v -> openImagePicker());
        // 设置账号和密码的点击事件
        userUsername.setOnClickListener(v -> openUsernameEditor());
        userPassword.setOnClickListener(v -> openPasswordEditor());
    }

    private void openImagePicker() {
        // 打开相册选择图片
        if (parentFragment instanceof SettingsFragment) {
            ((SettingsFragment) parentFragment).openImagePickerInFragment();
        }
    }

    private void openUsernameEditor() {
        // 打开账号编辑界面
        EditText input = new EditText(getContext());
        input.setText(userUsername.getText().toString());

        new AlertDialog.Builder(getContext())
                .setTitle("修改账号")
                .setView(input)
                .setPositiveButton("保存", (dialog, which) -> {
                    String newAccount = input.getText().toString();
                    setUsernameText(newAccount); // 更新显示的账号
                    if (mListener != null) {
                        mListener.onUsernameChanged(newAccount);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    private void openPasswordEditor() {
        // 打开密码编辑界面
        EditText input = new EditText(getContext());
        input.setText(userPassword.getText().toString());
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        new AlertDialog.Builder(getContext())
                .setTitle("修改密码")
                .setView(input)
                .setPositiveButton("保存", (dialog, which) -> {
                    String newPassword = input.getText().toString();
                    setPasswordText(newPassword); // 更新显示的密码
                    if (mListener != null) {
                        mListener.onPasswordChanged(newPassword);
                    }
                })
                .setNegativeButton("取消", null)
                .show();
    }

    // 设置头像路径回调
    public interface OnAvatarChangedListener {
        void onAvatarChanged(Uri imageUri);  // 接收图片Uri
        void onUsernameChanged(String newUsername);  // 接收新账号
        void onPasswordChanged(String newPassword);  // 接收新密码
    }



    public void setAvatarDrawable(Drawable drawable) {
        if (userAvatar != null && drawable != null) {
            Glide.with(getContext())
                    .load(drawable)
                    .apply(RequestOptions.circleCropTransform())  // 使用圆形裁剪
                    .into(userAvatar);  // 加载并显示
        }
    }
    public void setAvatarUri(@NonNull Uri uri) {
        if (userAvatar != null && uri != null) {
            Glide.with(getContext())
                    .load(uri)
                    .apply(RequestOptions.circleCropTransform()) // 使用圆形裁剪
                    .into(userAvatar); // 加载并显示
        }
    }
    public void setUsernameText(String text) {
        if (userUsername != null) {
            userUsername.setText(text);
        }
    }

    public void setPasswordText(String text) {
        if (userPassword != null) {
            userPassword.setText(text);
        }
    }

    public String getUsernameText() {
        return userUsername.getText().toString();
    }

    public String getPasswordText() {
        return userPassword.getText().toString();
    }

    public void setOnAvatarChangedListener(OnAvatarChangedListener listener) {
        this.mListener = listener;
    }
}