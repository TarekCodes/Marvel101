package com.tareksaidee.marvel101;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventSearchFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<ArrayList<Event>> {

    private static final String QUERY_URL = "https://gateway.marvel.com:443/v1/public/events";
    private static int EVENTS_LOADER_ID = 5;
    ArrayList<Event> events;
    EventAdapter adapter;
    ListView listView;
    EditText eventSearchBox;
    Button searchButton;
    CheckBox startsWithCheck;
    TextView emptyView;
    ProgressBar progressBar;

    public EventSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_event_search, container, false);
        listView = (ListView) rootView.findViewById(R.id.list);
        eventSearchBox = (EditText) rootView.findViewById(R.id.event_search_box);
        searchButton = (Button) rootView.findViewById(R.id.start_search_button);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        progressBar.setVisibility(GONE);
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        startsWithCheck = (CheckBox) rootView.findViewById(R.id.starts_with_check);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    getLoaderManager().destroyLoader(EVENTS_LOADER_ID);
                    getLoaderManager().initLoader(EVENTS_LOADER_ID, null, EventSearchFragment.this);
                    emptyView.setText("");
                    listView.setEmptyView(emptyView);
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(GONE);
                    emptyView.setText("No Internet Connection");
                }
            }
        });
        return rootView;
    }

    @Override
    public Loader<ArrayList<Event>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        String timeStamp = Calendar.getInstance().getTime().toString();
        uriBuilder.appendQueryParameter("apikey", SECRET_KEYS.PUBLIC_KEY);
        uriBuilder.appendQueryParameter("limit", "50");
        uriBuilder.appendQueryParameter("ts", timeStamp);
        uriBuilder.appendQueryParameter("hash", QueryUtils.getMD5Hash(timeStamp));
        if (startsWithCheck.isChecked())
            uriBuilder.appendQueryParameter("nameStartsWith", eventSearchBox.getText().toString());
        else
            uriBuilder.appendQueryParameter("name", eventSearchBox.getText().toString());
        return new EventSearchFragment.EventsLoader(this.getContext(), uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Event>> loader, ArrayList<Event> data) {
        events = data;
        adapter = new EventAdapter(getContext(), events);
        listView.setAdapter(adapter);
        emptyView.setText("No Events Found");
        progressBar.setVisibility(GONE);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Event>> loader) {
        adapter.clear();
    }

    private static class EventsLoader extends android.support.v4.content.AsyncTaskLoader<ArrayList<Event>> {

        private String mUrl;

        EventsLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public ArrayList<Event> loadInBackground() {
            return QueryUtils.extractEvents(NetworkUtils.getData(mUrl));
        }
    }
}
