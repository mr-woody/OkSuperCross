package com.woodys.supercross.dispatcher;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.woodys.supercross.dispatcher.cursor.DispatcherCursor;
import com.woodys.supercross.log.Debugger;


public class DispatcherProvider extends ContentProvider {
    private static final String TAG = DispatcherProvider.class.getSimpleName();

    public static final String PROJECTION_MAIN[] = {"main"};

    public static final String URI_SUFFIX="api.provider";

    @Override
    public boolean onCreate() {
        return false;
    }

    /**
     *
     * @param uri  例如：Uri.parse("content://com.woodys.supercross.dispatcher.api.provider/main");
     * @param projection
     * @param selection
     * @param selectionArgs
     * @param sortOrder
     * @return
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Debugger.d(TAG,"query,uri:" + uri.getAuthority());
        return DispatcherCursor.generateCursor(Dispatcher.getInstance().asBinder());
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
