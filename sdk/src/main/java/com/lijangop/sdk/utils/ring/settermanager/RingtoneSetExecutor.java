package com.lijangop.sdk.utils.ring.settermanager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by yychai
 * at 2016/1/26  09:43
 */
public class RingtoneSetExecutor {
    // 创建一个可重用固定线程数的线程池
    private static ExecutorService mPool;

    public static ExecutorService getInstance() {
        if (null == mPool) {
            mPool = Executors.newSingleThreadExecutor();
        }

        return mPool;
    }

    public void execute(Runnable task) {
        mPool.execute(task);
    }
}
