package com.tareksaidee.marvel101;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;


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


    public ComicsSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comics_search, container, false);
        listView = (ListView) rootView.findViewById(R.id.list);
        comicSearchBox = (EditText) rootView.findViewById(R.id.char_search_box);
        searchButton = (Button) rootView.findViewById(R.id.start_search_button);
        startsWithCheck = (CheckBox) rootView.findViewById(R.id.starts_with_check);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoaderManager().destroyLoader(COMICS_LOADER_ID);
                getLoaderManager().initLoader(COMICS_LOADER_ID, null, ComicsSearchFragment.this);
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

}
