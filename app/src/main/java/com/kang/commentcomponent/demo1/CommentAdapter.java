package com.kang.commentcomponent.demo1;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kang.commentcomponent.base.BaseEmptyRecyclerViewAdapter;
import com.kang.commentcomponent.constants.CommentType;
import com.kang.commentcomponent.databinding.ItemCommentCollapseBinding;
import com.kang.commentcomponent.databinding.ItemCommentL1Binding;
import com.kang.commentcomponent.databinding.ItemCommentL2Binding;
import com.kang.commentcomponent.databinding.ItemCommentLoadmoreBinding;
import com.kang.commentcomponent.demo1.bean.CommentInfo;

import java.util.List;


/**
 * Created by BinKang on 2021/8/13.
 * Des :评论的适配器(包含各种ViewHolder类)
 */
public class CommentAdapter extends BaseEmptyRecyclerViewAdapter<RecyclerView.ViewHolder, CommentInfo> {

    public static final int CONTENT_COMMENT_L1 = 0X9090001;//一级评论
    public static final int CONTENT_COMMENT_L2 = 0X9090002;//二级评论
    public static final int CONTENT_LOAD_MORE = 0X9090003;//展开更多回复
    public static final int CONTENT_COLLAPSE = 0X9090004;//收起评论
    public static final int CONTENT_NOT_ALLOW_COMMENT = 0X9090005;//不允许评论
    public static final int CONTENT_EMPTY = 0X9090006;//空页面

    /**
     * 一级评论监听
     */
    private CommentL1Listener commentL1Listener;
    /**
     * 二级评论监听
     */
    private CommentL2Listener commentL2Listener;
    /**
     * 加载二级评论监听
     */
    private CommentLoadMoreListener commentLoadMoreListener;

    /**
     * 是否允许评论
     */
    private boolean isAllowComment;

