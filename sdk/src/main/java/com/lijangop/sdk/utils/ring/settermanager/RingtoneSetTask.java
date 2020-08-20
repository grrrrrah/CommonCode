package com.lijangop.sdk.utils.ring.settermanager;

import android.content.Context;

import com.lijangop.sdk.utils.ring.RingtoneManagerFactory;
import com.lijangop.sdk.utils.ring.RingtoneManagerInterface;
import com.lijangop.sdk.utils.ring.RingtoneManagerStanderd;
import com.lijangop.sdk.utils.ring.RingtoneSetterConstants;


/**
 * Created by yychai
 * at 2016/1/25  17:55
 * 铃声设置的任务 中间包含文件操作和数据库操作  所以使用异步的方式
 */
public class RingtoneSetTask implements Runnable {

    private Context mContext;//尽量使用application的context
    private String mPendingSettingPath;
    private String mPendingSettingName;
    private RingtoneSetListener mListener;
    private int mSetType = RingtoneSetterConstants.RINGTONE_SET_TYPE_RING;
    private String mUserId;
    private boolean mMoveRing2SysDir = false;//是否将待设置的铃音移动到系统铃声文件夹下

    public RingtoneSetTask(int setType, Context context, String pendingSettingPath,
                           String pendingSettingName, boolean move2SysDir,
                           RingtoneSetListener listener) {
        this.mContext = context;
        this.mPendingSettingPath = pendingSettingPath;
        this.mPendingSettingName = pendingSettingName;
        this.mListener = listener;
        mSetType = setType;
        mMoveRing2SysDir = move2SysDir;
    }

    public RingtoneSetTask(int setType, Context context, String pendingSettingPath,
                           String pendingSettingName, String userId, boolean move2SysDir,
                           RingtoneSetListener listener) {
        this.mContext = context;
        this.mPendingSettingPath = pendingSettingPath;
        this.mPendingSettingName = pendingSettingName;
        this.mListener = listener;
        this.mSetType = setType;
        this.mUserId = userId;
        mMoveRing2SysDir = move2SysDir;
    }

    @Override
    public void run() {
        if (null == mListener) {
            return;
        }
        RingtoneManagerInterface ringtoneManager = RingtoneManagerFactory
                .getRingtoneManager(null);
        int setResult = 0;
        switch (mSetType) {
            case RingtoneSetterConstants.RINGTONE_SET_TYPE_RING:
                setResult = ringtoneManager.setRingtone(mContext, mPendingSettingPath,
                        mPendingSettingName, mMoveRing2SysDir);
                break;
            case RingtoneSetterConstants.RINGTONE_SET_TYPE_ALARM:
                setResult = ringtoneManager.setAlarm(mContext, mPendingSettingPath,
                        mPendingSettingName, mMoveRing2SysDir);
                break;
            case RingtoneSetterConstants.RINGTONE_SET_TYPE_SMS:
                setResult = ringtoneManager.setSms(mContext, mPendingSettingPath,
                        mPendingSettingName, mMoveRing2SysDir);
                break;
            case RingtoneSetterConstants.RINGTONE_SET_TYPE_NOTIFICATION:
                setResult = ringtoneManager.setNotification(mContext, mPendingSettingPath,
                        mPendingSettingName, mMoveRing2SysDir);
                break;
            case RingtoneSetterConstants.RINGTONE_SET_TYPE_SPECIFY_RINGTONE:
                RingtoneManagerStanderd standerdMgr = new RingtoneManagerStanderd();
                setResult = standerdMgr.setSpecifyRing(mContext, mPendingSettingPath,
                        mPendingSettingName, mUserId, mMoveRing2SysDir);
                break;
        }
        if (RingtoneManagerInterface.SET_RESULT_SUCCESS == setResult) {
            mListener.onRingtoneSetSuccess(mSetType, mPendingSettingPath, mPendingSettingName);
        } else if (RingtoneManagerInterface.SET_RESULT_FAILED == setResult) {
            mListener.onRingtoneSetFailed(mSetType, mPendingSettingPath, mPendingSettingName);
        } else if (RingtoneManagerInterface.SET_RESULT_WRITE_SETTINGS_PERMISSION_DENIED == setResult) {
            mListener.onSettingsPermissionDenied(mSetType, mPendingSettingPath, mPendingSettingName);
        }
    }
}
