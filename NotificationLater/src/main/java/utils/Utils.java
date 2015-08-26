package utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.provider.CallLog;
import android.support.v7.graphics.Palette;
import android.util.DisplayMetrics;
import android.util.Log;

import com.kimcy929.notificationlater.DebugTag;
import com.kimcy929.notificationlater.R;

import java.text.Collator;
import java.text.DateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import database.AppEntry;
import database.NotificationEntry;

/**
 * Created by kimcy on 18/08/2015.
 */
public class Utils {

    public static Comparator<AppEntry> sortName = new Comparator<AppEntry>() {
        Collator sCollator = Collator.getInstance();

        @Override
        public int compare(AppEntry object1, AppEntry object2) {
            return sCollator.compare(object1.getActivityLabel(), object2.getActivityLabel());
        }
    };

    public static Comparator<NotificationEntry> sortUnread = new Comparator<NotificationEntry>() {
        @Override
        public int compare(NotificationEntry lhs, NotificationEntry rhs) {
            return Integer.compare(lhs.getIsRead(), rhs.getIsRead());
        }
    };

    private Context context;
    private PackageManager pm;

    public Utils(Context context) {
        this.context = context;
    }

    public Utils(Context context, PackageManager pm) {
        this.context = context;
        this.pm = pm;
    }

    public boolean isPackageExisted(String targetPackage) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public boolean isPackageInstalled(String packageName) {
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            return false;
        }
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public Drawable getDrawableIcon(String appPackageName) throws PackageManager.NameNotFoundException {
        Drawable drawableIcon = null;
        ApplicationInfo applicationInfo = pm.getApplicationInfo(appPackageName, 0);
        drawableIcon = applicationInfo.loadIcon(pm);
        return drawableIcon;
    }

    public String getAppName(String packageName) {
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
            return applicationInfo.loadLabel(pm).toString();
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    public static String getVersionName(Context context) {
        String versionName = "";
        try {
            versionName = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return versionName;
    }

    public String convertTime(long longTime) {
        if (longTime != 0) {
            DateFormat dateFormat = DateFormat.getDateTimeInstance();
            dateFormat.setTimeZone(TimeZone.getDefault());
            Date date = new Date(longTime);
            return dateFormat.format(date);
        }
        return null;
    }

    public void leftOutRightIn() {
        ((Activity) context).overridePendingTransition(R.anim.right_in, R.anim.left_out);
    }

    public void leftInRightOut() {
        ((Activity) context).overridePendingTransition(R.anim.right_out, R.anim.left_in);
    }

    public void slideUp() {
        ((Activity) context).overridePendingTransition(R.anim.slide_in_bottom, R.anim.slide_out_top);
    }

    public void slideDown() {
        ((Activity) context).overridePendingTransition(R.anim.slide_in_top, R.anim.slide_out_bottom);
    }

    public void getColorPalette(final NotificationEntry entry) {
        try {
            Drawable drawable = getDrawableIcon(entry.getPackageName());
            if (drawable != null) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        entry.setColor(palette.getVibrantColor(Color.BLACK));
                    }
                });
            }
        } catch (Exception e) {
            entry.setColor(Color.BLACK);
            Log.e(DebugTag.TAG, "Error get drawable icon");
        }
    }

    public void showCallLogActivity() {
        Intent showCallLog = new Intent();
        showCallLog.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        showCallLog.setAction(Intent.ACTION_VIEW);
        showCallLog.setType(CallLog.Calls.CONTENT_TYPE);
        context.startActivity(showCallLog);
    }

    public float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}
