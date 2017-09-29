package com.srain.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by SRain on 2016/9/10 0010.
 * 用途：Bitmap处理工具类
 * <p/>
 * Bitmap知识总结：
 * Drawable是一个可画的对象，包括位图(BitmapDrawable)、图形(ShapeDrawable)、图层(LayerDrawable)。
 * Canvas画布，绘图的目标区域，用于绘图。
 * Bitmap位图，用于处理图。
 * Matrix矩阵
 * <p/>
 * 对比项  显示清晰度 占用内存 支持缩放 支持色相色差调整 支持旋转 支持透明色 绘制速度 支持像素操作
 * Bitmap   相同 大 是 是 是 是 慢 是
 * Drawable 相同 小 是 否 是 是 快 否
 */
public class BitmapUtils {

    private static final String TAG = BitmapUtils.class.getSimpleName();
    private Context mContext;
    private BitmapUtils bitmapUtils;

    public BitmapUtils getInstance(Context context) {
        if (bitmapUtils == null) {
            bitmapUtils = new BitmapUtils(context);
        }
        return bitmapUtils;
    }

    private BitmapUtils(Context context) {
        mContext = context;
    }

    /*-----------------------------------整理后-------------------------------*/

    /**
     * 根据资源id获取Bitmap
     *
     * @param context
     * @param resId   资源id
     * @return
     */
    public static Bitmap getBitmapForRes(Context context, int resId) {
        Resources res = context.getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(res, resId);
        return bitmap;
    }

    /**
     * Bitmap->byte[]
     *
     * @param bitmap
     * @return
     */
    public byte[] bitmap2Bytes(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    /**
     * byte[] → Bitmap
     *
     * @param bytes
     * @return
     */
    public Bitmap bytes2Bitmap(byte[] bytes) {
        Bitmap bitmap = null;
        if (bytes != null && bytes.length > 0) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return bitmap;
    }

    /**
     * 获取资源图片
     */
    public static Bitmap getBitmapForAsset(Context context, String filename) {
        Bitmap bitmap = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(filename);
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            LogUtils.e(TAG, "getBitmapForAsset" + e.toString());
        }
        return bitmap;
    }

