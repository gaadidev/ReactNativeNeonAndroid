package com.gaadi.neon.activity.gallery;

import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import com.gaadi.neon.activity.NeonBaseActivity;
import com.gaadi.neon.model.BucketModel;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.NeonImagesHandler;
import com.scanlibrary.R;

import java.util.ArrayList;

/**
 * @author princebatra
 * @version 1.0
 * @since 25/1/17
 */
public abstract class NeonBaseGalleryActivity extends NeonBaseActivity {

    private Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private ArrayList<BucketModel> buckets;

    protected ArrayList<BucketModel> getImageBuckets() {
        buckets = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            uri = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL);
        } else {
            uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        }

        String[] PROJECTION_BUCKET = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID, MediaStore.Images.ImageColumns.DATA};

        String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        Cursor mCursor;
        if (NeonImagesHandler.getSingletonInstance().getGalleryParam() != null && NeonImagesHandler.getSingletonInstance().getGalleryParam().isRestrictedExtensionJpgPngEnabled()) {
            mCursor = getContentResolver().query(uri, PROJECTION_BUCKET, MediaStore.Images.Media.MIME_TYPE + " in (?, ?)", new String[]{"image/jpeg", "image/png"}, orderBy);
        } else {
            mCursor = getContentResolver().query(uri, PROJECTION_BUCKET, null, null, orderBy);
        }
        if (mCursor == null) {
            Toast.makeText(this, getString(R.string.gallery_error), Toast.LENGTH_SHORT).show();
            finish();
            return null;
        }

        int idColumn = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        mCursor.moveToFirst();


        if(mCursor.getCount() > 0){
            do {
                String bucketId = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_ID));

                int index = getBucketIndexWithId(bucketId);
                if (index == -1) {
                    long id = mCursor.getLong(idColumn);
                    BucketModel bucketModel = new BucketModel();
                    bucketModel.setBucketId(bucketId);
                    bucketModel.setBucketName(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)));
                    bucketModel.setFileCount(1);

                    //bucketModel.setBucketCoverImagePath(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));

                    Uri contentUri = ContentUris.withAppendedId(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                    bucketModel.setBucketCoverImagePath(contentUri.toString());

                    buckets.add(bucketModel);
                } else {
                    buckets.get(index).setFileCount(buckets.get(index).getFileCount() + 1);
                }
            }while (mCursor.moveToNext());
        }
        mCursor.close();

        return buckets;
    }

    /**Pass bucketId if need all images from all buckets*/
    protected ArrayList<FileInfo> getFileFromBucketId(String bucketId) {
        ArrayList<FileInfo> fileInfos = new ArrayList<>();

        String[] PROJECTION_FILES = {
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.DATE_TAKEN, MediaStore.Images.Media.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME};

        String orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC";

        String selection = MediaStore.Images.Media.BUCKET_ID + " =? and " + MediaStore.Images.Media.SIZE + " >? and "
                + MediaStore.Images.Media.MIME_TYPE + " in (?, ?)";
        String[] selectionArgs = new String[]{bucketId, String.valueOf(0), "image/jpeg", "image/png"};
        if(bucketId == null){
            selection = null;
            selectionArgs = null;
        }
        Cursor mCursor = getContentResolver().query(uri, PROJECTION_FILES, selection, selectionArgs, orderBy);
        if (mCursor == null) {
            Toast.makeText(this, getString(R.string.gallery_error), Toast.LENGTH_SHORT).show();
            finish();
            return null;
        }

        int idColumn = mCursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);

        mCursor.moveToFirst();

        if(mCursor.getCount()>0){
            do{
                long id = mCursor.getLong(idColumn);
                FileInfo singleFileInfo = new FileInfo();
                singleFileInfo.setSource(FileInfo.SOURCE.PHONE_GALLERY);
                singleFileInfo.setDateTimeTaken(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATE_TAKEN)));
                singleFileInfo.setType(FileInfo.FILE_TYPE.IMAGE);

                //singleFileInfo.setFilePath(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA)));

                Uri contentUri = ContentUris.withAppendedId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);
                singleFileInfo.setFilePath(contentUri.toString());
                fileInfos.add(singleFileInfo);
            }while (mCursor.moveToNext());
        }
        mCursor.close();

        return fileInfos;
    }


    private int getBucketIndexWithId(String id) {
        if (buckets == null || buckets.size() <= 0) {
            return -1;
        }

        for (int i = 0; i < buckets.size(); i++) {
            if (buckets.get(i).getBucketId().equals(id)) {
                return i;
            }
        }
        return -1;
    }


}

