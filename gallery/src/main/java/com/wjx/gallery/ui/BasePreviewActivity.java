package com.wjx.gallery.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.wjx.gallery.R;
import com.wjx.gallery.adapter.PreviewPagerAdapter;
import com.wjx.gallery.adapter.PreviewThumbAdapter;
import com.wjx.gallery.adapter.SpacesItemDecoration;
import com.wjx.gallery.entity.IncapableCause;
import com.wjx.gallery.entity.Item;
import com.wjx.gallery.entity.SelectionSpec;
import com.wjx.gallery.listener.OnFragmentInteractionListener;
import com.wjx.gallery.model.SelectedItemCollection;
import com.wjx.gallery.utils.Platform;
import com.wjx.gallery.utils.UIUtils;
import com.wjx.gallery.widget.CheckView;
import com.wjx.gallery.widget.PreviewViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-11
 */
public abstract class BasePreviewActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener, OnFragmentInteractionListener {

    public static final String EXTRA_DEFAULT_BUNDLE = "extra_default_bundle";
    public static final String EXTRA_RESULT_BUNDLE = "extra_result_bundle";
    public static final String EXTRA_RESULT_APPLY = "extra_result_apply";
    public static final String EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable";
    public static final String CHECK_STATE = "checkState";
    protected SelectionSpec mSpec;
    protected final SelectedItemCollection mSelectedCollection = new SelectedItemCollection(this);
    protected boolean mOriginalEnable;
    private RelativeLayout mRlTitle;
    private ImageView mIvBack;
    protected CheckView mCheckView;
    protected PreviewViewPager mViewPager;
    private LinearLayout mLlBottomView;
    private RecyclerView mRecyclerView;
    private TextView mTvSure;
    protected PreviewPagerAdapter mAdapter;
    protected PreviewThumbAdapter mThumbAdapter;
    private boolean mIsToolbarHide = false;
    protected int mPreviousPos = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(SelectionSpec.getInstance().themeId);
        super.onCreate(savedInstanceState);
        if (!SelectionSpec.getInstance().hasInited) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        setContentView(R.layout.gallery_base_preview_act);
        if (Platform.hasKitKat()) {
            //透明状态栏
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

//        updatePreviewType();
        mSpec = SelectionSpec.getInstance();
        if (mSpec.needOrientationRestriction()) {
            setRequestedOrientation(mSpec.orientation);
        }

        if (savedInstanceState == null) {
            mSelectedCollection.onCreate(getIntent().getBundleExtra(EXTRA_DEFAULT_BUNDLE));
            mOriginalEnable = getIntent().getBooleanExtra(EXTRA_RESULT_ORIGINAL_ENABLE, false);
        } else {
            mSelectedCollection.onCreate(savedInstanceState);
            mOriginalEnable = savedInstanceState.getBoolean(CHECK_STATE);
        }
        initView();
        updateApplyButton();
        setThumbData();

    }

