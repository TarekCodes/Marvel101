package com.tareksaidee.marvel101;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by tarek on 12/28/2016.
 */

public class CharacterAdapter extends ArrayAdapter {

    public CharacterAdapter(Context context, ArrayList<Character> characters) {
        super(context, 0, characters);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView==null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.character_item, parent, false);
        }
        Character character = (Character) getItem(position);
        ImageView image = (ImageView) listItemView.findViewById(R.id.character_image);
        TextView charName = (TextView) listItemView.findViewById(R.id.character_name);
        TextView charDescrp = (TextView) listItemView.findViewById(R.id.character_description);
        if(!((Character) getItem(position)).wasClicked())
            charDescrp.setMaxLines(3);
        else
            charDescrp.setMaxLines(20);
        image.setImageBitmap(character.getImage());
        charName.setText(character.getCharName());
        charDescrp.setText(character.getDecrp());
        return listItemView;
    }
}
