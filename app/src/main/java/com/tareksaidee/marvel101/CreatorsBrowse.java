package com.tareksaidee.marvel101;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;

public class CreatorsBrowse extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Pair<ArrayList<Creator>, Integer>> {

    private static final String QUERY_URL = "https://gateway.marvel.com:443/v1/public/creators";
    private static final int CREATOR_LOADER_ID = 10;
    private final int LIMIT = 15;
    ArrayList<Creator> creators;
    CreatorAdapter adapter;
    ListView listView;
    TextView emptyView;
    ProgressBar progressBar;
    Button nextPageButton;
    Button previousPageButton;
    private int offset = 0;
    private int total;
    private boolean artificialClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browse);
        listView = (ListView) findViewById(R.id.list);
        emptyView = (TextView) findViewById(R.id.empty_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        RelativeLayout footerLayout = (RelativeLayout) getLayoutInflater().inflate(R.layout.listview_footer, null);
        listView.addFooterView(footerLayout);
        nextPageButton = (Button) footerLayout.findViewById(R.id.next_page_button);
        previousPageButton = (Button) footerLayout.findViewById(R.id.previous_page_button);
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offset += LIMIT;
                artificialClick = true;
                fetchTheData();
            }
        });
        previousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offset -= LIMIT;
                artificialClick = true;
                fetchTheData();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateLayout(view, (Creator) adapter.getItem(position));
            }
        });
        fetchTheData();
    }

    @Override
    public Loader<Pair<ArrayList<Creator>, Integer>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        String timeStamp = Calendar.getInstance().getTime().toString();
        uriBuilder.appendQueryParameter("apikey", SECRET_KEYS.PUBLIC_KEY);
        uriBuilder.appendQueryParameter("limit", LIMIT +"");
        uriBuilder.appendQueryParameter("ts", timeStamp);
        uriBuilder.appendQueryParameter("offset", offset + "");
        uriBuilder.appendQueryParameter("hash", QueryUtils.getMD5Hash(timeStamp));
        return new CreatorLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<Pair<ArrayList<Creator>, Integer>> loader, Pair<ArrayList<Creator>, Integer> data) {
        creators = data.first;
        total = data.second;
        adapter = new CreatorAdapter(CreatorsBrowse.this, creators);
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


    private static class CreatorLoader extends AsyncTaskLoader<Pair<ArrayList<Creator>, Integer>> {

        private String mUrl;

        CreatorLoader(Context context, String url) {
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

    private void fetchTheData() {
        if (!artificialClick) {
            offset = 0;
        }

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        getLoaderManager().destroyLoader(CREATOR_LOADER_ID);
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            progressBar.setVisibility(View.VISIBLE);
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(CREATOR_LOADER_ID, null, this);
            listView.setEmptyView(emptyView);
            emptyView.setVisibility(GONE);
        } else {
            progressBar.setVisibility(GONE);
            emptyView.setText("No Internet Connection");
        }
        artificialClick = false;
    }


    private void updateLayout(View tempView, Creator temp) {
        TextView events = ((TextView) tempView.findViewById(R.id.creator_events));
        Button allComics = (Button) tempView.findViewById(R.id.open_comics_button);
        TextView eventsHeader = (TextView) tempView.findViewById(R.id.creator_events_header);
        if (temp.getEvents().equals("")) {
            events.setVisibility(GONE);
            eventsHeader.setVisibility(GONE);
        } else {
            events.setVisibility(View.VISIBLE);
            eventsHeader.setVisibility(View.VISIBLE);
        }
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
