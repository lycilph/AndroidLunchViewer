package com.lycilph.lunchviewer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class WeekMenuAdapter extends ArrayAdapter<WeekMenu> {
    private Context context;

    public WeekMenuAdapter(List<WeekMenu> weekMenuList, Context context) {
        super(context, R.layout.week_menu_layout, weekMenuList);
        this.context = context;
    }

    @Override
    public boolean isEmpty() {
        return this.getCount() == 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        // First let's verify the convertView is not null
        if (convertView == null) {
            // This a new view we inflate the new layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.week_menu_layout, null);
        }

        WeekMenu wm = this.getItem(position);
        String text = String.format("%d - %d", wm.getYear(), wm.getWeek());

        if (position % 3 == 0) {
            LinearLayout ll = (LinearLayout) v.findViewById(R.id.selection_bar);
            ll.setVisibility(View.VISIBLE);
        }

        TextView tv = (TextView) v.findViewById(R.id.Title);
        tv.setText(text);

        return v;
    }

    public void Update(List<WeekMenu> weekMenus) {
        clear();
        addAll(weekMenus);
        notifyDataSetChanged();
    }
}
