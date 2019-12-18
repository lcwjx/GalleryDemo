package com.wjx.gallery.adapter;

import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.wjx.gallery.entity.Item;
import com.wjx.gallery.ui.fragment.PreviewItemFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-12
 */
public class PreviewPagerAdapter extends FragmentPagerAdapter {

    private ArrayList<Item> mItems = new ArrayList<>();
    private OnPrimaryItemSetListener mListener;

    public PreviewPagerAdapter(FragmentManager manager, OnPrimaryItemSetListener listener) {
        super(manager);
        mListener = listener;
    }

    @Override
    public Fragment getItem(int position) {
        return PreviewItemFragment.newInstance(mItems.get(position));
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        if (mListener != null) {
            mListener.onPrimaryItemSet(position);
        }
    }

    public Item getMediaItem(int position) {
        return mItems.get(position);
    }

    public int getItemIndex(Item item) {
        int index = -1;
        if (mItems.contains(item)) {
            index = mItems.indexOf(item);
        }
        return index;
    }

    public void addAll(List<Item> items) {
        mItems.addAll(items);
    }

    interface OnPrimaryItemSetListener {

        void onPrimaryItemSet(int position);
    }

}
