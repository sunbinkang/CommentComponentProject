package com.kang.commentcomponent.demo2;

import android.content.Context;
import android.util.AttributeSet;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kang.commentcomponent.base.BaseEmptyRecyclerViewAdapter;
import com.kang.commentcomponent.constants.CommentType;
import com.kang.commentcomponent.demo1.bean.SendCommentBean;
import com.kang.commentcomponent.demo2.bean.CommentInfo;
import com.kang.commentcomponent.demo2.bean.CommentResponse;
import com.kang.commentcomponent.dialog.CommentOperateDialog;
import com.kang.commentcomponent.dialog.EditCommentDialog;
import com.kang.commentcomponent.utils.ListUtils;
import com.kang.commentcomponent.utils.ToastUtils;
import com.sun.network.ZPlanApi;
import com.sun.network.beans.BaseResponse;
import com.sun.network.observer.BaseObserver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.RequestBody;


/**
 * Created by BinKang on 2021/8/13.
 * Des : 通用评论组件：这个是真实的网络请求数据CommentInfo，数据json放了一份在assets文件下和接口文档也在feed.md
 */
public class CommentRecyclerViewV1 extends RecyclerView {

    private CommentAdapterV1 adapter;

    private BaseInfo baseInfo;
    private LoadCommentNetworkStatus loadCommentNetworkStatus;

    public CommentRecyclerViewV1(Context context) {
        this(context, null);
    }

