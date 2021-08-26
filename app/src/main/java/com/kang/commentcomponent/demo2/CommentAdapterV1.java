package com.kang.commentcomponent.demo2;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kang.commentcomponent.R;
import com.kang.commentcomponent.base.BaseEmptyRecyclerViewAdapter;
import com.kang.commentcomponent.constants.CommentType;
import com.kang.commentcomponent.databinding.ItemCommentCollapseBinding;
import com.kang.commentcomponent.databinding.ItemCommentL1Binding;
import com.kang.commentcomponent.databinding.ItemCommentL2Binding;
import com.kang.commentcomponent.databinding.ItemCommentLoadmoreBinding;
import com.kang.commentcomponent.demo2.bean.CommentInfo;
import com.kang.commentcomponent.utils.TimeUtils;
import com.kang.commentcomponent.utils.ZLog;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by BinKang on 2021/8/13.
 * Des :评论的适配器(包含各种ViewHolder类)
 */
public class CommentAdapterV1 extends BaseEmptyRecyclerViewAdapter<RecyclerView.ViewHolder, CommentInfo> {

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
     * 收起二级评论监听
     */
    private CommentCollapseListener commentCollapseListener;

    /**
     * 是否允许评论
     */
    private boolean isAllowComment = true;

    private Map<String, Integer> commentPageMap;

