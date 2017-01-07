package com.tareksaidee.marvel101;


import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;

/**
 * A simple {@link Fragment} subclass.
 */
public class CharSearchFragment extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Pair<ArrayList<Character>, Integer>> {

    private static final String QUERY_URL = "https://gateway.marvel.com:443/v1/public/characters";
    private static int CHARACTER_LOADER_ID = 1;
    private final int LIMIT = 15;
    ArrayList<Character> characters;
    CharacterAdapter adapter;
    ListView listView;
    EditText charSearchBox;
    Button searchButton;
    CheckBox startsWithCheck;
    TextView emptyView;
    ProgressBar progressBar;
    Button nextPageButton;
    Button previousPageButton;
    private int offset = 0;
    private int total;
    private boolean artificialClick = false;

    public CharSearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_char_search, container, false);
        listView = (ListView) rootView.findViewById(R.id.list);
        charSearchBox = (EditText) rootView.findViewById(R.id.char_search_box);
        searchButton = (Button) rootView.findViewById(R.id.start_search_button);
        emptyView = (TextView) rootView.findViewById(R.id.empty_view);
        progressBar = (ProgressBar) rootView.findViewById(R.id.progress_bar);
        startsWithCheck = (CheckBox) rootView.findViewById(R.id.starts_with_check);
        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "fonts/comicsfont.TTF");
        charSearchBox.setTypeface(face);
        RelativeLayout footerLayout = (RelativeLayout) inflater.inflate(R.layout.listview_footer, null);
        listView.addFooterView(footerLayout);
        nextPageButton = (Button) footerLayout.findViewById(R.id.next_page_button);
        previousPageButton = (Button) footerLayout.findViewById(R.id.previous_page_button);
        nextPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offset += LIMIT;
                artificialClick = true;
                searchButton.performClick();
            }
        });
        previousPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                offset -= LIMIT;
                artificialClick = true;
                searchButton.performClick();
            }
        });
        emptyView.setVisibility(GONE);
        progressBar.setVisibility(GONE);
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!artificialClick) {
                    offset = 0;
                }
                if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                    getLoaderManager().destroyLoader(CHARACTER_LOADER_ID);
                    getLoaderManager().initLoader(CHARACTER_LOADER_ID, null, CharSearchFragment.this);
                    emptyView.setText("");
                    listView.setEmptyView(emptyView);
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(GONE);
                    emptyView.setText("No Internet Connection");
                }
                //close keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(charSearchBox.getWindowToken(), 0);
                artificialClick = false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateLayout(view, (Character) adapter.getItem(position));
            }
        });
        return rootView;
    }


    @Override
    public android.support.v4.content.Loader<Pair<ArrayList<Character>, Integer>> onCreateLoader(int id, Bundle args) {
        Uri baseUri = Uri.parse(QUERY_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        String timeStamp = Calendar.getInstance().getTime().toString();
        uriBuilder.appendQueryParameter("apikey", SECRET_KEYS.PUBLIC_KEY);
        uriBuilder.appendQueryParameter("limit", LIMIT + "");
        uriBuilder.appendQueryParameter("ts", timeStamp);
        uriBuilder.appendQueryParameter("hash", QueryUtils.getMD5Hash(timeStamp));
        uriBuilder.appendQueryParameter("offset", offset + "");
        if (startsWithCheck.isChecked())
            uriBuilder.appendQueryParameter("nameStartsWith", charSearchBox.getText().toString());
        else
            uriBuilder.appendQueryParameter("name", charSearchBox.getText().toString());
        return new CharacterLoader(this.getContext(), uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Pair<ArrayList<Character>, Integer>> loader, Pair<ArrayList<Character>, Integer> data) {
        characters = data.first;
        total = data.second;
        adapter = new CharacterAdapter(getContext(), characters);
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
    public void onLoaderReset(android.support.v4.content.Loader<Pair<ArrayList<Character>, Integer>> loader) {
        adapter.clear();
    }

    private static class CharacterLoader extends android.support.v4.content.AsyncTaskLoader<Pair<ArrayList<Character>, Integer>> {

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
            if(temp.getAvailableComics()!=0)
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
