package com.lijangop.sdk.utils.ring.internal;

import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public final class FileUtils {
    private static final String TAG = "FileUtils";

    public static String getMimeType(File file) {
        return getMimeType(file.getName());
    }

    public static String getMimeType(String fileName) {
        String ext = getExtension(fileName);
        String mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        return mimetype;
    }

    /**
     * return file extension name, without .
     *
     * @param uri
     * @return
     */
    public static String getExtension(String uri) {
        if (uri == null)
            return "";
        int dot = uri.lastIndexOf(".");
        if (dot == -1)
            return "";
        return uri.substring(dot + 1);
    }

    /**
     * remove folder's descents recursively, don't remove root directory itself.
     * note: upon return, the contents of folder may not completed removed if errors occurs.
     *
     * @param parent the root folder
     */
    public static void removeFolderDescents(File parent) {
        if (parent == null || !parent.exists())
            return;

        File[] files = parent.listFiles();

        for (File item : files) {
            if (item.isDirectory())
                removeFolderDescents(item);
            item.delete();
        }
    }

    public static boolean copyFile(File sourceFile, File targetFile) {
        try {
            // 新建文件输入流并对它进行缓冲
            FileInputStream input = new FileInputStream(sourceFile);
            BufferedInputStream inBuff = new BufferedInputStream(input);

            // 新建文件输出流并对它进行缓冲
            FileOutputStream output = new FileOutputStream(targetFile);
            BufferedOutputStream outBuff = new BufferedOutputStream(output);

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();

            // 关闭流
            inBuff.close();
            outBuff.close();
            output.close();
            input.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
