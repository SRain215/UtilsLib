package com.srain.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * author: SRain
 * blog  : https://my.oschina.net/u/2320057/blog
 * time  : 2016/09/30
 * desc  : 文件管理类(待续)
 */
public class FileUtils {

    private static final String TAG = "FileUtil";
    private Context context;
    private String SRCPATH;
    private String THUMBPATH;
    private boolean hasSD = false;

    /**
     * 单例模式
     */
    public FileUtils(Context context) {
        this.context = context;
        // 判断是否含有SD卡
        hasSD = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        String appName = AppUtils.getAppName(context);
        String rootPath = hasSD ? (Environment.getExternalStorageDirectory().getPath() + File.separator + "Android" + File.separator + "data" + File.separator + appName + File.separator + "cache" + File.separator) : (this.context.getFilesDir().getPath() + File.separator + "Android" + File.separator + "data" + File.separator + appName + File.separator + "cache" + File.separator);
        THUMBPATH = rootPath + "thumb" + File.separator;
        SRCPATH = rootPath + "src" + File.separator;
        File src = new File(SRCPATH), thumb = new File(THUMBPATH);
        if (!src.exists()) {
            src.mkdirs();
        }
        Log.e(TAG, src.getPath());
        if (!thumb.exists()) {
            thumb.mkdirs();
        }
        Log.e(TAG, thumb.getPath());
    }

    private static FileUtils instance;

    public static FileUtils getInstance(Context context) {
        if (instance == null) {
            instance = new FileUtils(context);
        }
        return instance;
    }

    /**
     * 写文件到sdcard
     *
     * @param fileName
     * @param bm
     * @param isThumb
     * @return
     */
    public boolean writeFileToSD(String fileName, Bitmap bm, boolean isThumb) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);// png类型
        byte[] datas = baos.toByteArray();
        return writeFileToSD(fileName, datas, isThumb);
    }

    /**
     * 将文件写入SD卡
     */
    public boolean writeFileToSD(String fileName, byte[] datas, boolean isThumb) {
        try {
            File file = new File((isThumb ? THUMBPATH : SRCPATH) + fileName);
            Log.e(TAG, "writeFileToSD[]:" + file.getPath());
            if (!file.exists()) {
                file.createNewFile();// 创建文件
            }
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(datas);
            stream.close();

            return true;
        } catch (Exception e) {
            Log.e(TAG, "writeFileToSD[]:Error" + e.getMessage());
        }
        return false;
    }

    /**
     * 判断sdcard有没有文件或文件夹
     *
     * @param fileName
     * @return
     */
    public boolean existsSrc(String fileName) {
        boolean flag = new File(SRCPATH + fileName).exists();
        Log.e(TAG, "existsSrc[]:" + SRCPATH + fileName + ";result:" + flag);
        return flag;
    }


