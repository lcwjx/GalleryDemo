package com.wjx.gallery.adapter;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
  private int mLeft;
  private int mTop;
  private int mRight;
  private int mBottom;
  private int mFirstAndLast;

  public SpacesItemDecoration(int left, int top, int right, int bottom, int firstAndLast) {
    this.mLeft = left;
    this.mTop = top;
    this.mRight = right;
    this.mBottom = bottom;
    this.mFirstAndLast = firstAndLast;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
    LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
    outRect.left = mLeft;
    outRect.right = mRight;
    outRect.bottom = mBottom;
    outRect.top = mTop;
    // Add top margin only for the first item to avoid double space between items
    if (parent.getChildAdapterPosition(view) == 0) {
      outRect.left = mFirstAndLast;
    }
    if (parent.getChildAdapterPosition(view) == layoutManager.getItemCount() - 1) {
      outRect.right = mFirstAndLast;
    }
  }
}