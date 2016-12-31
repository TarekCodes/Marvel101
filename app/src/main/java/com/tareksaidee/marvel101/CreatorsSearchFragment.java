package com.tareksaidee.marvel101;


import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.Loader;
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
public class CreatorsSearchFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<Creator>> {

    private static final String QUERY_URL = "https://gateway.marvel.com:443/v1/public/creators";
    private static int CREATORS_LOADER_ID = 4;
    ArrayList<Creator> creators;
    CreatorAdapter adapter;
    ListView listView;
    EditText creatorSearchBox;
    Button searchButton;
    CheckBox startsWithCheck;

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
        startsWithCheck = (CheckBox) rootView.findViewById(R.id.starts_with_check);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLoaderManager().destroyLoader(CREATORS_LOADER_ID);
                getLoaderManager().initLoader(CREATORS_LOADER_ID, null, CreatorsSearchFragment.this);
            }
        });
        return rootView;
    }

    @Override
    public Loader<ArrayList<Creator>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        String timeStamp = Calendar.getInstance().getTime().toString();
        uriBuilder.appendQueryParameter("apikey", SECRET_KEYS.PUBLIC_KEY);
        uriBuilder.appendQueryParameter("limit", "50");
        uriBuilder.appendQueryParameter("ts", timeStamp);
        uriBuilder.appendQueryParameter("hash", QueryUtils.getMD5Hash(timeStamp));
        if (startsWithCheck.isChecked())
            uriBuilder.appendQueryParameter("nameStartsWith", creatorSearchBox.getText().toString());
        else
            uriBuilder.appendQueryParameter("nameStartsWith", creatorSearchBox.getText().toString());
        return new CreatorsSearchFragment.CreatorsLoader(this.getContext(), uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Creator>> loader, ArrayList<Creator> data) {
        creators = data;
        adapter = new CreatorAdapter(getContext(), creators);
        listView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Creator>> loader) {
        adapter.clear();
    }

    private static class CreatorsLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<Creator>> {

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
        public ArrayList<Creator> loadInBackground() {
            return QueryUtils.extractComics(NetworkUtils.getData(mUrl));
        }
    }
}