/*
package com.gaadi.neon.activity.gallery;

import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.gaadi.neon.activity.NeonBaseActivity;
import com.gaadi.neon.model.BucketModel;
import com.gaadi.neon.util.Constants;
import com.gaadi.neon.util.FileInfo;
import com.gaadi.neon.util.NeonImagesHandler;
import com.scanlibrary.R;

import java.util.ArrayList;

*/
/**
 * @author princebatra
 * @version 1.0
 * @since 25/1/17
 *//*

public abstract class NeonBaseGalleryActivity extends NeonBaseActivity {

    private Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
    private ArrayList<BucketModel> buckets;

    protected ArrayList<BucketModel> getImageBuckets() {
        buckets = new ArrayList<>();

        String[] PROJECTION_BUCKET = {MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.BUCKET_ID, MediaStore.Images.ImageColumns.DATA};

        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

        Cursor mCursor;
        if (NeonImagesHandler.getSingletonInstance().getGalleryParam() != null && NeonImagesHandler.getSingletonInstance().getGalleryParam().isRestrictedExtensionJpgPngEnabled()) {
            boolean folderRestriction = false;
            String appName = "";
            if (NeonImagesHandler.getSingletonInstance().getGenericParam() != null && NeonImagesHandler.getSingletonInstance().getGenericParam().getCustomParameters() != null && NeonImagesHandler.getSingletonInstance().getGenericParam().getCustomParameters().getFolderRestrictive()) {
                if(NeonImagesHandler.getSingletonInstance().getGenericParam().getCustomParameters().getFolderName() != null){
                    appName = NeonImagesHandler.getSingletonInstance().getGenericParam().getCustomParameters().getFolderName();
                }else {
                    appName = Constants.getAppName(this);
                }
                if (appName != null && appName.length() > 0) {
                    folderRestriction = true;
                }
            }
            if (folderRestriction) {
                mCursor = getContentResolver().query(uri, PROJECTION_BUCKET, MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =? and " + MediaStore.Images.Media.MIME_TYPE + " in (?, ?)", new String[]{appName, "image/jpeg", "image/png"}, orderBy);
            } else {
                mCursor = getContentResolver().query(uri, PROJECTION_BUCKET, MediaStore.Images.Media.MIME_TYPE + " in (?, ?)", new String[]{"image/jpeg", "image/png"}, orderBy);
            }

        } else {
            mCursor = getContentResolver().query(uri, PROJECTION_BUCKET, null, null, orderBy);
        }
        if (mCursor == null) {
            Toast.makeText(this, getString(R.string.gallery_error), Toast.LENGTH_SHORT).show();
            finish();
            return null;
        }
        mCursor.moveToFirst();


        if (mCursor.getCount() > 0) {
            do {
                String bucketId = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_ID));

                int index = getBucketIndexWithId(bucketId);
                if (index == -1) {
                    BucketModel bucketModel = new BucketModel();
                    bucketModel.setBucketId(bucketId);
                    bucketModel.setBucketName(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)));
                    bucketModel.setFileCount(1);
                    bucketModel.setBucketCoverImagePath(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                    buckets.add(bucketModel);
                } else {
                    buckets.get(index).setFileCount(buckets.get(index).getFileCount() + 1);
                }
            } while (mCursor.moveToNext());
        }
        mCursor.close();

        return buckets;
    }

    */
