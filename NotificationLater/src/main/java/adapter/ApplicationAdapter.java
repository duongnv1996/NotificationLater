package adapter;

import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.util.ArrayMap;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.TextView;

import com.kimcy929.notificationlater.DebugTag;
import com.kimcy929.notificationlater.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.AppEntry;
import database.Constant;
import database.DBHelper;
import utils.Utils;

/**
 * Created by kimcy on 18/08/2015.
 */
public class ApplicationAdapter extends ArrayAdapter<AppEntry> {

    private int layoutId;
    private LayoutInflater inflater;
    private PackageManager pm;
    private ArrayMap<String, Boolean> lstChecked;
    private List<AppEntry> allApp;//For search
    private Utils utils;
    private int iconSize;

    public ApplicationAdapter(Context context, int resource, List<AppEntry> objects) {
        super(context, resource, objects);
        layoutId = resource;
        allApp = new ArrayList<>(objects);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        pm = context.getPackageManager();
        utils = new Utils(context, pm);
        iconSize = (int) utils.convertDpToPixel(Constant.ICON_SIZE, context);

        lstChecked = new ArrayMap<>(objects.size());
        DBHelper db = new DBHelper(context);
        try {
            db.open();
            Cursor cursor = db.getAllAppSelected();
            if (cursor != null && cursor.getCount() > 0) {
                String packageName = "";
                int column = cursor.getColumnIndex(DBHelper.PACKAGE_NAME);
                while (cursor.moveToNext()) {
                    packageName = cursor.getString(column);
                    if (utils.isPackageInstalled(packageName))
                        lstChecked.put(packageName, true);
                    else
                        db.deleteApp(packageName);
                }
                cursor.close();
            }
        } catch (Exception e) {

        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(layoutId, parent, false);
            viewHolder.txtAppName = (TextView) convertView.findViewById(R.id.txtAppName);
            viewHolder.cbChooseApp = (CheckBox) convertView.findViewById(R.id.cbChooseApp);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final AppEntry appEntry = getItem(position);

        Drawable drawable = appEntry.getAppIcon();
        if (drawable != null) {
            drawable.setBounds(0, 0, iconSize, iconSize);
            viewHolder.txtAppName.setCompoundDrawables(drawable, null, null, null);
        }
        viewHolder.txtAppName.setText(appEntry.getActivityLabel());

        viewHolder.cbChooseApp.setOnCheckedChangeListener(null);
        if (lstChecked.containsKey(appEntry.getPackageName())) {
            viewHolder.cbChooseApp.setChecked(true);
        } else {
            viewHolder.cbChooseApp.setChecked(false);
        }
        viewHolder.cbChooseApp.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                handCheckBox(appEntry, isChecked);
            }
        });
        return convertView;
    }

    public void handCheckBox(AppEntry appEntry, boolean isChecked) {
        DBHelper db = new DBHelper(getContext());
        db.open();
        try {
            ContentValues values = new ContentValues();
            values.put(DBHelper.PACKAGE_NAME, appEntry.getPackageName());
            if (isChecked) {
                if (db.insertApp(values)) {
                    Log.i(DebugTag.TAG, "Insert OK");
                    lstChecked.put(appEntry.getPackageName(), true);
                }
            } else {
                if (db.deleteApp(appEntry.getPackageName())) {
                    Log.i(DebugTag.TAG, "Remove OK");
                    lstChecked.remove(appEntry.getPackageName());
                }
            }
            notifyDataSetChanged();
        } catch (Exception e) {

        } finally {
            db.close();
        }

    }

    @Override
    public Filter getFilter() {
        return new FilterApp();
    }

    private class FilterApp extends Filter {
        private ArrayMap<String, AppEntry> lstFilter = new ArrayMap<>();
        private FilterResults filterResults = new FilterResults();
        private AppEntry appEntry;
        private String filterQuery = "";

        public FilterApp() {
            allApp = new ArrayList<>(ApplicationAdapter.this.allApp);
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            if (!TextUtils.isEmpty(constraint)) {
                lstFilter.clear();
                filterQuery = constraint.toString();
                for (int i = 0; i < allApp.size(); ++i) {
                    Pattern p = Pattern.compile(filterQuery,
                            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);
                    appEntry = allApp.get(i);
                    Matcher m = p.matcher(appEntry.getActivityLabel());
                    if (m.find()) {
                        lstFilter.put(appEntry.getPackageName(), appEntry);
                    }
                }
                filterResults.values = lstFilter.values();
                filterResults.count = lstFilter.size();
            } else {
                filterResults.values = allApp;
                filterResults.count = allApp.size();
                return filterResults;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            if (results != null && results.count > 0) {
                List<AppEntry> lstResult = new ArrayList<>((Collection<? extends AppEntry>) results.values);
                if (!lstResult.isEmpty()) {
                    setDataFilter(lstResult);
                }
            } else {
                notifyDataSetInvalidated();
            }
        }

        private void setDataFilter(List<AppEntry> lstData) {
            if (lstData != null && !lstData.isEmpty()) {
                clear();
                for (AppEntry appItem : lstData) {
                    add(appItem);
                }
                if (lstData.size() == allApp.size()) {
                    sort(Utils.sortName);
                }
                notifyDataSetChanged();
            }
        }
    }

    private static class ViewHolder {
        public TextView txtAppName;
        public CheckBox cbChooseApp;
    }
}
