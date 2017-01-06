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
 * Created by tarek on 12/30/2016.
 */

public class CreatorAdapter extends ArrayAdapter<Creator> {

    private Context mContext;

    public CreatorAdapter(Context context, ArrayList<Creator> creators) {
        super(context, 0, creators);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.creator_item, parent, false);
        }
        final Creator creator = getItem(position);
        ImageView imageView = (ImageView) listItemView.findViewById(R.id.creator_image);
        TextView name = (TextView) listItemView.findViewById(R.id.creator_name);
        TextView eventsHeader = (TextView) listItemView.findViewById(R.id.creator_events_header);
        TextView events = (TextView) listItemView.findViewById(R.id.creator_events);
        Button goToAllComics = (Button) listItemView.findViewById(R.id.open_comics_button);
        goToAllComics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(creator.getAllComicsURL()));
                mContext.startActivity(i);
            }
        });
        imageView.setImageBitmap(creator.getImage());
        name.setText(creator.getName());
        events.setText(creator.getEvents());
        if (creator.getEvents().equals("")) {
            events.setVisibility(GONE);
            eventsHeader.setVisibility(GONE);
        } else {
            events.setVisibility(View.VISIBLE);
            eventsHeader.setVisibility(View.VISIBLE);
        }
        if (!(getItem(position)).wasClicked()) {
            events.setMaxLines(5);
            goToAllComics.setVisibility(GONE);
        } else {
            events.setMaxLines(50);
            if (creator.getAllComicsURL() != null)
                goToAllComics.setVisibility(View.VISIBLE);
        }
        return listItemView;
    }
}
