package com.kang.commentcomponent.base;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.kang.commentcomponent.R;

//import com.hlscz.R;

/**
 * Created by sunbinkang on 2020/9/30.
 */
public abstract class BaseEmptyRecyclerViewAdapter<H extends RecyclerView.ViewHolder, K> extends BaseRecyclerViewAdapter<RecyclerView.ViewHolder, K> {
    public static final int EMPTY_VIEW = -0X0001;
    public static final int LOADING_VIEW = -0X0011;
    public static final int NETWORK_ERROR_VIEW = -0X0111;
    public static final int NONE_VIEW = 0X0000;
    public static final int CONTENT_VIEW = 0x0002;
    public static final int NO_MORE_VIEW = 0x0003;

    public OnItemViewClick onNetworkErrorRetryClick;

    public OnItemViewClick onEmptyClick;

    private State dataState = State.EMPTY;

    public BaseEmptyRecyclerViewAdapter(Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        if (items == null || items.size() == 0) {
            return 1;
        } else if (dataState == State.NO_MORE) {
            return items.size() + 1;
        }
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (items == null || items.size() == 0) {
            if (dataState == State.EMPTY) {
                return EMPTY_VIEW;
            } else if (dataState == State.NETWORK_ERROR) {
                return NETWORK_ERROR_VIEW;
            } else if (dataState == State.LODDING) {
                return LOADING_VIEW;
            } else if (dataState == State.NONE) {
                return NONE_VIEW;
            } else {
                return NONE_VIEW;
            }
        } else if (dataState == State.NO_MORE && position == items.size()) {
            return NO_MORE_VIEW;
        } else {
            return getItemViewContentType(position);
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == EMPTY_VIEW) {
            return getEmptyViewHolder(viewGroup, viewType);
        } else if (viewType == NETWORK_ERROR_VIEW) {
            return getNetworkErrorViewHolder(viewGroup, viewType);
        } else if (viewType == NONE_VIEW) {
            return getNoneViewHolder(viewGroup, viewType);
        } else if (viewType == LOADING_VIEW) {
            return getLoadingViewHolder(viewGroup, viewType);
        } else if (viewType == NO_MORE_VIEW) {
            return getNoMoreViewHolder(viewGroup, viewType);
        }
        return getContentViewHolder(viewGroup, viewType);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewTYpe = getItemViewType(position);
//        if (itemViewTYpe == getItemViewContentType(position)) {
//            bindContentViewHolder((H) viewHolder, position);
//        } else if (itemViewTYpe == NETWORK_ERROR_VIEW) {
//            editPlaceHolderView(viewHolder.itemView);
//            //network error
//            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onNetworkErrorRetryClick != null) {
//                        onNetworkErrorRetryClick.onItemViewClick(v, 0);
//                    }
//                }
//            });
//        } else if (itemViewTYpe == EMPTY_VIEW) {
//            editPlaceHolderView(viewHolder.itemView);
//            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (onEmptyClick != null) {
//                        onEmptyClick.onItemViewClick(v, 0);
//                    }
//                }
//            });
//        } else if (itemViewTYpe == LOADING_VIEW) {
////            editPlaceHolderView(viewHolder.itemView);
//        } else if (itemViewTYpe == NONE_VIEW) {
//
//        } else if (itemViewTYpe == NO_MORE_VIEW) {
//
//        }

        if (itemViewTYpe == NETWORK_ERROR_VIEW) {
            editPlaceHolderView(viewHolder.itemView);
            //network error
            viewHolder.itemView.setOnClickListener(v -> {
                if (onNetworkErrorRetryClick != null) {
                    onNetworkErrorRetryClick.onItemViewClick(v, 0);
                }
            });
        } else if (itemViewTYpe == EMPTY_VIEW) {
            editPlaceHolderView(viewHolder.itemView);
            viewHolder.itemView.setOnClickListener(v -> {
                if (onEmptyClick != null) {
                    onEmptyClick.onItemViewClick(v, 0);
                }
            });
        } else if (itemViewTYpe == LOADING_VIEW) {

        } else if (itemViewTYpe == NO_MORE_VIEW) {
            editPlaceHolderView(viewHolder.itemView);
        } else if (itemViewTYpe == NONE_VIEW) {

        } else {
            bindContentViewHolder((H) viewHolder, position);
        } 
    }

    /**
     * 加載頁面
     *
     * @param parent
     * @param viewType
     * @return
     */
    private RecyclerView.ViewHolder getLoadingViewHolder(ViewGroup parent, int viewType) {
        View itemLoadingView = LayoutInflater.from(context).inflate(R.layout.item_data_loading, parent, false);
        return new PlaceViewHolder(itemLoadingView);
    }


