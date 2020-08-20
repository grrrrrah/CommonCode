package com.lijangop.sdk.utils;

import java.text.DecimalFormat;

public class MoneyFormat {

    /**
     * 保留两位小数
     *
     * @param money
     * @return
     */
    public static String formatMoney(double money) {
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(money);
    }

    /**
     * 保留两位小数
     *
     * @param money
     * @return
     */
    public static String formatMoney(String money) {
        double moneyD = Double.parseDouble(money);
        DecimalFormat df = new DecimalFormat("#0.00");
        return df.format(moneyD);
    }

}