//    private static CustomProgress customProgress = null;

    public boolean clear(Context context) {
//        customProgress = CustomProgress.show(context, "loading...", false, null);
        File srcDir = new File(SRCPATH);

        String[] children = srcDir.list();
        for (int i = 0; i < children.length; i++) {
            if (!new File(srcDir, children[i]).delete()) {
                return false;
            }
        }
        File thumbDir = new File(THUMBPATH);
        children = thumbDir.list();
        for (int i = 0; i < children.length; i++) {
            if (!new File(thumbDir, children[i]).delete()) {
                return false;
            }
        }
//        customProgress.dismiss();
        return true;
    }

    public boolean existsThumb(String fileName) {
        boolean flag = new File(THUMBPATH + fileName).exists();
        Log.e(TAG, "existsThumb[]:" + THUMBPATH + fileName + ";result:" + flag);
        return flag;
    }

    /**
     * 读取文件
     */
    private Bitmap readSDFile(String fileName, boolean isThumb) {
        String pathName = (isThumb ? THUMBPATH : SRCPATH) + fileName;
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高，注意此处的bitmap为null
        bitmap = BitmapFactory.decodeFile(pathName, options);
        options.inJustDecodeBounds = false; // 设为 false

        // 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
        bitmap = BitmapFactory.decodeFile(pathName, options);
        // 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象

        return bitmap;
    }

    public Bitmap readSrcFile(String fileName) {
        return readSDFile(fileName, false);
    }

    public Bitmap readThumbFile(String fileName) {
        return readSDFile(fileName, true);
    }


    public String getSRCPATH() {
        return SRCPATH;
    }

    public String getTHUMBPATH() {
        return THUMBPATH;
    }

    /**
     * TODO
     * 获取缓存数据文件夹的路径,很简单但是知道的人不多,
     * 这个路径通常在SD卡上(这里的SD卡指的是广义上的SD卡,
     * 包括外部存储和内部存储)Adnroid/data/您的应用程序包名/cache/  下面.
     * 测试的时候,可以去这里面看是否缓存成功.缓存在这里的好处是:
     * 不用自己再去手动创建文件夹,
     * 不用担心用户把自己创建的文件夹删掉,
     * 在应用程序卸载的时候,这里会被清空,
     * 使用第三方的清理工具的时候,
     * 这里也会被清空.
     *
     * @param context
     * @return
     */
    public File getFile(Context context) {
        File file = context.getCacheDir();
        return file;
    }

    /**
     * 根据文件路径获取文件
     *
     * @param filePath 文件路径
     * @return 文件
     */
    public static File getFileByPath(String filePath) {
        return StringUtils.isSpace(filePath) ? null : new File(filePath);
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(String filePath) {
        return isFileExists(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在
     *
     * @param file 文件
     * @return {@code true}: 存在<br>{@code false}: 不存在
     */
    public static boolean isFileExists(File file) {
        return file != null && file.exists();
    }

    /**
     * 获取全路径中的文件拓展名
     *
     * @param file 文件
     * @return 文件拓展名
     */
    public static String getFileExtension(File file) {
        if (file == null) return null;
        return getFileExtension(file.getPath());
    }

    /**
     * 获取全路径中的文件拓展名
     *
     * @param filePath 文件路径
     * @return 文件拓展名
     */
    public static String getFileExtension(String filePath) {
        if (StringUtils.isSpace(filePath)) return filePath;
        int lastPoi = filePath.lastIndexOf('.');
        int lastSep = filePath.lastIndexOf(File.separator);
        if (lastPoi == -1 || lastSep >= lastPoi) return "";
        return filePath.substring(lastPoi + 1);
    }

    /**
     * 删除目录下的所有文件
     *
     * @param dirPath 目录路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFilesInDir(String dirPath) {
        return deleteFilesInDir(getFileByPath(dirPath));
    }

    /**
     * 删除目录下的所有文件
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFilesInDir(File dir) {
        if (dir == null) return false;
        // 目录不存在返回true
        if (!dir.exists()) return true;
        // 不是目录返回false
        if (!dir.isDirectory()) return false;
        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!deleteFile(file)) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false;
                }
            }
        }
        return true;
    }

    /**
     * 删除目录
     *
     * @param dirPath 目录路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteDir(String dirPath) {
        return deleteDir(getFileByPath(dirPath));
    }

    /**
     * 删除目录
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteDir(File dir) {
        if (dir == null) return false;
        // 目录不存在返回true
        if (!dir.exists()) return true;
        // 不是目录返回false
        if (!dir.isDirectory()) return false;
        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!deleteFile(file)) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false;
                }
            }
        }
        return dir.delete();
    }

    /**
     * 删除文件
     *
     * @param srcFilePath 文件路径
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(String srcFilePath) {
        return deleteFile(getFileByPath(srcFilePath));
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    /**
     * 关闭IO
     *
     * @param closeables closeable
     */
    public static void closeIO(Closeable... closeables) {
        if (closeables == null) return;
        try {
            for (Closeable closeable : closeables) {
                if (closeable != null) {
                    closeable.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*----------------------------待整理------------------------------------*/

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param filePath 文件路径
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    public static boolean createFileByDeleteOldFile(String filePath) {
        return createFileByDeleteOldFile(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在，存在则在创建之前删除
     *
     * @param file 文件
     * @return {@code true}: 创建成功<br>{@code false}: 创建失败
     */
    public static boolean createFileByDeleteOldFile(File file) {
        if (file == null) return false;
        // 文件存在并且删除失败返回false
        if (file.exists() && file.isFile() && !file.delete()) return false;
        // 创建目录失败返回false
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param dirPath 文件路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(String dirPath) {
        return createOrExistsDir(getFileByPath(dirPath));
    }

    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param filePath 文件路径
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(String filePath) {
        return createOrExistsFile(getFileByPath(filePath));
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(File file) {
        if (file == null) return false;
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 指定编码按行读取文件到字符串中
     *
     * @param filePath    文件路径
     * @param charsetName 编码格式
     * @return 字符串
     */
    public static String readFile2String(String filePath, String charsetName) {
        return readFile2String(getFileByPath(filePath), charsetName);
    }

    /**
     * 指定编码按行读取文件到字符串中
     *
     * @param file        文件
     * @param charsetName 编码格式
     * @return 字符串
     */
    public static String readFile2String(File file, String charsetName) {
        if (file == null) return null;
        BufferedReader reader = null;
        try {
            StringBuilder sb = new StringBuilder();
            if (StringUtils.isSpace(charsetName)) {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            } else {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charsetName));
            }
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\r\n");// windows系统换行为\r\n，Linux为\n
            }
            // 要去除最后的换行符
            return sb.delete(sb.length() - 2, sb.length()).toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            closeIO(reader);
        }
    }

    /**
     * 获取全路径中的文件名
     *
     * @param file 文件
     * @return 文件名
     */
    public static String getFileName(File file) {
        if (file == null) return null;
        return getFileName(file.getPath());
    }

    /**
     * 获取全路径中的文件名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    public static String getFileName(String filePath) {
        if (StringUtils.isSpace(filePath)) return filePath;
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? filePath : filePath.substring(lastSep + 1);
    }
}
