package com.srain.utils.db;

import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.Collection;
import java.util.List;

/**
 * Created by liushuanglong on 2017/4/27.
 * <p>
 * 数据库操作
 */
public interface IDataModel {

    long save(Object o);

    <T> int save(List<T> collection);

    int delete();

    <T> void deleteAll(Class<T> cls);

    int deleteByObject(Object object);

    <T> int delete(Collection<T> var);

    int modify(Object o);

    <T> List<T> queryAll();

    <T> T queryById(long id);

    <T> List<T> queryOrderby(String orderby);

    <T> List<T> queryByKey(String key, String value, String orderby);

    <T> List<T> queryByBuilder(QueryBuilder builder);
}
