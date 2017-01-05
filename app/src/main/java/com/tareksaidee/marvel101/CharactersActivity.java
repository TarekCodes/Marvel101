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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;

public class CharactersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Pair<ArrayList<Character>, Integer>> {

    private static final String QUERY_URL = "https://gateway.marvel.com:443/v1/public/characters";
    private static final int CHARACTER_LOADER_ID = 1;
    ArrayList<Character> characters;
    CharacterAdapter adapter;
    ListView listView;
    TextView emptyView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characters);
        listView = (ListView) findViewById(R.id.list);
        emptyView = (TextView) findViewById(R.id.empty_view);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.initLoader(CHARACTER_LOADER_ID, null, this);
            listView.setEmptyView(emptyView);
        } else {
            progressBar.setVisibility(GONE);
            emptyView.setText("No Internet Connection");
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateLayout(view, (Character) adapter.getItem(position));
            }
        });
    }

    @Override
    public Loader<Pair<ArrayList<Character>, Integer>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        String timeStamp = Calendar.getInstance().getTime().toString();
        uriBuilder.appendQueryParameter("apikey", SECRET_KEYS.PUBLIC_KEY);
        uriBuilder.appendQueryParameter("limit", "50");
        uriBuilder.appendQueryParameter("ts", timeStamp);
        uriBuilder.appendQueryParameter("hash", QueryUtils.getMD5Hash(timeStamp));
        return new CharacterLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<Pair<ArrayList<Character>, Integer>> loader, Pair<ArrayList<Character>, Integer> data) {
        characters = data.first;
        adapter = new CharacterAdapter(CharactersActivity.this, characters);
        listView.setAdapter(adapter);
        emptyView.setText("No Characters Found");
        progressBar.setVisibility(GONE);
    }

    @Override
    public void onLoaderReset(Loader<Pair<ArrayList<Character>, Integer>> loader) {
        adapter.clear();
    }


    private static class CharacterLoader extends AsyncTaskLoader<Pair<ArrayList<Character>, Integer>> {

        private String mUrl;

        CharacterLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            forceLoad();
        }

        @Override
        public Pair<ArrayList<Character>, Integer> loadInBackground() {
            return QueryUtils.extractCharacters(NetworkUtils.getData(mUrl));
        }
    }

    private void updateLayout(View tempView, Character temp) {
        TextView charDescrp = ((TextView) tempView.findViewById(R.id.character_description));
        TextView comicsNumber = (TextView) tempView.findViewById(R.id.comics_number);
        Button goToWiki = (Button) tempView.findViewById(R.id.open_wiki_button);
        if (!temp.wasClicked()) {
            charDescrp.setMaxLines(20);
            comicsNumber.setVisibility(View.VISIBLE);
            if (temp.getWikiURL() != null)
                goToWiki.setVisibility(View.VISIBLE);
            temp.gotClicked();
        } else {
            charDescrp.setMaxLines(3);
            comicsNumber.setVisibility(GONE);
            goToWiki.setVisibility(GONE);
            temp.unClicked();
        }
    }
}
