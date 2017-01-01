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
 * Created by tarek on 12/31/2016.
 */

public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.comic_item, parent, false);
        }
        Event event = (Event) getItem(position);
        ImageView image = (ImageView) listItemView.findViewById(R.id.event_image);
        TextView title = (TextView) listItemView.findViewById(R.id.event_title);
        TextView descrp = (TextView) listItemView.findViewById(R.id.event_description);
        TextView period = (TextView) listItemView.findViewById(R.id.event_period);
        TextView prevEvent = (TextView) listItemView.findViewById(R.id.previous_event);
        TextView nextEvent = (TextView) listItemView.findViewById(R.id.next_event);
        image.setImageBitmap(event.getImage());
        title.setText(event.getTitle());
        descrp.setText(event.getDescrp());
        period.setText(event.getEventPeriod());
        prevEvent.setText(event.getPrevEvent());
        nextEvent.setText(event.getNextEvent());
        return listItemView;
    }
}
