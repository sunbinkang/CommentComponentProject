package com.kang.commentcomponent.widget;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ActionMenuView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;

import com.kang.commentcomponent.R;
import com.kang.commentcomponent.demo2.CommentApiInterface;
import com.kang.commentcomponent.demo2.bean.CommentInfo;
import com.kang.commentcomponent.demo2.bean.CommentResponse;
import com.kang.commentcomponent.utils.Pub;
import com.sun.network.ZPlanApi;
import com.sun.network.beans.BaseResponse;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * Created by BinKang on 2021/8/23.
 * Des : 点赞按钮(带点赞的网络请求、带动画)、使用：外面直接xml写控件就行，代码直接调用api（setCommentInfo、setCount、setOperate）
 */
public class PraiseLayout extends LinearLayout {

    protected int drawable;
    protected int drawableHighLight;
    protected float textSize;
    protected float imageSize;
    protected int orientation;
    protected int textColor;
    protected float marginSize;

    private ImageView praiseIv;
    private TextView praiseTv;
    private CommentInfo mCommentInfo;

    /**
     * 点赞数量
     */
    private int mCountNum;

    /**
     * 点赞状态，true=点赞过，false=没有点赞过
     */
    private boolean operate;

    private Context context;

    public PraiseLayout(Context context) {
        this(context, null);
    }

    public PraiseLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PraiseLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {

        LayoutInflater.from(getContext()).inflate(R.layout.praise_layout, this, true);
        praiseIv = findViewById(R.id.iv_like_status);
        praiseTv = findViewById(R.id.tv_like_count);

        marginSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, this.getContext().getResources().getDisplayMetrics());
        orientation = PraiseLayout.VERTICAL;
        imageSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getContext().getResources().getDisplayMetrics());
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12, getContext().getResources().getDisplayMetrics());
        drawable = R.drawable.icon_love_no;
        drawableHighLight = R.drawable.icon_love;
        textColor = 0XFFA1A7B3;

        if (attrs != null) {
            @SuppressLint("CustomViewStyleable") TypedArray ta = getContext().obtainStyledAttributes(attrs, R.styleable.PraiseLayout);
            orientation = ta.getInteger(R.styleable.PraiseLayout_orientation_, PraiseLayout.VERTICAL);
            imageSize = ta.getDimension(R.styleable.PraiseLayout_imageSize, imageSize);
            drawable = ta.getResourceId(R.styleable.PraiseLayout_imageSrc, drawable);
            drawableHighLight = ta.getResourceId(R.styleable.PraiseLayout_imageSrcHighLight, drawableHighLight);
            textColor = ta.getColor(R.styleable.PraiseLayout_textcolor, textColor);
            textSize = ta.getDimension(R.styleable.PraiseLayout_textsize, textSize);
            ta.recycle();
        }

        praiseIv.setImageResource(drawable);
        praiseIv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        LayoutParams layoutParams = new ActionMenuView.LayoutParams((int) imageSize, (int) imageSize);
        praiseIv.setLayoutParams(layoutParams);

        praiseTv.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        praiseTv.setTextColor(textColor);


        setOnClickListener(v -> praise());
    }

    public void setCommentInfo(CommentInfo commentInfo) {
        this.mCommentInfo = commentInfo;
    }

    /**
     * 设置按钮的文字
     *
     * @param count
     */
    public void setCount(int count) {
        mCountNum = count;
        if (count <= 0) {
            praiseTv.setVisibility(INVISIBLE);
        } else {
            praiseTv.setVisibility(VISIBLE);
            praiseTv.setText(String.valueOf(count));
        }
    }

    public void setOperate(boolean operate) {
        changeStatus(operate);
    }

    private void changeStatus(boolean op) {
        this.operate = op;
        if (operate) {
//            praiseIv.setImageResource(drawableHighLight);
            praiseIv.setSelected(true);
            praiseTv.setTextColor(0xfff73254);
        } else {
//            praiseIv.setImageResource(drawable);
            praiseIv.setSelected(false);
            praiseTv.setTextColor(0xffcccccc);
        }
    }

    public int getCount() {
        return mCountNum;
    }

    public boolean isOperate() {
        return operate;
    }

    //点赞操作
    private void praise() {
        //检查是否登录
//        String userId = PreferenceUtil.getInstance(tztMainApplication.getIns().getBaseContext()).getUserID();
        if (!Pub.isLogin(getContext())) {
            return;
        }

        if (mCommentInfo == null) {
            return;
        }

        //如果点赞过技术就是需要-1
        final int change = operate ? -1 : 1;
        //如果点赞过就要取消点赞
        final String op = operate ? "1" : "0";
        //先更新UI，不等请求结果
        setCount(change + getCount());
        if (operate) {//取消点赞操作
            if (mCommentInfo != null) {
                mCommentInfo.setLikeCount(mCommentInfo.getLike() - 1);
                mCommentInfo.setLike(0);
            }
        } else {//点赞操作
            if (mCommentInfo != null) {
                mCommentInfo.setLikeCount(mCommentInfo.getLike() + 1);
                mCommentInfo.setLike(1);
            }
            praiseAnimator(praiseIv);
        }
        changeStatus(!operate);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("type", mCommentInfo.getType());// 0 股吧	1 发一发
            jsonObject.put("like", op);// 0 点赞 1 取消点赞
            jsonObject.put("resourceId", mCommentInfo.getResourceId());
            jsonObject.put("commentId", mCommentInfo.getCommentId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        Disposable subscribe = ZPlanApi.getService(CommentApiInterface.class)
                .prise(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<BaseResponse<CommentResponse>>() {
                    @Override
                    public void accept(BaseResponse<CommentResponse> commentResponseBaseResponse) throws Exception {
                        if (commentResponseBaseResponse.getCode().equals("0")) {

                        } else {
                            operate = false;
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.printStackTrace();

                    }
                });
    }

    /**
     * 点赞动画
     *
     * @param v
     */
    private void praiseAnimator(ImageView v) {
        ImageView view = (ImageView) v;
        PropertyValuesHolder pvhScaleX = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.6f);
        PropertyValuesHolder pvhScaleY = PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.6f);
        ObjectAnimator.ofPropertyValuesHolder(v, pvhScaleX, pvhScaleY).setDuration(150).start();

        PropertyValuesHolder pvhScaleX2 = PropertyValuesHolder.ofFloat("scaleX", 0.6f, 1.0f);
        PropertyValuesHolder pvhScaleY2 = PropertyValuesHolder.ofFloat("scaleY", 0.6f, 1.0f);
        ObjectAnimator objectAnimator2 = ObjectAnimator.ofPropertyValuesHolder(v, pvhScaleX2, pvhScaleY2);
        objectAnimator2.setDuration(150);
        objectAnimator2.setStartDelay(150);
        objectAnimator2.start();
    }
}
