package com.wjx.gallery.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.wjx.gallery.entity.Item;
import com.wjx.gallery.entity.SelectionSpec;
import com.wjx.gallery.model.SelectedItemCollection;

import java.util.List;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-12
 */
public class SelectedPreviewActivity extends BasePreviewActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!SelectionSpec.getInstance().hasInited) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        Bundle bundle = getIntent().getBundleExtra(EXTRA_DEFAULT_BUNDLE);
        List<Item> selected = bundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
        mAdapter.addAll(selected);
        mAdapter.notifyDataSetChanged();
        if (mSpec.countable) {
            mCheckView.setCheckedNum(1);
        } else {
            mCheckView.setChecked(true);
        }
        mPreviousPos = 0;
//        updateSize(selected.get(0));
    }

}
