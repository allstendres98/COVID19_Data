package com.al375502.covid_19data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.al375502.covid_19data.database.Country;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {
    Context context;
    ArrayList<Country> arr;

    public MyAdapter(Context context, ArrayList<Country> arr) {
        this.context = context;
        this.arr = arr;
    }

    @Override
    public int getCount() {
        return arr.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.country_design,parent,false);

        TextView name  = (TextView) convertView.findViewById(R.id.name);
        TextView flag = (TextView) convertView.findViewById(R.id.flag);

        name.setText("" + arr.get(position).name);
        flag.setText(""   + arr.get(position).flag);

        return convertView;
    }
}
