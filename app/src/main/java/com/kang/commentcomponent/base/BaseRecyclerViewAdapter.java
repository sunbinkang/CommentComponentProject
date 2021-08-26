package com.kang.commentcomponent.base;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by sunbinkang on 2020/9/30.
 */
public abstract class BaseRecyclerViewAdapter<K extends RecyclerView.ViewHolder, T> extends RecyclerView.Adapter<K> {

    protected Context context;
    protected List<T> items;
    protected OnItemViewClick onItemViewClick;

    public void setContext(Context context) {
        this.context = context;
    }

    public BaseRecyclerViewAdapter(Context context) {
        this.context = context;
        items = new ArrayList<>();
    }

    public void add(T t) {
        add(t, items.size());
    }

    public void add(T t, int position) {
        items.add(position, t);
        notifyDataSetChanged();
    }

    public void addAll(Collection<T> collection) {
        addAll(collection, items.size(), false);
    }

    public void addAll(Collection<T> collection, int position, boolean reload) {
        if (collection == null || collection.size() == 0)
            return;
        if (reload) {
            items.clear();
        }
        items.addAll(position, collection);
        notifyDataSetChanged();
    }

    public void insert(T t) {
        insert(t, items.size());
    }

    public void insert(T t, int position) {
        items.add(position, t);
        notifyItemInserted(position + 1);
    }

    public void insertAll(Collection<T> collection) {
        insertAll(collection, items.size());
    }

    public void insertAll(Collection<T> collection, int position) {
        if (collection == null || collection.size() == 0)
            return;
        items.addAll(position, collection);
        notifyItemRangeChanged(items.size() - 1, collection.size());
    }

    public void removeChange(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void remove(T t, boolean reload) {
        items.remove(t);
        if (reload) {
            notifyDataSetChanged();
        }
    }

    public T remove(int position, boolean reload) {
        T t = items.remove(position);
        if (reload) {
            notifyDataSetChanged();
        }
        return t;
    }

    public void recycle() {
        items.clear();
        notifyDataSetChanged();
    }

    public void clear() {
        items.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<T> getItems() {
        return items;
    }

    public void setOnItemViewClick(OnItemViewClick onItemViewClick) {
        this.onItemViewClick = onItemViewClick;
    }

    public static interface OnItemViewClick {
        void onItemViewClick(View view, int position);

    }
}