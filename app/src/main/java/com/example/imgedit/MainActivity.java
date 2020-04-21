package com.example.imgedit;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.imgedit.cameralibrary.util.LogUtil;
import com.example.imgedit.constant.Code;
import com.example.imgedit.constant.Key;
import com.example.imgedit.permission.PermissionListener;
import com.example.imgedit.permission.PermissionsUtil;
import com.example.imgedit.widget.EasyCameraActivity;


public class MainActivity extends AppCompatActivity {

    private ImageView iv_image;
    private Button bt_takePhoto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iv_image = findViewById(R.id.iv_image);
        bt_takePhoto = findViewById(R.id.bt_takePhoto);
        bt_takePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestReadContact();
            }
        });

    }
    private void requestReadContact() {
        PermissionsUtil.TipInfo tip = new PermissionsUtil.TipInfo("注意:", "需要获取权限!", "拒绝", "打开权限");
        PermissionsUtil.requestPermission(MainActivity.this, new PermissionListener() {
            @Override
            public void permissionGranted(@NonNull String[] permissions) {
                launchCamera(Code.REQUEST_CAMERA);
            }

            @Override
            public void permissionDenied(@NonNull String[] permissions) {
                Toast.makeText(MainActivity.this, "用户拒绝了权限", Toast.LENGTH_LONG).show();
            }
        }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, true, tip);
    }


    /**
     * 启动相机
     *
     * @param requestCode startActivityForResult的请求码
     */
    private void launchCamera(int requestCode) {
        Intent intent = new Intent(this, EasyCameraActivity.class);
        startActivityForResult(intent, requestCode);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case RESULT_OK:
                if (data == null) return;
                if (Code.REQUEST_CAMERA == requestCode) {
                    String imagePath = data.getStringExtra(Key.IMAGE_PATH);
                    Glide.with(this).load(imagePath).into(iv_image);
                }
            default:
                break;
        }
    }




}
