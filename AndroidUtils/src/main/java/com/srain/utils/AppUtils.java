package com.srain.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.webkit.MimeTypeMap;

import com.srain.utils.bean.AppInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * author: SRain
 * blog  : https://my.oschina.net/u/2320057/blog
 * time  : 2016/09/30
 * desc  : App相关工具类
 * status: 待测试
 */
public class AppUtils {

    private static final String TAG = "AppUtils";
    private static PackageManager manager;

    private AppUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断App是否安装
     *
     * @param context     上下文
     * @param packageName 包名
     * @return {@code true}: 已安装<br>{@code false}: 未安装
     */
    public static boolean isInstallApp(Context context, String packageName) {
        return !StringUtils.isSpace(packageName) && IntentUtils.getLaunchAppIntent(context, packageName) != null;
    }

    /*----------------------安装------------------------------*/

    /**
     * 安装App(支持6.0)
     *
     * @param context  上下文
     * @param filePath 文件路径
     */
    public static void installApp(Context context, String filePath) {
        installApp(context, FileUtils.getFileByPath(filePath));
    }

    /**
     * 安装App(支持6.0)
     *
     * @param context 上下文
     * @param file    文件
     */
    public static void installApp(Context context, File file) {
        if (file != null) {
            context.startActivity(IntentUtils.getInstallAppIntent(file));
        }
    }

    /**
     * 安装App(支持6.0)
     *
     * @param activity    activity
     * @param filePath    文件路径
     * @param requestCode 请求值
     */
    public static void installApp(Activity activity, String filePath, int requestCode) {
        installApp(activity, FileUtils.getFileByPath(filePath), requestCode);
    }

    /**
     * 安装App(支持6.0)
     *
     * @param activity    activity
     * @param file        文件
     * @param requestCode 请求值
     */
    public static void installApp(Activity activity, File file, int requestCode) {
        if (file != null) {
            activity.startActivityForResult(IntentUtils.getInstallAppIntent(file), requestCode);
        }
    }

    /*---------------------------------卸载APP--------------------------------------*/

    /**
     * 卸载App
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void uninstallApp(Context context, String packageName) {
        if (!StringUtils.isSpace(packageName)) {
            context.startActivity(IntentUtils.getUninstallAppIntent(packageName));
        }
    }

    /**
     * 卸载App
     *
     * @param activity    activity
     * @param packageName 包名
     * @param requestCode 请求值
     */
    public static void uninstallApp(Activity activity, String packageName, int requestCode) {
        if (!StringUtils.isSpace(packageName)) {
//            activity.startActivityForResult(IntentUtils.IntentUtils.getUninstallAppIntent(packageName), requestCode);
            activity.startActivityForResult(IntentUtils.getUninstallAppIntent(packageName), requestCode);
        }
    }

    /*---------------------------------打开APP--------------------------------------*/

    /***
     * 打开App
     *
     * @param context     上下文
     * @param packageName 包名
     */
    public static void launchApp(Context context, String packageName) {
        if (!StringUtils.isSpace(packageName)) {
            context.startActivity(IntentUtils.getLaunchAppIntent(context, packageName));
        }
    }

    /***
     * 打开App
     *
     * @param activity    activity
     * @param packageName 包名
     * @param requestCode 请求值
     */
    public static void launchApp(Activity activity, String packageName, int requestCode) {
        if (!StringUtils.isSpace(packageName)) {
            activity.startActivityForResult(IntentUtils.getLaunchAppIntent(activity, packageName), requestCode);
        }
    }

    /*---------------------------------获取APP信息--------------------------------------*/

    /***
     * 获取App包名
     *
     * @param context 上下文
     * @return App包名
     */
    public static String getAppPackageName(Context context) {
        return context.getPackageName();
    }

    /***
     * 获取App具体设置**
     *
     * @param context 上下文
     */
    public static void getAppDetailsSettings(Context context) {
        getAppDetailsSettings(context, context.getPackageName());
    }

    /***
     * 获取App具体设置**
     *
     * @param context 上下文*
     */
    public static void getAppDetailsSettings(Context context, String packageName) {
        if (!StringUtils.isSpace(packageName)) {
            context.startActivity(IntentUtils.getAppDetailsSettingsIntent(packageName));
        }
    }

    /***
     * 获取App名称**
     *
     * @param context 上下文*
     * @return App名称
     */
    public static String getAppName(Context context) {
        return getAppName(context, context.getPackageName());
    }

    /***
     * 获取App名称**
     *
     * @param context     上下文*
     * @param packageName 包名*
     * @return App名称
     */
    public static String getAppName(Context context, String packageName) {
        String appName = "";
        if (!StringUtils.isSpace(packageName)) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(packageName, 0);
                appName = (pi == null) ? "" : pi.applicationInfo.loadLabel(pm).toString();
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return appName;
    }

