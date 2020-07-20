package com.example.imgedit.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.imgedit.config.Config;
import com.example.imgedit.constant.Code;
import com.example.imgedit.listener.TakePhotoSuccessListener;
import com.example.imgedit.widget.ActivityLauncher;
import com.example.imgedit.widget.EasyCameraActivity;


public class TakePhotoUtil {

    public static void TakeThoto(Context context, String number, final TakePhotoSuccessListener listener) {
        Intent intent = new Intent(context, EasyCameraActivity.class);
        intent.putExtra(Config.CONGIG_SHOW_NUMBER, number);
        // 启动Activity
        ActivityLauncher.init((Activity) context, intent)
                .startActivityForResult(Code.REQUEST_CAMERA, new ActivityLauncher.Callback() {
                    @Override
                    public void onActivityResult(int requestCode, int resultCode, Intent data) {
                        if (Code.REQUEST_CAMERA == requestCode&&null!=data) {
                            String imagePath = data.getStringExtra(Config.CONGIG_SAVE_PATH);
                            if (null != listener) {
                                listener.onSuccess(imagePath);
                            }
                        }
                    }
                });

    }


}
