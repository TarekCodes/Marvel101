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
 * Created by tarek on 12/29/2016.
 */

public class ComicAdapter extends ArrayAdapter<Comic> {

    public ComicAdapter(Context context, ArrayList<Comic> comics) {
        super(context, 0, comics);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.comic_item, parent, false);
        }
        Comic comic = (Comic) getItem(position);
        ImageView cover = (ImageView) listItemView.findViewById(R.id.comic_cover);
        TextView title = (TextView) listItemView.findViewById(R.id.comic_title);
        TextView synopsis = (TextView) listItemView.findViewById(R.id.comic_synopsis);
        TextView creators = (TextView) listItemView.findViewById(R.id.comic_creators);
        TextView date = (TextView) listItemView.findViewById(R.id.comic_pub_date);
        cover.setImageBitmap(comic.getCover());
        title.setText(comic.getTitle());
        synopsis.setText(comic.getSynop());
        creators.setText(comic.getCreators());
        date.setText(comic.getPubDate());
        return listItemView;
    }
}
