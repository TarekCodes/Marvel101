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
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import static android.view.View.GONE;

/**
 * Created by tarek on 12/29/2016.
 */

public class ComicAdapter extends ArrayAdapter<Comic> {

    Context mContext;

    public ComicAdapter(Context context, ArrayList<Comic> comics) {
        super(context, 0, comics);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.comic_item, parent, false);
        }
        final Comic comic = (Comic) getItem(position);
        ImageView cover = (ImageView) listItemView.findViewById(R.id.comic_cover);
        TextView title = (TextView) listItemView.findViewById(R.id.comic_title);
        TextView synopsis = (TextView) listItemView.findViewById(R.id.comic_synopsis);
        TextView creators = (TextView) listItemView.findViewById(R.id.comic_creators);
        TextView date = (TextView) listItemView.findViewById(R.id.comic_pub_date);
        Button goToDetails = (Button) listItemView.findViewById(R.id.open_details_button);
        TextView collections = (TextView) listItemView.findViewById(R.id.comics_collection);
        TextView partOfEvents = (TextView) listItemView.findViewById(R.id.part_of_events);
        LinearLayout expContainer = (LinearLayout) listItemView.findViewById(R.id.expandable_views_container);
        cover.setImageBitmap(comic.getCover());
        title.setText(comic.getTitle());
        synopsis.setText(comic.getSynop());
        creators.setText(comic.getCreators());
        date.setText(comic.getPubDate());
        collections.setText(comic.getCollections());
        partOfEvents.setText(comic.getPartOfEvent());
        goToDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(comic.getDetailsURL()));
                mContext.startActivity(i);
            }
        });
        if(!getItem(position).wasClicked()) {
            synopsis.setMaxLines(3);
            expContainer.setVisibility(GONE);
        }
        else {
            synopsis.setMaxLines(20);
            expContainer.setVisibility(View.VISIBLE);
            if(comic.getDetailsURL()==null)
                goToDetails.setVisibility(View.GONE);
        }
        return listItemView;
    }
}
