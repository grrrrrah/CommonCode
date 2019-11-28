package com.lijangop.sdk.utils;

import android.app.ActivityManager;
import android.content.Context;

/**
 * @Author lijiangop
 * @CreateTime 2019/11/28 16:18
 */
public class ServiceUtlis {

    /**
     * 服务是否已开启
     *
     * @param context            上下文
     * @param servicePackageName 服务的全包名
     * @return
     */
    public static boolean isServiceAlive(Context context, String servicePackageName) {
        boolean isServiceRunning = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (manager == null) {
            return true;
        }
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (servicePackageName.equals(service.service.getClassName())) {
                isServiceRunning = true;
                break;
            }
        }
        return isServiceRunning;
    }
}
