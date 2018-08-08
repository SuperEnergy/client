package com.ees.chain.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

public final class MediaUtils {

    public static final int REQUEST_IMAGE_CODE = 1;// 选择照片的requestCode
    public static final int REQUEST_CODE_TAKE_VIDEO = 2;// 摄像的照相的requestCode
    public static final int RESULT_CAPTURE_RECORDER_SOUND = 3;// 录音的requestCode
    public static final int REQUEST_FILE_CODE = 4;// 选择照片的requestCode

    /**
     * 选择照片
     */
    public static void selectPhotos(Activity activity) {
        Intent getAlbum = new Intent();
        if (Build.VERSION.SDK_INT >= 19) {
            getAlbum.setAction("android.intent.action.OPEN_DOCUMENT");
        } else {
            getAlbum.setAction(Intent.ACTION_GET_CONTENT);
        }
        getAlbum.setType("image/*");
        activity.startActivityForResult(getAlbum, REQUEST_IMAGE_CODE);
    }
    /**
     * 选择文件
     */
    public static void selectFile(Activity activity) {
        Intent getFileIntent = new Intent();
        if (Build.VERSION.SDK_INT >= 19) {
            getFileIntent.setAction("android.intent.action.OPEN_DOCUMENT");
        } else {
            getFileIntent.setAction(Intent.ACTION_GET_CONTENT);
        }
        getFileIntent.setType("*/*");
        getFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
        try {
            activity.startActivityForResult(getFileIntent, REQUEST_FILE_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(activity, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 拍摄视频
     */
    public static void videoMethod(Activity activity) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
        activity.startActivityForResult(intent, REQUEST_CODE_TAKE_VIDEO);
    }

    /**
     * 录音功能
     */
    public void soundRecorderMethod(Activity activity) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/amr");
        activity.startActivityForResult(intent, RESULT_CAPTURE_RECORDER_SOUND);
    }

    public static void startPlayVideo(String url, Activity activity) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("video/*");
        intent.setDataAndType(Uri.parse(url), "video/*");
        activity.startActivity(intent);
    }

    public static String onActivityResult(int requestCode, Intent data,
            Activity activity) {
        switch (requestCode) {
        case REQUEST_IMAGE_CODE:
        case REQUEST_CODE_TAKE_VIDEO:
        case RESULT_CAPTURE_RECORDER_SOUND:
            return getFilePath(data, activity);
        }
        return null;
    }

    public static String getFilePath(Intent data, Activity activity) {
        if (data == null) {
            return null;
        }
        Uri uri = data.getData();
        if (uri == null) {
            return null;
        }
        String uriScheme = uri.getScheme();
        if( "content".equalsIgnoreCase(uriScheme) ) {
            if (Build.VERSION.SDK_INT >= 19) {
                return getPathForKITKAT(uri, activity);
            } else {
                return getPath(uri, activity);
            }
        } else if ( "file".equalsIgnoreCase(uriScheme) ) {
            return uri.getPath();
        }
        return null;
    }

    @TargetApi(19)
    private static String getPathForKITKAT(Uri uri, Context context) {
        String filePath = null;
        String[] column = { MediaStore.Images.Media.DATA };
        String wholeID = DocumentsContract.getDocumentId(uri);// video:1234 or image:1234
        String mediaType = wholeID.split(":")[0]; //media类型
        String id = wholeID.split(":")[1]; //media id
        String sel = MediaStore.Images.Media._ID + " =?";
        Uri queryUri = null;
        if( mediaType.equalsIgnoreCase("image") )
        	queryUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI; //content://media/external/images/media
        else if( mediaType.equalsIgnoreCase("video") )
        	queryUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI; //content://media/external/video/media
        Cursor cursor = context.getContentResolver().query(queryUri, column, sel, new String[]{id}, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndex(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(column_index);
            }
            cursor.close();
        }
        return filePath;
    }

    private static String getPath(Uri uri, Context context) {
        String filePath = null;
        String[] column = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, column, null,
                null, null);
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(column[0]);
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(column_index);
            }
            cursor.close();
        }
        return filePath;
    }
}