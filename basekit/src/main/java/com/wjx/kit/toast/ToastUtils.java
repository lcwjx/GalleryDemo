package com.wjx.kit.toast;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.StringRes;

import com.wjx.kit.R;


/**
 * @ProjectName :GalleryDemo
 * @Description :
 * @Author chen.li.o
 * @DATE 2020/4/27
 */
public class ToastUtils {
    private static final String TAG = "ToastUtils";
    private static Toast mToast;

    public static void showShortToast(Context context, String content) {
        showToast(context, content, Toast.LENGTH_SHORT);
    }

    public static void showShortToast(Context context, @StringRes int resId) {
        checkNullPointer(context, resId);
        showToast(context, context.getString(resId), Toast.LENGTH_SHORT);
    }

    public static void showLongToast(Context context, String content) {
        showToast(context, content, Toast.LENGTH_LONG);
    }

    public static void showLongToast(Context context, @StringRes int resId) {
        checkNullPointer(context, resId);
        showToast(context, context.getString(resId), Toast.LENGTH_LONG);
    }

    private static void showToast(Context context, String content, int duration) {
        checkNullPointer(context, content);

        TextView textView = getView(context);
        textView.setText(content);

        try {
            if (mToast == null) {
                mToast = new Toast(context);
            }
            mToast.setView(textView);
            mToast.setGravity(Gravity.CENTER, 0, 0);
            mToast.setDuration(duration);
            mToast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static TextView getView(Context context) {
        LayoutInflater inflater = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return (TextView) inflater.inflate(R.layout.view_toast, null);
    }


    private static void checkNullPointer(Context context, @StringRes int resId) {
        if (context == null || resId == 0) {
            throw new NullPointerException("context or resId not allow null value!!");
        }
    }

    private static void checkNullPointer(Context context, String content) {
        if (context == null) {
            throw new NullPointerException("context not allow null value!!");
        }
        if (TextUtils.isEmpty(content)) {
            Log.e(TAG, "Content is empty!");
        }
    }
}
