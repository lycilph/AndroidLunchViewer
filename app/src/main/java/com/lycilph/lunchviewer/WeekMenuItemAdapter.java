package com.lycilph.lunchviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.util.List;

public class WeekMenuItemAdapter extends ArrayAdapter<WeekMenuItem> {
    private Context context;

    public WeekMenuItemAdapter(List<WeekMenuItem> weekMenuItems, Context context) {
        super(context, R.layout.week_menu_item_layout, weekMenuItems);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.week_menu_item_layout, null);
        }

        WeekMenuItem item = getItem(position);

        String day = item.getDate().dayOfWeek().getAsShortText();
        TextView dayTv = (TextView) v.findViewById(R.id.day);
        dayTv.setText(day);

        TextView textTv = (TextView) v.findViewById(R.id.text);
        textTv.setText(item.getText());

        LocalTime lunchEnd = DateTime.now().withTime(13, 15, 0, 0).toLocalTime();
        LocalTime now = DateTime.now().toLocalTime();
        LocalDate nextLunchDate = (lunchEnd.isBefore(now) ? DateTime.now().plusDays(1).toLocalDate() : DateTime.now().toLocalDate());

        if (nextLunchDate.compareTo(item.getDate()) == 0) {
            LinearLayout ll = (LinearLayout) v.findViewById(R.id.selection_bar);
            ll.setVisibility(View.VISIBLE);
        }

        return v;
    }

    public void update(List<WeekMenuItem> items) {
        clear();
        addAll(items);
        notifyDataSetChanged();
    }
}