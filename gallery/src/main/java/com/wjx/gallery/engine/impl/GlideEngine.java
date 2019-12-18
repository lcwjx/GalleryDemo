package com.wjx.gallery.engine.impl;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.wjx.gallery.engine.ImageEngine;
import com.wjx.gallery.utils.UIUtils;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-10
 */
public class GlideEngine implements ImageEngine {
    @Override
    public void loadThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(new RequestOptions()
                        .override(resize)
                        .placeholder(placeholder)
                        .dontAnimate()
                        .centerCrop())
                .into(imageView);
    }

    @Override
    public void loadGifThumbnail(Context context, int resize, Drawable placeholder, ImageView imageView, Uri uri) {
        Glide.with(context)
                .asBitmap() // some .jpeg files are actually gif
                .load(uri)
                .apply(new RequestOptions()
                        .override(resize, resize)
                        .placeholder(placeholder)
                        .dontAnimate()
                        .centerCrop())
                .into(imageView);
    }

    @Override
    public void loadImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Glide.with(context)
                .load(uri)
                .apply(new RequestOptions()
                        .override(resizeX, resizeY)
                        .priority(Priority.HIGH)
                        .dontAnimate()
                        .fitCenter())

                .into(imageView);
    }

    @Override
    public void loadGifImage(Context context, int resizeX, int resizeY, ImageView imageView, Uri uri) {
        Glide.with(context)
                .asGif()
                .load(uri)
                .apply(new RequestOptions()
                        .override(resizeX, resizeY)
                        .priority(Priority.HIGH)
                        .dontAnimate()
                        .fitCenter())
                .into(imageView);
    }

    @Override
    public void loadThumbnailCorner(Context context, int resourceId, int roundingRadius, ImageView imageView, Uri uri) {
        Glide.with(context)
                .asBitmap()
                .load(uri)
                .apply(new RequestOptions()
                        .transforms(new CenterCrop(), new RoundedCorners(UIUtils.dp2px(context, 3)))
                        .placeholder(resourceId)
                        .dontAnimate())
                .into(imageView);
    }

    @Override
    public boolean supportAnimatedGif() {
        return true;
    }
}