    public CommentAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        if (!isAllowComment()) {
            return 1;
        }
        return super.getItemCount();
    }

    @Override
    protected int getItemViewContentType(int position) {
        if (!isAllowComment()) {
            return CONTENT_NOT_ALLOW_COMMENT;
        }
        if (items.size() == 0) {
            return CONTENT_EMPTY;
        }

        CommentInfo commentInfo = items.get(position);
        if (commentInfo.getComment_type() == CommentType.COMMENT_EMPTY) {
            return CONTENT_EMPTY;
        } else if (commentInfo.getComment_type() == CommentType.COMMENT_L1) {
            //L1 一级评论
            return CONTENT_COMMENT_L1;
        } else if (commentInfo.getComment_type() == CommentType.COMMENT_L2) {
            //L2 二级评论
            return CONTENT_COMMENT_L2;
        } else if (commentInfo.getComment_type() == CommentType.COMMENT_LOAD_MORE) {
            //加载更多
            return CONTENT_LOAD_MORE;
        } else if (commentInfo.getComment_type() == CommentType.COMMENT_COLLAPSE) {
            //收起
            return CONTENT_COLLAPSE;
        }
        return CONTENT_EMPTY;

    }

    @Override
    protected RecyclerView.ViewHolder getContentViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == CONTENT_COMMENT_L1) {
            viewHolder = new CommentL1ViewHolder(ItemCommentL1Binding.inflate(LayoutInflater.from(context), parent, false));
        } else if (viewType == CONTENT_COMMENT_L2) {
            viewHolder = new CommentL2ViewHolder(ItemCommentL2Binding.inflate(LayoutInflater.from(context), parent, false));
        } else if (viewType == CONTENT_LOAD_MORE) {
            viewHolder = new CommentLoadMoreViewHolder(ItemCommentLoadmoreBinding.inflate(LayoutInflater.from(context), parent, false));
        } else if (viewType == CONTENT_COLLAPSE) {
            viewHolder = new CommentCollapseViewHolder(ItemCommentCollapseBinding.inflate(LayoutInflater.from(context), parent, false));
        } else {
            viewHolder = new CommentEmptyViewHolder(new TextView(context));
        }

        return viewHolder;
    }

    @Override
    protected void bindContentViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        final CommentInfo commentInfo = getItems().get(position);

        if (viewHolder instanceof CommentL1ViewHolder) {
            ((CommentL1ViewHolder) viewHolder).updateView(commentInfo);
            viewHolder.itemView.setOnClickListener(v -> {
                if (commentL1Listener != null) {
                    commentL1Listener.click(position, commentInfo);
                }
            });
        } else if (viewHolder instanceof CommentL2ViewHolder) {
            ((CommentL2ViewHolder) viewHolder).updateView(commentInfo);
            viewHolder.itemView.setOnClickListener(v -> {
                if (commentL2Listener != null) {
                    commentL2Listener.click(position, commentInfo);
                }
            });
        } else if (viewHolder instanceof CommentLoadMoreViewHolder) {
            ((CommentLoadMoreViewHolder) viewHolder).updateView(commentInfo);
            viewHolder.itemView.setOnClickListener(v -> {
                if (commentLoadMoreListener != null) {
                    commentLoadMoreListener.click(position);
                }
            });
        } else if (viewHolder instanceof CommentCollapseViewHolder) {
            ((CommentCollapseViewHolder) viewHolder).updateView(commentInfo);
            viewHolder.itemView.setOnClickListener(v -> {
                //收起评论
                //收起流程是将二级评论第一条index开始计数，计数到收起按钮的index
                //将第一条二级评论到收起按钮之间的评论删除掉，再讲收起按钮的comment_type修改成comment_load_more
                int startDeleteIndex = -1;
                int endDeleteIndex = -1;
                int l2Count = 0;
                for (int i = 0; i < items.size(); i++) {
                    CommentInfo info = items.get(i);
                    if (info.getParent_id().equals(commentInfo.getParent_id())) {
                        if (CommentType.COMMENT_COLLAPSE_ID.equals(info.getId())) {
                            break;
                        }
                        l2Count++;
                        if (l2Count == 1) {
                            startDeleteIndex = i;
                        } else if (l2Count > 1) {
                            endDeleteIndex = i;
                        }
                    }
                }
                if (startDeleteIndex > 0 || endDeleteIndex > 0) {
                    if (endDeleteIndex - startDeleteIndex >= 1) {
                        if (items.size() > endDeleteIndex) {
                            int deleteIndex = startDeleteIndex + 1;
                            for (int i = startDeleteIndex + 1; i <= endDeleteIndex; i++) {
                                items.remove(deleteIndex);
                            }
                            notifyDataSetChanged();
                        }
                    }
                }
                collapse2LoadMore(commentInfo.getParent_id());
            });
        }
    }

    /**
     * 添加对应的parent_id的二级评论
     *
     * @param parent_id
     * @param data
     */
    public void addL2Comment(String parent_id, List<CommentInfo> data) {
        int insertIndex = -1;
        for (int i = 0; i < items.size(); i++) {
            CommentInfo info = items.get(i);
            if (info.getParent_id().equals(parent_id)
                    && CommentType.COMMENT_LOAD_MORE_ID.equals(info.getId())) {//往“查看更多回复”的位置处出入二级评论
                insertIndex = i;
                break;
            }
        }
        if (insertIndex > 0) {
            addAll(data, insertIndex, false);
        }
    }

    /**
     * 根据id查index
     *
     * @param _id
     * @return
     */
    public int findIndexById(String _id) {
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            CommentInfo info = items.get(i);
            if (info.getId().equals(_id)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * 获取子评论数量，因为加载更多和收起都是属于parent_id的子评论，所以最后结果要减去1
     *
     * @param parent_id 一级评论id
     * @return
     */
    public int getChildCount(String parent_id) {
        int count = 0;
        for (int i = 0; i < getItemCount(); i++) {
            CommentInfo commentInfo = getItems().get(i);
            if (commentInfo.getParent_id().equals(parent_id)) {
                count++;
            }
        }
        return count - 1;
    }

    /**
     * 展开更多回复按钮变成收起
     *
     * @param parent_id 2级评论的parent_id
     */
    public void loadMore2Collapse(String parent_id) {
        for (int i = 0; i < items.size(); i++) {
            CommentInfo info = items.get(i);
            if (info.getParent_id().equals(parent_id)
                    && CommentType.COMMENT_LOAD_MORE_ID.equals(info.getId())) {
                info.setId(CommentType.COMMENT_COLLAPSE_ID);
                info.setComment_type(CommentType.COMMENT_COLLAPSE);
                notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 收起按钮变成展开更多回复
     *
     * @param parent_id 2级评论的parent_id
     */
    public void collapse2LoadMore(String parent_id) {
        for (int i = 0; i < items.size(); i++) {
            CommentInfo info = items.get(i);
            if (info.getParent_id().equals(parent_id)
                    && CommentType.COMMENT_COLLAPSE_ID.equals(info.getId())) {
                info.setId(CommentType.COMMENT_LOAD_MORE_ID);
                info.setComment_type(CommentType.COMMENT_LOAD_MORE);
//                setCommentPage(parent_id, 0);
                notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 获取是否允许评论状态
     */
    public boolean isAllowComment() {
        return isAllowComment;
    }

    /**
     * 设置是否允许评论
     *
     * @param allowComment
     */
    public void setAllowComment(boolean allowComment) {
        isAllowComment = allowComment;
    }


    /**
     * 所有viewHolder的父类
     */
    public static abstract class BaseCommentViewHolder extends RecyclerView.ViewHolder {

        public BaseCommentViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public abstract void updateView(CommentInfo commentInfo);
    }

    /**
     * 一级评论ViewHolder
     */
    public static class CommentL1ViewHolder extends BaseCommentViewHolder {

        public CommentL1ViewHolder(@NonNull ItemCommentL1Binding itemView) {
            super(itemView.getRoot());
        }

        @Override
        public void updateView(CommentInfo commentInfo) {

        }
    }

    /**
     * 二级评论ViewHolder
     */
    public static class CommentL2ViewHolder extends BaseCommentViewHolder {

        public CommentL2ViewHolder(@NonNull ItemCommentL2Binding itemView) {
            super(itemView.getRoot());
        }

        @Override
        public void updateView(CommentInfo commentInfo) {

        }
    }

    /**
     * 加载更多的ViewHolder
     */
    public static class CommentLoadMoreViewHolder extends RecyclerView.ViewHolder {
        private ItemCommentLoadmoreBinding mItemCommentLoadmoreBinding;

        public CommentLoadMoreViewHolder(@NonNull ItemCommentLoadmoreBinding itemView) {
            super(itemView.getRoot());
            mItemCommentLoadmoreBinding = itemView;
        }

        public void updateView(CommentInfo commentInfo) {
            mItemCommentLoadmoreBinding.tvChildCount.setText("展开" + commentInfo.getChild_count() + "条回复");
        }
    }

    /**
     * 收起的ViewHolder
     */
    public static class CommentCollapseViewHolder extends RecyclerView.ViewHolder {

        public CommentCollapseViewHolder(@NonNull ItemCommentCollapseBinding itemView) {
            super(itemView.getRoot());
        }

        public void updateView(CommentInfo commentInfo) {

        }
    }

    /**
     * 空页面的ViewHolder
     */
    public static class CommentEmptyViewHolder extends RecyclerView.ViewHolder {

        public CommentEmptyViewHolder(@NonNull View itemView) {
            super(itemView);
            TextView textView = (TextView) itemView;
            textView.setTextColor(0XFF282828);
            textView.setTextSize(20);
            textView.setTypeface(Typeface.DEFAULT_BOLD);
            textView.setText("还没有评论");
            textView.setGravity(Gravity.CENTER);
            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            textView.setLayoutParams(lp);
        }
    }

    public static interface CommentL1Listener {
        void click(int position, CommentInfo commentInfo);
    }

    public static interface CommentL2Listener {
        void click(int position, CommentInfo commentInfo);
    }

    public static interface CommentLoadMoreListener {
        void click(int position);
    }

    public CommentL1Listener getCommentL1Listener() {
        return commentL1Listener;
    }

    public void setCommentL1Listener(CommentL1Listener commentL1Listener) {
        this.commentL1Listener = commentL1Listener;
    }

    public void setCommentL2Listener(CommentL2Listener commentL2Listener) {
        this.commentL2Listener = commentL2Listener;
    }

    public void setCommentLoadMoreListener(CommentLoadMoreListener commentLoadMoreListener) {
        this.commentLoadMoreListener = commentLoadMoreListener;
    }
}
