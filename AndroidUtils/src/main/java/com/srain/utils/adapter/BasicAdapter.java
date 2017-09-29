package com.srain.utils.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.srain.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liushuanglong on 2017/9/29.
 * <p>
 * 基本adapter
 */
public abstract class BasicAdapter<T> extends BaseAdapter {

    private static final String TAG = "BasicAdapter";

    protected Context mContext;
    protected List<T> data;
    protected View.OnClickListener mListener;
    protected LayoutInflater mInflater;
    protected int selectPosition = -1; // 记录当前选中的Item

    public BasicAdapter(Context context, List<T> data, View.OnClickListener listener) {
        mContext = context;
        this.data = data;
        mListener = listener;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        int count = 0;
        if (!isNullData()) {
            count = data.size();
        }
        return count;
    }

    @Override
    public T getItem(int position) {
        if (!isNullData()) {
            return this.data.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public abstract View getView(int i, View convertView, ViewGroup viewGroup);

    /**
     * data是否为null
     *
     * @return
     */
    protected boolean isNullData() {
        boolean isNull = ((null == data) || data.isEmpty());
        LogUtils.i(TAG, "isNullData --- isNull == " + isNull);
        return isNull;
    }

    /**
     * 根据position删除Item
     *
     * @param position
     */
    public void remove(int position) {
        if (!isNullData()) {
            this.data.remove(position);
            this.notifyDataSetInvalidated();
        }
    }

    /**
     * 清空list
     */
    public void clear() {
        if (!isNullData()) {
            this.data.clear();
            this.notifyDataSetInvalidated();
        }
    }

    /**
     * 添加数据(添加前先清空)
     *
     * @param data
     */
    public void setData(List<T> data) {
        if (isNullData()) {
            this.data = new ArrayList<T>();
        } else {
            this.data.clear();
        }
        this.data.addAll(data);
        this.notifyDataSetInvalidated();
    }

    /**
     * 添加数据(直接添加)
     *
     * @param data
     */
    public void addData(List<T> data) {
        if (isNullData()) {
            this.data = new ArrayList<T>();
        }
        this.addData(data);
        this.notifyDataSetInvalidated();
    }

    public void setSelectPosition(int position) {
        this.selectPosition = position;
    }

    public int getSelectPosition() {
        return selectPosition;
    }
}
