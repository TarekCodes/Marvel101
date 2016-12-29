package com.tareksaidee.marvel101;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;

public class CharactersActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Character>> {

    private static final String QUERY_URL = "https://gateway.marvel.com:443/v1/public/characters";
    private static final int CHARACTER_LOADER_ID = 1;
    ArrayList<Character> characters;
    CharacterAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_characters);
        listView = (ListView) findViewById(R.id.list);
        LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(CHARACTER_LOADER_ID, null, this);
    }

    @Override
    public Loader<ArrayList<Character>> onCreateLoader(int id, Bundle args) {
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
    public void onLoadFinished(Loader<ArrayList<Character>> loader, ArrayList<Character> data) {
        characters = data;
        adapter = new CharacterAdapter(CharactersActivity.this, characters);
        listView.setAdapter(adapter);
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<Character>> loader) {
        adapter.clear();
    }


    private static class CharacterLoader extends AsyncTaskLoader<ArrayList<Character>> {

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
        public ArrayList<Character> loadInBackground() {
            return QueryUtils.extractCharacters(NetworkUtils.getData(mUrl));
        }
    }
}
