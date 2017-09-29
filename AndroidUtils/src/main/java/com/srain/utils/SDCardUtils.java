package com.srain.utils;

import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * SD卡相关的辅助类
 * <p/>
 * <!-- SDCard中创建与删除文件权限 -->
 * <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"/>
 * <!-- 向SDCard写入数据权限 -->
 * <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
 * <p/>
 * Environment类下面的几个静态方法  ：
 * 1:getDataDirectory() 获取到Android中的data数据目录（sd卡中的data文件夹）
 * 2:getDownloadCacheDirectory() 获取到下载的缓存目录（sd卡中的download文件夹）
 * 3:getExternalStorageDirectory() 获取到外部存储的目录 一般指SDcard（/storage/sdcard0）
 * 4:getExternalStorageState() 获取外部设置的当前状态 一般指SDcard，比较常用的应该是 MEDIA_MOUNTED（SDcard存在并且可以进行读写）还有其他的一些状态，可以在文档中进行查找。
 * 5:getRootDirectory()  获取到Android Root路径
 * <p/>
 * 参考文档：http://blog.csdn.net/mad1989/article/details/37568667
 * http://www.jb51.net/article/61038.htm
 * <p/>
 * @author SRain
 */
public class SDCardUtils {

    private static final String TAG = SDCardUtils.class.getSimpleName();

    private SDCardUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断SDCard是否存在[当没有外挂SD卡时，内置ROM也被识别为存在sd卡]
     *
     * @return
     */
    public static boolean isSDCardEnable() {
        boolean isEnable = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        LogUtils.i(TAG, "isEnable == " + isEnable);
        return isEnable;
    }

    /**
     * TODO 继续跟踪
     * 获取SD卡路径
     *
     * @return
     */
    public static String getSDCardPath() {
        String path = "";
        if (isSDCardEnable()) {
            path = Environment.getExternalStorageDirectory().toString();
//            path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        }
        LogUtils.i(TAG, "SDCardPath == " + path);
        return path;
    }

    public static String getAbsolutePath() {
        String path = "";
        if (isSDCardEnable()) {
//            path = Environment.getExternalStorageDirectory().toString();
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        }
        LogUtils.i(TAG, "getAbsolutePath == " + path);
        return path;
    }

    /**
     * TODO 获取SD卡公共路径
     *
     * @param type
     * @return
     */
    public File getPublicPath(String type) {
        File file = Environment.getExternalStoragePublicDirectory(type);
        return file;
    }

    /**
     * 获取默认的文件路径
     *
     * @return
     */
    public static String getDefaultFilePath() {
        String filepath = "";
        File file = new File(Environment.getExternalStorageDirectory(), "abc.txt");
        if (file.exists()) {
            filepath = file.getAbsolutePath();
        } else {
            filepath = "不适用";
        }
        return filepath;
    }

    /**
     * 获取系统存储路径
     *
     * @return
     */
    public static String getRootDirectoryPath() {
        return Environment.getRootDirectory().getAbsolutePath();
    }

    /**
     * 判断SDCard的根路径是否可以使用
     *
     * @return
     */
    public static boolean hasSDPath() {
        String path = getSDCardPath();
        boolean has = !isSDCardEnable() ? false : !TextUtils.isEmpty(path);
        return has;
    }

    /**
     * 获取SD卡的剩余容量 单位byte
     *
     * @return
     */
    public static long getSDCardAllSize() {
        long size = 0;
        if (isSDCardEnable()) {
            StatFs stat = new StatFs(getSDCardPath());
            // 获取空闲的数据块的数量
            long availableBlocks = (long) stat.getAvailableBlocks() - 4;
            // 获取单个数据块的大小（byte）
            long freeBlocks = stat.getAvailableBlocks();
            size = freeBlocks * availableBlocks;
        }
        return size;
    }

    /**
     * 获取指定路径所在空间的剩余可用容量字节数，单位byte
     *
     * @param filePath
     * @return 容量字节 SDCard可用空间，内部存储可用空间
     */
    public static long getFreeBytes(String filePath) {
        // 如果是sd卡的下的路径，则获取sd卡可用容量
        if (filePath.startsWith(getSDCardPath())) {
            filePath = getSDCardPath();
        } else {
            // 如果是内部存储的路径，则获取内存存储的可用容量
            filePath = Environment.getDataDirectory().getAbsolutePath();
        }
        StatFs stat = new StatFs(filePath);
        long availableBlocks = (long) stat.getAvailableBlocks() - 4;
        return stat.getBlockSize() * availableBlocks;
    }

    /**
     * 获取SDCard上图片的流媒体
     * String filePath 图片路径
     * return
     */
    public static InputStream getInputStreamFromSDCard(String fileName) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String SDCarePath = Environment.getExternalStorageDirectory().toString();
            String filePath = SDCarePath + File.separator + fileName;
            File file = new File(filePath);
            try {
                FileInputStream fileInputStream = new FileInputStream(file);
                return fileInputStream;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
