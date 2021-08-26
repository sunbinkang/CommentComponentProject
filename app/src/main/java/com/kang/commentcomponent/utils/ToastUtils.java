package com.kang.commentcomponent.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.kang.commentcomponent.R;


/**
 * 弹Toast的工具类
 */
public class ToastUtils {

    private static final long LENGTH_SHORT = 3000; // ms
    private static final long LENGTH_LONG = 5000; // ms

    private long mDuration = LENGTH_SHORT;
    private String mText;

    private TextView mTextViewInToast;

    private View mToastView;
    private Toast mToast;

    private Context mContext;


    private static ToastUtils instance = null;
    /**
     * 是否支持自定义toast
     */
    private boolean mIsSupport = true;

    private int yOffset;

    private ToastUtils() {
    }

    public static ToastUtils getInstance() {
        if (instance == null) {
            //对代码块加锁，锁级别为类
            synchronized (ToastUtils.class) {
                if (instance == null) {
                    instance = new ToastUtils();
                }
            }
        }
        return instance;
    }

    public void init(Context ctx) {
        if (ctx == null) {
            return;
        }
        mContext = ctx;
        yOffset = DisplayUtils.getScreenHeight(ctx) / 6;
    }

    /**
     * 弹3秒的吐司
     */
    public void showShortToast(CharSequence text) {
        mText = text.toString();
        mDuration = LENGTH_SHORT;
        show();
    }

    /**
     * 弹3秒的吐司
     */
    public void showShortToast(int resId) {
        mText = mContext.getResources().getString(resId);
        mDuration = LENGTH_SHORT;
        show();
    }

    /**
     * 弹5秒的吐司
     */
    public void showLongToast(CharSequence text) {
        mText = text.toString();
        mDuration = LENGTH_LONG;
        show();
    }

    /**
     * 弹5秒的吐司
     */
    public void showLongToast(int resId) {
        mText = mContext.getResources().getString(resId);
        mDuration = LENGTH_LONG;
        show();
    }

    /**
     * 显示Toast
     */
    private void show() {
        if (mIsSupport) {
            showCustomToast();
        } else {
            Toast.makeText(mContext, mText, mDuration == LENGTH_SHORT ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
        }
    }

    private void showCustomToast() {

        mToast = new Toast(mContext);
        mToastView = LayoutInflater.from(mContext).inflate(R.layout.common_toast, null);
        mToastView.setBackgroundResource(R.drawable.shape_custom_toast_bg);
        mTextViewInToast = mToastView.findViewById(R.id.toast_text);
        mTextViewInToast.setText(mText);
        mTextViewInToast.setTextColor(mContext.getResources().getColor(R.color.color_282828));

        mToast.setDuration(mDuration == LENGTH_SHORT ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
        mToast.setView(mToastView);

        mToast.setGravity(Gravity.TOP, 0, yOffset - DisplayUtils.convertDipToPixel(40));
        mToast.show();
    }
}
