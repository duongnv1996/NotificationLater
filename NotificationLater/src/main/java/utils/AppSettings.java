package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by kimcy on 22/08/2015.
 */
public class AppSettings {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public static final String SORT_NOTIFICATION_BY = "SORT_NOTIFICATION_BY";

    public AppSettings(Context context) {
        preferences = context.getSharedPreferences("notification_later", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    public void setSortNotificationBy(int sortType) {
        editor.putInt(SORT_NOTIFICATION_BY, sortType);
        editor.commit();
    }

    public int getSortNotificationBy() {
        return preferences.getInt(SORT_NOTIFICATION_BY, 0);//DESC TIME
    }
}
