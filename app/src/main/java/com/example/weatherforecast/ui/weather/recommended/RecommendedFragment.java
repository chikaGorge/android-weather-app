package com.example.weatherforecast.ui.weather.recommended;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.weatherforecast.R;
import com.example.weatherforecast.common.Constants;
import com.example.weatherforecast.controller.QRCodeController;
public class RecommendedFragment extends Fragment {

    private EditText etUrl;
    private ImageView ivQRCode;
    private ImageView ivDailyImage;
    private Button btnGenerateQRCode;
    private Button btnDailyImage;
    private QRCodeController qrCodeController;

    private String savedUrlText;      // 保存 EditText 中的文本
    private Bitmap savedQRCode;       // 保存生成的二维码
    private Bitmap savedDailyImage;   // 保存拍摄的图片

    // 新式 ActivityResultLauncher，用于请求相机权限
    private ActivityResultLauncher<String> requestCameraPermissionLauncher;

    // 用于拍照预览并返回 Bitmap
    private ActivityResultLauncher<Void> takePictureLauncher;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. 注册“请求相机权限”的 Launcher
        requestCameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean isGranted) {
                        if (isGranted) {
                            // 用户同意权限后，直接启动拍照
                            takePictureLauncher.launch(null);
                        } else {
                            // 用户拒绝权限，可提示或做其他处理
                            Log.e("RecommendedFragment", "Camera permission denied.");
                        }
                    }
                }
        );

        // 2. 注册“拍照预览”的 Launcher
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                new ActivityResultCallback<Bitmap>() {
                    @Override
                    public void onActivityResult(Bitmap result) {
                        if (result != null) {
                            // 确保 UI 更新在主线程
                            if (isAdded() && !isDetached() && getContext() != null) {
                                // 创建一个新的缩放后的 Bitmap
                                int targetWidth = 500;
                                int targetHeight = 500;
                                Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                                        result, targetWidth, targetHeight, true);

                                ivDailyImage.setImageBitmap(scaledBitmap);
                                savedDailyImage = scaledBitmap; // 保存拍摄的图片
                            } else {
                                Log.e("RecommendedFragment", "Fragment is not attached or already detached.");
                            }
                        } else {
                            Log.e("RecommendedFragment", "Captured image is null.");
                        }
                    }
                }
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommended, container, false);

        etUrl = view.findViewById(R.id.etUrl);
        ivQRCode = view.findViewById(R.id.ivQRCode);
        ivDailyImage = view.findViewById(R.id.ivDailyImage);
        btnGenerateQRCode = view.findViewById(R.id.btnGenerateQRCode);
        btnDailyImage = view.findViewById(R.id.btnDailyImage);

        qrCodeController = new QRCodeController();

        // 如果恢复状态有文本和图像，则进行还原
        if (savedInstanceState != null) {
            savedUrlText = savedInstanceState.getString("saved_url_text", Constants.DEFAULT_SCHOOL_URL);
            savedQRCode = savedInstanceState.getParcelable("saved_qrcode_bitmap");
            savedDailyImage = savedInstanceState.getParcelable("saved_daily_image_bitmap");

            etUrl.setText(savedUrlText);
            if (savedQRCode != null) {
                ivQRCode.setImageBitmap(savedQRCode);
            }
            if (savedDailyImage != null) {
                ivDailyImage.setImageBitmap(savedDailyImage);
            }
        } else {
            // 如果没有保存过，则使用默认 URL
            savedUrlText = Constants.DEFAULT_SCHOOL_URL;
            etUrl.setText(savedUrlText);
        }

        // 点击按钮，生成二维码
        btnGenerateQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = etUrl.getText().toString();
                Bitmap qrCode = qrCodeController.generateQRCode(text);
                ivQRCode.setImageBitmap(qrCode);
                savedQRCode = qrCode; // 保存二维码图片
            }
        });

        // 点击按钮，拍摄照片
        btnDailyImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 检查当前相机权限
                if (ContextCompat.checkSelfPermission(
                        requireContext(), Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    // 如果已经有权限，则直接拍照
                    takePictureLauncher.launch(null);
                } else {
                    // 没有权限，先申请
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA);
                }
            }
        });

        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // 保存 EditText 输入的文本
        outState.putString("saved_url_text", etUrl.getText().toString());

        // 保存二维码和拍摄的图片
        if (savedQRCode != null) {
            outState.putParcelable("saved_qrcode_bitmap", savedQRCode);
        }
        if (savedDailyImage != null) {
            outState.putParcelable("saved_daily_image_bitmap", savedDailyImage);
        }
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        if (savedInstanceState != null) {
            // 恢复 EditText 中的文本
            savedUrlText = savedInstanceState.getString("saved_url_text", Constants.DEFAULT_SCHOOL_URL);
            etUrl.setText(savedUrlText);

            // 恢复二维码和拍摄的图片
            savedQRCode = savedInstanceState.getParcelable("saved_qrcode_bitmap");
            savedDailyImage = savedInstanceState.getParcelable("saved_daily_image_bitmap");

            if (savedQRCode != null) {
                ivQRCode.setImageBitmap(savedQRCode);
            }
            if (savedDailyImage != null) {
                ivDailyImage.setImageBitmap(savedDailyImage);
            }
        }
    }
}