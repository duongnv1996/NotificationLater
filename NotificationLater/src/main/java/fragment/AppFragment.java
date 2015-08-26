package fragment;


import android.app.Fragment;
import android.app.LoaderManager;
import android.content.Loader;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.kimcy929.notificationlater.R;

import java.util.List;

import adapter.ApplicationAdapter;
import database.AppEntry;
import loader.AppListLoader;

/**
 * A simple {@link Fragment} subclass.
 */
public class AppFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<List<AppEntry>> {

    private SearchView searchView;
    private ListView listViewApp;
    private ProgressBar progress;
    private ApplicationAdapter adapter;

    private static final int LOADER_ID = 8;

    public AppFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_app, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listViewApp = (ListView) view.findViewById(R.id.listViewApp);
        progress = (ProgressBar) view.findViewById(R.id.progress);
        getLoaderManager().initLoader(LOADER_ID, null, this).forceLoad();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        if (searchView != null) {
            searchView.setQueryHint(getResources().getString(R.string.search_app));
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
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<AppEntry>> onCreateLoader(int id, Bundle args) {
        return new AppListLoader(getActivity());
    }

    @Override
    public void onLoadFinished(Loader<List<AppEntry>> loader, final List<AppEntry> data) {
        if (data != null && !data.isEmpty()) {
            adapter = new ApplicationAdapter(getActivity(), R.layout.app_list_item, data);
            listViewApp.setAdapter(adapter);
            listViewApp.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    CheckBox cbChooseApp = (CheckBox) view.findViewById(R.id.cbChooseApp);
                    adapter.handCheckBox(data.get(position), !cbChooseApp.isChecked());
                }
            });
        }
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(Loader<List<AppEntry>> loader) {

    }
}
