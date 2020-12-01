package com.lijangop.sdk.utils;

import android.content.Intent;
import android.net.Uri;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.StringUtils;

import java.io.File;

public class OpenFileUtil {
    public static final int      FILE_TYPE_VIDEO = 1;
    public static final int      FILE_TYPE_AUDIO = 2;
    public static final int      FILE_TYPE_OTHER = 3;
    public static final String[] VIDEO_EXTS      = {"mp4", "mpeg", "mpg", "avi", "mov", "wmv", "flv",
            "rmvb", "ts"};
    public static final String[] AUDIO_EXTS      = {"mp3", "m4a", "wma", "amr", "flac", "aac", "wav",
            "ogg"};

    public static void shareFile(File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_SEND);
            //Uri uri = UriUtils.file2Uri(file);
            Uri uri = Uri.parse("file://" + file.getAbsolutePath());
            intent.putExtra(Intent.EXTRA_STREAM, uri);

            //微信暂不支持发送音频，所以不用"audio/*"
            switch (OpenFileUtil.getFileType(file)) {
                case OpenFileUtil.FILE_TYPE_VIDEO:
                    intent.setType("video/*");
                    break;
                case OpenFileUtil.FILE_TYPE_AUDIO:
                    intent.setType("*/*");
                    break;
                case OpenFileUtil.FILE_TYPE_OTHER:
                    intent.setType("*/*");
                    break;
            }
            ActivityUtils.startActivity(Intent.createChooser(intent, "分享"));
        } catch (Exception e) {
            //            ToastUtils.showLong("暂时无法分享");
            e.printStackTrace();
        }
    }

    public static void openFile(File file) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            //Uri uri = UriUtils.file2Uri(file);
            Uri uri = Uri.parse("file://" + file.getAbsolutePath());
            switch (OpenFileUtil.getFileType(file)) {
                case OpenFileUtil.FILE_TYPE_VIDEO:
                    intent.setDataAndType(uri, "video/*");
                    ActivityUtils.startActivity(intent);
                    break;
                case OpenFileUtil.FILE_TYPE_AUDIO:
                    intent.setDataAndType(uri, "audio/*");
                    ActivityUtils.startActivity(intent);
                    break;
                case OpenFileUtil.FILE_TYPE_OTHER:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取文件格式
     *
     * @param file
     * @return
     */
    public static int getFileType(File file) {
        String str = FileUtils.getFileExtension(file);
        if (StringUtils.isEmpty(str))
            return FILE_TYPE_OTHER;
        for (int i = 0; i < VIDEO_EXTS.length; i++) {
            if (str.equalsIgnoreCase(VIDEO_EXTS[i])) {
                return FILE_TYPE_VIDEO;
            }
        }
        for (int i = 0; i < AUDIO_EXTS.length; i++) {
            if (str.equalsIgnoreCase(AUDIO_EXTS[i])) {
                return FILE_TYPE_AUDIO;
            }
        }
        return FILE_TYPE_OTHER;
    }
}
