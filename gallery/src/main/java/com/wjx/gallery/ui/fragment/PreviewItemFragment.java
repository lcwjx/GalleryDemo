package com.wjx.gallery.ui.fragment;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.wjx.gallery.R;
import com.wjx.gallery.entity.Item;
import com.wjx.gallery.entity.SelectionSpec;
import com.wjx.gallery.listener.OnFragmentInteractionListener;
import com.wjx.gallery.utils.PhotoMetadataUtils;
import com.wjx.gallery.widget.ImageViewTouch;

import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2019-12-12
 */
public class PreviewItemFragment extends Fragment {

    private static final String ARGS_ITEM = "args_item";
    private OnFragmentInteractionListener mListener;

    public static PreviewItemFragment newInstance(Item item) {
        PreviewItemFragment fragment = new PreviewItemFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(ARGS_ITEM, item);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gallery_preview_item_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final Item item = getArguments().getParcelable(ARGS_ITEM);
        if (item == null) {
            return;
        }

        View videoPlayButton = view.findViewById(R.id.video_play_button);
        if (item.isVideo()) {
            videoPlayButton.setVisibility(View.VISIBLE);
            videoPlayButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setDataAndType(item.uri, "video/*");
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(getContext(), "No App found supporting video preview", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            videoPlayButton.setVisibility(View.GONE);
        }

        ImageViewTouch image = view.findViewById(R.id.image_view);
        image.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        image.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
            @Override
            public void onSingleTapConfirmed() {
                if (mListener != null) {
                    mListener.onClick();
                }
            }
        });

        Point size = PhotoMetadataUtils.getBitmapSize(item.getContentUri(), getActivity());
        if (item.isGif()) {
            SelectionSpec.getInstance().imageEngine.loadGifImage(getContext(), size.x, size.y, image,
                    item.getContentUri());
        } else {
            SelectionSpec.getInstance().imageEngine.loadImage(getContext(), size.x, size.y, image,
                    item.getContentUri());
        }
    }

    public void resetView() {
        if (getView() != null) {
            ((ImageViewTouch) getView().findViewById(R.id.image_view)).resetMatrix();
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