    /**
     * 高斯模糊之RenderScript（推荐用法）
     *
     * @param sentBitmap
     * @param radius     模糊程度
     * @return
     */
    public static Bitmap bular(Context context, Bitmap sentBitmap, float radius) {
        if (Build.VERSION.SDK_INT > 16) {
            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);
            RenderScript rs = RenderScript.create(context);
            Allocation input = Allocation.createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            Allocation output = Allocation.createTyped(rs, input.getType());
            ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));

            script.setRadius(radius);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);

            sentBitmap.recycle();
            rs.destroy();
            input.destroy();
            output.destroy();
            script.destroy();

            return bitmap;
        }
        return null;
    }

    /**
     * 高斯模糊之FastBlur
     *
     * @param sentBitmap
     * @param radius     模糊程度
     * @return
     */
    public static Bitmap bular(Bitmap sentBitmap, int radius) {
        Bitmap bitmap = FastBlur.doBlur(sentBitmap, radius);
        return bitmap;
    }

    /**
     * 从SDCard中获取图片
     */
    public Bitmap getBitmapForSDCard(String filePath) {
        return BitmapFactory.decodeFile(filePath, null);
    }

    /**
     * 从SDCard中获取图片
     */
    public Bitmap getBitmapForSDCard2(String fileName) {
        InputStream inputStream = SDCardUtils.getInputStreamFromSDCard(fileName);
        return BitmapFactory.decodeStream(inputStream);
    }

    // 从sd卡上加载图片
    public static Bitmap decodeSampledBitmapFromSd(String pathName, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return createScaleBitmap(src, reqWidth, reqHeight);
    }

    /**
     * 压缩且保存图片到SDCard
     * Bitmap rawBitmap 需要保存的Bitmap
     * String fileName  保存的文件名
     * int quality      图像压缩比的值
     */
    private boolean saveCompressToSDcard(Bitmap rawBitmap, String fileName, int quality) {
        boolean isSaved = false;
        if (SDCardUtils.hasSDPath()) {
            String filePath = SDCardUtils.getSDCardPath() + File.separator + fileName;
            File saveFile = new File(filePath);
            if (!saveFile.exists()) {
                try {
                    saveFile.createNewFile();
                    FileOutputStream fileOutputStream = new FileOutputStream(saveFile);
                    if (fileOutputStream != null) {
                        /**
                         * 把位图的压缩信息写入到一个指定的输出流中
                         * @param format 压缩的格式
                         * @param quality 图像压缩比的值,0-100.0 意味着小尺寸压缩,100意味着高质量压缩,常用的是80
                         * @param stream      输出流
                         * 使用方法：imageBitmap.compress(format, quality, stream);
                         */
                        rawBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
                    }
                    fileOutputStream.flush();
                    fileOutputStream.close();
                    isSaved = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return isSaved;
    }

    //参考文档：http://blog.sina.com.cn/s/blog_5a6f39cf0101aqsw.html

    /**
     * end
     * 将Bitmap转换成Drawable
     *
     * @param bitmap
     */
    public static Drawable bitmap2Drawable(Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(bitmap);
        return drawable;
    }

    /**
     * TODO end
     * 将Drawable转换成Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawable2Bitmap(Drawable drawable) {
        // 取 drawable 的长宽
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        // 取 drawable 的颜色格式
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //建立对应 bitmap 的画布.若不使用,在View或者surfaceview里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap drawable2Bitmap2(Drawable drawable) {
        BitmapDrawable bd = (BitmapDrawable) drawable;
        return bd.getBitmap();
    }

    /**
     * 缩放图片
     *
     * @param bitmap
     * @param width
     * @param height
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) width / w);
        float scaleHeight = ((float) height / h);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        return newbmp;
    }

    //获得带倒影的图片
    public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
        final int reflectionGap = 4;
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix matrix = new Matrix();
        matrix.preScale(1, -1);

        Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, h / 2, w, h / 2, matrix, false);
        Bitmap bitmapWithReflection = Bitmap.createBitmap(w, (h + h / 2), Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapWithReflection);
        canvas.drawBitmap(bitmap, 0, 0, null);
        Paint deafalutPaint = new Paint();
        canvas.drawRect(0, h, w, h + reflectionGap, deafalutPaint);
        canvas.drawBitmap(reflectionImage, 0, h + reflectionGap, null);

        Paint paint = new Paint();
        LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
                bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
                0x00ffffff, Shader.TileMode.CLAMP);
        paint.setShader(shader);
        // Set the Transfer mode to be porter duff and destination in
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        // Draw a rectangle using the paint with our linear gradient
        canvas.drawRect(0, h, w, bitmapWithReflection.getHeight() + reflectionGap, paint);
        return bitmapWithReflection;
    }

    /**
     * end
     * 圆角的Bitmap
     *
     * @param bitmap 需要修改的图片
     * @param pixels 圆角的弧度
     * @param color  边的颜色
     * @return 圆角图片
     * 参考资料: http://blog.csdn.net/c8822882/article/details/6906768
     */
    public Bitmap toRoundCorner(Bitmap bitmap, int pixels, int color) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        Bitmap roundCornerBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(roundCornerBitmap);
        Paint paint = new Paint();
        paint.setColor(color); // final int color = 0xff424242;
        //防止锯齿
        paint.setAntiAlias(true);
        Rect rect = new Rect(0, 0, w, h);
        RectF rectF = new RectF(rect);
        float roundPx = pixels;
        //相当于清屏
        canvas.drawARGB(0, 0, 0, 0);
        //先画了一个带圆角的矩形
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        //再把原来的bitmap画到现在的bitmap！！！注意这个理解
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return roundCornerBitmap;
    }

    /**
     * end
     * Drawable缩放
     *
     * @param drawable
     * @param w
     * @param h
     * @return
     */
    public static Drawable zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        // drawable转换成bitmap
        Bitmap oldbmp = drawable2Bitmap(drawable);
        // 创建操作图片用的Matrix对象
        Matrix matrix = new Matrix();
        // 计算缩放比例
        float sx = ((float) w / width);
        float sy = ((float) h / height);
        // 设置缩放比例
        matrix.postScale(sx, sy);
        // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height, matrix, true);
        oldbmp.recycle();
        return new BitmapDrawable(newbmp);
    }

    /**
     * TODO 比较完美方法
     * view->bitmap
     *
     * @param view
     * @return
     */
    private Bitmap saveViewBitmap(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache(true);
        Bitmap bitmap = view.getDrawingCache(true);

        Bitmap bmp = duplicateBitmap(bitmap);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
        view.setDrawingCacheEnabled(false);
        return bmp;
    }

    /**
     * Bitmap->Bitmap
     *
     * @param bmpSrc
     * @return
     */
    public static Bitmap duplicateBitmap(Bitmap bmpSrc) {
        if (null != bmpSrc) {
            int bmpSrcWidth = bmpSrc.getWidth();
            int bmpSrcHeight = bmpSrc.getHeight();
            Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight, Bitmap.Config.ARGB_8888);
            if (null != bmpDest) {
                Canvas canvas = new Canvas(bmpDest);
                final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);
                canvas.drawBitmap(bmpSrc, rect, rect, null);
            }
            return bmpDest;
        } else {
            return null;
        }
    }

    //view->bitmap
    private Bitmap viewCanvas2Bitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        //利用bitmap生成画布
        Canvas canvas = new Canvas(bitmap);
        //把view中的内容绘制在画布上
        view.draw(canvas);
        return bitmap;
    }

    //一般情况下，这个方法能够正常的工作。但有时候，生成Bitmap会出现问题(Bitmap全黑色)。主要原因是drawingCache的值大于系统给定的值。
    private static Bitmap convertViewToBitmap(View view) {
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * end
     * View->Bitmap
     * 完美的解决方案
     *
     * @param view
     * @return
     */
    public static Bitmap view2Bitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * @param path
     * @return
     * @Description: 处理Bitmap
     */
    public static Bitmap disposeBitmap(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = getOptions(path); // 图片宽高都为原来的二分之一，即图片为原来的四分之一
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * @param path
     * @return
     * @Description: 获取bitmap转换值
     */
    private static int getOptions(String path) {
        Bitmap sentBitmap = BitmapFactory.decodeFile(path);
        int options = 1;
        if (sentBitmap != null) {
            int count = sentBitmap.getAllocationByteCount();
            if (count / (1024 * 1024 * 10) > 0) { // 文件大于10M
                options = 8;
            } else if (count / (1024 * 1024) > 0) { // 文件大于1M
                options = 4;
            } else if (count / (1024 * 1024 / 2) > 0) { // 文件大于500K
                options = 2;
            }
        }
        return options;
    }

    /*-----------------------------------整理前-------------------------------*/
    private static BitmapFactory.Options setBitmapOption(Context mContext, int resId, int width, int height) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        // 设置只是解码图片的边距，此操作目的是度量图片的实际宽度和高度
        InputStream is = mContext.getResources().openRawResource(resId);
        BitmapFactory.decodeStream(is, null, opt);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int outWidth = opt.outWidth; // 获得图片的实际高和宽
        int outHeight = opt.outHeight;
        opt.inDither = false;
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        // 设置加载图片的颜色数为16bit，默认是RGB_8888，表示24bit颜色和透明通道，但一般用不上
        opt.inSampleSize = 1;
        // 设置缩放比,1表示原比例，2表示原来的四分之一....
        // 计算缩放比
        if (outWidth != 0 && outHeight != 0 && width != 0 && height != 0) {
            int sampleSize = (outWidth / width + outHeight / height) / 2;
            opt.inSampleSize = sampleSize;
        }

        opt.inJustDecodeBounds = false;// 最后把标志复原
        // 设置图片的DPI为当前手机的屏幕dpi
        opt.inTargetDensity = mContext.getResources().getDisplayMetrics().densityDpi;
        opt.inScaled = true;
        return opt;
    }

    public static Bitmap readBitmapAutoSize(Context mContext, int resId, int outWidth, int outHeight) {
        // outWidth和outHeight是目标图片的最大宽度和高度，用作限制
        InputStream fs = null;
        BufferedInputStream bs = null;
        try {
            fs = mContext.getResources().openRawResource(resId);
            bs = new BufferedInputStream(fs);
            BitmapFactory.Options options = setBitmapOption(mContext, resId, outWidth, outHeight);
            return BitmapFactory.decodeStream(bs, null, options);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                bs.close();
                fs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 资源释放
     *
     * @param imageView
     */
    public static void recycle(ImageView imageView) {
        if (imageView != null && imageView.getDrawable() != null) {
            Bitmap oldBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            imageView.setImageResource(0);
            if (oldBitmap != null) {
                oldBitmap.recycle();
                oldBitmap = null;
            }
        }
        System.gc();// Other code.
    }

    /**
     * 把图片转换为字节
     *
     * @param mContext
     * @param id
     * @return
     */
    public static byte[] img(Context mContext, int id) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) mContext.getResources().getDrawable(id)).getBitmap();
        if (bitmap != null && bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)) {
            // bitmap.recycle();
        }
        byte[] results = baos.toByteArray();
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return results;
    }

    public LruCache<String, Bitmap> getmMemoryCache() {
        if (mMemoryCache == null) {
            // 获取到可用内存的最大值，使用内存超出这个值会引起OutOfMemory异常。
            // LruCache通过构造函数传入缓存值，以KB为单位。
            int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
            // 使用最大可用内存值的1/8作为缓存的大小。
            int cacheSize = maxMemory / 8;
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    // 重写此方法来衡量每张图片的大小，默认返回图片数量。
                    return bitmap.getByteCount() / 1024;
                }
            };
        }
        return mMemoryCache;
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        Bitmap bt = getBitmapFromMemCache(key);
        if (bt == null) {
            mMemoryCache.put(key, bitmap);
        } else {
            mMemoryCache.remove(key);
            mMemoryCache.put(key, bitmap);
            System.gc();
        }
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return mMemoryCache.get(key);
    }

    /* 图片缓存 */
    private static LruCache<String, Bitmap> mMemoryCache;
    public static final String CURR_WALLPAPER = "curr_wallpaper";
    public static final String CURR_FUZZY_WALLPAPER = "curr_fuzzy_wallpaper";

    /**
     * 压缩后图片
     */
    public Bitmap BitmapCompression(Bitmap rawBitmap, float newHeight, float newWidth) {
        // 得到原图的高度和宽度
        float rawHeight = rawBitmap.getHeight();
        float rawWidth = rawBitmap.getWidth();
        // 计算缩放因子
        float heightScale = ((float) newHeight) / rawHeight;
        float widthScale = ((float) newWidth) / rawWidth;
        // 新建立矩阵
        Matrix matrix = new Matrix();
        matrix.postScale(heightScale, widthScale);

        // 设置图片的旋转角度
        //matrix.postRotate(-30);
        // 设置图片的倾斜
        //matrix.postSkew(0.1f, 0.1f);

        //压缩后图片的宽和高以及KB大小均会变化
        //TODO 构造参数不对
//        Bitmap bitmap = Bitmap.createBitmap(rawBitmap, 0, 0, rawWidth, rawWidth, matrix, true);
        Bitmap bitmap = null;
        return bitmap;
    }

    //问题:
    //原图大小为625x690 90.2kB
    //如果设置图片500x500 压缩后大小为171kB.即压缩后kB反而变大了.
    //原因是将:compress(Bitmap.CompressFormat.JPEG, quality, fileOutputStream);
    //第二个参数quality设置得有些大了(比如100).
    //常用的是80,刚设100太大了造成的.
    //————>以上为将图片高宽和的大小kB压缩

    /**
     * 获取网络图片
     *
     * @param imgUrl 网络图片的地
     * @param im     网络图片
     */
    public static synchronized void setBitmap(final URL imgUrl, final ImageView im) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                Bitmap bitmap = data.getParcelable("bitmap");

