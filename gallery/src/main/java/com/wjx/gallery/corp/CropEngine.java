package com.wjx.gallery.corp;

import android.content.Intent;
import android.net.Uri;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-17
 */
public interface CropEngine {
    /**
     * @param uri    要裁剪的图片地址
     * @param desUri 裁剪后图片保存地址
     */
    void crop(AppCompatActivity context, Uri uri, Uri desUri);

    Uri getCropPath(Intent data);

}
