package loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.util.Log;

import com.kimcy929.notificationlater.DebugTag;
import com.kimcy929.notificationlater.R;

import java.util.ArrayList;
import java.util.List;

import database.Constant;
import database.DBHelper;
import database.NotificationEntry;
import utils.Utils;

/**
 * Created by kimcy on 19/08/2015.
 */
public class NotificationLoader extends AsyncTaskLoader<List<NotificationEntry>> {
    private Utils utils;
    private int[] alphabetColor;
    private int length;
    private SharedPreferences preferences;
    private Resources resources;

    public NotificationLoader(Context context) {
        super(context);
        alphabetColor = context.getResources().getIntArray(R.array.colorAlphabet);
        length = alphabetColor.length;
        utils = new Utils(context, context.getPackageManager());
        preferences = context.getSharedPreferences(Constant.APP_SETTING_NAME, Context.MODE_PRIVATE);
        resources =  context.getResources();
    }

    private List<NotificationEntry> lstData;//Reference to list data loader

    @Override
    public List<NotificationEntry> loadInBackground() {
        List<NotificationEntry> notificationData = new ArrayList<>();
        DBHelper db = new DBHelper(getContext());
        db.open();
        Cursor cursor = db.getAllNotificationSaved();
        if (cursor != null && cursor.getCount() > 0) {
            int columnId = cursor.getColumnIndex(DBHelper._ID);
            int columnPackageName = cursor.getColumnIndex(DBHelper.PACKAGE_NAME);
            int columnContentTitle = cursor.getColumnIndex(DBHelper.CONTENT_TITLE);
            int columnContentText = cursor.getColumnIndex(DBHelper.CONTENT_TEXT);
            int columnTimePost = cursor.getColumnIndex(DBHelper.TIME_POST);
            int columnIsRead = cursor.getColumnIndex(DBHelper.IS_READ);
            while (cursor.moveToNext()) {
                NotificationEntry entry = new NotificationEntry();
                entry.setId(cursor.getInt(columnId));
                entry.setPackageName(cursor.getString(columnPackageName));
                entry.setAppName(utils.getAppName(entry.getPackageName()));
                entry.setNtContentTitle(cursor.getString(columnContentTitle));
                entry.setNtContentText(cursor.getString(columnContentText));
                entry.setNtTimePost(cursor.getString(columnTimePost));
                entry.setIsRead(cursor.getInt(columnIsRead));

                switch (Integer.valueOf(
                        preferences.getString(resources.getString(R.string.key_alphabetOption), "1"))) {
                    case 1: //Random
                        entry.setColor(alphabetColor[(int) (Math.random() * length)]);
                        break;
                    case 2: //Primary color of app icon
                        break;
                    case 3: //Use app icon default
                        try {
                            entry.setAppIcon(utils.getDrawableIcon(entry.getPackageName()));
                        } catch (PackageManager.NameNotFoundException e) {
                            Log.i(DebugTag.TAG, "Error get icon");
                        }
                        break;
                }

                notificationData.add(entry);
            }
            cursor.close();
        }
        db.close();
        return notificationData;
    }

    @Override
    public void deliverResult(List<NotificationEntry> data) {
        if (isReset()) {
            if (data != null) {
                releaseResource(data);
                return;
            }
        }

        List<NotificationEntry> oldData = lstData;
        lstData = data;
        if (isStarted()) {
            super.deliverResult(data);
        }

        //Invalidate data
        if (oldData != null && oldData != data) {
            releaseResource(oldData);
        }
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        if (lstData != null) {
            deliverResult(lstData);
        }

        if (takeContentChanged()) {
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        onStopLoading();
        if (lstData != null) {
            releaseResource(lstData);
            lstData = null;
        }
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    public void onCanceled(List<NotificationEntry> data) {
        releaseResource(data);
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
    }

    private void releaseResource(List<NotificationEntry> data) {

    }
}
