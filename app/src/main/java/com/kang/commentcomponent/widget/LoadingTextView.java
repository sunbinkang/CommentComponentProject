package com.kang.commentcomponent.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kang.commentcomponent.R;
import com.kang.commentcomponent.constants.JsonKey;


/**
 * Created by BinKang on 2021/8/18.
 * Des : 带加载动画的TextView
 */
public class LoadingTextView extends FrameLayout {

    private TextView mTextView;
    private ImageView mImageView;

    private int loadingTextColor;

    private int loadingTextSize = 16;

    private String loadingText;


    public LoadingTextView(@NonNull Context context) {
        this(context, null);
    }

    public LoadingTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {

        LayoutInflater.from(context).inflate(R.layout.layout_textview_loading, this);
        mTextView = findViewById(R.id.tv_loading);
        mImageView = findViewById(R.id.iv_loading);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.LoadingTextView);

        if (a != null) {
            loadingText = a.getString(R.styleable.LoadingTextView_loading_text);
            loadingTextColor = a.getColor(R.styleable.LoadingTextView_loading_textColor, 0xff999999);
            loadingTextSize = (int) a.getDimension(R.styleable.LoadingTextView_loading_textSize, getResources().getDimension(R.dimen.dp_16));
            a.recycle();
        }

        mTextView.setText(loadingText);
        mTextView.setTextColor(loadingTextColor);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, loadingTextSize);

    }

    public TextView getTextView() {
        return mTextView;
    }

    public ImageView getImageView() {
        return mImageView;
    }

    public void setViewStatus(String status) {

        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.common_rotate_anim);

        if (JsonKey.LoadingTextViewStatus.LOADING.equals(status)) {
            mTextView.setVisibility(INVISIBLE);
            mImageView.setVisibility(VISIBLE);
            mImageView.startAnimation(animation);
            this.setEnabled(false);
        } else if (JsonKey.LoadingTextViewStatus.COMPLETE.equals(status)) {
            mTextView.setVisibility(VISIBLE);
            mImageView.clearAnimation();
            mImageView.setVisibility(GONE);
            this.setEnabled(true);
        } else {
            mTextView.setVisibility(VISIBLE);
            mImageView.clearAnimation();
            mImageView.setVisibility(GONE);
            this.setEnabled(true);
        }
    }
}
