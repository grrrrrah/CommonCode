package com.lijangop.sdk.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v4.app.Fragment;

import com.lijangop.sdk.constant.Common;


/**
 * @Author lijiangop
 * @CreateTime 2020/1/6 11:57
 */
public class SettingUtils {

    /**
     * 获取手机品牌
     *
     * @return
     */
    public static String getPhoneBrand() {
        String brand = android.os.Build.BRAND;
        return brand.toLowerCase();
    }

    /**
     * 打开自启动界面
     *
     * @param context
     */
    public static void enterWhiteListSetting(Context context) {
        try {
            context.startActivity(getSettingIntent());
        } catch (Exception e) {
            context.startActivity(new Intent(Settings.ACTION_SETTINGS));
        }
    }

    /**
     * 获取各机型自启动指令代码
     *
     * @return
     */
    private static Intent getSettingIntent() {
        ComponentName componentName = null;
        String brand = android.os.Build.BRAND;
        switch (brand.toLowerCase()) {
            case Common.Brand.BRAND_SAMSUNG:
                componentName = new ComponentName("com.samsung.android.sm", "com.samsung.android.sm.app.dashboard.SmartManagerDashBoardActivity");
                break;
            case Common.Brand.BRAND_HUAWEI:
                componentName = new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.startupmgr.ui.StartupNormalAppListActivity");
                break;
            case Common.Brand.BRAND_XIAOMI:
                componentName = new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity");
                break;
            case Common.Brand.BRAND_VIVO:
                componentName = new ComponentName("com.iqoo.secure", "com.iqoo.secure.ui.phoneoptimize.AddWhiteListActivity");
                break;
            case Common.Brand.BRAND_OPPO:
                componentName = new ComponentName("com.coloros.oppoguardelf", "com.coloros.powermanager.fuelgaue.PowerUsageModelActivity");
                break;
            case Common.Brand.BRAND_360:
                componentName = new ComponentName("com.yulong.android.coolsafe", "com.yulong.android.coolsafe.ui.activity.autorun.AutoRunListActivity");
                break;
            case Common.Brand.BRAND_MEIZU:
                componentName = new ComponentName("com.meizu.safe", "com.meizu.safe.permission.SmartBGActivity");
                break;
            case Common.Brand.BRAND_ONEPLUS:
                componentName = new ComponentName("com.oneplus.security", "com.oneplus.security.chainlaunch.view.ChainLaunchAppListActivity");
                break;
            default:
                break;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (componentName != null) {
            intent.setComponent(componentName);
        } else {
            intent.setAction(Settings.ACTION_SETTINGS);
        }
        return intent;
    }

    /**
     * 打开应用详情界面
     *
     * @param fragment
     * @param requestCode
     */
    public static void enterAppSettingDetail(Fragment fragment, int requestCode) {
        try {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package",fragment.getContext().getPackageName(), null));
            fragment.startActivityForResult(localIntent, requestCode);
        } catch (Exception e) {
            fragment.startActivityForResult(new Intent(Settings.ACTION_SETTINGS), requestCode);
        }
    }

}
