package com.example.imgedit.widget;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.imgedit.R;
import com.example.imgedit.cameralibrary.JCameraView;
import com.example.imgedit.cameralibrary.listener.ClickListener;
import com.example.imgedit.cameralibrary.listener.ErrorListener;
import com.example.imgedit.cameralibrary.listener.JCameraListener;
import com.example.imgedit.cameralibrary.util.FileUtil;
import com.example.imgedit.config.Config;
import com.example.imgedit.constant.Code;
import com.example.imgedit.constant.Key;
import com.example.imgedit.editLibrary.IMGEditActivity;
import com.example.imgedit.utils.SystemUtils;
import com.example.imgedit.utils.UriUtils;

import java.io.File;

public class EasyCameraActivity extends AppCompatActivity {

    private JCameraView jCameraView;
    private ProgressBar pbProgress;
    private RelativeLayout rlCoverView;

    private String applicationName = "";
    private String cameraPath = null;
    private static String storagePath = "";
    private static final File parentPath = Environment.getExternalStorageDirectory();
    public static Bitmap bitmapImg;
    private String number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        number = getIntent().getStringExtra(Config.CONGIG_SHOW_NUMBER);
        try {
            applicationName = getString(R.string.app_name);
            PackageManager packageManager = getApplicationContext().getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            applicationName = (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        toCustomCamera();

    }

    private void toCustomCamera() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            WindowManager.LayoutParams lp = getWindow().getAttributes();
            // 始终允许窗口延伸到屏幕短边上的刘海区域
            lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            getWindow().setAttributes(lp);
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        setContentView(R.layout.activity_camera);
        pbProgress = findViewById(R.id.pb_progress);
        jCameraView = findViewById(R.id.jCameraView);
        jCameraView.enableCameraTip(true);
//        if (Setting.cameraCoverView != null && Setting.cameraCoverView.get() != null) {
//            View coverView = Setting.cameraCoverView.get();
//            rlCoverView = findViewById(R.id.rl_cover_view);
//            coverView.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//            rlCoverView.addView(coverView);
//        }
        initCustomCamera();
    }

    private int getFeature() {
        return JCameraView.BUTTON_STATE_ONLY_CAPTURE;//拍照

//        switch (Setting.captureType) {
//            case Capture.ALL:
//                return JCameraView.BUTTON_STATE_BOTH;
//            case Capture.IMAGE:
//                return JCameraView.BUTTON_STATE_ONLY_CAPTURE;//拍照
//            default:
//                return JCameraView.BUTTON_STATE_ONLY_RECORDER;
//        }
    }

    private void initCustomCamera() {
        //视频存储路径
        if (SystemUtils.beforeAndroidTen()) {
            jCameraView.setSaveVideoPath(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + File.separator + applicationName);
        } else {
            jCameraView.setSaveVideoPath(getExternalFilesDir(Environment.DIRECTORY_DCIM) + File.separator + applicationName);
        }
        jCameraView.setFeatures(getFeature());
        jCameraView.setMediaQuality(JCameraView.MEDIA_QUALITY_MIDDLE);
        //fixme 录像时间+800ms 修复录像时间少1s问题
        jCameraView.setDuration(15800);
        jCameraView.setErrorListener(new ErrorListener() {
            @Override
            public void onError() {
                //错误监听
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }

            @Override
            public void AudioPermissionError() {
                //Toast.makeText(EasyCameraActivity.this, getString(R.string.missing_audio_permission), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_CANCELED, intent);
                finish();
            }
        });
//        if (Setting.cameraCoverView != null && Setting.cameraCoverView.get() != null) {
//            jCameraView.setPreViewListener(new JCameraPreViewListener() {
//
//                @Override
//                public void start(int type) {
//                    rlCoverView.setVisibility(View.GONE);
//                }
//
//                @Override
//                public void stop(int type) {
//                    rlCoverView.setVisibility(View.VISIBLE);
//                }
//            });
//        }
        //JCameraView监听
        jCameraView.setJCameraListener(new JCameraListener() {
            @Override
            public void captureSuccess(Bitmap bitmap) {
                bitmapImg = bitmap;
               Intent intent = new Intent(EasyCameraActivity.this, IMGEditActivity.class);
                intent.putExtra(Config.CONGIG_SHOW_NUMBER, number);
                startActivityForResult(intent,Code.REQUEST_EDIT);

            }

            @Override
            public void recordSuccess(final String url, Bitmap firstFrame) {
                //获取视频路径
                if (SystemUtils.beforeAndroidTen()) {
                    //String path = FileUtil.saveBitmap(applicationName, firstFrame);
                    Intent intent = new Intent();
                    intent.putExtra(Key.EXTRA_RESULT_CAPTURE_VIDEO_PATH, url);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    pbProgress.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //String path = FileUtil.saveBitmap(applicationName, firstFrame);
                            final String resUrl = FileUtil.copy2DCIMAndroidQ(EasyCameraActivity.this, url, applicationName);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    pbProgress.setVisibility(View.GONE);
                                    if (!isFinishing()) {
                                        Intent intent = new Intent();
                                        //intent.putExtra(Key.EXTRA_RESULT_CAPTURE_IMAGE_PATH, path);
                                        intent.putExtra(Key.EXTRA_RESULT_CAPTURE_VIDEO_PATH, resUrl);
                                        setResult(RESULT_OK, intent);
                                        finish();
                                    }
                                }
                            });
                        }
                    }).start();
                }

            }
        });

        jCameraView.setLeftClickListener(new ClickListener() {
            @Override
            public void onClick() {
                finish();
            }
        });
    }


    private String initPath() {
        if (storagePath.equals("")) {
            storagePath = parentPath.getAbsolutePath() + File.separator + "myphoto";
            File file = new File(storagePath);
            if (!file.exists()) {
                file.mkdir();
            }
        }
        return storagePath;
    }

    public String saveEditorPhotoJpgPath() {
        return initPath() + File.separator + "editor_" + System.currentTimeMillis() + ".jpg";
    }

    @Override
    protected void onStart() {
        super.onStart();
        //全屏显示
        if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        } else {
            View decorView = getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(option);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if (!Setting.useSystemCamera) {
        jCameraView.onResume();
        //}
    }

    @Override
    protected void onPause() {
        super.onPause();
        //if (!Setting.useSystemCamera) {
        jCameraView.onPause();
        //}
    }

    @Override
    protected void onDestroy() {
        // if (Setting.cameraCoverView != null) Setting.cameraCoverView.clear();
        // Setting.cameraCoverView = null;
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
          //  finish();
            return;
        }
        if (resultCode == RESULT_OK && Code.REQUEST_CAMERA == requestCode && cameraPath != null) {
            Intent intent = new Intent();
            intent.putExtra(Key.EXTRA_RESULT_CAPTURE_IMAGE_PATH, cameraPath);
            setResult(RESULT_OK, intent);
            finish();
        } if (resultCode == RESULT_OK && Code.REQUEST_EDIT == requestCode ) {
            initPath();


            Intent intent = new Intent();
            intent.putExtra(Config.CONGIG_SAVE_PATH, data.getStringExtra(Key.IMAGE_PATH));
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
