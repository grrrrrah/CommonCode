package com.lijangop.sdk.utils.ring;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

/**
 * 适配6.0铃声设置权限工具类
 *
 * @author: cyli8
 * @date: 2017/9/19 21:08
 */

public class PermissionUtil {

    public static boolean hasWriteSettingPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            Log.i("cyli8", "setRingTone,sdk>=23");
            if (!Settings.System.canWrite(context)) {
                Log.i("cyli8", "setRingTone,Applications do not have permission to write settings");
                return false;
            }
        }
        return true;
    }

    public static void startSetings(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
            intent.setData(Uri.parse("package:" + context.getPackageName()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            if (isActivityCanJump(context, intent) && isIntentAvailable(context, intent)) {
                context.startActivity(intent);
            }
        }
    }

    /**
     * 判断是否有可以接受的Activity
     * @param context
     * @param intent
     * @return
     */
    public static boolean isIntentAvailable(Context context, Intent intent) {
        return intent != null && context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    /**
     * 判断当前activity是否可跳转
     * @param context
     * @param intent
     * @return
     */
    public static boolean isActivityCanJump(Context context,Intent intent){
        if(null ==intent)return false;
        ResolveInfo resolveInfo = context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if(resolveInfo != null && resolveInfo.activityInfo  != null){
            return   resolveInfo.activityInfo.exported;
        }
        return  false;
    }
}
