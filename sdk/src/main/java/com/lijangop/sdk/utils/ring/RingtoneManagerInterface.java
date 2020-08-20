package com.lijangop.sdk.utils.ring;

import android.content.Context;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * 铃声设置公共接口类
 *
 * @author yudeng
 */
public interface RingtoneManagerInterface {
    /**
     * 设置成功
     */
    int SET_RESULT_SUCCESS = 1;
    /**
     * 设置失败
     */
    int SET_RESULT_FAILED = 2;
    /**
     * 未获取修改系统设置权限
     */
    int SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED = 3;

    /**
     * 外部存储空间中媒体的Content Uri.
     */
    Uri ContentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    /**
     * 一次性设置所有铃声
     *
     * @param context   用于调用系统API的Context对象
     * @param ringPath  来电铃声文件路径
     * @param ringName  来电铃声名称
     * @param alarmPath 闹钟铃声文件路径
     * @param alarmName 闹钟铃声名称
     * @param notiPath  短信铃声文件路径
     * @param notiName  短信铃声名称
     * @return 设置结果 {@link #SET_RESULT_FAILED {@link #SET_RESULT_SUCCESS}
     * {@link #SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED}}
     */
    int setAllRingAudio(Context context,
                        String ringPath, String ringName, String alarmPath,
                        String alarmName, String notiPath, String notiName);

    /**
     * 一次性取消设置所有铃声
     *
     * @param context
     * @return 设置结果 {@link #SET_RESULT_FAILED {@link #SET_RESULT_SUCCESS}
     * {@link #SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED}}
     */
    int removeAllAudioSettings(Context context);

    /**
     * 设置手机来电铃声为默认
     *
     * @param context 用于调用系统API的Context对象
     * @return 设置结果 {@link #SET_RESULT_FAILED {@link #SET_RESULT_SUCCESS}
     * {@link #SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED}}
     */
    int setDefaultRingtone(Context context);

    /**
     * 设置闹钟铃声为默认
     *
     * @param context 用于调用系统API的Context对象
     * @return 设置结果 {@link #SET_RESULT_FAILED {@link #SET_RESULT_SUCCESS}
     * {@link #SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED}}
     */
    int setDefaultAlarm(Context context);

    /**
     * 设置短信铃声为默认
     *
     * @param context 用于调用系统API的Context对象
     * @return 设置结果 {@link #SET_RESULT_FAILED {@link #SET_RESULT_SUCCESS}
     * {@link #SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED}}
     */
    int setDefaultNotification(Context context);

    /**
     * 将指定的path的媒体文件设置为当前手机来电铃声
     *
     * @param context 用于调用系统API的Context对象
     * @param path    媒体文件路径
     * @param name    铃声在系统铃声列表里的名称
     * @return 设置结果 {@link #SET_RESULT_FAILED {@link #SET_RESULT_SUCCESS}
     * {@link #SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED}}
     */
    int setRingtone(Context context, String path, String name, boolean move2SysDir);

    /**
     * 将指定的path的媒体文件设置为当前手机闹钟铃声
     *
     * @param context 用于调用系统API的Context对象
     * @param path    媒体文件路径
     * @param name    铃声在系统铃声列表里的名称
     * @return 设置结果 {@link #SET_RESULT_FAILED {@link #SET_RESULT_SUCCESS}
     * {@link #SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED}}
     */
    int setAlarm(Context context, String path, String name, boolean move2SysDir);

    /**
     * 将指定的path的媒体文件设置为当前手机短信铃声
     *
     * @param context 用于调用系统API的Context对象
     * @param path    媒体文件路径
     * @param name    铃声在系统铃声列表里的名称
     * @return 设置结果 {@link #SET_RESULT_FAILED {@link #SET_RESULT_SUCCESS}
     * {@link #SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED}}
     */
    int setSms(Context context, String path, String name, boolean move2SysDir);

    /**
     * 将指定的path的媒体文件设置为当前手机通知音
     *
     * @param context 用于调用系统API的Context对象
     * @param path    媒体文件路径
     * @param name    铃声在系统铃声列表里的名称
     * @return 设置结果 {@link #SET_RESULT_FAILED {@link #SET_RESULT_SUCCESS}
     * {@link #SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED}}
     */
    int setNotification(Context context, String path, String name, boolean move2SysDir);

    /**
     * 检查指定的path的媒体文件是否被设置为当前手机短信铃声
     *
     * @param context 用于调用系统API的Context对象
     * @param path 媒体文件路径
     * @param name 铃声在系统铃声列表里的名称
     * @return 设置成功，true；否则，false
     */
//	boolean isNotificationApplyed(Context context, String path, String name);

    /**
     * 检查指定的path的媒体文件是否被设置为当前手机来电铃声
     *
     * @param context 用于调用系统API的Context对象
     * @param path 媒体文件路径
     * @param name 铃声在系统铃声列表里的名称
     * @return 设置成功，true；否则，false
     */
//	boolean isRingtoneApplyed(Context context, String path, String name);

    /**
     * 检查指定的path的媒体文件是否被设置为当前手机闹钟铃声
     *
     * @param context 用于调用系统API的Context对象
     * @param path 媒体文件路径
     * @param name 铃声在系统铃声列表里的名称
     * @return 设置成功，true；否则，false
     */
//	boolean isAlarmApplyed(Context context, String path, String name);
}