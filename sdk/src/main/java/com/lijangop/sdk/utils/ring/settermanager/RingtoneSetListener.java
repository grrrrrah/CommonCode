package com.lijangop.sdk.utils.ring.settermanager;

/**
 * Created by yychai at 2016/1/25  18:02
 * 铃声设置的回调接口  将传入参数回调回去作为判断使用
 */
public interface RingtoneSetListener {
    void onRingtoneSetSuccess(int setType, String setPath, String setName);

    void onRingtoneSetFailed(int setType, String setPath, String setName);

    /**
     * 修改系统设置权限被拒绝
     */
    void onSettingsPermissionDenied(int setType, String setPath, String setName);
}
