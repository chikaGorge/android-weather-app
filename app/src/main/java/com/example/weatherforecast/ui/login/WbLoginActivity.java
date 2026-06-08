package com.example.weatherforecast.ui.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;


import com.example.weatherforecast.R;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbAuthListener;
import com.sina.weibo.sdk.common.UiError;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.WBAPIFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class WbLoginActivity extends Activity implements View.OnClickListener {
    private Button mInit;

    private Button mSso;

    private Button mClientSso;

    private Button mWebSso;

    private IWBAPI mWBAPI;

    private static String LOG_TAG = "WEIBO_SDK_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wb_login);
        mInit = findViewById(R.id.init_sdk);
        mSso = findViewById(R.id.sso);
        mClientSso = findViewById(R.id.sso_client);
        mWebSso = findViewById(R.id.sso_web);
        mInit.setOnClickListener(this);
        mSso.setOnClickListener(this);
        mClientSso.setOnClickListener(this);
        mWebSso.setOnClickListener(this);
        if (savedInstanceState != null && savedInstanceState.getBoolean("sdk_init")) {
            initSdk();
        }
    }

    //init sdk
    private void initSdk() {
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mSso.setEnabled(true);
        mClientSso.setEnabled(true);
        mWebSso.setEnabled(true);
    }

    private void startAuth() {
        //auth
        mWBAPI.authorize(this, new WbAuthListener() {
            @Override
            public void onComplete(Oauth2AccessToken token) {
                String text="授权成功：uid=" + token.getUid() +
                        ";\n token=" + token.getAccessToken()+
                        ";\n RefreshToken=" + token.getRefreshToken()+
                        ";\n ScreenName=" +  token.getScreenName()+
                        ";\n ExpiresTime=" + token.getExpiresTime();
                Toast.makeText(WbLoginActivity.this, "微博授权成功", Toast.LENGTH_SHORT).show();

                Log.d(LOG_TAG,text);
                //回到页面以及返回数据
                Intent resultIntent = new Intent();
                resultIntent.putExtra("token", text);  // 将微博授权 token 传回
                resultIntent.putExtra("wbUid", token.getUid());  // 将微博授权 token 传回
                resultIntent.putExtra("wbScreenName", token.getScreenName());  // 将微博授权 token 传回
                // 设置结果并结束
                setResult(Activity.RESULT_OK, resultIntent);
                finish();  // 关闭 WbLoginActivity
            }

            @Override
            public void onError(UiError error) {
                Toast.makeText(WbLoginActivity.this, "微博授权出错", Toast.LENGTH_SHORT).show();
                Log.d(LOG_TAG,"微博授权出错：" + error.errorMessage + "-" + error.errorDetail);
            }

            @Override
            public void onCancel() {
                Toast.makeText(WbLoginActivity.this, "微博授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mWBAPI.isAuthorizeResult(requestCode,resultCode,data)) {
            mWBAPI.authorizeCallback(this, requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == mInit) {
            initSdk();
        }
        if (v == mSso) {
            try {
                startAuth();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(WbLoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if (v == mClientSso) {
            try {
                startClientAuth();
            }   catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(WbLoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        if (v == mWebSso) {
            try {
                startWebAuth();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(WbLoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startClientAuth() {
        mWBAPI.authorizeClient(this, new WbAuthListener() {
            @Override
            public void onComplete(Oauth2AccessToken token) {
                Toast.makeText(WbLoginActivity.this, "微博授权成功", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(UiError error) {
                Toast.makeText(WbLoginActivity.this, "微博授权出错", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(WbLoginActivity.this, "微博授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startWebAuth() {
        mWBAPI.authorizeWeb(this, new WbAuthListener() {
            @Override
            public void onComplete(Oauth2AccessToken token) {
                Toast.makeText(WbLoginActivity.this, "微博授权成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(UiError error) {
                Toast.makeText(WbLoginActivity.this, "微博授权出错:" + error.errorDetail, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(WbLoginActivity.this, "微博授权取消", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("sdk_init", mWBAPI != null);
    }



}