    /**
     * TODO
     * 根据包名获取应用程序的名称
     *
     * @param context
     * @param packName
     * @return
     */
    public static String getAppName2(Context context, String packName) {
        PackageInfo info = getPackageInfo(context, packName);
        int labelRes = info.applicationInfo.labelRes;
        return context.getResources().getString(labelRes);
    }

    /***
     * 获取App路径**
     *
     * @param context 上下文*
     * @return App路径
     */
    public static String getAppPath(Context context) {
        return getAppPath(context, context.getPackageName());
    }

    /***
     * 获取App路径**
     *
     * @param context     上下文*
     * @param packageName 包名*
     * @return App路径
     */
    public static String getAppPath(Context context, String packageName) {
        String appPath = "";
        if (!StringUtils.isSpace(packageName)) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(packageName, 0);
                appPath = (pi == null) ? null : pi.applicationInfo.sourceDir;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return appPath;
    }

    /**
     * 获取当前应用包的管理实例
     *
     * @param context
     * @return
     */
    public static PackageManager getManager(Context context) {
        if (manager == null) {
            manager = context.getPackageManager();
        }
        return manager;
    }

    /**
     * 获取当前应用信息
     *
     * @param context
     * @return
     */
    public static PackageInfo getPackageInfo(Context context) {
        PackageInfo info = getPackageInfo(context, getPackageName(context));
        return info;
    }

    /**
     * 根据包名获取的应用信息
     *
     * @param context
     * @param packName
     * @return
     */
    public static PackageInfo getPackageInfo(Context context, String packName) {
        PackageInfo info = null;
        try {
            info = getManager(context).getPackageInfo(packName, 0);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(TAG, "getPackageInfo ---  e == " + e.toString());
        }
        return info;
    }

    /**
     * 获取当前程序的包名
     *
     * @param context
     * @return
     */
    public static String getPackageName(Context context) {
        String packName = context.getPackageName();
        return packName;
    }

    /**
     * 获取当前应用程序版本名称信息
     *
     * @param context
     * @return 当前应用的版本名称
     */
    public static String getVersionName(Context context) {
        PackageInfo info = getPackageInfo(context);
        return (info == null) ? "" : info.versionName;
    }

    /**
     * 根据包名获取应用的版本号
     *
     * @param context
     * @param packName
     * @return
     */
    public static String getVersionName(Context context, String packName) {
        PackageInfo info = getPackageInfo(context, packName);
        return (info == null) ? "" : info.versionName;
    }

    /***
     * 获取App版本号**
     *
     * @param context 上下文*
     * @return App版本号
     */
    public static String getAppVersionName(Context context) {
        return getAppVersionName(context, context.getPackageName());
    }

    /***
     * 获取App版本号** @param context     上下文*
     *
     * @param packageName 包名*
     * @return App版本号
     */
    public static String getAppVersionName(Context context, String packageName) {
        String versionName = "";
        if (!StringUtils.isSpace(packageName)) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(packageName, 0);
                versionName = (pi == null) ? null : pi.versionName;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return versionName;
    }

    /***
     * 获取App版本码**
     *
     * @param context 上下文*
     * @return App版本码
     */
    public static int getAppVersionCode(Context context) {
        return getAppVersionCode(context, context.getPackageName());
    }

    /***
     * 获取App版本码**
     *
     * @param context     上下文*
     * @param packageName 包名*
     * @return App版本码
     */
    public static int getAppVersionCode(Context context, String packageName) {
        int versionCode = -1;
        if (!StringUtils.isSpace(packageName)) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(packageName, 0);
                versionCode = (pi == null) ? -1 : pi.versionCode;
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return versionCode;
    }

    /***
     * 获取App签名**
     *
     * @param context 上下文*
     * @return App签名
     */
    public static Signature[] getAppSignature(Context context) {
        return getAppSignature(context, context.getPackageName());
    }

