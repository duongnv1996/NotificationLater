package adapter;


import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.kimcy929.notificationlater.DebugTag;
import com.kimcy929.notificationlater.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import customview.TextViewCustomFont;
import database.Constant;
import database.DBHelper;
import database.NotificationEntry;
import utils.Utils;

/**
 * Created by kimcy on 18/08/2015.
 */
public class NotificationSavedAdapter
        extends RecyclerView.Adapter<NotificationSavedAdapter.ViewHolder>
        implements Filterable {

    private Context context;
    private LayoutInflater inflater;

    private List<NotificationEntry> lstData;
    private List<NotificationEntry> allNotification;//For search

    private int layoutId;
    private int disableTextColor;
    private int primaryTextColor;
    private int secondaryTextColor;
    private int timePostColor;
    private int iconSize;

    private SharedPreferences preferences;
    private Resources resources;

    private Utils utils;

    public NotificationSavedAdapter(Context ctx, List<NotificationEntry> data, int resource) {
        context = ctx;
        lstData = data;
        allNotification = new ArrayList<>(lstData);
        layoutId = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        resources = context.getResources();
        disableTextColor = resources.getColor(R.color.primary_text_disabled_material_light);
        primaryTextColor = Color.BLACK;
        secondaryTextColor = resources.getColor(R.color.abc_secondary_text_material_light);
        timePostColor = context.getResources().getColor(R.color.timePostColor);
        utils = new Utils(context, context.getPackageManager());
        preferences = context.getSharedPreferences(Constant.APP_SETTING_NAME, Context.MODE_PRIVATE);
        iconSize = (int) utils.convertDpToPixel(Constant.ICON_SIZE, context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(layoutId, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        final NotificationEntry entry = getNotificationItem(position);

        switch (Integer.valueOf(preferences.getString(resources.getString(R.string.key_alphabetOption), "1"))) {
            case 1:
                viewHolder.alphabetIcon.setText(entry.getAppName().substring(0, 1));
                setAlphabetColor(viewHolder.alphabetIcon, entry.getColor());
                break;
            case 2:
                viewHolder.alphabetIcon.setText(entry.getAppName().substring(0, 1));
                if (entry.getColor() == -1)
                    getColorPalette(viewHolder, entry);
                else
                    setAlphabetColor(viewHolder.alphabetIcon, entry.getColor());
                break;
            case 3:
                try {
                    Drawable drawable = entry.getAppIcon();
                    if (drawable == null) {
                        drawable = utils.getDrawableIcon(entry.getPackageName());
                    }
                    drawable.setBounds(0, 0, iconSize, iconSize);
                    viewHolder.alphabetIcon.
                            setCompoundDrawables(drawable, null, null, null);
                    entry.setColor(Color.TRANSPARENT);
                    setAlphabetColor(viewHolder.alphabetIcon, Color.TRANSPARENT);
                } catch (Exception e) {
                    Log.d(DebugTag.TAG, "Error get drawable");
                }
                break;
        }

        viewHolder.txtAppName.setText(entry.getAppName());
        viewHolder.txtPostTime.setText(utils.convertTime(Long.parseLong(entry.getNtTimePost())));
        viewHolder.txtNtContentTitle.setText(entry.getNtContentTitle());
        viewHolder.txtNtContentText.setText(entry.getNtContentText());
        if (entry.getIsRead() == 1) {//Read
            viewHolder.txtAppName.setTextColor(secondaryTextColor);
            viewHolder.txtPostTime.setTextColor(disableTextColor);
            viewHolder.txtNtContentTitle.setTextColor(disableTextColor);
            viewHolder.txtNtContentText.setTextColor(disableTextColor);
        } else {
            viewHolder.txtAppName.setTextColor(primaryTextColor);
            viewHolder.txtPostTime.setTextColor(timePostColor);
            viewHolder.txtNtContentTitle.setTextColor(primaryTextColor);
            viewHolder.txtNtContentText.setTextColor(secondaryTextColor);
        }
    }

    public void getColorPalette(final ViewHolder viewHolder, final NotificationEntry entry) {
        try {
            Drawable drawable = utils.getDrawableIcon(entry.getPackageName());
            if (drawable != null) {
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        entry.setColor(palette.getLightVibrantColor(Color.WHITE));
                        if (entry.getColor() == Color.WHITE) {
                            entry.setColor(palette.getVibrantColor(resources.getColor(R.color.myAccentColor)));
                        }
                        setAlphabetColor(viewHolder.alphabetIcon, entry.getColor());
                    }
                });
            }
        } catch (Exception e) {
            entry.setColor(Color.BLACK);
            Log.e(DebugTag.TAG, "Error get drawable icon");
        }
    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    @Override
    public Filter getFilter() {
        return new FilterApp(this);
    }

    public NotificationEntry getNotificationItem(int position) {
        return lstData.get(position);
    }

    public void removeItem(int position) {
        lstData.remove(position);
        allNotification.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        lstData.clear();
        allNotification.clear();
        notifyDataSetChanged();
    }

    private void setAlphabetColor(TextViewCustomFont alphabetIcon, int color) {
        GradientDrawable drawable = (GradientDrawable) alphabetIcon.getBackground();
        drawable.setColor(color);
    }

    public void setRead(int position) {
        DBHelper db = new DBHelper(context);
        db.open();
        ContentValues values = new ContentValues();
        values.put(DBHelper.IS_READ, 1);
        if (db.updateIsRead(values, lstData.get(position).getId())) {
            lstData.get(position).setIsRead(1);
            allNotification.get(position).setIsRead(1);
            notifyItemChanged(position);
        }
        db.close();
    }

    public void filterUnread() {
        Collections.sort(lstData, Utils.sortUnread);
        allNotification.clear();
        allNotification.addAll(lstData);
        notifyDataSetChanged();
    }

    private class FilterApp extends Filter {
        private NotificationSavedAdapter adapter;
        private ArrayList<NotificationEntry> lstFilter;
        private FilterResults filterResults;
        private NotificationEntry notificationEntry;
        private String filterQuery = "";
        private Matcher m, m2, m3;

        public FilterApp(NotificationSavedAdapter adapter) {
            this.adapter = adapter;
            filterResults = new FilterResults();
            lstFilter = new ArrayList<>(allNotification.size());
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            // TODO Auto-generated method stub

            if (!TextUtils.isEmpty(constraint)) {
                lstFilter.clear();
                filterQuery = constraint.toString();
                for (int i = 0; i < allNotification.size(); ++i) {
                    Pattern p = Pattern.compile(filterQuery,
                            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

                    notificationEntry = allNotification.get(i);
                    m = p.matcher(notificationEntry.getNtContentTitle());
                    m2 = p.matcher(notificationEntry.getNtContentText());
                    m3 = p.matcher(notificationEntry.getAppName());
                    if (m.find() || m2.find() || m3.find()) {
                        lstFilter.add(notificationEntry);
                    }
                }
                lstFilter.trimToSize();
                filterResults.values = lstFilter;
                filterResults.count = lstFilter.size();
            } else {
                filterResults.values = allNotification;
                filterResults.count = allNotification.size();
                return filterResults;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            if (results != null && results.count > 0) {
                ArrayList<NotificationEntry> lstResult = new ArrayList<>((Collection<? extends NotificationEntry>) results.values);
                if (!lstResult.isEmpty()) {
                    adapter.lstData.clear();
                    adapter.lstData.addAll(lstResult);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextViewCustomFont alphabetIcon;
        private TextViewCustomFont txtPostTime;
        private TextViewCustomFont txtAppName;
        private TextViewCustomFont txtNtContentTitle;
        private TextViewCustomFont txtNtContentText;

        public ViewHolder(View itemView) {
            super(itemView);
            alphabetIcon = (TextViewCustomFont) itemView.findViewById(R.id.alphabetIcon);
            txtAppName = (TextViewCustomFont) itemView.findViewById(R.id.txtAppName);
            txtPostTime = (TextViewCustomFont) itemView.findViewById(R.id.txtPostTime);
            txtNtContentTitle = (TextViewCustomFont) itemView.findViewById(R.id.txtNtContentTitle);
            txtNtContentText = (TextViewCustomFont) itemView.findViewById(R.id.txtNtContentText);
            itemView.setOnClickListener(null);
        }
    }
}
