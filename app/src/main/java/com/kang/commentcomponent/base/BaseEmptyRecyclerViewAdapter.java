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
     * ????????????
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
     * ??????????????????
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
     * ???????????????
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
     * ???????????????view
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
     * ?????????????????????????????????view
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
     * ?????????PlaceHolder??????????????????????????????
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
                    p.setFullSpan(true);//????????????
                }
            }
        }
    }

    /**
     * ???????????????????????????????????????????????????
     *
     * @param onNetworkErrorRetryClick ????????????
     */
    public void setOnNetworkErrorRetryClick(OnItemViewClick onNetworkErrorRetryClick) {
        this.onNetworkErrorRetryClick = onNetworkErrorRetryClick;
    }

    /**
     * ????????????????????????????????????
     *
     * @param onEmptyClick
     */
    public void setOnEmptyClick(OnItemViewClick onEmptyClick) {
        this.onEmptyClick = onEmptyClick;
    }

    /**
     * ??????????????????, ???????????????????????????????????????????????????
     *
     * @param dataState ???????????? {@linkplain State}
     */
    public void setDataState(State dataState) {
        this.dataState = dataState;
        notifyItemChanged(0);
    }

    /**
     * ?????????????????????????????????,??????????????????????????????
     */
    public void setNoMoreState() {
        this.dataState = State.NO_MORE;
        notifyDataSetChanged();
    }


    /**
     * ?????????????????? {@linkplain State State}
     *
     * @return
     */
    public State getDataState() {
        return dataState;
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????
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
     * ????????????item
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract H getContentViewHolder(ViewGroup parent, int viewType);

    /**
     * ???????????????item
     *
     * @param viewHolder
     * @param position
     */
    protected abstract void bindContentViewHolder(@NonNull H viewHolder, int position);

    /**
     * ?????????ViewHolder ??????????????????????????????????????????
     */
    public static class PlaceViewHolder extends RecyclerView.ViewHolder {

        public PlaceViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * ????????????
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