    /***
     * 获取App签名**
     *
     * @param context     上下文*
     * @param packageName 包名*
     * @return App签名
     */
    public static Signature[] getAppSignature(Context context, String packageName) {
        if (StringUtils.isSpace(packageName)) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.signatures;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据包名获取程序的签名
     */
    public static String getAppSignature2(Context context, String packName) {
        try {
            PackageInfo packInfo = getManager(context).getPackageInfo(packName, PackageManager.GET_SIGNATURES);
            //获取到所有的权限
            return packInfo.signatures[0].toCharsString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /***
     * 判断App是否是系统应用**
     *
     * @param context 上下文*
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isSystemApp(Context context) {
        return isSystemApp(context, context.getPackageName());
    }

    /***
     * 判断App是否是系统应用**
     *
     * @param context     上下文*
     * @param packageName 包名*
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isSystemApp(Context context, String packageName) {
        boolean isSysApp = false;
        if (!StringUtils.isSpace(packageName)) {
            try {
                PackageManager pm = context.getPackageManager();
                ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
                isSysApp = (ai != null) && ((ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
            } catch (NameNotFoundException e) {
                LogUtils.i(TAG, "isSystemApp --- e == " + e.toString());
                e.printStackTrace();
            }
        }
        LogUtils.i(TAG, "isSystemApp --- packageName == " + packageName + " , isSysApp == " + isSysApp);
        return isSysApp;
    }

    /**
     * 获取当前应用的ApplicationInfo
     *
     * @param context
     * @return
     */
    public static ApplicationInfo getApplicationInfo(Context context) {
        ApplicationInfo info = getApplicationInfo(context, getPackageName(context));
        return info;
    }

    /**
     * 根据包名获取应用的ApplicationInfo
     *
     * @param context
     * @param packName
     * @return
     */
    public static ApplicationInfo getApplicationInfo(Context context, String packName) {
        ApplicationInfo info = null;
        try {
            info = getManager(context).getApplicationInfo(packName, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * 获取当前应用程序的图标
     *
     * @param context
     * @return
     */
    public static Drawable getAppIcon(Context context) {
        Drawable drawable = getAppIcon(context, getPackageName(context));
        return drawable;
    }

    /**
     * 根据包名获取程序图标
     *
     * @param context
     * @param packName 应用程序包名
     * @return 图标
     */
    public static Drawable getAppIcon(Context context, String packName) {
        ApplicationInfo info = getApplicationInfo(context, packName);
        return (null == info) ? null : info.loadIcon(getManager(context));
    }

    /***
     * 获取App图标**
     *
     * @param context     上下文*
     * @param packageName 包名*
     * @return App图标
     */
    public static Drawable getAppIcon2(Context context, String packageName) {
        Drawable drawable = null;
        if (!StringUtils.isSpace(packageName)) {
            try {
                PackageManager pm = context.getPackageManager();
                PackageInfo pi = pm.getPackageInfo(packageName, 0);
                drawable = (pi == null) ? null : pi.applicationInfo.loadIcon(pm);
            } catch (NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return drawable;
    }

    /**
     * 根据包名获取程序的权限
     */
    public static String[] getAppPremission(Context context, String packName) {
        try {
            PackageInfo packInfo = getManager(context).getPackageInfo(packName, PackageManager.GET_PERMISSIONS);
            //获取到所有的权限
            return packInfo.requestedPermissions;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 获取ApiKey
    public static String getMetaValue(Context context, String metaKey) {
        Bundle metaData = null;
        String apiKey = null;
        if (context == null || metaKey == null) {
            return null;
        }
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (null != ai) {
                metaData = ai.metaData;
            }
            if (null != metaData) {
                apiKey = metaData.getString(metaKey);
            }
        } catch (NameNotFoundException e) {
        }
        return apiKey;
    }

    /*------------------------------退出应用程序---start--------------------------------*/

    //android.os.Process.killProcess(pid) 和 System.exit(int code)会导致进程非正常退出，
    // 进程退出时不会去执行Activity的onPause、onStop和onDestroy方法，那么进程很有可能错过了保存数据的机会。
    // 因此，这两个方法最好使用在出现异常的时候！大家需要注意其使用方法。

    /**
     * 退出程序
     *
     * @param param 0正常退出,退出JVM（java虚拟机）.!0异常退出
     */
    public static void exitApp(int param) {
        System.exit(param);
    }

    /**
     * 结束进程
     * android中所有的activity都在主进程中，在Androidmanifest.xml中可以设置成启动不同进程.
     * Service不是一个单独的进程也不是一个线程。
     * 当你Kill掉当前程序的进程时也就是说整个程序的所有线程都会结束，Service也会停止，整个程序完全退出。
     */
    public static void killApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 退出
     * 系统会将，该包下的 ，所有进程，服务，全部杀掉，就可以杀干净了，要注意加上
     * <uses-permission android:name=\"android.permission.RESTART_PACKAGES\"/>
     *
     * @param context
     */
    public static void exit(Context context, String parkName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.restartPackage(parkName);
    }

    /**
     * 需要在xml中加入权限声明
     * <uses-permission android:name="android.permission.KILL_BACKGROUND_PROCESSES"/>
     *
     * @param context
     * @param parkName
     */
    public static void killApp(Context context, String parkName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        manager.killBackgroundProcesses(parkName);
    }
    /*------------------------------退出应用程序---end-----------------------------------*/

    /***
     * 判断App是否处于前台*
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.GET_TASKS"/>}</p>
     * <p>并且必须是系统应用该方法才有效</p>**
     *
     * @param context 上下文*
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppForeground(Context context) {
        return isAppForeground(context, context.getPackageName());
    }

    /***
     * 判断App是否处于前台*
     * <p>需添加权限 {@code <uses-permission android:name="android.permission.GET_TASKS"/>}</p>*
     * <p>并且必须是系统应用该方法才有效</p>**
     *
     * @param context 上下文*
     * @return {@code true}: 是<br>{@code false}: 否
     */
    public static boolean isAppForeground(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        @SuppressWarnings("deprecation")
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        return !tasks.isEmpty() && tasks.get(0).topActivity.getPackageName().equals(packageName);
    }

    /***
     * 获取App信息*
     * <p>AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）</p>**
     *
     * @param context 上下文*
     * @return 当前应用的AppInfo
     */
    public static AppInfo getAppInfo(Context context) {
        return getAppInfo(context, context.getPackageName());
    }

    /***
     * 获取App信息*
     * <p>AppInfo（名称，图标，包名，版本号，版本Code，是否系统应用）</p>
     *
     * @param context 上下文*
     * @return 当前应用的AppInfo
     */
    public static AppInfo getAppInfo(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return getBean(pm, pi);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /***
     * 得到AppInfo的Bean**
     *
     * @param pm 包的管理*
     * @param pi 包的信息*
     * @return AppInfo类
     */
    private static AppInfo getBean(PackageManager pm, PackageInfo pi) {
        if (pm == null || pi == null) return null;
        ApplicationInfo ai = pi.applicationInfo;
        String name = ai.loadLabel(pm).toString();
        Drawable icon = ai.loadIcon(pm);
        String packageName = pi.packageName;
        String packagePath = ai.sourceDir;
        String versionName = pi.versionName;
        int versionCode = pi.versionCode;
        boolean isSystem = (ApplicationInfo.FLAG_SYSTEM & ai.flags) != 0;
        return new AppInfo(name, icon, packageName, packagePath, versionName, versionCode, isSystem);
    }

    /***
     * 获取所有已安装App信息* <p>{@link #getBean(PackageManager, PackageInfo)}（名称，图标，包名，包路径，版本号，版本Code，是否系统应用）</p>* <p>依赖上面的getBean方法</p>** @param context 上下文* @return 所有已安装的AppInfo列表
     */
    public static List<AppInfo> getAppsInfo(Context context) {
        List<AppInfo> list = new ArrayList<>();
        PackageManager pm = context.getPackageManager();
        // 获取系统中安装的所有软件信息
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo pi : installedPackages) {
            AppInfo ai = getBean(pm, pi);
            if (ai == null) continue;
            list.add(ai);
        }
        return list;
    }

    /***
     * 清除App所有数据** @param context  上下文* @param dirPaths 目录路径
     */
    public static boolean cleanAppData(Context context, String... dirPaths) {
        File[] dirs = new File[dirPaths.length];
        int i = 0;
        for (String dirPath : dirPaths) {
            dirs[i++] = new File(dirPath);
        }
        return cleanAppData(context, dirs);
    }

    /***
     * 清除App所有数据** @param context 上下文* @param dirs    目录
     */
    public static boolean cleanAppData(Context context, File... dirs) {
        boolean isSuccess = CleanUtils.cleanInternalCache(context);
        isSuccess &= CleanUtils.cleanInternalDbs(context);
        isSuccess &= CleanUtils.cleanInternalSP(context);
        isSuccess &= CleanUtils.cleanInternalFiles(context);
        isSuccess &= CleanUtils.cleanExternalCache(context);
        for (File dir : dirs) {
            isSuccess &= CleanUtils.cleanCustomCache(dir);
        }
        return isSuccess;
    }

    /***
     * 获取打开App的意图**
     *
     * @param context     上下文*
     * @param packageName 包名*
     * @return intent
     */
    public static Intent getLaunchAppIntent(Context context, String packageName) {
        return context.getPackageManager().getLaunchIntentForPackage(packageName);
    }

    /***
     * 获取安装App(支持6.0)的意图**
     *
     * @param file 文件*
     * @return intent
     */
    public static Intent getInstallAppIntent(File file) {
        if (file == null) return null;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String type;
        if (Build.VERSION.SDK_INT < 23) {
            type = "application/vnd.android.package-archive";
        } else {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtils.getFileExtension(file));
        }
        intent.setDataAndType(Uri.fromFile(file), type);
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /***
     * 获取卸载App的意图**
     *
     * @param packageName 包名*
     * @return intent
     */
    public static Intent getUninstallAppIntent(String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }

    /***
     * 获取App具体设置的意图**
     *
     * @param packageName 包名*
     * @return intent
     */
    public static Intent getAppDetailsSettingsIntent(String packageName) {
        Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
        intent.setData(Uri.parse("package:" + packageName));
        return intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    }
}
