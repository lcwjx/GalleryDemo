package com.wjx.gallery.filter;

import android.content.Context;

import com.wjx.gallery.MimeType;
import com.wjx.gallery.entity.IncapableCause;
import com.wjx.gallery.entity.Item;

import java.util.Set;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-10
 */
public abstract class Filter {
    /**
     * Convenient constant for a minimum value.
     */
    public static final int MIN = 0;
    /**
     * Convenient constant for a maximum value.
     */
    public static final int MAX = Integer.MAX_VALUE;
    /**
     * Convenient constant for 1024.
     */
    public static final int K = 1024;

    /**
     * Against what mime types this filter applies.
     */
    protected abstract Set<MimeType> constraintTypes();

    /**
     * Invoked for filtering each item.
     *
     * @return null if selectable, {@link IncapableCause} if not selectable.
     */
    public abstract IncapableCause filter(Context context, Item item);

    /**
     * Whether an {@link Item} need filtering.
     */
    protected boolean needFiltering(Context context, Item item) {
        for (MimeType type : constraintTypes()) {
            if (type.checkType(context.getContentResolver(), item.getContentUri())) {
                return true;
            }
        }
        return false;
    }
}
