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

/**
 * Created by tarek on 1/4/2017.
 */

public class ComicsBrowse extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Pair<ArrayList<Comic>, Integer>> {

    private static final String QUERY_URL = "https://gateway.marvel.com:443/v1/public/comics";
    private static final int COMIC_LOADER_ID = 7;
    private final int LIMIT = 15;
    ArrayList<Comic> comics;
    ComicAdapter adapter;
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
                updateLayout(view, (Comic) adapter.getItem(position));
            }
        });
        fetchTheData();
    }

    @Override
    public Loader<Pair<ArrayList<Comic>, Integer>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        String timeStamp = Calendar.getInstance().getTime().toString();
        uriBuilder.appendQueryParameter("apikey", SECRET_KEYS.PUBLIC_KEY);
        uriBuilder.appendQueryParameter("limit", LIMIT +"");
        uriBuilder.appendQueryParameter("noVariants", "true");
        uriBuilder.appendQueryParameter("format", "comic");
        uriBuilder.appendQueryParameter("ts", timeStamp);
        uriBuilder.appendQueryParameter("offset", offset + "");
        uriBuilder.appendQueryParameter("hash", QueryUtils.getMD5Hash(timeStamp));
        return new ComicLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<Pair<ArrayList<Comic>, Integer>> loader, Pair<ArrayList<Comic>, Integer> data) {
        comics = data.first;
        total = data.second;
        adapter = new ComicAdapter(ComicsBrowse.this, comics);
        listView.setAdapter(adapter);
        emptyView.setText("No Comics Found");
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
    public void onLoaderReset(Loader<Pair<ArrayList<Comic>, Integer>> loader) {
        adapter.clear();
    }


    private static class ComicLoader extends AsyncTaskLoader<Pair<ArrayList<Comic>, Integer>> {

        private String mUrl;

        ComicLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Pair<ArrayList<Comic>, Integer> loadInBackground() {
            return QueryUtils.extractComics(NetworkUtils.getData(mUrl));
        }
    }

    private void fetchTheData() {
        if (!artificialClick) {
            offset = 0;
        }

        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        getLoaderManager().destroyLoader(COMIC_LOADER_ID);
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            progressBar.setVisibility(View.VISIBLE);
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(COMIC_LOADER_ID, null, this);
            listView.setEmptyView(emptyView);
            emptyView.setVisibility(GONE);
        } else {
            progressBar.setVisibility(GONE);
            emptyView.setText("No Internet Connection");
        }
        artificialClick = false;
    }

    private void updateLayout(View tempView, Comic temp) {
        TextView synop = ((TextView) tempView.findViewById(R.id.comic_synopsis));
        LinearLayout expContainer = (LinearLayout) tempView.findViewById(R.id.expandable_views_container);
        Button goToDetails = (Button) tempView.findViewById(R.id.open_details_button);
        if (!temp.wasClicked()) {
            synop.setMaxLines(20);
            expContainer.setVisibility(View.VISIBLE);
            if (temp.getDetailsURL() == null)
                goToDetails.setVisibility(View.GONE);
            temp.gotClicked();
        } else {
            synop.setMaxLines(3);
            expContainer.setVisibility(GONE);
            temp.unClicked();
        }
    }
}
