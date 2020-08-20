package com.lijangop.sdk.utils.ring;

import android.content.Context;

/**
 * 设置铃声工厂类
 *
 * @author yudeng
 */
public class RingtoneManagerFactory {
    public static RingtoneManagerInterface getRingtoneManager(Context context) {
        return new RingtoneManagerStanderd();
    }
}
