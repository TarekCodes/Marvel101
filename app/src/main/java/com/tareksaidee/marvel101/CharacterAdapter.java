package com.tareksaidee.marvel101;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by tarek on 12/28/2016.
 */

public class CharacterAdapter extends ArrayAdapter {

    private Context mContext;
    public CharacterAdapter(Context context, ArrayList<Character> characters) {
        super(context, 0, characters);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView==null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.character_item, parent, false);
        }
        final Character character = (Character) getItem(position);
        ImageView image = (ImageView) listItemView.findViewById(R.id.character_image);
        TextView charName = (TextView) listItemView.findViewById(R.id.character_name);
        TextView comicsNumber = (TextView) listItemView.findViewById(R.id.comics_number);
        Button goToWiki = (Button) listItemView.findViewById(R.id.open_wiki_button);
        goToWiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(character.getWikiURL()));
                mContext.startActivity(i);
            }
        });
        TextView charDescrp = (TextView) listItemView.findViewById(R.id.character_description);
        if(!((Character) getItem(position)).wasClicked()) {
            charDescrp.setMaxLines(3);
            comicsNumber.setVisibility(GONE);
            goToWiki.setVisibility(GONE);
        }
        else {
            charDescrp.setMaxLines(20);
            if(character.getAvailableComics()!=0)
                comicsNumber.setVisibility(View.VISIBLE);
            else
                comicsNumber.setVisibility(GONE);
            if(character.getWikiURL()!=null)
                goToWiki.setVisibility(View.VISIBLE);
            else
                goToWiki.setVisibility(GONE);
        }
        image.setImageBitmap(character.getImage());
        charName.setText(character.getCharName());
        charDescrp.setText(character.getDecrp());
        comicsNumber.setText("Appeared in Over " + character.getAvailableComics() + " Comics");
        return listItemView;
    }
}
