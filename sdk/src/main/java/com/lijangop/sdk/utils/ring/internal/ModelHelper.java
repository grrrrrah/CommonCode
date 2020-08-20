package com.lijangop.sdk.utils.ring.internal;

import android.os.Build;

import java.lang.reflect.Method;

public final class ModelHelper {

    public static boolean isOPhone() {
        final String model = Build.MODEL;
        return "OMAP_SS".equalsIgnoreCase(model);
    }

    public static boolean isXIAOMI() {
        if ("MI-ONE Plus".equalsIgnoreCase(Build.MODEL)) {
            return true;
        }
        if ("MI 2S".equalsIgnoreCase(Build.MODEL)
                || "MI 2C".equalsIgnoreCase(Build.MODEL)
                || "MI 2A".equalsIgnoreCase(Build.MODEL)
                || "MI 2".equalsIgnoreCase(Build.MODEL)
                || "MI 2SC".equalsIgnoreCase(Build.MODEL)
                || "2013022".equals(Build.MODEL)
                || "HM NOTE 1TD".equals(Build.MODEL)
                || "MI NOTE LTE".equals(Build.MODEL)) {
            return true;
        }
        if ("Xiaomi".equals(Build.MANUFACTURER)) {
            return true;
        }
        return "MI 1S".equalsIgnoreCase(Build.MODEL);
    }

    public static boolean isVivoX() {
        return "vivo X5Pro D".equals(Build.MODEL) || "vivo X7".equals(Build.MODEL);
    }

    public static boolean isVivoY66OrY67() {
        return "vivo Y66".equals(Build.MODEL) || "vivo Y67".equals(Build.MODEL);
    }

    public static boolean isViVoY67(){
        return "vivo Y67".equals(Build.MODEL);
    }


    public static boolean isOppoX9007() {
        return "X9007".equals(Build.MODEL);
    }

    public static boolean isNewOppo(){
        return "PBEM00".equals(Build.MODEL) || "PBCM30".equals(Build.MODEL);
    }


    public static boolean isSamSung() {
        final String[] models = new String[]{"GT-N7108", "GT-N7100",
                "GT-I9300", "GT-I9508"};
        final int size = models.length;
        for (int i = 0; i < size; i++) {
            if (models[i].equalsIgnoreCase(Build.MODEL)) {
                return true;
            }
        }
        return "samsung".equals(Build.MANUFACTURER);
    }

    public static boolean isMeiZu() {
        return "Meizu".equals(Build.MANUFACTURER);
    }

    public static boolean isChuiZi() {
        return "smartisan".equalsIgnoreCase(Build.MANUFACTURER);
    }

    public static boolean isLeTv(){
        return "Le X620".equals(Build.MODEL);
    }

    public static boolean isHTC() {
        final String[] models = new String[]{"HTC Incredible S", "HTC Z710e"};
        final int size = models.length;
        for (int i = 0; i < size; i++) {
            if (models[i].equalsIgnoreCase(Build.MODEL)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCoolPad() {
        boolean result = false;
        if (Build.VERSION.SDK_INT >= 21) {
            String mode = null;
            final String COMM_PERIVATE_DEFAULT_MODE = "persist.yulong.defaultmode";
            final String SP_String = "android.os.SystemProperties";
            Class<?> spClass;
            try {
                spClass = Class.forName(SP_String);
                Method spGet = spClass.getMethod("get", String.class,
                        String.class);
                mode = (String) spGet.invoke(spClass,
                        COMM_PERIVATE_DEFAULT_MODE, "");
                if (mode != null && mode.length() > 0) {
                    if (mode.equalsIgnoreCase("0")
                            || mode.equalsIgnoreCase("1")
                            || mode.equalsIgnoreCase("2"))
                        result = true;
                }
            } catch (Exception e) {
                result = false;
                e.printStackTrace();
            }
            return result;
        }
        final String[] models = new String[]{"8020", "8022", "Coolpad Y75"};
        final int size = models.length;
        for (int i = 0; i < size; i++) {
            if (models[i].equalsIgnoreCase(Build.MODEL)) {
                return true;
            }
        }
        return "Coolpad".equals(Build.MANUFACTURER);
    }

    public static boolean isHuaWeiC8813() {
        return "HUAWEI C8813".equalsIgnoreCase(Build.MODEL);
    }

    /**
     * 华为大部分机型的适配方案是相同的 可以在这里优先判断一下 然后用这种方式来适配
     *
     * @return
     * @author yychai
     */
    public static boolean isHuaWeiSpecial() {
        return "HUAWEI C8816".equalsIgnoreCase(Build.MODEL)
                || "G620-L75".equals(Build.MODEL);
    }

    public static boolean isMeizuSpecial() {
        return "M040".equalsIgnoreCase(Build.MODEL)
                || "M045".equalsIgnoreCase(Build.MODEL)
                || "M351".equalsIgnoreCase(Build.MODEL)
                || "M355".equalsIgnoreCase(Build.MODEL);
    }

    // 华为P6、P7系列手机
    // 华为荣耀系列
    // 华为MT系列
    public static boolean isHuaWeiP6() {
        return "HUAWEI P6-T00".equals(Build.MODEL)
                || Build.MODEL.startsWith("HUAWEI P")
                || Build.MODEL.startsWith("HUAWEI G")
                || Build.MODEL.startsWith("HUAWEI MT");
    }

    public static boolean isHonorV10(){
        return "BKL-AL20".equals(Build.MODEL);
    }

    public static boolean isMotorola() {
        final String[] models = new String[]{"ME865"};
        final int size = models.length;
        for (int i = 0; i < size; i++) {
            if (models[i].equalsIgnoreCase(Build.MODEL)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isNexus() {
        // 9250
        final String[] models = new String[]{"GT-I9250"};
        final int size = models.length;
        for (int i = 0; i < size; i++) {
            if (models[i].equalsIgnoreCase(Build.MODEL)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isVIVO() {
        final String[] models = new String[]{"vivo X3L"};
        final int size = models.length;
        for (int i = 0; i < size; i++) {
            if (models[i].equalsIgnoreCase(Build.MODEL)) {
                return true;
            }
        }
        return (Build.MODEL.startsWith("vivo"));
    }

    public static boolean is360(){
        return "1807-A01".equalsIgnoreCase(Build.MODEL);
    }

    /**
     * 针对x21和x23进行适配
     * @return
     */
    public static boolean isNewVIVO(){
        return "V1816A".equals(Build.MODEL) || "vivo X21A".equals(Build.MODEL);
    }

    public static boolean isOppo() {
        return "OPPO".equals(Build.MANUFACTURER);
    }

    public static boolean isGionee() {
        final String manufacturer = Build.MANUFACTURER;
        return "GiONEE".equalsIgnoreCase(manufacturer);
    }

    public static boolean isNubiaZ18mini(){
        return "NX611J".equals(Build.MODEL);
    }
}
