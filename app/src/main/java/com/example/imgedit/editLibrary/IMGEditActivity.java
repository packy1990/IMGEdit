package com.example.imgedit.editLibrary;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.example.imgedit.constant.Key;
import com.example.imgedit.editLibrary.core.IMGMode;
import com.example.imgedit.editLibrary.core.IMGText;
import com.example.imgedit.editLibrary.core.file.IMGAssetFileDecoder;
import com.example.imgedit.editLibrary.core.file.IMGDecoder;
import com.example.imgedit.editLibrary.core.file.IMGFileDecoder;
import com.example.imgedit.editLibrary.core.util.IMGUtils;
import com.example.imgedit.editLibrary.utils.FileUtil;
import com.example.imgedit.editLibrary.utils.SystemUtils;
import com.example.imgedit.utils.UriUtils;
import com.example.imgedit.widget.EasyCameraActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by felix on 2017/11/14 下午2:26.
 */

public class IMGEditActivity extends IMGEditBaseActivity {

    private static final int MAX_WIDTH = 1024;

    private static final int MAX_HEIGHT = 1024;

    public static final String EXTRA_IMAGE_URI = "IMAGE_URI";

    public static final String EXTRA_IMAGE_SAVE_PATH = "IMAGE_SAVE_PATH";
    private String path;
    private String url;
    private String deleteUrl;

    @Override
    public void onCreated() {

    }

    @Override
    public Bitmap getBitmap() {
        return EasyCameraActivity.bitmapImg;
    }

    @Override
    public void onText(IMGText text) {
        mImgView.addStickerText(text);
    }

    @Override
    public void onModeClick(IMGMode mode) {
        IMGMode cm = mImgView.getMode();
        if (cm == mode) {
            mode = IMGMode.NONE;
        }
        mImgView.setMode(mode);
        updateModeUI();

//        if (mode == IMGMode.CLIP) {
//            setOpDisplay(OP_CLIP);
//        }
    }

    @Override
    public void onUndoClick() {
        IMGMode mode = mImgView.getMode();
        if (mode == IMGMode.SHADE) {
            mImgView.undoShade();
        } else if (mode == IMGMode.DOODLE) {
            mImgView.undoDoodle();
        } else if (mode == IMGMode.MOSAIC) {
            mImgView.undoMosaic();
        }
    }

    @Override
    public void onCancelClick() {
       // deletePic();//删除图片
        finish();
    }

    @Override
    public void onDoneClick() {
        //deletePic();//删除图片
        if (SystemUtils.beforeAndroidTen()) {
            path = FileUtil.saveBitmap("photo_edit", mImgView.saveBitmap());
        } else {
            path = FileUtil.saveBitmapAndroidQ(this, "photo_edit", mImgView.saveBitmap());
        }
        Intent intent = new Intent();
        intent.putExtra(Key.IMAGE_PATH, path);
        setResult(RESULT_OK, intent);
        finish();
        return;

    }
    private void deletePic() {
        File file = new File(url);
        //删除系统缩略图
        getContentResolver().delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, MediaStore.Images.Media.DATA + "=?", new String[]{url});
        //删除手机中图片
        file.delete();
    }

    @Override
    public void onCancelClipClick() {
        mImgView.cancelClip();
        setOpDisplay(mImgView.getMode() == IMGMode.CLIP ? OP_CLIP : OP_NORMAL);
    }

    @Override
    public void onDoneClipClick() {
        mImgView.doClip();
        setOpDisplay(mImgView.getMode() == IMGMode.CLIP ? OP_CLIP : OP_NORMAL);
    }

    @Override
    public void onResetClipClick() {
        mImgView.resetClip();
    }

    @Override
    public void onRotateClipClick() {
        mImgView.doRotate();
    }

    @Override
    public void onColorChanged(int checkedColor) {
        mImgView.setPenColor(checkedColor);
    }
}
