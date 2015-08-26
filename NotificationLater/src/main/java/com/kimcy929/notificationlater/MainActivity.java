package com.kimcy929.notificationlater;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import fragment.AppFragment;
import fragment.NotificationSavedFragment;
import utils.Utils;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private int curFragmentId = -1;

    private String FRAGMENT_TAG = "FRAGMENT_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableNotificationPermission();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.navigationView);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(selectedListener);
        }

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };

        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        setSelectedItem(R.id.action_notification_saved);
        if (navigationView != null) {
            navigationView.getMenu().getItem(0).setChecked(true);
            getSupportActionBar().setTitle(getResources().getString(R.string.notification));
        }
    }

    private NavigationView.OnNavigationItemSelectedListener selectedListener
            = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(MenuItem menuItem) {
            menuItem.setChecked(true);
            setSelectedItem(menuItem.getItemId());
            return true;
        }
    };

    private void setSelectedItem(int menuId) {
        if (menuId == R.id.action_select_app
                || menuId == R.id.action_notification_saved) {
            if (curFragmentId != menuId) {
                curFragmentId = menuId;

                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                //ft.setCustomAnimations(R.animator.fragment_slide_left_enter, R.animator.fragment_slide_right_exit);
                Fragment fragment = null;
                if (menuId == R.id.action_notification_saved) {
                    fragment = new NotificationSavedFragment();
                    if (fm.getBackStackEntryCount() > 0) {
                        fm.popBackStack();
                    }
                    toolbar.setTitle(getResources().getString(R.string.notification));
                } else {
                    fragment = new AppFragment();
                    if (fm.getBackStackEntryCount() == 0) {//Add fragment to stack
                        ft.addToBackStack(FRAGMENT_TAG);
                    }
                    toolbar.setTitle(getResources().getString(R.string.select_app));
                }
                ft.replace(R.id.frame, fragment);
                ft.commit();
            }
        } else {
            Utils utils = new Utils(this);
            switch (menuId) {
                case R.id.action_settings:
                    toolbar.setTitle(getResources().getString(R.string.action_settings));
                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                    utils.leftOutRightIn();
                    break;
                case R.id.action_support:
                    startActivity(new Intent(this, SupportActivity.class));
                    utils.leftOutRightIn();
                    break;
            }
        }
        drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            curFragmentId = R.id.action_notification_saved;
            navigationView.getMenu().getItem(0).setChecked(true);
            getFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    private void enableNotificationPermission() {
        ContentResolver contentResolver = getContentResolver();
        String enabledNotificationListeners = Settings.Secure.getString(contentResolver, "enabled_notification_listeners");
        String packageName = getPackageName();
        // check to see if the enabledNotificationListeners String contains our package name
        if (enabledNotificationListeners == null || !enabledNotificationListeners.contains(packageName)) {
            // in this situation we know that the user has not granted the app the Notification access permission
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
    }
}
