package com.example.yourtrip.commonUtil;

//public class FileUtils {
//    public static String getPath(Context context, Uri uri) {
//        Cursor cursor = context.getContentResolver().query(uri,
//                null, null, null, null);
//
//        if (cursor == null) return null;
//        cursor.moveToFirst();
//
//        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
//        String path = cursor.getString(idx);
//        cursor.close();
//
//        return path;
//    }
//}

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    /**
     * SAF(Content URI) → byte[] 변환
     * Android 11+ 완전 지원
     */
    public static byte[] readBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    /**
     * (선택) Content URI에서 파일 이름 추출
     * 서버 multipart 업로드 시 파일명을 생성하는 용도
     */
    public static String getFileName(Context context, Uri uri) {

        String result = null;

        if (uri.getScheme().equals("content")) {

            try (Cursor cursor = context.getContentResolver()
                    .query(uri, null, null, null, null)) {

                if (cursor != null && cursor.moveToFirst()) {
                    int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    result = cursor.getString(nameIndex);
                }
            }
        }

        // content:// 이 아닌 경우 (file://)
        if (result == null) {
            result = uri.getLastPathSegment();
        }

        return result;
    }

    /**
     * (구버전 전용) Uri → File Path 변환
     * Android 10+, 특히 11에서는 거의 동작하지 않음.
     * → 지금은 사용 X (InputStream 방식 사용)
     */
    @Deprecated
    public static String getPath(Context context, Uri uri) {
        // 경로 기반 File 접근은 Android 11 이상에서 대부분 불가능.
        // 그래서 업로드에는 사용하지 않음.
        Log.e("FileUtils", "getPath()는 최신 안드로이드에서 거의 동작하지 않습니다.");
        return null;
    }
}
