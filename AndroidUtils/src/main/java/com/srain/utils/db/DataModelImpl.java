package com.srain.utils.db;

import android.content.Context;

import com.litesuits.orm.db.assit.QueryBuilder;
import com.srain.utils.LogUtils;

import java.util.Collection;
import java.util.List;

/**
 * Created by liushuanglong on 2017/9/12.
 * <p>
 * 数据库实现方法
 */

public abstract class DataModelImpl implements IDataModel {

    private static final String TAG = "DataModelImpl";
    private Context mContext;
    private Class mClass;
    protected LiteOrmManager mOrmManager;

    public <T> DataModelImpl(Context context, Class<T> tClass, String dbName) {
        mContext = context;
        mOrmManager = LiteOrmManager.getInstance(mContext, dbName);
        this.mClass = tClass;
        LogUtils.i(TAG, "DataModelImpl " + mClass.getName());
    }

    @Override
    public long save(Object o) {
        long id = mOrmManager.save(o);
        return id;
    }

    @Override
    public <T> int save(List<T> collection) {
        int id = mOrmManager.save(collection);
        return id;
    }

    @Override
    public int delete() {
        int id = mOrmManager.delete(mClass);
        return id;
    }

    @Override
    public void deleteAll(Class cls) {
        mOrmManager.delete(cls);
    }

    @Override
    public int deleteByObject(Object object) {
        int id = mOrmManager.delete(object);
        return id;
    }

    @Override
    public <T> int delete(Collection<T> var) {
        return mOrmManager.deleteByCollection(var);
    }

    @Override
    public int modify(Object o) {
        int id = mOrmManager.modify(o);
        return id;
    }

    @Override
    public <T> List<T> queryAll() {
        return mOrmManager.queryAll(mClass);
    }

    @Override
    public <T> T queryById(long id) {
        return (T) mOrmManager.queryById(id, mClass);
    }

    @Override
    public <T> List<T> queryOrderby(String orderby) {
        return mOrmManager.queryOrderby(mClass, orderby);
    }

    @Override
    public <T> List<T> queryByKey(String key, String value, String orderby) {
        return mOrmManager.queryByKey(key, value, mClass, orderby);
    }

    @Override
    public <T> List<T> queryByBuilder(QueryBuilder builder) {
        return mOrmManager.queryByBuilder(builder);
    }
}
