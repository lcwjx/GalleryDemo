package com.wjx.gallery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wjx.gallery.ui.MatisseActivity;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Set;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-10
 */
public class GalleryFinal {
    private final WeakReference<Activity> mContext;
    private final WeakReference<Fragment> mFragment;

    private GalleryFinal(Activity activity) {
        this(activity, null);
    }

    private GalleryFinal(Fragment fragment) {
        this(fragment.getActivity(), fragment);
    }

    private GalleryFinal(Activity activity, Fragment fragment) {
        mContext = new WeakReference<>(activity);
        mFragment = new WeakReference<>(fragment);
    }

    public static GalleryFinal with(Activity activity) {
        return new GalleryFinal(activity);
    }

    public static GalleryFinal with(Fragment fragment) {
        return new GalleryFinal(fragment);
    }

    /**
     * Obtain user selected media' {@link Uri} list in the starting Activity or Fragment.
     *
     * @param data Intent passed by {@link Activity#onActivityResult(int, int, Intent)} or
     *             {@link Fragment#onActivityResult(int, int, Intent)}.
     * @return User selected media' {@link Uri} list.
     */
    public static List<Uri> obtainResult(Intent data) {
        return data.getParcelableArrayListExtra(MatisseActivity.EXTRA_RESULT_SELECTION);
    }

    /**
     * Obtain user selected media path list in the starting Activity or Fragment.
     *
     * @param data Intent passed by {@link Activity#onActivityResult(int, int, Intent)} or
     *             {@link Fragment#onActivityResult(int, int, Intent)}.
     * @return User selected media path list.
     */
    public static List<String> obtainPathResult(Intent data) {
        return data.getStringArrayListExtra(MatisseActivity.EXTRA_RESULT_SELECTION_PATH);
    }

    /**
     * 图片或视频类型选择限制,默认MimeType.ofImage()类型
     */
    public SelectionCreator choose() {
        return this.choose(MimeType.ofImage(), false);
    }

    /**
     * 图片或视频类型选择限制,默认MimeType.ofImage()类型
     *
     * @param mimeTypes mimeTypes
     */
    public SelectionCreator choose(Set<MimeType> mimeTypes) {
        return this.choose(mimeTypes, false);
    }

    /**
     * 图片或视频类型选择限制,默认MimeType.ofImage()类型
     *
     * @param mimeTypes          mimeTypes
     * @param mediaTypeExclusive 是否可以同时选中视频和图片,默认false能同时选中
     */
    public SelectionCreator choose(Set<MimeType> mimeTypes, boolean mediaTypeExclusive) {
        return new SelectionCreator(this, mimeTypes, mediaTypeExclusive);
    }

    @Nullable
    Activity getActivity() {
        return mContext.get();
    }

    @Nullable
    Fragment getFragment() {
        return mFragment != null ? mFragment.get() : null;
    }


    public static Uri obtainCropResult(Intent data) {
        if (data == null)
            new NullPointerException("Intent is null");
        return data.getParcelableExtra(MatisseActivity.EXTRA_RESULT_CROP);
    }
}
