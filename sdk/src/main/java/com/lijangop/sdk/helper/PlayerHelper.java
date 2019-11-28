package com.lijangop.sdk.helper;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.MediaPlayer;
import android.media.PlaybackParams;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @Author lijiangop
 * @CreateTime 2019/7/5 09:23
 */
public class PlayerHelper {
    private static volatile PlayerHelper instance;

    private MediaPlayer         mMediaPlayer;
    private AssetManager        mAssets;
    private AssetFileDescriptor mAfd;

    private static class Holder {
        private static final PlayerHelper PLAYER = new PlayerHelper();
    }

    public interface Callback {
        void onPlayFinish();
    }

    public static PlayerHelper getIntance() {
        if (instance == null) {
            synchronized (PlayerHelper.class) {
                if (instance == null) {
                    instance = Holder.PLAYER;
                }
            }
        }
        return instance;
    }

    public PlayerHelper setResource(Context context, String path) {
        return setResource(context, path, null, null);
    }

    public PlayerHelper setResource(Context context, String path, MediaPlayer.OnCompletionListener onCompletionListener) {
        return setResource(context, path, onCompletionListener, null);
    }

    public PlayerHelper setResource(Context context, String path, MediaPlayer.OnPreparedListener onPreparedListener) {
        return setResource(context, path, null, onPreparedListener);
    }

    //设置音频文件源
    public PlayerHelper setResource(Context context, String path, MediaPlayer.OnCompletionListener onCompletionListener, MediaPlayer.OnPreparedListener onPreparedListener) {
        FileInputStream fis = null;
        try {
            if (mMediaPlayer == null)
                mMediaPlayer = getMediaPlayer(context);
            pause();
            if (onCompletionListener != null)
                mMediaPlayer.setOnCompletionListener(onCompletionListener);
            reset();
            fis = new FileInputStream(new File(path));
            mMediaPlayer.setDataSource(fis.getFD());
            prepare();
            if (onPreparedListener != null)
                mMediaPlayer.setOnPreparedListener(onPreparedListener);
            try {
                fis.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            fis = null;
            return null;
        }
    }

    /**
     * 播放
     */
    public void play() {
        if (mMediaPlayer == null)
            return;
        if (!mMediaPlayer.isPlaying())
            mMediaPlayer.start();
    }

    /**
     * 播放(参数是进度)
     *
     * @param progress
     */
    public void play(int progress) {
        if (mMediaPlayer == null)
            return;
        if (!mMediaPlayer.isPlaying()) {
            if (progress == 0)
                mMediaPlayer.start();
            else
                seekTo(progress);
        }
    }

    /**
     * 暂停播放
     */
    public void pause() {
        if (mMediaPlayer == null)
            return;
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.pause();
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mMediaPlayer == null)
            return;
        if (mMediaPlayer.isPlaying())
            mMediaPlayer.reset();
    }

    /**
     * @param progress
     */
    public void seekTo(int progress) {
        try {
            if (mMediaPlayer == null)
                return;
            mMediaPlayer.seekTo(progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public int getCurrentPosition() {
        try {
            if (mMediaPlayer == null)
                return 0;
            return mMediaPlayer.getCurrentPosition();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void reset() {
        try {
            if (mMediaPlayer == null)
                return;
            mMediaPlayer.reset();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void prepare() throws Exception {
        if (mMediaPlayer == null)
            return;
        mMediaPlayer.prepare();
    }

    public int getDuration() {
        try {
            if (mMediaPlayer == null)
                return 0;
            return mMediaPlayer.getDuration();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 设置播放的参数
     *
     * @param pitch
     * @param speed
     */
    public void setPLaybackParams(float pitch, float speed) {
        if (mMediaPlayer == null)
            return;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            PlaybackParams playbackParams = mMediaPlayer.getPlaybackParams();
            if (pitch != 0)
                playbackParams.setPitch(pitch);
            if (speed != 0)
                playbackParams.setSpeed(speed);
            mMediaPlayer.setPlaybackParams(playbackParams);
        }
    }

    /**
     * 播放asset中音频文件
     *
     * @param context
     * @param fileName
     * @param callback
     */
    public void playAssetSounds(Context context, String fileName, final Callback callback) {
        try {
            if (mAssets == null)
                mAssets = context.getAssets();
            if (mAfd != null) {
                mAfd.close();
            }
            if (TextUtils.isEmpty(fileName))
                return;
            mAfd = mAssets.openFd(fileName);
            if (mAfd == null)
                return;
            if (mMediaPlayer == null)
                mMediaPlayer = getMediaPlayer(context);
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (callback != null)
                        callback.onPlayFinish();
                    try {
                        mAfd.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            mMediaPlayer.reset();
            mMediaPlayer.setDataSource(mAfd.getFileDescriptor(), mAfd.getStartOffset(), mAfd.getLength());
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取Mediaplayer
     * :解决MediaPlayer播放音乐的时候报错： Should have subtitle controller already set
     *
     * @param context
     * @return
     */
    private MediaPlayer getMediaPlayer(Context context) {
        MediaPlayer mediaplayer = new MediaPlayer();
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.KITKAT) {
            return mediaplayer;
        }
        try {
            Class<?> cMediaTimeProvider = Class.forName("android.media.MediaTimeProvider");
            Class<?> cSubtitleController = Class.forName("android.media.SubtitleController");
            Class<?> iSubtitleControllerAnchor = Class.forName("android.media.SubtitleController$Anchor");
            Class<?> iSubtitleControllerListener = Class.forName("android.media.SubtitleController$Listener");
            Constructor constructor = cSubtitleController.getConstructor(
                    new Class[]{Context.class, cMediaTimeProvider, iSubtitleControllerListener});
            Object subtitleInstance = constructor.newInstance(context, null, null);
            Field f = cSubtitleController.getDeclaredField("mHandler");
            f.setAccessible(true);
            try {
                f.set(subtitleInstance, new Handler());
            } catch (IllegalAccessException e) {
                return mediaplayer;
            } finally {
                f.setAccessible(false);
            }
            Method setsubtitleanchor = mediaplayer.getClass().getMethod("setSubtitleAnchor",
                    cSubtitleController, iSubtitleControllerAnchor);
            setsubtitleanchor.invoke(mediaplayer, subtitleInstance, null);
        } catch (Exception e) {
            Log.d("error", "getMediaPlayer crash ,exception = " + e);
        }
        return mediaplayer;
    }

    /**
     * 清除引用
     */
    public void onDestroy() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        if (mAssets != null) {
            mAssets = null;
        }
        if (mAfd != null) {
            mAfd = null;
        }
    }
}
