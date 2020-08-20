package com.lijangop.sdk.utils.ring;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDiskIOException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.lijangop.sdk.utils.ring.internal.FileUtils;
import com.lijangop.sdk.utils.ring.internal.ModelHelper;

import java.io.File;
import java.net.URLDecoder;

/**
 * 本类包括设置铃声的上层实现、获取当前铃声的开放接口、设置联系人来电的具体实现等
 *
 * @author yudeng
 */
public class RingtoneManagerStanderd implements RingtoneManagerInterface {

    private static String[] RingtoneType = {"TYPE_RINGTONE", // SIM卡1
            "TYPE_RINGTONE2", // SIM卡2，因机型不同，可能有多个
            "TYPE_RINGTONE_2", "TYPE_RINGTONE_SIM2", "TYPE_RINGTONE_2G" // 联想K900
    };
    private static String[] SmsType      = {
            "TYPE_NOTIFICATION", // 标准SIM卡1
            "TYPE_SMS_NOTIFI", // Lenovo A750 使用
            "TYPE_NOTIFICATION2", // SIM卡2，非标准，因机型不同，可能有多个
            "TYPE_NOTIFICATION_2", "TYPE_NOTIFICATION_SIM2", "TYPE_MESSAGE",
            "TYPE_MESSAGE2", "TYPE_MESSAGE_2", "TYPE_NOTIFICATION_2G" // 联想K900
    };
    private static String[] AlarmType    = {"TYPE_ALARM", // SIM卡1
            "TYPE_ALARM2", // SIM卡2，因机型不同，可能有多个
            "TYPE_ALARM_2", "TYPE_ALARM_SIM2"};

    private static String[] NotificationType = {
            "TYPE_NOTIFICATION", // 标准SIM卡1
            "TYPE_SMS_NOTIFI", // Lenovo A750 使用
            "TYPE_NOTIFICATION2", // SIM卡2，非标准，因机型不同，可能有多个
            "TYPE_NOTIFICATION_2", "TYPE_NOTIFICATION_SIM2", "TYPE_MESSAGE",
            "TYPE_MESSAGE2", "TYPE_MESSAGE_2", "TYPE_NOTIFICATION_2G" // 联想K900
    };

    public String[] getRingtoneType() {
        return RingtoneType;
    }

    public String[] getSmsType() {
        if (ModelHelper.isNewVIVO()) {
            SmsType = new String[]{
                    //            "TYPE_NOTIFICATION", // 标准SIM卡1
                    "TYPE_SMS_NOTIFI", // Lenovo A750 使用
                    "TYPE_NOTIFICATION2", // SIM卡2，非标准，因机型不同，可能有多个
                    "TYPE_NOTIFICATION_2", "TYPE_NOTIFICATION_SIM2", "TYPE_MESSAGE",
                    "TYPE_MESSAGE2", "TYPE_MESSAGE_2", "TYPE_NOTIFICATION_2G" // 联想K900
            };
        }
        return SmsType;
    }

    public String[] getAlarmType() {
        return AlarmType;
    }

    public String[] getNotificationType() {
        if (ModelHelper.isNewVIVO()) {
            NotificationType = new String[]{
                    "TYPE_NOTIFICATION", // 标准SIM卡1
                    "TYPE_SMS_NOTIFI", // Lenovo A750 使用
                    "TYPE_NOTIFICATION2", // SIM卡2，非标准，因机型不同，可能有多个
                    "TYPE_NOTIFICATION_2", "TYPE_NOTIFICATION_SIM2", /*"TYPE_MESSAGE",*/
                    "TYPE_MESSAGE2", "TYPE_MESSAGE_2", "TYPE_NOTIFICATION_2G" // 联想K900
            };
        }
        return NotificationType;
    }

