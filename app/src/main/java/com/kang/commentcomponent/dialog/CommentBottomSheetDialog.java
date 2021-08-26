package com.kang.commentcomponent.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.kang.commentcomponent.R;
import com.kang.commentcomponent.demo1.bean.SendCommentBean;
import com.kang.commentcomponent.demo2.BaseInfo;
import com.kang.commentcomponent.demo2.CommentApiInterface;
import com.kang.commentcomponent.demo2.bean.CommentInfo;
import com.kang.commentcomponent.demo2.CommentRecyclerViewV1;
import com.kang.commentcomponent.demo2.bean.CommentResponse;
import com.kang.commentcomponent.utils.Pub;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
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
 * Created by BinKang on 2021/8/17.
 * Des :自定义封装一下底部弹框BottomSheetDialog，可拖拽
 */
public class CommentBottomSheetDialog extends BottomSheetDialog {

    private Context mContext;
    private BaseInfo baseInfo;
    private TextView commentNumTv;
    private SmartRefreshLayout mSmartRefreshLayout;

    private float slideOffset = 0;

    public CommentBottomSheetDialog(@NonNull Context context, BaseInfo baseInfo) {
        super(context, R.style.dialog);
        mContext = context;
        this.baseInfo = baseInfo;
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(true);
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_bottomsheet);
        initView(context);
    }

    private void initView(Context context) {
//        View view = LayoutInflater.from(context).inflate(R.layout.dialog_bottomsheet, null);
//        setContentView(view);
        View view = findViewById(R.id.root_layout);
        CommentRecyclerViewV1 commentRecyclerView = findViewById(R.id.dialog_bottomsheet_rv_lists);
        commentRecyclerView.setBaseInfo(baseInfo);
        commentRecyclerView.loadL1Comment();
        ImageView closeIv = findViewById(R.id.dialog_bottomsheet_iv_close);
        commentNumTv = findViewById(R.id.dialog_bottomsheet_tv_comment_num);
        mSmartRefreshLayout = findViewById(R.id.refreshLayout);
        mSmartRefreshLayout.setEnableRefresh(false);
        mSmartRefreshLayout.setNestedScrollingEnabled(false);

        mSmartRefreshLayout.setOnLoadMoreListener(refreshLayout -> commentRecyclerView.loadL1Comment());

        commentRecyclerView.setLoadCommentNetworkStatus(new CommentRecyclerViewV1.LoadCommentNetworkStatus() {
            @Override
            public void onSuccess(int level) {
                completeRefreshState();
            }

            @Override
            public void onFailed(int level) {
                completeRefreshState();
            }
        });

        closeIv.setOnClickListener(v -> {
            dismiss();
        });

        RelativeLayout commentInputRl = findViewById(R.id.rl_comment);
        commentInputRl.setOnClickListener(v -> {

            if (!Pub.isLogin(context)) {
                return;
            }

            // 发送一级评论
            String resourceType = baseInfo.getResourceType();
            String resourceId = baseInfo.getResourceId();
            if (resourceId.contains("002945")) {
                resourceId = "002945";
            }
            SendCommentBean sendCommentBean = new SendCommentBean(resourceType, resourceId, 0, 0, "留下你的精彩评论吧~~");
            EditCommentDialog dialog = new EditCommentDialog(getContext(), sendCommentBean);
            dialog.setOnSendResultListener(new EditCommentDialog.OnSendResultListener() {
                @Override
                public void sendResult(CommentInfo commentInfo) {
                    commentRecyclerView.getCommentAdapter().insertL1CommentToFirst(commentInfo);
                    //滑动到最顶部
                    commentRecyclerView.smoothScrollToPosition(0);
                }
            });
            dialog.show();
        });

        BottomSheetBehavior<View> mDialogBehavior = BottomSheetBehavior.from((View) view.getParent());
        mDialogBehavior.setPeekHeight(getWindowHeight(context));
        //dialog滑动监听
        mDialogBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    mDialogBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                } else if (newState == BottomSheetBehavior.STATE_SETTLING) {
                    if (slideOffset <= -0.28) {
                        //当向下滑动时 值为负数
                        dismiss();
                    }
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                CommentBottomSheetDialog.this.slideOffset = slideOffset;//记录滑动值
            }
        });

        getCommentNum(baseInfo);
    }

    /**
     * 查询评论数量
     *
     * @param baseInfo
     */
    private void getCommentNum(BaseInfo baseInfo) {

        JSONObject jsonObject = new JSONObject();
        RequestBody commentBody = null;
        try {
            jsonObject.put("type", baseInfo.getResourceType());
            jsonObject.put("resourceId", baseInfo.getResourceId());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String commentJson = jsonObject.toString();
        commentBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), commentJson);
        Observable<BaseResponse<CommentResponse>> responseObservable = ZPlanApi.getService(CommentApiInterface.class)
                .getCommentNum(commentBody)
                .compose(ZPlanApi.getInstance().applySchedulers(new BaseObserver<BaseResponse<CommentResponse>>() {

                    @Override
                    public void onSubscription(Disposable d) {

                    }

                    @Override
                    public void onSuccess(BaseResponse<CommentResponse> commentResponseBaseResponse) {
                        commentNumTv.setText(commentResponseBaseResponse.getBody().getTotal() + "条评论");
                    }

                    @Override
                    public void onFailure(Throwable t) {

                    }
                }));
    }

    private int getWindowHeight(Context context) {
        Resources res = context.getResources();
        DisplayMetrics displayMetrics = res.getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

    private void completeRefreshState() {
        mSmartRefreshLayout.finishRefresh();
        mSmartRefreshLayout.finishLoadMore();
    }
}