    private void initView() {
        mRlTitle = findViewById(R.id.rl_title);
        mIvBack = findViewById(R.id.iv_back);
        mIvBack.setOnClickListener(this);
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.addOnPageChangeListener(this);
        mAdapter = new PreviewPagerAdapter(getSupportFragmentManager(), null);
        mViewPager.setAdapter(mAdapter);
        mLlBottomView = findViewById(R.id.ll_bottom_view);
        mRecyclerView = findViewById(R.id.recyclerview);
        mThumbAdapter = new PreviewThumbAdapter(this, new ArrayList<>(), item -> {
            int itemPosition = mAdapter.getItemIndex(item);
            mViewPager.setCurrentItem(itemPosition, false);
        });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(100);
        defaultItemAnimator.setRemoveDuration(100);
        mRecyclerView.setItemAnimator(defaultItemAnimator);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(0, UIUtils.dp2px(this, 25), UIUtils.dp2px(this, 3), 0, UIUtils.dp2px(this, 25)));
        mRecyclerView.setAdapter(mThumbAdapter);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mTvSure = findViewById(R.id.tv_sure);
        mTvSure.setOnClickListener(this);
        mCheckView = findViewById(R.id.check_view);
        mCheckView.setCountable(mSpec.countable);
        mCheckView.setOnClickListener(v -> {
            Item item = mAdapter.getMediaItem(mViewPager.getCurrentItem());
            if (mSelectedCollection.isSelected(item)) {
                mSelectedCollection.remove(item);
                mThumbAdapter.removeItem(item);
                if (mSpec.countable) {
                    mCheckView.setCheckedNum(CheckView.UNCHECKED);
                } else {
                    mCheckView.setChecked(false);
                }
            } else {
                if (assertAddSelection(item)) {
                    mSelectedCollection.add(item);
                    mThumbAdapter.addItem(item);
                    if (mSpec.countable) {
                        mCheckView.setCheckedNum(mSelectedCollection.checkedNumOf(item));
                    } else {
                        mCheckView.setChecked(true);
                    }
                }
            }
            updateApplyButton();
            thumbScrollToPosition();

            if (mSpec.onSelectedListener != null) {
                mSpec.onSelectedListener.onSelected(
                        mSelectedCollection.asListOfUri(), mSelectedCollection.asListOfString());
            }
        });

    }

    private void setThumbData() {
        Bundle bundle = mSelectedCollection.getDataWithBundle();
        List<Item> selected = bundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
        mThumbAdapter.setData(selected);
        mThumbAdapter.notifyDataSetChanged();
    }

    private void thumbScrollToPosition() {
        //缩略图添加数据，定位的位置
        if (mPreviousPos != -1) {
            Item item = mAdapter.getMediaItem(mViewPager.getCurrentItem());
            mThumbAdapter.setSelectItem(item);
            mRecyclerView.scrollToPosition(mThumbAdapter.getItemPosition(item));
        }
    }

    private void updateApplyButton() {
        int selectedCount = mSelectedCollection.count();
        if (selectedCount == 0) {
            mTvSure.setEnabled(false);
            mTvSure.setText(R.string.gallery_sure);
        } else if (selectedCount == 1 && mSpec.singleSelectionModeEnabled()) {
            mTvSure.setEnabled(true);
            mTvSure.setText(R.string.gallery_sure);
        } else {
            mTvSure.setEnabled(true);
            mTvSure.setText(getString(R.string.gallery_sure_count, selectedCount));
        }
    }

    @Override
    public void onBackPressed() {
        sendBackResult(false);
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.iv_back){
            onBackPressed();
        }else if (v.getId()==R.id.tv_sure){
            sendBackResult(true);
            finish();
        }

    }


    protected void sendBackResult(boolean apply) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_RESULT_BUNDLE, mSelectedCollection.getDataWithBundle());
        intent.putExtra(EXTRA_RESULT_APPLY, apply);
        intent.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
        setResult(Activity.RESULT_OK, intent);
    }

    private boolean assertAddSelection(Item item) {
        IncapableCause cause = mSelectedCollection.isAcceptable(item);
        IncapableCause.handleCause(this, cause);
        return cause == null;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        PreviewPagerAdapter adapter = (PreviewPagerAdapter) mViewPager.getAdapter();
        Item item = adapter.getMediaItem(position);
        int itemPosition = mThumbAdapter.getItemPosition(item);
        mThumbAdapter.setSelectItem(item);
        mRecyclerView.scrollToPosition(itemPosition);
        if (mPreviousPos != -1 && mPreviousPos != position) {
            if (mSpec.countable) {
                int checkedNum = mSelectedCollection.checkedNumOf(item);
                mCheckView.setCheckedNum(checkedNum);
                if (checkedNum > 0) {
                    mCheckView.setEnabled(true);
                } else {
                    mCheckView.setEnabled(!mSelectedCollection.maxSelectableReached());
                }
            } else {
                boolean checked = mSelectedCollection.isSelected(item);
                mCheckView.setChecked(checked);
                if (checked) {
                    mCheckView.setEnabled(true);
                } else {
                    mCheckView.setEnabled(!mSelectedCollection.maxSelectableReached());
                }
            }
        }

        mPreviousPos = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onClick() {
        if (mIsToolbarHide) {
            //显示
            TranslateAnimation showAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, -1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);
            showAnim.setDuration(300);
            mRlTitle.startAnimation(showAnim);
            mRlTitle.setVisibility(View.VISIBLE);

            TranslateAnimation bottomShowAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f);
            bottomShowAnim.setDuration(300);
            mLlBottomView.startAnimation(bottomShowAnim);
            mLlBottomView.setVisibility(View.VISIBLE);

        } else {
            //隐藏
            TranslateAnimation showAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, -1.0f);
            showAnim.setDuration(300);
            mRlTitle.startAnimation(showAnim);
            mRlTitle.setVisibility(View.INVISIBLE);

            TranslateAnimation bottomShowAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 1.0f);

            bottomShowAnim.setDuration(300);
            mLlBottomView.startAnimation(bottomShowAnim);
            mLlBottomView.setVisibility(View.INVISIBLE);


        }
        mIsToolbarHide = !mIsToolbarHide;
    }
}