/**
     * Pass bucketId if need all images from all buckets
     *//*

    protected ArrayList<FileInfo> getFileFromBucketId(String bucketId) {
        ArrayList<FileInfo> fileInfos = new ArrayList<>();

        boolean folderRestriction = false;
        String appName = "";
        if (NeonImagesHandler.getSingletonInstance().getGenericParam() != null && NeonImagesHandler.getSingletonInstance().getGenericParam().getCustomParameters() != null && NeonImagesHandler.getSingletonInstance().getGenericParam().getCustomParameters().getFolderRestrictive()) {
            if(NeonImagesHandler.getSingletonInstance().getGenericParam().getCustomParameters().getFolderName() != null){
                appName = NeonImagesHandler.getSingletonInstance().getGenericParam().getCustomParameters().getFolderName();
            }else {
                appName = Constants.getAppName(this);
            }
            if (appName != null && appName.length() > 0) {
                folderRestriction = true;
            }
        }

        String[] PROJECTION_FILES = {MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.BUCKET_ID, MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.DATE_TAKEN};

        String orderBy = MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC";

        String selection; // = MediaStore.Images.Media.BUCKET_ID + " =? and " + MediaStore.Images.Media.SIZE + " >? and " + MediaStore.Images.Media.MIME_TYPE + " in (?, ?)";
        String[] selectionArgs; // = new String[]{bucketId, String.valueOf(0), "image/jpeg", "image/png"};
        if (bucketId == null) {
            if(folderRestriction){
                selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =? and " + MediaStore.Images.Media.SIZE + " >? and "
                        + MediaStore.Images.Media.MIME_TYPE + " in (?, ?)";
                selectionArgs = new String[]{appName, String.valueOf(0), "image/jpeg", "image/png"};
            }else {
                selection = null;
                selectionArgs = null;
            }
        }else {
            if(folderRestriction){
                selection = MediaStore.Images.Media.BUCKET_ID + " =? and " + MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " =? and " + MediaStore.Images.Media.SIZE + " >? and "
                        + MediaStore.Images.Media.MIME_TYPE + " in (?, ?)";
                selectionArgs = new String[]{bucketId, appName, String.valueOf(0), "image/jpeg", "image/png"};
            }else {
                selection = MediaStore.Images.Media.BUCKET_ID + " =? and " + MediaStore.Images.Media.SIZE + " >? and "
                        + MediaStore.Images.Media.MIME_TYPE + " in (?, ?)";
                selectionArgs = new String[]{bucketId, String.valueOf(0), "image/jpeg", "image/png"};
            }
        }
        Cursor mCursor = getContentResolver().query(uri, PROJECTION_FILES, selection, selectionArgs, orderBy);
        if (mCursor == null) {
            Toast.makeText(this, getString(R.string.gallery_error), Toast.LENGTH_SHORT).show();
            finish();
            return null;
        }
        mCursor.moveToFirst();

        if (mCursor.getCount() > 0) {
            do {
                FileInfo singleFileInfo = new FileInfo();
                singleFileInfo.setSource(FileInfo.SOURCE.PHONE_GALLERY);
                singleFileInfo.setFilePath(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)));
                singleFileInfo.setDateTimeTaken(mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_TAKEN)));
                singleFileInfo.setType(FileInfo.FILE_TYPE.IMAGE);
                fileInfos.add(singleFileInfo);
            } while (mCursor.moveToNext());
        }
        mCursor.close();

        return fileInfos;
    }


    private int getBucketIndexWithId(String id) {
        if (buckets == null || buckets.size() <= 0) {
            return -1;
        }

        for (int i = 0; i < buckets.size(); i++) {
            if (buckets.get(i).getBucketId().equals(id)) {
                return i;
            }
        }
        return -1;
    }


}
*/
