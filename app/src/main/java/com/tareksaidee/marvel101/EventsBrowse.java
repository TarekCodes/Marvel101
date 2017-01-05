package com.tareksaidee.marvel101;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;

public class EventsBrowse extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Pair<ArrayList<Event>, Integer>> {

    private static final String QUERY_URL = "https://gateway.marvel.com:443/v1/public/events";
    private static final int EVENT_LOADER_ID = 1;
    private final int LIMIT = 15;
    ArrayList<Event> events;
    EventAdapter adapter;
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
                updateLayout(view, adapter.getItem(position));
            }
        });
        fetchTheData();
    }

    @Override
    public Loader<Pair<ArrayList<Event>, Integer>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        String timeStamp = Calendar.getInstance().getTime().toString();
        uriBuilder.appendQueryParameter("apikey", SECRET_KEYS.PUBLIC_KEY);
        uriBuilder.appendQueryParameter("limit", LIMIT +"");
        uriBuilder.appendQueryParameter("ts", timeStamp);
        uriBuilder.appendQueryParameter("offset", offset + "");
        uriBuilder.appendQueryParameter("hash", QueryUtils.getMD5Hash(timeStamp));
        return new EventLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<Pair<ArrayList<Event>, Integer>> loader, Pair<ArrayList<Event>, Integer> data) {
        events = data.first;
        total = data.second;
        Log.e("whatever",total+"");
        adapter = new EventAdapter(EventsBrowse.this, events);
        listView.setAdapter(adapter);
        emptyView.setText("No Characters Found");
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
    public void onLoaderReset(Loader<Pair<ArrayList<Event>, Integer>> loader) {
        adapter.clear();
    }


    private static class EventLoader extends AsyncTaskLoader<Pair<ArrayList<Event>, Integer>> {

        private String mUrl;

        EventLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Pair<ArrayList<Event>, Integer> loadInBackground() {
            return QueryUtils.extractEvents(NetworkUtils.getData(mUrl));
        }
    }

    private void fetchTheData() {
        if (!artificialClick) {
            offset = 0;
        }

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        getLoaderManager().destroyLoader(EVENT_LOADER_ID);
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            progressBar.setVisibility(View.VISIBLE);
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(EVENT_LOADER_ID, null, this);
            listView.setEmptyView(emptyView);
            emptyView.setVisibility(GONE);
        } else {
            progressBar.setVisibility(GONE);
            emptyView.setText("No Internet Connection");
        }
        artificialClick = false;
    }

    private void updateLayout(View tempView, Event temp) {
        TextView descrp = ((TextView) tempView.findViewById(R.id.event_description));
        Button goToDetails = (Button) tempView.findViewById(R.id.open_details_button);
        LinearLayout expContainer = (LinearLayout) tempView.findViewById(R.id.expandable_views_container);
        if (!temp.wasClicked()) {
            descrp.setMaxLines(20);
            expContainer.setVisibility(View.VISIBLE);
            if (temp.getDetailsURL() == null)
                goToDetails.setVisibility(View.GONE);
            temp.gotClicked();
        } else {
            descrp.setMaxLines(3);
            expContainer.setVisibility(GONE);
            temp.unClicked();
        }
    }
}
