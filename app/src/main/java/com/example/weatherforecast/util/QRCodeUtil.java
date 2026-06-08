package com.example.weatherforecast.util;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.Hashtable;

public class QRCodeUtil {
    public static Bitmap generateQRCode(String text){
        int width=500;
        int height=500;
        Bitmap bitmap=Bitmap.createBitmap(width,height,Bitmap.Config.ARGB_8888);
        Hashtable<EncodeHintType, Object> encodingHints = new Hashtable<>();

        encodingHints.put(EncodeHintType.CHARACTER_SET,"UTF-8");
        try{
            BitMatrix bitMatrix=new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE,width,height,encodingHints);
            for(int i=0;i<width;i++){
                for (int j=0;j<height;j++){
                    bitmap.setPixel(i,j,bitMatrix.get(i,j)? Color.BLACK:Color.WHITE);
                }
            }
        }catch (WriterException e){
            e.printStackTrace();
        }
        return bitmap;
    }
}