    public CommentAdapterV1(Context context) {
        super(context);
        commentPageMap = new HashMap<>();
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
            viewHolder.itemView.setOnLongClickListener(v -> {
                if (commentL1Listener != null) {
                    commentL1Listener.longClick(position, commentInfo);
                }
                return false;
            });
        } else if (viewHolder instanceof CommentL2ViewHolder) {
            ((CommentL2ViewHolder) viewHolder).updateView(commentInfo);
            viewHolder.itemView.setOnClickListener(v -> {
                if (commentL2Listener != null) {
                    commentL2Listener.click(position, commentInfo);
                }
            });
            viewHolder.itemView.setOnLongClickListener(v -> {
                if (commentL2Listener != null) {
                    commentL2Listener.longClick(position, commentInfo);
                }
                return false;
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
//                    if (info.getReplyCommentId() == commentInfo.getReplyCommentId()) {
                    if (info.getRootCommentId() == commentInfo.getRootCommentId()) {
                        if (CommentType.COMMENT_COLLAPSE_ID_ == info.getCommentId()) {
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
                        ZLog.i("endDeleteIndex = " + endDeleteIndex + " startDeleteIndex = " + startDeleteIndex);
                        if (items.size() > endDeleteIndex) {
                            int deleteIndex = startDeleteIndex + 1;
                            for (int i = startDeleteIndex + 1; i <= endDeleteIndex; i++) {
                                ZLog.i("deleteIndex = " + deleteIndex);
                                items.remove(deleteIndex);
                            }
                            notifyDataSetChanged();
                        }
                    }
                }
                int commentL1ChildCount = endDeleteIndex - startDeleteIndex;
//                collapse2LoadMore(commentInfo.getReplyCommentId(), commentL1ChildCount);
                collapse2LoadMore(commentInfo.getRootCommentId(), commentL1ChildCount);
                //用户体验：加个监听，为了RecyclerView拿到跟评论的位置，滑到那个位置
                if (commentCollapseListener != null) {
                    int collapseAfterPosition = findIndexById(commentInfo.getRootCommentId());
                    commentCollapseListener.collapseAfterPosition(collapseAfterPosition);
                }
            });
        }
    }

    /**
     * 缺省页
     *
     * @param placeHolderView
     */
    @Override
    protected void editPlaceHolderView(View placeHolderView) {
        super.editPlaceHolderView(placeHolderView);
        if (getDataState() == State.EMPTY) {
            TextView textView = placeHolderView.findViewById(R.id.item_data_nodata_text);
            textView.setTextColor(0xFF282828);
            textView.setTextSize(18);
            textView.setText("还没有评论，赶快发表你的想法吧！");

        } else if (getDataState() == State.NETWORK_ERROR) {
            TextView text = placeHolderView.findViewById(R.id.item_data_network_error_text);
            text.setTextSize(18);
            text.setTypeface(Typeface.DEFAULT);
            text.setText("网络貌似出了点问题！");
            text.setTextColor(0xFF282828);
        }
    }

    /**
     * 添加对应的parent_id的二级评论
     *
     * @param parent_id
     * @param data
     */
    public void addL2Comment(int parent_id, List<CommentInfo> data) {
        int insertIndex = -1;
        for (int i = 0; i < items.size(); i++) {
            CommentInfo info = items.get(i);
            //测试模拟数据
//            if (info.getReplyCommentId() == parent_id
//                    && CommentType.COMMENT_LOAD_MORE_ID_ == info.getCommentId()) {//往“查看更多回复”的位置处插入二级评论
//                info.setCommentCount(-1);//comment_count为-1代表 查看更多回复
//                insertIndex = i;
//                break;
//            }
            if (info.getRootCommentId() == parent_id
                    && CommentType.COMMENT_LOAD_MORE_ID_ == info.getCommentId()) {//往“查看更多回复”的位置处插入二级评论
                info.setCommentCount(-1);//comment_count为-1代表 查看更多回复
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
    public int findIndexById(int _id) {
        int index = -1;
        for (int i = 0; i < items.size(); i++) {
            CommentInfo info = items.get(i);
            if (info.getCommentId() == _id) {
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
    public int getChildCount(int parent_id) {
        int count = 0;
        for (int i = 0; i < getItemCount(); i++) {
            CommentInfo commentInfo = getItems().get(i);
            if (commentInfo.getRootCommentId() == parent_id) {
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
    public void loadMore2Collapse(int parent_id) {
        for (int i = 0; i < items.size(); i++) {
            CommentInfo info = items.get(i);
            if (info.getRootCommentId() == parent_id
                    && CommentType.COMMENT_LOAD_MORE_ID_ == info.getCommentId()) {
                info.setCommentId(CommentType.COMMENT_COLLAPSE_ID_);
                info.setComment_type(CommentType.COMMENT_COLLAPSE);
                notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 收起按钮变成展开更多回复
     *
     * @param parent_id           2级评论的parent_id
     * @param commentL1ChildCount 对应的一级评论的子评论数量
     */
    public void collapse2LoadMore(int parent_id, int commentL1ChildCount) {
        for (int i = 0; i < items.size(); i++) {
            CommentInfo info = items.get(i);
            if (info.getRootCommentId() == parent_id
                    && CommentType.COMMENT_COLLAPSE_ID_ == info.getCommentId()) {
                info.setCommentId(CommentType.COMMENT_LOAD_MORE_ID_);
                info.setComment_type(CommentType.COMMENT_LOAD_MORE);
                info.setCommentCount(commentL1ChildCount);
                setCommentPage(String.valueOf(parent_id), 0);
                notifyDataSetChanged();
                break;
            }
        }
    }

    /**
     * 获取指定一级评论的加载更多翻页，每get一次该页码加1
     *
     * @param id 二级评论的parent_id
     * @return
     */
    public int getCommentPage(String id) {
        if (commentPageMap.containsKey(id)) {
            Integer page = commentPageMap.get(id);
            page++;
            commentPageMap.put(id, page);
            return page;
        } else {
            commentPageMap.put(id, 1);
            return 1;
        }
    }

    /**
     * 指定某个一级评论的加载更多页码
     *
     * @param id    二级评论的parent_id
     * @param count 页码
     */
    public void setCommentPage(String id, Integer count) {
        if (commentPageMap.containsKey(id)) {
            commentPageMap.put(id, count);
        }
    }

    /**
     * 页码减1
     *
     * @param id 二级评论的parent_id
     */
    public void minusCommentPage(String id) {
        if (commentPageMap.containsKey(id)) {
            Integer page = commentPageMap.get(id);
            page--;
            commentPageMap.put(id, page);
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
     * 往第0个位置插入刚评论的一级评论
     *
     * @param commentInfo
     */
    public void insertL1CommentToFirst(CommentInfo commentInfo) {
        commentInfo.setComment_type(CommentType.COMMENT_L1);
        items.add(0, commentInfo);
        notifyDataSetChanged();
    }

    /**
     * 往列表中插入二级评论
     *
     * @param commentInfo
     * @return 返回要插入的位置，方便滑动到置顶位置
     */
    public int insertL2CommentToList(CommentInfo commentInfo) {
        commentInfo.setComment_type(CommentType.COMMENT_L2);

        int indexById = findIndexById(commentInfo.getRootCommentId());
        int childCount = getChildCount(commentInfo.getRootCommentId());
        int insertIndex = indexById + 1;
        items.add(insertIndex, commentInfo);
        notifyDataSetChanged();
        return insertIndex;
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

        private ItemCommentL1Binding binding;

        public CommentL1ViewHolder(@NonNull ItemCommentL1Binding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        @Override
        public void updateView(CommentInfo commentInfo) {
            binding.tvReviewerName.setText(commentInfo.getUserName());
            binding.tvCommentContent.setText(commentInfo.getCommentContent());
            binding.tvCommentPublishTime.setText(TimeUtils.getRecentTime(commentInfo.getReplyTime(), "yyyy-MM-dd HH:mm:ss"));
//            binding.tvLikeCount.setText(String.valueOf(commentInfo.getLikeCount()));
//            binding.ivLikeStatus.setImageResource(commentInfo.getLike() == 0 ? R.drawable.icon_love_no : R.drawable.icon_love);

            binding.llPrise.setCommentInfo(commentInfo);
            binding.llPrise.setCount(commentInfo.getLikeCount());
            binding.llPrise.setOperate(commentInfo.getLike() == 1);

//            Glide.with(binding.getRoot().getContext())
//                    .asBitmap()
//                    .load(commentInfo.getUserImgUrl())
//                    .apply(GlideRequestOptionsManager.makeOptions(R.drawable.icon_default_avatar))
//                    .into(binding.ivReviewerAvatar);
        }
    }

    /**
     * 二级评论ViewHolder
     */
    public static class CommentL2ViewHolder extends BaseCommentViewHolder {

        private ItemCommentL2Binding binding;

        public CommentL2ViewHolder(@NonNull ItemCommentL2Binding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }

        @Override
        public void updateView(CommentInfo commentInfo) {
            binding.tvL2ReviewerName.setText(commentInfo.getUserName());
            binding.tvL2CommentContent.setText(commentInfo.getCommentContent());
            if (commentInfo.getIsAuthor() == 1) {
                binding.tvL2AuthorLabel.setVisibility(View.VISIBLE);
            } else {
                binding.tvL2AuthorLabel.setVisibility(View.GONE);
            }

            CommentInfo.FeedMsgReplyCommentUserVoBean feedMsgReplyCommentUserVo = commentInfo.getFeedMsgReplyCommentUserVo();
            if (feedMsgReplyCommentUserVo != null && !TextUtils.isEmpty(feedMsgReplyCommentUserVo.getReplyCommentUserName())) {
                binding.tvL2ReplyerName.setVisibility(View.VISIBLE);
                binding.tvL2ReplyerName.setText(feedMsgReplyCommentUserVo.getReplyCommentUserName());
            } else {
                binding.tvL2ReplyerName.setVisibility(View.GONE);
            }

            binding.tvL2CommentPublishTime.setText(TimeUtils.getRecentTime(commentInfo.getReplyTime(), "yyyy-MM-dd HH:mm:ss"));

//            binding.tvL2LikeCount.setText(String.valueOf(commentInfo.getLikeCount()));
//            binding.ivL2LikeStatus.setImageResource(commentInfo.getLike() == 0 ? R.drawable.icon_love_no : R.drawable.icon_love);
            binding.llL2Prise.setCommentInfo(commentInfo);
            binding.llL2Prise.setCount(commentInfo.getLikeCount());
            binding.llL2Prise.setOperate(commentInfo.getLike() == 1);

//            Glide.with(binding.getRoot().getContext())
//                    .asBitmap()
//                    .load(commentInfo.getUserImgUrl())
//                    .apply(GlideRequestOptionsManager.makeOptions(R.drawable.icon_default_avatar))
//                    .into(binding.ivL2ReviewerAvatar);
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
            if (commentInfo.getCommentCount() == -1) {//本地设置comment_count为-1时候，显示展开更多回复
                mItemCommentLoadmoreBinding.tvChildCount.setText("展开更多回复");
            } else {
                mItemCommentLoadmoreBinding.tvChildCount.setText("展开" + commentInfo.getCommentCount() + "条回复");
            }
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
        //单击
        void click(int position, CommentInfo commentInfo);

        //长按
        void longClick(int position, CommentInfo commentInfo);
    }

    public static interface CommentL2Listener {
        //单击
        void click(int position, CommentInfo commentInfo);

        //长按
        void longClick(int position, CommentInfo commentInfo);
    }

    public static interface CommentLoadMoreListener {
        void click(int position);
    }

    public static interface CommentCollapseListener {
        void collapseAfterPosition(int position);
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

    public void setCommentCollapseListener(CommentCollapseListener commentCollapseListener) {
        this.commentCollapseListener = commentCollapseListener;
    }
}
