package com.wjx.gallery.corp.impl;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.wjx.gallery.corp.CropEngine;
import com.yalantis.ucrop.UCrop;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-17
 */
public class DefaultCropEngine implements CropEngine {
    @Override
    public void crop(AppCompatActivity context, Uri uri, Uri desUri) {
        Log.e("crop",desUri.toString());
        UCrop.of(uri, desUri)
                .withAspectRatio(1, 1)
                .start(context);
    }
}
