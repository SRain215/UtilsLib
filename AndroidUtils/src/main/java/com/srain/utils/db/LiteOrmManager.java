package com.srain.utils.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.SQLiteHelper;
import com.litesuits.orm.db.model.ConflictAlgorithm;
import com.litesuits.orm.log.OrmLog;
import com.srain.utils.LogUtils;
import com.srain.utils.ToastUtils;

import java.util.Collection;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by SRain on 2016/12/29.
 * <p/>
 * 数据库操作类
 */
public class LiteOrmManager implements SQLiteHelper.OnUpdateListener, EasyPermissions.PermissionCallbacks {

    private static final String TAG = "LiteOrmManager";
    private Context context;
    private static LiteOrmManager mManager;
    private static LiteOrm mLiteOrm;

    public static LiteOrmManager getInstance(Context context, String dbName) {
        if (null == mManager) {
            mManager = new LiteOrmManager(context, dbName);
        }
        return mManager;
    }

    private LiteOrmManager(Context context, String dbName) {
        this.context = context;
        mLiteOrm = getliteOrm(dbName);
    }

    public LiteOrm getliteOrm(String dbName) {
        if (mLiteOrm == null) {
            Log.e(TAG, " --- DB_NAME == " + dbName);
            DataBaseConfig config = new DataBaseConfig(context, dbName);
            config.dbVersion = 1; // set database version
            config.onUpdateListener = null; // set database update listener
            LogUtils.i(TAG, "getliteOrm --- config == " + config.toString());

            int sdkCode = Build.VERSION.SDK_INT;
            LogUtils.i(TAG, "getliteOrm --- SDK_INT == " + sdkCode);
            mLiteOrm = LiteOrm.newSingleInstance(config); //独立操作，适用于没有级联关系的单表操作
//              mLiteOrm = LiteOrm.newCascadeInstance(config); //级联操作,适用于多表级联操作
        }
        mLiteOrm.setDebugged(true);
        return mLiteOrm;
    }