//                    int width, height;
//                    height = bitmap.getHeight();
//                    width = bitmap.getWidth();
//
//                    Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
//                    Canvas c = new Canvas(bmpGrayscale);
//                    Paint paint = new Paint();
//                    ColorMatrix cm = new ColorMatrix();
//                    cm.setSaturation(0);
//                    ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
//                    paint.setColorFilter(f);
//                    c.drawBitmap(bitmap, 0, 0, paint);

                if (bitmap != null && !bitmap.isRecycled()) {
                    im.setImageBitmap(bitmap);
                }
//					bitmap.recycle();
                super.handleMessage(msg);
            }
        };
        // 将文件名MD5加密
        final String umd5 = MD5Utils.MD5(imgUrl.toString());

        final FileUtils fu = FileUtils.getInstance(null);

        if (fu.existsThumb(umd5)) {
            Message msg = new Message();
            Bundle data = new Bundle();
            Bitmap thumb = fu.readThumbFile(umd5);
            data.putParcelable("bitmap", thumb);
            msg.setData(data);
            handler.sendMessage(msg);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(imgUrl.openStream());
                        fu.writeFileToSD(umd5, bitmap, true);
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putParcelable("bitmap", bitmap);
                        msg.setData(data);
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                    }
                }
            }).start();
        }
    }

    /***
     * 设置网路图片为缩略图
     */
    public static void setThumbBitmap(final URL imgUrl, final ImageView im) {
        setThumbBitmap(imgUrl, im, 100, 100);
    }

    /**
     * 缩略图
     */
    public static synchronized void setThumbBitmap(final URL imgUrl, final ImageView im, final int height, final int width) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                Bitmap thumb = data.getParcelable("thumb");
                if (thumb != null && !thumb.isRecycled()) {
                    im.setImageBitmap(thumb);
                }
                super.handleMessage(msg);
            }
        };
        // 将文件名MD5加密
        final String umd5 = MD5Utils.MD5(imgUrl.toString());

        final FileUtils fu = FileUtils.getInstance(null);

        if (fu.existsThumb(umd5)) {
            Message msg = new Message();
            Bundle data = new Bundle();
            Bitmap thumb = fu.readThumbFile(umd5);
            data.putParcelable("thumb", thumb);
            msg.setData(data);
            handler.sendMessage(msg);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(imgUrl.openStream());
                        Bitmap thumb = ThumbnailUtils.extractThumbnail(bitmap, width, height);
                        fu.writeFileToSD(umd5, thumb, true);
                        bitmap.recycle();
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putParcelable("thumb", thumb);
                        msg.setData(data);
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                    }
                }
            }).start();
        }
    }

    /**
     * 获取图片的缩略图
     * String filePath 图片路径
     */
    private Bitmap getBitmapThumbnail(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        //true那么将不返回实际的bitmap对象,不给其分配内存空间但是可以得到一些解码边界信息即图片大小等信息
        options.inJustDecodeBounds = true;
        //此时rawBitmap为null
        Bitmap rawBitmap = BitmapFactory.decodeFile(filePath, options);
        if (rawBitmap == null) {
            System.out.println("此时rawBitmap为null");
        }
        //inSampleSize表示缩略图大小为原始图片大小的几分之一,若该值为3
        //则取出的缩略图的宽和高都是原始图片的1/3,图片大小就为原始大小的1/9
        //计算sampleSize
        int sampleSize = computeSampleSize(options, 150, 200 * 200);
        //为了读到图片,必须把options.inJustDecodeBounds设回false
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        //原图大小为625x690 90.2kB
        //测试调用computeSampleSize(options, 100, 200*100);
        //得到sampleSize=8
        //得到宽和高位79和87
        //79*8=632 87*8=696
        Bitmap thumbnailBitmap = BitmapFactory.decodeFile(filePath, options);
        //保存到SD卡方便比较
        this.saveCompressToSDcard(thumbnailBitmap, "15.jpg", 80);
        return thumbnailBitmap;
    }

    /**
     * 获取图片缩略图方法2
     * String filePath 图片路径
     */
    public Bitmap getBitmapThumbnail2(String filePath, int newHeight, int newWidth) {
        //String SDCarePath2=Environment.getExternalStorageDirectory().toString();
        //String filePath2=SDCarePath2+"/"+"haha.jpg";
        Bitmap tempBitmap = BitmapFactory.decodeFile(filePath);
        return ThumbnailUtils.extractThumbnail(tempBitmap, newHeight, newWidth);
    }


    /**
     * 设置图片灰度
     */
    public static synchronized void setGreyBitmap(final URL imgUrl, final ImageView im) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Bundle data = msg.getData();
                Bitmap bitmap = data.getParcelable("bitmap");

                int width, height;
                height = bitmap.getHeight();
                width = bitmap.getWidth();

                Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                Canvas c = new Canvas(bmpGrayscale);
                Paint paint = new Paint();
                ColorMatrix cm = new ColorMatrix();
                cm.setSaturation(0);
                ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
                paint.setColorFilter(f);
                c.drawBitmap(bitmap, 0, 0, paint);

                if (bmpGrayscale != null && !bmpGrayscale.isRecycled()) {
                    im.setImageBitmap(bmpGrayscale);
                }