    /**
     * 加載更多頁面
     *
     * @param parent
     * @param viewType
     * @return
     */
    private RecyclerView.ViewHolder getNoMoreViewHolder(ViewGroup parent, int viewType) {
        View itemLoadingView = LayoutInflater.from(context).inflate(R.layout.item_data_nomore, parent, false);
        return new PlaceViewHolder(itemLoadingView);
    }

    /**
     * 獲取空頁面
     *
     * @param parent
     * @param viewType
     * @return
     */
    private RecyclerView.ViewHolder getNoneViewHolder(ViewGroup parent, int viewType) {
        LinearLayout.LayoutParams lllp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout ll = new LinearLayout(context);
        ll.setBackgroundColor(Color.BLACK);
        ll.setLayoutParams(lllp);
        return new PlaceViewHolder(ll);
    }

    /**
     * 获取无内容view
     *
     * @param parent
     * @param viewType
     * @return
     */
    private RecyclerView.ViewHolder getEmptyViewHolder(ViewGroup parent, int viewType) {
        View itemEmptyView = LayoutInflater.from(context).inflate(R.layout.item_data_nodata, parent, false);
        return new PlaceViewHolder(itemEmptyView);
    }

    /**
     * 获取网络加载数据失败的view
     *
     * @param parent
     * @param viewType
     * @return
     */
    private RecyclerView.ViewHolder getNetworkErrorViewHolder(ViewGroup parent, int viewType) {
        View itemErrorView = LayoutInflater.from(context).inflate(R.layout.item_data_network_error, parent, false);
        return new PlaceViewHolder(itemErrorView);
    }

    /**
     * 當顯示PlaceHolder的時候，自動佔滿頁面
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getLayoutPosition();
        if (getItemViewType(position) == EMPTY_VIEW || getItemViewType(position) == NETWORK_ERROR_VIEW
                || getItemViewType(position) == LOADING_VIEW || getItemViewType(position) == NONE_VIEW) {
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (null != lp && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) lp;
                if (!p.isFullSpan()) {
                    p.setFullSpan(true);//占满一行
                }
            }
        }
    }

    /**
     * 设置网络加载数据失败重试的点击事件
     *
     * @param onNetworkErrorRetryClick 点击事件
     */
    public void setOnNetworkErrorRetryClick(OnItemViewClick onNetworkErrorRetryClick) {
        this.onNetworkErrorRetryClick = onNetworkErrorRetryClick;
    }

    /**
     * 設置空頁面的時候點擊事件
     *
     * @param onEmptyClick
     */
    public void setOnEmptyClick(OnItemViewClick onEmptyClick) {
        this.onEmptyClick = onEmptyClick;
    }

    /**
     * 设置数据状态, 如果列表存在数据，设置这个则不生效
     *
     * @param dataState 数据状态 {@linkplain State}
     */
    public void setDataState(State dataState) {
        this.dataState = dataState;
        notifyItemChanged(0);
    }

    /**
     * 设置没有更多的数据状态,需要重新刷新一下页面
     */
    public void setNoMoreState() {
        this.dataState = State.NO_MORE;
        notifyDataSetChanged();
    }


    /**
     * 獲取数据状态 {@linkplain State State}
     *
     * @return
     */
    public State getDataState() {
        return dataState;
    }

    /**
     * 调整占位图，可能有一些页面的提示信息或者字体的修改，在此改动
     *
     * @param placeHolderView
     */
    protected void editPlaceHolderView(View placeHolderView) {

    }

    /**
     * itemViewContentType
     *
     * @param position
     * @return
     */
    protected abstract int getItemViewContentType(int position);

    /**
     * 生成显示item
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract H getContentViewHolder(ViewGroup parent, int viewType);

    /**
     * 绑定数据到item
     *
     * @param viewHolder
     * @param position
     */
    protected abstract void bindContentViewHolder(@NonNull H viewHolder, int position);

    /**
     * 占位的ViewHolder 显示无数据或者网络错误的情况
     */
    public static class PlaceViewHolder extends RecyclerView.ViewHolder {

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * 数据状态
     */
    public enum State {
        EMPTY(EMPTY_VIEW), NETWORK_ERROR(NETWORK_ERROR_VIEW), NONE(NONE_VIEW), LODDING(LOADING_VIEW),
        NO_MORE(NO_MORE_VIEW);


        int value;

        State(int emptyView) {
            this.value = emptyView;
        }
    }

}
