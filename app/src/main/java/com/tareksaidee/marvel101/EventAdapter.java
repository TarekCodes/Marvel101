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
 * Created by tarek on 12/31/2016.
 */

public class EventAdapter extends ArrayAdapter<Event> {

    private Context mContext;

    public EventAdapter(Context context, ArrayList<Event> events) {
        super(context, 0, events);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.event_item, parent, false);
        }
        final Event event = (Event) getItem(position);
        ImageView image = (ImageView) listItemView.findViewById(R.id.event_image);
        TextView title = (TextView) listItemView.findViewById(R.id.event_title);
        TextView descrp = (TextView) listItemView.findViewById(R.id.event_description);
        TextView period = (TextView) listItemView.findViewById(R.id.event_period);
        TextView prevEvent = (TextView) listItemView.findViewById(R.id.previous_event);
        TextView nextEvent = (TextView) listItemView.findViewById(R.id.next_event);
        LinearLayout expContainer = (LinearLayout) listItemView.findViewById(R.id.expandable_views_container);
        Button openDetailsButton = (Button) listItemView.findViewById(R.id.open_details_button);
        openDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(event.getDetailsURL()));
                mContext.startActivity(i);
            }
        });
        image.setImageBitmap(event.getImage());
        title.setText(event.getTitle());
        descrp.setText(event.getDescrp());
        period.setText(event.getEventPeriod());
        prevEvent.setText(event.getPrevEvent());
        nextEvent.setText(event.getNextEvent());
        if(!(getItem(position)).wasClicked()) {
            descrp.setMaxLines(3);
            expContainer.setVisibility(GONE);
        }
        else {
            descrp.setMaxLines(20);
            expContainer.setVisibility(View.VISIBLE);
            if(event.getDetailsURL()==null)
                openDetailsButton.setVisibility(View.GONE);
        }
        return listItemView;
    }
}
