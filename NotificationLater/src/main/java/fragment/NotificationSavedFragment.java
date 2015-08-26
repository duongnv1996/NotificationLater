package fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.Loader;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kimcy929.notificationlater.NotificationDetailActivity;
import com.kimcy929.notificationlater.R;

import java.util.List;

import adapter.NotificationSavedAdapter;
import adapter.SimpleDividerItemDecoration;
import database.Constant;
import database.DBHelper;
import database.NotificationEntry;
import loader.NotificationLoader;
import utils.Utils;


public class NotificationSavedFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<NotificationEntry>>
        ,RecyclerView.OnItemTouchListener,
        View.OnClickListener{

    private SearchView searchView;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView recyclerView;
    private NotificationSavedAdapter adapter;
    private LinearLayoutManager layoutManager;
    private FloatingActionButton fab;
    private TextView btnUnread;

    private Utils utils;

    private int curPosition;

    private GestureDetectorCompat gestureDetector;

    private int LOADER_ID = 9;

    private BroadcastReceiver updateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Constant.ACTION_UPDATE_NOTIFICATION)) {
                getLoaderManager().initLoader(LOADER_ID, null, NotificationSavedFragment.this).forceLoad();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification_saved, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        utils = new Utils(getActivity());
        swipeRefresh = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefresh);
        btnUnread = (TextView) view.findViewById(R.id.btnUnRead);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        swipeRefresh.setColorSchemeColors(Color.RED, Color.BLUE, Color.GREEN);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getActivity().getLoaderManager()
                        .initLoader(LOADER_ID, null, NotificationSavedFragment.this).forceLoad();
            }
        });

        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        recyclerView.addOnItemTouchListener(this);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        gestureDetector = new GestureDetectorCompat(getActivity(), new RecyclerViewListener());

        btnUnread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter != null&& adapter.getItemCount() > 0) {
                    adapter.filterUnread();
                }
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adapter != null && adapter.getItemCount() > 0)
                    createDeleteDialog();
            }
        });
        getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
    }

    private void createDeleteDialog() {
        Resources resources = getResources();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyAlertDialogAppCompatStyle);
        builder.setTitle(resources.getString(R.string.delete_dialog_title))
                .setMessage(resources.getString(R.string.delete_notification_message))
                .setNegativeButton(resources.getString(R.string.cancel_label), null)
                .setPositiveButton(resources.getString(R.string.ok_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AsyncTask<Void, Void, Boolean>() {
                            @Override
                            protected Boolean doInBackground(Void... params) {
                                DBHelper db = new DBHelper(getActivity());
                                db.open();
                                boolean result = false;
                                if (db.deleteAllNotification()) {
                                    result = true;
                                }
                                db.close();
                                return result;
                            }

                            @Override
                            protected void onPostExecute(Boolean aBoolean) {
                                if (aBoolean) {
                                    adapter.clear();
                                }
                                super.onPostExecute(aBoolean);
                            }
                        }.execute();

                    }
                });
        builder.show();
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);

        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        if (searchView != null) {
            searchView.setQueryHint(getResources().getString(R.string.search_nt));
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

                @Override
                public boolean onQueryTextSubmit(String arg0) {
                    // TODO Auto-generated method stub
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    if (adapter != null) {
                        adapter.getFilter().filter(newText);
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(
                updateReceiver, new IntentFilter(Constant.ACTION_UPDATE_NOTIFICATION)
        );
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(updateReceiver);
        super.onPause();
    }

    private ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            DBHelper db = new DBHelper(getActivity());
            db.open();
            if (db.deleteNotification(adapter.getNotificationItem(position).getId())) {
                adapter.removeItem(position);
            }
            db.close();
        }

    };

    @Override
    public Loader<List<NotificationEntry>> onCreateLoader(int id, Bundle args) {
        swipeRefresh.setRefreshing(true);
        return new NotificationLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<NotificationEntry>> loader, List<NotificationEntry> data) {
        if (data != null && !data.isEmpty()) {
            adapter = new NotificationSavedAdapter(getActivity(), data, R.layout.notification_list_item);
            recyclerView.setAdapter(adapter);
        }
        stopSwipeRefresh();
    }

    @Override
    public void onLoaderReset(Loader<List<NotificationEntry>> loader) {
        stopSwipeRefresh();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private void stopSwipeRefresh() {
        if (swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }

    @Override
    public void onClick(View v) {
        if (adapter != null && adapter.getItemCount() > 0) {
            int position = recyclerView.getChildLayoutPosition(v);
            adapter.setRead(position);
            curPosition = position;
            NotificationEntry entry = adapter.getNotificationItem(position);
            Intent intentDetail = new Intent(getActivity(), NotificationDetailActivity.class);
            intentDetail.putExtra(DBHelper._ID, entry.getId());
            intentDetail.putExtra(DBHelper.TIME_POST, entry.getNtTimePost());
            intentDetail.putExtra(DBHelper.CONTENT_TITLE, entry.getNtContentTitle());
            intentDetail.putExtra(DBHelper.CONTENT_TEXT, entry.getNtContentText());
            intentDetail.putExtra(DBHelper.PACKAGE_NAME, entry.getPackageName());
            intentDetail.putExtra(DBHelper.APP_NAME, entry.getAppName());
            intentDetail.putExtra(Constant.COLOR_LABEL, entry.getColor());
            startActivityForResult(intentDetail, Constant.REQUEST_DELETE);
            utils.slideUp();
        }
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    //Catch event touch RecyclerView item
    private class RecyclerViewListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
            onClick(view);
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (adapter != null && adapter.getItemCount() > 0) {
                View view = recyclerView.findChildViewUnder(e.getX(), e.getY());
                int position = recyclerView.getChildLayoutPosition(view);
                adapter.setRead(position);
                String packageName = adapter.getNotificationItem(position).getPackageName();

                if (!packageName.equals(Constant.ANDROID_PHONE_PACKAGE)) {
                    Intent intentLauncher = getActivity().getPackageManager().getLaunchIntentForPackage(packageName);
                    if (intentLauncher != null) {
                        startActivity(intentLauncher);
                        utils.slideUp();
                    }
                } else {
                    utils.showCallLogActivity();
                    utils.slideUp();
                }
            }
            return super.onDoubleTap(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == Constant.REQUEST_DELETE) {
            if (data.getExtras() != null) {
                if (data.getBooleanExtra(Constant.DELETE_RESULT, false)) {
                    if (adapter != null) {
                        adapter.removeItem(curPosition);
                    }
                }
            }
        }
    }
}
