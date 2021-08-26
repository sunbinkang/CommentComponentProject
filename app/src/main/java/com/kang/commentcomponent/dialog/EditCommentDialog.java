package com.kang.commentcomponent.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.kang.commentcomponent.R;
import com.kang.commentcomponent.constants.JsonKey;
import com.kang.commentcomponent.demo1.bean.SendCommentBean;
import com.kang.commentcomponent.demo2.CommentApiInterface;
import com.kang.commentcomponent.demo2.bean.CommentInfo;
import com.kang.commentcomponent.utils.DisplayUtils;
import com.kang.commentcomponent.utils.NetUtils;
import com.kang.commentcomponent.utils.ToastUtils;
import com.kang.commentcomponent.widget.LoadingTextView;
import com.sun.network.ZPlanApi;
import com.sun.network.beans.BaseResponse;
import com.sun.network.observer.BaseObserver;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * Created by BinKang on 2021/8/18.
 * Des :评论便捷框
 */
public class EditCommentDialog extends Dialog implements TextWatcher, View.OnClickListener {

    private Context context;

    private EditText et_content;

    private LoadingTextView tv_send;

    private TextView mTextNum;

    private SendCommentBean mSendCommentBean;

    private int maxNumber = 100;

    public EditCommentDialog(@NonNull Context context, SendCommentBean sendCommentBean) {
        super(context, R.style.DialogPictureSelect);
        this.context = context;
        this.mSendCommentBean = sendCommentBean;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setLayout();

        //触摸返回键和空白区可dismiss弹框
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        setContentView(R.layout.dialog_edit_comment);
        initView(context);
    }

    private void initView(Context context) {
        tv_send = findViewById(R.id.tv_comment_send);
        mTextNum = findViewById(R.id.tv_text_num);
        et_content = findViewById(R.id.input_comment_et);
        et_content.setHint(mSendCommentBean.getHint());
        et_content.addTextChangedListener(this);
        //不加这三行代码，第一次点dialog显示就不会获取焦点无法弹起软键盘
        et_content.setFocusable(true);
        et_content.setFocusableInTouchMode(true);
        et_content.requestFocus();

        tv_send.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mTextNum.setText(s.length() + "/" + maxNumber);
        if (s.length() > 0) {
            tv_send.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) tv_send.getLayoutParams();
            if (et_content.getLineCount() == 1) {
                params.gravity = Gravity.CENTER_VERTICAL;
                params.setMargins(0, 0, DisplayUtils.convertDipToPixel(16), 0);
            } else {
                params.gravity = Gravity.BOTTOM;
                params.setMargins(0, 0, DisplayUtils.convertDipToPixel(16), DisplayUtils.convertDipToPixel(10));
            }
        } else {
            tv_send.setVisibility(View.GONE);
        }
        if (s.length() >= 100) {
            Toast.makeText(context, "最长可输入100个字符", Toast.LENGTH_SHORT).show();
        }
    }

    private void setLayout() {
        //设置弹框的尺寸和位置、动画
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); //可设置dialog的位置
        window.setWindowAnimations(R.style.dialogAnim);
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.tv_comment_send: {
                if (!NetUtils.isNetAvailable(getContext())) {
                    ToastUtils.getInstance().showShortToast("网络发生了错误");
                    return;
                }
                checkContent();
                break;
            }

            default: {

                break;
            }
        }
    }

    private void checkContent() {
        String content = et_content.getText().toString().trim();
        if (TextUtils.isEmpty(content)) {
            ToastUtils.getInstance().showLongToast("评论不能为空");
            return;
        }
        //去除输入评论文字中间的换行
        String noBlankText = content.replaceAll("\r|\n", " ");
        sendCommentNet(noBlankText);
    }

    private void sendCommentNet(final String noBlankText) {
        JSONObject jsonObject = new JSONObject();
        String resourceId = mSendCommentBean.getResourceId();
        if (resourceId.contains("002945")) {
            resourceId = "002945";
        }
        RequestBody commentBody = null;
        try {
            jsonObject.put("type", mSendCommentBean.getType());// 0 股吧评论	1 发一发评论
            jsonObject.put("resourceId", resourceId);
            jsonObject.put("content", noBlankText);// 评论内容 //[300]
            jsonObject.put("replyCommentId", mSendCommentBean.getReplyCommentId());// 被评论的原评论 ID , 如果为0 ，默认为对帖子的评论，即一级评论//long
            jsonObject.put("rootCommentId", mSendCommentBean.getRootCommentId());// 评论的根评论，如果为0，为一级评论
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String commentJson = jsonObject.toString();
        commentBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), commentJson);

//        CommentInfo commentInfo = new CommentInfo();
//        commentInfo.setLike(0);
//        commentInfo.setLikeCount(0);
//        commentInfo.setUserName("15707974725");
//        commentInfo.setReplyTime("2021-08-20 16:24:18");
//        commentInfo.setCommentContent(noBlankText);
//        if (mOnSendResultListener!=null) {
//            mOnSendResultListener.sendResult(commentInfo);
//        }

        Observable<BaseResponse<CommentInfo>> compose = ZPlanApi.getService(CommentApiInterface.class)
                .publishComment(commentBody)
                .compose(ZPlanApi.getInstance().applySchedulers(new BaseObserver<BaseResponse<CommentInfo>>() {
                    @Override
                    public void onSubscription(Disposable d) {
                        tv_send.setViewStatus(JsonKey.LoadingTextViewStatus.LOADING);
                        et_content.setTextColor(context.getResources().getColor(R.color.color_999999));
                        et_content.setEnabled(false);
                    }

                    @Override
                    public void onSuccess(BaseResponse<CommentInfo> commentInfoBaseResponse) {
                        tv_send.setViewStatus(JsonKey.LoadingTextViewStatus.COMPLETE);
                        String status = commentInfoBaseResponse.getCode();
                        if ("0".equals(status)) {
                            ToastUtils.getInstance().showShortToast("评论成功，稍后展示");
                        } else {
                            ToastUtils.getInstance().showShortToast("评论失败");
                        }
                        dismiss();

                        //把评论的信息回调回去，立即展示在列表
                        if (mOnSendResultListener != null && null != commentInfoBaseResponse.getBody()) {
                            mOnSendResultListener.sendResult(commentInfoBaseResponse.getBody());
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        ToastUtils.getInstance().showShortToast("评论失败,请稍后再试");
                        dismiss();
                        tv_send.setViewStatus(JsonKey.LoadingTextViewStatus.ERROR);
                        et_content.setTextColor(context.getResources().getColor(R.color.color_282828));
                        et_content.setEnabled(true);
                    }
                }));
    }

    private OnSendResultListener mOnSendResultListener;

    /**
     * 接口返回一个你当前评论，插入列表中
     */
    public interface OnSendResultListener {
        void sendResult(CommentInfo commentInfo);
    }

    public void setOnSendResultListener(OnSendResultListener onSendResultListener) {
        mOnSendResultListener = onSendResultListener;
    }
}