//					bitmap.recycle();
                super.handleMessage(msg);
            }
        };
        // 将文件名MD5加密
        final String umd5 = MD5Utils.MD5(imgUrl.toString());

        final FileUtils fu = FileUtils.getInstance(null);

        if (fu.existsThumb(umd5)) {
            Message msg = new Message();
            Bundle data = new Bundle();
            Bitmap thumb = fu.readThumbFile(umd5);
            data.putParcelable("bitmap", thumb);
            msg.setData(data);
            handler.sendMessage(msg);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(imgUrl.openStream());
                        fu.writeFileToSD(umd5, bitmap, true);
                        Message msg = new Message();
                        Bundle data = new Bundle();
                        data.putParcelable("bitmap", bitmap);
                        msg.setData(data);
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                    }
                }
            }).start();
        }
    }


    //参考资料：
    //http://my.csdn.net/zljk000/code/detail/18212
    //第一个参数:原本Bitmap的options
    //第二个参数:希望生成的缩略图的宽高中的较小的值
    //第三个参数:希望生成的缩量图的总像素
    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return roundedSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        //原始图片的宽
        double w = options.outWidth;
        //原始图片的高
        double h = options.outHeight;
        System.out.println("========== w=" + w + ",h=" + h);
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    private static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
    private static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) {   // 如果没有缩放，那么不回收
            src.recycle();  // 释放Bitmap的native像素数组
        }
        return dst;
    }

    // 从Resources中加载图片
    public static Bitmap decodeSampledBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(res, resId, options); // 读取图片长款
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight); // 计算inSampleSize
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeResource(res, resId, options); // 载入一个稍大的缩略图
        return createScaleBitmap(src, reqWidth, reqHeight); // 进一步得到目标大小的缩略图
    }
}
