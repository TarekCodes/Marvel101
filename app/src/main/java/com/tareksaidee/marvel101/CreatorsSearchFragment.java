package com.tareksaidee.marvel101;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreatorsSearchFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Pair<ArrayList<Creator>, Integer>> {

    private static final String QUERY_URL = "https://gateway.marvel.com:443/v1/public/creators";
    private static int CREATORS_LOADER_ID = 4;
    private final int LIMIT = 15;
    ArrayList<Creator> creators;
    CreatorAdapter adapter;
    ListView listView;
    EditText creatorSearchBox;
    Button searchButton;
    CheckBox startsWithCheck;
    TextView emptyView;
    ProgressBar progressBar;
    Button nextPageButton;
    Button previousPageButton;
    private int offset = 0;
    private int total;
    private boolean artificialClick = false;

    public CreatorsSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_creators_search, container, false);
        listView = (ListView) rootView.findViewById(R.id.list);
        creatorSearchBox = (EditText) rootView.findViewById(R.id.creator_search_box);
        searchButton = (Button) rootView.findViewById(R.id.start_search_button);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        startsWithCheck = (CheckBox) rootView.findViewById(R.id.starts_with_check);
        RelativeLayout footerLayout = (RelativeLayout) inflater.inflate(R.layout.listview_footer, null);
        listView.addFooterView(footerLayout);
        nextPageButton = (Button) footerLayout.findViewById(R.id.next_page_button);
        previousPageButton = (Button) footerLayout.findViewById(R.id.previous_page_button);
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offset += LIMIT;
                artificialClick = true;
                searchButton.performClick();
            }
        });
        previousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offset -= LIMIT;
                artificialClick = true;
                searchButton.performClick();
            }
        });
        progressBar.setVisibility(GONE);
        emptyView.setVisibility(GONE);
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!artificialClick) {
                    offset = 0;
                }
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    getLoaderManager().destroyLoader(CREATORS_LOADER_ID);
                    getLoaderManager().initLoader(CREATORS_LOADER_ID, null, CreatorsSearchFragment.this);
                    emptyView.setText("");
                    listView.setEmptyView(emptyView);
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(GONE);
                    emptyView.setText("No Internet Connection");
                }
                //close keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(creatorSearchBox.getWindowToken(), 0);
                artificialClick = false;
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateLayout(view, adapter.getItem(position));
            }
        });
        return rootView;
    }

    @Override
    public Loader<Pair<ArrayList<Creator>, Integer>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        String timeStamp = Calendar.getInstance().getTime().toString();
        uriBuilder.appendQueryParameter("apikey", SECRET_KEYS.PUBLIC_KEY);
        uriBuilder.appendQueryParameter("limit", LIMIT + "");
        uriBuilder.appendQueryParameter("ts", timeStamp);
        uriBuilder.appendQueryParameter("hash", QueryUtils.getMD5Hash(timeStamp));
        uriBuilder.appendQueryParameter("offset", offset + "");
        if (startsWithCheck.isChecked())
            uriBuilder.appendQueryParameter("nameStartsWith", creatorSearchBox.getText().toString());
        else
            uriBuilder.appendQueryParameter("nameStartsWith", creatorSearchBox.getText().toString());
        return new CreatorsSearchFragment.CreatorsLoader(this.getContext(), uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<Pair<ArrayList<Creator>, Integer>> loader, Pair<ArrayList<Creator>, Integer> data) {
        creators = data.first;
        total = data.second;
        adapter = new CreatorAdapter(getContext(), creators);
        listView.setAdapter(adapter);
        emptyView.setText("No Creators Found");
        progressBar.setVisibility(GONE);
        if (offset + LIMIT >= total)
            nextPageButton.setVisibility(GONE);
        else
            nextPageButton.setVisibility(View.VISIBLE);
        if (offset == 0)
            previousPageButton.setVisibility(GONE);
        else
            previousPageButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void onLoaderReset(Loader<Pair<ArrayList<Creator>, Integer>> loader) {
        adapter.clear();
    }

    private static class CreatorsLoader extends android.support.v4.content.AsyncTaskLoader<Pair<ArrayList<Creator>, Integer>> {

        private String mUrl;

        CreatorsLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Pair<ArrayList<Creator>, Integer> loadInBackground() {
            return QueryUtils.extractCreators(NetworkUtils.getData(mUrl));
        }
    }

    private void updateLayout(View tempView, Creator temp) {
        TextView events = ((TextView) tempView.findViewById(R.id.creator_events));
        Button allComics = (Button) tempView.findViewById(R.id.open_comics_button);
        if (!temp.wasClicked()) {
            events.setMaxLines(50);
            if (temp.getAllComicsURL() != null)
                allComics.setVisibility(View.VISIBLE);
            temp.gotClicked();
        } else {
            events.setMaxLines(5);
            allComics.setVisibility(GONE);
            temp.unClicked();
        }
    }
}
