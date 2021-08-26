package com.kang.commentcomponent.demo1;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.kang.commentcomponent.base.BaseEmptyRecyclerViewAdapter;
import com.kang.commentcomponent.constants.CommentType;
import com.kang.commentcomponent.demo1.bean.CommentInfo;
import com.kang.commentcomponent.demo1.bean.SendCommentBean;
import com.kang.commentcomponent.dialog.EditCommentDialog;
import com.kang.commentcomponent.utils.Pub;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BinKang on 2021/8/13.
 * Des : 通用评论组件：这个对应的CommentInfo是我模拟的数据，应用到真实的场景需要做一些改动
 */
public class CommentRecyclerView extends RecyclerView {

    private CommentAdapter adapter;

    public CommentRecyclerView(Context context) {
        this(context, null);
    }

    public CommentRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CommentRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
        initListener();
    }

    private void init(Context context, AttributeSet attrs) {
        adapter = new CommentAdapter(context);
        adapter.setAllowComment(true);
        adapter.setDataState(BaseEmptyRecyclerViewAdapter.State.LODDING);
        setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        setAdapter(adapter);
    }

    private void initListener() {
        adapter.setCommentL1Listener((position, commentInfo) -> {
            //检查登录，没登录就return
            if (!Pub.isLogin(getContext())) {
                return;
            }
            // 发送二级评论
            SendCommentBean sendCommentBean = new SendCommentBean("0", "002945", 0, 0, "留下你的精彩评论吧~~");
            EditCommentDialog dialog = new EditCommentDialog(getContext(), sendCommentBean);
            dialog.show();
        });
        adapter.setCommentL2Listener((position, commentInfo) -> {
            if (!Pub.isLogin(getContext())) {
                return;
            }
            // 发送三级评论
            SendCommentBean sendCommentBean = new SendCommentBean("0", "002945", 0, 0, "留下你的精彩评论吧~~");
            EditCommentDialog dialog = new EditCommentDialog(getContext(), sendCommentBean);
            dialog.show();
        });
        adapter.setCommentLoadMoreListener(position -> {
            CommentInfo commentInfo = adapter.getItems().get(position);
            loadL2Comment(commentInfo.getParent_id());
        });
    }

    /**
     * 加载一级评论
     */
    public void loadL1Comment() {
        List<CommentInfo> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            CommentInfo commentInfo = new CommentInfo();
            commentInfo.setComment_type(CommentType.COMMENT_L1);
            commentInfo.setChild_count(i);
            commentInfo.setId("" + i);
            commentInfo.setParent_id("" + i);
            list.add(commentInfo);
//            for (int j = 0; j < 3; j++) {
//                CommentInfo commentInfo2 = new CommentInfo();
//                commentInfo2.setComment_type(CommentType.COMMENT_L2);
//                list.add(commentInfo2);
//            }
            if (i > 0) {
                CommentInfo loadMore = new CommentInfo();
                loadMore.setComment_type(CommentType.COMMENT_LOAD_MORE);
                loadMore.setId(CommentType.COMMENT_LOAD_MORE_ID);
                loadMore.setParent_id(commentInfo.getId());
                loadMore.setChild_count(i);
                list.add(loadMore);
            }
        }
        adapter.addAll(list);
    }

    /**
     * 加载二级评论
     */
    public void loadL2Comment(String parent_id) {

        List<CommentInfo> commentInfoList = new ArrayList<>();
        for (int j = 0; j < 5; j++) {
            CommentInfo commentInfo2 = new CommentInfo();
            commentInfo2.setParent_id(parent_id);
            commentInfo2.setComment_type(CommentType.COMMENT_L2);
            commentInfo2.setId("" + j);
            commentInfoList.add(commentInfo2);
        }

        if (commentInfoList.size() > 0) {
            adapter.addL2Comment(parent_id, commentInfoList);
        }

        int parentIndex = adapter.findIndexById(parent_id);
        CommentInfo commentInfo = adapter.getItems().get(parentIndex);
        int childCount = adapter.getChildCount(parent_id);
        if (childCount >= commentInfo.getChild_count()) {
            adapter.loadMore2Collapse(parent_id);
        }
    }
}
