package com.wjx.gallery.adapter;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wjx.gallery.R;
import com.wjx.gallery.entity.Item;
import com.wjx.gallery.entity.SelectionSpec;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-13
 */
public class PreviewThumbAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<Item> mData;
    private int mPosition;
    private OnItemClickListener onItemClickListener;

    public PreviewThumbAdapter(Context context, List<Item> data, OnItemClickListener onItemClickListener) {
        this.mContext = context;
        this.mData = data;
        this.onItemClickListener = onItemClickListener;
    }

    public void setData(List<Item> data) {
        if (mData == null) {
            mData = new ArrayList<>();
            mData.addAll(data);
        } else {
            mData.clear();
            mData.addAll(data);
        }
    }

    public void addItem(Item item) {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mPosition = mData.size();
        mData.add(mPosition, item);
        notifyItemChanged(mPosition - 1 < 0 ? 0 : mPosition - 1, "refresh");
        notifyItemChanged(mPosition, "refresh");
        notifyItemInserted(mPosition);
    }

    public void removeItem(Item item) {
        int itemPosition = getItemPosition(item);
        mData.remove(itemPosition);
        notifyItemRemoved(itemPosition);
        notifyItemRangeChanged(itemPosition, getItemCount());
        notifyItemChanged(itemPosition - 1, "refresh");
    }

    public int getItemPosition(Item item) {
        int index = -1;
        if (mData.contains(item)) {
            index = mData.indexOf(item);
        }
        return index;
    }

    public void setSelectItem(Item item) {
        notifyItemChanged(mPosition, "refresh");
        mPosition = getItemPosition(item);
        notifyItemChanged(mPosition, "refresh");
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gallery_item_thumb, parent, false);
        return new ThumbViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ThumbViewHolder) {
            ThumbViewHolder thumbViewHolder = (ThumbViewHolder) holder;

            if (mData.get(position)
                    .isVideo()) {
                SelectionSpec.getInstance().imageEngine.loadThumbnailCorner(mContext, R.drawable.gallery_placeholder_video, 3, thumbViewHolder.mIvThumbItem, mData.get(position).uri);
                thumbViewHolder.mThumbVideoDuration.setVisibility(View.VISIBLE);
                thumbViewHolder.mThumbVideoDuration.setText(DateUtils.formatElapsedTime(mData.get(position).duration / 1000));
            } else {
                SelectionSpec.getInstance().imageEngine.loadThumbnailCorner(mContext, R.drawable.gallery_placeholder, 3, thumbViewHolder.mIvThumbItem, mData.get(position).uri);
                thumbViewHolder.mThumbVideoDuration.setVisibility(View.INVISIBLE);
            }

            thumbViewHolder.mIvThumbItem.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(mData.get(position));
                }
                mPosition = position;
            });
            if (mPosition == position) {
                thumbViewHolder.mIvThumbItemSelect.setVisibility(View.VISIBLE);
            } else {
                thumbViewHolder.mIvThumbItemSelect.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            onBindViewHolder(holder, position);
        } else {
            if (holder instanceof ThumbViewHolder) {
                ThumbViewHolder thumbViewHolder = (ThumbViewHolder) holder;
                if (mPosition == position) {
                    thumbViewHolder.mIvThumbItemSelect.setVisibility(View.VISIBLE);
                } else {
                    thumbViewHolder.mIvThumbItemSelect.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ThumbViewHolder extends RecyclerView.ViewHolder {

        private ImageView mIvThumbItem;
        private ImageView mIvThumbItemSelect;
        private TextView mThumbVideoDuration;

        public ThumbViewHolder(@NonNull View itemView) {
            super(itemView);
            mIvThumbItem = itemView.findViewById(R.id.iv_thumb_item);
            mIvThumbItemSelect = itemView.findViewById(R.id.iv_thumb_item_select);
            mThumbVideoDuration = itemView.findViewById(R.id.thumb_video_duration);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }
}
