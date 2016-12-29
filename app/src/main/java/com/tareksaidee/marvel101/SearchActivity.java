package com.tareksaidee.marvel101;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class SearchActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<Character>> {

    private static final String QUERY_URL = "https://gateway.marvel.com:443/v1/public/characters";
    private static int LOADER_ID = 2;
    ArrayList<Character> characters;
    CharacterAdapter adapter;
    ListView listView;
    Button searchButton;
    EditText searchBox;
    CheckBox startsWithCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        listView = (ListView) findViewById(R.id.character_search_list);
        searchButton = (Button) findViewById(R.id.start_char_search);
        searchBox = (EditText) findViewById(R.id.character_search_box);
        startsWithCheck = (CheckBox) findViewById(R.id.starts_with_check);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!searchBox.getText().toString().equals("")) {
                    if (adapter != null)
                        adapter.clear();
                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.initLoader(LOADER_ID, null, SearchActivity.this);
                    LOADER_ID++;
                } else
                    Toast.makeText(SearchActivity.this, "Enter a name!", Toast.LENGTH_SHORT).show();
            }
        });
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
        if (startsWithCheck.isChecked())
            uriBuilder.appendQueryParameter("nameStartsWith", searchBox.getText().toString());
        else
            uriBuilder.appendQueryParameter("name", searchBox.getText().toString());
        return new CharacterLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<Character>> loader, ArrayList<Character> data) {
        characters = data;
        adapter = new CharacterAdapter(SearchActivity.this, characters);
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
