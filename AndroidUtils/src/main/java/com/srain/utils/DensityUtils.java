package com.srain.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

/**
 * 常用单位转换的辅助类
 * <p/>
 * ndroid主要有以下几种屏：
 * QVGA和WQVGA屏density=120；
 * HVGA屏density=160；
 * WVGA屏density=240；
 * <p/>
 * px = dp * (dpi / 160) 即px =dp*density;(density=densityDpi/160)
 * <p/>
 *
 * @author SRain
 *         参考博客：
 *         http://blog.csdn.net/jiangwei0910410003/article/details/40509571
 */
public class DensityUtils {
    private DensityUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * dp转px
     * 根据分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dpVal
     * @return
     */
    public static int dp2px(Context context, float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.getResources().getDisplayMetrics());

//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px转dp
     * 根据分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context
     * @param pxVal
     * @return
     */
    public static float px2dp(Context context, float pxVal) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (pxVal / scale);

//        final float scale = context.getResources().getDisplayMetrics().density;
//        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * sp转px
     *
     * @param context
     * @param spVal
     * @return
     */
    public static int sp2px(Context context, float spVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spVal, context.getResources().getDisplayMetrics());
    }

    /**
     * px转sp
     *
     * @param pxVal
     * @return
     */
    public static float px2sp(Context context, float pxVal) {
        return (pxVal / context.getResources().getDisplayMetrics().scaledDensity);
//        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
//        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * dp转成px
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        return (int) (dipValue * getDensity(context) + 0.5f);
    }

    /**
     * px转成dp
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        return (int) (pxValue / getDensity(context) + 0.5f);
    }

    /**
     * sp转成px
     *
     * @param spValue
     * @param type
     * @return
     */
    public static float sp2px(Context context, float spValue, int type) {
        float scaledDensity = getScaledDensity(context);
        switch (type) {
            case CHINESE:
                return spValue * scaledDensity;
            case NUMBER_OR_CHARACTER:
                return spValue * scaledDensity * 10.0f / 18.0f;
            default:
                return spValue * scaledDensity;
        }
    }

    //TODO 区分中文
    private static final int CHINESE = 1;
    private static final int NUMBER_OR_CHARACTER = 2;

    /*-------------------------------------------------*/
    // 获取屏幕的信息有现在我知道的有两种方法：

    /**
     * 获取屏幕信息
     * <p/>
     * int width = metric.widthPixels;  // 屏幕宽度（像素）
     * int height = metric.heightPixels;  // 屏幕高度（像素）
     * float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
     * int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
     */
    public static DisplayMetrics getMetrics(Context context) {
        DisplayMetrics metrics = new DisplayMetrics();
        metrics = context.getResources().getDisplayMetrics();
        return metrics;
    }

    /**
     * 获取屏幕信息
     */
    public static DisplayMetrics getMetrics(Activity context) {
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    /**
     * sp转px
     *
     * @param context
     * @param scaledPixelSize
     * @return
     */
    public static int sp2px(Context context, int scaledPixelSize) {
        DisplayMetrics dm = getMetrics(context);
        return (int) (scaledPixelSize * dm.scaledDensity);
    }

    public static int sp2px(Activity context, int scaledPixelSize) {
        DisplayMetrics dm = getMetrics(context);
        return (int) (scaledPixelSize * dm.scaledDensity);
    }

    /**
     * 获取屏幕密度
     * density值表示每英寸有多少个显示点，将dip和dpi联系在一起了,与分辨率是两个不同的概念
     * float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
     * 参见android中的文档 :docs/reference/android/util/DisplayMetrics.html
     *
     * @param context
     * @return
     */
    public static float getDensity(Context context) {
        return getMetrics(context).density;
    }

    public static float getDensity(Activity context) {
        return getMetrics(context).density;
    }

    /**
     * 获取屏幕密度DPI
     * 每寸像素：120/160/240/320
     * int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
     */
    public static int getDensityDpi(Context context) {
        return getMetrics(context).densityDpi;
    }

    public static int getDensityDpi(Activity context) {
        return getMetrics(context).densityDpi;
    }

    /**
     * 获取屏幕相对密度
     *
     * @param context
     * @return
     */
    public static float getScaledDensity(Context context) {
        return getMetrics(context).scaledDensity;
    }

    public static float getScaledDensity(Activity context) {
        return getMetrics(context).scaledDensity;
    }

    /**
     * 获得屏幕尺寸
     * int width = metric.widthPixels;  // 屏幕宽度（像素）
     * int height = metric.heightPixels;  // 屏幕高度（像素）
     *
     * @param context
     * @return
     */
    public static Point getScreenSize(Context context) {
        Point point = new Point();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getRealSize(point);
        return point;
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }

    /**
     * 获得状态栏的高度
     *
     * @param context
     * @return
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = -1;
        try {
            Class<?> clazz = Class.forName("com.android.internal.R$dimen");
            Object object = clazz.newInstance();
            int height = Integer.parseInt(clazz.getField("status_bar_height").get(object).toString());
            statusHeight = context.getResources().getDimensionPixelSize(height);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return statusHeight;
    }

    /**
     * 获取当前屏幕截图，包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, 0, width, height);
        view.destroyDrawingCache();
        return bp;
    }

    /**
     * 获取当前屏幕截图，不包含状态栏
     *
     * @param activity
     * @return
     */
    public static Bitmap snapShotWithoutStatusBar(Activity activity) {
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bmp = view.getDrawingCache();
        Rect frame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
        int statusBarHeight = frame.top;

        int width = getScreenWidth(activity);
        int height = getScreenHeight(activity);
        Bitmap bp = null;
        bp = Bitmap.createBitmap(bmp, 0, statusBarHeight, width, height - statusBarHeight);
        view.destroyDrawingCache();
        return bp;
    }

    public static String getIMSI(Context context) {
        String strIMSI = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            strIMSI = telephonyManager.getSubscriberId();
        } catch (Exception exception1) {
        }
        return strIMSI;
    }

    public static String getIMEI(Context context) {
        String imei = "";
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            imei = telephonyManager.getDeviceId();
        } catch (Exception exception1) {
        }
        return imei;
    }

    public static String getDeviceId(Context context) {
        String strMd5 = "";
        try {
            String strIMEI = getIMEI(context);
            if (strIMEI == null || strIMEI.equals("")) {
                strIMEI = getIMSI(context);
                if (strIMEI == null || strIMEI.equals("")) {
                    return "";
                }
            }
            String strTemp = strIMEI + strIMEI + strIMEI;
            strMd5 = MiscUtil.getMD5(strTemp.getBytes());
        } catch (Exception exception1) {
        }
        return strMd5;
    }
}
