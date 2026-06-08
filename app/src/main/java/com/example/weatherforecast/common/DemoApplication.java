package com.example.weatherforecast.common;

import android.app.Application;

import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.openapi.IWBAPI;
import com.sina.weibo.sdk.openapi.SdkListener;
import com.sina.weibo.sdk.openapi.WBAPIFactory;

public class DemoApplication extends Application {

    //在微博开发平台为应用申请的App Key
    private static final String APP_KY = "1794377351";
    //在微博开放平台设置的授权回调页
    private static final String REDIRECT_URL = "http://www.sina.com";
    //在微博开放平台为应用申请的高级权限
    private static final String SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";


    private IWBAPI mWBAPI;

    @Override
    public void onCreate() {
        super.onCreate();
        initSdk();
    }

    //init sdk
    private void initSdk() {
        AuthInfo authInfo = new AuthInfo(this, APP_KY, REDIRECT_URL, SCOPE);
        mWBAPI = WBAPIFactory.createWBAPI(this);
        mWBAPI.registerApp(this, authInfo, new SdkListener() {
            @Override
            public void onInitSuccess() {

            }

            @Override
            public void onInitFailure(Exception e) {

            }
        });
    }
}

