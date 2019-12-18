package com.wjx.gallery.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wjx.gallery.R;
import com.wjx.gallery.adapter.AlbumMediaAdapter;
import com.wjx.gallery.entity.Album;
import com.wjx.gallery.entity.Item;
import com.wjx.gallery.entity.SelectionSpec;
import com.wjx.gallery.model.AlbumCollection;
import com.wjx.gallery.model.SelectedItemCollection;
import com.wjx.gallery.ui.fragment.MediaSelectionFragment;
import com.wjx.gallery.utils.MediaStoreCompat;
import com.wjx.gallery.utils.PathUtils;
import com.wjx.gallery.utils.SingleMediaScanner;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-10
 */
public class MatisseActivity extends BaseActivity implements View.OnClickListener, AlbumCollection.AlbumCallbacks,
        MediaSelectionFragment.SelectionProvider, AlbumMediaAdapter.CheckStateListener, AlbumMediaAdapter.OnMediaClickListener, AlbumMediaAdapter.OnPhotoCapture {
    public static final String EXTRA_RESULT_SELECTION = "extra_result_selection";
    public static final String EXTRA_RESULT_SELECTION_PATH = "extra_result_selection_path";
    public static final String EXTRA_RESULT_ORIGINAL_ENABLE = "extra_result_original_enable";
    public static final String EXTRA_RESULT_CROP = "extra_result_crop";
    private static final int REQUEST_CODE_PREVIEW = 23;
    private static final int REQUEST_CODE_CAPTURE = 24;
    private static final int REQUEST_CODE_CORP = 69;
    public static final String CHECK_STATE = "checkState";
    private SelectionSpec mSpec;
    private MediaStoreCompat mMediaStoreCompat;
    private SelectedItemCollection mSelectedCollection = new SelectedItemCollection(this);
    private final AlbumCollection mAlbumCollection = new AlbumCollection();
    private boolean mOriginalEnable;
    private RelativeLayout mRlTitle;
    private ImageView mBack;
    private TextView mTvCancel;
    private RelativeLayout mRlBottom;
    private TextView mTvPreview;
    private TextView mTvSure;
    private FrameLayout mContainer;
    private FrameLayout mEmptyView;
    private Uri mCropOutUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSpec = SelectionSpec.getInstance();
        setTheme(mSpec.themeId);
        if (!mSpec.hasInited) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }
        setContentView(R.layout.gallery_matisse_act);
        //是否需要设置横竖屏
        if (mSpec.needOrientationRestriction()) {
            setRequestedOrientation(mSpec.orientation);
        }
        //是否开启相册拍照功能(默认false)
        if (mSpec.capture) {
            mMediaStoreCompat = new MediaStoreCompat(this);
            //需要配置file_provider路径
            if (mSpec.captureStrategy == null)
                throw new RuntimeException("Don't forget to set CaptureStrategy.");
            mMediaStoreCompat.setCaptureStrategy(mSpec.captureStrategy);
        }
        initView();

        mSelectedCollection.onCreate(savedInstanceState);
        //设置已选中图片或视频
        if (mSpec.selectedList != null && !mSpec.selectedList.isEmpty()) {
            mSelectedCollection.setDefaultSelection(mSpec.selectedList);
        }

        if (savedInstanceState != null) {
            mOriginalEnable = savedInstanceState.getBoolean(CHECK_STATE);
        }
        updateBottomToolbar();

        mAlbumCollection.onCreate(this, this);
        mAlbumCollection.onRestoreInstanceState(savedInstanceState);
        mAlbumCollection.loadAlbums();
    }

    private void initView() {
        mRlTitle = findViewById(R.id.rl_title);
        mBack = findViewById(R.id.gallery_back);
        mBack.setOnClickListener(this);
        mTvCancel = findViewById(R.id.tv_cancel);
        mTvCancel.setOnClickListener(this);
        mRlBottom = findViewById(R.id.rl_bottom);
        mTvPreview = findViewById(R.id.tv_preview);
        mTvPreview.setOnClickListener(this);
        mTvSure = findViewById(R.id.tv_sure);
        mTvSure.setOnClickListener(this);
        mContainer = findViewById(R.id.container);
        mEmptyView = findViewById(R.id.empty_view);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mSelectedCollection.onSaveInstanceState(outState);
        mAlbumCollection.onSaveInstanceState(outState);
        outState.putBoolean("checkState", mOriginalEnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAlbumCollection.onDestroy();
        mSpec.onCheckedListener = null;
        mSpec.onSelectedListener = null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        setResult(Activity.RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK)
            return;

        if (requestCode == REQUEST_CODE_PREVIEW) {
            Bundle resultBundle = data.getBundleExtra(BasePreviewActivity.EXTRA_RESULT_BUNDLE);
            ArrayList<Item> selected = resultBundle.getParcelableArrayList(SelectedItemCollection.STATE_SELECTION);
            mOriginalEnable = data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, false);
            int collectionType = resultBundle.getInt(SelectedItemCollection.STATE_COLLECTION_TYPE,
                    SelectedItemCollection.COLLECTION_UNDEFINED);
            if (data.getBooleanExtra(BasePreviewActivity.EXTRA_RESULT_APPLY, false)) {
                Intent result = new Intent();
                ArrayList<Uri> selectedUris = new ArrayList<>();
                ArrayList<String> selectedPaths = new ArrayList<>();
                if (selected != null) {
                    for (Item item : selected) {
                        selectedUris.add(item.getContentUri());
                        selectedPaths.add(PathUtils.getPath(this, item.getContentUri()));
                    }
                }
                result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selectedUris);
                result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPaths);
                result.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
                setResult(RESULT_OK, result);
                finish();
            } else {
                mSelectedCollection.overwrite(selected, collectionType);
                Fragment mediaSelectionFragment = getSupportFragmentManager().findFragmentByTag(
                        MediaSelectionFragment.class.getSimpleName());
                if (mediaSelectionFragment instanceof MediaSelectionFragment) {
                    ((MediaSelectionFragment) mediaSelectionFragment).refreshMediaGrid();
                }
                updateBottomToolbar();
            }
        } else if (requestCode == REQUEST_CODE_CAPTURE) {
            // Just pass the data back to previous calling Activity.
            Uri contentUri = mMediaStoreCompat.getCurrentPhotoUri();
            String path = mMediaStoreCompat.getCurrentPhotoPath();
            ArrayList<Uri> selected = new ArrayList<>();
            selected.add(contentUri);
            ArrayList<String> selectedPath = new ArrayList<>();
            selectedPath.add(path);
            Intent result = new Intent();
            result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selected);
            result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPath);
            setResult(RESULT_OK, result);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
                MatisseActivity.this.revokeUriPermission(contentUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

            new SingleMediaScanner(this.getApplicationContext(), path, () -> Log.i("SingleMediaScanner", "scan finish!"));
            finish();
        } else if (requestCode == REQUEST_CODE_CORP) {
            Intent result = new Intent();
            result.putExtra(EXTRA_RESULT_CROP, mCropOutUri);
            setResult(RESULT_OK, result);
            finish();
        }
    }

    private void updateBottomToolbar() {
        int selectedCount = mSelectedCollection.count();
        if (selectedCount == 0) {
            mTvPreview.setEnabled(false);
            mTvSure.setEnabled(false);
            mTvSure.setText(R.string.gallery_sure);
        } else if (selectedCount == 1 && mSpec.singleSelectionModeEnabled()) {
            mTvPreview.setEnabled(true);
            mTvSure.setEnabled(true);
            mTvSure.setText(R.string.gallery_sure);
        } else {
            mTvPreview.setEnabled(true);
            mTvSure.setEnabled(true);
            mTvSure.setText(getString(R.string.gallery_sure_count, selectedCount));
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.gallery_back) {
            finish();
        } else if (v.getId() == R.id.tv_cancel) {
            finish();
        } else if (v.getId() == R.id.tv_preview) {
            Intent intent = new Intent(this, SelectedPreviewActivity.class);
            intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
            intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            startActivityForResult(intent, REQUEST_CODE_PREVIEW);
        } else if (v.getId() == R.id.tv_sure) {
            Intent result = new Intent();
            ArrayList<Uri> selectedUris = (ArrayList<Uri>) mSelectedCollection.asListOfUri();
            result.putParcelableArrayListExtra(EXTRA_RESULT_SELECTION, selectedUris);
            ArrayList<String> selectedPaths = (ArrayList<String>) mSelectedCollection.asListOfString();
            result.putStringArrayListExtra(EXTRA_RESULT_SELECTION_PATH, selectedPaths);
            result.putExtra(EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            setResult(RESULT_OK, result);
            finish();
        }
    }

    @Override
    public void onAlbumLoad(final Cursor cursor) {
        // select default album.
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            cursor.moveToPosition(mAlbumCollection.getCurrentSelection());
            Album album = Album.valueOf(cursor);
            if (album.isAll() && SelectionSpec.getInstance().capture) {
                album.addCaptureCount();
            }
            onAlbumSelected(album);
        });
    }

    @Override
    public void onAlbumReset() {
    }

    /**
     * 判断album是否为empty,不为空通过fragment显示图片或视频
     */
    private void onAlbumSelected(Album album) {
        if (album.isAll() && album.isEmpty()) {
            mContainer.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mContainer.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
            Fragment fragment = MediaSelectionFragment.newInstance(album);
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container, fragment, MediaSelectionFragment.class.getSimpleName())
                    .commitAllowingStateLoss();
        }
    }

    @Override
    public SelectedItemCollection provideSelectedItemCollection() {
        return mSelectedCollection;
    }

    @Override
    public void onUpdate() {
        updateBottomToolbar();
        if (mSpec.onSelectedListener != null) {
            mSpec.onSelectedListener.onSelected(
                    mSelectedCollection.asListOfUri(), mSelectedCollection.asListOfString());
        }
    }

    @Override
    public void onMediaClick(Album album, Item item, int position) {
        if (mSpec.imageCrop) {
            mCropOutUri = Uri.fromFile(new File(getCacheDir(), UUID.randomUUID() + ".png"));
            mSpec.cropEngine.crop(this, item.getContentUri(), mCropOutUri);
        } else {
            Intent intent = new Intent(this, AlbumPreviewActivity.class);
            intent.putExtra(AlbumPreviewActivity.EXTRA_ALBUM, album);
            intent.putExtra(AlbumPreviewActivity.EXTRA_ITEM, item);
            intent.putExtra(BasePreviewActivity.EXTRA_DEFAULT_BUNDLE, mSelectedCollection.getDataWithBundle());
            intent.putExtra(BasePreviewActivity.EXTRA_RESULT_ORIGINAL_ENABLE, mOriginalEnable);
            startActivityForResult(intent, REQUEST_CODE_PREVIEW);
        }
    }

    @Override
    public void capture() {
        if (mMediaStoreCompat != null) {
            mMediaStoreCompat.dispatchCaptureIntent(this, REQUEST_CODE_CAPTURE);
        }
    }
}
