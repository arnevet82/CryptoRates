package com.crypto.velis.cryptorates;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by natalie on 22/01/18.
 */

public class ListAdapter extends ArrayAdapter<Model> {


    private Context activityContext;
    private List<Model> list;
    public static final String TAG = "ListView";

    public ListAdapter(Context context, List<Model> list){
        super(context, R.layout.single_layout, list);
        this.activityContext = context;
        this.list = list;
    }


    @Override
    public View getView(final int position, View view, ViewGroup viewGroup){

        final ViewHolder viewHolder;


        if (view == null) {
            view = LayoutInflater.from(activityContext).inflate(R.layout.single_layout, null);
            viewHolder = new ViewHolder();
            Typeface typeface = Typeface.createFromAsset(activityContext.getAssets(), "Montserrat-Light.ttf");
            viewHolder.coinName = (TextView) view.findViewById(R.id.name);
            viewHolder.value = (TextView) view.findViewById(R.id.value);
            viewHolder.arrow = (ImageView)view.findViewById(R.id.arrow);
            viewHolder.icon = (ImageView)view.findViewById(R.id.icon);
            viewHolder.pct = (TextView) view.findViewById(R.id.pct);

            viewHolder.coinName.setTypeface(typeface);
            viewHolder.value.setTypeface(typeface);
            viewHolder.pct.setTypeface(typeface);

            viewHolder.arrow.setImageResource(list.get(position).getArrow());
            viewHolder.coinName.setText(list.get(position).getName());
            viewHolder.value.setText(list.get(position).getValue());

            viewHolder.pct.setText(list.get(position).getPct());
            String flag = list.get(position).getFlag();
            if(flag == "up"){
                viewHolder.pct.setTextColor(Color.GREEN);
            }else if(flag == "down"){
                viewHolder.pct.setTextColor(Color.RED);
            }else{
                viewHolder.pct.setTextColor(Color.TRANSPARENT);
            }

            viewHolder.icon.setImageResource(list.get(position).getIcon());
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        return view;
    }

    private static class ViewHolder {

        TextView coinName;
        TextView value;
        ImageView arrow;
        ImageView icon;
        TextView pct;

    }


}