    public CommentRecyclerViewV1(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentRecyclerViewV1(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
        initListener();
    }

    private void init(Context context, AttributeSet attrs) {
        baseInfo = new BaseInfo("0", "002945");//初始值
        adapter = new CommentAdapterV1(context);
        adapter.setAllowComment(true);
        adapter.setDataState(BaseEmptyRecyclerViewAdapter.State.LODDING);
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        setAdapter(adapter);
    }

    public CommentAdapterV1 getCommentAdapter() {
        return adapter;
    }

    private void initListener() {
        adapter.setCommentL1Listener(new CommentAdapterV1.CommentL1Listener() {
            @Override
            public void click(int position, CommentInfo commentInfo) {
                // 发送二级评论
                String hint = "回复 @" + commentInfo.getUserName();
                String type = baseInfo.getResourceType();
                String resourceId = baseInfo.getResourceId();
                if (resourceId.contains("002945")) {
                    resourceId = "002945";
                }
                SendCommentBean sendCommentBean = new SendCommentBean(type, resourceId, commentInfo.getCommentId(), commentInfo.getCommentId(), hint);
                EditCommentDialog dialog = new EditCommentDialog(getContext(), sendCommentBean);
                dialog.setOnSendResultListener(new EditCommentDialog.OnSendResultListener() {
                    @Override
                    public void sendResult(CommentInfo commentInfo) {
                        int position = adapter.insertL2CommentToList(commentInfo);
                        scrollToPosition(position);
                    }
                });
                dialog.show();
            }

            @Override
            public void longClick(int position, CommentInfo commentInfo) {
                CommentOperateDialog commentOperateDialog = new CommentOperateDialog(getContext(), commentInfo);
                commentOperateDialog.setOnListenerDeleteComment(() -> {
                    int childCount = adapter.getChildCount(commentInfo.getCommentId());
                    if (childCount > 0) {
                        commentInfo.setCommentContent("该条评论已删除");
                        adapter.notifyDataSetChanged();
                    } else {
                        int commentId = commentInfo.getCommentId();
                        int indexById = adapter.findIndexById(commentId);
                        adapter.remove(indexById, true);
                    }
                });
                commentOperateDialog.show();
            }
        });
        adapter.setCommentL2Listener(new CommentAdapterV1.CommentL2Listener() {
            @Override
            public void click(int position, CommentInfo commentInfo) {
                // 发送三级评论
//            SendCommentBean sendCommentBean = new SendCommentBean("0", "002945", commentInfo.getCommentId(), commentInfo.getReplyCommentId(),CommentType.COMMENT_THREE);
                String hint = "回复 @" + commentInfo.getUserName();
                String type = baseInfo.getResourceType();
                String resourceId = baseInfo.getResourceId();
                if (resourceId.contains("002945")) {
                    resourceId = "002945";
                }
                SendCommentBean sendCommentBean = new SendCommentBean(type, resourceId, commentInfo.getCommentId(), commentInfo.getRootCommentId(), hint);
                EditCommentDialog dialog = new EditCommentDialog(getContext(), sendCommentBean);
                dialog.setOnSendResultListener(new EditCommentDialog.OnSendResultListener() {
                    @Override
                    public void sendResult(CommentInfo commentInfo) {
                        //将当前你评论成功的内容插入到对应的位置，并滑到顶部第一条
                        int position = adapter.insertL2CommentToList(commentInfo);
                        scrollToPosition(position);
                    }
                });
                dialog.show();
            }

            @Override
            public void longClick(int position, CommentInfo commentInfo) {
                CommentOperateDialog commentOperateDialog = new CommentOperateDialog(getContext(), commentInfo);
                commentOperateDialog.setOnListenerDeleteComment(() -> {
                    int commentId = commentInfo.getCommentId();
                    int indexById = adapter.findIndexById(commentId);
                    adapter.remove(indexById, true);
                });
                commentOperateDialog.show();
            }
        });
        adapter.setCommentLoadMoreListener(position -> {
            //加载更多二级评论
            CommentInfo commentInfo = adapter.getItems().get(position);
//            loadL2Comment(commentInfo.getReplyCommentId());
            loadL2Comment(commentInfo.getRootCommentId());
        });
        adapter.setCommentCollapseListener(this::smoothScrollToPosition);
    }

    /**
     * 获取评论需要的参数(调用这个组件必须要调用的方法，因为要知道是获取哪个资源的评论和发评论给哪个资源)
     *
     * @param baseInfo
     */
    public void setBaseInfo(BaseInfo baseInfo) {
        this.baseInfo = baseInfo;
    }

    public void setLoadCommentNetworkStatus(LoadCommentNetworkStatus loadCommentNetworkStatus) {
        this.loadCommentNetworkStatus = loadCommentNetworkStatus;
    }

    /**
     * 加载一级评论
     */
    public void loadL1Comment() {
        String type = baseInfo.getResourceType();
        String resourceId = baseInfo.getResourceId();
        if (resourceId.contains("002945")) {
            resourceId = "002945";
        }
        int commentPage = adapter.getCommentPage(resourceId);
        JSONObject jsonObject = new JSONObject();
        RequestBody commentBody;
        try {
            jsonObject.put("type", type);
            jsonObject.put("resourceId", resourceId);
            jsonObject.put("commentId", 0);
            jsonObject.put("pageNum", commentPage);
            jsonObject.put("pageSize", 10);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String commentJson = jsonObject.toString();
        commentBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), commentJson);

        String finalResourceId = resourceId;
        Observable<BaseResponse<CommentResponse>> responseObservable = ZPlanApi.getService(CommentApiInterface.class)
                .getComment(commentBody)
                .compose(ZPlanApi.getInstance().applySchedulers(new BaseObserver<BaseResponse<CommentResponse>>() {
                    @Override
                    public void onSubscription(Disposable d) {
//                        ToastUtils.getInstance().showShortToast("正在请求中");
                    }

                    @Override
                    public void onSuccess(BaseResponse<CommentResponse> commentResponse) {
                        List<CommentInfo> list = new ArrayList<>();
                        List<CommentInfo> data = commentResponse.getBody().getData();
                        for (int i = 0; i < data.size(); i++) {
                            CommentInfo commentInfo = data.get(i);
//                            commentInfo.setReplyCommentId(commentInfo.getCommentId());
                            commentInfo.setRootCommentId(commentInfo.getCommentId());
                            commentInfo.setComment_type(CommentType.COMMENT_L1);
                            list.add(commentInfo);

                            //给每个一级评论增加“展开n条评论”item
                            if (commentInfo.getCommentCount() > 0) {
                                CommentInfo loadMore = new CommentInfo();
                                loadMore.setComment_type(CommentType.COMMENT_LOAD_MORE);
                                loadMore.setCommentId(CommentType.COMMENT_LOAD_MORE_ID_);
//                                loadMore.setReplyCommentId(commentInfo.getCommentId());
                                loadMore.setRootCommentId(commentInfo.getCommentId());
                                loadMore.setCommentCount(commentInfo.getCommentCount());
                                list.add(loadMore);
                            }
                        }
                        adapter.addAll(list);

                        //没有数据的要设置空页面
                        if (ListUtils.isEmpty(list)) {
                            adapter.setDataState(BaseEmptyRecyclerViewAdapter.State.EMPTY);
                        }

                        if (loadCommentNetworkStatus != null) {
                            loadCommentNetworkStatus.onSuccess(1);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                        if (loadCommentNetworkStatus != null) {
                            loadCommentNetworkStatus.onFailed(1);
                        }
                        ToastUtils.getInstance().showShortToast("请求错误 :" + t.getMessage());
                        adapter.minusCommentPage(finalResourceId);
                        adapter.setDataState(BaseEmptyRecyclerViewAdapter.State.NETWORK_ERROR);
                    }
                }));
    }

    /**
     * 加载二级评论
     */
    public void loadL2Comment(int parent_id) {
        int commentPage = adapter.getCommentPage(String.valueOf(parent_id));
        String type = baseInfo.getResourceType();
        String resourceId = baseInfo.getResourceId();
        if (resourceId.contains("002945")) {
            resourceId = "002945";
        }
        JSONObject jsonObject = new JSONObject();
        RequestBody commentBody;
        try {
            jsonObject.put("type", type);
            jsonObject.put("resourceId", resourceId);
            jsonObject.put("commentId", parent_id);
            jsonObject.put("pageNum", commentPage);
            jsonObject.put("pageSize", 10);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String commentJson = jsonObject.toString();
        commentBody = RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), commentJson);

        Observable<BaseResponse<CommentResponse>> responseObservable = ZPlanApi.getService(CommentApiInterface.class)
                .getComment(commentBody)
                .compose(ZPlanApi.getInstance().applySchedulers(new BaseObserver<BaseResponse<CommentResponse>>() {
                    @Override
                    public void onSubscription(Disposable d) {
//                        ToastUtils.getInstance().showShortToast("正在请求中");
                    }

                    @Override
                    public void onSuccess(BaseResponse<CommentResponse> commentResponse) {
//                        ToastUtils.getInstance().showShortToast("请求成功 :" + commentResponse.getBody().getTotal());
                        List<CommentInfo> list = new ArrayList<>();
                        List<CommentInfo> data = commentResponse.getBody().getData();
                        for (int i = 0; i < data.size(); i++) {
                            CommentInfo commentInfo = data.get(i);
//                            commentInfo.setReplyCommentId(parent_id);
                            commentInfo.setComment_type(CommentType.COMMENT_L2);
                            list.add(commentInfo);
                        }
                        if (list.size() > 0) {
                            adapter.addL2Comment(parent_id, list);
                        }

                        //这里就是插入到对应一级评论的子评论的对应位置，还需要判断是“展开更多”还是“收起”的操作
                        int parentIndex = adapter.findIndexById(parent_id);
                        CommentInfo commentInfo = adapter.getItems().get(parentIndex);
                        int childCount = adapter.getChildCount(parent_id);
                        //判断是“展开更多”还是“收起”的操作
                        if (childCount >= commentInfo.getCommentCount()) {
                            adapter.loadMore2Collapse(parent_id);
                        }

                        if (loadCommentNetworkStatus != null) {
                            loadCommentNetworkStatus.onSuccess(2);
                        }
                    }

                    @Override
                    public void onFailure(Throwable t) {
                        t.printStackTrace();
                        if (loadCommentNetworkStatus != null) {
                            loadCommentNetworkStatus.onFailed(2);
                        }
                        ToastUtils.getInstance().showShortToast("请求错误 :" + t.getMessage());
                        adapter.minusCommentPage(String.valueOf(parent_id));
                    }
                }));
    }

    public static interface LoadCommentNetworkStatus {
        /**
         * 加载成功
         *
         * @param level level 1=1级评论 2=2级评论
         */
        void onSuccess(int level);

        /**
         * 加载失败
         *
         * @param level 1=1级评论 2=2级评论
         */
        void onFailed(int level);
    }
}
