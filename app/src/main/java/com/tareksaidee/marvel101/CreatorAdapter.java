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
 * Created by tarek on 12/30/2016.
 */

public class CreatorAdapter extends ArrayAdapter<Creator> {

    public CreatorAdapter(Context context, ArrayList<Creator> creators) {
        super(context, 0, creators);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.creator_item, parent, false);
        }
        Creator creator = (Creator) getItem(position);
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.creator_image);
        TextView name = (TextView) listItemView.findViewById(R.id.creator_name);
        TextView events = (TextView) listItemView.findViewById(R.id.creator_events);
        imageView.setImageBitmap(creator.getImage());
        name.setText(creator.getName());
        events.setText(creator.getEvents());
        return listItemView;
    }
}
