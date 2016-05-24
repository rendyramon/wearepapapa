package com.hotcast.vr.tools;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.provider.MediaStore;

/**
 * Created by zhangjunjun on 2016/5/11.
 */
public class VideoDBUtils {

    public static String getLocalVideoPathById(Context context, long id) {
        String path = null;
        ContentResolver contentResolver = context.getContentResolver();
        String[] projection = new String[]{MediaStore.Video.Media.DATA};
        Cursor cursor = contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, MediaStore.Video.Media._ID + "=" + id,
                null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        if (cursor.getCount() != 0) {
            cursor.moveToNext();
            path = cursor.getString(0);
        }
        cursor.close();
        return path;
    }

}
