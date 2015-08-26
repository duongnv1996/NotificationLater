package loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import database.AppEntry;

/**
 * Created by vanchung on 13/07/2015.
 */
public class AppListLoader extends AsyncTaskLoader<List<AppEntry>> {

    private List<AppEntry> lstData;
    private PackageManager pm;


    public AppListLoader(Context context) {
        super(context);
        pm = context.getPackageManager();
    }

    @Override
    public List<AppEntry> loadInBackground() {
        //Get data
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> listApp = pm.queryIntentActivities(intent, 0);
        Collections.sort(listApp, new ResolveInfo.DisplayNameComparator(pm));

        List<AppEntry> appData = new ArrayList<>(listApp.size());

        int size = listApp.size();
        ResolveInfo resolveInfo;
        for (int i = 0; i < size; ++i) {
            resolveInfo = listApp.get(i);
            AppEntry appInfo = new AppEntry();
            appInfo.setPackageName(resolveInfo.activityInfo.applicationInfo.packageName);
            appInfo.setActivityLabel(resolveInfo.loadLabel(pm).toString());
            //appInfo.setAppName(resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
            appInfo.setAppIcon(resolveInfo.activityInfo.loadIcon(pm));
            appData.add(appInfo);
        }

        return appData;
    }

    @Override
    public void deliverResult(List<AppEntry> data) {//data from do in background
        if (isReset()) {
            if (data != null)
               releaseResource(data);
            return;
        }

        List<AppEntry> oldData = lstData;
        lstData = data;//Assign data to lstData

        if (isStarted()) {
            super.deliverResult(data);
        }
        //Invalidate data
        if (oldData != null && oldData != data) {
            releaseResource(oldData);
        }
    }

    @Override
    protected void onStartLoading() {
        //If data before not null
        if (lstData != null) {
            deliverResult(lstData);
        }

        if (takeContentChanged()) {
            forceLoad();
        }
    }

    //Release data when configure changed
    @Override
    protected void onReset() {
        onStopLoading();//Sure stop loader
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
    public void onCanceled(List<AppEntry> data) {
        releaseResource(data);
    }

    @Override
    protected void onForceLoad() {
        super.onForceLoad();
    }

    private void releaseResource(List<AppEntry> data) {
        //data = null;
    }
}
