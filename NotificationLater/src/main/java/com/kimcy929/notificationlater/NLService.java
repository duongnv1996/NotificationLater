package com.kimcy929.notificationlater;

import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import database.Constant;
import database.DBHelper;

public class NLService extends NotificationListenerService {

    private String packageName;
    private SharedPreferences preferences;

    public NLService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        preferences = getSharedPreferences(Constant.APP_SETTING_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public void onDestroy() {
        //Log.i(DebugTag.TAG, "NL destroyed");
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return super.onBind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        Notification notification = sbn.getNotification();
        if (notification != null) {
            if (preferences.getBoolean(getResources().getString(R.string.key_cbHideAllNotification), false)) {
                packageName = sbn.getPackageName();
                DBHelper db = new DBHelper(getApplicationContext());
                try {
                    db.open();
                    insertNotification(notification, db, sbn);
                } catch (Exception e) {
                    Log.e(DebugTag.TAG, "Error open database");
                } finally {
                    db.close();
                }
            } else {
                if (sbn.isClearable()) {
                    packageName = sbn.getPackageName();
                    DBHelper db = new DBHelper(getApplicationContext());
                    try {
                        db.open();
                        if (db.checkPackageName(packageName)) {
                            insertNotification(notification, db, sbn);
                        }
                    } catch (Exception e) {
                        Log.e(DebugTag.TAG, "Error open database");
                    } finally {
                        db.close();
                    }
                }
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    private void insertNotification(Notification notification, DBHelper db, StatusBarNotification sbn) {
        ContentValues values = new ContentValues();
        Bundle extras = notification.extras;
        values.put(DBHelper.PACKAGE_NAME, packageName);
        values.put(DBHelper.CONTENT_TITLE, extras.getString("android.title"));
        values.put(DBHelper.CONTENT_TEXT, extras.getCharSequence("android.text").toString());
        values.put(DBHelper.TIME_POST, String.valueOf(sbn.getPostTime()));
        values.put(DBHelper.IS_READ, 0);
        if (db.insertNotification(values)) {
            Log.i(DebugTag.TAG, "Insert new notification success");
            sendBroadcast(new Intent(Constant.ACTION_UPDATE_NOTIFICATION));
        }
        cancelAllNotifications();
    }
}
