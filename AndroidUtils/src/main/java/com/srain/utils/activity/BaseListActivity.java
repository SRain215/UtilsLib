package com.srain.utils.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.srain.utils.R;
import com.srain.utils.adapter.BasicAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SRain on 2017/9/29.
 * <p>
 * 含有ListView或GridView的Activity基类(不含下拉刷新功能)
 */
public abstract class BaseListActivity<T> extends BaseActivity {

    private static final String TAG = "BaseListActivity";

    protected ListView listView;
    protected BasicAdapter adapter;
    protected List<T> data = new ArrayList<T>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initListView();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initListView();
    }

    public void initListView() {
        listView = (ListView) findViewById(R.id.list_view);
        adapter = initAdapter();
        listView.setAdapter(adapter);
        initView();
    }

    protected abstract void initView();

    abstract public BasicAdapter initAdapter();
}
