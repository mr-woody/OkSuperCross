package com.woodys.supercross.dispatcher.cursor;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.woodys.supercross.bean.BinderWrapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 多进程数据传递MatrixCursor实现类
 */
public class DispatcherCursor extends MatrixCursor {

    public static final String KEY_BINDER_WRAPPER = "KEY_BINDER_WRAPPER";

    private static Map<String, DispatcherCursor> cursorCache = new ConcurrentHashMap<>();

    public static final String[] DEFAULT_COLUMNS = {"col"};

    private Bundle binderExtras = new Bundle();

    public DispatcherCursor(String[] columnNames, IBinder binder) {
        super(columnNames);
        binderExtras.putParcelable(KEY_BINDER_WRAPPER, new BinderWrapper(binder));
    }

    @Override
    public Bundle getExtras() {
        return binderExtras;
    }

    /**
     * 用于将 binder 放入 Cursor 中
     * @param binder
     * @return
     */
    public static DispatcherCursor generateCursor(IBinder binder) {
        try {
            DispatcherCursor cursor = cursorCache.get(binder.getInterfaceDescriptor());
            if (cursor != null) {
                return cursor;
            }
            cursor = new DispatcherCursor(DEFAULT_COLUMNS, binder);
            cursorCache.put(binder.getInterfaceDescriptor(), cursor);
            return cursor;
        } catch (RemoteException ex) {
            return null;
        }
    }

    /**
     * 用于将 binder 从 Cursor 中取出
     * @param cursor
     * @return
     */
    public static IBinder stripBinder(Cursor cursor) {
        if (null == cursor) {
            return null;
        }
        Bundle bundle = cursor.getExtras();
        bundle.setClassLoader(BinderWrapper.class.getClassLoader());
        BinderWrapper binderWrapper = bundle.getParcelable(KEY_BINDER_WRAPPER);
        return null != binderWrapper ? binderWrapper.getBinder() : null;
    }

}
