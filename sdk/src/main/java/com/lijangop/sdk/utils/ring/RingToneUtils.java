package com.lijangop.sdk.utils.ring;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * @Author lijiangop
 * @CreateTime 2020/1/8 15:42
 */
public class RingToneUtils {

    /**
     * (目前只能设置一张卡)  卡1卡2同时设置
     *
     * @param context
     * @param type
     * @param ringtoneFile
     * @param fileName
     * @return
     */
    public static boolean setRingOneSimCard(Context context,String title, int type, File ringtoneFile, String fileName) {
        //构造数据插入数据库
        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DATA, ringtoneFile.getAbsolutePath());
        values.put(MediaStore.MediaColumns.TITLE, title);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
        values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
        values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        values.put(MediaStore.Audio.Media.IS_ALARM, true);
        values.put(MediaStore.Audio.Media.IS_MUSIC, true);
        //获取文件的uri路径
        Uri fileUri = MediaStore.Audio.Media.getContentUriForPath(ringtoneFile.getAbsolutePath());
        if (fileUri == null) {
            Log.d("TAG", "fileUri-----null");
            return false;
        }
        //如果已经设置过(按名字判断)
        if (isHaveCurrentAppRingtone(context, title))
            return true;
        //插入铃声数据到数据库
        Uri insertUri = context.getContentResolver().insert(fileUri, values);
        if (insertUri == null) {//无法插入就删除
            context.getContentResolver().delete(fileUri, null, null);
            insertUri = context.getContentResolver().insert(fileUri, values);
            if (insertUri == null) {
                Log.d("TAG", "insertUri-----null");
                return false;
            }
        }
        //设置铃声
        switch (type) {
            case RingtoneManager.TYPE_RINGTONE://来电铃声
                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE, insertUri);
                Log.e("TAG", "设置来电铃声成功");
                break;
            case RingtoneManager.TYPE_NOTIFICATION:
                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, insertUri);
                Log.e("TAG", "设置通知铃声成功");
                break;
            case RingtoneManager.TYPE_ALARM://闹钟
                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_ALARM, insertUri);
                Log.e("TAG", "设置闹钟铃声成功");
                break;
        }
        return true;
    }

    /**
     * 判断手机是否已经有该app设置的铃声(按名字判断)
     *
     * @return
     */
    private static boolean isHaveCurrentAppRingtone(Context context, String ringToneTitle) {
        boolean result = false;
        RingtoneManager ringtoneManager = new RingtoneManager(context); // 铃声管理器
        Cursor cursor = ringtoneManager.getCursor(); //获取铃声表,根据表名取值
        int count = cursor.getCount(); //获取铃声列表数量
        if (count == 0)
            return result;
        for (int i = 0; i < count; i++) {
            String title = ringtoneManager.getRingtone(i).getTitle(context);
            if (TextUtils.equals(ringToneTitle, title)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * 卡1卡2同时设置(讯飞人员给的铃声设置代码)
     *
     * @param context
     * @param typeRingtone
     * @param audioFile
     * @param fileName
     * @return
     */
    public static boolean setRingTowSimCard(Context context, int typeRingtone, File audioFile, String fileName) {
        int result = RingtoneManagerEnhanced.setRingtone(context, audioFile.getAbsolutePath(), fileName, false);
        return result == 1;
    }
}
