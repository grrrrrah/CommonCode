package com.lijangop.sdk.utils.ring;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.lijangop.sdk.utils.ring.settermanager.RingtoneSetExecutor;
import com.lijangop.sdk.utils.ring.settermanager.RingtoneSetListener;
import com.lijangop.sdk.utils.ring.settermanager.RingtoneSetTask;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 本类为设置铃声对外开放设置接口类以及针对不同机型进行适配的具体实现
 *
 * @author yudeng
 */
public class RingtoneManagerEnhanced {
    public static synchronized int setAllRingAudio(Context context, String ringPath,
                                                   String ringName, String alarmPath,
                                                   String alarmName, String notiPath,
                                                   String notiName) {
        RingtoneManagerInterface ringtoneManager = RingtoneManagerFactory
                .getRingtoneManager(context);
        return ringtoneManager.setAllRingAudio(context, ringPath, ringName,
                alarmPath, alarmName, notiPath, notiName);
    }

    public static synchronized int removeAllAudioSettings(Context context) {
        RingtoneManagerInterface ringtoneManager = RingtoneManagerFactory
                .getRingtoneManager(context);
        return ringtoneManager.removeAllAudioSettings(context);
    }

    /**
     * @param context
     * @param path
     * @param name
     * @param copy2SysDir //是否将待设置的铃音移动到系统铃声文件夹下再设置  防止被一些清理软件误清理
     * @param listener
     */
    public static synchronized void setRingtone(Context context,
                                                String path, String name, boolean copy2SysDir,
                                                RingtoneSetListener listener) {
        RingtoneSetExecutor.getInstance().execute(
                new RingtoneSetTask(RingtoneSetterConstants.RINGTONE_SET_TYPE_RING,
                        context, path, name, copy2SysDir, listener));
    }

    /**
     * 设置中包含文件操作以及数据库操作
     *
     * @param context
     * @param path
     * @param name
     * @param copy2SysDir //是否将待设置的铃音移动到系统铃声文件夹下再设置  防止被一些清理软件误清理
     * @return
     */
    @Deprecated
    public static synchronized int setRingtone(Context context, String path, String name, boolean copy2SysDir) {
        RingtoneManagerInterface ringtoneManager = RingtoneManagerFactory
                .getRingtoneManager(context);
        return ringtoneManager.setRingtone(context, path, name, copy2SysDir);
    }

    /**
     * 异步的方式设置闹钟铃声
     *
     * @param context
     * @param path
     * @param name
     * @param listener
     */
    public static synchronized void setAlarm(Context context, String path,
                                             String name, boolean copy2SysDir,
                                             RingtoneSetListener listener) {
        RingtoneSetExecutor.getInstance().execute(
                new RingtoneSetTask(RingtoneSetterConstants.RINGTONE_SET_TYPE_ALARM,
                        context, path, name, copy2SysDir, listener));
    }

    /**
     * 设置中包含文件操作以及数据库操作
     *
     * @return 设置结果 {@link RingtoneManagerInterface#SET_RESULT_FAILED
     * {@link RingtoneManagerInterface#SET_RESULT_SUCCESS}
     * {@link RingtoneManagerInterface#SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED}}
     */
    public static synchronized int setAlarm(Context context, String path,
                                            String name, boolean copy2SysDir) {
        RingtoneManagerInterface ringtoneManager = RingtoneManagerFactory
                .getRingtoneManager(context);
        return ringtoneManager.setAlarm(context, path, name, copy2SysDir);
    }

    public static synchronized void setSmsAudio(Context context, String path,
                                                String name, boolean copy2SysDir,
                                                RingtoneSetListener listener) {
        RingtoneSetExecutor.getInstance().execute(
                new RingtoneSetTask(RingtoneSetterConstants.RINGTONE_SET_TYPE_SMS,
                        context, path, name, copy2SysDir, listener));
    }

    /**
     * 设置中包含文件操作以及数据库操作
     *
     * @return 设置结果 {@link RingtoneManagerInterface#SET_RESULT_FAILED
     * {@link RingtoneManagerInterface#SET_RESULT_SUCCESS}
     * {@link RingtoneManagerInterface#SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED}}
     */
    @Deprecated
    public static synchronized int setSmsAudio(Context context,
                                               String path, String name, boolean copy2SysDir) {
        RingtoneManagerInterface ringtoneManager = RingtoneManagerFactory
                .getRingtoneManager(context);
        return ringtoneManager.setSms(context, path, name, copy2SysDir);
    }

    public static synchronized void setNotification(Context context, String path,
                                                    String name, boolean copy2SysDir,
                                                    RingtoneSetListener listener) {
        RingtoneSetExecutor.getInstance().execute(
                new RingtoneSetTask(RingtoneSetterConstants.RINGTONE_SET_TYPE_NOTIFICATION,
                        context, path, name, copy2SysDir, listener));
    }

    /**
     * 设置中包含文件操作以及数据库操作  将设置操作改为异步操作
     *
     * @return 设置结果 {@link RingtoneManagerInterface#SET_RESULT_FAILED
     * {@link RingtoneManagerInterface#SET_RESULT_SUCCESS}
     * {@link RingtoneManagerInterface#SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED}}
     */
    @Deprecated
    public static synchronized int setNotification(Context context,
                                                   String path, String name,
                                                   boolean copy2SysDir) {
        RingtoneManagerInterface ringtoneManager = RingtoneManagerFactory
                .getRingtoneManager(context);
        return ringtoneManager.setNotification(context, path, name, copy2SysDir);
    }

    public static synchronized void setSpecifyRing(Context context, String path, String name,
                                                   String userID, boolean copy2SysDir,
                                                   RingtoneSetListener listener) {
        RingtoneSetExecutor.getInstance().execute(new RingtoneSetTask(
                RingtoneSetterConstants.RINGTONE_SET_TYPE_SPECIFY_RINGTONE,
                context, path, name, userID, copy2SysDir, listener));
    }

    @Deprecated
    public static synchronized int setSpecifyRing(Context context,
                                                  String path, String name, String userID,
                                                  boolean copy2SysDir) {
        RingtoneManagerStanderd ringtoneManager = new RingtoneManagerStanderd();
        return ringtoneManager.setSpecifyRing(context, path, name, userID, copy2SysDir);
    }


    // ---------- OPhone手机的适配 --------------------//

