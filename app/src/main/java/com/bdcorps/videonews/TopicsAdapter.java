package com.bdcorps.videonews;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by gdesi on 2016-07-26.
 */
public class TopicsAdapter extends BaseAdapter {

    String[] topicNames;
    Context context;
    int[] topicLogos;
    String[] topicColors;

    private static LayoutInflater inflater = null;

    public TopicsAdapter(Context context, String[] topicNames, int[] topicLogos, String[] topicColors) {
        this.topicNames = topicNames;
        this.context = context;
        this.topicLogos = topicLogos;
        this.topicColors = topicColors;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount() {
        return topicNames.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        CardView topicCardView;
        TextView topicName;
        ImageView topicLogo;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.topic_item_frag, null);
        holder.topicName = (TextView) rowView.findViewById(R.id.topic_name);
        holder.topicLogo = (ImageView) rowView.findViewById(R.id.topic_logo);
        holder.topicCardView = (CardView) rowView.findViewById(R.id.topic_cardview);

        holder.topicName.setText(topicNames[position]);
        holder.topicLogo.setImageResource(topicLogos[position]);

        final int cd_back_color = context.getResources().getIdentifier(topicColors[position], "color", context.getPackageName());

        holder.topicCardView.setCardBackgroundColor(ContextCompat.getColor(context, cd_back_color));

        Animation fabScaleAnim = AnimationUtils.loadAnimation(context, R.anim.card_reveal);
        holder.topicCardView.startAnimation(fabScaleAnim);

        holder.topicCardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Toast.makeText(context, "You Clicked " + topicNames[position], Toast.LENGTH_LONG).show();
            }
        });

        return rowView;
    }

}
