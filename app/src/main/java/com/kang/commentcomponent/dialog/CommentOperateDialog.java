package com.kang.commentcomponent.dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;


import androidx.annotation.NonNull;

import com.kang.commentcomponent.R;
import com.kang.commentcomponent.demo2.CommentApiInterface;
import com.kang.commentcomponent.demo2.bean.CommentInfo;
import com.kang.commentcomponent.demo2.bean.CommentResponse;
import com.kang.commentcomponent.utils.ToastUtils;
import com.sun.network.ZPlanApi;
import com.sun.network.beans.BaseResponse;
import com.sun.network.observer.BaseObserver;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

import static android.content.Context.CLIPBOARD_SERVICE;

/**
 * Created by BinKang on 2021/8/3.
 * Des :
 */
public class CommentOperateDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private CommentInfo mCommentInfo;

    public CommentOperateDialog(@NonNull Context context, CommentInfo commentInfo) {
        super(context, R.style.DialogPictureSelect);
        this.context = context;
        this.mCommentInfo = commentInfo;
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        //设置弹框的尺寸和位置、动画
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;   //设置宽度充满屏幕
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
        window.setGravity(Gravity.BOTTOM); //可设置dialog的位置
        window.setWindowAnimations(R.style.dialogAnim);

        //触摸返回键和空白区可dismiss弹框
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        setContentView(R.layout.comment_operate_dialog_layout);
        initView(context);

    }

    private void initView(Context context) {

        TextView deleteTv = findViewById(R.id.comment_operate_delete);
        TextView replyTv = findViewById(R.id.comment_operate_reply);
        replyTv.setVisibility(View.GONE);
        TextView reportTv = findViewById(R.id.comment_operate_report);
        reportTv.setVisibility(View.GONE);
        TextView copyTv = findViewById(R.id.comment_operate_copy);
        TextView cancelTv = findViewById(R.id.comment_operate_cancel);

//        String userId = PreferenceUtil.getInstance(tztMainApplication.getIns().getBaseContext()).getUserID();
        String userId = "10186";
        if (!TextUtils.isEmpty(userId) && userId.equals(String.valueOf(mCommentInfo.getUserId()))) {
            deleteTv.setVisibility(View.VISIBLE);
        } else {
            deleteTv.setVisibility(View.GONE);
        }

        deleteTv.setOnClickListener(this);
        replyTv.setOnClickListener(this);
        reportTv.setOnClickListener(this);
        copyTv.setOnClickListener(this);
        cancelTv.setOnClickListener(this);

        setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_operate_report:

                dismiss();
                break;
            case R.id.comment_operate_reply:

                break;
            case R.id.comment_operate_copy:
                String commentContent = mCommentInfo.getCommentContent();
                ClipboardManager myClipboard = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip = ClipData.newPlainText("text", commentContent);
                myClipboard.setPrimaryClip(myClip);
                if (!TextUtils.isEmpty(commentContent)) {
                    ToastUtils.getInstance().showShortToast("已复制到粘贴板");
                }
                dismiss();
                break;
            case R.id.comment_operate_delete:
                if (mOnListenerDeleteComment != null) {
                    mOnListenerDeleteComment.delete();
                }
//                deleteComment();
                dismiss();
                break;
            case R.id.comment_operate_cancel:
                dismiss();
                break;
        }
    }

    //网络请求处理删除评论的逻辑
    private void deleteComment() {
        Observable<BaseResponse<CommentResponse>> compose = ZPlanApi.getService(CommentApiInterface.class)
                .commentDelete(null)
                .compose(ZPlanApi.getInstance().applySchedulers(new BaseObserver<BaseResponse<CommentResponse>>() {
                    @Override
                    public void onSubscription(Disposable d) {

                    }

                    @Override
                    public void onSuccess(BaseResponse<CommentResponse> commentResponseBaseResponse) {
                        ToastUtils.getInstance().showShortToast("删除成功");
                        if (mOnListenerDeleteComment != null) {
                            mOnListenerDeleteComment.delete();
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                        ToastUtils.getInstance().showShortToast("删除失败");
                    }
                }));
    }

    private OnListenerDeleteComment mOnListenerDeleteComment;

    public interface OnListenerDeleteComment {
        void delete();
    }

    public void setOnListenerDeleteComment(OnListenerDeleteComment onListenerDeleteComment) {
        this.mOnListenerDeleteComment = onListenerDeleteComment;
    }

}