    @Override
    public void onUpdate(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long save(Object o) {
        if (o == null) {
            return 0;
        }
        return mLiteOrm.save(o);
    }

    public <T> int save(List<T> collection) {
        if (null == collection || collection.isEmpty()) {
            ToastUtils.show(context, "保存失败");
            return 0;
        }
        return mLiteOrm.save(collection);
    }

    public int delete(Object object) {
        if (null == object) {
            return -1;
        }
        return mLiteOrm.delete(object);
    }

    public <T> int delete(Class<T> tClass) {
        if (tClass == null) {
            return 0;
        }
        return mLiteOrm.delete(tClass);
    }

    public <T> int deleteByCollection(Collection<T> var) {
        int id = -1;
        if (null != var) {
            id = mLiteOrm.delete(var);
        }
        return id;
    }

    public int modify(Object o) {
        int id = mLiteOrm.update(o, ConflictAlgorithm.Replace);
        return id;
    }

    public <T> List<T> queryAll(Class<T> tClass) {
        if (tClass == null) {
            return null;
        }
        LogUtils.e(TAG, "queryAll --- tClass == " + tClass.getName());
        return mLiteOrm.query(tClass);
    }

    public <T> T queryById(long id, Class<T> tClass) {
        if (0 == id) {
            return null;
        }
        return mLiteOrm.queryById(id, tClass);
    }

    public <T> List<T> queryOrderby(Class<T> tClass, String orderby) {
        if (tClass == null) {
            return null;
        }
        QueryBuilder builder = new QueryBuilder<T>(tClass)
                .distinct(true);
        if (!orderby.isEmpty()) {
            builder.orderBy(orderby);
        }
        List<T> list = mLiteOrm.query(builder);
        OrmLog.e(TAG, list);
        return list;
    }

    public <T> List<T> queryByKey(String key, String value, Class<T> tClass, String orderby) {
        if (tClass == null) {
            return null;
        }
        QueryBuilder builder = new QueryBuilder<T>(tClass)
                .whereEquals(key, value)
                .distinct(true);
        if (!orderby.isEmpty()) {
            builder.orderBy(orderby);
        }
        List<T> list = mLiteOrm.query(builder);
        OrmLog.e(TAG, list);
        return list;
    }

    public <T> List<T> queryByBuilder(QueryBuilder builder) {
        List<T> list = mLiteOrm.query(builder);
        OrmLog.e(TAG, list);
        return list;
    }

    /**
     * 根据id列表查找
     *
     * @param key
     * @param ids
     * @param tClass
     * @param orderby
     * @param <T>
     * @return
     */
    public <T> List<T> queryByIds(String key, Object[] ids, Class<T> tClass, String orderby) {
        if (tClass == null) {
            return null;
        }
        QueryBuilder builder = new QueryBuilder<T>(tClass)
                .whereIn(key, ids)
                .distinct(true);
        if (!orderby.isEmpty()) {
            builder.orderBy(orderby);
        }
        List<T> list = mLiteOrm.query(builder);
        OrmLog.e(TAG, list);
        return list;
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

    }

//    public <T> List<T> queryParentByModelKey(String key) {
//        if (TextUtils.isEmpty(key)) {
//            return null;
//        }
//        QueryBuilder builder = new QueryBuilder<ReportParent>(ReportParent.class)
//                .whereEquals(ReportParent.KEY_MODEL_KEY, key)
//                .distinct(true);
//        List<T> list = mLiteOrm.query(builder);
//        OrmLog.e(TAG, list);
//        return list;
//    }
//
//    public static void init(Context context) {
//        try {
//            if (mLiteOrm.getWritableDatabase() != null) {
//                mLiteOrm.deleteDatabase();
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "init --- e == " + e.toString());
//        }
//        mLiteOrm.openOrCreateDatabase();
//        try {
//            JSONArray pArray = JsonModelImpl.getParentJson(context);
//            if (null != pArray && 0 < pArray.length()) {
//                for (int i = 0; i < pArray.length(); i++) {
//                    ReportParent parent = new ReportParent(pArray.getJSONObject(i));
//                    mLiteOrm.save(parent);
//                }
//            }
//
//            JSONArray rArray = JsonModelImpl.getReportJson(context);
//            if (null != rArray && 0 < rArray.length()) {
//                for (int i = 0; i < rArray.length(); i++) {
//                    JSONObject json = rArray.getJSONObject(i);
//                    Log.e(TAG, "json report == " + json);
//                    Report report = new Report(json);
//                    Log.e(TAG, "report == " + report.toString());
//                    mLiteOrm.save(report);
//                }
//            }
//
//            JSONArray infoArray = JsonModelImpl.getReportInfoJson(context);
//            if (null != infoArray && 0 < infoArray.length()) {
//                for (int i = 0; i < infoArray.length(); i++) {
//                    JSONObject json = infoArray.getJSONObject(i);
//                    Log.e(TAG, "json report info == " + json);
//                    ReportInfo info = new ReportInfo(json);
//                    Log.e(TAG, "report info == " + info.toString());
//                    mLiteOrm.save(info);
//                }
//            }
//
//            JSONArray statusArray = JsonModelImpl.getStatusJson(context);
//            if (null != statusArray && 0 < statusArray.length()) {
//                for (int i = 0; i < statusArray.length(); i++) {
//                    JSONObject json = statusArray.getJSONObject(i);
//                    ReportStatus status = new ReportStatus(json);
//                    mLiteOrm.save(status);
//                }
//            }
//
//            JSONArray thresholdArray = JsonModelImpl.getThresholdJson(context);
//            if (null != thresholdArray && 0 < thresholdArray.length()) {
//                for (int i = 0; i < thresholdArray.length(); i++) {
//                    JSONObject json = thresholdArray.getJSONObject(i);
//                    ReportThreshold threshold = new ReportThreshold(json);
//                    mLiteOrm.save(threshold);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e(TAG, "init --- e == " + e.toString());
//        }
//    }
//
//    public static List<ReportInfo> getSearchReportInfo(long rID, int dataType) {
//        List<ReportInfo> mReportInfos = null;
//        QueryBuilder builder = new QueryBuilder<ReportInfo>(ReportInfo.class)
//                .whereEquals(ReportInfo.KEY_R_ID, rID)
//                .whereAppendAnd()
//                .whereEquals(ReportInfo.KEY_IN_SEARCH, true)
//                .whereAppendAnd()
//                .whereEquals(ReportInfo.KEY_DATA_TYPE, dataType)
//                .distinct(true);
//        builder.orderBy(ReportInfo.KEY_SHOW_INDEX);
//
//        mReportInfos = mLiteOrm.query(builder);
//        OrmLog.e(TAG, "getSearchReportInfo --- " + mReportInfos);
//        return mReportInfos;
//    }
}
