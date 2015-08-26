package com.kimcy929.notificationlater;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

import utils.SupportAction;
import utils.Utils;

public class SupportActivity extends AppCompatActivity {

    private LinearLayout btnFeedback;
    private LinearLayout btnVoteApp;
    private LinearLayout btnShareApp;
    private LinearLayout btnMoreApp;
    private LinearLayout btnChangeLog;
    private LinearLayout btnUserGuide;

    private TextView txtAppName;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);
        showArrow();

        txtAppName = (TextView) findViewById(R.id.txtAppName);
        txtAppName.setText(txtAppName.getText() + " Version " + Utils.getVersionName(this));

        btnFeedback = (LinearLayout) findViewById(R.id.btnFeedback);
        btnVoteApp = (LinearLayout) findViewById(R.id.btnVoteApp);
        btnShareApp = (LinearLayout) findViewById(R.id.btnShareApp);
        btnMoreApp = (LinearLayout) findViewById(R.id.btnMoreApp);
        btnChangeLog = (LinearLayout) findViewById(R.id.btnChangeLog);
        btnUserGuide = (LinearLayout) findViewById(R.id.btnUserGuide);

        btnFeedback.setOnClickListener(myOnClick);
        btnVoteApp.setOnClickListener(myOnClick);
        btnShareApp.setOnClickListener(myOnClick);
        btnMoreApp.setOnClickListener(myOnClick);
        btnChangeLog.setOnClickListener(myOnClick);
        btnUserGuide.setOnClickListener(myOnClick);
    }

    private void showArrow() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private View.OnClickListener myOnClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SupportAction supportAction = new SupportAction(SupportActivity.this);
            int id = v.getId();
            if (id == btnFeedback.getId()) {
                supportAction.sendFeedBack();
            } else if (id == btnVoteApp.getId()) {
                supportAction.searchOnMarket(getPackageName());
            } else if (id == btnShareApp.getId()) {
                String link = "https://play.google.com/store/apps/details?id=" + getPackageName();
                supportAction.shareApplication(link);
            } else if (id == btnMoreApp.getId()) {
                supportAction.moreApplication();
            } else if (id == btnChangeLog.getId()) {
                showChangeLog("change_log.html");
            } else if (id == btnUserGuide.getId()) {
                showChangeLog("user_guide.html");
            }
        }
    };

    private void showChangeLog(String fileName) {
        Resources resources = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyAlertDialogAppCompatStyle);
        View view = getLayoutInflater().inflate(R.layout.change_log_layout, null);
        WebView webView = (WebView) view.findViewById(R.id.webViewChangeLog);
        loadDataToWebView(webView, fileName);
        if (fileName.equals("change_log.html")) {
            builder.setTitle(resources.getString(R.string.change_log));
        } else {
            builder.setTitle(resources.getString(R.string.user_guide));
        }
        builder.setPositiveButton(resources.getString(R.string.ok_label), null);
        builder.setView(view);
        builder.show();
    }

    private void loadDataToWebView(WebView webView, String fileName) {
        webView.loadUrl("file:///android_asset/" + fileName);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_support, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            finish();
            new Utils(this).leftInRightOut();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new Utils(this).leftInRightOut();
    }
}
