package com.srain.utils;

import android.text.TextUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private final static String TAG = "StringUtils";
    private final static NumberFormat numberFormat = NumberFormat.getNumberInstance();

    public StringUtils() {
         /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    static {
        numberFormat.setMaximumIntegerDigits(9);
        numberFormat.setMaximumFractionDigits(2);
        numberFormat.setMinimumFractionDigits(2);
    }

    private final static ThreadLocal<SimpleDateFormat> dateFormater = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
    };

    private final static ThreadLocal<SimpleDateFormat> dateFormater2 = new ThreadLocal<SimpleDateFormat>() {
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd");
        }
    };

    /**
     * 将字符串转位日期类型
     *
     * @param sdate
     * @return
     */
    public static Date toDate(String sdate) {
        try {
            return dateFormater.get().parse(sdate);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 以友好的方式显示时间
     *
     * @param sdate
     * @return
     */
    public static String friendly_time(String sdate) {
        Date time = toDate(sdate);
        if (time == null) {
            return "Unknown";
        }
        String ftime = "";
        Calendar cal = Calendar.getInstance();

        // 判断是否是同一天
        String curDate = dateFormater2.get().format(cal.getTime());
        String paramDate = dateFormater2.get().format(time);
        if (curDate.equals(paramDate)) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
            return ftime;
        }

        long lt = time.getTime() / 86400000;
        long ct = cal.getTimeInMillis() / 86400000;
        int days = (int) (ct - lt);
        if (days == 0) {
            int hour = (int) ((cal.getTimeInMillis() - time.getTime()) / 3600000);
            if (hour == 0) {
                ftime = Math.max((cal.getTimeInMillis() - time.getTime()) / 60000, 1) + "分钟前";
            } else {
                ftime = hour + "小时前";
            }
        } else if (days == 1) {
            ftime = "昨天";
        } else if (days == 2) {
            ftime = "前天";
        } else if (days > 2 && days <= 10) {
            ftime = days + "天前";
        } else if (days > 10) {
            ftime = dateFormater2.get().format(time);
        }
        return ftime;
    }

    /**
     * 判断给定字符串时间是否为今日
     *
     * @param sdate
     * @return boolean
     */
    public static boolean isToday(String sdate) {
        boolean b = false;
        Date time = toDate(sdate);
        Date today = new Date();
        if (time != null) {
            String nowDate = dateFormater2.get().format(today);
            String timeDate = dateFormater2.get().format(time);
            if (nowDate.equals(timeDate)) {
                b = true;
            }
        }
        return b;
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * URL解码
     *
     * @param str
     * @return
     */
    public static String decodeURL(String str) {
        return URLDecoder.decode(str);
    }

    public static String enCodeRUL(String str) {
        try {
            return URLEncoder.encode(str, "UTF-8");
        } catch (Exception ex) {
        }
        return str;
    }

    /**
     * 去掉时间为00:00:00
     */
    public static String replaceTimeZero(String date) {
        if (date != null) {
            if (date.indexOf("00:00:00") > 0) {
                date = date.replaceAll("00:00:00", "");
            } else if (date.indexOf(":00") == 16) {
                date = date.substring(0, 16);
            }
        }
        return date;
    }

    public static boolean startWithHttp(Object str) {
        return str != null && str.toString().toLowerCase().startsWith("http://");
    }

    /**
     * 字符串截取 防止出现半个汉字
     */
    public static String truncate(String str, int byteLength) {
        if (str == null) {
            return null;
        }
        if (str.length() == 0) {
            return str;
        }
        if (byteLength < 0) {
            throw new IllegalArgumentException("Parameter byteLength must be great than 0");
        }
        int i = 0;
        int len = 0;
        int leng = 0;
        char[] chs = str.toCharArray();
        try {
            leng = str.getBytes("gbk").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (leng <= byteLength)
            return str;
        while ((len < byteLength) && (i < leng)) {
            len = (chs[i++] > 0xff) ? (len + 2) : (len + 1);
        }

        if (len > byteLength) {
            i--;
        }
        return new String(chs, 0, i) + "...";
    }

    /**
     * 分割keyword 按最后一个出现的@分割
     *
     * @param data
     * @return keyword
     */
    public static String splitKeyWord(String data) {
        if (data == null || data.length() == 0)
            return null;
        if (data.lastIndexOf("@") == -1)
            return data;
        return data.substring(0, data.lastIndexOf("@"));
    }

    /**
     * @param date (时间戳)
     * @return 年－月－日 (2013-03-01)
     */
    public static String convertDate(String date) {
        try {
            if (date == null || "".equals(date))
                return "";
            if (isNumeric(date))
                return computingTime(Long.parseLong(date));
            else
                return date;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 确定是否是时间戳
     *
     * @param str
     * @return
     */
    private static boolean isNumeric(String str) {
        boolean isCurNum = false;
        if (!TextUtils.isEmpty(str) && ((str.length() == 10) || (str.length() == 13))) {
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(str);
            if (isNum.matches()) {
                isCurNum = true;
            }
        }
        return isCurNum;
    }

    /**
     * 计算时间1-59分钟前，
     *
     * @param date
     * @return
     */
    private static String computingTime(Long date) {
        if (date < 10000)
            return "";
        long currentTime = System.currentTimeMillis();
        float i = ((currentTime - date) / 3600 / 1000);
        if (i < 1) {
            int time = (int) Math.ceil(i * 60);
            return time + 1 + "分钟前";
        } else if (i < 24) {
            return (int) i + "小时前";
        } else if (i < 48)
            return "昨天";
        return toNYR(date);
    }

    /**
     * 截取年月日 如（2013-01-08）
     *
     * @param data
     * @return yyyy-MM-dd
     */
    private static String toNYR(long data) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-M-d");
        try {
            return dateFormat.format(data);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 抽正文标题
     *
     * @param str
     * @return
     */
    public static String setReadabilityTitle(String str) {
        String res = null;
        if (str != null) {
            if (str.length() > 9) {
                res = str.substring(0, 3) + "..." + str.substring(str.length() - 3, str.length());
            }
        }
        return res == null ? str : res;
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * 将list中的字符串用split间隔开
     *
     * @param list
     * @param split
     * @return
     */
    public static String Join(List<String> list, String split) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
            if (i + 1 != list.size()) {
                sb.append(split);
            }
        }
        return sb.toString();
    }

    @Deprecated
    public static String toPriceStr(double price) {
        return "￥" + numberFormat.format(price);
    }

    public static String toDateString(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
    }


    /**
     * 流转字符串方法
     *
     * @param is
     * @return
     */
    public static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * 判断字符串是否为null或全为空格
     *
     * @param s 待校验字符串
     * @return {@code true}: null或全空格<br> {@code false}: 不为null且不全空格
     */
    public static boolean isSpace(String s) {
        return (TextUtils.isEmpty(s) || s.trim().length() == 0);
    }

    /**
     * 字符串转换成十六进制字符串
     *
     * @param str 待转换的ASCII字符串
     * @return String 每个Byte之间空格分隔，如: [61 6C 6B]
     */
    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;

        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
            sb.append(' ');
        }
        return sb.toString().trim();
    }

    /**
     * 十六进制转换字符串
     *
     * @param hexStr Byte字符串(Byte之间无分隔符 如:[616C6B])
     * @return 对应的字符串
     */
    public static String hexStr2Str(String hexStr) {
        String str = "0123456789ABCDEF";
        char[] hexs = hexStr.toCharArray();
        byte[] bytes = new byte[hexStr.length() / 2];
        int n;

        for (int i = 0; i < bytes.length; i++) {
            n = str.indexOf(hexs[2 * i]) * 16;
            n += str.indexOf(hexs[2 * i + 1]);
            bytes[i] = (byte) (n & 0xff);
        }
        String toStr = new String(bytes);
        LogUtils.i(TAG, "hexStr2Str --- hexStr == " + hexStr + " , toStr == " + toStr);
        return toStr;
    }

    /**
     * bytes转换成十六进制字符串
     *
     * @param b byte数组
     * @return 每个Byte值之间空格分隔
     */
    public static String byte2HexStr(byte[] b) {
        String stmp = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length() == 1) ? "0" + stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }

    /**
     * bytes字符串转换为Byte值
     *
     * @param src Byte字符串，每个Byte之间没有分隔符
     * @return
     */
    public static byte[] hexStr2Bytes(String src) {
        int m = 0, n = 0;
        int l = src.length() / 2;
        System.out.println(l);
        byte[] ret = new byte[l];
        for (int i = 0; i < l; i++) {
            m = i * 2 + 1;
            n = m + 1;
            ret[i] = Byte.decode("0x" + src.substring(i * 2, m) + src.substring(m, n));
        }
        return ret;
    }

    public static byte[] hex2byte(String hex) {
        String digital = "0123456789ABCDEF";
        char[] hex2char = hex.toCharArray();
        byte[] bytes = new byte[hex.length() / 2];
        int temp;
        for (int i = 0; i < bytes.length; i++) {
            temp = digital.indexOf(hex2char[2 * i]) * 16;
            temp += digital.indexOf(hex2char[2 * i + 1]);
            bytes[i] = (byte) (temp & 0xff);
        }
        return bytes;
    }

    public static byte[] hex2Bytes(String hexString) {
        if (TextUtils.isEmpty(hexString)) {
            return null;
        }
        hexString = hexString.toLowerCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] bytes = new byte[length];
        String hexDigits = "0123456789abcdef";
        for (int i = 0; i < length; i++) {
            int pos = i * 2; // 两个字符对应一个byte
            int h = hexDigits.indexOf(hexChars[pos]) << 4; // 注1
            int l = hexDigits.indexOf(hexChars[pos + 1]); // 注2
            if (-1 == h || l == -1) { // 非16进制字符
                return null;
            }
            bytes[i] = (byte) (h | l);
            LogUtils.i(TAG, " bytes[i] == " + Integer.toHexString(bytes[i] & 0xFF) + ", i == " + i);
        }
        return bytes;
    }

    /**
     * String的字符串转换成unicode的String
     *
     * @param strText 全角字符串
     * @return 每个unicode之间无分隔符
     * @throws Exception
     */
    public static String strToUnicode(String strText) throws Exception {
        char c;
        StringBuilder str = new StringBuilder();
        int intAsc;
        String strHex;
        for (int i = 0; i < strText.length(); i++) {
            c = strText.charAt(i);
            intAsc = (int) c;
            strHex = Integer.toHexString(intAsc);
            if (intAsc > 128)
                str.append("\\u" + strHex);
            else // 低位在前面补00    
                str.append("\\u00" + strHex);
        }
        return str.toString();
    }

    /**
     * unicode的String转换成String的字符串
     *
     * @param hex hex 16进制值字符串 （一个unicode为2byte）
     * @return
     */
    public static String unicodeToString(String hex) {
        int t = hex.length() / 6;
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < t; i++) {
            String s = hex.substring(i * 6, (i + 1) * 6);
            // 高位需要补上00再转    
            String s1 = s.substring(2, 4) + "00";
            // 低位直接转    
            String s2 = s.substring(4);
            // 将16进制的string转为int    
            int n = Integer.valueOf(s1, 16) + Integer.valueOf(s2, 16);
            // 将int转换为字符    
            char[] chars = Character.toChars(n);
            str.append(new String(chars));
        }
        return str.toString();
    }

    /**
     * 是否是数字
     *
     * @param txt
     * @return
     */
    public static boolean isNumber(String txt) {
        boolean isNum = isStringType(1, txt);
        return isNum;
    }

    /**
     * 特殊字符判断
     *
     * @param type
     * @param txt
     * @return
     */
    private static boolean isStringType(int type, String txt) {
        boolean isNum = false;
        Pattern p = Pattern.compile("[0-9]*");
        switch (type) {
            case 1: // 数字
                p = Pattern.compile("[0-9]*");
                break;
            case 2: // 字母
                p = Pattern.compile("[a-zA-Z]");
                break;
            case 3: // 汉字
                p = Pattern.compile("[\u4e00-\u9fa5]");
                break;
        }

        Matcher m = p.matcher(txt);
        if (m.matches()) {
            isNum = true;
        }
        return isNum;
    }

    public static SimpleDateFormat infoFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //24小时制
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");  // 转换成年、月、日
    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss"); // 转换成时间


    /**
     * 轉成10位的時間戳
     *
     * @param time
     * @return
     */
    public static long toTenNumeric(long time) {
        String strTime = String.valueOf(time);
        if (!TextUtils.isEmpty(strTime) && (strTime.length() == 13)) {
            time = time / 1000;
        }
        return time;
    }

    /**
     * long类型转时间字符串
     *
     * @param time
     * @return
     */
    public static String numericToStr(long time) {
        time = (String.valueOf(time).length() == 10) ? time * 1000 : time;
        return infoFormat.format(new Date(time));
    }

    /**
     * 时间戳转时间
     *
     * @param s
     * @return
     */
    public static String numericToStr(String s) {
        if (isNumeric(s)) {
            long cur = Long.valueOf(s);
            return infoFormat.format(new Date((s.length() == 10) ? cur * 1000 : cur));
        }
        return s;
    }

    /**
     * 时间戳转日期
     *
     * @param s
     * @return
     */
    public static String numericToDate(String s) {
        if (isNumeric(s)) {
            long cur = Long.valueOf(s);
            return dateFormat.format(new Date((s.length() == 10) ? cur * 1000 : cur));
        }
        return s;
    }

    /**
     * 计算time2减去time1的差值 差值只设置 几天 几个小时 或 几分钟
     * 根据差值返回多长之间前或多长时间后
     *
     * @param time1
     * @param time2
     * @return
     */
    public static String getDistanceTime(long time1, long time2) {
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        long diff;
        String flag;
        if (time1 < time2) {
            diff = time2 - time1;
            flag = "前";
        } else {
            diff = time1 - time2;
            flag = "后";
        }
        day = diff / (24 * 60 * 60 * 1000);
        hour = (diff / (60 * 60 * 1000) - day * 24);
        min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
        sec = (diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        if (day != 0) return day + "天" + flag;
        if (hour != 0) return hour + "小时" + flag;
        if (min != 0) return min + "分钟" + flag;
        return "刚刚";
    }

    public static String getDistanceTime1(long start, long end) {
        String distance = "";
        long diff;
        if (end > start) {
            diff = end - start;
        } else {
            diff = start - end;
        }
        long day = diff / (24 * 60 * 60);
        long hour = (diff / (60 * 60) - day * 24);
        long min = ((diff / (60)) - day * 24 * 60 - hour * 60);
        long sec = (diff - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        LogUtils.i(TAG, " getDistanceTime1 --- diff == " + diff + " , day == " + day + " , hour == " + hour + " , min == " + min + " , sec == " + sec);
        if (day > 0) {
            distance += (day + "天");
        }
        if (hour > 0) {
            distance += (hour + "小时");
        }
        if (min > 0) {
            distance += (min + "分");
        }
        return distance;
    }
}