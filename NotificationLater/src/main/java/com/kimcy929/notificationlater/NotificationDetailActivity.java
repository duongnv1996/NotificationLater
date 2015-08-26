package com.kimcy929.notificationlater;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import customview.TextViewCustomFont;
import database.Constant;
import database.DBHelper;
import utils.Utils;

public class NotificationDetailActivity extends AppCompatActivity {

    private String timePost, contentTitle, contentText, packageName, appName;
    private int ntId, color;

    private TextViewCustomFont alphabetIcon;
    private TextViewCustomFont txtAppName;
    private TextViewCustomFont txtPostTime;
    private TextViewCustomFont txtNtContentTitle;
    private TextViewCustomFont txtNtContentText;

    private ForegroundLinearLayout btnLaunchApp;

    private ActionBar actionBar;
    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getNotificationData();
        actionBar = getSupportActionBar();
        showArrow();
        setContentView(R.layout.activity_notification_detail);
        utils = new Utils(this, getPackageManager());
        alphabetIcon = (TextViewCustomFont) findViewById(R.id.alphabetIcon);
        txtAppName = (TextViewCustomFont) findViewById(R.id.txtAppName);
        txtPostTime = (TextViewCustomFont) findViewById(R.id.txtPostTime);
        txtNtContentTitle = (TextViewCustomFont) findViewById(R.id.txtNtContentTitle);
        txtNtContentText = (TextViewCustomFont) findViewById(R.id.txtNtContentText);
        btnLaunchApp = (ForegroundLinearLayout) findViewById(R.id.btnLaunchApp);
        btnLaunchApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!packageName.equals(Constant.ANDROID_PHONE_PACKAGE)) {
                    Intent intentLauncher = getPackageManager().getLaunchIntentForPackage(packageName);
                    if (intentLauncher != null) {
                        startActivity(intentLauncher);
                        utils.leftOutRightIn();
                    }
                } else {
                    utils.showCallLogActivity();
                    utils.leftOutRightIn();
                }

            }
        });

        setData();
    }

    private void setData() {
        if (color != Color.TRANSPARENT) {
            alphabetIcon.setText(appName.substring(0, 1));
            setAlphabetColor(alphabetIcon, color);
            btnLaunchApp.setBackgroundColor(color);
        } else {
            try {
                Drawable drawable = utils.getDrawableIcon(packageName);
                int iconSize = (int) utils.convertDpToPixel(Constant.ICON_SIZE, this);
                drawable.setBounds(0, 0, iconSize, iconSize);
                alphabetIcon.setCompoundDrawables(drawable, null, null, null);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(DebugTag.TAG, "Error get drawable icon");
            }
            setAlphabetColor(alphabetIcon, Color.TRANSPARENT);
            btnLaunchApp.setBackgroundColor(getResources().getColor(R.color.myAccentColor));
        }
        txtAppName.setText(appName);
        txtPostTime.setText(txtPostTime.getText() + " " + new Utils(this).convertTime(Long.valueOf(timePost)));
        txtNtContentTitle.setText(contentTitle);
        txtNtContentText.setText(contentText);
    }

    private void setAlphabetColor(TextViewCustomFont alphabetIcon, int color) {
        GradientDrawable drawable = (GradientDrawable) alphabetIcon.getBackground();
        drawable.setColor(color);
    }

    private void getNotificationData() {
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            ntId = intent.getIntExtra(DBHelper._ID, -1);
            timePost = intent.getStringExtra(DBHelper.TIME_POST);
            contentTitle = intent.getStringExtra(DBHelper.CONTENT_TITLE);
            contentText = intent.getStringExtra(DBHelper.CONTENT_TEXT);
            packageName = intent.getStringExtra(DBHelper.PACKAGE_NAME);
            appName = intent.getStringExtra(DBHelper.APP_NAME);
            color = intent.getIntExtra(Constant.COLOR_LABEL, Color.BLACK);
        }
    }

    private void showArrow() {
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notification_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case android.R.id.home:
                finish();
                new Utils(this).slideDown();
                return true;
            case R.id.action_delete:
                DBHelper db = new DBHelper(this);
                db.open();
                boolean success = false;
                if (success = db.deleteNotification(ntId)) {
                    db.close();
                    Intent intent = new Intent();
                    intent.putExtra(Constant.DELETE_RESULT, success);
                    setResult(RESULT_OK, intent);
                    finish();
                    new Utils(this).slideDown();
                } else {
                    db.close();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new Utils(this).slideDown();
    }
}
