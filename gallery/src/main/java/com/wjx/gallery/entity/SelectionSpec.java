package com.wjx.gallery.entity;

import android.content.pm.ActivityInfo;

import androidx.annotation.StyleRes;

import com.wjx.gallery.MimeType;
import com.wjx.gallery.R;
import com.wjx.gallery.engine.CropEngine;
import com.wjx.gallery.engine.impl.DefaultCropEngine;
import com.wjx.gallery.engine.ImageEngine;
import com.wjx.gallery.engine.impl.GlideEngine;
import com.wjx.gallery.filter.Filter;
import com.wjx.gallery.listener.OnCheckedListener;
import com.wjx.gallery.listener.OnSelectedListener;

import java.util.List;
import java.util.Set;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-10
 */
public final class SelectionSpec {

    public Set<MimeType> mimeTypeSet;
    public boolean mediaTypeExclusive;
    public boolean showSingleMediaType;
    @StyleRes
    public int themeId;
    public int orientation;
    public boolean countable;
    public int maxSelectable;
    public int maxImageSelectable;
    public int maxVideoSelectable;
    public List<Filter> filters;
    public List<Item> selectedList;
    public boolean capture;
    public CaptureStrategy captureStrategy;
    public int spanCount;
    public int gridExpectedSize;
    public float thumbnailScale;
    public ImageEngine imageEngine;
    public CropEngine cropEngine;
    public boolean hasInited;
    public OnSelectedListener onSelectedListener;
    public boolean originalable;
    public boolean autoHideToobar;
    public int originalMaxSize;
    public OnCheckedListener onCheckedListener;
    public boolean showPreview;
    public boolean imageCrop;

    private SelectionSpec() {
    }

    public static SelectionSpec getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public static SelectionSpec getCleanInstance() {
        SelectionSpec selectionSpec = getInstance();
        selectionSpec.reset();
        return selectionSpec;
    }

    private void reset() {
        mimeTypeSet = null;
        mediaTypeExclusive = true;
        showSingleMediaType = false;
        themeId = R.style.Gallery_theme;
        orientation = 0;
        countable = true;
        maxSelectable = 1;
        maxImageSelectable = 0;
        maxVideoSelectable = 0;
        filters = null;
        selectedList = null;
        capture = false;
        captureStrategy = null;
        spanCount = 4;
        gridExpectedSize = 0;
        thumbnailScale = 0.5f;
        imageEngine = new GlideEngine();
        cropEngine = new DefaultCropEngine();
        hasInited = true;
        originalable = false;
        autoHideToobar = false;
        originalMaxSize = Integer.MAX_VALUE;
        showPreview = true;
        imageCrop = false;
    }

    public boolean singleSelectionModeEnabled() {
        return !countable && (maxSelectable == 1 || (maxImageSelectable == 1 && maxVideoSelectable == 1));
    }

    public boolean needOrientationRestriction() {
        return orientation != ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
    }

    public boolean onlyShowImages() {
        return showSingleMediaType && MimeType.ofImage().containsAll(mimeTypeSet);
    }

    public boolean onlyShowVideos() {
        return showSingleMediaType && MimeType.ofVideo().containsAll(mimeTypeSet);
    }

    public boolean onlyShowGif() {
        return showSingleMediaType && MimeType.ofGif().equals(mimeTypeSet);
    }

    private static final class InstanceHolder {
        private static final SelectionSpec INSTANCE = new SelectionSpec();
    }
}

