package com.wjx.gallery.ui;

import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.wjx.gallery.adapter.PreviewPagerAdapter;
import com.wjx.gallery.entity.Album;
import com.wjx.gallery.entity.Item;
import com.wjx.gallery.entity.SelectionSpec;
import com.wjx.gallery.model.AlbumMediaCollection;
import com.wjx.gallery.model.SelectedItemCollection;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-13
 */
public class AlbumPreviewActivity extends BasePreviewActivity implements AlbumMediaCollection.AlbumMediaCallbacks {
    public static final String EXTRA_ALBUM = "extra_album";
    public static final String EXTRA_ITEM = "extra_item";
    private AlbumMediaCollection mCollection = new AlbumMediaCollection();
    private boolean mIsAlreadySetPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SelectionSpec.getInstance().hasInited) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        mCollection.onCreate(this, this);
        Album album = getIntent().getParcelableExtra(EXTRA_ALBUM);
        mCollection.load(album);

        Item item = getIntent().getParcelableExtra(EXTRA_ITEM);
        if (mSpec.countable) {
            mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(item));
        } else {
            mCheckView.setChecked(mSelectedCollection.isSelected(item));
        }
        Bundle bundle = getIntent().getBundleExtra(EXTRA_DEFAULT_BUNDLE);
        List<Item> selected = bundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
        //缩略图
        mThumbAdapter.setData(selected);
        mThumbAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAlbumMediaLoad(Cursor cursor) {
        List<Item> items = new ArrayList<>();
        while (cursor.moveToNext()) {
            items.add(Item.valueOf(cursor));
        }

        if (items.isEmpty()) {
            return;
        }

        PreviewPagerAdapter adapter = (PreviewPagerAdapter) mViewPager.getAdapter();
        adapter.addAll(items);
        adapter.notifyDataSetChanged();
        if (!mIsAlreadySetPosition) {
            mIsAlreadySetPosition = true;
            Item selected = getIntent().getParcelableExtra(EXTRA_ITEM);
            int selectedIndex = items.indexOf(selected);
            mViewPager.setCurrentItem(selectedIndex, false);
            mPreviousPos = selectedIndex;
        }
    }

    @Override
    public void onAlbumMediaReset() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCollection.onDestroy();
    }
}
