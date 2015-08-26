package database;

/**
 * Created by kimcy on 18/08/2015.
 */

import android.graphics.drawable.Drawable;

/**
 * Created by vanchung on 13/07/2015.
 */
public class AppEntry {
    String appName;
    String activityLabel;
    String packageName;
    Drawable appIcon;

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setActivityLabel(String activityLabel) {
        this.activityLabel = activityLabel;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public String getActivityLabel() {
        return activityLabel;
    }

    public String getPackageName() {
        return packageName;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }
}
