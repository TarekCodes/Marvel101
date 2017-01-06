package com.tareksaidee.marvel101;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public static final String LOG_TAG = MainActivity.class.getName();
    Button charactersButton;
    Button searchButton;
    Button comicsButton;
    Button creatorsButton;
    Button eventsButton;
    TextView accreditMarvel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/comicsfont.TTF");
        charactersButton = (Button) findViewById(R.id.characters_button);
        charactersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CharactersBrowse.class);
                startActivity(intent);
            }
        });
        searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intent);
            }
        });
        comicsButton = (Button) findViewById(R.id.comics_button);
        comicsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ComicsBrowse.class);
                startActivity(intent);
            }
        });
        creatorsButton = (Button) findViewById(R.id.creators_button);
        creatorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CreatorsBrowse.class);
                startActivity(intent);
            }
        });
        eventsButton = (Button) findViewById(R.id.events_button);
        eventsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EventsBrowse.class);
                startActivity(intent);
            }
        });
        accreditMarvel = (TextView) findViewById(R.id.accredit_marvel);
        accreditMarvel.setTypeface(face);
        charactersButton.setTypeface(face);
        comicsButton.setTypeface(face);
        searchButton.setTypeface(face);
        creatorsButton.setTypeface(face);
        eventsButton.setTypeface(face);
    }
}