    /**
     * 设置默认铃声的通用方法
     *
     * @param context  用于调用系统API的Context对象
     * @param typeList 类型名称列表
     */
    protected int setDefaultRing(Context context, String[] typeList) {
        if (!checkAndSetPermission(context)) {
            return SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED;
        }
        try {
            int typeId = getTypeIdByReflect(typeList[0]);
            Uri defUri = RingtoneManager.getDefaultUri(typeId);
            setRingByType(context, typeList, defUri);
        } catch (Exception e) {
            return SET_RESULT_FAILED;
        }
        return SET_RESULT_SUCCESS;
    }

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private boolean checkAndSetPermission(final Context context) {
        if (!PermissionUtil.hasWriteSettingPermission(context)) {
            // 跳转到设置WRITE_SETTINGS权限界面
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(context, "允许修改系统设置,铃声才能设置成功!", Toast.LENGTH_SHORT).show();
                }
            });
            PermissionUtil.startSetings(context);
            return false;
        }
        return true;
    }

    /**
     * 设置铃声的通用方法
     *
     * @param context      用于调用系统API的Context对象
     * @param ringtoneType 来电铃声类型名称列表，传null则不设置
     * @param smsType      短信铃声类型名称列表，传null则不设置
     * @param alarmType    闹钟铃声类型名称列表，传null则不设置
     * @param path         铃声路径
     * @param name         铃声名称
     * @return 设置结果 {@link #SET_RESULT_FAILED {@link #SET_RESULT_SUCCESS}
     * {@link #SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED}}
     */
    protected int setRing(Context context, String[] ringtoneType,
                          String[] smsType, String[] alarmType, String[] notificationType,
                          String path,
                          String name, boolean copy2SysDir) {
        if (!checkAndSetPermission(context)) {
            return SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED;
        }
        try {
            // 统一sdcard路径为/mnt/sdcard
            path = path.replaceFirst("^/sdcard", Environment
                    .getExternalStorageDirectory().getAbsolutePath());
            if (TextUtils.isEmpty(path))
                return SET_RESULT_FAILED;

            File file = new File(path);
            if (!file.exists() || !file.isFile())
                return SET_RESULT_FAILED;

            if (copy2SysDir) {//是否将待设置的铃音移动到系统铃声文件夹下再设置  防止被一些清理软件误清理
                path = moveRing2SysRingDir(path, name, file);
            }

            boolean updatedAudio = false;
            Cursor cursor = null;
            try {
                boolean isRingtone = (ringtoneType != null);
                boolean isSms = (smsType != null);
                boolean isAlarm = (alarmType != null);
                boolean isNotification = (notificationType != null);

                // 下面为了获取媒体文件的Uri
                ContentValues contentValues = new ContentValues();
                Uri ringtoneUri = null;
                ContentResolver cr = context.getContentResolver();
                cursor = cr.query(ContentUri, null, MediaStore.Audio.Media.DATA
                        + "=?", new String[]{path}, null);
                if (cursor.moveToFirst() && cursor.getCount() > 0) {
                    // 媒体文件已经被系统媒体库收录，只需要更新相关的字段
                    String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    contentValues.put(MediaStore.Audio.Media.TITLE, name);
                    contentValues.put(MediaStore.Audio.Media.IS_RINGTONE,
                            isRingtone ? 1 : 0);// 设置来电铃声为true
                    contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION,
                            isSms | isNotification ? 1 : 0);// 设置通知铃声为true
                    contentValues.put(MediaStore.Audio.Media.IS_ALARM, isAlarm ? 1 : 0);
                    contentValues.put(MediaStore.Audio.Media.IS_MUSIC, 1);

                    //					int drmIndex = cursor.getColumnIndex("is_drm");
                    //					if (drmIndex >= 0) {
                    //						contentValues.put("is_drm", 0);
                    //					}

                    // 把需要设为铃声的歌曲更新铃声库
                    cr.update(ContentUri, contentValues,
                            MediaStore.Audio.Media.DATA + "=?",
                            new String[]{path});
                    ringtoneUri = ContentUris.withAppendedId(ContentUri,
                            Long.valueOf(id));
                } else {
                    // 媒体文件未被系统媒体库收录，必须将其添加进系统媒体库中
                    contentValues.put(MediaStore.Audio.Media.DATA, path);
                    contentValues.put(MediaStore.Audio.Media.TITLE, name);
                    contentValues.put(MediaStore.Audio.Media.DISPLAY_NAME, name);
                    contentValues.put(MediaStore.Audio.Media.SIZE,
                            file.length());
                    String mimetype = FileUtils.getMimeType(file);
                    contentValues.put(MediaStore.Audio.Media.MIME_TYPE,
                            mimetype);
                    contentValues.put(MediaStore.Audio.Media.IS_RINGTONE,
                            isRingtone ? 1 : 0);// 设置来电铃声为true
                    contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION,
                            isSms | isNotification ? 1 : 0);// 设置通知铃声为true
                    contentValues.put(MediaStore.Audio.Media.IS_ALARM, isAlarm ? 1 : 0);
                    contentValues.put(MediaStore.Audio.Media.IS_MUSIC, 1);

                    //vivo的手机midea数据库中设置了铃声之后该字段有值为0  但不设置该字段铃声也能设置成功
                    //	int drmIndex = cursor.getColumnIndex("is_drm");
                    //	if (drmIndex >= 0) {
                    //		contentValues.put("is_drm", 0);
                    //	}

                    ringtoneUri = cr.insert(ContentUri, contentValues);
                }
                updatedAudio = true;
                // 设置铃声
                if (isRingtone) {
                    if (ModelHelper.isOPhone()) {
                        try {
                            RingtoneManagerEnhanced.setRingForOPhone(cr,
                                    ringtoneUri);
                        } catch (Exception ex) {
                            setRingByType(context, ringtoneType, ringtoneUri);
                        }
                    } else if (ModelHelper.isCoolPad()) {
                        try {
                            if (Build.MODEL.equals("Coolpad T2-00")
                                    || Build.MODEL.equals("8681-M02")) { //奇酷青春版
                                setRingByType(context, ringtoneType, ringtoneUri);
                            } else if (Build.VERSION.SDK_INT >= 21) {
                                RingtoneManagerEnhanced
                                        .setRingtoneForCoolPadNew(context, path);
                            } else {
                                RingtoneManagerEnhanced.setRingtoneForCoolPad(
                                        context, ringtoneUri);
                            }
                        } catch (Exception ex) {
                            setRingByType(context, ringtoneType, ringtoneUri);
                        }
                    } else if (ModelHelper.is360()) {
                        RingtoneManagerEnhanced.setringtoneFor360(context, ringtoneUri);
                        setRingByType(context, ringtoneType, ringtoneUri);
                    } else if (ModelHelper.isVIVO()) {
                        try {
                            setRingByType(context, ringtoneType, ringtoneUri);
                            RingtoneManagerEnhanced.setRingToneForVivoY66OrY67(context, ringtoneUri);
                        } catch (Exception e) {
                            setRingByType(context, ringtoneType, ringtoneUri);
                        }

                    } else {
                        setRingByType(context, ringtoneType, ringtoneUri);
                    }
                }
                if (isSms) {
                    if (ModelHelper.isOPhone()) {
                        try {
                            RingtoneManagerEnhanced.setNotificationForOPhone(
                                    context, ringtoneUri);
                        } catch (Exception ex) {
                            setRingByType(context, smsType,
                                    ringtoneUri);
                        }
                    } else if (ModelHelper.isXIAOMI()) {
                        try {
                            RingtoneManagerEnhanced.setNotificationForXiaoMi(
                                    context, ringtoneUri);
                        } catch (Exception ex) {
                            setRingByType(context, smsType,
                                    ringtoneUri);
                        }
                    } else if (ModelHelper.isHuaWeiC8813()) {
                        try {
                            RingtoneManagerEnhanced
                                    .setSMSRingtoneForHuaWeiC8813(context,
                                            ringtoneUri);
                        } catch (Exception ex) {
                            setRingByType(context, smsType,
                                    ringtoneUri);
                        }
                    } else if (ModelHelper.isHuaWeiSpecial()) {
                        try {
                            RingtoneManagerEnhanced
                                    .setNotificationForHuaWeiC8816Enhanced(
                                            context, ringtoneUri, path);
                        } catch (Exception e) {
                            setRingByType(context, smsType,
                                    ringtoneUri);
                        }
                    } else if (ModelHelper.isCoolPad()) {
                        try {
                            RingtoneManagerEnhanced
                                    .setSMSRingtoneForCoolPadNew(context,
                                            ringtoneUri);
                            RingtoneManagerEnhanced.setSMSRingtoneForCoolPad(
                                    context, ringtoneUri);
                        } catch (Exception ex) {
                            setRingByType(context, smsType,
                                    ringtoneUri);
                        }
                    } else if (ModelHelper.isMeizuSpecial()) {
                        try {
                            //							setRingByType(context, smsType,
                            //									ringtoneUri);
                            RingtoneManager.setActualDefaultRingtoneUri(
                                    context, 8, ringtoneUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (ModelHelper.isHuaWeiP6()) {
                        RingtoneManagerEnhanced.setSMSRingtoneForHuaWeiP6(
                                context, ringtoneUri);
                    } else if (ModelHelper.isVIVO() && !ModelHelper.isNewVIVO()) {
                        RingtoneManagerEnhanced.setSMSForVivo(context,
                                ringtoneUri);
                    } else if (ModelHelper.isSamSung()) {
                        try { // 先按照一下特殊的机型的方式设置
                            RingtoneManagerEnhanced.setNotificationForSamsung(
                                    context, ringtoneUri);
                            setRingByType(context, smsType,
                                    ringtoneUri);
                        } catch (Exception e) { // 如果没有 则按默认方式设置
                            e.printStackTrace();
                        }
                    } else if (ModelHelper.isMeiZu()) {
                        RingtoneManagerEnhanced.setNotificationForMeiZu(
                                context, ringtoneUri);
                        //						setRingByType(context, smsType, ringtoneUri);
                    } else if (ModelHelper.isOppo()) {
                        Log.e("liangma", "对oppo手机设置短信音");
                        //						setRingByType(context, smsType, ringtoneUri);
                        RingtoneManagerEnhanced.setNotificationForOppo(context,
                                ringtoneUri);
                    } else if (ModelHelper.isGionee()) {
                        //设置卡1和卡2的短信铃声
                        RingtoneManagerEnhanced.setRingtoneForGionee(context, 32, ringtoneUri);
                        RingtoneManagerEnhanced.setRingtoneForGionee(context, 64, ringtoneUri);
                    } else if (ModelHelper.isChuiZi()) {
                        RingtoneManagerEnhanced.setSmsForChuizi(context, ringtoneUri);
                        //                        setRingByType(context, smsType, ringtoneUri);
                    } else if (ModelHelper.isLeTv()) {
                        RingtoneManagerEnhanced.setSMSForLeTv(context, ringtoneUri);

                    } else if (ModelHelper.isNubiaZ18mini()) {
                        RingtoneManagerEnhanced.setSmsForNubia(context, ringtoneUri);
                    } else {
                        setRingByType(context, smsType, ringtoneUri);
                    }
                }
                if (isAlarm) {
                    if (ModelHelper.isOPhone()) {
                        try {
                            RingtoneManagerEnhanced.setAlarmAlertForOPhone(
                                    context, ringtoneUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (ModelHelper.isXIAOMI()) {
                        try {
                            RingtoneManagerEnhanced.setAlarmAlert(context, ringtoneUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            RingtoneManagerEnhanced.setAlarmAlertForXiaoMiV6(context, ringtoneUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        // 同时设置为默认
                    } else if (ModelHelper.isSamSung()) {
                        try {
                            RingtoneManagerEnhanced.setAlarmAlertForSamSung(
                                    context, ringtoneUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (ModelHelper.isHTC()) {
                        try {
                            RingtoneManagerEnhanced.setAlarmAlertForHtc(context, ringtoneUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (ModelHelper.isCoolPad()) {
                        try {
                            RingtoneManagerEnhanced.setAlarmAlertForCoolPad(context, ringtoneUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (ModelHelper.isMotorola()) {
                        try {
                            RingtoneManagerEnhanced.setAlarmAlertForMOTOROLA(
                                    context, ringtoneUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (ModelHelper.isViVoY67() || ModelHelper.isVivoX()) {
                        try {
                            RingtoneManagerEnhanced.setAlarmAlertForVivoX(
                                    context, ringtoneUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (ModelHelper.isOppoX9007() || ModelHelper.isNewOppo()) {
                        RingtoneManagerEnhanced.setAlarmOppoX9007(context, ringtoneUri);
                    } else if (ModelHelper.isChuiZi()) {
                        RingtoneManagerEnhanced.setAlarmForChuiZiM1(context, ringtoneUri, path);
                    } else {
                        try {
                            RingtoneManagerEnhanced.setAlarmAlert(context,
                                    ringtoneUri);
                        } catch (Exception e) {
                            Log.e("fgtian", "型号" + Build.MODEL
                                    + "不能按照一般的方式设置闹铃");
                        }
                    }
                    setRingByType(context, alarmType, ringtoneUri);
                }
                if (isNotification) {//通知音
                    setRingByType(context, notificationType, ringtoneUri);
                }
                cursor.close();
            } catch (SQLiteDiskIOException ex) {
                ex.printStackTrace();
                cursor.close();
                if (!updatedAudio) {
                    return SET_RESULT_FAILED;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                cursor.close();
                if (!updatedAudio) {
                    return SET_RESULT_FAILED;
                }
            }
            return SET_RESULT_SUCCESS;
        } catch (Throwable e) {
            e.printStackTrace();
            return SET_RESULT_FAILED;
        }
    }

    /**
     * 将铃音移动到系统铃声目录下并取得新的路径
     *
     * @param path
     * @param name
     * @param file
     * @return
     */
    private String moveRing2SysRingDir(String path, String name, File file) {
        File ringtoneDirFile = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_RINGTONES);
        if (ringtoneDirFile.exists() && ringtoneDirFile.isDirectory()) {//ringtones文件夹已经存在
            String ringtoneDirPath = ringtoneDirFile.getAbsolutePath();
            //如果路径中不包含公共铃音目录（不在铃音目录下）
            if (!path.contains(ringtoneDirPath)) {
                File targetFile = new File(ringtoneDirPath, name);
                if ((!targetFile.exists() || !targetFile.isFile() || targetFile.length() <= 0)) {
                    //如果文件不存在
                    if (FileUtils.copyFile(file, new File(ringtoneDirPath, name))) {
                        path = ringtoneDirPath + File.separator + name;
                    }
                } else {
                    path = targetFile.getAbsolutePath();
                }
            } else {//设置的文件路径在公共铃音目录下
                //do nothing
            }
        } else {//如果系统的铃声文件夹还未创建  先创建 在copy
            if (ringtoneDirFile.mkdirs()) {
                String ringtoneDirPath = ringtoneDirFile.getAbsolutePath();
                if (FileUtils.copyFile(file, new File(ringtoneDirPath, name))) {
                    path = ringtoneDirPath + File.separator + name;
                }
            }
        }
        return path;
    }

    public Uri getRingUri(Context context, String path) {
        // 统一sdcard路径为/mnt/sdcard
        if (null == path)
            return null;
        path = path.replaceFirst("^/sdcard", Environment
                .getExternalStorageDirectory().getAbsolutePath());

        File file = new File(path);
        if (!file.exists() || !file.isFile())
            return null;

        Uri uri = null;
        Cursor cursor = context.getContentResolver()
                .query(ContentUri, null, MediaStore.Audio.Media.DATA + "=?",
                        new String[]{path}, null);
        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            // 媒体文件已经被系统媒体库收录，获取其id
            String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
            uri = ContentUris.withAppendedId(ContentUri, Long.valueOf(id));
        }
        return uri;
    }

    /**
     * 检查铃声是否被设置的通用方法
     *
     * @param context
     *            用于调用系统API的Context对象
     * @param type
     *            铃声类型
     * @param path
     *            铃声文件路径
     * @param name
     *            铃声名称
     * @return true, 铃声已被设置
     */
    //	protected boolean isRingApplyed(Context context, String type, String path,
    //			String name) {
    //		if (null == context) {
    //			return false;
    //		}
    //		boolean isApplyed = false;
    //		int typeId = getTypeIdByReflect(type);
    //		Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context,
    //				typeId);
    //		if (null == ringtoneUri) {
    //			return false;
    //		}
    //		Uri checkUri = getRingUri(context, path);
    //		if (null == checkUri) {
    //			return false;
    //		}
    //		if (ringtoneUri.equals(checkUri))
    //			isApplyed = true;
    //		return isApplyed;
    //	}

    /**
     * 根据Type列表设置铃声
     *
     * @param context  用于调用系统API的Context对象
     * @param typelist Type列表
     * @param uri      铃声的Uri
     */
    protected void setRingByType(Context context, String[] typelist, Uri uri) {
        for (String type : typelist) {
            int typeId = getTypeIdByReflect(type);
            if (typeId != -1) {
                RingtoneManager.setActualDefaultRingtoneUri(context, typeId, uri);
            }
        }
    }

    /**
     * 使用反射机制获取Type的值
     *
     * @param type
     * @return Type值，如果为-1，代表不存在该Type
     */
    protected int getTypeIdByReflect(String type) {
        Class<RingtoneManager> ringtone = RingtoneManager.class;
        int typeId = -1;
        try {
            typeId = ringtone.getField(type).getInt(null);
        } catch (Exception e) {
            typeId = -1;
        }
        return typeId;
    }

    @Override
    public int setAllRingAudio(Context context, String ringPath,
                               String ringName, String alarmPath, String alarmName,
                               String notiPath, String notiName) {
        int ringResult = setRingtone(context, ringPath, ringName, false);
        int alarmResult = setAlarm(context, alarmPath, alarmName, false);
        int notiResult = setSms(context, notiPath, notiPath, false);
        if (SET_RESULT_SUCCESS == ringResult && SET_RESULT_SUCCESS == alarmResult &&
                SET_RESULT_SUCCESS == notiResult) {
            return SET_RESULT_SUCCESS;
        } else if (SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED == ringResult) {
            return SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED;
        } else {
            return SET_RESULT_FAILED;
        }
    }

    @Override
    public int removeAllAudioSettings(Context context) {
        int alarmResult = setDefaultAlarm(context);
        int notiResult = setDefaultNotification(context);
        int ringResult = setDefaultRingtone(context);
        if (SET_RESULT_SUCCESS == ringResult && SET_RESULT_SUCCESS == alarmResult &&
                SET_RESULT_SUCCESS == notiResult) {
            return SET_RESULT_SUCCESS;
        } else if (SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED == ringResult) {
            return SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED;
        } else {
            return SET_RESULT_FAILED;
        }
    }

    @Override
    public int setDefaultRingtone(Context context) {
        return setDefaultRing(context, getRingtoneType());
    }

    @Override
    public int setDefaultAlarm(Context context) {
        return setDefaultRing(context, getAlarmType());
    }

    @Override
    public int setDefaultNotification(Context context) {
        return setDefaultRing(context, getSmsType());
    }

    @Override
    public int setRingtone(Context context, String path, String name, boolean move2SysDir) {
        return setRing(context, getRingtoneType(), null, null, null, path, name, move2SysDir);
    }

    @Override
    public int setAlarm(Context context, String path, String name, boolean move2SysDir) {
        return setRing(context, null, null, getAlarmType(), null, path, name, move2SysDir);
    }

    @Override
    public int setSms(Context context, String path, String name, boolean move2SysDir) {
        return setRing(context, null, getSmsType(), null, getNotificationType(), path, name, move2SysDir);
    }

    @Override
    public int setNotification(Context context, String path, String name, boolean move2SysDir) {
        return setRing(context, null, null, null, getNotificationType(), path, name, move2SysDir);
    }

    //	@Override
    //	public boolean isNotificationApplyed(Context context, String path,
    //			String name) {
    //		return isRingApplyed(context, getSmsType()[0], path, name);
    //	}
    //
    //	@Override
    //	public boolean isRingtoneApplyed(Context context, String path, String name) {
    //		return isRingApplyed(context, getRingtoneType()[0], path, name);
    //	}
    //
    //	@Override
    //	public boolean isAlarmApplyed(Context context, String path, String name) {
    //		return isRingApplyed(context, getAlarmType()[0], path, name);
    //	}

    public boolean isInternal(Uri uri) {
        int index = uri.toString().indexOf("internal");
        return (index > 0);
    }

    /**
     * 获取手机当前来电铃声
     *
     * @param context
     * @return
     */
    public RingInfo getCurrentRingtone(Context context) {
        if (null == context || (context instanceof Activity && ((Activity) context).isFinishing()))
            return null;
        int typeId = getTypeIdByReflect(getRingtoneType()[0]);
        // 当前默认的来电铃声
        Uri ringtoneUri = null;
        if (ModelHelper.isOPhone()) {
            try {
                ringtoneUri = RingtoneManagerEnhanced
                        .getRingtoneForOPhone(context);
            } catch (Exception e) {
                ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                        context, typeId);
            }
        } else {
            ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context,
                    typeId);
        }
        if (ModelHelper.isCoolPad()) {
            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    return RingtoneManagerEnhanced
                            .getRingtoneForCoolPadNew(context);
                }
                return RingtoneManagerEnhanced.getRingtoneForCoolPad(context);
            } catch (Exception e) {
                return queryMediaSoundByUri(context, ringtoneUri);
                // return getCurrentRingtoneByUri(context, ringtoneUri);
            }
        } else {
            return queryMediaSoundByUri(context, ringtoneUri);
            // return getCurrentRingtoneByUri(context, ringtoneUri);
        }
    }

    private synchronized RingInfo queryMediaSoundByUri(Context context, Uri mediaUri) {
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = null;
        if (null == mediaUri || "".equals(mediaUri.toString().trim())) {
            return null;
        }
        if ("file".equals(mediaUri.getScheme()) || null == mediaUri.getScheme()) {
            // 小米的系统铃声uri的scheme是“file”
            String path = mediaUri.getEncodedPath(); // 华为的系统铃声的uri的scheme为null
            if (isEmptyOrBlack(path)) {
                return null;
            }
            path = Uri.decode(path);
            StringBuffer buff = new StringBuffer();
            buff.append("(").append(MediaStore.Audio.Media.DATA).append("=").append("'" + path + "'").append(")");
            cursor = resolver.query(MediaStore.Audio.Media.INTERNAL_CONTENT_URI, null, buff.toString(), null, null);
            int index = 0;
            int dataIdx = 0;
            if (null != cursor && cursor.moveToFirst()) {
                int count = cursor.getColumnCount();
                for (int i = 0; i < count; i++) {
                    String str = cursor.getString(i);
                    String name = cursor.getColumnName(i);
                    Log.e("Audio.Media", "index:" + i + "__name:" + name + "_value:" + str);
                }
                int columnIndex = 0;
                String title = "";
                if ((columnIndex = cursor
                        .getColumnIndex(MediaStore.Audio.Media.TITLE)) >= 0) {
                    title = cursor.getString(columnIndex);
                    columnIndex = -1;
                }
                if (null == title || "".equals(title.trim())) {
                    if ((columnIndex = cursor
                            .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)) >= 0) {
                        title = cursor.getString(columnIndex);
                    }
                }
                RingInfo ringInfo = new RingInfo();
                ringInfo.mPath = path;
                ringInfo.mName = title;
                cursor.close();
                return ringInfo;
            }
            //            path = Uri.decode(path);
            //            if (!isEmptyOrBlack(path)) {
            //                File file = new File(path);
            //                if (file.exists()) {
            //                    RingInfo info = new RingInfo();
            //                    info.mPath = path;
            //                    File f = new File(info.mPath);
            //                    String title = f.getName();
            //                    if (!isEmptyOrBlack(title) && title.contains(".")) {
            //                        title = title.substring(0, title.lastIndexOf("."));
            //                    }
            //                    info.mName = title;
            //                    return info;
            //                }
            //            }
            // cursor = resolver.query(ContentUri,
            // null,MediaStore.Audio.Media.DATA + "=?", new String[] { path },
            // null);
        } else {
            cursor = resolver.query(mediaUri, null, null, null, null);
        }
        try {
            if (null != cursor && cursor.moveToFirst()
                    && cursor.getColumnCount() > 0) {
                int columnIndex = -1;
                String path = null;
                String title = null;
                if ((columnIndex = cursor
                        .getColumnIndex(MediaStore.Audio.Media.DATA)) >= 0) {
                    path = cursor.getString(columnIndex);
                    columnIndex = -1;
                }
                if ((columnIndex = cursor
                        .getColumnIndex(MediaStore.Audio.Media.TITLE)) >= 0) {
                    title = cursor.getString(columnIndex);
                    columnIndex = -1;
                }
                if (null == title || "".equals(title.trim())) {
                    if ((columnIndex = cursor
                            .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)) >= 0) {
                        title = cursor.getString(columnIndex);
                    }
                }
                if (!isEmptyOrBlack(title) && title.contains(".")) {
                    title = title.substring(0, title.lastIndexOf("."));
                }
                int count = cursor.getColumnCount();
                for (int i = 0; i < count; i++) {
                    String str = cursor.getString(i);
                    String name = cursor.getColumnName(i);
                    Log.e("cursor遍历", "index:" + i + "__name:" + name + "_value:" + str);
                }
                RingInfo info = new RingInfo();
                info.mPath = path;
                info.mName = title;
                cursor.close();
                return info;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != cursor) {
                    cursor.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Deprecated
    private RingInfo getCurrentRingtoneByUri(Context context, Uri ringtoneUri) {
        if (null == ringtoneUri) {
            return null;
        }
        String uri = ringtoneUri.toString();
        String str = URLDecoder.decode(uri);
        int index = str.lastIndexOf("/");
        String lastStr = str.substring(index + 1, str.length());
        if ("file".equals(ringtoneUri.getScheme())) {
            RingInfo info = new RingInfo();
            info.mPath = ringtoneUri.getPath();
            File f = new File(info.mPath);
            info.mName = f.getName();
            return info;
        }
        if (isInternal(ringtoneUri)) {
            RingInfo info = new RingInfo();
            info.mName = lastStr;
            info.mPath = str;
            return info;
        }
        int id = -1;
        try {
            id = Integer.valueOf(lastStr);
        } catch (Exception e) {
            // 是系统铃声
            RingInfo info = new RingInfo();
            info.mName = lastStr;
            info.mPath = str;
            return info;
        }
        return getAudioInfo(context, (long) id);
    }

    /**
     * 获取手机当前闹铃
     *
     * @param context
     * @return
     */
    public RingInfo getCurrentAlarm(Context context) {
        if (null == context || (context instanceof Activity && ((Activity) context).isFinishing()))
            return null;
        int typeId = getTypeIdByReflect(getAlarmType()[0]);
        // 当前默认的闹铃铃音

        Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context,
                typeId);
        return queryMediaSoundByUri(context, ringtoneUri);

        // if (null == ringtoneUri) {
        // return null;
        // }
        // String uri = ringtoneUri.toString();
        // String str = URLDecoder.decode(uri);
        // int index = str.lastIndexOf("/");
        // String lastStr = str.substring(index + 1, str.length());
        // if ("file".equals(ringtoneUri.getScheme())) {
        // RingInfo info = new RingInfo();
        // info.mPath = ringtoneUri.getPath();
        // File f = new File(info.mPath);
        // info.mName = f.getName();
        // return info;
        // }
        // if (isInternal(ringtoneUri)) {
        // RingInfo info = new RingInfo();
        // info.mName = lastStr;
        // info.mPath = str;
        // return info;
        // }
        // int id = -1;
        // try {
        // id = Integer.valueOf(lastStr);
        // } catch (Exception e) {
        // // 是系统铃声
        // RingInfo info = new RingInfo();
        // info.mName = lastStr;
        // info.mPath = str;
        // return info;
        // }
        // return getAudioInfo(context, (long) id);
    }

    /**
     * 获取手机当前短信音
     *
     * @param context
     * @return
     */
    public RingInfo getCurrentSms(Context context) {
        if (null == context || (context instanceof Activity && ((Activity) context).isFinishing()))
            return null;
        int typeId = getTypeIdByReflect(getSmsType()[0]);
        // 获取当前默认的短信通知音
        Uri ringtoneUri = null;
        if (ModelHelper.isXIAOMI()) {
            try {
                ringtoneUri = RingtoneManagerEnhanced
                        .getNotificationForXiaoMi(context);
            } catch (Exception e) {
                ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                        context, typeId);
            }
        } else if (ModelHelper.isMeiZu()) {
            try {
                ringtoneUri = RingtoneManagerEnhanced
                        .getNotificationForMeiZu(context);
            } catch (Exception e) {
                ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                        context, typeId);
            }
        } else if (ModelHelper.isVIVO()) {
            ringtoneUri = RingtoneManagerEnhanced.getSMSForVivo(context);
        } else if (ModelHelper.isHuaWeiC8813()) {
            try {
                ringtoneUri = RingtoneManagerEnhanced
                        .getSMSRingtoneForHuaWeiC8813(context);
            } catch (Exception e) {
                ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                        context, typeId);
            }
        } else if (ModelHelper.isHuaWeiSpecial()) {
            try {
                ringtoneUri = RingtoneManagerEnhanced
                        .getNotificationForHuaWeiC8816Enhanced(context);
            } catch (Exception e) {
                ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                        context, typeId);
            }
        } else if (ModelHelper.isHuaWeiP6()) {
            try {
                ringtoneUri = RingtoneManagerEnhanced
                        .getSMSRingtoneForHuaWeiP6(context);
            } catch (Exception e) {
                ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                        context, typeId);
            }
        } else if (ModelHelper.isOPhone()) {
            try {
                ringtoneUri = RingtoneManagerEnhanced
                        .getSMSAlertForOPhone(context);
            } catch (Exception e) {
                ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                        context, typeId);
            }
        } else if (ModelHelper.isOppo()) {
            try {
                ringtoneUri = RingtoneManagerEnhanced
                        .getSmsRingForOppo(context);
            } catch (Exception e) {
                ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                        context, typeId);
            }
        } else if ("M040".equalsIgnoreCase(Build.MODEL)) {
            ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context,
                    8);
        } else if (ModelHelper.isGionee()) {
            ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(context,
                    32);
        } else if (ModelHelper.isChuiZi()) {
            ringtoneUri = RingtoneManagerEnhanced.getSMSChuiZi(context);
        } else if (ModelHelper.isLeTv()) {
            ringtoneUri = RingtoneManagerEnhanced.getSMSForLeTv(context);
        } else if (ModelHelper.isNewVIVO()) {
            ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                    context, 32);
        } else if (ModelHelper.isNubiaZ18mini()) {
            ringtoneUri = RingtoneManagerEnhanced.getSmsForNubia(context);
        } else {
            ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                    context, typeId);
        }
        if (ModelHelper.isCoolPad()) {
            try {
                if (Build.VERSION.SDK_INT >= 21) {
                    return RingtoneManagerEnhanced
                            .getSMSRingtoneForCoolPadNew(context);
                }
                return RingtoneManagerEnhanced
                        .getSMSRingtoneForCoolPad(context);
            } catch (Exception e) {
                return queryMediaSoundByUri(context, ringtoneUri);
                // return getCurrentNotifcationByUri(context, ringtoneUri);
            }
        } else {
            return queryMediaSoundByUri(context, ringtoneUri);
            // return getCurrentNotifcationByUri(context, ringtoneUri);
        }
    }

    /**
     * 获取当前通知音
     **/
    public RingInfo getCurrentNotifi(Context context) {
        if (null == context || (context instanceof Activity && ((Activity) context).isFinishing()))
            return null;
        int typeId = getTypeIdByReflect(getNotificationType()[0]);
        // 获取当前默认的短信通知音
        Uri ringtoneUri = RingtoneManager.getActualDefaultRingtoneUri(
                context, typeId);
        return queryMediaSoundByUri(context, ringtoneUri);
    }

    @Deprecated
    private RingInfo getCurrentNotifcationByUri(Context context, Uri ringtoneUri) {
        if (null == ringtoneUri) {
            return null;
        }
        String uri = ringtoneUri.toString();
        String str = URLDecoder.decode(uri);
        int index = str.lastIndexOf("/");
        String lastStr = str.substring(index + 1, str.length());
        if ("file".equals(ringtoneUri.getScheme())) {
            RingInfo info = new RingInfo();
            info.mPath = ringtoneUri.getPath();
            File f = new File(info.mPath);
            info.mName = f.getName();
            return info;
        }
        if (isInternal(ringtoneUri)) {
            RingInfo info = new RingInfo();
            info.mName = lastStr;
            info.mPath = str;
            return info;
        }
        int id = -1;
        try {
            id = Integer.valueOf(lastStr);
        } catch (Exception e) {
            // 是系统铃声
            RingInfo info = new RingInfo();
            info.mName = lastStr;
            info.mPath = str;
            return info;
        }
        return getAudioInfo(context, (long) id);
    }

    /**
     * 给联系人设置来电
     *
     * @param context
     * @param path
     * @param name
     * @param userID
     * @return
     */
    public int setSpecifyRing(Context context, String path, String name,
                              String userID, boolean copy2SysDir) {
        if (!checkAndSetPermission(context)) {
            return SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED;
        }

        // 统一sdcard路径为/mnt/sdcard
        path = path.replaceFirst("^/sdcard", Environment
                .getExternalStorageDirectory().getAbsolutePath());

        File file = new File(path);
        if (!file.exists() || !file.isFile())
            return SET_RESULT_FAILED;
        if (copy2SysDir) {//是否将待设置的铃音移动到系统铃声文件夹下再设置  防止被一些清理软件误清理
            path = moveRing2SysRingDir(path, name, file);
        }

        Uri ringtoneUri = null;
        Cursor cursor = null;
        if (Build.ID.startsWith("AliyunOs")) {
            context.getContentResolver().delete(ContentUri,
                    MediaStore.Audio.Media.DATA + "=?", new String[]{path});
        }
        try {
            // 下面为了获取媒体文件的Uri
            ContentValues contentValues = new ContentValues();
            cursor = context.getContentResolver().query(ContentUri, null,
                    MediaStore.Audio.Media.DATA + "=?", new String[]{path},
                    null);
            if (cursor != null && cursor.moveToFirst() && cursor.getCount() > 0) {
                // 媒体文件已经被系统媒体库收录，只需要更新相关的字段
                String id = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                contentValues.put(MediaStore.Audio.Media.TITLE, name);
                contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);// 设置来电铃声为true
                contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);// 设置通知铃声为true
                contentValues.put(MediaStore.Audio.Media.IS_ALARM, true);// 设置闹钟铃声为true
                // 把需要设为铃声的歌曲更新铃声库
                context.getContentResolver().update(ContentUri, contentValues,
                        MediaStore.Audio.Media.DATA + "=?",
                        new String[]{path});
                ringtoneUri = ContentUris.withAppendedId(ContentUri,
                        Long.valueOf(id));
            } else {
                // 媒体文件未被系统媒体库收录，必须将其添加进系统媒体库中
                contentValues.put(MediaStore.Audio.Media.DATA, path);
                contentValues.put(MediaStore.Audio.Media.TITLE, name);
                contentValues.put(MediaStore.Audio.Media.SIZE, file.length());
                String mimetype = FileUtils.getMimeType(file);
                contentValues.put(MediaStore.Audio.Media.MIME_TYPE, mimetype);
                contentValues.put(MediaStore.Audio.Media.IS_RINGTONE, true);// 设置来电铃声为true
                contentValues.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);// 设置通知铃声为true
                contentValues.put(MediaStore.Audio.Media.IS_ALARM, true);// 设置闹钟铃声为true
                contentValues.put(MediaStore.Audio.Media.IS_MUSIC, true);
                ringtoneUri = context.getContentResolver().insert(ContentUri,
                        contentValues);
            }
            // 设置铃声
            ContentValues values = new ContentValues();
            // Uri contactUri = Uri.parse(CONTACT_URI + userID);
            // Uri rawContactUri =
            // Uri.parse("content://com.android.contacts/raw_contacts/" +
            // userID);
            // values.put(ContactsContract.Contacts.CUSTOM_RINGTONE,
            // ringtoneUri.toString());
            // context.getContentResolver().update(rawContactUri, values, null,
            // null);
            // 插入铃声信息
            Uri contactUri = ContactsContract.Contacts.CONTENT_URI;
            // if (ModelHelper.isOPhone()) {
            // contactUri = Contacts.People.CONTENT_URI;
            // }
            Uri rawContactUri = ContentUris.withAppendedId(contactUri,
                    Long.valueOf(userID));
            values.clear();
            values.put("custom_ringtone", ringtoneUri.toString());
            context.getContentResolver().update(rawContactUri, values,
                    "_id" + " = " + userID, null);
            cursor.close();
        } catch (Exception ex) {
            return SET_RESULT_FAILED;
        }
        return SET_RESULT_SUCCESS;
    }

    /**
     * @param context
     * @param id      这里的id是这条媒体数据在数据库中的 id 不是ColumnID by yychai
     * @return
     */
    public static synchronized RingInfo getAudioInfo(Context context, long id) {
        if (null == context) {
            return null;
        }
        Cursor cursor = null;
        try {
            try {
                cursor = context.getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                        MediaStore.Audio.Media._ID + "=?",
                        new String[]{String.valueOf(id)}, null);
            } catch (Exception e) {
                return null;
            }

            if (null == cursor) {
                return null;
            }
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                String path = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DATA));
                String name = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                if (null != path) {
                    RingInfo info = new RingInfo();
                    int index = name.lastIndexOf(".");
                    String absoluteName = name;
                    if (index > 0) {
                        absoluteName = name.substring(0, index);
                    }
                    info.mPath = path;
                    info.mName = absoluteName;
                    cursor.close();
                    return info;
                }
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean isEmptyOrBlack(String text) {

        return text == null || "".equals(text.trim());
    }

    public boolean isCanSetSpecailNotifi() {
        return ModelHelper.isOPhone() || ModelHelper.isXIAOMI()
                || ModelHelper.isHuaWeiC8813() || ModelHelper.isHuaWeiSpecial()
                || ModelHelper.isHuaWeiP6() || ModelHelper.isVIVO()
                || ModelHelper.isMeiZu()
                || ModelHelper.isCoolPad()
                || ModelHelper.isMeizuSpecial();
    }
}
