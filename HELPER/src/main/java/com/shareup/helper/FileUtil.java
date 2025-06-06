package com.shareup.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtil {

    /**
     * Convert a Uri into a File by copying its content to a temporary file.
     */
    public static File getFileFromUri(Context context, Uri uri) throws IOException {
        InputStream inputStream = context.getContentResolver().openInputStream(uri);
        String fileName = getFileName(context, uri);
        if (fileName == null) {
            fileName = "temp_file";
        }

        File tempFile = new File(context.getCacheDir(), fileName);
        FileOutputStream outputStream = new FileOutputStream(tempFile);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }

        outputStream.close();
        inputStream.close();

        return tempFile;
    }

    public static File convertUriToPngFile(Context context, Uri imageUri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), imageUri);

        File file = new File(context.getCacheDir(), "image.png");
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        outputStream.flush();
        outputStream.close();

        return file;
    }

    /**
     * Get file name from Uri.
     */
    public static String getFileName(Context context, Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    if (index != -1) {
                        result = cursor.getString(index);
                    }
                }
            }
        }
        if (result == null) {
            result = uri.getLastPathSegment();
        }
        return result;
    }

    /**
     * Get file size in bytes from Uri.
     */
    public static long getFileSize(Context context, Uri uri) {
        long size = 0;
        if (uri.getScheme().equals("content")) {
            try (Cursor cursor = context.getContentResolver().query(uri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                    if (sizeIndex != -1) {
                        size = cursor.getLong(sizeIndex);
                    }
                }
            }
        } else if (uri.getScheme().equals("file")) {
            File file = new File(uri.getPath());
            size = file.length();
        }
        return size;
    }

    /**
     * Resize an image from a URI and save it to a temporary file.
     */
    public static File resizeImage(File imageFile, int maxWidth, int maxHeight, int quality) {
        try {
            // Decode the image from the file
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            // Calculate the scaling factor
            int originalWidth = options.outWidth;
            int originalHeight = options.outHeight;
            int scaleFactor = Math.min(originalWidth / maxWidth, originalHeight / maxHeight);

            // Decode the image with the scaling factor
            options.inJustDecodeBounds = false;
            options.inSampleSize = scaleFactor;
            Bitmap scaledBitmap = BitmapFactory.decodeFile(imageFile.getAbsolutePath(), options);

            // Further scale the bitmap to exact dimensions
            Bitmap resizedBitmap = Bitmap.createScaledBitmap(scaledBitmap, maxWidth, maxHeight, true);

            // Create a temporary file to save the compressed image
            File compressedFile = File.createTempFile("resized_image", ".jpg");
            try (FileOutputStream outputStream = new FileOutputStream(compressedFile)) {
                // Compress the bitmap into the file
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            }

            // Recycle the bitmaps to free memory
            scaledBitmap.recycle();
            resizedBitmap.recycle();

            return compressedFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Return null if an error occurs
        }
    }

    /**
     * Get the MIME type of a file from Uri.
     */
    public static String getMimeType(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        return contentResolver.getType(uri);
    }

    /**
     * Safely delete a file.
     */
    public static boolean deleteFile(File file) {
        if (file != null && file.exists()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Create a temporary file in the cache directory.
     */
    public static File createTempFile(Context context, String prefix, String extension) throws IOException {
        return File.createTempFile(prefix, extension, context.getCacheDir());
    }

    /**
     * Get file extension from Uri.
     */
    public static String getFileExtension(Context context, Uri uri) {
        String extension = null;
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        }
        return extension;
    }
}
