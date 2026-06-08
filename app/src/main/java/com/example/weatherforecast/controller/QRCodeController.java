package com.example.weatherforecast.controller;

import android.graphics.Bitmap;

import com.example.weatherforecast.util.QRCodeUtil;

public class QRCodeController {
    public Bitmap generateQRCode(String text){
        return QRCodeUtil.generateQRCode(text);
    }
}
