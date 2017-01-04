package com.tareksaidee.marvel101;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class ComicsSearchFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<Comic>> {

    private static final String QUERY_URL = "https://gateway.marvel.com:443/v1/public/comics";
    private static int COMICS_LOADER_ID = 1;
    ArrayList<Comic> comics;
    ComicAdapter adapter;
    ListView listView;
    EditText comicSearchBox;
    Button searchButton;
    CheckBox startsWithCheck;
    TextView emptyView;
    ProgressBar progressBar;


    public ComicsSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comics_search, container, false);
        listView = (ListView) rootView.findViewById(R.id.list);
        comicSearchBox = (EditText) rootView.findViewById(R.id.char_search_box);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(GONE);
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        searchButton = (Button) rootView.findViewById(R.id.start_search_button);
        startsWithCheck = (CheckBox) rootView.findViewById(R.id.starts_with_check);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    getLoaderManager().destroyLoader(COMICS_LOADER_ID);
                    getLoaderManager().initLoader(COMICS_LOADER_ID, null, ComicsSearchFragment.this);
                    emptyView.setText("");
                    listView.setEmptyView(emptyView);
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(GONE);
                    emptyView.setText("No Internet Connection");
                }
                //close keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(comicSearchBox.getWindowToken(), 0);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateLayout(view, (Comic) adapter.getItem(position));
            }
        });
        return rootView;
    }


    @Override
    public android.support.v4.content.Loader<ArrayList<Comic>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        String timeStamp = Calendar.getInstance().getTime().toString();
        uriBuilder.appendQueryParameter("apikey", SECRET_KEYS.PUBLIC_KEY);
        uriBuilder.appendQueryParameter("limit", "50");
        uriBuilder.appendQueryParameter("noVariants", "true");
        uriBuilder.appendQueryParameter("format", "comic");
        uriBuilder.appendQueryParameter("ts", timeStamp);
        uriBuilder.appendQueryParameter("hash", QueryUtils.getMD5Hash(timeStamp));
        if (startsWithCheck.isChecked())
            uriBuilder.appendQueryParameter("titleStartsWith", comicSearchBox.getText().toString());
        else
            uriBuilder.appendQueryParameter("title", comicSearchBox.getText().toString());
        return new ComicsSearchFragment.ComicsLoader(this.getContext(), uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<ArrayList<Comic>> loader, ArrayList<Comic> data) {
        comics = data;
        adapter = new ComicAdapter(getContext(), comics);
        listView.setAdapter(adapter);
        emptyView.setText("No Comics Found");
        progressBar.setVisibility(GONE);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<ArrayList<Comic>> loader) {
        adapter.clear();
    }

    private static class ComicsLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<Comic>> {

        private String mUrl;

        ComicsLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public ArrayList<Comic> loadInBackground() {
            return QueryUtils.extractComics(NetworkUtils.getData(mUrl));
        }
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