    /***
     * OPhone设置振铃
     */
    public static void setRingForOPhone(ContentResolver cr, Uri uri)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        Class<?> cls = Class.forName("android.provider.Settings$Profile");
        Method method = cls.getMethod("setRingTone", ContentResolver.class, Uri.class);
        method.invoke(cls, cr, uri);
    }

    public static Uri getRingtoneForOPhone(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        Class<?> cls = Class.forName("android.provider.Settings$Profile");
        Method getRingTone = cls.getMethod("getRingTone", ContentResolver.class, String.class);
        String colName = "line1_ringtone";
        return (Uri) getRingTone.invoke(cls,
                c.getContentResolver(), colName);
    }

    /***
     * 设置短信提示音
     */
    public static void setNotificationForOPhone(Context c, Uri uri)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        String[] sProjection = {"_id", "name", "ringmode", "volume",
                "line1_ringtone", "line2_ringtone", "message_alert",
                "email_alert", "calendar_alert", "alarm_alert",
                "reminder_alert", "fetion_alert", "_preload", "_airplane"};
        int currentprofileid = 0;
        Class<?> cls = Class.forName("android.provider.Settings$System");
        Method method = cls.getMethod("getInt", ContentResolver.class, String.class, int.class);
        currentprofileid = (Integer) method.invoke(cls,
                c.getContentResolver(), "current_profile", 1);
        Uri url = ContentUris.withAppendedId(
                Uri.parse("content://settings/profile"), currentprofileid);

        // cursor:
        Cursor cursor = c.getContentResolver().query(url, sProjection, null,
                null, null);
        ContentValues paramContentValues = new ContentValues(1);
        paramContentValues.put("message_alert", uri.toString());
        if (null != cursor && cursor.moveToFirst()) {
            c.getContentResolver().update(url, paramContentValues, null, null);
        }
        if (null != cursor) {
            cursor.close();
        }
    }

    public static Uri getSMSAlertForOPhone(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        String[] sProjection = {"_id", "name", "ringmode", "volume",
                "line1_ringtone", "line2_ringtone", "message_alert",
                "email_alert", "calendar_alert", "alarm_alert",
                "reminder_alert", "fetion_alert", "_preload", "_airplane"};
        int currentprofileid = 0;
        Class<?> cls = Class.forName("android.provider.Settings$System");
        Method method = cls.getMethod("getInt", ContentResolver.class, String.class, int.class);
        currentprofileid = (Integer) method.invoke(cls,
                c.getContentResolver(), "current_profile", 1);
        Uri uri = ContentUris.withAppendedId(
                Uri.parse("content://settings/profile"), currentprofileid);

        // cursor:
        Cursor cursor = c.getContentResolver().query(uri, sProjection, null,
                null, null);
        if (null != cursor) {
            if (cursor.moveToFirst()) {
                String url = cursor.getString(6);
                if (null != url) {
                    cursor.close();
                    return Uri.parse(url);
                }
            }
            cursor.close();
        }
        return null;
    }

    /***
     * 设置闹铃铃声
     */
    public static void setAlarmAlertForOPhone(Context c, Uri uri) {
        try {
            setDefaultAlarmAlertForOPhone(c, uri);
        } catch (Exception e) {
            Log.e("fgtian", "OPhone：默认闹钟设置失败");
        }
        List<Long> idList = new ArrayList<Long>();
        // 完整的表结构 {"_id", "hour", "minutes", "daysofweek", "alarmtime",
        // "enabled",
        // "vibrate", "message", "alert", "smartenable", "durationid", "toneid",
        // "duration", "smartalarmring" };
        String[] idProjection = new String[]{"_id"};
        Uri alertUri = Uri.parse("content://com.android.alarmclock/alarm");
        Cursor cursor = c.getContentResolver().query(alertUri, idProjection,
                null, null, null);
        if (null != cursor) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                Log.e("fgtian", "OPhone: 闹钟id=" + id);
                idList.add(id);
            }
            cursor.close();
        }
        if (idList.isEmpty()) {
            Log.e("fgtian", "未发现闹铃");
            return;
        }

        // 这里设置smartalarmring无效，
        // 原因"可能"是需要同时设置smartenable字段，留待后来人继续努力
        String alert = uri.toString();
        ContentValues cv = new ContentValues(1);
        cv.put("alert", alert);
        ContentResolver cr = c.getContentResolver();
        int updateRows = 0;
        while (!idList.isEmpty()) {
            long id = idList.remove(0);
            int row = cr.update(ContentUris.withAppendedId(alertUri, id), cv,
                    null, null);
            Log.e("fgtian", "update rows = " + row);
            updateRows += row;
        }
        Log.e("fgtian", "update rows(总计) = " + updateRows);
    }

    public static void setDefaultAlarmAlertForOPhone(Context c, Uri uri) {
        @SuppressWarnings("unused")
        String[] sProjection = {"_id", "name", "ringmode", "volume",
                "line1_ringtone", "line2_ringtone", "message_alert",
                "email_alert", "calendar_alert", "alarm_alert",
                "reminder_alert", "fetion_alert", "_preload", "_airplane"};
        ContentResolver cr = c.getContentResolver();
        int i = Settings.System.getInt(cr, "current_profile",
                1);
        ContentValues cv = new ContentValues(1);
        cv.put("alarm_alert", uri.toString());
        int rows = cr.update(
                ContentUris.withAppendedId(
                        Uri.parse("content://settings/profile"), i), cv, null,
                null);
        Log.e("fgtian", "更新的条目数：" + rows);
    }

    public static void setNotificationForSamsung(Context c, Uri uri)
            throws ClassNotFoundException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        String className = "android.media.RingtoneManager";
        Class<?> cls = Class.forName(className);
        // /反编译三星framework中的参数 卡1的短信音和卡2的短信音
        int TYPE_NOTIFICATION = 2;
        // int TYPE_NOTIFICATION_2 = 16;
        String methodName = "setActualDefaultRingtoneUri";
        Class<?>[] methodParams = new Class[]{Context.class, int.class,
                Uri.class};
        Method method = cls.getMethod(methodName, methodParams);
        method.invoke(cls, c, TYPE_NOTIFICATION, uri);
    }

    public static void setNotificationForMeiZu(Context c, Uri uri)
            throws ClassNotFoundException, NoSuchMethodException,
            IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        int TYPE_MMS;
        Class<?> cls;
        if ("mx5".equalsIgnoreCase(Build.MODEL) ||
                "mx6".equalsIgnoreCase(Build.MODEL)
                || "m1 note".equalsIgnoreCase(Build.MODEL) || "16th".equalsIgnoreCase(Build.MODEL)
                || "M1822".equalsIgnoreCase(Build.MODEL)) {
            //TYPE_CALENDAR = 1024;
            //TYPE_EMAIL = 512;
            //TYPE_MMS = 256;
            //TYPE_RINGTONE_1 = 2048;
            //TYPE_RINGTONE_2 = 4096;
            String className = "android.media.MzRingtoneManager";
            cls = Class.forName(className);
            TYPE_MMS = 256;
        } else {
            String className = "android.media.RingtoneManager";
            cls = Class.forName(className);
            // 反编译魅族framework中的参数
            // int TYPE_ALARM = 4;
            // int TYPE_ALL = 7;
            // int TYPE_CALENDAR = 32;
            // int TYPE_EMAIL = 16;
            // final int TYPE_NOTIFICATION = 2;
            // int TYPE_RINGTONE = 1;
            if ("mx4".equalsIgnoreCase(Build.MODEL)
                    || "m2 note".equalsIgnoreCase(Build.MODEL)) {
                TYPE_MMS = 32;
            } else {
                TYPE_MMS = 8;
            }
        }
        String methodName = "setActualDefaultRingtoneUri";
        Class<?>[] methodParams = new Class[]{Context.class, int.class,
                Uri.class};
        Method method = cls.getMethod(methodName, methodParams);
        method.invoke(cls, c, TYPE_MMS, uri);
    }

    public static Uri getNotificationForMeiZu(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        String className = "android.media.RingtoneManager";
        if ("mx5".equalsIgnoreCase(Build.MODEL) ||
                "mx6".equalsIgnoreCase(Build.MODEL)) {
            className = "android.media.MzRingtoneManager";
        }
        String methodName = "getActualDefaultRingtoneUri";
        Class<?> cls = Class.forName(className);
        Class<?>[] methodParams = new Class[]{Context.class, int.class};
        // int TYPE_MMS = 8;
        int TYPE_MMS;
        if ("mx4".equalsIgnoreCase(Build.MODEL)
                || "m1 note".equalsIgnoreCase(Build.MODEL)
                || "m2 note".equalsIgnoreCase(Build.MODEL)) {
            TYPE_MMS = 32;
        } else if ("mx5".equalsIgnoreCase(Build.MODEL) ||
                "mx6".equalsIgnoreCase(Build.MODEL)
                || "16th".equalsIgnoreCase(Build.MODEL)
                || "M1822".equalsIgnoreCase(Build.MODEL)) {
            TYPE_MMS = 256;
        } else {
            TYPE_MMS = 8;
        }
        Uri uri = null;
        Method method1 = cls.getMethod(methodName, methodParams);
        uri = (Uri) method1.invoke(cls, c, TYPE_MMS);

        return uri;
    }

    // ----------------- 小米手机的适配 ----------------- //

    /**
     * 设置短信提示音
     */
    public static void setNotificationForXiaoMi(Context c, Uri uri)
            throws ClassNotFoundException, SecurityException,
            IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        String className = "android.media.ExtraRingtoneManager";
        @SuppressLint("PrivateApi") Class<?> cls = Class.forName(className);
        int ringtoneType = 16;
        try {
            Uri localUri = null;
            try {
                String methodName = "resolveDefaultRingtoneUri";
                Class<?>[] methodParams = new Class[]{Context.class, int.class,
                        Uri.class};
                Method method = cls.getMethod(methodName, methodParams);
                localUri = (Uri) method.invoke(cls, c, ringtoneType, uri);
            } catch (Exception e) {
            }
            Class<?>[] mp2 = new Class[]{Context.class, int.class, Uri.class};
            String m2 = "setRingtoneUri";
            Method method2 = cls.getMethod(m2, mp2);
            if (null != localUri) {
                method2.invoke(cls, c, ringtoneType, localUri);
                RingtoneManager.setActualDefaultRingtoneUri(c, ringtoneType,
                        localUri);
            }
            method2.invoke(cls, c, ringtoneType, uri);
            RingtoneManager.setActualDefaultRingtoneUri(c, ringtoneType, uri);

        } catch (NoSuchMethodException e) {
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
                return;
            }
            try {
                // for miuiV6
                String methodName4 = "resolveRingtonePath";
                Class<?>[] methodParams4 = new Class<?>[]{Context.class,
                        Uri.class};
                Method method4 = cls.getMethod(methodName4, methodParams4);
                String ringPath = (String) method4.invoke(cls, c, uri);

                String methodName3 = "saveDefaultSound";
                Class<?>[] methodParams3 = new Class<?>[]
                        {Context.class, int.class, Uri.class};
                Method method3 = cls.getMethod(methodName3, methodParams3);
                //                method3.invoke(cls, c, 16, uri);
                method3.invoke(cls, c, 1024, uri);
                method3.invoke(cls, c, 2048, uri);
                setNotificationForXiaoMiEnhanced(c, ringPath);
            } catch (Throwable e1) {
                e1.printStackTrace();
            }
        }

    }

    public static void setNotificationForOppo(Context c, Uri uri)
            throws ClassNotFoundException, SecurityException,
            IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        String className = "android.provider.Settings$System";
        Class<?> cls = Class.forName(className);
        String methodName = "putString";
        Class<?>[] methodParams = new Class<?>[]{ContentResolver.class,
                String.class, String.class};
        String str = uri.toString();
        if (null == str || str.trim().equalsIgnoreCase("")) {
            return;
        }
        try {
            Method method = cls.getMethod(methodName, methodParams);
            method.invoke(cls, c.getContentResolver(),
                    "oppo_sms_notification_sound", uri.toString());

            // 适配双卡双待另一个槽的手机
            method.invoke(cls, c.getContentResolver(),
                    "notification_sim2", str);
        } catch (NoSuchMethodException e) {
            Log.e("liangma", "oppo设置方法未找到");
            e.printStackTrace();
        }
    }

    public static boolean setRingtoneForGionee(Context context, int ringType, Uri ringUri) {
        if (!getRomVersion().contains("rom4.2.12") && !isCancelAudioProfile(context)) {
            try {
                Class e1 = Class.forName("com.gionee.common.audioprofile.IAudioProfileService");
                if (e1 == null) {
                    return false;
                }
                Class[] params = new Class[]{String.class, Integer.TYPE, Uri.class};
                Object[] objects = new Object[]{"mtk_audioprofile_general",
                        Integer.valueOf(ringType), ringUri};
                invoke(e1, "setRingtoneUri", params, objects);
            } catch (NoClassDefFoundError var6) {
                RingtoneManager.setActualDefaultRingtoneUri(context, ringType, ringUri);
            } catch (Exception var7) {
                var7.printStackTrace();
                return false;
            }
        } else {
            RingtoneManager.setActualDefaultRingtoneUri(context, ringType, ringUri);
        }
        return true;
    }

    private static String getRomVersion() {
        String romVer = (String) invoke("android.os.SystemProperties", "get",
                new Class[]{String.class, String.class},
                new Object[]{"ro.gn.gnromvernumber", "GiONEE ROM4.0.1"});
        Log.i("KY_Ringtone", "getRomVersion,romVer=" + romVer);
        return romVer;
    }

    public static Object invoke(String classNameS, String methodNameS, Class[] classes,
                                Object[] objectO) {
        Object object = null;

        try {
            Class e = Class.forName(classNameS);
            Method method = e.getDeclaredMethod(methodNameS, classes);
            object = method.invoke(e, objectO);
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return object;
    }

    public static Object invoke(Class<?> className, String methodNameS, Class[] classes,
                                Object[] objectO) {
        Object object = null;

        try {
            Method e = className.getDeclaredMethod(methodNameS, classes);
            object = e.invoke(className, objectO);
        } catch (Exception var6) {
            var6.printStackTrace();
        }

        return object;
    }

    private static boolean isCancelAudioProfile(Context context) {
        try {
            PackageInfo e = context.getPackageManager().
                    getPackageInfo("gn.com.android.audioprofile", 0);
            return e == null;
        } catch (Exception var2) {
            return true;
        }
    }

    public static void setNotificationForXiaoMiEnhanced(Context c, String str3)
            throws ClassNotFoundException, SecurityException,
            IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        String className = "android.provider.MiuiSettings$System";
        Class<?> cls = Class.forName(className);
        String methodName = "putString";
        Class<?>[] methodParams = new Class<?>[]{ContentResolver.class,
                String.class, String.class};
        try {
            Method method = cls.getMethod(methodName, methodParams);
            method.invoke(cls, c.getContentResolver(),
                    "sms_received_sound", str3);

            // 适配双卡双待的手机
            method.invoke(cls, c.getContentResolver(),
                    "sms_received_sound_slot_1", str3);
            method.invoke(cls, c.getContentResolver(),
                    "sms_received_sound_slot_2", str3);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void setAlarmAlertForXiaoMiV6(Context c, Uri uri) {

        String className = "android.media.ExtraRingtoneManager";
        Class<?> cls = null;
        try {
            cls = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            String methodName = "resolveRingtonePath";
            Class<?>[] methodParams = new Class<?>[]{Context.class, Uri.class};
            Method method = cls.getMethod(methodName, methodParams);
            String ringPath = (String) method.invoke(cls, c, uri);

            String methodName2 = "putString";
            Class<?>[] methodParams2 = new Class<?>[]{ContentResolver.class,
                    String.class, String.class};

            Method method2 = cls.getMethod(methodName2, methodParams2);
            method2.invoke(cls, c.getContentResolver(),
                    "alarm_alert", ringPath);
        } catch (Exception e) {

        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            return;
        }
        try {
            // for miuiV6
            String methodName3 = "saveDefaultSound";
            Class<?>[] methodParams3 = new Class<?>[]
                    {Context.class, int.class, Uri.class};
            Method method3 = cls.getMethod(methodName3, methodParams3);
            method3.invoke(cls, c, 4, uri);
        } catch (Throwable e1) {
            e1.printStackTrace();
        }
    }

    /**
     * 小米短信铃声适配
     **/
    public static void setSmsSoundForRedMiNote4(Context c, Uri uri) throws ClassNotFoundException, SecurityException,
            IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        String className = "android.media.RingtoneManager";
        String className2 = "android.media.ExtraRingtoneManager";
        Class<?> cls = Class.forName(className);
        Class<?> cls2 = Class.forName(className2);
        String methodName = "setActualDefaultRingtoneUri";
        String methodName2 = "getActualDefaultRingtoneUri";
        String methodName3 = "getDefaultSoundActualUri";
        Class<?>[] methodParams = new Class<?>[]{Context.class, int.class, Uri.class};
        Class<?>[] methodParams2 = new Class<?>[]{Context.class, int.class};
        Class<?>[] methodParams3 = new Class<?>[]{Context.class, int.class};
        try {
            Method method = cls.getMethod(methodName, methodParams);
            for (int i = 0; i <= 2048; i++) {
                method.invoke(cls, c, i, uri);
            }

            Method method2 = cls.getMethod(methodName2, methodParams2);
            method2.invoke(cls, c, 16);

            Method method3 = cls2.getMethod(methodName3, methodParams3);
            method3.invoke(cls, c, 16);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static Uri getSmsRingForOppo(Context c) throws ClassNotFoundException,
            InvocationTargetException, IllegalAccessException {
        String className = "android.provider.Settings$System";
        Class<?> cls = Class.forName(className);
        String methodName = "getString";
        Class<?>[] methodParams = new Class<?>[]{ContentResolver.class,
                String.class};
        Uri uri = null;
        String str = null;
        try {
            Method method = cls.getMethod(methodName, methodParams);
            method.invoke(cls, c.getContentResolver(),
                    "oppo_sms_notification_sound");

            // 适配双卡双待另一个槽的手机
            str = (String) method.invoke(cls, c.getContentResolver(),
                    "notification_sim2");
        } catch (NoSuchMethodException e) {
            Log.e("liangma", "oppo设置方法未找到");
            e.printStackTrace();
        }
        if (null != str && !TextUtils.isEmpty(str.trim())) {
            uri = Uri.parse(str);
            return uri;
        } else {
            throw new ClassNotFoundException();
        }
    }

    /**
     * 获取短信提示音
     */
    public static Uri getNotificationForXiaoMi(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        String className = "android.media.ExtraRingtoneManager";
        String methodName = "getRingtoneUri";
        String methodName2 = "getDefaultRingtoneUri";
        Class<?>[] methodParams = new Class[]{Context.class, int.class};
        Class<?>[] methodParams2 = new Class[]{Context.class};
        Class<?> cls = Class.forName(className);
        int ringtoneType = 16;
        Uri uri = null;

        Method method1 = cls.getMethod(methodName, methodParams);
        uri = (Uri) method1.invoke(cls, c, ringtoneType);
        if (uri == null) {
            Method method2 = cls.getMethod(methodName2, methodParams2);
            uri = (Uri) method2.invoke(cls, c);
        }
        return uri;
    }

    /***
     * 小米闹铃适配：也适用于其他一些手机
     */
    public static void setAlarmAlert(Context c, Uri uri) {
        List<Long> idList = new ArrayList<Long>();
        // 完整的表结构 {"_id", "hour", "minutes", "daysofweek", "alarmtime",
        // "enabled", "vibrate", "message", "alert", "type" };
        String[] idProjection = new String[]{"_id"};
        Uri alertUri = Uri.parse("content://com.android.deskclock/alarm");
        Cursor cursor = c.getContentResolver().query(alertUri, idProjection,
                null, null, null);
        if (null != cursor) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                Log.e("fgtian", "一般: 闹钟id=" + id);
                idList.add(id);
            }
            cursor.close();
        }
        if (idList.isEmpty()) {
            Log.e("fgtian", "未发现闹铃");
            return;
        }

        String alert = uri.toString();
        ContentValues cv = new ContentValues(1);
        cv.put("alert", alert);
        ContentResolver cr = c.getContentResolver();
        int updateRows = 0;
        while (!idList.isEmpty()) {
            long id = idList.remove(0);
            int row = cr.update(ContentUris.withAppendedId(alertUri, id), cv,
                    null, null);
            Log.e("fgtian", "update rows = " + row);
            updateRows += row;
        }
        Log.e("fgtian", "update rows(总计) = " + updateRows);
    }

    // ---------------- HTC手机的适配 ---------------- //
    public static void setAlarmAlertForHtc(Context c, Uri uri) {
        List<Long> idList = new ArrayList<Long>();
        // 完整的表结构 {"_id", "hour", "minutes", "daysofweek", "alarmtime",
        // "enabled", "vibrate", "message", "alert", "snoozed"};
        String[] idProjection = new String[]{"_id"};
        Uri alertUri = Uri.parse("content://com.htc.android.alarmclock/alarm");
        Cursor cursor = c.getContentResolver().query(alertUri, idProjection,
                null, null, null);
        if (null != cursor) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                Log.e("fgtian", "HTC: 闹钟id=" + id);
                idList.add(id);
            }
            cursor.close();
        }
        if (idList.isEmpty()) {
            Log.e("fgtian", "未发现闹铃");
            return;
        }

        String alert = uri.toString();
        ContentValues cv = new ContentValues(1);
        cv.put("alert", alert);
        ContentResolver cr = c.getContentResolver();
        int updateRows = 0;
        while (!idList.isEmpty()) {
            long id = idList.remove(0);
            int row = cr.update(ContentUris.withAppendedId(alertUri, id), cv,
                    null, null);
            Log.e("fgtian", "update rows = " + row);
            updateRows += row;
        }
        Log.e("fgtian", "update rows(总计) = " + updateRows);
    }

    // ---------------- 三星手机的适配 ---------------- //
    public static void setAlarmAlertForSamSung(Context c, Uri uri) {
        List<Long> idList = new ArrayList<Long>();
        String[] idProjection = new String[]{"_id"};
        Uri alertUri = Uri
                .parse("content://com.samsung.sec.android.clockpackage/alarm");
        Cursor cursor = c.getContentResolver().query(alertUri, idProjection,
                null, null, null);
        if (null != cursor) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                Log.e("fgtian", "三星: 闹钟id=" + id);
                idList.add(id);
            }
            cursor.close();
        }
        if (idList.isEmpty()) {
            Log.e("fgtian", "未发现闹铃");
            return;
        }

        String alert = uri.toString();
        ContentValues cv = new ContentValues(1);
        cv.put("alarmuri", alert);
        ContentResolver cr = c.getContentResolver();
        int updateRows = 0;
        while (!idList.isEmpty()) {
            long id = idList.remove(0);
            int row = cr.update(ContentUris.withAppendedId(alertUri, id), cv,
                    null, null);
            Log.e("fgtian", "update rows = " + row);
            updateRows += row;
        }
        Log.e("fgtian", "update rows(总计) = " + updateRows);
    }

    private static final String SYSTEMSERVICE_COOLPAD = "coolpadSystem";

    // ------------ 酷派手机 ------------ //
    @SuppressWarnings({"unused"})
    @SuppressLint("WrongConstant")
    public static void testCoolPad(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        String firstPhone = null;
        String secondPhone = null;
        String firstSMS = null;
        String secondSMS = null;
        String smName = "com.yulong.android.server.systeminterface.SystemManager";
        String SceneModeType = "com.yulong.android.server.systeminterface.bean.SceneMode";
        Object SystemManager = c.getSystemService(SYSTEMSERVICE_COOLPAD);
        int[] modes = new int[]{1, 4, 6}; // 普通模式、户外模式、VIP模式
        for (int i = 0; i < modes.length; i++) {
            Method getOriginSceneMode = SystemManager.getClass().getMethod(
                    "getOriginSceneMode", int.class);
            // 类型是“com.yulong.android.server.systeminterface.bean.SceneMode”
            Object mode = getOriginSceneMode.invoke(SystemManager,
                    modes[i]);
            // 反编译出设置方法
            if (null == mode) {
                continue;
            }
            Method getPhoneMusicFirst = mode.getClass().getMethod(
                    "getPhoneMusicFirst", (Class<?>[]) null);
            String url = (String) getPhoneMusicFirst.invoke(mode,
                    (Object[]) null);
            Cursor cursor = getAudioCursorFromUri(c, url);
            if (null == cursor || !cursor.moveToFirst()) {
                if (null != cursor) {
                    cursor.close();
                }
                continue;
            }
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String path = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));
            Log.e("fgtian", "title = " + title + ", path=" + path);
            cursor.close();

            Method getPhoneMusicSecond = mode.getClass().getMethod(
                    "getPhoneMusicSecond", (Class<?>[]) null);
            url = (String) getPhoneMusicSecond.invoke(mode, (Object[]) null);
            cursor = getAudioCursorFromUri(c, url);
            if (null == cursor || !cursor.moveToFirst()) {
                if (null != cursor) {
                    cursor.close();
                }
                continue;
            }
            title = cursor.getString(cursor.getColumnIndex("title"));
            path = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));
            Log.e("fgtian", "title = " + title + ", path=" + path);
            cursor.close();

            Method getSmsMusicFirst = mode.getClass().getMethod(
                    "getSmsMusicFirst", (Class<?>[]) null);
            url = (String) getSmsMusicFirst.invoke(mode, (Object[]) null);
            cursor = getAudioCursorFromUri(c, url);
            if (null == cursor || !cursor.moveToFirst()) {
                if (null != cursor) {
                    cursor.close();
                }
                continue;
            }
            title = cursor.getString(cursor.getColumnIndex("title"));
            path = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));
            Log.e("fgtian", "title = " + title + ", path=" + path);
            cursor.close();

            Method getSmsMusicSecond = mode.getClass().getMethod(
                    "getSmsMusicSecond", (Class<?>[]) null);
            url = (String) getSmsMusicSecond.invoke(mode, (Object[]) null);
            cursor = getAudioCursorFromUri(c, url);
            if (null == cursor || !cursor.moveToFirst()) {
                if (null != cursor) {
                    cursor.close();
                }
                continue;
            }
            title = cursor.getString(cursor.getColumnIndex("title"));
            path = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));
            Log.e("fgtian", "title = " + title + ", path=" + path);
            cursor.close();
        }
    }


    //----------------适配vivoX5 pro 的闹钟铃声 ---------------//
    public static void setAlarmAlertForVivoX(Context c, Uri uri) {
        Settings.System.putString(c.getContentResolver(),
                "bbk_alarm_alert", uri.toString());

    }

    public static Uri getAlarmForVivo(Context c) {
        return Settings.System.getUriFor(
                "bbk_alarm_alert");

    }

    //采用这种适配方法的原因是vivo手机会默认检查是否只有卡2，如果只有卡2，才会对卡2的来电铃声设置生效。
    //否则只能设置卡一，这里通过以下方法对卡2进行设置，在调用该方法钱用普通方法设置卡1来电铃声
    public static void setRingToneForVivoY66OrY67(Context c, Uri uri) {
        Settings.System.putString(c.getContentResolver(), "ringtone_sim2", uri.toString());
    }

    @SuppressLint("WrongConstant")
    public static void setRingtoneForCoolPad(Context c, Uri uri)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        String SceneModeType = "com.yulong.android.server.systeminterface.bean.SceneMode";
        Object SystemManager = c.getSystemService(SYSTEMSERVICE_COOLPAD);
        Class<?> SceneMode = Class.forName(SceneModeType);
        Method setSceneMode = SystemManager.getClass().getMethod(
                "setSceneMode", int.class, SceneMode);
        int[] modes = new int[]{1, 4, 6}; // 普通模式、户外模式、VIP模式
        String url = uri.toString();
        for (int i = 0; i < modes.length; i++) {
            Method getOriginSceneMode = SystemManager.getClass().getMethod(
                    "getOriginSceneMode", int.class);
            Method getSceneMode = SystemManager.getClass().getMethod(
                    "getSceneMode", int.class);
            // 类型是“com.yulong.android.server.systeminterface.bean.SceneMode”
            Object mode = getSceneMode.invoke(SystemManager,
                    modes[i]);
            if (null == mode) {
                mode = getOriginSceneMode.invoke(SystemManager,
                        modes[i]);
            }
            // 反编译出设置方法
            if (null == mode) {
                continue;
            }
            Method setPhoneMusicFirst = mode.getClass().getMethod(
                    "setPhoneMusicFirst", String.class);
            Method setPhoneMusicSecond = mode.getClass().getMethod(
                    "setPhoneMusicSecond", String.class);
            setPhoneMusicFirst.invoke(mode, url);
            setPhoneMusicSecond.invoke(mode, url);
            // 进行设置：
            setSceneMode.invoke(SystemManager, modes[i], mode);
        }
    }

    public static void setRingtoneForCoolPadNew(Context c, String path)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException,
            InstantiationException {

        boolean mResult = false;

        if (path != null && !path.startsWith("file://")) {
            path = "file://" + path;
        }

        String systemManagerName = "com.yulong.android.server.systeminterface.SystemManager";
        Class<?> systemManagerClass = Class.forName(systemManagerName);

        Constructor cons = systemManagerClass.getConstructor(Context.class);

        Object systemManager = cons.newInstance(c);

        Method setCurrentRing = null;
        try {
            setCurrentRing = systemManagerClass.getMethod("setCurrentCDMARing",
                    String.class);
            if (setCurrentRing != null) {
                mResult = (Boolean) setCurrentRing.invoke(systemManager, path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            setCurrentRing = systemManagerClass.getMethod("setCurrentGSMRing",
                    String.class);
            if (setCurrentRing != null) {
                mResult = (Boolean) setCurrentRing.invoke(systemManager, path);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressLint("WrongConstant")
    public static RingInfo getRingtoneForCoolPad(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        Object SystemManager = c.getSystemService(SYSTEMSERVICE_COOLPAD);
        Method getCurrentModel = SystemManager.getClass().getMethod(
                "getCurrentModel", (Class<?>[]) null);
        int[] modes = new int[]{1, 4, 6}; // 普通模式、户外模式、VIP模式
        int curMode = (Integer) getCurrentModel.invoke(SystemManager,
                (Object[]) null);
        boolean find = false;
        for (int i = 0; i < modes.length; i++) {
            if (curMode == modes[i]) {
                find = true;
                break;
            }
        }
        if (!find)
            return null;
        Class<?>[] pType = new Class[]{int.class};
        Object[] pValue = new Object[]{curMode};
        Method getOriginSceneMode = SystemManager.getClass().getMethod(
                "getOriginSceneMode", pType);
        Method getSceneMode = SystemManager.getClass().getMethod(
                "getSceneMode", pType);
        // 当前的模式：类型是“com.yulong.android.server.systeminterface.bean.SceneMode”
        Object mode = getSceneMode.invoke(SystemManager, pValue);
        if (null == mode) {
            mode = getOriginSceneMode.invoke(SystemManager, pValue);
        }
        // 反编译出设置方法
        if (null == mode) {
            return null;
        }
        Method getPhoneMusicFirst = mode.getClass().getMethod(
                "getPhoneMusicFirst", (Class<?>[]) null);
        String url = (String) getPhoneMusicFirst.invoke(mode, (Object[]) null);
        Cursor cursor = getAudioCursorFromUri(c, url);
        if (null == cursor || !cursor.moveToFirst()) {
            if (null != cursor) {
                cursor.close();
            }
            return null;
        }
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String path = cursor.getString(cursor
                .getColumnIndex(MediaStore.Audio.Media.DATA));
        Log.e("fgtian", "title = " + title + ", path=" + path);
        cursor.close();
        RingInfo ret = new RingInfo();
        ret.mName = title;
        ret.mPath = path;
        return ret;
    }

    public static RingInfo getRingtoneForCoolPadNew(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException,
            InstantiationException {

        String systemManagerName = "com.yulong.android.server.systeminterface.SystemManager";
        Class<?> systemManagerClass = Class.forName(systemManagerName);

        Constructor cons = systemManagerClass.getConstructor(Context.class);

        Object systemManager = cons.newInstance(c);

        Method getCurrentCDMARing = systemManager.getClass().getMethod(
                "getCurrentCDMARing");
        Method getCurrentGSMRing = systemManager.getClass().getMethod(
                "getCurrentGSMRing");

        String url = (String) getCurrentCDMARing.invoke(systemManager);
        Log.e("chtian", "getCurrentCDMARing:" + url);

        if (null == url) {
            url = (String) getCurrentGSMRing.invoke(systemManager);
            Log.e("chtian", "getCurrentGSMRing:" + url);
        }

        RingInfo ret = getRingInfoFromPath(url);
        return ret;
    }

    public static RingInfo getRingInfoFromPath(String path) {
        RingInfo ri = new RingInfo();
        if (null != path) {
            ri.mPath = path;
            ri.mName = path;
            int i = path.lastIndexOf("/");
            String filename = path.substring(i + 1, path.length());
            i = filename.lastIndexOf(".");
            if (i > 0) {
                ri.mName = filename.substring(0, i);
            } else {
                ri.mName = filename;
            }
        }
        return ri;
    }

    @SuppressLint("WrongConstant")
    public static void setSMSRingtoneForCoolPad(Context c, Uri uri)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        String SceneModeType = "com.yulong.android.server.systeminterface.bean.SceneMode";
        Object SystemManager = c.getSystemService(SYSTEMSERVICE_COOLPAD);
        Class<?> SceneMode = Class.forName(SceneModeType);
        Method setSceneMode = SystemManager.getClass().getMethod(
                "setSceneMode", int.class, SceneMode);
        int[] modes = new int[]{1, 4, 6}; // 普通模式、户外模式、VIP模式
        String url = uri.toString();
        for (int i = 0; i < modes.length; i++) {
            Method getOriginSceneMode = SystemManager.getClass().getMethod(
                    "getOriginSceneMode", int.class);
            Method getSceneMode = SystemManager.getClass().getMethod(
                    "getSceneMode", int.class);
            // 类型是“com.yulong.android.server.systeminterface.bean.SceneMode”
            Object mode = getSceneMode.invoke(SystemManager,
                    modes[i]);
            if (null == mode) {
                mode = getOriginSceneMode.invoke(SystemManager,
                        modes[i]);
            }
            // 反编译出设置方法
            if (null == mode) {
                continue;
            }
            Method setSmsMusicFirst = mode.getClass().getMethod(
                    "setSmsMusicFirst", String.class);
            Method setSmsMusicSecond = mode.getClass().getMethod(
                    "setSmsMusicSecond", String.class);
            setSmsMusicFirst.invoke(mode, url);
            setSmsMusicSecond.invoke(mode, url);
            // 进行设置：
            setSceneMode.invoke(SystemManager, modes[i], mode);
        }
    }

    public static void setSMSRingtoneForCoolPadNew(Context c, Uri uri)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException,
            InstantiationException {
        String SceneModeType = "com.yulong.android.server.systeminterface.bean.SceneMode";
        Class<?> SceneMode = Class.forName(SceneModeType);

        String systemManagerName = "com.yulong.android.server.systeminterface.SystemManager";
        Class<?> systemManagerClass = Class.forName(systemManagerName);

        Constructor cons = systemManagerClass.getConstructor(Context.class);

        Object systemManager = cons.newInstance(c);

        Method getCurrentModel = systemManagerClass
                .getMethod("getCurrentModel");
        int curModel = (Integer) getCurrentModel.invoke(systemManager);

        Method setSceneMode = systemManagerClass.getMethod("setSceneMode",
                int.class, SceneMode);
        Method getOriginSceneMode = systemManagerClass.getMethod(
                "getOriginSceneMode", int.class);
        Method getSceneMode = systemManagerClass.getMethod("getSceneMode",
                int.class);

        Object mode = getSceneMode.invoke(systemManager,
                curModel);

        if (mode == null) {
            mode = getOriginSceneMode.invoke(systemManager,
                    curModel);
        }

        if (null == mode) {
            return;
        }

        String url = uri.toString();
        Method setSmsMusicFirst = mode.getClass().getMethod("setSmsMusicFirst",
                String.class);
        Method setSmsMusicSecond = mode.getClass().getMethod(
                "setSmsMusicSecond", String.class);
        setSmsMusicFirst.invoke(mode, url);
        setSmsMusicSecond.invoke(mode, url);

        // 进行设置：
        setSceneMode.invoke(systemManager, curModel, mode);
    }

    @SuppressLint("WrongConstant")
    public static RingInfo getSMSRingtoneForCoolPad(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        Object SystemManager = c.getSystemService(SYSTEMSERVICE_COOLPAD);
        Method getCurrentModel = SystemManager.getClass().getMethod(
                "getCurrentModel", (Class<?>[]) null);
        int[] modes = new int[]{1, 4, 6}; // 普通模式、户外模式、VIP模式
        int curMode = (Integer) getCurrentModel.invoke(SystemManager,
                (Object[]) null);
        boolean find = false;
        for (int i = 0; i < modes.length; i++) {
            if (curMode == modes[i]) {
                find = true;
                break;
            }
        }
        if (!find)
            return null;
        Class<?>[] pType = new Class<?>[]{int.class};
        Object[] pValue = new Object[]{curMode};
        Method getOriginSceneMode = SystemManager.getClass().getMethod(
                "getOriginSceneMode", pType);
        Method getSceneMode = SystemManager.getClass().getMethod(
                "getSceneMode", pType);
        // 当前的模式：类型是“com.yulong.android.server.systeminterface.bean.SceneMode”
        Object mode = getSceneMode.invoke(SystemManager, pValue);
        if (null == mode) {
            mode = getOriginSceneMode.invoke(SystemManager, pValue);
        }
        // 反编译出设置方法
        if (null == mode) {
            return null;
        }
        Method getSmsMusicFirst = mode.getClass().getMethod("getSmsMusicFirst",
                (Class<?>[]) null);
        String url = (String) getSmsMusicFirst.invoke(mode, (Object[]) null);
        Cursor cursor = getAudioCursorFromUri(c, url);
        if (null == cursor || !cursor.moveToFirst()) {
            if (null != cursor) {
                cursor.close();
            }
            return null;
        }
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String path = cursor.getString(cursor
                .getColumnIndex(MediaStore.Audio.Media.DATA));
        Log.e("fgtian", "title = " + title + ", path=" + path);
        cursor.close();
        RingInfo ret = new RingInfo();
        ret.mName = title;
        ret.mPath = path;
        return ret;
    }

    public static RingInfo getSMSRingtoneForCoolPadNew(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException,
            InstantiationException {
        String SceneModeType = "com.yulong.android.server.systeminterface.bean.SceneMode";
        Class<?> SceneMode = Class.forName(SceneModeType);

        String systemManagerName = "com.yulong.android.server.systeminterface.SystemManager";
        Class<?> systemManagerClass = Class.forName(systemManagerName);

        Constructor cons = systemManagerClass.getConstructor(Context.class);

        Object systemManager = cons.newInstance(c);

        Method getCurrentModel = systemManagerClass
                .getMethod("getCurrentModel");
        int curModel = (Integer) getCurrentModel.invoke(systemManager);

        Method setSceneMode = systemManagerClass.getMethod("setSceneMode",
                int.class, SceneMode);
        Method getOriginSceneMode = systemManagerClass.getMethod(
                "getOriginSceneMode", int.class);
        Method getSceneMode = systemManagerClass.getMethod("getSceneMode",
                int.class);

        Object mode = getSceneMode.invoke(systemManager,
                curModel);

        if (mode == null) {
            mode = getOriginSceneMode.invoke(systemManager,
                    curModel);
        }

        if (null == mode) {
            return null;
        }

        Method getSmsMusicFirst = mode.getClass().getMethod("getSmsMusicFirst");
        Method getSmsMusicSecond = mode.getClass().getMethod(
                "getSmsMusicSecond");
        String url = (String) getSmsMusicFirst.invoke(mode);
        if (null == url) {
            url = (String) getSmsMusicSecond.invoke(mode);
        }
        RingInfo ret = null;
        if (url != null && !url.startsWith("file://")) {
            Cursor cursor = getAudioCursorFromUri(c, url);
            if (null == cursor || !cursor.moveToFirst()) {
                if (null != cursor) {
                    cursor.close();
                }
                return null;
            }
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String path = cursor.getString(cursor
                    .getColumnIndex(MediaStore.Audio.Media.DATA));
            cursor.close();
            ret = new RingInfo();
            ret.mName = title;
            ret.mPath = path;
        } else {
            ret = getRingInfoFromPath(url);
        }

        return ret;
    }

    public static Cursor getAudioCursorFromUri(Context context, String url) {
        boolean isEmpty = url == null || url.length() == 0;
        if (isEmpty) {
            return null;
        }
        String[] projection = null;
        String idStr = null;
        int i = url.lastIndexOf("/");
        if (i < 0)
            return null;
        idStr = url.substring(i + 1, url.length());
        String selection;
        try {
            Long id = Long.valueOf(idStr);
            selection = "_id=" + id;
            if (url.indexOf("internal") >= 0) {
                return context.getContentResolver().query(
                        MediaStore.Audio.Media.INTERNAL_CONTENT_URI,
                        projection, selection, null, null);
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection,
                selection, null, null);
    }

    public static void setAlarmAlertForCoolPad(Context c, Uri uri) {
        setAlarmAlertNormal(c, uri,
                "content://com.yulong.xtime.alarmclock/alarm", "_id", "alert");
    }

    // ----------------- 一般性的方法
    public static void setAlarmAlertNormal(Context c, Uri uri, String contentUri,
                                           String idName, String alertColumnName) {
        List<Long> idList = new ArrayList<Long>();
        String[] idProjection = new String[]{idName};
        Uri alertUri = Uri.parse(contentUri);
        Cursor cursor = c.getContentResolver().query(alertUri, idProjection,
                null, null, null);
        if (null != cursor) {
            while (cursor.moveToNext()) {
                long id = cursor.getLong(0);
                Log.e("fgtian", "闹钟id=" + id);
                idList.add(id);
            }
            cursor.close();
        }
        if (idList.isEmpty()) {
            Log.e("fgtian", "未发现闹铃");
            return;
        }

        String alert = uri.toString();
        ContentValues cv = new ContentValues(1);
        cv.put(alertColumnName, alert);
        ContentResolver cr = c.getContentResolver();
        int updateRows = 0;
        // while (!idList.isEmpty()) {
        // long id = idList.remove(0);
        // int row = cr.update(ContentUris.withAppendedId(alertUri, id), cv,
        // null, null);
        // Log.e("fgtian", "update rows = " + row);
        // updateRows += row;
        // }
        // 整体更新：
        updateRows = cr.update(alertUri, cv, null, null);
        Log.e("fgtian", "update rows(总计) = " + updateRows);
    }

    // ------------ 华为 ------------ //
    public static void TestHuaWeiC8813(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        String[] toneKey = {"ringtone", "ringtone2", "alarm_alert",
                "notification_sound", "message", "email", "calendar", "lock",
                "unlock", "effect_tick"};
        String[] toneKeyPath = {"ringtone_path", "ringtone2_path",
                "alarm_alert_path", "notification_sound_path", "message_path",
                "email_path", "calendar_path", "lock_path", "unlock_path",
                "effect_tick_path"};
        String className = "com.huawei.android.provider.SettingsEx$Systemex";
        String methodName = "getString";
        Class<?>[] methodParams = new Class[]{ContentResolver.class,
                String.class};
        Class<?> cls = Class.forName(className);
        Method getString = cls.getMethod(methodName, methodParams);
        for (int i = 0; i < toneKeyPath.length; i++) {
            String str1 = (String) getString.invoke(cls,
                    c.getContentResolver(), toneKeyPath[i]);
            String str2 = (String) getString.invoke(cls,
                    c.getContentResolver(), toneKey[i]);
            Log.e("fgtian", "path = " + str1);
            Log.e("fgtian", "key = " + str2);
        }
        // 打印所有的方法：
        Method[] methodArr = cls.getDeclaredMethods();
        int len = methodArr == null ? 0 : methodArr.length;
        for (int i = 0; i < len; i++) {
            Method curMethod = methodArr[i];
            String name = curMethod.getName();
            Log.e("fgtian", "METHOD NAME = " + name);
            Class<?>[] paramTypes = curMethod.getParameterTypes();
            if (null != paramTypes) {
                for (int j = 0; j < paramTypes.length; j++) {
                    Class<?> p = paramTypes[j];
                    String pName = p.getName();
                    Log.e("fgtian", "\tPARAM NAME = " + pName);
                }
            }
        }
    }

    public static Uri getSMSRingtoneForHuaWeiC8813(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        // String[] toneKey = { "ringtone", "ringtone2", "alarm_alert",
        // "notification_sound", "message", "email", "calendar", "lock",
        // "unlock", "effect_tick" };
        String[] toneKeyPath = {"ringtone_path", "ringtone2_path",
                "alarm_alert_path", "notification_sound_path", "message_path",
                "email_path", "calendar_path", "lock_path", "unlock_path",
                "effect_tick_path"};
        String className = "com.huawei.android.provider.SettingsEx$Systemex";
        String methodName = "getString";
        int index = 4;
        Class<?>[] methodParams = new Class[]{ContentResolver.class,
                String.class};
        Class<?> cls = Class.forName(className);
        Method getString = cls.getMethod(methodName, methodParams);
        // String key = (String)getString.invoke(cls, new Object[]
        // {c.getContentResolver(), toneKey[index]});
        String path = (String) getString.invoke(cls,
                c.getContentResolver(), toneKeyPath[index]);
        // return key;
        if (null == path)
            return null;
        return Uri.parse(path);
    }

    public static void setSMSRingtoneForHuaWeiC8813(Context c, Uri uri)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        String[] toneKey = {"ringtone", "ringtone2", "alarm_alert",
                "notification_sound", "message", "email", "calendar", "lock",
                "unlock", "effect_tick"};
        String[] toneKeyPath = {"ringtone_path", "ringtone2_path",
                "alarm_alert_path", "notification_sound_path", "message_path",
                "email_path", "calendar_path", "lock_path", "unlock_path",
                "effect_tick_path"};
        int index = 4;
        String className = "com.huawei.android.provider.SettingsEx$Systemex";
        String methodName = "putString";
        Class<?>[] methodParams = new Class<?>[]{ContentResolver.class,
                String.class, String.class};
        Class<?> cls = Class.forName(className);
        Method putString = cls.getMethod(methodName, methodParams);
        putString.invoke(cls, c.getContentResolver(),
                toneKey[index], uri.toString());
        putString.invoke(cls, c.getContentResolver(),
                toneKeyPath[index], uri.toString());
    }

    public static Uri getNotificationForHuaWeiC8816Enhanced(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        // String[] toneKey = { "ringtone", "ringtone2", "alarm_alert",
        // "notification_sound", "message", "email", "calendar", "lock",
        // "unlock", "effect_tick" };
        String[] toneKeyPath = {"ringtone_path", "ringtone2_path",
                "alarm_alert_path", "notification_sound_path", "message_path",
                "email_path", "calendar_path", "lock_path", "unlock_path",
                "effect_tick_path"};
        String className = "android.provider.Settings$System";
        String methodName = "getString";
        int index = 4;
        Class<?>[] methodParams = new Class[]{ContentResolver.class,
                String.class};
        Class<?> cls = Class.forName(className);
        Method getString = cls.getMethod(methodName, methodParams);
        // String key = (String)getString.invoke(cls, new Object[]
        // {c.getContentResolver(), toneKey[index]});
        String path = (String) getString.invoke(cls,
                c.getContentResolver(), toneKeyPath[index]);

        if (null == path)
            return null;
        return Uri.parse(path);
    }

    public static void setNotificationForHuaWeiC8816Enhanced(Context c, Uri uri, String path)
            throws ClassNotFoundException,
            SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        // String[] toneKey = { "ringtone", "ringtone2", "alarm_alert",
        // "notification_sound", "message", "email", "calendar"};
        // String[] toneKeyPath = { "ringtone_path", "ringtone2_path",
        // "alarm_alert_path", "notification_sound_path", "message_path",
        // "email_path", "calendar_path", "lock_path", "unlock_path",
        // "effect_tick_path" };
        String className = "android.provider.Settings$System";
        Class<?> cls = Class.forName(className);

        String methodName = "putString";
        Class<?>[] methodParams = new Class[]{ContentResolver.class,
                String.class, String.class};
        Method method = cls.getMethod(methodName, methodParams);
        // int index = 4;
        // String path = uri.getPath();
        method.invoke(cls, c.getContentResolver(), "message",
                uri.toString());
        method.invoke(cls, c.getContentResolver(),
                "message_path", uri.toString());
    }

    // 这里的TYPE_NOTIFICATION 是指的其他类型的通知音
    public static Uri getSMSRingtoneForHuaWeiC8816(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        String className = "android.media.RingtoneManager";
        Class<?> cls = Class.forName(className);
        String methodName = "getActualDefaultRingtoneUri";
        Class<?>[] methodParams = new Class<?>[]{Context.class, int.class};
        Method method = cls.getMethod(methodName, methodParams);
        int TYPE_NOTIFICATION = 2;
        Uri uri = (Uri) method.invoke(cls,
                c, TYPE_NOTIFICATION);

        return uri;
    }

    // 这里的TYPE_NOTIFICATION 是指的其他类型的通知音
    public static void setNotificationForHuaWeiC8816(Context context, Uri uri)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        String className = "android.media.RingtoneManager";
        Class<?> cls = Class.forName(className);
        String methodName = "setActualDefaultRingtoneUri";
        Class<?>[] methodParams = new Class<?>[]{Context.class, int.class,
                Uri.class};
        Method method = cls.getMethod(methodName, methodParams);
        int TYPE_NOTIFICATION = 2;
        method.invoke(cls, context, TYPE_NOTIFICATION, uri);
    }

    public static Uri getSMSRingtoneForHuaWeiP6(Context c)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        // String[] toneKey = { "ringtone", "ringtone2", "alarm_alert",
        // "notification_sound", "message", "email", "calendar"};
        String[] toneKeyPath = {"ringtone_path", "ringtone2_path",
                "alarm_alert_path", "notification_sound_path", "message_path",
                "email_path", "calendar_path"};
        String className = "android.provider.Settings$System";
        String methodName = "getString";
        int index = 4;
        Class<?>[] methodParams = new Class[]{ContentResolver.class,
                String.class};
        Class<?> cls = Class.forName(className);
        Method getString = cls.getMethod(methodName, methodParams);
        // String key = (String)getString.invoke(cls, new Object[]
        // {c.getContentResolver(), toneKey[index]});
        String path = (String) getString.invoke(cls,
                c.getContentResolver(), toneKeyPath[index]);
        // return key;
        if (null == path)
            return null;
        return Uri.parse(path);
    }

    public static void setSMSRingtoneForHuaWeiP6(Context c, Uri uri)
            throws ClassNotFoundException, SecurityException,
            NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        String[] toneKey = {"ringtone", "ringtone2", "alarm_alert",
                "notification_sound", "message", "email", "calendar"};
        String[] toneKeyPath = {"ringtone_path", "ringtone2_path",
                "alarm_alert_path", "notification_sound_path", "message_path",
                "email_path", "calendar_path"};
        int index = 4;

        String className = "android.provider.Settings$System";
        String methodName = "putString";
        Class<?>[] methodParams = new Class[]{ContentResolver.class,
                String.class, String.class};
        Class<?> cls = Class.forName(className);
        Method putString = cls.getMethod(methodName, methodParams);
        putString.invoke(cls, c.getContentResolver(),
                toneKey[index], uri.toString());
        putString.invoke(cls, c.getContentResolver(),
                toneKeyPath[index], uri.toString());
    }

    // ------------------- 摩托ME860 ------------------- //
    public static void setAlarmAlertForMOTOROLA(Context c, Uri uri) {
        setAlarmAlertNormal(c, uri,
                "content://com.motorola.blur.alarmclock/alarm", "_id", "alert");
    }

    // ------------------- vivo ------------------- //
    public static void setSMSForVivo(Context c, Uri uri) {
        Settings.System.putString(c.getContentResolver(),
                "message_sound", uri.toString());
        Settings.System.putString(c.getContentResolver(),
                "message_sound_sim2", uri.toString());
    }

    public static Uri getSMSForVivo(Context c) {
        String url = Settings.System.getString(
                c.getContentResolver(), "message_sound");
        if (null == url) {
            url = Settings.System.getString(
                    c.getContentResolver(), "message_sound_sim2");
            if (null == url) {
                return null;
            }
        }
        return Uri.parse(url);
    }

    public static Uri getSMSChuiZi(Context c) {
        String str = Settings.System.getString(c.getContentResolver(), "mms_ringtone_uri");
        if (str == null)
            return null;
        return Uri.parse(str);
    }

    //乐视短信铃声适配：
    public static void setSMSForLeTv(Context c, Uri uri) {
        try {
            String className = "android.media.RingtoneManager";
            Class<?> cls = Class.forName(className);
            String methodName = "setActualDefaultRingtoneUri";
            Class<?>[] methodParams = new Class<?>[]{Context.class, int.class, Uri.class};
            Method method = cls.getMethod(methodName, methodParams);
            method.invoke(cls, c, 32, uri);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static Uri getSMSForLeTv(Context c) {
        Uri uri = null;
        try {
            String className = "android.media.RingtoneManager";
            Class<?> cls = Class.forName(className);
            String methodName = "getActualDefaultRingtoneUri";
            Class<?>[] methodParams = new Class<?>[]{Context.class, int.class};
            Method method = cls.getMethod(methodName, methodParams);
            uri = (Uri) method.invoke(cls, c, 32);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return uri;
    }


    public static void setAlarmForChuiZiM1(Context c, Uri uri, String path) {
        List<String> idList = new ArrayList<String>();
        //        String[] idProjection = new String[]{"_id","alert","sourceid","type"};
        Uri alertUri = Uri.parse("content://com.smartisanos.clock.alarm/alarm");
        Cursor cursor = c.getContentResolver().query(alertUri, null,
                null, null, null);
        if (null != cursor) {
            while (cursor.moveToNext()) {
                String id = cursor.getString(cursor.getColumnIndex("_id"));
                idList.add(id);
            }
            cursor.close();
        }
        if (idList.isEmpty()) {
            return;
        }
        String alert = uri.toString();
        ContentValues cv = new ContentValues(1);
        cv.put("alert", alert);
        cv.put("alertname", path);
        ContentResolver cr = c.getContentResolver();
        int updateRows = 0;
        while (!idList.isEmpty()) {
            String id = idList.remove(0);
            int row = cr.update(ContentUris.withAppendedId(alertUri, Long.valueOf(id)), cv,
                    null, null);
            updateRows += row;
        }
        RingtoneManager.setActualDefaultRingtoneUri(c, 4, uri);
    }

    public static void setSMSChuiZi(Context c, Uri uri, String path) {
        Settings.System.getString(c.getContentResolver(), "alarm_alert");

        try {
            String className = "smartisanos.api.ContactsContractSmtImpl";
            Class<?> cls = Class.forName(className);
            String methodName = "get_BASE_URI";
            Class<?>[] methodParams = new Class<?>[]{};
            Method method = cls.getMethod(methodName, methodParams);
            method.invoke(cls, new Object[]{});
        } catch (Throwable e) {
            e.printStackTrace();
        }

        PreferenceManager.getDefaultSharedPreferences(c).getString("configuration", "");
        c.getSharedPreferences("tqt_default_sp", 0);
        //        SharedPreferences.Editor editor = c.getSharedPreferences("AlarmRingtone",Context.MODE_PRIVATE).edit();
        //        editor.putString("ringtone", path);
        //        editor.apply();


        c.getSharedPreferences("AlarmRingtone", 0).getString("AlarmRingtone", null);
        c.getSharedPreferences("AlarmClock", Context.MODE_PRIVATE).getString("AlarmRingtone", " ");
        //
        //        try{
        //            String className = "android.media.RingtoneManager";
        //            Class<?> cls = Class.forName(className);
        //            String methodName = "setActualDefaultRingtoneUri";
        //            Class<?>[] classParams = new Class<?>[]{Context.class,int.class,Uri.class};
        //            Method method = cls.getMethod(methodName,classParams);
        //
        //            for (int i =0 ; i<= 2048; i++){
        //                method.invoke(cls,c,i,uri);
        //            }
        //        }catch (Throwable e){
        //            e.printStackTrace();
        //        }
        //        try{
        //            String className = "android.media.RingtoneManager";
        //            Class<?> cls = Class.forName(className);
        //            String methodName = "getDefaultUri";
        //            Class<?>[] classParams = new Class<?>[]{int.class};
        //            Method method = cls.getDeclaredMethod(methodName,classParams);
        //            method.invoke(cls,4);
        //
        //        }catch (Throwable e){
        //            e.printStackTrace();
        //        }

        //        try{
        //            String className = "android.os.SystemProperties";
        //            Class<?> cls = Class.forName(className);
        //            String methodName = "get";
        //            Class<?>[] classParams = new Class<?>[]{String.class};
        //            Method method = cls.getMethod(methodName,classParams);
        //            method.invoke(cls,"ro.config.notification_sound");
        //
        //        }catch (Throwable e){
        //            e.printStackTrace();
        //        }
    }

    //不可用，SharedPreferences私有访问
    public static Uri getAlarmOppoR9S(Context c) {
        SharedPreferences mSharePre = c.getSharedPreferences("shared_prefs_alarm_app", 0);
        String uriString = mSharePre.getString("set_alram_ringtone", null);
        try {
            if (uriString != null) {
                Uri alarmUri = Uri.parse(uriString);
                return alarmUri;
            }
        } catch (Exception e) {

        }
        return null;
    }

    //不可用，SharedPreferences私有访问
    public static void setAlarmOppoR9s(Context c, Uri uri) {
        SharedPreferences mSharePre = c.getSharedPreferences("shared_prefs_alarm_app", 0);
        SharedPreferences.Editor localEditor = mSharePre.edit();
        localEditor.putString("set_alram_ringtone", uri.toString());
        localEditor.commit();
    }

    public static void setAlarmOppoX9007(Context c, Uri uri) throws ClassNotFoundException, SecurityException,
            IllegalArgumentException, IllegalAccessException,
            InvocationTargetException {
        String className = "android.provider.Settings$System";
        Class<?> cls = Class.forName(className);
        String methodName = "putString";
        Class<?>[] methodParams = new Class<?>[]{ContentResolver.class,
                String.class, String.class};
        String str = uri.toString();
        if (null == str || str.trim().equalsIgnoreCase("")) {
            return;
        }
        try {
            Method method = cls.getMethod(methodName, methodParams);
            method.invoke(cls, c.getContentResolver(),
                    "oppo_default_alarm", uri.toString());

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    public static void setSmsForNubia(Context context, Uri ringtoneUri) {
        Settings.System.putString(context.getContentResolver(), "message_sound", ringtoneUri.toString());
    }

    public static void setringtoneFor360(Context context, Uri ringtoneUri) {
        Settings.System.putString(context.getContentResolver(), "ringtone_2", ringtoneUri.toString());
    }

    public static void setSmsForChuizi(Context context, Uri ringtoneUri) {
        Settings.System.putString(context.getContentResolver(), "mms_ringtone_uri", ringtoneUri.toString());
        Settings.System.putString(context.getContentResolver(), "mms_ringtone_uri_2", ringtoneUri.toString());
    }

    public static Uri getSmsForNubia(Context context) {
        return Uri.parse(Settings.System.getString(context.getContentResolver(), "message_sound"));
    }
}
